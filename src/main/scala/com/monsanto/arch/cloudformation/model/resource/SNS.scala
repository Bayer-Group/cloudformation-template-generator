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
  DisplayName: Option[Token[String]],
  Subscription: Option[Seq[Token[Subscription]]],
  TopicName: Option[Token[String]],
  override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::SNS::Topic`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::SNS::Topic`(name, DisplayName, Subscription, TopicName, newCondition)

  override def arn = ResourceRef(this)
}
object `AWS::SNS::Topic` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SNS::Topic`] = jsonFormat5(`AWS::SNS::Topic`.apply)
}

case class `AWS::SNS::TopicPolicy`(
  name: String,
  PolicyDocument: PolicyDocument,
  Topics: Seq[ResourceRef[`AWS::SNS::Topic`]],
  override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::SNS::TopicPolicy`] {
  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::SNS::TopicPolicy`(name, PolicyDocument, Topics, newCondition)
}
object `AWS::SNS::TopicPolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SNS::TopicPolicy`] = jsonFormat4(`AWS::SNS::TopicPolicy`.apply)
}

trait Subscribable {
  def asSubscription : Token[Subscription]
}