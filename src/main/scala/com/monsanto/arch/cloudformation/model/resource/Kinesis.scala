package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token, `Fn::GetAtt`}
import spray.json._

case class `AWS::Kinesis::Stream`(
  name: String,
  Name: Option[String] = None,
  RetentionPeriodHours: Option[Token[Int]] = None,
  ShardCount: Token[Int],
  Tags: Option[Seq[AmazonTag]] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::Kinesis::Stream`] with HasArn {
  override def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::Kinesis::Stream` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Kinesis::Stream`] = jsonFormat7(`AWS::Kinesis::Stream`.apply)
}
