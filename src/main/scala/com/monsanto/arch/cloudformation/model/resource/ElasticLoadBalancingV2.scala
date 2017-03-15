package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
  * The AWS::ElasticLoadBalancingV2::LoadBalancer resource creates an Elastic Load Balancing Application load balancer
  * that distributes incoming application traffic across multiple targets (such as EC2 instances) in multiple
  * Availability Zones. For more information, see the
  * [Application Load Balancers Guide](http://docs.aws.amazon.com/elasticloadbalancing/latest/application/).
  *
  * @param name CloudFormation logical name
  * @param Subnets Specifies a list of at least two IDs of the subnets to associate with the load balancer. The subnets
  *                must be in different Availability Zones.
  * @param LoadBalancerAttributes Specifies the load balancer configuration.
  * @param Name Specifies a name for the load balancer. This name must be unique within your AWS account and can have a
  *             maximum of 32 alphanumeric characters and hyphens. A name can't begin or end with a hyphen.
  * @param Scheme Specifies whether the load balancer is internal or Internet-facing. An internal load balancer routes
  *               requests to targets using private IP addresses. An Internet-facing load balancer routes requests from
  *               clients over the Internet to targets in your public subnets.
  * @param SecurityGroups Specifies a list of the IDs of the security groups to assign to the load balancer.
  * @param Tags Specifies an arbitrary set of tags (key–value pairs) to associate with this load balancer. Use tags to
  *             manage your resources.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::ElasticLoadBalancingV2::LoadBalancer`(
  name:                   String,
  Subnets:                Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
  LoadBalancerAttributes: Option[Seq[LoadBalancerAttribute]] = None,
  Name:                   Option[Token[String]] = None,
  Scheme:                 Option[ELBScheme] = None,
  SecurityGroups:         Option[Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]]] = None,
  Tags:                   Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::ElasticLoadBalancingV2::LoadBalancer`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::ElasticLoadBalancingV2::LoadBalancer` = copy(Condition = newCondition)
  def arn: Token[String] = ResourceRef(this)

  def dnsName: Token[String] = `Fn::GetAtt`(Seq(name, "DNSName"))
  def canonicalHostedZoneID: Token[String] = `Fn::GetAtt`(Seq(name, "CanonicalHostedZoneID"))
  def loadBalancerFullName: Token[String] = `Fn::GetAtt`(Seq(name, "LoadBalancerFullName"))
  def loadBalancerName: Token[String] = `Fn::GetAtt`(Seq(name, "LoadBalancerName"))
  def securityGroups: Token[String] = `Fn::GetAtt`(Seq(name, "SecurityGroups"))
}

object `AWS::ElasticLoadBalancingV2::LoadBalancer` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::LoadBalancer`] = jsonFormat9(`AWS::ElasticLoadBalancingV2::LoadBalancer`.apply)
}


