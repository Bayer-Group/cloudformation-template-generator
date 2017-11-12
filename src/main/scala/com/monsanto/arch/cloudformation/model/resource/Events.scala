package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

case class `AWS::Events::Rule`(name: String,
                               Name: Option[Token[String]] = None,
                               Description: Option[String] = None,
                               EventPattern: Option[JsValue] = None,
                               ScheduleExpression: Option[Token[String]] = None,
                               State: Option[RuleState] = None,
                               Targets: Option[Seq[RuleTarget]] = None,
                               override val DependsOn: Option[Seq[String]] = None,
                               override val Condition: Option[ConditionRef] = None
                              ) extends Resource[`AWS::Events::Rule`] {
  
  requiure(EventPattern.isDefined || ScheduleExpression.isDefined, "AWS::Events::Rule must have either EventPattern and/or ScheduledExpression specified")
  
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::Events::Rule` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Events::Rule`] = jsonFormat9(`AWS::Events::Rule`.apply)

  @deprecated("This method is broken.  Please update.")
  def apply(name: String,
            ScheduleExpression: Token[String],
            Targets: Seq[Option[RuleTarget]],
            Condition: Option[ConditionRef]): `AWS::Events::Rule` =
  `AWS::Events::Rule`(
    name = name,
    ScheduleExpression = ScheduleExpression,
    Targets = Some(Targets.flatten),
    Condition = Condition
  )

  @deprecated("This method is broken.  Please update.")
  def apply(name: String,
            ScheduleExpression: Token[String],
            Targets: Seq[Option[RuleTarget]]): `AWS::Events::Rule` =
    `AWS::Events::Rule`(
      name = name,
      ScheduleExpression = ScheduleExpression,
      Targets = Some(Targets.flatten)
    )
}

case class RuleTarget(Id: String, Arn: Token[String])

object RuleTarget extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleTarget] = jsonFormat2(RuleTarget.apply)
}

sealed trait RuleState extends Product with Serializable

object RuleState {
  private type T = RuleState
  case object ENABLED extends T
  case object DISABLED extends T
  implicit val format : JsonFormat[T] = new JsonFormat[T] {
    override def write(obj: T): JsValue = JsString(obj.toString)
    override def read(json: JsValue): T = json.toString match {
      case "ENABLED" => ENABLED
      case "DISABLED" => DISABLED
    }
  }
}
