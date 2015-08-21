package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ResourceRef, ConditionRef, Token}
import spray.json._

/**
 * Created by bkrodg on 3/30/15.
 */
case class `AWS::S3::Bucket`(name:                      String,
                             BucketName:                Option[Token[String]],
                             AccessControl:             Option[S3AccessControl] = None,
                             CorsConfiguration:         Option[S3CorsRules] = None,
                             LifecycleConfiguration:    Option[S3LifecycleConfigurationRules] = None,
                             LoggingConfiguration:      Option[S3LoggingConfiguration] = None,
                             NotificationConfiguration: Option[TopicConfigurations] = None,
                             VersioningConfiguration:   Option[S3VersioningConfiguration] = None,
                             WebsiteConfiguration:      Option[S3WebsiteConfiguration] = None,
                             Tags:                      Option[Seq[AmazonTag]] = None,
                             override val Condition:    Option[ConditionRef] = None) extends Resource[`AWS::S3::Bucket`] {
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
object S3AccessControl extends DefaultJsonProtocol {
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

case class `AWS::S3::BucketPolicy`(name: String,
                                   Bucket: Token[String],
                                   PolicyDocument: PolicyDocument,
                                   override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::S3::BucketPolicy`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::S3::BucketPolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::S3::BucketPolicy`] = jsonFormat4(`AWS::S3::BucketPolicy`.apply)
}

case class S3LoggingConfiguration(DestinationBucketName: Option[Token[ResourceRef[`AWS::S3::Bucket`]]] = None,
                                  LogFilePrefix:         Option[Token[String]] = None)
object S3LoggingConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3LoggingConfiguration] = jsonFormat2(S3LoggingConfiguration.apply)
}

case class TopicConfigurations(TopicConfigurations: Seq[TopicConfiguration])
object TopicConfigurations extends DefaultJsonProtocol {
  implicit val format: JsonFormat[TopicConfigurations] = jsonFormat1(TopicConfigurations.apply)
}

case class TopicConfiguration(Event: S3Event,
                              Topic: Token[`AWS::SNS::Topic`])
object TopicConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[TopicConfiguration] = jsonFormat2(TopicConfiguration.apply)
}

/**
 * S3 events
 * http://docs.aws.amazon.com/AmazonS3/latest/dev/NotificationHowTo.html#notification-how-to-event-types-and-destinations
 */
sealed trait S3Event
object S3Event extends DefaultJsonProtocol {
  case object `s3:ObjectCreated:*`                       extends S3Event
  case object `s3:ObjectCreated:Put`                     extends S3Event
  case object `s3:ObjectCreated:Post`                    extends S3Event
  case object `s3:ObjectCreated:Copy`                    extends S3Event
  case object `s3:ObjectCreated:CompleteMultipartUpload` extends S3Event
  case object `s3:ObjectRemoved:*`                       extends S3Event
  case object `s3:ObjectRemoved:Delete`                  extends S3Event
  case object `s3:ObjectRemoved:DeleteMarkerCreated`     extends S3Event
  case object `s3:ReducedRedundancyLostObject`           extends S3Event

  implicit val format: JsonFormat[S3Event] = new JsonFormat[S3Event] {
    override def write(obj: S3Event)= JsString(obj.toString)
    override def read(json: JsValue): S3Event = {
      json.toString match {
        case "s3:ObjectCreated:*"                       => `s3:ObjectCreated:*`
        case "s3:ObjectCreated:Put"                     => `s3:ObjectCreated:Put`
        case "s3:ObjectCreated:Post"                    => `s3:ObjectCreated:Post`
        case "s3:ObjectCreated:Copy"                    => `s3:ObjectCreated:Copy`
        case "s3:ObjectCreated:CompleteMultipartUpload" => `s3:ObjectCreated:CompleteMultipartUpload`
        case "s3:ObjectRemoved:*"                       => `s3:ObjectRemoved:*`
        case "s3:ObjectRemoved:Delete"                  => `s3:ObjectRemoved:Delete`
        case "s3:ObjectRemoved:DeleteMarkerCreated"     => `s3:ObjectRemoved:DeleteMarkerCreated`
        case "s3:ReducedRedundancyLostObject"           => `s3:ReducedRedundancyLostObject`
      }
    }
  }
}

case class S3VersioningConfiguration(Status: S3VersioningStatus)
object S3VersioningConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3VersioningConfiguration] = jsonFormat1(S3VersioningConfiguration.apply)
}
sealed trait S3VersioningStatus
object S3VersioningStatus extends DefaultJsonProtocol {
  case object Enabled extends S3VersioningStatus
  case object Suspended extends S3VersioningStatus

  implicit val format: JsonFormat[S3VersioningStatus] = new JsonFormat[S3VersioningStatus] {
    override def write(obj: S3VersioningStatus)= JsString(obj.toString)
    override def read(json: JsValue): S3VersioningStatus = {
      json.toString match {
        case "Enabled"   => Enabled
        case "Suspended" => Suspended
      }
    }
  }
}

