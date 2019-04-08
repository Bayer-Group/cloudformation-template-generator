# CloudFormation Template Generator

[![Build Status](https://travis-ci.org/MonsantoCo/cloudformation-template-generator.svg?branch=master)](https://travis-ci.org/MonsantoCo/cloudformation-template-generator) [![Coverage Status](https://coveralls.io/repos/MonsantoCo/cloudformation-template-generator/badge.svg?branch=master&service=github)](https://coveralls.io/github/MonsantoCo/cloudformation-template-generator?branch=master)

Scala DSL to create AWS CloudFormation (CFN) templates. The library
allows for easier creation of the AWS CloudFormation JSON by writing Scala code
to describe the AWS resources. Lets say we have a handful of CFN templates we
want to maintain, and all of those templates use the same AMI. Instead of
copying that information into all the templates, lets create an AMI component
instead, and then load it into the actual templates.

**_Why not just write JSON?_**  Because, who in their right mind would want to
write all AWS resources in JSON?

## Documentation

See the intro [blog
post](http://engineering.monsanto.com/2015/07/10/cloudformation-template-generator/).

To use this library, you must add the following resolver

```scala
resolvers ++= Seq(Resolver.jcenterRepo)
```

and the dependency

```scala
libraryDependencies ++= Seq (
  "com.monsanto.arch" %% "cloud-formation-template-generator" % "3.8.0"
).map(_.force())
```

to your `build.sbt`.

See the
[Scaladoc](http://monsantoco.github.io/cloudformation-template-generator/) for
detailed documentation and examples.

See the [Change Log](CHANGELOG.md) for information on new, changed,
and deprecated featured.

**Note**: we are no longer using the git-flow develop/master branch paradigm.
Please just branch off master and submit your PRs against it.

### Components

Create a Template instance of resources and check out VPCWriter to help write
it to a file.

To use the fancier parts of the routing DSL, be sure to import
`TransportProtocol._`.

### Misc Features

NEW since the blog post, say you have a topology with subnets across multiple
AZ's and you want to specify an autoscaling group that spans them, using our
fancy Builders methods. Well this is cross-cutting, so its not strictly nested,
so you can use an evil evil var, or now you can use our
`Template.lookupResource[R <: Resource[R]](name: String)` method on previous
template parts to extract resources by name. Note this will produce a
generation-time error if you lookup something that does not exist or has the
wrong type (unfortunately not a generation-time compiler error as most of our
other features):

```scala
describe("Template Lookup") {
  it("Should lookup resources with the correct type") {

    val expected = `AWS::EC2::VPC`(
      name = "TestVPC",
      CidrBlock = CidrBlock(0,0,0,0,0),
      Tags = Seq.empty[AmazonTag]
    )
    val template = Template.fromResource(expected)
  
    assert(expected === template.lookupResource[`AWS::EC2::VPC`]("TestVPC"))
}
```

### Currently supported AWS resource types

- AWS::ApiGateway::Account
- AWS::ApiGateway::ApiKey
- AWS::ApiGateway::Authorizer
- AWS::ApiGateway::BasePathMapping
- AWS::ApiGateway::ClientCertificate
- AWS::ApiGateway::Deployment
- AWS::ApiGateway::Method
- AWS::ApiGateway::Model
- AWS::ApiGateway::Resource
- AWS::ApiGateway::RestApi
- AWS::ApiGateway::Stage
- AWS::ApiGateway::UsagePlan
- AWS::ApiGateway::UsagePlanKey
- AWS::AutoScaling::AutoScalingGroup
- AWS::AutoScaling::LaunchConfiguration
- AWS::AutoScaling::ScalingPolicy
- AWS::Batch::ComputeEnvironment
- AWS::Batch::JobDefinition
- AWS::Batch::JobQueue
- AWS::CloudFormation::Stack
- AWS::CloudFormation::WaitCondition
- AWS::CloudFormation::WaitConditionHandle
- AWS::CloudTrail::Trail
- AWS::CloudFront:Distribution:
- AWS::CloudWatch::Alarm
- AWS::CodeBuild::Project
- AWS::CodeCommit::Repository
- AWS::CodePipeline::CustomActionType
- AWS::CodePipeline::Pipeline
- AWS::DynamoDB::Table
- AWS::EC2::CustomerGateway
- AWS::EC2::EIP
- AWS::EC2::EIPAssociation
- AWS::EC2::Instance
- AWS::EC2::InternetGateway
- AWS::EC2::KeyPair::KeyName
- AWS::EC2::NatGateway
- AWS::EC2::NetworkAcl
- AWS::EC2::NetworkAclEntry
- AWS::EC2::Route
- AWS::EC2::RouteTable
- AWS::EC2::SecurityGroup
- AWS::EC2::SecurityGroupEgress
- AWS::EC2::SecurityGroupIngress
- AWS::EC2::Subnet
- AWS::EC2::SubnetNetworkAclAssociation
- AWS::EC2::SubnetRouteTableAssociation
- AWS::EC2::VPC
- AWS::EC2::VPCGatewayAttachment
- AWS::EC2::VPCPeeringConnection
- AWS::EC2::VPNConnection
- AWS::EC2::VPNConnectionRoute
- AWS::EC2::VPNGateway
- AWS::EC2::Volume
- AWS::EC2::VolumeAttachment
- AWS::ECR::Repository
- AWS::EFS::FileSystem
- AWS::EFS::MountTarget
- AWS::ElastiCache::CacheCluser
- AWS::ElastiCache::SubnetGroup
- AWS::Elasticsearch::Domain
- AWS::ElasticLoadBalancing::LoadBalancer
- AWS::ElasticLoadBalancingV2::Listener
- AWS::ElasticLoadBalancingV2::ListenerRule
- AWS::ElasticLoadBalancingV2::LoadBalancer
- AWS::ElasticLoadBalancingV2::TargetGroup
- AWS::ElasticBeanstalk::Application
- AWS::ElasticBeanstalk::ApplicationVersion
- AWS::ElasticBeanstalk::ConfigurationTemplate
- AWS::ElasticBeanstalk::Environment
- AWS::EMR::Cluster
- AWS::EMR::Step
- AWS::Events::Rule
- AWS::IAM::AccessKey
- AWS::IAM::Group
- AWS::IAM::InstanceProfile
- AWS::IAM::ManagedPolicy
- AWS::IAM::Policy
- AWS::IAM::Role
- AWS::IAM::User
- AWS::Kinesis::Stream
- AWS::KinesisFirehose::DeliveryStream
- AWS::KMS::Alias
- AWS::KMS::Key
- AWS::Lambda::Alias
- AWS::Lambda::EventSourceMapping
- AWS::Lambda::Function
- AWS::Lambda::Permission
- AWS::Lambda::Version
- AWS::Logs::Destination
- AWS::Logs::LogGroup
- AWS::Logs::LogStream
- AWS::Logs::MetricFilter
- AWS::Logs::SubscriptionFilter
- AWS::DataPipeline::Pipeline
- AWS::RDS::DBInstance::Engine
- AWS::RDS::DBInstance::StorageType
- AWS::RDS::DBInstance
- AWS::RDS::DBParameterGroup
- AWS::RDS::DBSecurityGroup
- AWS::RDS::DBSubnetGroup
- AWS::Redshift::Cluster
- AWS::Redshift::ClusterSecurityGroup
- AWS::Redshift::ClusterSecurityGroupIngress
- AWS::Redshift::ClusterParameterGroup (along with helper RedshiftClusterParameter type)
- AWS::Redshift::ClusterSubnetGroup
- AWS::Route53::HostedZone
- AWS::Route53::RecordSet
- AWS::S3::Bucket
- AWS::S3::BucketPolicy
- AWS::SNS::Subscription
- AWS::SNS::Topic
- AWS::SNS::TopicPolicy
- AWS::SQS::Queue
- AWS::SQS::QueuePolicy
- AWS::SSM::Association
- AWS::SSM::Document
- AWS::SSM::Parameter

### Custom types

This project packages certain useful custom CloudFormation types.  These are Lambda backed types that perform
tasks that CloudFormation does not natively support.  In order to use them, you must upload the Lambda function
to your account and region.  The code for these functions is found in this repo under assets/custom-types.

#### Remote Route 53 entries
A given domain (or hosted zone, more specifically) must be managed out of a single AWS account.  This poses problems if you want to create resources under that domain in templates that will run out of other accounts.  A CloudFormation template can only work in one given account.  However, with Cloud Formation's custom type functionality, we use custom code to assume a role in the account that owns the hosted zone.  This requires some setup steps for each hosted zone and each account.  For instructions, please see: https://github.com/MonsantoCo/cloudformation-template-generator/blob/master/assets/custom-types/remote-route53/README.md for more. 

## Working with Cloudformation Concatenating
In the CloudFormation DSL, there is support for concatenating strings, parameters, and function calls together to build strings.
This can get really ugly as they are chained together.
There is a [string interpolator](http://monsantoco.github.io/cloudformation-template-generator/latest/api/#com.monsanto.arch.cloudformation.model.package$$AwsStringInterpolator) to make this easier.

**Update 11/17/2016:** While we are not deprecating this functionality at this time, CFTG now supports `Fn::Sub`, a native way to do something very similar.  It can replace both `Fn::Join` and many uses of `Fn::GetAtt`.  Read more [here](http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-sub.html).

## Releasing

Make sure the changes for the release are included in CHANGELOG.md.

This project uses the sbt release plugin.  After the changes you want to
release are committed on the master branch, you simple need to run two
commands to publish the library and its documentation.

    sbt release
    sbt ghpagesPushSite

After publishing, create a new release under Github [releases](https://github.com/MonsantoCo/cloudformation-template-generator/releases), copying the portion of the change log for this release from CHANGELOG.md.
