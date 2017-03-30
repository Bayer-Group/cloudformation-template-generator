package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.DefaultJsonProtocol._
import spray.json.JsonFormat

case class `AWS::ElasticLoadBalancingV2::LoadBalancer`(name: String,
                                                       Subnets: Token[Seq[ResourceRef[`AWS::EC2::Subnet`]]],
                                                       LoadBalancerAttributes: Option[Seq[LoadBalancerAttribute]] = None,
                                                       Name: Option[Token[String]] = None,
                                                       Scheme: Option[ELBScheme] = None,
                                                       SecurityGroups: Option[Seq[Token[String]]] = None,
                                                       Tags: Option[Seq[AmazonTag]] = None,
                                                       override val DependsOn : Option[Seq[String]] = None)
  extends Resource[`AWS::ElasticLoadBalancingV2::LoadBalancer`] with HasArn {
  override def when(newCondition: Option[ConditionRef]): `AWS::ElasticLoadBalancingV2::LoadBalancer` = ???

  override def arn: Token[String] = ???
}

object `AWS::ElasticLoadBalancingV2::LoadBalancer` {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::LoadBalancer`] = jsonFormat8(`AWS::ElasticLoadBalancingV2::LoadBalancer`.apply)
}

case class `AWS::ElasticLoadBalancingV2::Listener`(name: String,
                                                   DefaultActions: Seq[Action],
                                                   LoadBalancerArn: Token[String],
                                                   Port: Token[Int],
                                                   Protocol: Token[String],
                                                   Certificates: Option[Seq[Certificate]] = None,
                                                   SslPolicy: Option[Token[String]] = None)
  extends Resource[`AWS::ElasticLoadBalancingV2::Listener`] with HasArn {
  override def when(newCondition: Option[ConditionRef]): `AWS::ElasticLoadBalancingV2::Listener` = ???

  override def arn: Token[String] = ???
}

object `AWS::ElasticLoadBalancingV2::Listener` {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::Listener`] = jsonFormat7(`AWS::ElasticLoadBalancingV2::Listener`.apply)
}

case class `AWS::ElasticLoadBalancingV2::ListenerRule`(name: String,
                                                       Actions: Seq[Action],
                                                       Conditions: Seq[Condition],
                                                       ListenerArn: Token[String],
                                                       Priority: Token[Int])
  extends Resource[`AWS::ElasticLoadBalancingV2::ListenerRule`] with HasArn {
  override def when(newCondition: Option[ConditionRef]): `AWS::ElasticLoadBalancingV2::ListenerRule` = ???

  override def arn: Token[String] = ???
}

object `AWS::ElasticLoadBalancingV2::ListenerRule` {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::ListenerRule`] = jsonFormat5(`AWS::ElasticLoadBalancingV2::ListenerRule`.apply)
}

case class `AWS::ElasticLoadBalancingV2::TargetGroup`(name: String,
                                                      Matcher: Matcher,
                                                      Protocol: Token[String],
                                                      Port: Token[Int],
                                                      VpcId: Token[ResourceRef[`AWS::EC2::VPC`]],
                                                      HealthCheckIntervalSeconds: Option[Token[Int]] = None,
                                                      HealthCheckPath: Option[Token[String]] = None,
                                                      HealthCheckPort: Option[Token[String]] = None,
                                                      HealthCheckProtocol: Option[Token[String]] = None,
                                                      HealthCheckTimeoutSeconds: Option[Token[Int]] = None,
                                                      HealthyThresholdCount: Option[Token[Int]] = None,
                                                      Name: Option[Token[String]] = None,
                                                      Tags: Option[Seq[AmazonTag]] = None,
                                                      TargetGroupAttributes: Option[Seq[TargetGroupAttribute]] = None,
                                                      Targets: Option[Seq[TargetDescription]] = None,
                                                      UnhealthyThresholdCount: Option[Token[Int]] = None,
                                                      override val DependsOn: Option[Seq[String]] = None)
  extends Resource[`AWS::ElasticLoadBalancingV2::TargetGroup`] with HasArn {
  override def when(newCondition: Option[ConditionRef]): `AWS::ElasticLoadBalancingV2::TargetGroup` = ???

  override def arn: Token[String] = ???
}

object `AWS::ElasticLoadBalancingV2::TargetGroup` {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::TargetGroup`] = jsonFormat17(`AWS::ElasticLoadBalancingV2::TargetGroup`.apply)
}

case class Matcher(HttpCode: Option[Token[String]] = None)

object Matcher {
  implicit val format: JsonFormat[Matcher] = jsonFormat1(Matcher.apply)
}

case class TargetGroupAttribute(Key: Option[Token[String]], Value: Option[Token[String]])

object TargetGroupAttribute {
  implicit val format: JsonFormat[TargetGroupAttribute] = jsonFormat2(TargetGroupAttribute.apply)
}

case class TargetDescription(Id: Token[String], Port: Option[Token[Int]] = None)

object TargetDescription {
  implicit val format: JsonFormat[TargetDescription] = jsonFormat2(TargetDescription.apply)
}

case class LoadBalancerAttribute(Key: Option[Token[String]] = None, Value: Option[Token[String]] = None)

object LoadBalancerAttribute {
  implicit val format: JsonFormat[LoadBalancerAttribute] = jsonFormat2(LoadBalancerAttribute.apply)
}

case class Certificate(CertificateArn: Option[Token[String]] = None)

object Certificate {
  implicit val format: JsonFormat[Certificate] = jsonFormat1(Certificate.apply)
}

case class Action(TargetGroupArn: Token[String], Type: Token[String] = "forward")

object Action {
  implicit val format: JsonFormat[Action] = jsonFormat2(Action.apply)
}

case class Condition(Field: Option[Token[String]] = None, Values: Option[Seq[String]] = None)

object Condition {
  implicit val format: JsonFormat[Condition] = jsonFormat2(Condition.apply)
}