case class S3WebsiteConfiguration(IndexDocument:         Token[String],
                                  ErrorDocument:         Option[Token[String]] = None,
                                  RedirectAllRequestsTo: Option[S3RedirectAllRequestsTo] = None,
                                  RoutingRules:          Option[S3RoutingRules] = None)
object S3WebsiteConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3WebsiteConfiguration] = jsonFormat4(S3WebsiteConfiguration.apply)
}

case class S3RedirectAllRequestsTo(HostName:      Token[String],
                                   ErrorDocument: Option[String] = None)
object S3RedirectAllRequestsTo extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3RedirectAllRequestsTo] = jsonFormat2(S3RedirectAllRequestsTo.apply)
}

case class S3RoutingRules(RedirectRule:         Token[S3RedirectRule],
                          RoutingRuleCondition: Option[S3RoutingRuleCondition] = None)
object S3RoutingRules extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3RoutingRules] = jsonFormat2(S3RoutingRules.apply)
}

/**
 * The ReplaceKeyPrefixWith and ReplaceKeyWith properties are mutually exclusive
 * so the default constructor is private and there are three public constructors,
 * one without either and two each optionally accepting one of them.
 * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-s3-websiteconfiguration-routingrules-redirectrule.html
 *
 * @param HostName
 * @param HttpRedirectCode
 * @param Protocol
 * @param ReplaceKeyPrefixWith
 * @param ReplaceKeyWith
 */
case class S3RedirectRule private (HostName:             Option[Token[String]],
                                   HttpRedirectCode:     Option[String],
                                   Protocol:             Option[String],
                                   ReplaceKeyPrefixWith: Option[S3ReplaceKeyPrefixWith],
                                   ReplaceKeyWith:       Option[S3ReplaceKeyWith])
object S3RedirectRule extends DefaultJsonProtocol {
  def apply(HostName:         Option[Token[String]] = None,
            HttpRedirectCode: Option[String]        = None,
            Protocol:         Option[String]        = None): S3RedirectRule =
    S3RedirectRule(HostName, HttpRedirectCode, Protocol, None, None)
  def havingReplaceKeyPrefixWith(ReplaceKeyPrefixWith: S3ReplaceKeyPrefixWith,
                                 HostName: Option[Token[String]] = None,
                                 HttpRedirectCode: Option[String] = None,
                                 Protocol: Option[String] = None): S3RedirectRule =
    S3RedirectRule(HostName, HttpRedirectCode, Protocol, Some(ReplaceKeyPrefixWith), None)
  def havingReplaceKeyWith(ReplaceKeyWith: S3ReplaceKeyWith,
                           HostName: Option[Token[String]] = None,
                           HttpRedirectCode: Option[String] = None,
                           Protocol: Option[String] = None): S3RedirectRule =
    S3RedirectRule(HostName, HttpRedirectCode, Protocol, None, Some(ReplaceKeyWith))
  implicit val format: JsonFormat[S3RedirectRule] = jsonFormat5(S3RedirectRule.apply)
}
case class S3ReplaceKeyPrefixWith(replaceKeyPrefixWith: String)
object S3ReplaceKeyPrefixWith extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3ReplaceKeyPrefixWith] = jsonFormat1(S3ReplaceKeyPrefixWith.apply)
}
case class S3ReplaceKeyWith(replaceKeyWith: String)
object S3ReplaceKeyWith extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3ReplaceKeyWith] = jsonFormat1(S3ReplaceKeyWith.apply)
}

/**
 * You must specify at least one condition property
 * so the default constructor is private and there are two public constructors, each one requiring
 * one type of condition and optionally taking the other.
 * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-s3-websiteconfiguration-routingrules-routingrulecondition.html
 *
 * @param HttpErrorCodeReturnedEquals
 * @param KeyPrefixEquals
 */
case class S3RoutingRuleCondition private (HttpErrorCodeReturnedEquals: Option[S3HttpErrorCode],
                                           KeyPrefixEquals:             Option[S3KeyPrefix])
object S3RoutingRuleCondition extends DefaultJsonProtocol {
  def havingHttpErrorCode(httpErrorCode: S3HttpErrorCode, keyPrefix: Option[S3KeyPrefix] = None): S3RoutingRuleCondition =
    S3RoutingRuleCondition(Some(httpErrorCode), keyPrefix)
  def havingKeyPrefix(keyPrefix: S3KeyPrefix, httpErrorCode: Option[S3HttpErrorCode] = None): S3RoutingRuleCondition =
    S3RoutingRuleCondition(httpErrorCode, Some(keyPrefix))
  implicit val format: JsonFormat[S3RoutingRuleCondition] = jsonFormat2(S3RoutingRuleCondition.apply)
}
case class S3HttpErrorCode(code: String)
object S3HttpErrorCode extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3HttpErrorCode] = jsonFormat1(S3HttpErrorCode.apply)
}
case class S3KeyPrefix(prefix: String)
object S3KeyPrefix extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3KeyPrefix] = jsonFormat1(S3KeyPrefix.apply)
}
