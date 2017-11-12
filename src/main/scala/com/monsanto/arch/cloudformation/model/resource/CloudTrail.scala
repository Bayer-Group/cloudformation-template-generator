package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token}
import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat

case class `AWS::CloudTrail::Trail`(
                                   name : String,
  CloudWatchLogsLogGroupArn : Option[Token[String]] = None,
  CloudWatchLogsRoleArn : Option[Token[String]] = None,
  EnableLogFileValidation : Option[Boolean] = None,
  IncludeGlobalServiceEvents : Option[Boolean] = None,
  IsLogging : Boolean,
  IsMultiRegionTrail : Option[Boolean] = None,
  KMSKeyId : Option[Token[String]] = None,
  S3BucketName : Token[String],
  S3KeyPrefix : Token[String],
  SnsTopicName : Option[Token[String]] = None,
  Tags : Seq[ResourceTag],
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::CloudTrail::Trail`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::CloudTrail::Trail` = copy(Condition = newCondition)
}

object `AWS::CloudTrail::Trail` {
  implicit val format : JsonFormat[`AWS::CloudTrail::Trail`] = jsonFormat14(`AWS::CloudTrail::Trail`.apply)
}
