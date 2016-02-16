package com.monsanto.arch.cloudformation.model.simple

import java.util.UUID

import com.monsanto.arch.cloudformation.model.resource._
import com.monsanto.arch.cloudformation.model._

import scala.language.implicitConversions

/**
 * Created by Ryan Richt on 5/10/15
 */

object Builders extends
  Route with
  Instance with
  Subnet with
  Route53 with
  Autoscaling with
  SecurityGroup with
  VPC with
  AvailabilityZone with
  EC2 with
  Outputs with
  Gateway with
  Conditions with
  ElasticLoadBalancing

trait Conditions {
  def when[R <: Resource[R]](condition: ConditionRef)(rs: Seq[R]): Seq[R] = rs.map(r => r.when(Some(condition)))
}

trait Outputs {
  implicit class RichResource[R <: Resource[R]](r: R) {
    def andOutput(name: String, description: String) = Template.fromResource(r) ++ Template.fromOutput( Output(name, description, ResourceRef(r)) )
  }
}

trait Route {
  implicit class RichRouteTable(rt: `AWS::EC2::RouteTable`) {
    def withRouteT(
      visibility:        String,
      routeTableOrdinal: Int,
      routeOrdinal:      Int,
      connectionBobber:             ValidRouteComboOption,
      cidr:              CidrBlock = CidrBlock(0,0,0,0,0),
      dependsOn:         Option[Seq[String]] = None
    ) =
      `AWS::EC2::Route`(
        visibility + "RouteTable" + routeTableOrdinal + "Route" + routeOrdinal,
        RouteTableId           = ResourceRef(rt),
        DestinationCidrBlock   = cidr,
        connectionBobber:             ValidRouteComboOption,
        DependsOn              = dependsOn
      )


    def withRoute(
      visibility:        String,
      routeTableOrdinal: Int,
      routeOrdinal:      Int,
      connectionBobber:             ValidRouteComboOption,
      cidr:              CidrBlock = CidrBlock(0,0,0,0,0),
      dependsOn:         Option[Seq[String]] = None
    ) =
      `AWS::EC2::Route`(
        visibility + "RouteTable" + routeTableOrdinal + "Route" + routeOrdinal,
        RouteTableId           = ResourceRef(rt),
        DestinationCidrBlock   = cidr,
        connectionBobber       = connectionBobber,
        DependsOn              = dependsOn
      )
  }

  def withRouteTable(visibility: String, ordinal: Int)(implicit vpc: `AWS::EC2::VPC`) = {
    val name = visibility + "RouteTable" + ordinal
    `AWS::EC2::RouteTable`(
      name,
      VpcId = vpc,
      Tags = AmazonTag.fromName(name)
    )
  }
}

trait Instance {

  implicit class RichInstance(ec2: `AWS::EC2::Instance`) {

    @deprecated(message = "Use withEIPInVPC or withEIPInClassic", since = "v3.0.6")
    def withEIP(
      name:      String,
      domain:    String              = "vpc",
      dependsOn: Option[Seq[String]] = None
    ) : `AWS::EC2::EIP` = withEIPInVPC(name, dependsOn)

    def withEIPInVPC(
                 name:      String,
                 dependsOn: Option[Seq[String]] = None
               ) : `AWS::EC2::EIP` =
        `AWS::EC2::EIP`.vpc(
          name       = name,
          InstanceId = Some(ResourceRef(ec2)),
          DependsOn  = dependsOn
        )

    def withEIPInClassic(
                  name:      String,
                  dependsOn: Option[Seq[String]] = None
                ) : `AWS::EC2::EIP` =
        `AWS::EC2::EIP`.classic(
          name       = name,
          InstanceId = Some(ResourceRef(ec2)),
          DependsOn  = dependsOn
        )

    def alarmOnSystemFailure(name: String, description: String) =
      `AWS::CloudWatch::Alarm`(
        name,
        ActionsEnabled = Some(true),
        AlarmActions = Some(Seq(`Fn::Join`(":", Seq("arn:aws:automate", `AWS::Region`, "ec2:recover")))),
        AlarmDescription = Some(s"Auto recover $description"),
        AlarmName = Some(`Fn::Join`("-", Seq(s"$description-alarm", `AWS::StackName`))),
        ComparisonOperator = `AWS::CloudWatch::Alarm::ComparisonOperator`.GreaterThanThreshold,
        Dimensions = Some(Seq(`AWS::CloudWatch::Alarm::Dimension`.from("InstanceId", ec2))),
        EvaluationPeriods = "2",
        MetricName = "StatusCheckFailed_System",
        Namespace = `AWS::CloudWatch::Alarm::Namespace`.`AWS/EC2`,
        Period = "60",
        Statistic = `AWS::CloudWatch::Alarm::Statistic`.Minimum,
        Threshold = "0"
      )
  }
}