/**
  * The AWS::ElasticLoadBalancingV2::Listener resource creates a listener for an Elastic Load Balancing Application load
  * balancer. The listener checks for connection requests and forwards them to one or more target groups. For more
  * information, see the [Listeners for Your Application Load Balancers](http://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-listeners.html)
  * in the Application Load Balancers Guide.
  *
  * @param name CloudFormation logical name
  * @param DefaultActions The default actions that the listener takes when handling incoming requests.
  * @param LoadBalancerArn The Amazon Resource Name (ARN) of the load balancer to associate with the listener.
  * @param Port The port on which the listener listens for requests.
  * @param Protocol The protocol that clients must use to send requests to the listener.
  * @param Certificates The SSL server certificate for the listener. With a certificate, you can encrypt traffic between
  *                     the load balancer and the clients that initiate HTTPS sessions, and traffic between the load
  *                     balancer and your targets.
  * @param SslPolicy The security policy that defines the ciphers and protocols that the load balancer supports.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::ElasticLoadBalancingV2::Listener`(
  name:                   String,
  DefaultActions:         Seq[ListenerAction],
  LoadBalancerArn:        Token[String],
  Port:                   Token[Int],
  Protocol:               ALBProtocol,
  Certificates:           Option[Seq[Certificate]] = None,
  SslPolicy:              Option[ELBSecurityPolicy] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::ElasticLoadBalancingV2::Listener`] with HasArn {

  if (Protocol == ALBProtocol.HTTPS && (!Certificates.exists(_.nonEmpty) || SslPolicy.isEmpty))
    throw new IllegalArgumentException("Certificates and SslPolicy are both required for an HTTPS listener")

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::ElasticLoadBalancingV2::Listener` = copy(Condition = newCondition)
  def arn: Token[String] = ResourceRef(this)
}

object `AWS::ElasticLoadBalancingV2::Listener` extends DefaultJsonProtocol {
  def forHttp(name:            String,
              DefaultActions:  Seq[ListenerAction],
              LoadBalancerArn: Token[String],
              Port:            Token[Int] = 80,
              Condition:       Option[ConditionRef] = None): `AWS::ElasticLoadBalancingV2::Listener` =
    `AWS::ElasticLoadBalancingV2::Listener`(
      name = name,
      Protocol = ALBProtocol.HTTP,
      DefaultActions = DefaultActions,
      LoadBalancerArn = LoadBalancerArn,
      Port = Port,
      Condition = Condition
    )

  def forHttps(name:            String,
               DefaultActions:  Seq[ListenerAction],
               LoadBalancerArn: Token[String],
               Certificates:    Seq[Certificate],
               Port:            Token[Int] = 443,
               SslPolicy:       ELBSecurityPolicy = ELBSecurityPolicy.`ELBSecurityPolicy-2016-08`,
               Condition:       Option[ConditionRef] = None): `AWS::ElasticLoadBalancingV2::Listener` =
    `AWS::ElasticLoadBalancingV2::Listener`(
      name = name,
      Protocol = ALBProtocol.HTTPS,
      Certificates = Some(Certificates),
      SslPolicy = Some(SslPolicy),
      DefaultActions = DefaultActions,
      LoadBalancerArn = LoadBalancerArn,
      Port = Port,
      Condition = Condition
    )

  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::Listener`] = jsonFormat8(`AWS::ElasticLoadBalancingV2::Listener`.apply)
}


/**
  * The AWS::ElasticLoadBalancingV2::ListenerRule resource defines which requests an Elastic Load Balancing listener
  * takes action on and the action that it takes. For more information, see the [Listeners for Your Application Load
  * Balancers](http://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-listeners.html) in the
  * Application Load Balancers Guide.
  *
  * @param name CloudFormation logical name
  * @param Actions The action that the listener takes when a request meets the specified condition.
  * @param Conditions The conditions under which a rule takes effect.
  * @param ListenerArn The Amazon Resource Name (ARN) of the listener that the rule applies to.
  * @param Priority The priority for the rule. Elastic Load Balancing evaluates rules in priority order, from the lowest
  *                 value to the highest value. If a request satisfies a rule, Elastic Load Balancing ignores all
  *                 subsequent rules.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::ElasticLoadBalancingV2::ListenerRule`(
  name:                   String,
  Actions:                Seq[ListenerAction],
  Conditions:             Seq[RuleCondition],
  ListenerArn:            Token[String],
  Priority:               Token[Int],
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::ElasticLoadBalancingV2::ListenerRule`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::ElasticLoadBalancingV2::ListenerRule` = copy(Condition = newCondition)
  def arn: Token[String] = ResourceRef(this)
}

object `AWS::ElasticLoadBalancingV2::ListenerRule` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::ListenerRule`] = jsonFormat6(`AWS::ElasticLoadBalancingV2::ListenerRule`.apply)
}


/**
  * The AWS::ElasticLoadBalancingV2::TargetGroup resource creates an Elastic Load Balancing target group that routes
  * requests to one or more registered targets, such as EC2 instances. For more information, see the [Target Groups
  * for Your Application Load Balancers](http://docs.aws.amazon.com/elasticloadbalancing/latest/application/load-balancer-target-groups.html)
  * in the Application Load Balancers Guide.
  *
  * @param name CloudFormation logical name
  * @param Protocol The protocol to use for routing traffic to the targets.
  * @param Port The port on which the targets receive traffic. This port is used unless you specify a port override when
  *             registering the target.
  * @param VpcId The identifier of the virtual private cloud (VPC).
  * @param HealthCheckIntervalSeconds The approximate number of seconds between health checks for an individual target.
  *                                   The default is 30 seconds.
  * @param HealthCheckPath The ping path destination where Elastic Load Balancing sends health check requests. The
  *                        default is /.
  * @param HealthCheckPort The port that the load balancer uses when performing health checks on the targets.  The
  *                        default is `traffic-port`, which indicates the port on which each target receives traffic
  *                        from the load balancer.
  * @param HealthCheckProtocol The protocol the load balancer uses when performing health checks on targets. The default
  *                            is the HTTP protocol.
  * @param HealthCheckTimeoutSeconds The amount of time, in seconds, during which no response from a target means a
  *                                  failed health check. The default is 5 seconds.
  * @param HealthyThresholdCount The number of consecutive health checks successes required before considering an
  *                              unhealthy target healthy. The default is 5.
  * @param UnhealthyThresholdCount The number of consecutive health check failures required before considering a target
  *                                unhealthy. The default is 2.
  * @param Matcher The HTTP codes to use when checking for a successful response from a target. The default is 200.
  * @param Name The name of the target group. This name must be unique per region per account, can have a maximum of 32
  *             characters, must contain only alphanumeric characters or hyphens, and must not begin or end with a
  *             hyphen.
  * @param Tags An arbitrary set of tags (key–value pairs) for the target group. Use tags to help manage resources.
  * @param TargetGroupAttributes Target group configurations.
  * @param Targets The targets to add to this target group.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::ElasticLoadBalancingV2::TargetGroup`(
  name:                       String,
  Protocol:                   ALBProtocol,
  Port:                       Token[Int],
  VpcId:                      Token[ResourceRef[`AWS::EC2::VPC`]],
  Matcher:                    Option[Matcher],
  HealthCheckIntervalSeconds: Option[Token[Int]] = None,
  HealthCheckPath:            Option[Token[String]] = None,
  HealthCheckPort:            Option[Token[String]] = None,
  HealthCheckProtocol:        Option[ALBProtocol] = None,
  HealthCheckTimeoutSeconds:  Option[Token[Int]] = None,
  HealthyThresholdCount:      Option[Token[Int]] = None,
  UnhealthyThresholdCount:    Option[Token[Int]] = None,
  Name:                       Option[Token[String]] = None,
  TargetGroupAttributes:      Option[Seq[TargetGroupAttribute]] = None,
  Targets:                    Option[Seq[TargetDescription]] = None,
  Tags:                       Option[Seq[AmazonTag]] = None,
  override val Condition:     Option[ConditionRef] = None,
  override val DependsOn:     Option[Seq[String]] = None
) extends Resource[`AWS::ElasticLoadBalancingV2::TargetGroup`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::ElasticLoadBalancingV2::TargetGroup` = copy(Condition = newCondition)
  def arn: Token[String] = ResourceRef(this)

  def loadBalancerArns: Token[String] = `Fn::GetAtt`(Seq(name, "LoadBalancerArns"))
  def targetGroupFullName: Token[String] = `Fn::GetAtt`(Seq(name, "TargetGroupFullName"))
}

object `AWS::ElasticLoadBalancingV2::TargetGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElasticLoadBalancingV2::TargetGroup`] = jsonFormat18(`AWS::ElasticLoadBalancingV2::TargetGroup`.apply)
}


sealed trait ALBProtocol
object ALBProtocol extends DefaultJsonProtocol {
  case object HTTP  extends ALBProtocol
  case object HTTPS extends ALBProtocol
  val values = Seq(HTTPS, HTTPS)
  implicit val format: JsonFormat[ALBProtocol] = new EnumFormat[ALBProtocol](values)
}


/**
  * @see http://docs.aws.amazon.com/elasticloadbalancing/latest/application/create-https-listener.html
  */
