package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{FunctionCallToken, `Fn::GetAtt`, Token, ConditionRef}
import spray.json.{JsonFormat, DefaultJsonProtocol}

/**
  * Created by Tyler Southwick on 11/18/15.
  */
case class `AWS::SQS::Queue`(name: String,
                             QueueName: Token[String],
                             DelaySeconds: Token[Int],
                             MessageRetentionPeriod: Token[Int],
                             ReceiveMessageWaitTimeSeconds: Token[Int],
                             VisibilityTimeout: Token[Int],
                             override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::SQS::Queue`] with HasArn with Subscribable {

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

  override def asSubscription = Subscription(
    Endpoint = arn,
    Protocol = "sqs"
  )

}

object `AWS::SQS::Queue` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SQS::Queue`] = jsonFormat7(`AWS::SQS::Queue`.apply)
}

case class `AWS::SQS::QueuePolicy`(name: String,
                                   PolicyDocument: PolicyDocument,
                                   Queues: Seq[Token[String]],
                                   override val Condition: Option[ConditionRef] = None
                                  ) extends Resource[`AWS::SQS::QueuePolicy`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::SQS::QueuePolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SQS::QueuePolicy`] = jsonFormat4(`AWS::SQS::QueuePolicy`.apply)
}
