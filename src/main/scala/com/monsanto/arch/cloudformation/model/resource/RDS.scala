package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

// We're mapping "RecordName" to "Name," not to be confused with the name we give the resource in the template, which is called "name" at the Scala level.
// Similarly, "RecordType" becomes "Type," which is different from the "type" of the resource.
// Note that if you add, remove, or re-order fields, you must update the JSON mapping.  ORDER COUNTS!
// Not yet supporting advanced DNS parameters: Failover, GeoLocation, HealthCheckId, Region, SetIdentifier, Weight
// Also, didn't include HostedZoneId.  Seemed like it'd always be easier to go with HostedZoneName,
// and it makes the object model cleaner not to support both.

case class `AWS::RDS::DBInstance`(
  name:                       String,
  AllocatedStorage:           Token[String],
  DBInstanceClass:            Token[String],
  //AllowMajorVersionUpgrade: Option[Boolean] = None,
  //AllowMinorVersionUpgrade: Option[Boolean] = None,
  //AvailabilityZone: Option[String] = None, // cannot be set when MultiAZ set
  BackupRetentionPeriod:      Option[String] = None,
  DBInstanceIdentifier:       Option[Token[String]] = None,
  DBName:                     Option[Token[String]] = None,
  DBParameterGroupName:       Option[ResourceRef[`AWS::RDS::DBParameterGroup`]] = None,
  //DBSecurityGroups: Option[Seq[ResourceRef[`AWS::RDS::DBSecurityGroup`]]] = None, // cannot be set when VPCSecurityGroups set
  DBSnapshotIdentifier:       Option[String] = None,
  DBSubnetGroupName:          Option[ResourceRef[`AWS::RDS::DBSubnetGroup`]] = None, // required for VPC RDS
  Engine:                     Option[Token[`AWS::RDS::DBInstance::Engine`]] = None, // required if no DBSnapshotIdentifier given
  EngineVersion:              Option[String] = None,
  Iops:                       Option[String] = None, // multiple of 1000 between 1000 and 10000
  //LicenseModel: Option[String] = None,
  MasterUsername:             Option[Token[String]] = None, // required if no DBSnapshotIdentifier given
  MasterUserPassword:         Option[Token[String]] = None, // required if no DBSnapshotIdentifier given
  MultiAZ:                    Option[Boolean] = None, // cannot be set when AvailabilityZone set
  //OptionGroupName: Option[String] = None,
  Port:                       Option[String] = None,
  PreferredBackupWindow:      Option[String] = None,
  PreferredMaintenanceWindow: Option[String] = None,
  //PubliclyAccessible: Option[Boolean] = None,
  //SourceDBInstanceIdentifier: Option[ResourceRef[`AWS::RDS::DBInstance`]] = None, // does not work with MultiAZ, DBSnapshotIdentifier, etc.
  StorageType:                Option[`AWS::RDS::DBInstance::StorageType`] = None,
  Tags:                       Option[Seq[AmazonTag]] = None,
  VPCSecurityGroups:          Option[Seq[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None, // cannot be set when DBSecurityGroups set
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::RDS::DBInstance`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBInstance` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBInstance`] = jsonFormat22(`AWS::RDS::DBInstance`.apply)
}

sealed abstract class `AWS::RDS::DBInstance::Engine`(val name: String)
object `AWS::RDS::DBInstance::Engine` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::Engine`] = new JsonFormat[`AWS::RDS::DBInstance::Engine`] {
    def write(obj: `AWS::RDS::DBInstance::Engine`) = JsString(obj.name)
    //TODO
    def read(json: JsValue) = ???
  }
}
case object `AWS::RDS::DBInstance::Engine::MySQL`    extends `AWS::RDS::DBInstance::Engine`("MySQL")
case object `AWS::RDS::DBInstance::Engine::postgres` extends `AWS::RDS::DBInstance::Engine`("postgres")

sealed abstract class `AWS::RDS::DBInstance::StorageType`(val name: String)
object `AWS::RDS::DBInstance::StorageType` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::StorageType`] = new JsonFormat[`AWS::RDS::DBInstance::StorageType`] {
    def write(obj: `AWS::RDS::DBInstance::StorageType`) = JsString(obj.name)
    //TODO
    def read(json: JsValue) = ???
  }
}
case object `AWS::RDS::DBInstance::Storage::standard` extends `AWS::RDS::DBInstance::StorageType`("standard")
case object `AWS::RDS::DBInstance::Storage::gp2`      extends `AWS::RDS::DBInstance::StorageType`("gp2")
case object `AWS::RDS::DBInstance::Storage::io1`      extends `AWS::RDS::DBInstance::StorageType`("io1")

case class `AWS::RDS::DBParameterGroup`(
  name:        String,
  Description: String,
  Family:      String,
  Parameters:  Option[Map[String,String]] = None,
  Tags:        Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::RDS::DBParameterGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBParameterGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBParameterGroup`] = jsonFormat6(`AWS::RDS::DBParameterGroup`.apply)
}

case class `AWS::RDS::DBSecurityGroup`(
  name:                   String,
  DBSecurityGroupIngress: Seq[Map[String,String]], //finite list of keys: http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-rds-security-group-rule.html
  GroupDescription:       String,
  EC2VpcId:               Option[ResourceRef[`AWS::EC2::VPC`]] = None,
  Tags:                   Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::RDS::DBSecurityGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBSecurityGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBSecurityGroup`] = jsonFormat6(`AWS::RDS::DBSecurityGroup`.apply)
}

case class `AWS::RDS::DBSubnetGroup`(
  name:                     String,
  DBSubnetGroupDescription: String,
  SubnetIds:                Seq[ResourceRef[`AWS::EC2::Subnet`]],
  Tags:                     Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::RDS::DBSubnetGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBSubnetGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBSubnetGroup`] = jsonFormat5(`AWS::RDS::DBSubnetGroup`.apply)
}
