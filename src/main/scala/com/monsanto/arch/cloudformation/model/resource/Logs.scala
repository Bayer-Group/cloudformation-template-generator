package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._

import spray.json._

/**
  * The AWS::Logs::Destination resource creates an Amazon CloudWatch Logs (CloudWatch Logs) destination, which enables
  * you to specify a physical resource (such as an Amazon Kinesis stream) that subscribes to CloudWatch Logs log events
  * from another AWS account.
  *
  * @param name CloudFormation logical name
  * @param DestinationName The name of the CloudWatch Logs destination.
  * @param DestinationPolicy An AWS Identity and Access Management (IAM) policy that specifies who can write to your
  *                          destination.
  * @param RoleArn The Amazon Resource Name (ARN) of an IAM role that permits CloudWatch Logs to send data to the
  *                specified AWS resource (TargetArn).
  * @param TargetArn The ARN of the AWS resource that receives log events. Currently, you can specify only an Amazon
  *                  Kinesis stream.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::Logs::Destination`(
  name:                   String,
  DestinationName:        Token[String],
  DestinationPolicy:      Token[String],
  RoleArn:                Token[String],
  TargetArn:              Token[String],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]]  = None
) extends Resource[`AWS::Logs::Destination`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Logs::Destination` = copy(Condition = newCondition)
  override def arn: Token[String] = ResourceRef(this)
}

object `AWS::Logs::Destination` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Logs::Destination`] = jsonFormat7(`AWS::Logs::Destination`.apply)
}


/**
  * The AWS::Logs::LogGroup resource creates an Amazon CloudWatch Logs log group that defines common properties for log
  * streams, such as their retention and access control rules. Each log stream must belong to one log group.
  *
  * @param name CloudFormation logical name
  * @param LogGroupName A name for the log group. If you don't specify a name, AWS CloudFormation generates a unique
  *                     physical ID and uses that ID for the table name.
  * @param RetentionInDays The number of days log events are kept in CloudWatch Logs. When a log event expires,
  *                        CloudWatch Logs automatically deletes it.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::Logs::LogGroup`(
  name:                   String,
  LogGroupName:           Option[Token[String]],
  RetentionInDays:        Option[Token[Int]] = None,
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]]  = None
) extends Resource[`AWS::Logs::LogGroup`] with HasArn {

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Logs::LogGroup` = copy(Condition = newCondition)

  override def arn: Token[String] = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::Logs::LogGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Logs::LogGroup`] = jsonFormat5(`AWS::Logs::LogGroup`.apply)
  @deprecated("RetentionInDays should be optional", "3.7.2")
  def apply(name: String,
            LogGroupName: Option[Token[String]],
            RetentionInDays: Token[Int]): `AWS::Logs::LogGroup` =
    apply(name, LogGroupName, Some(RetentionInDays))
}


/**
  * The AWS::Logs::LogStream resource creates an Amazon CloudWatch Logs log stream in a log group. A log stream
  * represents the sequence of events coming from an application instance or resource that you are monitoring.
  *
  * @param name CloudFormation logical name
  * @param LogGroupName The name of the log group where the log stream is created.
  * @param LogStreamName The name of the log stream to create. The name must be unique within the log group.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::Logs::LogStream` private (
  name:                   String,
  LogGroupName:           ResourceRef[`AWS::Logs::LogGroup`],
  LogStreamName:          Option[Token[String]],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]]  = None
) extends Resource[`AWS::Logs::LogStream`] {

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Logs::LogStream` = copy(Condition = newCondition)
}

object `AWS::Logs::LogStream` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Logs::LogStream`] = jsonFormat5(`AWS::Logs::LogStream`.apply)
}


/**
  * The AWS::Logs::MetricFilter resource creates a metric filter that describes how Amazon CloudWatch Logs extracts
  * information from logs that you specify and transforms it into Amazon CloudWatch metrics. If you have multiple metric
  * filters that are associated with a log group, all the filters are applied to the log streams in that group.
  *
  * @param name CloudFormation logical name
  * @param FilterPattern Describes the pattern that CloudWatch Logs follows to interpret each entry in a log. For
  *                      example, a log entry might contain fields such as timestamps, IP addresses, error codes, bytes
  *                      transferred, and so on. You use the pattern to specify those fields and to specify what to look
  *                      for in the log file. For example, if you're interested in error codes that begin with 1234,
  *                      your filter pattern might be [timestamps, ip_addresses, error_codes = 1234*, size, ...].
  * @param LogGroupName The name of an existing log group that you want to associate with this metric filter.
  * @param MetricTransformations Describes how to transform data from a log into a CloudWatch metric.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::Logs::MetricFilter` private (
  name:                   String,
  FilterPattern:          Token[String],
  LogGroupName:           Token[String],
  MetricTransformations:  Seq[MetricTransformation],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]]  = None
) extends Resource[`AWS::Logs::MetricFilter`] {

  require(MetricTransformations.length == 1,
    "Currently, you can specify only one metric transformation for each metric filter. If you want to specify multiple" +
      "metric transformations, you must specify multiple metric filters.")

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Logs::MetricFilter` = copy(Condition = newCondition)
}

object `AWS::Logs::MetricFilter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Logs::MetricFilter`] = jsonFormat6(`AWS::Logs::MetricFilter`.apply)
}


/**
  * MetricTransformation is a property of the AWS::Logs::MetricFilter resource that describes how to transform log
  * streams into a CloudWatch metric.
  *
  * @param MetricName The name of the CloudWatch metric to which the log information will be published.
  * @param MetricNamespace The destination namespace of the CloudWatch metric. Namespaces are containers for metrics.
  *                        For example, you can add related metrics in the same namespace.
  * @param MetricValue The value that is published to the CloudWatch metric. For example, if you're counting the
  *                    occurrences of a particular term like Error, specify 1 for the metric value. If you're counting
  *                    the number of bytes transferred, reference the value that is in the log event by using $ followed
  *                    by the name of the field that you specified in the filter pattern, such as $size.
  */