sealed trait ELBSecurityPolicy
object ELBSecurityPolicy extends DefaultJsonProtocol {
  case object `ELBSecurityPolicy-2016-08`         extends ELBSecurityPolicy
  case object `ELBSecurityPolicy-TLS-1-2-2017-01` extends ELBSecurityPolicy
  case object `ELBSecurityPolicy-TLS-1-1-2017-01` extends ELBSecurityPolicy
  case object `ELBSecurityPolicy-2015-05`         extends ELBSecurityPolicy
  val values = Seq(`ELBSecurityPolicy-2016-08`, `ELBSecurityPolicy-TLS-1-2-2017-01`, `ELBSecurityPolicy-TLS-1-1-2017-01`, `ELBSecurityPolicy-2015-05`)
  implicit val format: JsonFormat[ELBSecurityPolicy] = new EnumFormat[ELBSecurityPolicy](values)
}


/**
  * @param HttpCode The HTTP codes that a healthy target must use when responding to a health check, such as 200,202 or
  *                 200-399.
  */
case class Matcher(HttpCode: Token[String])

object Matcher extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Matcher] = jsonFormat1(Matcher.apply)
}


sealed trait TargetGroupStickinessType
object TargetGroupStickinessType extends DefaultJsonProtocol {
  case object lb_cookie extends TargetGroupStickinessType

