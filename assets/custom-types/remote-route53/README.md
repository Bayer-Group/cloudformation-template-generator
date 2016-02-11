#Setup

In order to manage a domain across accounts, you will need to set up the Lambda function and the appropriate roles.  These changes are only needed when you add a new domain, or when you add a new account and/or region where you want to create CloudFormation stacks that use the domain.

The Lambda function needs to be uploaded once for every account that will use this function.  When you upload it, it will also create SNS topics in every region that can be used for calls from CloudFormation stacks.

There is no harm in using this function in templates that are being run from the same account that owns the domain.  While you don't need the cross-account functionality in this case and could use the native Route 53 types, if you want to author a template that will be used in multiple accounts, it may be more straightforward to use this function universally rather than try to create conditionals or multiple template versions.

## Pre-reqs:

1. If you haven't already, install the AWS CLI.  You'll either want to setup profiles for both accounts (http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html#cli-multiple-profiles), or just have both credentials ready and reconfigure.  
2. When we refer to authenticating to an account below, you do this either by setting the `AWS_DEFAULT_PROFILE=<profile>` environment variable if you are using profiles, or by running `aws configure` and enter in the credentials for that account.  If you are using profiles, and you want to execute against a different region than is in the profile, you can override it with the environment variable `AWS_DEFAULT_REGION=<region>`.
    - References to the "Route 53 account" below refer to the AWS account that owns the hosted zone for the domain.
    - References to the "CF account" below refer to another AWS account where you want to be able to run templates that create route 53 entries in the "Route 53 account."
3. You also need `npm` and `jq` installed.


## Per domain setup:

These steps need to be performed for each domain that you want to manage from other accounts.  This only needs to be done once in the account that owns the domain, not in each AWS account.

1. You will need access to change IAM policies and roles in the Route53 account that is managing the domain ("Hosted Zone" in AWS terminology).  
2. Authenticate to the **Route 53 account.**
3. Look up the hosted zone ID for the domain. The zone ID is an alphanumeric code, not the actual domain name. 
4. From the `assets/custom-types/remote-route53` directory, run `./create-zone-admin.sh <zone-id-to-manage>`.  If you've already setup another domain in this account, it will add this domain to the role.
5. The ARN of the role will be used as the "DestinationRole" parameter to the Cloud Formation resource, regardless of what account you are using to run the template.
6. If you are adding an additional domain but have already setup all the accounts and regions per the instructions below, you do not need to repeat these steps.  They will pick up the new domain on the existing role.


## Per AWS account setup:

These steps need to be performed for each account where you want to run CloudFormation templates that use this function (the "CF Account"). 

1. You will need access to change IAM policies and roles in both the Route53 account and the "CF Account".  You will also need permissions to create Lambda functions and SNS topics in the CF account.
2. Authenticate to the **CF account.**  Regardless of the profile's region, `us-east-1` will be used, since that is where the AWS Route 53 endpoints live.
3. Run `./deploy.sh` to create the Lambda function and the appropriate execution role, along with an SNS topic for the function in all regions.  Note that SNS topics have no charge for just existing, so there is no cost for topics in regions you aren't using here.
4. Look at the output from the script above and note the command it asks you to run (`./add-zone-admin-trust <role-name>`).
5. Authenticate to the **Route 53 account.**
6. Run the command from step 4.  This will grant the function permissions to assume the Route 53 role created earlier.

## Now what?

Now that you have setup your roles, Lambda functions, and SNS topics, you will need two pieces of information to use this in your templates.

1. When you created the zone admin role, it output the ARN of the role.  This value needs to be specified as the `DestinationRole` parameter to the Route 53 type in your template.  It will take the form of `arn:aws:iam::<route-53-account-number>:role/remote-route53-cf-admin`.  The value will be the same regardless of what account you are running the Cloud Formation template from. Therefore, this value can be hard coded in your template or be set as a default value if you want to parameterize it.  
2. The SNS topic ARN for the account and region where the Cloud Formation template is running must be specified as the `ServiceToken` parameter to the Route 53 type in your template.  It will take the form of `arn:aws:sns:<cf-region>:<cf-account-num>:cf-remote-route53`.  Because the account and region should always be the values for where you are running the template, you can assemble this dynamically, using CF psuedo-parameters.  This would look like `` `Fn::Join`(":", Seq("arn:aws:sns", `AWS::Region`, `AWS::AccountId`, "cf-remote-route53"))``.  This can then be hard coded into your template and will fill in with the proper region and account ID automatically.

## Cleaning up

1. Authenticate to the **CF account** that you want to clean up.  Run `./delete-lambda.sh` to remove the Lambda function in any region you have uploaded it, all SNS topics created for this function, and the Lambda execution role in this account.  
2. Authenticate to the **Route 53 account** that you want to clean up.  Run `./delete-zone-admin.sh` to remove the zone admin role in this account and its associated policies.
