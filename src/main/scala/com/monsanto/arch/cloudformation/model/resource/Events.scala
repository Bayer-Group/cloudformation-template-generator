package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.{DefaultJsonProtocol, JsonFormat}

case class `AWS::Events::Rule`(name: String,
                               ScheduleExpression: Token[String],
                               Targets: Seq[Option[RuleTarget]],
                               override val Condition: Option[ConditionRef] = None
                              ) extends Resource[`AWS::Events::Rule`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::Events::Rule` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Events::Rule`] = jsonFormat4(`AWS::Events::Rule`.apply)
}

case class RuleTarget(Id: String, Arn: Token[String])

object RuleTarget extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleTarget] = jsonFormat2(RuleTarget.apply)
}