trait Subnet extends AvailabilityZone with Outputs {

  def withRouteTableAssoc(visibility: String, subnetOrdinal: Int, routeTable: Token[ResourceRef[`AWS::EC2::RouteTable`]])(implicit s: `AWS::EC2::Subnet`) =
    s.withRouteTableAssoc( visibility, subnetOrdinal, routeTable )

  def withNAT(
               ordinal: Int,
               vpcGatewayAttachmentResource: `AWS::EC2::VPCGatewayAttachment`,
               privateRouteTables: Seq[`AWS::EC2::RouteTable`],
               cfNATLambdaARN: Token[String])(implicit s: `AWS::EC2::Subnet`) =
  s.withNAT(ordinal, vpcGatewayAttachmentResource, privateRouteTables, cfNATLambdaARN)

  def withNAT(
               ordinal: Int,
               vpcGatewayAttachmentResource: `AWS::EC2::VPCGatewayAttachment`,
               privateRouteTable: `AWS::EC2::RouteTable`,
               cfNATLambdaARN: Token[String])(implicit s: `AWS::EC2::Subnet`) =
    s.withNAT(ordinal, vpcGatewayAttachmentResource, privateRouteTable, cfNATLambdaARN)

  implicit class RichSubnet(s: `AWS::EC2::Subnet`){
    def withRouteTableAssoc(visibility: String, subnetOrdinal: Int, routeTable: Token[ResourceRef[`AWS::EC2::RouteTable`]]) =
      `AWS::EC2::SubnetRouteTableAssociation`(
        visibility.take(3) + "Subnet" + subnetOrdinal + "RTAssoc",
        SubnetId = s,
        RouteTableId = routeTable
      )
    def withNAT(
                 ordinal: Int,
                 vpcGatewayAttachmentResource: `AWS::EC2::VPCGatewayAttachment`,
                 privateRouteTable: `AWS::EC2::RouteTable`,
                 cfNATLambdaARN: Token[String]): Template =
      withNAT(ordinal, vpcGatewayAttachmentResource, Seq(privateRouteTable), cfNATLambdaARN)

    def withNAT(
             ordinal: Int,
             vpcGatewayAttachmentResource: `AWS::EC2::VPCGatewayAttachment`,
             privateRouteTables: Seq[`AWS::EC2::RouteTable`],
             cfNATLambdaARN: Token[String]): Template = {
      val natEIP = `AWS::EC2::EIP`.vpc(
        name = s"NAT${ordinal}EIP",
        InstanceId = None,
        DependsOn = Some(Seq(vpcGatewayAttachmentResource.name))
      )

      val natWaitHandle = `AWS::CloudFormation::WaitConditionHandle`(s"NAT${ordinal}WaitHandle")

      val nat = `Custom::NatGateway`(
        name=s"NAT${ordinal}",
        ServiceToken = cfNATLambdaARN,
        AllocationId = `Fn::GetAtt`(Seq(natEIP.name, "AllocationId")),
        SubnetId = ResourceRef(s),
        WaitHandle = ResourceRef(natWaitHandle)
      )

      val natWaitCondition = `AWS::CloudFormation::WaitCondition`(
        s"NAT${ordinal}WaitCondition",
        Handle = ResourceRef(natWaitHandle),
        Timeout = 240,
        Count = None,
        DependsOn = Some(Seq(nat.name))
      )
      val privateRoutes = privateRouteTables.map{
        privateRouteTable => `Custom::NatGatewayRoute`(
          name = s"NAT${ordinal}Route",
          ServiceToken = cfNATLambdaARN,
          RouteTableId = ResourceRef(privateRouteTable),
          NatGatewayId = ResourceRef(nat),
          DestinationCidrBlock = CidrBlock(0,0,0,0,0),
          DependsOn = Some(Seq(natWaitCondition.name))
        )
      }

      Template.fromResource(nat) ++ natEIP.andOutput(s"NAT${ordinal}EIP", s"NAT ${ordinal} EIP") ++
        natWaitCondition ++ natWaitHandle ++ privateRoutes
    }
  }

