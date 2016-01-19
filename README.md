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
  "com.monsanto.arch" %% "cloud-formation-template-generator" % "2.0.0"
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

- AWS::AutoScaling::AutoScalingGroup
- AWS::AutoScaling::LaunchConfiguration
- AWS::AutoScaling::ScalingPolicy
- AWS::CloudWatch::Alarm
- AWS::DynamoDB::Table
- AWS::EC2::CustomerGateway
- AWS::EC2::EIP
- AWS::EC2::Instance
- AWS::EC2::InternetGateway
- AWS::EC2::KeyPair::KeyName
- AWS::EC2::Route
- AWS::EC2::RouteTable
- AWS::EC2::SecurityGroup
- AWS::EC2::SecurityGroupEgress
- AWS::EC2::SecurityGroupIngress
- AWS::EC2::Subnet
- AWS::EC2::SubnetRouteTableAssociation
- AWS::EC2::VPC
- AWS::EC2::VPCGatewayAttachment
- AWS::EC2::Volume
- AWS::EC2::VolumeAttachment
- AWS::ElasticLoadBalancing::LoadBalancer
- AWS::IAM::AccessKey
- AWS::IAM::Group
- AWS::IAM::InstanceProfile
- AWS::IAM::ManagedPolicy
- AWS::IAM::Policy
- AWS::IAM::Role
- AWS::IAM::User
- AWS::Lambda::EventSourceMapping
- AWS::Lambda::Function
- AWS::Lambda::Permission
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
- AWS::SNS::Topic
- AWS::SNS::TopicPolicy
- AWS::SQS::Queue
- AWS::SQS::QueuePolicy

## Releasing

This project uses the sbt release plugin.  After the changes you want to
release are committed on the master branch, you simple need to run two
commands to publish the library and its documentation.

    sbt release
    sbt ghpages-push-site
