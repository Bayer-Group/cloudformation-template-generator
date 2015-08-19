package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
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
  AvailabilityZones:         Option[Token[String]],
  ConnectionDrainingPolicy:  Option[ELBConnectionDrainingPolicy],
  ConnectionSettings:        Option[ELBConnectionSettings],
  CrossZone:                 Option[Boolean],
  HealthCheck:               Option[ELBHealthCheck],
  Instances:                 Option[Seq[Token[ResourceRef[`AWS::EC2::Instance`]]]],
  LBCookieStickinessPolicy:  Option[ELBLBCookieStickinessPolicy],
  LoadBalancerName:          Option[Token[String]],
  Policies:                  Option[Seq[ELBPolicy]],
  Scheme:                    Option[ELBScheme],
  SecurityGroups:            Option[Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]]],
  Subnets:                   Option[Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]]],
  Tags:                      Option[Seq[AmazonTag]],
  override val Condition:    Option[ConditionRef] = None
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
    AccessLoggingPolicy:       Option[ELBAccessLoggingPolicy]                             = None,
    AppCookieStickinessPolicy: Option[ELBAppCookieStickinessPolicy]                       = None,
    ConnectionDrainingPolicy:  Option[ELBConnectionDrainingPolicy]                        = None,
    ConnectionSettings:        Option[ELBConnectionSettings]                              = None,
    CrossZone:                 Option[Boolean]                                            = None,
    HealthCheck:               Option[ELBHealthCheck]                                        = None,
    Instances:                 Option[Seq[Token[ResourceRef[`AWS::EC2::Instance`]]]]      = None,
    LBCookieStickinessPolicy:  Option[ELBLBCookieStickinessPolicy]                        = None,
    LoadBalancerName:          Option[Token[String]]                                      = None,
    Policies:                  Option[Seq[ELBPolicy]]                            = None,
    Scheme:                    Option[ELBScheme]                                          = None,
    SecurityGroups:            Option[Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]]] = None,
    Tags:                      Option[Seq[AmazonTag]]                                     = None,
    Condition:                 Option[ConditionRef]                                       = None
  ) = `AWS::ElasticLoadBalancing::LoadBalancer`(
    name = name,
    Listeners = Listeners,
    AccessLoggingPolicy = AccessLoggingPolicy,
    AppCookieStickinessPolicy = AppCookieStickinessPolicy,
    AvailabilityZones = None,
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
    Subnets = Some(Subnets),
    Tags = Tags,
    Condition = Condition
  )

  /**
   * Create an ELB for EC2 Classic instances. It does not take the Subnets parameter, subnets are
   * only available in VPCs.  The AvailabilityZones parameter is optional.
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
   * @param Scheme
   * @param SecurityGroups
   * @param Tags
   * @param Condition
   * @return
   */
  def noVpc(
    name:                      String,
    Listeners:                 Seq[ELBListener],
    AccessLoggingPolicy:       Option[ELBAccessLoggingPolicy]                             = None,
    AppCookieStickinessPolicy: Option[ELBAppCookieStickinessPolicy]                       = None,
    AvailabilityZones:         Option[Token[String]]                                      = None,
    ConnectionDrainingPolicy:  Option[ELBConnectionDrainingPolicy]                        = None,
    ConnectionSettings:        Option[ELBConnectionSettings]                              = None,
    CrossZone:                 Option[Boolean]                                            = None,
    HealthCheck:               Option[ELBHealthCheck]                                        = None,
    Instances:                 Option[Seq[Token[ResourceRef[`AWS::EC2::Instance`]]]]      = None,
    LBCookieStickinessPolicy:  Option[ELBLBCookieStickinessPolicy]                        = None,
    LoadBalancerName:          Option[Token[String]]                                      = None,
    Policies:                  Option[Seq[ELBPolicy]]                            = None,
    Scheme:                    Option[ELBScheme]                                          = None,
    SecurityGroups:            Option[Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]]] = None,
    Tags:                      Option[Seq[AmazonTag]]                                     = None,
    Condition:                 Option[ConditionRef]                                       = None
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
    Scheme = Scheme,
    SecurityGroups = SecurityGroups,
    Subnets = None,
    Tags = Tags,
    Condition = Condition
  )

  implicit val format: JsonFormat[`AWS::ElasticLoadBalancing::LoadBalancer`] = jsonFormat18(`AWS::ElasticLoadBalancing::LoadBalancer`.apply)
}

case class ELBAccessLoggingPolicy(
  Enabled:        Boolean,
  S3BucketName:   Token[ResourceRef[`AWS::S3::Bucket`]],
  EmitInterval:   Option[Int] = None,
  S3BucketPrefix: Option[String] = None
)
object ELBAccessLoggingPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBAccessLoggingPolicy] = jsonFormat4(ELBAccessLoggingPolicy.apply)
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
  LoadBalancerPort: String,
  Protocol:         String,
  InstancePort:     String,
  SSLCertificateId: Option[Token[String]],
  InstanceProtocol: String = "HTTP",
  PolicyNames:      Option[Seq[String]] = None
)
object ELBListener extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ELBListener] = jsonFormat6(ELBListener.apply)
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
  PolicyName:        String,
  PolicyType:        String,
  Attributes:        Seq[NameValuePair],
  InstancePorts:     Option[Seq[String]],
  LoadBalancerPorts: Option[Seq[String]]
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
  case object Internal extends ELBScheme
  case object InternetFacing extends ELBScheme

  implicit val format: JsonFormat[ELBScheme] = new JsonFormat[ELBScheme] {
    override def write(obj: ELBScheme)= JsString(obj.toString)
    override def read(json: JsValue): ELBScheme = {
      json.toString match {
        case "internal"  => Internal
        case "internet-facing" => InternetFacing
      }
    }
  }
}