  private def ucFirst(s: String): String = (s.head.toUpper +: s.tail.toCharArray).mkString

  def subnet(visibility: String, ordinal: Int, vpc: Token[ResourceRef[`AWS::EC2::VPC`]], az: Token[String],
    cidr: Token[CidrBlock], tagger: (String, String) => Seq[AmazonTag]) =
    `AWS::EC2::Subnet`(
      ucFirst(visibility.take(3)) + "Subnet" + ordinal,
      VpcId = vpc,
      AvailabilityZone = Some(az),
      CidrBlock = cidr,
      Tags = tagger(visibility.take(3).toLowerCase + "subnet" + ordinal, visibility)
    )

  def subnet(visibility: String, ordinal: Int, vpc: Token[ResourceRef[`AWS::EC2::VPC`]], az: Option[Token[String]] =  None,
             cidr: Token[CidrBlock], tagger: (String, String) => Seq[AmazonTag]) =
    `AWS::EC2::Subnet`(
      ucFirst(visibility.take(3)) + "Subnet" + ordinal,
      VpcId = vpc,
      AvailabilityZone = az,
      CidrBlock = cidr,
      Tags = tagger(visibility.take(3).toLowerCase + "subnet" + ordinal, visibility)
    )

  def withSubnet(name: String, cidr: Token[CidrBlock])
    (f: (`AWS::EC2::Subnet`) => Template)(implicit vpc: `AWS::EC2::VPC`, az: AZ): Template = {

    val sub = `AWS::EC2::Subnet`(
      name,
      VpcId = vpc,
      AvailabilityZone = az.zone,
      CidrBlock = cidr,
      Tags = AmazonTag.fromName(name)
    )

    f(sub) ++ sub.andOutput(name, name)
  }
}

trait Route53 {
  def anyAliasRecord(
    name:               String,
    subdomainNameParam: ParameterRef[String],
    baseDomainName:     ParameterRef[String],
    sslTargetName:      String,
    sslCondition:       Option[ConditionRef]
  ) = {
    `AWS::Route53::RecordSet`.aliasRecord(
      name,
      `Fn::Join`("", Seq(subdomainNameParam, ".", baseDomainName, ".")),
      `Fn::Join`("", Seq(baseDomainName, ".")),
      Route53AliasTarget(
        `Fn::GetAtt`(Seq(sslTargetName, "DNSName")),
        `Fn::GetAtt`(Seq(sslTargetName, "CanonicalHostedZoneNameID")),
        false
      ),
      Condition = sslCondition
    )
  }
}

trait Autoscaling {
  implicit class RichASG(asg: `AWS::AutoScaling::AutoScalingGroup`){
    def withPolicy(name: String, delta: Int, coolDown: Token[Int], adjType: String = "ChangeInCapacity") =
      `AWS::AutoScaling::ScalingPolicy`(
        name,
        AdjustmentType = adjType,
        AutoScalingGroupName = asg,
        Cooldown = coolDown,
        ScalingAdjustment = delta.toString
      )
  }

