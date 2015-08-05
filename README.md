# CloudFormation Template Generator

Simple Scala DSL to create AWS CloudFormation (CFN) templates. The library
allows for easier creation of the AWS CloudFormation JSON by writing Scala code
to describe the AWS resources. Lets say we have a handful of CFN templates we
want to maintain, and all of those templates use the same AMI. Instead of
copying that information into all the templates, lets create an AMI component
instead, and then load it into the actual templates.

**_Why not just write JSON?_**  Because, who in their right mind would want to
write all AWS resources in JSON?

## Documentation

You must add the following resolver to your `build.sbt` to use this library.

```scala
resolvers ++= Seq(Resolver.jcenterRepo)
```

See the
[Scaladoc](http://monsantoco.github.io/cloudformation-template-generator/) for
detailed documentation and examples.

### Components

Create a Template instance of resources and check out VPCWriter to help write
it to a file.

### Currently supported AWS resource types

- AWS::AutoScaling::AutoScalingGroup
- AWS::AutoScaling::LaunchConfiguration
- AWS::AutoScaling::ScalingPolicy
- AWS::CloudWatch::Alarm
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
- AWS::RDS::DBInstance::Engine
- AWS::RDS::DBInstance::StorageType
- AWS::RDS::DBInstance
- AWS::RDS::DBParameterGroup
- AWS::RDS::DBSecurityGroup
- AWS::RDS::DBSubnetGroup
- AWS::Route53::HostedZone
- AWS::Route53::RecordSet
- AWS::S3::Bucket
- AWS::S3::BucketPolicy
- AWS::SNS::Topic
- AWS::SNS::Topic
