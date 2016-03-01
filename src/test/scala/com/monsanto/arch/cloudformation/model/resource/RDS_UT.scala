package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{Matchers, FunSpec}
import spray.json._

class RDS_UT extends FunSpec with Matchers {
  describe("AWS::RDS::DBInstance") {
    val vpc = `AWS::EC2::VPC`(
      name      = "VPC",
      CidrBlock = CidrBlock(10, 10, 10, 10, 16),
      Tags      = Seq()
    )
    val dbSubnet = `AWS::EC2::Subnet`(
      name             = "Subnet",
      VpcId            = ResourceRef(vpc),
      AvailabilityZone = Some("us-east-1a"),
      CidrBlock        = CidrBlock(10, 10, 10, 10, 24),
      Tags             = Seq()
    )
    val subnetGroupName = "DBSubnetGroup"
    val dbSubnetGroup = `AWS::RDS::DBSubnetGroup`(
      name                     = subnetGroupName,
      DBSubnetGroupDescription = "Not a real group",
      SubnetIds                = Seq(ResourceRef(dbSubnet))
    )
    val rdsInVpc = RdsVpc(dbSubnetGroupName = ResourceRef(dbSubnetGroup))
    val username = "route"
    val pw = "secret"
    val newEncryptedRds = NewRds(
      rdsAvailabilityZone = RdsMultiAZ(),
      rdsEncryption       = RdsEncryptionStorage(),
      engine              = `AWS::RDS::DBInstance::Engine`.postgres,
      masterUsername      = username,
      masterUserPassword  = pw
    )
    val instanceClass = "db.t2.large"
    val storage = 5
    val rdsInstance = RdsBuilder.buildRds(
      name             = "TestRds",
      rdsSource        = newEncryptedRds,
      rdsLocation      = rdsInVpc,
      allocatedStorage = Left(storage),
      dbInstanceClass  = instanceClass,
      rdsStorageType   = RdsStorageTypeStandard()
    )

    it("should create a valid new RDS database instance") {
      val expected = JsObject(
        "TestRds" -> JsObject(
          "Type" -> JsString("AWS::RDS::DBInstance"),
          "Properties" -> JsObject(
            "AllocatedStorage" -> JsNumber(storage),
            "DBInstanceClass" -> JsString(instanceClass),
            "Engine" -> JsString("postgres"),
            "MasterUsername" -> JsString(username),
            "MasterUserPassword" -> JsString(pw),
            "MultiAZ" -> JsBoolean(true),
            "StorageEncrypted" -> JsBoolean(true),
            "StorageType" -> JsString("standard"),
            "DBSubnetGroupName" -> JsObject("Ref" -> JsString(subnetGroupName))
          )
        )
      )
      Seq[Resource[_]](rdsInstance).toJson should be (expected)
    }

    it("should create a valid pIOPS RDS database instance from snapshot") {
      val secGroupName = "SecurityGroup"
      val secGroup = `AWS::RDS::DBSecurityGroup`(
        name                   = secGroupName,
        DBSecurityGroupIngress = Seq(),
        GroupDescription       = "Not a real group"
      )
      val dbSnapshotId = "NotARealSnapshot"
      val az = "us-east-1d"
      val rdsFromSnapshot = FromSnapshot(
        rdsAvailabilityZone  = RdsSingleAZ(az = Some(az)),
        dbSnapshotIdentifier = dbSnapshotId
      )
      val iops = 2000
      val iopsStorage = 500
      val rdsInstance = RdsBuilder.buildRds(
        name             = "TestRdsFromSnapshot",
        rdsSource        = rdsFromSnapshot,
        rdsLocation      = RdsClassic(dbSecurityGroups = Some(Seq(ResourceRef(secGroup)))),
        allocatedStorage = Left(iopsStorage),
        dbInstanceClass  = instanceClass,
        rdsStorageType   = RdsStorageTypeIo1(iops = Left(iops))
      )
      val expected = JsObject(
        "TestRdsFromSnapshot" -> JsObject(
          "Type" -> JsString("AWS::RDS::DBInstance"),
          "Properties" -> JsObject(
            "AllocatedStorage" -> JsNumber(iopsStorage),
            "AvailabilityZone" -> JsString(az),
            "DBInstanceClass" -> JsString(instanceClass),
            "DBSecurityGroups" -> JsArray(JsObject("Ref" -> JsString(secGroupName))),
            "DBSnapshotIdentifier" -> JsString(dbSnapshotId),
            "Iops" -> JsNumber(iops),
            "StorageType" -> JsString("io1")
          )
        )
      )
      Seq[Resource[_]](rdsInstance).toJson should be (expected)
    }

    it("should create a read replica") {
      val replica = RdsBuilder.buildRds(
        name             = "Replicant",
        rdsSource        = ReadReplica(sourceDBInstanceIdentifier = ResourceRef(rdsInstance)),
        rdsLocation      = rdsInVpc,
        allocatedStorage = Left(storage),
        dbInstanceClass  = instanceClass,
        rdsStorageType   = RdsStorageTypeStandard()
      )
      val expected = JsObject(
        "Replicant" -> JsObject(
          "Type" -> JsString("AWS::RDS::DBInstance"),
          "Properties" -> JsObject(
            "AllocatedStorage" -> JsNumber(storage),
            "DBInstanceClass" -> JsString(instanceClass),
            "StorageType" -> JsString("standard"),
            "DBSubnetGroupName" -> JsObject("Ref" -> JsString(subnetGroupName)),
            "SourceDBInstanceIdentifier" -> JsObject("Ref" -> JsString(rdsInstance.name))
          )
        )
      )
      Seq[Resource[_]](replica).toJson should be (expected)
    }

    it("should not create an encrypted RDS instance outside a VPC") {
      val message = "You cannot use storage encryption in non-VPC RDS instances"
      try {
        RdsBuilder.buildRds(
          name             = "TestRds",
          rdsSource        = newEncryptedRds,
          rdsLocation      = RdsClassic(),
          allocatedStorage = Left(10),
          dbInstanceClass  = instanceClass
        )
      } catch {
        case e: IllegalArgumentException if e.getMessage == message =>
      }
    }

    it("should not create an RDS with incompatible iops and storage parameters") {
      try {
        RdsBuilder.buildRds(
          name = "TheSource",
          rdsSource = newEncryptedRds,
          rdsLocation = rdsInVpc,
          allocatedStorage = Left(5),
          dbInstanceClass = instanceClass,
          rdsStorageType = RdsStorageTypeIo1(iops = Left(2000))
        )
      } catch {
        case e: IllegalArgumentException if e.getMessage.startsWith("invalid Iops value") =>
      }
    }

    it("should create an RDS instance using parameters") {
      val rdsUser = StringParameter(
        name        = "RDSUser",
        Description = Some("Sets the user for the infrastructure RDS instance")
      )
      val rdsPassword = StringParameter(
        name                  = "RDSPassword",
        Description           = Some("Sets the password for the infrastructure RDS instance"),
        MinLength             = Some(StringBackedInt(8)),
        MaxLength             = Some(StringBackedInt(128)),
        AllowedPattern        = Some("^[^\"@/]+$"),
        ConstraintDescription = Some("Must be between 8 and 128 characters. \", @, and / are not allowed"),
        NoEcho                = Some(true)
      )
      val rdsAllocatedStorage = NumberParameter(
        name                  = "RDSAllocatedStorage",
        Description           = Some("The size in GB of the storage for the infrastructure RDS instance"),
        MinValue              = Some(StringBackedInt(5)),
        ConstraintDescription = Some("Must be an integer greater than or equal to 5"),
        Default               = Some(StringBackedInt(5))
      )
      val rdsInstanceClass = StringParameter(
        name                  = "RDSInstanceClass",
        Description           = Some("The instance class for the infrastructure RDS Instance"),
        AllowedValues         = Some(Seq("db.t2.micro", "db.t2.small")),
        ConstraintDescription = Some("Must be an RDS instance that supports encryption"),
        Default               = Some("db.t2.micro")
      )
      val dbName = "RdsUP"
      val rdsInstance = RdsBuilder.buildRds(
        name                       = "RdsUsingParameters",
        rdsSource                  = NewRds(
          rdsAvailabilityZone   = RdsNoAZ(),
          rdsEncryption         = RdsEncryptionNone(),
          engine                = `AWS::RDS::DBInstance::Engine`.postgres,
          masterUsername        = ParameterRef(rdsUser),
          masterUserPassword    = ParameterRef(rdsPassword),
          backupRetentionPeriod = Some("7"),
          dbName                = Some(dbName)
        ),
        rdsLocation                = RdsClassic(),
        allocatedStorage           = Right(ParameterRef(rdsAllocatedStorage)),
        dbInstanceClass            = ParameterRef(rdsInstanceClass),
        rdsStorageType             = RdsStorageTypeGp2(),
        allowMajorVersionUpgrade   = Some(false),
        autoMinorVersionUpgrade    = Some(true),
        dbInstanceIdentifier       = Some(`Fn::Join`("-", Seq(`AWS::StackName`, dbName))),
        publiclyAccessible         = Some(false),
        tags                       = Some(AmazonTag.fromName(dbName))
      )
      val expected = JsObject(
        "RdsUsingParameters" -> JsObject(
          "Type" -> JsString("AWS::RDS::DBInstance"),
          "Properties" -> JsObject(
            "AllocatedStorage" -> JsObject("Ref" -> JsString(rdsAllocatedStorage.name)),
            "AllowMajorVersionUpgrade" -> JsBoolean(false),
            "AutoMinorVersionUpgrade" -> JsBoolean(true),
            "BackupRetentionPeriod" -> JsString("7"),
            "DBInstanceClass" -> JsObject("Ref" -> JsString(rdsInstanceClass.name)),
            "DBInstanceIdentifier" -> JsObject(
              "Fn::Join" -> JsArray(
                JsString("-"),
                JsArray(
                  JsObject("Ref" -> JsString("AWS::StackName")),
                  JsString(dbName)
                )
              )
            ),
            "DBName" -> JsString(dbName),
            "Engine" -> JsString("postgres"),
            "MasterUsername" -> JsObject("Ref" -> JsString(rdsUser.name)),
            "MasterUserPassword" -> JsObject("Ref" -> JsString(rdsPassword.name)),
            "PubliclyAccessible" -> JsBoolean(false),
            "StorageType" -> JsString("gp2"),
            "Tags" -> JsArray(JsObject(
              "Key" -> JsString("Name"),
              "Value" -> JsObject("Fn::Join" -> JsArray(
                JsString("-"),
                JsArray(
                  JsString(dbName),
                  JsObject("Ref" -> JsString("AWS::StackName"))
                )
              ))
            ))
          )
        )
      )
      Seq[Resource[_]](rdsInstance).toJson should be (expected)
    }
  }
}