  def launchConfig(
    name:         String,
    image:        Token[AMIId],
    instanceType: Token[String],
    keyName:      Token[String],
    sgs:          Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
    userData:     `Fn::Base64`,
    iam:          Option[Token[ResourceRef[`AWS::IAM::InstanceProfile`]]] = None,
    condition:    Option[ConditionRef] = None,
    dependsOn:    Option[Seq[String]]  = None,
    blockDevices: Option[Seq[BlockDeviceMapping]] = None
  )(implicit vpc: `AWS::EC2::VPC`) =
    SecurityGroupRoutable from `AWS::AutoScaling::LaunchConfiguration`(
      name                  = name,
      ImageId               = image,
      InstanceType          = instanceType,
      KeyName               = keyName,
      SecurityGroups        = sgs,
      UserData              = userData,
      IamInstanceProfile    = iam,
      Condition             = condition,
      DependsOn             = dependsOn,
      BlockDeviceMappings   = blockDevices
    )

  def asg(
      baseName:     String,
      image:        Token[AMIId],
      instanceType: Token[String],
      keyName:      Token[String],
      sgs:          Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
      userData:     `Fn::Base64`,
      iam:          Option[Token[ResourceRef[`AWS::IAM::InstanceProfile`]]] = None,
      condition:    Option[ConditionRef] = None,
      dependsOn:    Option[Seq[String]]  = None,
      blockDevices: Option[Seq[BlockDeviceMapping]] = None
    )(
      minSize:     Int,
      maxSize:     Int,
      desiredSize: Token[Int],
      tag:         String,
      azs:         Seq[Token[String]],
      subnets:     Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
      elbs:        Option[Seq[Token[ResourceRef[`AWS::ElasticLoadBalancing::LoadBalancer`]]]] = None
    )(implicit vpc: `AWS::EC2::VPC`) = {

      val resourceName = baseName + "LaunchConfig"
      val asgName = baseName + "AutoScale"

      val launchConfigSGR @ SecurityGroupRoutable(aLaunchConfig, _, _) =
        launchConfig(baseName, image, instanceType, keyName, sgs, userData, iam, condition, dependsOn, blockDevices)

      val asg = `AWS::AutoScaling::AutoScalingGroup`(
        name                    = asgName,
        AvailabilityZones       = azs,
        LaunchConfigurationName = aLaunchConfig,
        MinSize                 = minSize,
        MaxSize                 = maxSize,
        DesiredCapacity         = desiredSize,
        HealthCheckType         = "EC2",
        VPCZoneIdentifier       = subnets,
        Tags                    = AmazonTag.fromNamePropagate(tag),
        LoadBalancerNames       = elbs,
        Condition               = condition,
        DependsOn               = dependsOn
      )

      SecurityGroupRoutable.from(launchConfigSGR, asg)
  }
}