  val values = Seq(lb_cookie)

  implicit val format: JsonFormat[TargetGroupStickinessType] = new EnumFormat[TargetGroupStickinessType](values)
}


case class TargetGroupAttribute private (Key: Option[Token[String]], Value: Option[Token[String]])

object TargetGroupAttribute extends DefaultJsonProtocol {
  /**
    * @param seconds The amount of time for Elastic Load Balancing to wait before changing the state of a deregistering
    *                target from draining to unused. The range is 0-3600 seconds. The default value is 300 seconds.
    * @return
    */
  def `deregistration_delay.timeout_seconds`(seconds: Token[String]): TargetGroupAttribute = TargetGroupAttribute(Some("deregistration_delay.timeout_seconds"), seconds)

  /**
    * @param enabled Indicates whether sticky sessions are enabled.
    * @return
    */
  def `stickiness.enabled`(enabled: Token[String]): TargetGroupAttribute = TargetGroupAttribute(Some("stickiness.enabled"), enabled)

  /**
    * @param seconds The cookie expiration period, in seconds. After this period, the cookie is considered stale. The
    *                minimum value is 1 second and the maximum value is 7 days (604800 seconds). The default value is
    *                1 day (86400 seconds).
    * @return
    */
  def `stickiness.lb_cookie.duration_seconds`(seconds: Token[String]): TargetGroupAttribute = TargetGroupAttribute(Some("stickiness.lb_cookie.duration_seconds"), seconds)

  /**
    * @param stickyType The type of stickiness.
    * @return
    */
  def `stickiness.type`(stickyType: TargetGroupStickinessType): TargetGroupAttribute = TargetGroupAttribute(Some("stickiness.type"), Some(stickyType.toString))

  implicit val format: JsonFormat[TargetGroupAttribute] = jsonFormat2(TargetGroupAttribute.apply)
}


/**
  * @param Id The ID of the target, such as an EC2 instance ID.
  * @param Port The port number on which the target is listening for traffic.
  */
case class TargetDescription(Id: Token[String], Port: Option[Token[Int]] = None)

