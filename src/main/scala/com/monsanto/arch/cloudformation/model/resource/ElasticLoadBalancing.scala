package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import spray.json._

/**
 * The AvailabilityZones and Subnets properties are mutually exclusive, with the latter
 * being used for ELBs within VPCs.  Therefore, the default constructor is private and
 * methods on the companion object are used to safely construct the resource.  See
 * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-elb.html
 */

case class `AWS::ElasticLoadBalancing::LoadBalancer` private (
  name:                      String,
  Listeners:                 Seq[ELBListener],
  AccessLoggingPolicy:       Option[ELBAccessLoggingPolicy],
  AppCookieStickinessPolicy: Option[ELBAppCookieStickinessPolicy],
  AvailabilityZones:         TokenSeq[String],
  ConnectionDrainingPolicy:  Option[ELBConnectionDrainingPolicy],
  ConnectionSettings:        Option[ELBConnectionSettings],
  CrossZone:                 Option[Boolean],
  HealthCheck:               Option[ELBHealthCheck],
  Instances:                 Seq[Token[ResourceRef[`AWS::EC2::Instance`]]],
  LBCookieStickinessPolicy:  Option[ELBLBCookieStickinessPolicy],
  LoadBalancerName:          Option[Token[String]],
  Policies:                  Seq[ELBPolicy],
  Scheme:                    Option[ELBScheme],
  SecurityGroups:            Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
  Subnets:                   Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
  Tags:                      Seq[AmazonTag],
  override val Condition:    Option[ConditionRef] = None,
  override val DependsOn:    Option[Seq[String]]  = None
) extends Resource[`AWS::ElasticLoadBalancing::LoadBalancer`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::ElasticLoadBalancing::LoadBalancer` extends DefaultJsonProtocol {
  /**
   * Create a ELB within a VPC.  Therefore this does not accept the AvailabilityZones parameter
   * and requires the Subnets parameter.
   *
   * @param name
   * @param Listeners
   * @param Subnets
   * @param AccessLoggingPolicy
   * @param AppCookieStickinessPolicy
   * @param ConnectionDrainingPolicy
   * @param ConnectionSettings
   * @param CrossZone
   * @param HealthCheck
   * @param Instances
   * @param LBCookieStickinessPolicy
   * @param LoadBalancerName
   * @param Policies
   * @param Scheme
   * @param SecurityGroups
   * @param Tags
   * @param Condition
   * @return
   */
  def inVpc(
    name:                      String,
    Listeners:                 Seq[ELBListener],
    Subnets:                   Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
    AccessLoggingPolicy:       Option[ELBAccessLoggingPolicy]                     = None,
    AppCookieStickinessPolicy: Option[ELBAppCookieStickinessPolicy]               = None,
    ConnectionDrainingPolicy:  Option[ELBConnectionDrainingPolicy]                = None,
    ConnectionSettings:        Option[ELBConnectionSettings]                      = None,
    CrossZone:                 Option[Boolean]                                    = None,
    HealthCheck:               Option[ELBHealthCheck]                             = None,
    Instances:                 Seq[Token[ResourceRef[`AWS::EC2::Instance`]]]      = Seq.empty,
    LBCookieStickinessPolicy:  Option[ELBLBCookieStickinessPolicy]                = None,
    LoadBalancerName:          Option[Token[String]]                              = None,
    Policies:                  Seq[ELBPolicy]                                     = Seq.empty,
    Scheme:                    Option[ELBScheme]                                  = None,
    SecurityGroups:            Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]] = Seq.empty,
    Tags:                      Seq[AmazonTag]                                     = Seq.empty,
    Condition:                 Option[ConditionRef]                               = None,
    DependsOn:                 Option[Seq[String]]                                = None
  ) = `AWS::ElasticLoadBalancing::LoadBalancer`(
    name = name,
    Listeners = Listeners,
    AccessLoggingPolicy = AccessLoggingPolicy,
    AppCookieStickinessPolicy = AppCookieStickinessPolicy,
    AvailabilityZones = Seq.empty,
    ConnectionDrainingPolicy = ConnectionDrainingPolicy,
    ConnectionSettings = ConnectionSettings,
    CrossZone = CrossZone,
    HealthCheck = HealthCheck,
    Instances = Instances,
    LBCookieStickinessPolicy = LBCookieStickinessPolicy,
    LoadBalancerName = LoadBalancerName,
    Policies = Policies,
    Scheme = Scheme,
    SecurityGroups = SecurityGroups,
    Subnets = Subnets,
    Tags = Tags,
    Condition = Condition,
    DependsOn = DependsOn
  )

  /**
   * Create an ELB for EC2 Classic instances. It does not take the Subnets or Scheme parameters, subnets are
   * only available in VPCs and the scheme for non-VPC ELBs must be internet-facing.  The AvailabilityZones
   * parameter is optional.
   *
   * @param name
   * @param Listeners
   * @param AccessLoggingPolicy
   * @param AppCookieStickinessPolicy
   * @param AvailabilityZones
   * @param ConnectionDrainingPolicy
   * @param ConnectionSettings
   * @param CrossZone
   * @param HealthCheck
   * @param Instances
   * @param LBCookieStickinessPolicy
   * @param LoadBalancerName
   * @param Policies
   * @param SecurityGroups
   * @param Tags
   * @param Condition
   * @return
   */
  def noVpc(
    name:                      String,
    Listeners:                 Seq[ELBListener],
    AccessLoggingPolicy:       Option[ELBAccessLoggingPolicy]                     = None,
    AppCookieStickinessPolicy: Option[ELBAppCookieStickinessPolicy]               = None,
    AvailabilityZones:         TokenSeq[String]                                   = Seq.empty,
    ConnectionDrainingPolicy:  Option[ELBConnectionDrainingPolicy]                = None,
    ConnectionSettings:        Option[ELBConnectionSettings]                      = None,
    CrossZone:                 Option[Boolean]                                    = None,
    HealthCheck:               Option[ELBHealthCheck]                             = None,
    Instances:                 Seq[Token[ResourceRef[`AWS::EC2::Instance`]]]      = Seq.empty,
    LBCookieStickinessPolicy:  Option[ELBLBCookieStickinessPolicy]                = None,
    LoadBalancerName:          Option[Token[String]]                              = None,
    Policies:                  Seq[ELBPolicy]                                     = Seq.empty,
    SecurityGroups:            Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]] = Seq.empty,
    Tags:                      Seq[AmazonTag]                                     = Seq.empty,
    Condition:                 Option[ConditionRef]                               = None,
    DependsOn:                 Option[Seq[String]]                                = None
  ) = `AWS::ElasticLoadBalancing::LoadBalancer`(
    name = name,
    Listeners = Listeners,
    AccessLoggingPolicy = AccessLoggingPolicy,
    AppCookieStickinessPolicy = AppCookieStickinessPolicy,
    AvailabilityZones = AvailabilityZones,
    ConnectionDrainingPolicy = ConnectionDrainingPolicy,
    ConnectionSettings = ConnectionSettings,
    CrossZone = CrossZone,
    HealthCheck = HealthCheck,
    Instances = Instances,
    LBCookieStickinessPolicy = LBCookieStickinessPolicy,
    LoadBalancerName = LoadBalancerName,
    Policies = Policies,
    Scheme = Some(ELBScheme.`internet-facing`),
    SecurityGroups = SecurityGroups,
    Subnets = Seq.empty,
    Tags = Tags,
    Condition = Condition,
    DependsOn = DependsOn
  )

  implicit val format: JsonFormat[`AWS::ElasticLoadBalancing::LoadBalancer`] =
    jsonFormat19(`AWS::ElasticLoadBalancing::LoadBalancer`.apply)
}

case class ELBAccessLoggingPolicy(
  Enabled:        Boolean,
  S3BucketName:   Token[ResourceRef[`AWS::S3::Bucket`]],
  EmitInterval:   Option[ELBLoggingEmitInterval] = None,
  S3BucketPrefix: Option[Token[String]] = None
)
object ELBAccessLoggingPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBAccessLoggingPolicy] = jsonFormat4(ELBAccessLoggingPolicy.apply)
}

sealed trait ELBLoggingEmitInterval
object ELBLoggingEmitInterval extends DefaultJsonProtocol {
  case object `5`  extends ELBLoggingEmitInterval
  case object `60` extends ELBLoggingEmitInterval
  val values = Seq(`5`, `60`)
  implicit val format: JsonFormat[ELBLoggingEmitInterval] =
    new EnumFormat[ELBLoggingEmitInterval](values)
}

case class ELBAppCookieStickinessPolicy(
  CookieName: Token[String],
  PolicyName: Token[String]
)
object ELBAppCookieStickinessPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBAppCookieStickinessPolicy] = jsonFormat2(ELBAppCookieStickinessPolicy.apply)
}

case class ELBConnectionDrainingPolicy(
  Enabled: Boolean,
  Timeout: Option[Int] = None
)
object ELBConnectionDrainingPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBConnectionDrainingPolicy] = jsonFormat2(ELBConnectionDrainingPolicy.apply)
}

case class ELBConnectionSettings(
  IdleTimeout: Int
)
object ELBConnectionSettings extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBConnectionSettings] = jsonFormat1(ELBConnectionSettings.apply)
}

case class ELBLBCookieStickinessPolicy(
  PolicyName:             Token[String],
  CookieExpirationPeriod: Option[String] = None
)
object ELBLBCookieStickinessPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBLBCookieStickinessPolicy] = jsonFormat2(ELBLBCookieStickinessPolicy.apply)
}

case class ELBListener(
  InstancePort:     String,
  LoadBalancerPort: String,
  Protocol:         ELBListenerProtocol,
  InstanceProtocol: Option[ELBListenerProtocol] = Some(ELBListenerProtocol.HTTP),
  PolicyNames:      TokenSeq[String] = Seq.empty,
  SSLCertificateId: Option[Token[String]] = None
)
object ELBListener extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBListener] = jsonFormat6(ELBListener.apply)
}

sealed trait ELBListenerProtocol
object ELBListenerProtocol extends DefaultJsonProtocol {
  case object HTTP  extends ELBListenerProtocol
  case object HTTPS extends ELBListenerProtocol
  case object SSL   extends ELBListenerProtocol
  case object TCP   extends ELBListenerProtocol
  val values = Seq(HTTPS, HTTPS, SSL, TCP)
  implicit val format: JsonFormat[ELBListenerProtocol] = new EnumFormat[ELBListenerProtocol](values)
}

case class ELBHealthCheck(
  Target:             String,
  HealthyThreshold:   String,
  UnhealthyThreshold: String,
  Interval:           String,
  Timeout:            String
)
object ELBHealthCheck extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBHealthCheck] = jsonFormat5(ELBHealthCheck.apply)
}

case class ELBPolicy(
  PolicyName:        Token[String],
  PolicyType:        String,
  Attributes:        Seq[NameValuePair],
  InstancePorts:     Seq[String],
  LoadBalancerPorts: Seq[String]
)
object ELBPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBPolicy] = jsonFormat5(ELBPolicy.apply)
}

case class NameValuePair(Name: String, Value: String)
object NameValuePair extends DefaultJsonProtocol {
  implicit  val format: JsonFormat[NameValuePair] = jsonFormat2(NameValuePair.apply)
}

sealed trait ELBScheme
object ELBScheme extends DefaultJsonProtocol {
  case object internal          extends ELBScheme
  case object `internet-facing` extends ELBScheme
  val values = Seq(internal, `internet-facing`)
  implicit val format: JsonFormat[ELBScheme] = new EnumFormat[ELBScheme](values)
}