trait SecurityGroup {
  case class CidrTransport(fromPort: Int, protocol: String = "tcp", from: Option[Token[CidrBlock]] = None, toPort: Option[Int] = None)
  case class SGTransport(fromPort: Int, protocol: String = "tcp", from: Option[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None, toPort: Option[Int] = None)

  def securityGroup(name: String, desc: String)(implicit vpc: `AWS::EC2::VPC`) =
    `AWS::EC2::SecurityGroup`(
      name,
      GroupDescription = desc,
      VpcId = vpc,
      SecurityGroupEgress = None,
      SecurityGroupIngress = None,
      Tags = AmazonTag.fromName(name),
      Condition = None
    )

  def securityGroup(name: String, desc: String, condition: ConditionRef)(implicit vpc: `AWS::EC2::VPC`) =
    `AWS::EC2::SecurityGroup`(
      name,
      GroupDescription = desc,
      VpcId = vpc,
      SecurityGroupEgress = None,
      SecurityGroupIngress = None,
      Tags = AmazonTag.fromName(name),
      Condition = Some(condition)
    )

  def securityGroupFromOption(name: String, desc: String, condition: Option[ConditionRef])(implicit vpc: `AWS::EC2::VPC`) =
    `AWS::EC2::SecurityGroup`(
      name,
      GroupDescription = desc,
      VpcId = vpc,
      SecurityGroupEgress = None,
      SecurityGroupIngress = None,
      Tags = AmazonTag.fromName(name),
      Condition = condition
    )

  def securityGroup(name: String, desc: String, egress: Option[Seq[EgressSpec]] = None)(implicit vpc: `AWS::EC2::VPC`) =
    `AWS::EC2::SecurityGroup`(
      name,
      GroupDescription = desc,
      VpcId = vpc,
      SecurityGroupEgress = egress,
      SecurityGroupIngress = None,
      Tags = AmazonTag.fromName(name),
      Condition = None
    )

  private def noNeg1(s: String) = s.replaceAll("-1","Neg1")
  private def noNeg1(i: Int) = i.toString.replaceAll("-1","Neg1")
  private def safeResourceName(name: String): String = name.replaceAll("[^A-Za-z0-9]", "")
  private def ingressName(from: String, portProto: PortProtocol, to: String): String =
    safeResourceName(from + "To" + to + "IngressProto" + noNeg1(portProto.protocol.rep) + "FromPort" +
                     noNeg1(portProto.startPort) + "ToPort" + noNeg1(portProto.endPort))


  private def ingressSpec(from: ParameterRef[CidrBlock], portProto: PortProtocol, to: `AWS::EC2::SecurityGroup`)
    = `AWS::EC2::SecurityGroupIngress`(
      ingressName(from.p.name, portProto, to.name),
      to,
      portProto.protocol.rep,
      portProto.startPort.toString,
      portProto.endPort.toString,
      CidrIp = Some(from)
    )

  def resourceNameSafeUUID() = UUID.randomUUID().toString.replaceAll("-","")

  private def ingressSpec(from: `AWS::EC2::SecurityGroup`, portProto: PortProtocol, to: `AWS::EC2::SecurityGroup`)
    = `AWS::EC2::SecurityGroupIngress`(
      ingressName(from.name, portProto, to.name),
      to,
      portProto.protocol.rep,
      portProto.startPort.toString,
      portProto.endPort.toString,
      SourceSecurityGroupId = Some(from)
    )

  private def ingressSpec(from: Token[ResourceRef[`AWS::EC2::SecurityGroup`]], portProto: PortProtocol, to: Token[ResourceRef[`AWS::EC2::SecurityGroup`]])
    = `AWS::EC2::SecurityGroupIngress`(
      ingressName(from.toString, portProto, to.toString),
      to,
      portProto.protocol.rep,
      portProto.startPort.toString,
      portProto.endPort.toString,
      SourceSecurityGroupId = Some(from)
    )

  case class PortProtocolFragment(start: Int, end: Int) {
    def /(proto: TransportProtocol) = PortProtocol(start, end, proto)
  }
  object PortProtocolFragment {
    implicit def fromRange(r: Range): PortProtocolFragment = PortProtocolFragment(r.start, r.end)
  }

  sealed abstract class TransportProtocol(val rep: String)
  object TransportProtocol{
    implicit def fromInt(p: Int): PortProtocolFragment = PortProtocolFragment(p, p)
    implicit def fromRange(r: Range): PortProtocolFragment = PortProtocolFragment(r.start, r.end)
  }

  case object TCP  extends TransportProtocol("tcp")
  case object ICMP extends TransportProtocol("icmp")
  case object UDP  extends TransportProtocol("udp")
  case object ALL  extends TransportProtocol("-1")

  case class PortProtocol(startPort: Int, endPort: Int, protocol: TransportProtocol)
  object PortProtocol{
    //def apply(ports: Range, protocol: TransportProtocol = TCP): PortProtocol = PortProtocol(ports.start, ports.end, protocol)
    //def apply(port: Int, protocol: TransportProtocol = TCP): PortProtocol = PortProtocol(port, port, protocol)
    implicit def fromIntToSeq(port: Int): Seq[PortProtocol] = Seq(PortProtocol(port, port, TCP))
    implicit def fromInt(port: Int): PortProtocol = PortProtocol(port, port, TCP)

    implicit def fromRangeToSeq(ports: Range): Seq[PortProtocol] = Seq(PortProtocol(ports.start, ports.end, TCP))
    //implicit def fromRange(ports: Range): PortProtocol = PortProtocol(ports.start, ports.end, TCP)

    implicit def toSeq(pp: PortProtocol): Seq[PortProtocol] = Seq(pp)
  }

  implicit class RichCidrParam(val c: ParameterRef[CidrBlock]){
    def ->-(port: Seq[PortProtocol]) = CidrIngressPrefix(c, port)
  }

  case class CidrIngressPrefix(from: ParameterRef[CidrBlock], portProto: Seq[PortProtocol]){
    def ->-(to: RichSecurityGroup) = portProto.flatMap( pp => Seq(ingressSpec(from.c, pp, to.s)) )
    def ->-[R <: Resource[R]](to: SecurityGroupRoutable[R]) = portProto.flatMap( pp => Seq(ingressSpec(from.c, pp, to.sg)) )
  }

  implicit class RichSecurityGroup(val s: `AWS::EC2::SecurityGroup`){
    def -<-(portProto: Seq[PortProtocol]) = IngressSuffix(this, portProto)
    def ->-(portProto: Seq[PortProtocol]) = IngressPrefix(this, portProto)
  }

  implicit class RichSecurityGroupRoutable[R <: Resource[R]](val sgr: SecurityGroupRoutable[R]){
    def -<-(portProto: Seq[PortProtocol]) = RoutableIngressSuffix(this, portProto)
    def ->-(portProto: Seq[PortProtocol]) = RoutableIngressPrefix(this, portProto)
  }

  implicit class RichTokenRefSecurityGroup(val s: Token[ResourceRef[`AWS::EC2::SecurityGroup`]]){
    def -<-(portProto: Seq[PortProtocol]) = TokenIngressSuffix(this, portProto)
    def ->-(portProto: Seq[PortProtocol]) = TokenIngressPrefix(this, portProto)
  }
  case class TokenIngressPrefix(from: RichTokenRefSecurityGroup, portProto: Seq[PortProtocol]){
    def ->-(to:        RichTokenRefSecurityGroup) = portProto.map(pp => ingressSpec(from.s, pp, to.s))
    def ->-(to:        RichSecurityGroup)         = portProto.map(pp => ingressSpec(from.s, pp, to.s))
    def -<-(fromOther: RichTokenRefSecurityGroup) = portProto.map(pp => ingressSpec(from.s, pp, fromOther.s)) ++
                                                    portProto.map(pp => ingressSpec(fromOther.s, pp, from.s))
    def -<-(fromOther: RichSecurityGroup)         = portProto.map(pp => ingressSpec(from.s, pp, fromOther.s)) ++
                                                    portProto.map(pp => ingressSpec(fromOther.s, pp, from.s))
  }
  case class TokenIngressSuffix(to: RichTokenRefSecurityGroup, portProto: Seq[PortProtocol]){
    def ->-(toOther: RichTokenRefSecurityGroup) = portProto.map(pp => ingressSpec(to.s, pp, toOther.s)) ++
                                                  portProto.map(pp => ingressSpec(toOther.s, pp, to.s))
    def ->-(toOther: RichSecurityGroup)         = portProto.map(pp => ingressSpec(to.s, pp, toOther.s)) ++
                                                  portProto.map(pp => ingressSpec(toOther.s, pp, to.s))
    def -<-(from:    RichTokenRefSecurityGroup) = portProto.map(pp => ingressSpec(from.s, pp, to.s))
    def -<-(from:    RichSecurityGroup)         = portProto.map(pp => ingressSpec(from.s, pp, to.s))
  }


  case class IngressPrefix(from: RichSecurityGroup, portProto: Seq[PortProtocol]){
    def ->-[R2 <: Resource[R2 ]](to: RichSecurityGroupRoutable[R2]) = portProto.map(pp => ingressSpec(from.s, pp, to.sgr.sg))
    def ->-(to:        RichSecurityGroup) = portProto.map(pp => ingressSpec(from.s, pp, to.s))
    def -<-(fromOther: RichSecurityGroup) = portProto.map(pp => ingressSpec(from.s, pp, fromOther.s)) ++
                                            portProto.map(pp => ingressSpec(fromOther.s, pp, from.s))
  }
  case class IngressSuffix(to: RichSecurityGroup, portProto: Seq[PortProtocol]){
    def ->-(toOther: RichSecurityGroup) = portProto.map(pp => ingressSpec(to.s, pp, toOther.s)) ++
                                          portProto.map(pp => ingressSpec(toOther.s, pp, to.s))
    def -<-(from:    RichSecurityGroup) = portProto.map(pp => ingressSpec(from.s, pp, to.s))
  }

  case class RoutableIngressPrefix[R <: Resource[R]](from: RichSecurityGroupRoutable[R], portProto: Seq[PortProtocol]){
    def ->-[R2 <: Resource[R2 ]](to:        RichSecurityGroupRoutable[R2]) = portProto.map(pp => ingressSpec(from.sgr.sg, pp, to.sgr.sg))
    def ->-(to: RichSecurityGroup)                                         = portProto.flatMap( pp => Seq(ingressSpec(from.sgr.sg, pp, to.s)) )
    def -<-[R2 <: Resource[R2]](fromOther: RichSecurityGroupRoutable[R2]) = portProto.map(pp => ingressSpec(from.sgr.sg, pp, fromOther.sgr.sg)) ++
      portProto.map(pp => ingressSpec(fromOther.sgr.sg, pp, from.sgr.sg))
  }
  case class RoutableIngressSuffix[R <: Resource[R]](to: RichSecurityGroupRoutable[R], portProto: Seq[PortProtocol]){
    def ->-[R2 <: Resource[R2]](toOther: RichSecurityGroupRoutable[R2]) = portProto.map(pp => ingressSpec(to.sgr.sg, pp, toOther.sgr.sg)) ++
      portProto.map(pp => ingressSpec(toOther.sgr.sg, pp, to.sgr.sg))
    def -<-[R2 <: Resource[R2]](from:    RichSecurityGroupRoutable[R2]) = portProto.map(pp => ingressSpec(from.sgr.sg, pp, to.sgr.sg))
  }
}

trait Gateway {
  def withInternetGateway(implicit vpc: `AWS::EC2::VPC`) = {
    // internet gateway
    val gName = "InternetGateway"
    val gateway = `AWS::EC2::InternetGateway`(
      gName,
      Tags = AmazonTag.fromName(gName)
    )

    val attName = "GatewayToInternet"
    val attachment = `AWS::EC2::VPCGatewayAttachment`(
      attName,
      VpcId = vpc,
      gatewayId = gateway
    )

    (gateway, attachment)
  }
}

trait VPC extends Outputs {
  implicit class RichVPC(vpc: `AWS::EC2::VPC`)

