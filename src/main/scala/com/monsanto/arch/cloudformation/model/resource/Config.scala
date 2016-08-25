package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

sealed trait ExecutionFrequency
object ExecutionFrequency extends DefaultJsonProtocol {

  case object One_Hour          extends ExecutionFrequency
  case object Three_Hours       extends ExecutionFrequency
  case object Six_Hours         extends ExecutionFrequency
  case object Twelve_Hours      extends ExecutionFrequency
  case object TwentyFour_Hours  extends ExecutionFrequency

  implicit val format: JsonFormat[ExecutionFrequency] = new JsonFormat[ExecutionFrequency] {
    def write(f: ExecutionFrequency) = JsString(f.toString)

    def read(value: JsValue) = {
      value.toString() match {
        case "One_Hour"         => ExecutionFrequency.One_Hour
        case "Three_Hours"      => ExecutionFrequency.Three_Hours
        case "Six_Hours"        => ExecutionFrequency.Six_Hours
        case "Twelve_Hours"     => ExecutionFrequency.Twelve_Hours
        case "TwentyFour_Hours" => ExecutionFrequency.TwentyFour_Hours
      }
    }
  }
}


sealed trait ResourceType
object ResourceType extends DefaultJsonProtocol {

  case object `AWS::ACM::Certificate`         extends ResourceType
  case object `AWS::CloudTrail::Trail`        extends ResourceType
  case object `AWS::EC2::Volume`              extends ResourceType
  case object `AWS::EC2::Host`                extends ResourceType
  case object `AWS::EC2::EIP`                 extends ResourceType
  case object `AWS::EC2::Instance`            extends ResourceType
  case object `AWS::EC2::NetworkInterface`    extends ResourceType
  case object `AWS::EC2::SecurityGroup`       extends ResourceType
  case object `AWS::IAM::User`                extends ResourceType
  case object `AWS::IAM::Group`               extends ResourceType
  case object `AWS::IAM::Role`                extends ResourceType
  case object `AWS::IAM::Policy`              extends ResourceType
  case object `AWS::RDS::DBInstance`          extends ResourceType
  case object `AWS::RDS::DBSecurityGroup`     extends ResourceType
  case object `AWS::RDS::DBSnapshot`          extends ResourceType
  case object `AWS::RDS::DBSubnetGroup`       extends ResourceType
  case object `AWS::RDS::EventSubscription`   extends ResourceType
  case object `AWS::EC2::CustomerGateway`     extends ResourceType
  case object `AWS::EC2::InternetGateway`     extends ResourceType
  case object `AWS::EC2::NetworkAcl`          extends ResourceType
  case object `AWS::EC2::RouteTable`          extends ResourceType
  case object `AWS::EC2::Subnet`              extends ResourceType
  case object `AWS::EC2::VPC`                 extends ResourceType
  case object `AWS::EC2::VPNConnection`       extends ResourceType
  case object `AWS::EC2::VPNGateway`          extends ResourceType

  implicit val format: JsonFormat[ResourceType] = new JsonFormat[ResourceType] {
    def write(f: ResourceType) = JsString(f.toString)

    def read(value: JsValue) = {
      value.toString() match {
        case "AWS::ACM::Certificate"         => ResourceType.`AWS::ACM::Certificate`
        case "AWS::CloudTrail::Trail"        => ResourceType.`AWS::CloudTrail::Trail`
        case "AWS::EC2::Volume"              => ResourceType.`AWS::EC2::Volume`
        case "AWS::EC2::Host"                => ResourceType.`AWS::EC2::Host`
        case "AWS::EC2::EIP"                 => ResourceType.`AWS::EC2::EIP`
        case "AWS::EC2::Instance"            => ResourceType.`AWS::EC2::Instance`
        case "AWS::EC2::NetworkInterface"    => ResourceType.`AWS::EC2::NetworkInterface`
        case "AWS::EC2::SecurityGroup"       => ResourceType.`AWS::EC2::SecurityGroup`
        case "AWS::IAM::User"                => ResourceType.`AWS::IAM::User`
        case "AWS::IAM::Group"               => ResourceType.`AWS::IAM::Group`
        case "AWS::IAM::Role"                => ResourceType.`AWS::IAM::Role`
        case "AWS::IAM::Policy"              => ResourceType.`AWS::IAM::Policy`
        case "AWS::RDS::DBInstance"          => ResourceType.`AWS::RDS::DBInstance`
        case "AWS::RDS::DBSecurityGroup"     => ResourceType.`AWS::RDS::DBSecurityGroup`
        case "AWS::RDS::DBSnapshot"          => ResourceType.`AWS::RDS::DBSnapshot`
        case "AWS::RDS::DBSubnetGroup"       => ResourceType.`AWS::RDS::DBSubnetGroup`
        case "AWS::RDS::EventSubscription"   => ResourceType.`AWS::RDS::EventSubscription`
        case "AWS::EC2::CustomerGateway"     => ResourceType.`AWS::EC2::CustomerGateway`
        case "AWS::EC2::InternetGateway"     => ResourceType.`AWS::EC2::InternetGateway`
        case "AWS::EC2::NetworkAcl"          => ResourceType.`AWS::EC2::NetworkAcl`
        case "AWS::EC2::RouteTable"          => ResourceType.`AWS::EC2::RouteTable`
        case "AWS::EC2::Subnet"              => ResourceType.`AWS::EC2::Subnet`
        case "AWS::EC2::VPC"                 => ResourceType.`AWS::EC2::VPC`
        case "AWS::EC2::VPNConnection"       => ResourceType.`AWS::EC2::VPNConnection`
        case "AWS::EC2::VPNGateway"          => ResourceType.`AWS::EC2::VPNGateway`
      }
    }
  }
}

