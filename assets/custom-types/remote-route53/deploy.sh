#!/bin/bash

role=lambda-execution-cf-remote-route53
function_name=cf-remote-route53
account_id=$(aws ec2 describe-security-groups --group-names default | jq -r .SecurityGroups[0].OwnerId)

# Hard coded to us-east-1, since that's where Route 53 lives
lambdaRegion=us-east-1

echo "Packaging function..."
rm -rf target
mkdir -p target
cd target
cp ../remote_route53.js remote_route53.js
npm install aws-sdk
zip -r remote_route53.zip remote_route53.js  node_modules/ >/dev/null
cd ..


if ! aws iam get-role --role-name $role >/dev/null 2>&1 ; then
    echo "Creating role..."
    aws iam create-role --role-name $role \
     --assume-role-policy-document file://lambda-trust-policy.json >/dev/null
    # The role seems to take a little time to settle down and work. Because...amazon.
    echo "Giving Amazon time to settle"...
    sleep 10
fi

echo "Setting role policy..."
aws iam put-role-policy --role-name $role \
 --policy-name $role \
 --policy-document file://lambda-policy.json



echo "Uploading function..."

if aws lambda get-function --region $lambdaRegion --function-name $function_name >/dev/null 2>&1 ; then
    aws lambda update-function-code --region $lambdaRegion --function-name $function_name \
        --zip-file fileb://target/remote_route53.zip > /dev/null
else
    while ! aws lambda create-function --region $lambdaRegion --function-name $function_name \
        --description "Custom CloudFormation function for managing Route 53 in another account" \
        --runtime nodejs \
        --role arn:aws:iam::${account_id}:role/${role} \
        --handler remote_route53.handler \
        --timeout 300 \
        --zip-file fileb://target/remote_route53.zip > /dev/null ; do

        echo "The 'The role defined for the function cannot be assumed by Lambda' error you may have just seen is time based.  Sleeping 1 and trying again."
        echo "If you saw a different error or this doesn't resolve itself after a few tries, hit ctrl-c"
        sleep 1
    done
fi
lambdaArn=$(aws lambda get-function --region $lambdaRegion --function-name $function_name --output text --query Configuration.FunctionArn)
lambdaRole=$(aws lambda get-function --region $lambdaRegion --function-name $function_name --output text --query Configuration.Role)




# Create SNS topic for each region.
for snsRegion in $(aws ec2 describe-regions | jq -r .Regions[].RegionName) ; do
    topicSubscriptions=$(aws sns list-subscriptions-by-topic --region $snsRegion --topic-arn arn:aws:sns:${snsRegion}:${account_id}:cf-remote-route53 2>/dev/null)

    if [[ $? != 0 ]]  ; then
        echo "Creating SNS topic in $snsRegion"
        topicArn=$(aws sns create-topic --region $snsRegion --name cf-remote-route53 --query TopicArn --output text)

        aws sns subscribe --region $snsRegion --topic-arn $topicArn --protocol lambda --notification-endpoint $lambdaArn > /dev/null
        sid=$(cat /dev/urandom | env LC_CTYPE=C tr -dc a-zA-Z0-9 | head -c 16; echo)
        aws lambda add-permission --region $lambdaRegion --function-name $lambdaArn \
            --statement-id $sid --action lambda:invokeFunction \
            --principal sns.amazonaws.com --source-arn $topicArn > /dev/null
    else
        topicIsSubscribed=$(echo "$topicSubscriptions" | jq -r 'select(.Subscriptions[].Endpoint == "'$lambdaArn'") | any')
        if [[ "$topicIsSubscribed" != "true" ]] ; then
            echo "Subscribing existing SNS topic in $snsRegion to Lambda function."
            topicArn=$(aws sns get-topic-attributes --region $snsRegion --topic-arn arn:aws:sns:${snsRegion}:${account_id}:cf-remote-route53 --output text --query Attributes.TopicArn)

            aws sns subscribe --region $snsRegion --topic-arn $topicArn --protocol lambda --notification-endpoint $lambdaArn > /dev/null
            sid=$(cat /dev/urandom | env LC_CTYPE=C tr -dc a-zA-Z0-9 | head -c 16; echo)
            aws lambda add-permission --region $lambdaRegion --function-name $lambdaArn \
                --statement-id $sid --action lambda:invokeFunction \
                --principal sns.amazonaws.com --source-arn $topicArn > /dev/null
        else
            echo "SNS topic already exists in $snsRegion"
        fi

    fi
done

echo ""
echo "To grant this function permission to manage Route 53, please re-authenticate to the Route 53 AWS account and run:"
echo "./add-zone-admin-trust.sh $lambdaRole"
echo ""
