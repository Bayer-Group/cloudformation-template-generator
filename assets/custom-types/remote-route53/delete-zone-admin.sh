#!/usr/bin/env bash

role=remote-route53-cf-admin

if aws iam get-role --role-name $role >/dev/null 2>&1 ; then
    for policy in $(aws iam list-role-policies --role-name $role --query PolicyNames --output text) ; do
        echo "Deleting policy $policy on role $role"
        aws iam delete-role-policy --role-name $role --policy-name $policy
    done
    echo "Deleting role $role"
    aws iam delete-role --role-name $role >/dev/null
fi
