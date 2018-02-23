package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import com.monsanto.arch.cloudformation.model.{ConditionRef, Token, `Fn::GetAtt`}
import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
  * Created by Tyler Southwick on 11/18/15.
  */
case class `AWS::SQS::Queue`(name: String,
                             QueueName: Token[String],
                             DelaySeconds: Token[Int],
                             MessageRetentionPeriod: Token[Int],
                             ReceiveMessageWaitTimeSeconds: Token[Int],
                             VisibilityTimeout: Token[Int],
                             RedrivePolicy: Option[RedrivePolicy] = None,
                             override val DependsOn: Option[Seq[String]] = None,
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
  implicit val format: JsonFormat[`AWS::SQS::Queue`] = jsonFormat9(`AWS::SQS::Queue`.apply)
}

case class `AWS::SQS::QueuePolicy`(name: String,
                                   PolicyDocument: PolicyDocument,
                                   Queues: TokenSeq[String],
                                   override val DependsOn: Option[Seq[String]] = None,
                                   override val Condition: Option[ConditionRef] = None
                                  ) extends Resource[`AWS::SQS::QueuePolicy`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::SQS::QueuePolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SQS::QueuePolicy`] = jsonFormat5(`AWS::SQS::QueuePolicy`.apply)
}


case class RedrivePolicy(
                          deadLetterTargetArn: Token[String],
                          maxReceiveCount: Token[Int]
                        )

object RedrivePolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RedrivePolicy] = jsonFormat2(RedrivePolicy.apply)
}
