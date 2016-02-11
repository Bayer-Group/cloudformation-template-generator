#!/usr/bin/env bash
lambdaRoleARN=$1

route53RoleName=remote-route53-cf-admin

currentPolicy=$(aws iam get-role --role-name $route53RoleName | jq .AssumeRolePolicyDocument)

aws iam get-role --role-name $route53RoleName | \
    jq ".Role.AssumeRolePolicyDocument | if (.Statement[0].Principal.AWS | type) == \"string\" then .Statement[0].Principal.AWS = [.Statement[0].Principal.AWS ] + [\"$lambdaRoleARN\"] else .Statement[0].Principal.AWS |= . + [\"$lambdaRoleARN\"] end"  > target/tmp-zone-admin-trust-policy.json

aws iam update-assume-role-policy --role-name $route53RoleName --policy-document file://target/tmp-zone-admin-trust-policy.json

rm target/tmp-zone-admin-trust-policy.json
