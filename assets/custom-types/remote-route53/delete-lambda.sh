#!/usr/bin/env bash

role=lambda-execution-cf-remote-route53
function_name=cf-remote-route53
account_id=$(aws ec2 describe-security-groups --group-names default | jq -r .SecurityGroups[0].OwnerId)

for region in $(aws ec2 describe-regions | jq -r .Regions[].RegionName) ; do
    echo "Checking region $region"
    if aws lambda get-function --region $region --function-name $function_name >/dev/null 2>&1  ; then
       echo "  Deleting function in region $region"
       aws lambda delete-function --region $region --function-name $function_name >/dev/null
    fi

    if aws sns get-topic-attributes --region $region --topic-arn arn:aws:sns:${region}:${account_id}:cf-remote-route53 >/dev/null 2>&1 ; then
        echo "  Deleting SNS topic in region $region"
        aws sns delete-topic --region $region --topic-arn arn:aws:sns:${region}:${account_id}:cf-remote-route53 >/dev/null
    fi
done

if  aws iam get-role --role-name $role >/dev/null 2>&1 ; then
    for policy in $(aws iam list-role-policies --role-name $role --query PolicyNames --output text) ; do
        echo "Deleting policy $policy on role $role"
        aws iam delete-role-policy --role-name $role --policy-name $policy
    done
    echo "Deleting role $role"
    aws iam delete-role --role-name $role >/dev/null
fi
