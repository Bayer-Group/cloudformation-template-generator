package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token, `Fn::GetAtt`}
import spray.json._

case class `AWS::Kinesis::Stream`(name: String, ShardCount: Token[Int], Tags: Option[Seq[AmazonTag]], override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::Kinesis::Stream`] with HasArn {
  override def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::Kinesis::Stream` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Kinesis::Stream`] = jsonFormat4(`AWS::Kinesis::Stream`.apply)
}
