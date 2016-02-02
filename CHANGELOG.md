# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

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

## [3.0.6] - 2015-01-19

### Added

-   Support `AWS::EC2::EIPAssociation` (closes [#43](https://github.com/MonsantoCo/cloudformation-template-generator/issues/43))

-   Custom type for IPAddress

-   Support `AWS::EC2::VPCPeeringConnection` in routes (closes [#47](https://github.com/MonsantoCo/cloudformation-template-generator/issues/47))

-   Support `PrivateIP` on EC2 instances (closes [#49](https://github.com/MonsantoCo/cloudformation-template-generator/issues/49))

-   Support `MapPublicIpOnLaunch` on subnets (closes [#51](https://github.com/MonsantoCo/cloudformation-template-generator/issues/51))

-   Support `AWS::EC2::CustomerGateway`

### Changed

-   Have predictable order in the output JSON (fixes [#45](https://github.com/MonsantoCo/cloudformation-template-generator/issues/45))

## [3.0.5] - 2015-01-07

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
