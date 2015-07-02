package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

case class `AWS::ElasticLoadBalancing::LoadBalancer`(
  name:           String,
  CrossZone:      Boolean,
  SecurityGroups: Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
  Subnets:        Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
  Listeners:      Seq[Listener],
  HealthCheck:    HealthCheck,
  Policies:       Option[Seq[LoadBalancerPolicy]],
  Tags:           Seq[AmazonTag],
  Scheme:         Option[String] = None, // TODO: Make this an enum
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::ElasticLoadBalancing::LoadBalancer`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::ElasticLoadBalancing::LoadBalancer` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancing::LoadBalancer`] = jsonFormat10(`AWS::ElasticLoadBalancing::LoadBalancer`.apply)
}

case class Listener(LoadBalancerPort: String, Protocol: String, InstancePort: String, SSLCertificateId: Option[Token[String]], InstanceProtocol: String = "HTTP", PolicyNames: Option[Seq[String]] = None)
object Listener extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Listener] = jsonFormat6(Listener.apply)
}
case class HealthCheck(Target: String, HealthyThreshold: String, UnhealthyThreshold: String, Interval: String, Timeout: String)
object HealthCheck extends DefaultJsonProtocol {
  implicit val format: JsonFormat[HealthCheck] = jsonFormat5(HealthCheck.apply)
}

case class LoadBalancerPolicy(PolicyName: String, PolicyType: String, Attributes: Seq[NameValuePair], InstancePorts: Option[Seq[String]], LoadBalancerPorts: Option[Seq[String]])
object LoadBalancerPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[LoadBalancerPolicy] = jsonFormat5(LoadBalancerPolicy.apply)
}

case class NameValuePair(Name: String, Value: String)
object NameValuePair extends DefaultJsonProtocol {
  implicit  val format: JsonFormat[NameValuePair] = jsonFormat2(NameValuePair.apply)
}