case class Scope(
  ComplianceResourceId:       Option[Token[ResourceRef[_]]] = None,
  ComplianceResourceTypes:    Option[List[ResourceType]] = None,
  TagKey:                     Option[String] = None,
  TagValue:                   Option[String] = None
)
object Scope extends DefaultJsonProtocol {
  implicit val format = jsonFormat4(Scope.apply)
}

sealed trait SourceOwner
object SourceOwner extends DefaultJsonProtocol {

  case object CUSTOM_LAMBDA   extends SourceOwner
  case object AWS             extends SourceOwner

  implicit val format: JsonFormat[SourceOwner] = new JsonFormat[SourceOwner] {
    def write(f: SourceOwner) = JsString(f.toString)

    def read(value: JsValue) = {
      value.toString() match {
        case "CUSTOM_LAMBDA"  => SourceOwner.CUSTOM_LAMBDA
        case "AWS"            => SourceOwner.AWS
      }
    }
  }
}

sealed trait EventSource
object EventSource extends DefaultJsonProtocol {

  case object `aws.config`       extends EventSource

  implicit val format: JsonFormat[EventSource] = new JsonFormat[EventSource] {
    def write(f: EventSource) = JsString(f.toString)

    def read(value: JsValue) = {
      value.toString() match {
        case "aws.config"      => EventSource.`aws.config`
      }
    }
  }
}

sealed trait MessageType
object MessageType extends DefaultJsonProtocol {

  case object ConfigurationItemChangeNotification       extends MessageType
  case object ConfigurationSnapshotDeliveryCompleted    extends MessageType
  case object ScheduledNotification                     extends MessageType

  implicit val format: JsonFormat[MessageType] = new JsonFormat[MessageType] {
    def write(f: MessageType) = JsString(f.toString)

    def read(value: JsValue) = {
      value.toString() match {
        case "ConfigurationItemChangeNotification"      => MessageType.ConfigurationItemChangeNotification
        case "ConfigurationSnapshotDeliveryCompleted"   => MessageType.ConfigurationSnapshotDeliveryCompleted
        case "ScheduledNotification"                    => MessageType.ScheduledNotification
      }
    }
  }
}

case class SourceDetail(
                         EventSource:                Option[EventSource] = None,
                         MaximumExecutionFrequency:  Option[ExecutionFrequency] = None,
                         MessageType:                Option[MessageType] = None
                       )
object SourceDetail extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(SourceDetail.apply)
}

case class Source(
                   Owner:                Option[SourceOwner] = None,
                   SourceDetails:        Option[List[SourceDetail]] = None,
                   SourceIdentifier:     Option[Token[ResourceRef[_]]] = None
                 )
object Source extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(Source.apply)
}

case class `AWS::Config::ConfigRule`(
                                      name:                       String,
                                      ConfigRuleName:             Option[String] = None,
                                      Description:                Option[String] = None,
                                      InputParameters:            Option[Map[String, Token[String]]] = None,
                                      MaximumExecutionFrequency:  Option[ExecutionFrequency] = None,
                                      Scope:                      Option[Scope] = None,
                                      Source:                     Option[Source] = None,
                                      override val Condition:  Option[ConditionRef] = None
                                    ) extends Resource[`AWS::Config::ConfigRule`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::Config::ConfigRule` = copy(Condition = newCondition)
}
object `AWS::Config::ConfigRule` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Config::ConfigRule`] = jsonFormat8(`AWS::Config::ConfigRule`.apply)
}

case class RecordingGroup(
                           AllSupported:                   Option[Boolean] = None,
                           IncludeGlobalResourceTypes:     Option[Boolean] = None,
                           ResourceTypes:                  Option[List[ResourceType]] = None
                         )
object RecordingGroup extends DefaultJsonProtocol {
  implicit val format = jsonFormat3(RecordingGroup.apply)
}

case class `AWS::Config::ConfigurationRecorder`(
  name:               String,
  Name:               Option[String],
  RecordingGroup:     Option[RecordingGroup],
  RoleARN:            Token[String],
  override val Condition:  Option[ConditionRef] = None
) extends Resource[`AWS::Config::ConfigurationRecorder`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::Config::ConfigurationRecorder` = copy(Condition = newCondition)
}
object `AWS::Config::ConfigurationRecorder` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Config::ConfigurationRecorder`] = jsonFormat5(`AWS::Config::ConfigurationRecorder`.apply)
}

case class ConfigSnapshotDeliveryProperties(
  DeliveryFrequency: Option[ExecutionFrequency]
)
object ConfigSnapshotDeliveryProperties extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(ConfigSnapshotDeliveryProperties.apply)
}

case class `AWS::Config::DeliveryChannel`(
  name:                                 String,
  ConfigSnapshotDeliveryProperties:     Option[ConfigSnapshotDeliveryProperties],
  Name:                                 Option[String],
  S3BucketName:                         Option[Token[String]],
  S3KeyPrefix:                          Option[String],
  SnsTopicARN:                          Option[Token[String]],
  override val Condition:               Option[ConditionRef] = None
) extends Resource[`AWS::Config::DeliveryChannel`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::Config::DeliveryChannel` = copy(Condition = newCondition)
}
object `AWS::Config::DeliveryChannel` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Config::DeliveryChannel`] = jsonFormat7(`AWS::Config::DeliveryChannel`.apply)
}
