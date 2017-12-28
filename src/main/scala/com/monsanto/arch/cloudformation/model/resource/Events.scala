package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

/**
  * The `AWS::Events::Rule` resource creates a rule that matches incoming Amazon CloudWatch Events (CloudWatch Events)
  * events and routes them to one or more targets for processing. For more information, see
  * [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/WhatIsCloudWatchEvents.html Using CloudWatch Events]]
  * in the Amazon CloudWatch User Guide.
  *
  * @param name CloudFormation logical name
  * @param Name A name for the rule. If you don't specify a name, AWS CloudFormation generates a unique physical ID and
  *             uses that ID for the rule name. IMPORTANT: If you specify a name, you cannot perform updates that require
  *             replacement of this resource. You can perform updates that require no or some interruption. If you must
  *             replace the resource, specify a new name.
  * @param Description A description of the rule's purpose.
  * @param EventPattern Describes which events CloudWatch Events routes to the specified target. These routed events
  *                     are matched events. For more information, see
  *                     [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/DeveloperGuide/CloudWatchEventsandEventPatterns.html Events and Event Patterns]]
  *                     in the Amazon CloudWatch User Guide.
  * @param ScheduleExpression The schedule or rate (frequency) that determines when CloudWatch Events runs the rule.
  *                           For more information, see
  *                           [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html Schedule Expression Syntax for Rules]]
  *                           in the Amazon CloudWatch User Guide.
  * @param State Indicates whether the rule is enabled. For valid values, see the State parameter for the PutRule action
  *              in the Amazon CloudWatch Events API Reference.
  * @param Targets The resources, such as Lambda functions or Kinesis streams, that CloudWatch Events routes events to
  *                and invokes when the rule is triggered. For information about valid targets, see the PutTargets
  *                action in the Amazon CloudWatch Events API Reference.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Events::Rule`(name: String,
                               Name: Option[Token[String]] = None,
                               Description: Option[String] = None,
                               EventPattern: Option[JsValue] = None,
                               ScheduleExpression: Option[ScheduleExpression] = None,
                               State: Option[RuleState] = None,
                               Targets: Option[Seq[RuleTarget]] = None,
                               override val DependsOn: Option[Seq[String]] = None,
                               override val Condition: Option[ConditionRef] = None
                              ) extends Resource[`AWS::Events::Rule`] with HasArn {

  require(EventPattern.isDefined || ScheduleExpression.isDefined, "AWS::Events::Rule must have either EventPattern and/or ScheduledExpression specified")

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Events::Rule` = copy(Condition = newCondition)

  def arn: Token[String] = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::Events::Rule` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Events::Rule`] = jsonFormat9(`AWS::Events::Rule`.apply)
}


/**
  * The Target property type specifies a target, such as AWS Lambda (Lambda) functions or Kinesis streams, that
  * CloudWatch Events invokes when a rule is triggered.
  *
  * @param Arn The Amazon Resource Name (ARN) of the target.
  * @param Id A unique, user-defined identifier for the target. Acceptable values include alphanumeric characters,
  *           periods (.), hyphens (-), and underscores (_).
  * @param EcsParameters The Amazon ECS task definition and task count to use, if the event target is an Amazon ECS task.
  * @param Input A JSON-formatted text string that is passed to the target. This value overrides the matched event.
  * @param InputPath When you don't want to pass the entire matched event, the JSONPath that describes which part of the
  *                  event to pass to the target.
  * @param InputTransformer Settings that provide custom input to a target based on certain event data. You can extract
  *                         one or more key-value pairs from the event, and then use that data to send customized input
  *                         to the target.
  * @param KinesisParameters Settings that control shard assignment, when the target is a Kinesis stream. If you don't
  *                          include this parameter, eventId is used as the partition key.
  * @param RoleArn The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) role to use for this
  *                target when the rule is triggered. If one rule triggers multiple targets, you can use a different IAM
  *                role for each target. NOTE: CloudWatch Events needs appropriate permissions to make API calls against
  *                the resources you own. For Kinesis streams, CloudWatch Events relies on IAM roles. For Lambda,
  *                Amazon SNS, and Amazon SQS resources, CloudWatch Events relies on resource-based policies.
  * @param RunCommandParameters Parameters used when the rule invokes Amazon EC2 Systems Manager Run Command.
  */
case class RuleTarget(Arn: Token[String],
                      Id: String,
                      EcsParameters: Option[RuleEcsParameters] = None,
                      Input: Option[Token[String]] = None,
                      InputPath: Option[Token[String]] = None,
                      InputTransformer: Option[RuleInputTransformer] = None,
                      KinesisParameters: Option[RuleKinesisParameters] = None,
                      RoleArn: Option[Token[String]] = None,
                      RunCommandParameters: Option[RuleRunCommandParameters] = None)

object RuleTarget extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleTarget] = jsonFormat9(RuleTarget.apply)
}


/**
  * The EcsParameters property type specifies information about an Amazon Elastic Container Service (Amazon ECS) task
  * target.
  *
  * @param TaskCount The number of tasks to create based on the task definition. The default is 1.
  * @param TaskDefinitionArn The Amazon Resource Name (ARN) of the task definition to use.
  */
case class RuleEcsParameters(TaskDefinitionArn: Token[String],
                             TaskCount: Option[Token[Int]] = None)

object RuleEcsParameters extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleEcsParameters] = jsonFormat2(RuleEcsParameters.apply)
}


/**
  * The InputTransformer property type specifies settings that provide custom input to an Amazon CloudWatch Events rule
  * target based on certain event data.
  *
  * @param InputPathsMap The map of JSON paths to extract from the event, as key-value pairs where each value is a JSON
  *                      path. You must use JSON dot notation, not bracket notation. Duplicates aren't allowed.
  * @param InputTemplate The input template where you can use the values of the keys from InputPathsMap to customize the
  *                      data that's sent to the target.
  */
case class RuleInputTransformer(InputTemplate: Token[String],
                                InputPathsMap: Option[Map[Token[String], Token[String]]] = None)

object RuleInputTransformer extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleInputTransformer] = jsonFormat2(RuleInputTransformer.apply)
}


/**
  * The KinesisParameters property type specifies settings that control shard assignment for a Kinesis stream target.
  *
  * @param PartitionKeyPath The JSON path to extract from the event and use as the partition key. The default is to use
  *                         the eventId as the partition key. For more information, see Amazon Kinesis Streams Key
  *                         Concepts in the Kinesis Streams Developer Guide.
  */
case class RuleKinesisParameters(PartitionKeyPath: Token[String])

object RuleKinesisParameters extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleKinesisParameters] = jsonFormat1(RuleKinesisParameters.apply)
}


/**
  * The RunCommandParameters property type specifies parameters used when an Amazon CloudWatch Events rule invokes
  * Amazon EC2 Systems Manager Run Command.
  *
  * @param RunCommandTargets The criteria (either InstanceIds or a tag) that specifies which EC2 instances that the
  *                          command is sent to.
  */
case class RuleRunCommandParameters(RunCommandTargets: Seq[Target])

object RuleRunCommandParameters extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RuleRunCommandParameters] = jsonFormat1(RuleRunCommandParameters.apply)
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
