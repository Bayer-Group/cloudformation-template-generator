#!/bin/bash
rm -rf target
mkdir -p target
cd target
cp ../nat_gateway.js nat_gateway.js
npm install aws-sdk
zip -r nat_gateway.zip nat_gateway.js  node_modules/
cd ..

account_id=$(aws iam get-user | jq -r .User.Arn |  perl -pe 's/arn:aws:iam::(\d+):.*/$1/')

role=lambda-execution-cf-nat-gateway
function_name=cf-nat-gateway

if ! aws iam get-role --role-name $role >/dev/null 2>&1 ; then
    aws iam create-role --role-name $role \
     --assume-role-policy-document file://trust-policy.json
fi

aws iam put-role-policy --role-name $role \
 --policy-name $role \
 --policy-document file://policy.json

# The role seems to take a little time to settle down and work. Because...amazon.

sleep 10

if aws lambda get-function --function-name $function_name >/dev/null 2>&1 ; then
    aws lambda update-function-code --function-name $function_name \
        --zip-file fileb://target/nat_gateway.zip
else
    while ! aws lambda create-function --function-name $function_name \
        --description "Custom CloudFormation function for managing NAT Gateways" \
        --runtime nodejs \
        --role arn:aws:iam::${account_id}:role/${role} \
        --handler nat_gateway.handler \
        --timeout 300 \
        --zip-file fileb://target/nat_gateway.zip ; do

        echo "The 'The role defined for the function cannot be assumed by Lambda' error you may have just seen is time based.  Sleeping 1 and trying again."
        echo "If you saw a different error or this doesn't resolve itself after a few tries, hit ctrl-c"
        sleep 1
    done
fi


