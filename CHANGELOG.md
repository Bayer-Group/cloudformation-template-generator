# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).


## [3.0.7] - 2016-01-21

### Added

-   Add ability to work with NAT gateways.  Requires a custom Lambda function.  See README.md.
-   Added `AWS::CloudFormation::WaitCondition` and `AWS::CloudFormation::WaitConditionHandle` to support pausing
        for resources to do things.

### Changed

-   Improved EIP model to better handle VPC vs Classic EIPs.  Changes are backwards compatible, but 
        existing methods are now deprecated.

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
