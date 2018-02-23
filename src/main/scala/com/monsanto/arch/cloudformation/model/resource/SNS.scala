package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ResourceRef, Token, ConditionRef}
import spray.json.{JsonFormat, DefaultJsonProtocol}

/**
 * Created by Kyle Jones on 7/26/15.
 */
case class Subscription(Endpoint: Token[String], Protocol: Token[String])
object Subscription extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Subscription] = jsonFormat2(Subscription.apply)
}

case class `AWS::SNS::Topic`(
  name: String,
  DisplayName: Option[Token[String]] = None,
  Subscription: Option[Seq[Token[Subscription]]] = None,
  TopicName: Option[Token[String]] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::SNS::Topic`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::SNS::Topic`(name, DisplayName, Subscription, TopicName, DependsOn, newCondition)

  override def arn = ResourceRef(this)
}
object `AWS::SNS::Topic` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SNS::Topic`] = jsonFormat6(`AWS::SNS::Topic`.apply)
}

case class `AWS::SNS::TopicPolicy`(
  name: String,
  PolicyDocument: PolicyDocument,
  Topics: Seq[ResourceRef[`AWS::SNS::Topic`]],
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::SNS::TopicPolicy`] {
  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::SNS::TopicPolicy`(name, PolicyDocument, Topics, DependsOn, newCondition)
}
object `AWS::SNS::TopicPolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SNS::TopicPolicy`] = jsonFormat5(`AWS::SNS::TopicPolicy`.apply)
}

trait Subscribable {
  def asSubscription : Token[Subscription]
}

case class `AWS::SNS::Subscription`(
  name : String,
  Endpoint : Option[Token[String]] = None,
  Protocol : Token[String],
  TopicArn : Token[String],
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition : Option[ConditionRef] = None)
  extends Resource[`AWS::SNS::Subscription`] {

  override def when(newCondition: Option[ConditionRef]): `AWS::SNS::Subscription` = copy(Condition = newCondition)
}

object `AWS::SNS::Subscription` extends DefaultJsonProtocol {
  implicit val format : JsonFormat[`AWS::SNS::Subscription`] = jsonFormat6(`AWS::SNS::Subscription`.apply)
}
