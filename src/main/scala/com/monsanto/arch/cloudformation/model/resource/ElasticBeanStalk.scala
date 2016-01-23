package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ResourceRef, ConditionRef, Token, `Fn::GetAtt`}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, JsonFormat}

case class `AWS::ElasticBeanstalk::Application`(
                                                 name: String,
                                                 ApplicationName: Option[String] = None,
                                                 Description: Option[String] = None,
                                                 override val Condition: Option[ConditionRef] = None
                                               ) extends Resource[`AWS::ElasticBeanstalk::Application`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}

object `AWS::ElasticBeanstalk::Application` {
  implicit val format: JsonFormat[`AWS::ElasticBeanstalk::Application`] = jsonFormat4(`AWS::ElasticBeanstalk::Application`.apply)
}

case class `AWS::ElasticBeanstalk::ApplicationVersion`(
                                                        name: String,
                                                        ApplicationName: Token[String],
                                                        Description: Option[String] = None,
                                                        SourceBundle: SourceBundle,
                                                        override val Condition: Option[ConditionRef] = None
                                                      ) extends Resource[`AWS::ElasticBeanstalk::ApplicationVersion`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}

object `AWS::ElasticBeanstalk::ApplicationVersion` {
  implicit val format: JsonFormat[`AWS::ElasticBeanstalk::ApplicationVersion`] = jsonFormat5(`AWS::ElasticBeanstalk::ApplicationVersion`.apply)
}

case class SourceBundle(
                         S3Bucket: String,
                         S3Key: String
                       )

object SourceBundle {
  implicit val format: JsonFormat[SourceBundle] = jsonFormat2(SourceBundle.apply)
}

case class `AWS::ElasticBeanstalk::ConfigurationTemplate`(
                                                           name: String,
                                                           ApplicationName: ResourceRef[`AWS::ElasticBeanstalk::Application`],
                                                           Description: Option[String] = None,
                                                           EnvironmentId: Option[String],
                                                           OptionSettings: Option[Seq[OptionSetting]],
                                                           SolutionStackName: Option[String],
                                                           SourceConfiguration: Option[SourceConfiguration],
                                                           override val Condition: Option[ConditionRef] = None
                                                         ) extends Resource[`AWS::ElasticBeanstalk::ConfigurationTemplate`] {

  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}

object `AWS::ElasticBeanstalk::ConfigurationTemplate` {
  implicit val format: JsonFormat[`AWS::ElasticBeanstalk::ConfigurationTemplate`] = jsonFormat8(`AWS::ElasticBeanstalk::ConfigurationTemplate`.apply)
}

case class SourceConfiguration(
                                //ApplicationName: Resource[`AWS::ElasticBeanstalk::Application`],
                                //TemplateName: Resource[`AWS::ElasticBeanstalk::ConfigurationTemplate`]
                                ApplicationName: Token[String],
                                TemplateName: Token[String]
                              )

object SourceConfiguration {
  implicit val format: JsonFormat[SourceConfiguration] = jsonFormat2(SourceConfiguration.apply)
}

case class `AWS::ElasticBeanstalk::Environment`(
                                                 name: String,
                                                 ApplicationName: ResourceRef[`AWS::ElasticBeanstalk::Application`],
                                                 CNAMEPrefix: Option[String] = None,
                                                 Description: Option[String] = None,
                                                 EnvironmentName: Option[String] = None,
                                                 OptionSettings: Option[Seq[OptionSetting]] = None,
                                                 SolutionStackName: Option[String] = None,
                                                 Tags: Option[Seq[ResourceTag]] = None,
                                                 TemplateName: Option[String] = None,
                                                 Tier: Option[EnvironmentTier] = None,
                                                 VersionLabel: Option[String] = None,
                                                 override val Condition: Option[ConditionRef] = None
                                               ) extends Resource[`AWS::ElasticBeanstalk::Environment`] {
  def endpointUrl: Token[String] = `Fn::GetAtt`(Seq(name, "EndpointURL"))

  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}

object `AWS::ElasticBeanstalk::Environment` {
  implicit val format: JsonFormat[`AWS::ElasticBeanstalk::Environment`] = jsonFormat12(`AWS::ElasticBeanstalk::Environment`.apply)
}

case class OptionSetting(
                          Namespace: String,
                          OptionName: String,
                          Value: String
                        )

object OptionSetting {
  implicit val format: JsonFormat[OptionSetting] = jsonFormat3(OptionSetting.apply)
}

case class ResourceTag(
                        Key: String,
                        Value: String
                      )

object ResourceTag {
  implicit val format: JsonFormat[ResourceTag] = jsonFormat2(ResourceTag.apply)
}

sealed abstract class EnvironmentTierType(val tierType: String)

object EnvironmentTierType {
  implicit val format = new JsonFormat[EnvironmentTierType] {
    override def read(json: JsValue) = ???

    override def write(obj: EnvironmentTierType) = JsString(obj.tierType)
  }
}

/**
  * WebServer
  */
case object Standard extends EnvironmentTierType("Standard")

/**
  * Worker Tier
  */
case object `SQS/HTTP` extends EnvironmentTierType("SQS/HTTP")

case class EnvironmentTier(
                            Name: Option[String],
                            Type: Option[EnvironmentTierType],
                            Version: Option[String]
                          )

object EnvironmentTier {
  implicit val format: JsonFormat[EnvironmentTier] = jsonFormat3(EnvironmentTier.apply)
}

