package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

// Dimensions { "Name" : "InstanceId", "Value" : { "Ref" : "WebServerInstance" }} ],
// MetricName "StatusCheckFailed_System"
case class `AWS::CloudWatch::Alarm`(
  name: String,
  ComparisonOperator:      `AWS::CloudWatch::Alarm::ComparisonOperator`,
  EvaluationPeriods:       String, //BackedInt,
  MetricName:              String,
  Namespace:               `AWS::CloudWatch::Alarm::Namespace`,
  Period:                  String, //BackedInt,
  Statistic:               `AWS::CloudWatch::Alarm::Statistic`,
  Threshold:               String, //BackedInt,
  ActionsEnabled:          Option[Boolean] = None,
  AlarmActions:            Option[Seq[Token[String]]] = None,
  AlarmDescription:        Option[String] = None,
  AlarmName:               Option[Token[String]] = None,
  Dimensions:              Option[Seq[`AWS::CloudWatch::Alarm::Dimension`]] = None,
  InsufficientDataActions: Option[Seq[Token[String]]] = None,
  OKActions:               Option[Seq[Token[String]]] = None,
  Unit:                    Option[`AWS::CloudWatch::Alarm::Unit`] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::CloudWatch::Alarm`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::CloudWatch::Alarm` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::CloudWatch::Alarm`] = jsonFormat17(`AWS::CloudWatch::Alarm`.apply)
}

sealed abstract class `AWS::CloudWatch::Alarm::ComparisonOperator`(val operator: String)
object `AWS::CloudWatch::Alarm::ComparisonOperator` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::CloudWatch::Alarm::ComparisonOperator`] = new JsonFormat[`AWS::CloudWatch::Alarm::ComparisonOperator`] {
    def write(obj: `AWS::CloudWatch::Alarm::ComparisonOperator`) = JsString(obj.operator)
    //TODO
    def read(json: JsValue) = ???
  }
}
case object GreaterThanOrEqualToThreshold extends `AWS::CloudWatch::Alarm::ComparisonOperator`("GreaterThanOrEqualToThreshold")
case object GreaterThanThreshold          extends `AWS::CloudWatch::Alarm::ComparisonOperator`("GreaterThanThreshold")
case object LessThanOrEqualToThreshold    extends `AWS::CloudWatch::Alarm::ComparisonOperator`("LessThanOrEqualToThreshold")
case object LessThanThreshold             extends `AWS::CloudWatch::Alarm::ComparisonOperator`("LessThanThreshold")

case class `AWS::CloudWatch::Alarm::Dimension`(Name: String, Value: Token[ResourceRef[_]])
object `AWS::CloudWatch::Alarm::Dimension` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::CloudWatch::Alarm::Dimension`] = jsonFormat2(`AWS::CloudWatch::Alarm::Dimension`.apply)
  def from[A <: Resource[A]](name: String, value: Token[ResourceRef[A]]): `AWS::CloudWatch::Alarm::Dimension` = `AWS::CloudWatch::Alarm::Dimension`(name, value.asInstanceOf[Token[ResourceRef[_]]])
}

sealed abstract class `AWS::CloudWatch::Alarm::Namespace`(val name: String)
object `AWS::CloudWatch::Alarm::Namespace` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::CloudWatch::Alarm::Namespace`] = new JsonFormat[`AWS::CloudWatch::Alarm::Namespace`] {
    def write(obj: `AWS::CloudWatch::Alarm::Namespace`) = JsString(obj.name)
    //TODO
    def read(json: JsValue) = ???
  }
}
case object `AWS/AutoScaling`      extends `AWS::CloudWatch::Alarm::Namespace`("AWS/AutoScaling")
case object `AWS/Billing`          extends `AWS::CloudWatch::Alarm::Namespace`("AWS/Billing")
case object `AWS/CloudFront`       extends `AWS::CloudWatch::Alarm::Namespace`("AWS/CloudFront")
case object `AWS/DynamoDB`         extends `AWS::CloudWatch::Alarm::Namespace`("AWS/DynamoDB")
case object `AWS/ElastiCache`      extends `AWS::CloudWatch::Alarm::Namespace`("AWS/ElastiCache")
case object `AWS/EBS`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/EBS")
case object `AWS/EC2`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/EC2")
case object `AWS/ELB`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/ELB")
case object `AWS/ElasticMapReduce` extends `AWS::CloudWatch::Alarm::Namespace`("AWS/ElasticMapReduce")
case object `AWS/Kinesis`          extends `AWS::CloudWatch::Alarm::Namespace`("AWS/Kinesis")
case object `AWS/OpsWorks`         extends `AWS::CloudWatch::Alarm::Namespace`("AWS/OpsWorks")
case object `AWS/Redshift`         extends `AWS::CloudWatch::Alarm::Namespace`("AWS/Redshift")
case object `AWS/RDS`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/RDS")
case object `AWS/Route53`          extends `AWS::CloudWatch::Alarm::Namespace`("AWS/Route53")
case object `AWS/SNS`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/SNS")
case object `AWS/SQS`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/SQS")
case object `AWS/SWF`              extends `AWS::CloudWatch::Alarm::Namespace`("AWS/SWF")
case object `AWS/StorageGateway`   extends `AWS::CloudWatch::Alarm::Namespace`("AWS/StorageGateway")