object TargetDescription extends DefaultJsonProtocol {
  implicit val format: JsonFormat[TargetDescription] = jsonFormat2(TargetDescription.apply)
}


/**
  * @param Key The name of an attribute that you want to configure.
  * @param Value A value for the attribute.
  */
case class LoadBalancerAttribute private (Key: Option[Token[String]] = None, Value: Option[Token[String]] = None)

object LoadBalancerAttribute extends DefaultJsonProtocol {
  /**
    * @param enabled Indicates whether access logs stored in Amazon S3 are enabled. The value is true or false.
    * @return
    */
  def `access_logs.s3.enabled`(enabled: Token[String]): LoadBalancerAttribute = LoadBalancerAttribute(Some("access_logs.s3.enabled"), enabled)

  /**
    * @param bucket The name of the S3 bucket for the access logs. This attribute is required if access logs in Amazon
    *               S3 are enabled. The bucket must exist in the same region as the load balancer and have a bucket
    *               policy that grants Elastic Load Balancing permission to write to the bucket.
    * @return
    */
  def `access_logs.s3.bucket`(bucket: Token[String]): LoadBalancerAttribute = LoadBalancerAttribute(Some("access_logs.s3.bucket"), bucket)

  /**
    *
    * @param prefix The prefix for the location in the S3 bucket. If you don't specify a prefix, the access logs are
    *               stored in the root of the bucket.
    * @return
    */
  def `access_logs.s3.prefix`(prefix: Token[String]): LoadBalancerAttribute = LoadBalancerAttribute(Some("access_logs.s3.prefix"), prefix)

  /**
    *
    * @param enabled Indicates whether deletion protection is enabled.
    * @return
    */
  def `deletion_protection.enabled`(enabled: Token[String]): LoadBalancerAttribute = LoadBalancerAttribute(Some("deletion_protection.enabled"), enabled)

  /**
    *
    * @param seconds The idle timeout value, in seconds.
    * @return
    */
  def `idle_timeout.timeout_seconds`(seconds: Token[String]): LoadBalancerAttribute = LoadBalancerAttribute(Some("idle_timeout.timeout_seconds"), seconds)

  implicit val format: JsonFormat[LoadBalancerAttribute] = jsonFormat2(LoadBalancerAttribute.apply)
}


/**
  * @param CertificateArn The Amazon Resource Name (ARN) of the certificate to associate with the listener.
  */
case class Certificate(CertificateArn: Token[String])

object Certificate extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Certificate] = jsonFormat1(Certificate.apply)
}


/**
  * @param TargetGroupArn The Amazon Resource Name (ARN) of the target group to which Elastic Load Balancing routes the
  *                       traffic.
  * @param Type The type of action.
  */
case class ListenerAction private (TargetGroupArn: Token[String], Type: Token[String])

object ListenerAction extends DefaultJsonProtocol {
  /**
    * @param TargetGroupArn The Amazon Resource Name (ARN) of the target group to which Elastic Load Balancing forwards
    *                       the traffic.
    * @return
    */
  def forward(TargetGroupArn: Token[String]): ListenerAction = ListenerAction(TargetGroupArn, "forward")

  implicit val format: JsonFormat[ListenerAction] = jsonFormat2(ListenerAction.apply)
}

/**
  * @param Field The name of the condition that you want to define, such as path-pattern (which forwards requests based
  *              on the URL of the request).
  * @param Values The value for the field that you specified in the Field property.
  */
case class RuleCondition private (Field: Option[Token[String]] = None, Values: Option[Seq[String]] = None)

object RuleCondition extends DefaultJsonProtocol {
  /**
    * @param Values The value for the path-pattern.
    * @return
    */
  def `path-pattern`(Values: Seq[String]): RuleCondition = RuleCondition(Some("path-pattern"), Some(Values))

  implicit val format: JsonFormat[RuleCondition] = jsonFormat2(RuleCondition.apply)
}