  def withVpc(cidrBlock: Token[CidrBlock])(f: (`AWS::EC2::VPC`) => Template): Template = {
    val vpcName = "VPC"
    val vpc = `AWS::EC2::VPC`(
      vpcName,
      CidrBlock = cidrBlock,
      Tags = AmazonTag.fromName(vpcName),
      EnableDnsSupport = true,
      EnableDnsHostnames = true
    )

    f(vpc) ++ vpc.andOutput("VPCID", "VPC Info")
  }
}

trait AvailabilityZone {
  case class AZ(zone: Token[String])
  def withAZ(zone: Token[String])(f: (AZ) => Template): Template = f(AZ(zone)) // no resource to create
}

trait EC2 {
  def ec2(
    name:                  String,
    InstanceType:          Token[String],
    KeyName:               Token[String],
    ImageId:               Token[AMIId],
    SecurityGroupIds:      Seq[ResourceRef[`AWS::EC2::SecurityGroup`]],
    Tags:                  Seq[AmazonTag],
    Metadata:              Option[Map[String, String]]                             = None,
    IamInstanceProfile:    Option[Token[ResourceRef[`AWS::IAM::InstanceProfile`]]] = None,
    SourceDestCheck:       Option[String]                                          = None,
    UserData:              Option[`Fn::Base64`]                                    = None,
    Monitoring:            Option[Boolean]                                         = None,
    Volumes:               Option[Seq[EC2MountPoint]]                              = None,
    DisableApiTermination: Option[String]                                          = None,
    Condition:             Option[ConditionRef]                                    = None,
    DependsOn:             Option[Seq[String]]                                     = None
  )(implicit subnet: `AWS::EC2::Subnet`, vpc: `AWS::EC2::VPC`) =
    SecurityGroupRoutable from `AWS::EC2::Instance`(
      name                  = name,
      InstanceType          = InstanceType,
      KeyName               = KeyName,
      SubnetId              = subnet,
      ImageId               = ImageId,
      Tags                  = Tags,
      SecurityGroupIds      = SecurityGroupIds,
      Metadata              = Metadata,
      IamInstanceProfile    = IamInstanceProfile,
      SourceDestCheck       = SourceDestCheck,
      UserData              = UserData,
      Monitoring            = Monitoring,
      Volumes               = Volumes,
      DisableApiTermination = DisableApiTermination,
      Condition             = Condition,
      DependsOn             = DependsOn
    )
}

trait ElasticLoadBalancing {