case class MetricTransformation(MetricName: Token[String],
                                MetricNamespace: Token[String],
                                MetricValue: Token[String])

object MetricTransformation extends DefaultJsonProtocol {
  implicit val format: JsonFormat[MetricTransformation] = jsonFormat3(MetricTransformation.apply)
}


/**
  * The AWS::Logs::SubscriptionFilter resource creates an Amazon CloudWatch Logs (CloudWatch Logs) subscription filter
  * that defines which log events are delivered to your Amazon Kinesis stream or AWS Lambda (Lambda) function and where
  * to send them.
  *
  * @param name CloudFormation logical name
  * @param DestinationArn The Amazon Resource Name (ARN) of the Amazon Kinesis stream or Lambda function that you want
  *                       to use as the subscription feed destination.
  * @param FilterPattern The filtering expressions that restrict what gets delivered to the destination AWS resource.
  * @param LogGroupName The log group to associate with the subscription filter. All log events that are uploaded to
  *                     this log group are filtered and delivered to the specified AWS resource if the filter pattern
  *                     matches the log events.
  * @param RoleArn An IAM role that grants CloudWatch Logs permission to put data into the specified Amazon Kinesis
  *                stream. For Lambda and CloudWatch Logs destinations, don't specify this property because CloudWatch
  *                Logs gets the necessary permissions from the destination resource.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::Logs::SubscriptionFilter` private (
  name:                   String,
  DestinationArn:         Token[String],
  FilterPattern:          Token[String],
  LogGroupName:           Token[String],
  RoleArn:                Option[Token[String]] = None,
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]]  = None
) extends Resource[`AWS::Logs::SubscriptionFilter`] {

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Logs::SubscriptionFilter` = copy(Condition = newCondition)
}

object `AWS::Logs::SubscriptionFilter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Logs::SubscriptionFilter`] = jsonFormat7(`AWS::Logs::SubscriptionFilter`.apply)
}