sealed abstract class `AWS::CloudWatch::Alarm::Statistic`(val name: String)
object `AWS::CloudWatch::Alarm::Statistic` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::CloudWatch::Alarm::Statistic`] = new JsonFormat[`AWS::CloudWatch::Alarm::Statistic`] {
    def write(obj: `AWS::CloudWatch::Alarm::Statistic`) = JsString(obj.name)
    //TODO
    def read(json: JsValue) = ???
  }
}
case object SampleCount extends `AWS::CloudWatch::Alarm::Statistic`("SampleCount")
case object Average     extends `AWS::CloudWatch::Alarm::Statistic`("Average")
case object Sum         extends `AWS::CloudWatch::Alarm::Statistic`("Sum")
case object Minimum     extends `AWS::CloudWatch::Alarm::Statistic`("Minimum")
case object Maximum     extends `AWS::CloudWatch::Alarm::Statistic`("Maximum")

sealed abstract class `AWS::CloudWatch::Alarm::Unit`(val name: String)
object `AWS::CloudWatch::Alarm::Unit` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::CloudWatch::Alarm::Unit`] = new JsonFormat[`AWS::CloudWatch::Alarm::Unit`] {
    def write(obj: `AWS::CloudWatch::Alarm::Unit`) = JsString(obj.name)
    //TODO
    def read(json: JsValue) = ???
  }
}
case object Seconds            extends `AWS::CloudWatch::Alarm::Unit`("Seconds")
case object Microseconds       extends `AWS::CloudWatch::Alarm::Unit`("Microseconds")
case object Milliseconds       extends `AWS::CloudWatch::Alarm::Unit`("Milliseconds")
case object Bytes              extends `AWS::CloudWatch::Alarm::Unit`("Bytes")
case object Kilobytes          extends `AWS::CloudWatch::Alarm::Unit`("Kilobytes")
case object Megabytes          extends `AWS::CloudWatch::Alarm::Unit`("Megabytes")
case object Gigabytes          extends `AWS::CloudWatch::Alarm::Unit`("Gigabytes")
case object Terabytes          extends `AWS::CloudWatch::Alarm::Unit`("Terabytes")
case object Bits               extends `AWS::CloudWatch::Alarm::Unit`("Bits")
case object Kilobits           extends `AWS::CloudWatch::Alarm::Unit`("Kilobits")
case object Megabits           extends `AWS::CloudWatch::Alarm::Unit`("Megabits")
case object Gigabits           extends `AWS::CloudWatch::Alarm::Unit`("Gigabits")
case object Terabits           extends `AWS::CloudWatch::Alarm::Unit`("Terabits")
case object Percent            extends `AWS::CloudWatch::Alarm::Unit`("Percent")
case object Count              extends `AWS::CloudWatch::Alarm::Unit`("Count")
case object `Bytes/Second`     extends `AWS::CloudWatch::Alarm::Unit`("Bytes/Second")
case object `Kilobytes/Second` extends `AWS::CloudWatch::Alarm::Unit`("Kilobytes/Second")
case object `Megabytes/Second` extends `AWS::CloudWatch::Alarm::Unit`("Megabytes/Second")
case object `Gigabytes/Second` extends `AWS::CloudWatch::Alarm::Unit`("Gigabytes/Second")
case object `Terabytes/Second` extends `AWS::CloudWatch::Alarm::Unit`("Terabytes/Second")
case object `Bits/Second`      extends `AWS::CloudWatch::Alarm::Unit`("Bits/Second")
case object `Kilobits/Second`  extends `AWS::CloudWatch::Alarm::Unit`("Kilobits/Second")
case object `Megabits/Second`  extends `AWS::CloudWatch::Alarm::Unit`("Megabits/Second")
case object `Gigabits/Second`  extends `AWS::CloudWatch::Alarm::Unit`("Gigabits/Second")
case object `Terabits/Second`  extends `AWS::CloudWatch::Alarm::Unit`("Terabits/Second")
case object `Count/Second`     extends `AWS::CloudWatch::Alarm::Unit`("Count/Second")
case object UnitNone           extends `AWS::CloudWatch::Alarm::Unit`("UnitNone")