  def elbL(
      name:              String,
      subnets:           Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
      healthCheckTarget: String,
      loadBalancerName:  Option[Token[String]] = None,
      condition:         Option[ConditionRef] = None,
      scheme:            Option[ELBScheme] = None,
      loggingBucket:     Option[Token[ResourceRef[`AWS::S3::Bucket`]]] = None,
      dependsOn:         Option[Seq[String]] = None
    )(
      listeners: Seq[ELBListener]
    )(
      healthCheck: ELBHealthCheck = ELBHealthCheck(
        Target             = healthCheckTarget,
        HealthyThreshold   = "3",
        UnhealthyThreshold = "5",
        Interval           = "30",
        Timeout            = "5")
    )(implicit vpc: `AWS::EC2::VPC`) =
    SecurityGroupRoutable from `AWS::ElasticLoadBalancing::LoadBalancer`.inVpc(
      name,
      CrossZone           = Some(true),
      Scheme              = scheme,
      Subnets             = subnets,
      Listeners           = listeners,
      LoadBalancerName    = loadBalancerName,
      HealthCheck         = Some(healthCheck),
      Tags                = AmazonTag.fromName(name),
      AccessLoggingPolicy = loggingBucket match {
        case Some(b) => Some(ELBAccessLoggingPolicy(
          Enabled         = true,
          S3BucketName    = b,
          EmitInterval    = Some(ELBLoggingEmitInterval.`60`),
          S3BucketPrefix  = Some(s"elb/$name")
        ))
        case None    => None
      },
      Condition           = condition,
      DependsOn           = dependsOn
    )

  def elb(
      name:              String,
      subnets:           Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
      healthCheckTarget: String,
      loadBalancerName:  Option[Token[String]] = None,
      condition:         Option[ConditionRef] = None,
      scheme:            Option[ELBScheme] = None,
      loggingBucket:     Option[Token[ResourceRef[`AWS::S3::Bucket`]]] = None,
      dependsOn:         Option[Seq[String]] = None
    )(
      listener: ELBListener
    )(
      healthCheck: ELBHealthCheck = ELBHealthCheck(
        Target             = healthCheckTarget,
        HealthyThreshold   = "3",
        UnhealthyThreshold = "5",
        Interval           = "30",
        Timeout            = "5")
    )(implicit vpc: `AWS::EC2::VPC`) =
      elbL(name, subnets, healthCheckTarget, loadBalancerName, condition, scheme, loggingBucket, dependsOn)(Seq(listener))(healthCheck)
}
