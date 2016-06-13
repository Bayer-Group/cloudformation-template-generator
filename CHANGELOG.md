# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [3.3.5] - TBD

### Added

-   Support for AWS::CloudFormation::Stack (see [#100](hub.com/MonsantoCo/cloudformation-template-generator/pull/100))

## [3.3.4] - 2016-05-06

**Note: This breaks backwards compatibility for some people using ApiGateways.**

### Changed

-   Datatypes for ApiGateway (see [#93](https://github.com/MonsantoCo/cloudformation-template-generator/pull/93))


## [3.3.3] - 2016-04-29

### Added

-   Support for AWS::CloudFront:Distribution (see [#71](https://github.com/MonsantoCo/cloudformation-template-generator/pull/71))

-   Support for tokens in an output (see [#74](https://github.com/MonsantoCo/cloudformation-template-generator/pull/74))

-   AWS/Lambda CloudWatch Alarm Namespace (see [#87](https://github.com/MonsantoCo/cloudformation-template-generator/pull/87))

-   Support for NodeJs4.3 as a Lambda rutime (see [#88](https://github.com/MonsantoCo/cloudformation-template-generator/pull/88))

-   Support for AWS::ApiGateway (see [#89](https://github.com/MonsantoCo/cloudformation-template-generator/pull/89))

-   Support for IAMPolicyVersions (see [#90](https://github.com/MonsantoCo/cloudformation-template-generator/pull/90))

## [3.3.2] - 2016-04-08

### Added

-   Support for AWS::CloudTrail::Trail

-   Support for AWS::DataPipeline::Pipeline

-   Ability to use DBSubnet parameters for RDS

-   Ability to use Subnet parameters for EC2 (see [#81](https://github.com/MonsantoCo/cloudformation-template-generator/issues/81))


## [3.3.1] - 2016-03-30

### Changed

-   Modified Fn::Not to be a NestableAmazonFunctionCall to support using the function within an Fn::And block

## [3.3.0] - 2016-03-18

**Note: This breaks backwards compatibility for anyone using custom NAT Gateways.**

### Added

-   Added support for AWS::Elasticsearch::Domain and the related types (see [#73](https://github.com/MonsantoCo/cloudformation-template-generator/issues/73))

### Changed

-   Removed the custom NAT gateway support in favor of the official CloudFormation mechanism (see [#72](https://github.com/MonsantoCo/cloudformation-template-generator/issues/72))


## [3.2.0] - 2016-03-02

**Note: Minor breaks in backwards compatibility on `AWS::EC2::Subnet`.  If you use the `Builder` wrappers, the change is backwards compatible.**

### Added

-   Added custom type to remotely manage Route 53 entries in another account (see [#75](https://github.com/MonsantoCo/cloudformation-template-generator/pull/75)).

### Changed

-   AvailabilityZone is optional for Subnet (see [#69](https://github.com/MonsantoCo/cloudformation-template-generator/pull/69))
-   Fixed an issue in the NAT Gateway custom type that can cause an unrecoverable failure if the gateway was manually deleted.


## [3.1.2] - 2016-02-08

### Added

-   Added resources to support ElasticBeanstalk (see [#64](https://github.com/MonsantoCo/cloudformation-template-generator/pull/64))

## [3.1.1] - 2016-02-02

### Changed

-   Rolled back the Token changes for functions (see [#60](https://github.com/MonsantoCo/cloudformation-template-generator/issues/60))

## [3.1.0] - 2016-01-26

**Note: Minor breaks in backwards compatibility**

### Added

-   Added the ability to work with NAT gateways.  Requires a custom Lambda function.  See README.md.

-   Added `AWS::CloudFormation::WaitCondition` and `AWS::CloudFormation::WaitConditionHandle` to support pausing
        for resources to do things.

-   Added support for the `Fn::GetAZs` function

-   Added in VPN support

### Changed

-   Improved EIP model to better handle VPC vs Classic EIPs.  Changes are backwards compatible, but 
        existing methods are now deprecated.

-   Most functions updated to use `Token[ConditionRef]` instead of `Token[String]`

-   Changed how implicits are handle in creating `AWS::EC2::Route` to simplify the code

## [3.0.6] - 2016-01-19

### Added

-   Support `AWS::EC2::EIPAssociation` (closes [#43](https://github.com/MonsantoCo/cloudformation-template-generator/issues/43))

-   Custom type for IPAddress

-   Support `AWS::EC2::VPCPeeringConnection` in routes (closes [#47](https://github.com/MonsantoCo/cloudformation-template-generator/issues/47))

-   Support `PrivateIP` on EC2 instances (closes [#49](https://github.com/MonsantoCo/cloudformation-template-generator/issues/49))

-   Support `MapPublicIpOnLaunch` on subnets (closes [#51](https://github.com/MonsantoCo/cloudformation-template-generator/issues/51))

-   Support `AWS::EC2::CustomerGateway`

### Changed

-   Have predictable order in the output JSON (fixes [#45](https://github.com/MonsantoCo/cloudformation-template-generator/issues/45))

## [3.0.5] - 2016-01-07

### Added

-   Basic Lambda support


## [3.0.4] - 2015-11-30

### Changed

-   Minor updates to README.md, CHANGELOG.md and MAINTAINERS

## [3.0.3] - 2015-11-30

The "tylersouthwick" release!

### Added

-   CHANGELOG.md

-   SQS and DynamoDB support

-   Support for IAM Policy Conditions

-   aws triple-quoted string interpolation

## [3.0.2] - 2015-10-27

### Changed

-   Made names of auto-generated SecurityGroup and SecurityGroupIngress
    resources deterministic, i.e., removed the UUID part of the name,
    and easier to read.

## [3.0.1] - 2015-10-21

### Added

-   Add mechanism to make a SecurityGroupRoutable from a DBInstance.

## [3.0.0] - 2015-02-17

### Added

-   Provide builder for RDS DBInstances

### Deprecated

-   Create AWS::RDS::DBInstance from default case class constructor.
