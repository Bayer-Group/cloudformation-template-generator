#!/usr/bin/env bash
hostedZoneId=$1

roleName=remote-route53-cf-admin

if ! aws iam get-role --role-name $roleName >/dev/null 2>&1 ; then
    echo "Creating $roleName"

    aws iam create-role --role-name $roleName \
     --assume-role-policy-document file://zone-admin-trust-policy-stub.json >/dev/null

    aws iam put-role-policy --role-name $roleName \
     --policy-name list-zones \
     --policy-document file://zone-admin-list-policy.json >/dev/null
else
    echo "$roleName exists, adding zone to it"
fi

cat zone-admin-policy.json | jq ".Statement[0].Resource = \"arn:aws:route53:::hostedzone/$hostedZoneId\"" > target/tmp-zone-admin-policy.json

aws iam put-role-policy --role-name $roleName \
 --policy-name zone-${hostedZoneId}-admin \
 --policy-document file://target/tmp-zone-admin-policy.json  >/dev/null

roleArn=$(aws iam get-role --role-name $roleName --query Role.Arn)

echo ""
echo "Role ARN: $roleArn"
echo "Use this ARN in the as the 'DestinationRole' parameter to the Cloud Formation resource, regardless of what account you are running the template from."
echo ""

rm target/tmp-zone-admin-policy.json
