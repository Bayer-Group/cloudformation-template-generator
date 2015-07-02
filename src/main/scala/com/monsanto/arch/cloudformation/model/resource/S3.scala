package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token}
import spray.json._

/**
 * Created by bkrodg on 3/30/15.
 */
case class `AWS::S3::Bucket`(
                             name:                      String,
                             BucketName:                Option[Token[String]],
                             AccessControl:             Option[S3AccessControl],
                             CorsConfiguration:         Option[S3CorsRules] = None,
                             LifecycleConfiguration:    Option[S3LifecycleConfigurationRules] = None,
                             LoggingConfiguration:      Option[String] = None,
                             NotificationConfiguration: Option[String] = None,
                             VersioningConfiguration:   Option[String] = None,
                             WebsiteConfiguration:      Option[String] = None,
                             Tags:                      Option[Seq[AmazonTag]] = None,
                             override val Condition:    Option[ConditionRef] = None
                               ) extends Resource[`AWS::S3::Bucket`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::S3::Bucket` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::S3::Bucket`] = jsonFormat11(`AWS::S3::Bucket`.apply)
}

case class S3CorsRules(CorsRules: Seq[S3CorsRule])
object S3CorsRules extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3CorsRules] = jsonFormat1(S3CorsRules.apply)
}

case class S3CorsRule(AllowedMethods: Seq[String],
                      AllowedOrigins: Seq[String],
                      AllowedHeaders: Option[Seq[String]],
                      ExposedHeaders: Option[Seq[String]],
                      Id:             Option[String],
                      MaxAge:         Option[Int])
object S3CorsRule extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3CorsRule] = jsonFormat6(S3CorsRule.apply)
}

case class S3LifecycleConfigurationRules(Rules: Seq[S3LifecycleConfigurationRule])
object S3LifecycleConfigurationRules extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3LifecycleConfigurationRules] = jsonFormat1(S3LifecycleConfigurationRules.apply)
}

case class S3LifecycleConfigurationRule(Status: S3LifecycleConfigurationRuleStatus,
                                        ExpirationDate: Option[String],
                                        ExpirationInDays: Option[Int],
                                        Id: Option[String],
                                        Prefix: Option[String],
                                        Transition: Option[S3LifecycleTransition])
object S3LifecycleConfigurationRule extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3LifecycleConfigurationRule] = jsonFormat6(S3LifecycleConfigurationRule.apply)
}

case class S3LifecycleTransition(StorageClass: String,
                                 TransitionDate: Option[String],
                                 TransitionInDays: Option[Int])
object S3LifecycleTransition extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3LifecycleTransition] = jsonFormat3(S3LifecycleTransition.apply)
}

sealed trait S3LifecycleConfigurationRuleStatus
object S3LifecycleConfigurationRuleStatus extends DefaultJsonProtocol {
  case object Enabled extends S3LifecycleConfigurationRuleStatus
  case object Disabled extends S3LifecycleConfigurationRuleStatus

  implicit val format: JsonFormat[S3LifecycleConfigurationRuleStatus] = new JsonFormat[S3LifecycleConfigurationRuleStatus] {

    override def write(obj: S3LifecycleConfigurationRuleStatus)= JsString(obj.toString)

    override def read(json: JsValue): S3LifecycleConfigurationRuleStatus = {
      json.toString match {
        case "Enabled"  => Enabled
        case "Disabled" => Disabled
      }
    }
  }
}

sealed trait S3AccessControl
object S3AccessControl                  extends DefaultJsonProtocol {
  case object Private                   extends S3AccessControl
  case object PublicRead                extends S3AccessControl
  case object PublicReadWrite           extends S3AccessControl
  case object AuthenticatedRead         extends S3AccessControl
  case object LogDeliveryWrite          extends S3AccessControl
  case object BucketOwnerRead           extends S3AccessControl
  case object BucketOwnerFullControl    extends S3AccessControl

  implicit val format: JsonFormat[S3AccessControl] = new JsonFormat[S3AccessControl] {

    override def write(obj: S3AccessControl) = JsString(obj.toString)

    override def read(json: JsValue): S3AccessControl = {
      json.toString match {
        case "Private"                    => Private
        case "PublicRead"                 => PublicRead
        case "PublicReadWrite"            => PublicReadWrite
        case "AuthenticatedRead"          => AuthenticatedRead
        case "LogDeliveryWrite"           => LogDeliveryWrite
        case "BucketOwnerRead"            => BucketOwnerRead
        case "BucketOwnerFullControl"     => BucketOwnerFullControl
      }
    }
  }
}

case class `AWS::S3::BucketPolicy`(
                                    name: String,
                                    Bucket: Token[String],
                                    PolicyDocument: PolicyDocument,
                                    override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::S3::BucketPolicy`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::S3::BucketPolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::S3::BucketPolicy`] = jsonFormat4(`AWS::S3::BucketPolicy`.apply)
}