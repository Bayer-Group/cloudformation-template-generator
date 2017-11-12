package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

import scala.language.implicitConversions

/** not tested but including for completeness */
case class `AWS::Redshift::Cluster`(
    name: String,
    AllowVersionUpgrade: Option[Boolean] = None,
    AutomatedSnapshotRetentionPeriod: Option[Int] = None,
    AvailabilityZone: Option[Token[String]] = None,
    ClusterParameterGroupName: Option[ResourceRef[`AWS::Redshift::ClusterParameterGroup`]] = None,
    ClusterSecurityGroups: Option[Seq[ResourceRef[`AWS::Redshift::ClusterSecurityGroup`]]] = None,
    ClusterSubnetGroupName: Option[ResourceRef[`AWS::Redshift::ClusterSubnetGroup`]] = None,
    ClusterType: Token[String],
    ClusterVersion: Option[Token[String]] = None,
    DBName: Token[String],
    ElasticIp: Option[ResourceRef[`AWS::EC2::EIP`]] = None,
    Encrypted: Option[Boolean] = None,
    HsmClientCertificateIdentifier: Option[Token[String]] = None,
    HsmConfigurationIdentifier: Option[Token[String]] = None,
    MasterUsername: Token[String],
    MasterUserPassword: Token[String],
    NodeType: Token[String],
    NumberOfNodes: Option[Token[Int]], // required if multi-node
    OwnerAccount: Option[Token[String]] = None, // required if restoring snapshot
    Port: Option[Int] = None,
    PreferredMaintenanceWindow: Option[Token[String]] = None,
    PubliclyAccessible: Option[Boolean] = None,
    SnapshotClusterIdentifier: Option[Token[String]] = None,
    SnapshotIdentifier: Option[Token[String]] = None,
    VpcSecurityGroupIds: Option[Seq[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None,
    override val DependsOn: Option[Seq[String]] = None,
    override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Redshift::Cluster`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
/** Sorry this looks ugly but arity only goes to 22 :( */
object `AWS::Redshift::Cluster` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Redshift::Cluster`] = new RootJsonFormat[`AWS::Redshift::Cluster`] {
    def write(c: `AWS::Redshift::Cluster`) = {
      val obj = JsObject(
        "AllowVersionUpgrade" -> c.AllowVersionUpgrade.toJson,
        "AutomatedSnapshotRetentionPeriod" -> c.AutomatedSnapshotRetentionPeriod.toJson,
        "AvailabilityZone" -> c.AvailabilityZone.toJson,
        "ClusterParameterGroupName" -> c.ClusterParameterGroupName.toJson,
        "ClusterSecurityGroups" -> c.ClusterSecurityGroups.toJson,
        "ClusterSubnetGroupName" -> c.ClusterSubnetGroupName.toJson,
        "ClusterType" -> c.ClusterType.toJson,
        "ClusterVersion" -> c.ClusterVersion.toJson,
        "DBName" -> c.DBName.toJson,
        "ElasticIp" -> c.ElasticIp.toJson,
        "Encrypted" -> c.Encrypted.toJson,
        "HsmClientCertificateIdentifier" -> c.HsmClientCertificateIdentifier.toJson,
        "HsmConfigurationIdentifier" -> c.HsmConfigurationIdentifier.toJson,
        "MasterUsername" -> c.MasterUsername.toJson,
        "MasterUserPassword" -> c.MasterUserPassword.toJson,
        "NodeType" -> c.NodeType.toJson,
        "NumberOfNodes" -> c.NumberOfNodes.toJson,
        "OwnerAccount" -> c.OwnerAccount.toJson,
        "Port" -> c.Port.toJson,
        "PreferredMaintenanceWindow" -> c.PreferredMaintenanceWindow.toJson,
        "PubliclyAccessible" -> c.PubliclyAccessible.toJson,
        "SnapshotClusterIdentifier" -> c.SnapshotClusterIdentifier.toJson,
        "SnapshotIdentifier" -> c.SnapshotIdentifier.toJson,
        "VpcSecurityGroupIds" -> c.VpcSecurityGroupIds.toJson
      )
      obj.copy(fields = obj.fields.filter(_._2 != JsNull))
    }

    // TODO: does anyone use this code to read JSON in rather than just write?
    def read(value: JsValue) = ???
  }
}

/**
 * For some reason Amazon uses the logical name as the actual group name here.
 * Not sure why they did it that way, but if you want to condition the name on anything
 * then you can either use parameters or custom resources to talk to lambda or something.
 */
case class `AWS::Redshift::ClusterSecurityGroup`(
    name: String,
    Description: String,
    override val DependsOn: Option[Seq[String]] = None,
    override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Redshift::ClusterSecurityGroup`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::Redshift::ClusterSecurityGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Redshift::ClusterSecurityGroup`] = jsonFormat4(`AWS::Redshift::ClusterSecurityGroup`.apply)
}

case class `AWS::Redshift::ClusterSecurityGroupIngress`(
    name: String,
    ClusterSecurityGroupName: Token[String],
    CIDRIP: Option[Token[String]] = None,
    EC2SecurityGroupName: Option[ResourceRef[`AWS::EC2::SecurityGroup`]] = None,
    EC2SecurityGroupOwnerId: Option[Token[String]] = None,
    override val DependsOn: Option[Seq[String]] = None,
    override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Redshift::ClusterSecurityGroupIngress`] {
  require(CIDRIP.isDefined ^ (EC2SecurityGroupName.isDefined && EC2SecurityGroupOwnerId.isDefined))
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::Redshift::ClusterSecurityGroupIngress` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Redshift::ClusterSecurityGroupIngress`] = jsonFormat7(`AWS::Redshift::ClusterSecurityGroupIngress`.apply)
}

case class `AWS::Redshift::ClusterParameterGroup`(
    name: String,
    Description: String,
    ParameterGroupFamily: Token[String],
    Parameters: Option[Seq[RedshiftClusterParameter]] = None,
    override val DependsOn: Option[Seq[String]] = None,
    override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Redshift::ClusterParameterGroup`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::Redshift::ClusterParameterGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Redshift::ClusterParameterGroup`] = jsonFormat6(`AWS::Redshift::ClusterParameterGroup`.apply)
}

case class RedshiftClusterParameter(
    ParameterName: Token[String],
    ParameterValue: Token[String])
object RedshiftClusterParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RedshiftClusterParameter] = jsonFormat2(RedshiftClusterParameter.apply)
}

case class `AWS::Redshift::ClusterSubnetGroup`(
    name: String,
    Description: String,
    SubnetIds: Seq[ResourceRef[`AWS::EC2::Subnet`]],
    override val DependsOn: Option[Seq[String]] = None,
    override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Redshift::ClusterSubnetGroup`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::Redshift::ClusterSubnetGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Redshift::ClusterSubnetGroup`] = jsonFormat5(`AWS::Redshift::ClusterSubnetGroup`.apply)
}
