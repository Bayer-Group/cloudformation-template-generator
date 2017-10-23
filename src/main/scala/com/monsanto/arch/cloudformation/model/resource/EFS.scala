package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

import scala.language.implicitConversions

case class `AWS::EFS::MountTarget`(
    name: String,
    FileSystemId: ResourceRef[`AWS::EFS::FileSystem`],
    IpAddress: Option[String] = None,
    SecurityGroups: Seq[ResourceRef[`AWS::EC2::SecurityGroup`]],
    SubnetId: ResourceRef[`AWS::EC2::Subnet`],
    override val Condition: Option[ConditionRef] = None,
    override val DependsOn: Option[Seq[String]] = None
  ) extends Resource[`AWS::EFS::MountTarget`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::EFS::MountTarget` =
    copy(Condition = newCondition)
}

object `AWS::EFS::MountTarget` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EFS::MountTarget`] = jsonFormat7(`AWS::EFS::MountTarget`.apply)
}

sealed trait PerformanceMode
object PerformanceMode extends DefaultJsonProtocol {
  case object generalPurpose extends PerformanceMode
  case object maxIO extends PerformanceMode

  val values = List(generalPurpose, maxIO)
  implicit val format: JsonFormat[PerformanceMode] = new EnumFormat[PerformanceMode](values)
}

case class `AWS::EFS::FileSystem`(
    name: String,
    FileSystemTags: Option[Seq[AmazonTag]] = None,
    Encrypted: Boolean = false,
    KmsKeyId: Option[ResourceRef[`AWS::KMS::Key`]] = None,
    PerformanceMode: Option[PerformanceMode] = None,
    override val Condition: Option[ConditionRef] = None,
    override val DependsOn: Option[Seq[String]] = None
  ) extends Resource[`AWS::EFS::FileSystem`] {

  if (KmsKeyId.isDefined) {
    require(Encrypted, "Encrypted must be true if KmsKeyId is also set")
  }

  override def when(newCondition: Option[ConditionRef]): `AWS::EFS::FileSystem` =
    copy(Condition = newCondition)
}

object `AWS::EFS::FileSystem` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EFS::FileSystem`] = jsonFormat7(`AWS::EFS::FileSystem`.apply)
}
