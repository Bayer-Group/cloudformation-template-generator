package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

import scala.annotation.implicitNotFound
import scala.language.implicitConversions

/**
 * Created by Ryan Richt on 2/28/15
 */

case class `AWS::EC2::EIP` private (
  name:                   String,
  Domain:                 Option[String],
  InstanceId:             Option[Token[ResourceRef[`AWS::EC2::Instance`]]],
  override val Condition: Option[ConditionRef],
  override val DependsOn: Option[Seq[String]]
) extends Resource[`AWS::EC2::EIP`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::EC2::EIP` extends DefaultJsonProtocol {
  // Need to be explicit here to get it to pick the apply above, not the backwards compatibility one below.
  implicit val format: JsonFormat[`AWS::EC2::EIP`] =
    jsonFormat5[String,
      Option[String],
      Option[Token[ResourceRef[`AWS::EC2::Instance`]]],
      Option[ConditionRef],
      Option[Seq[String]],
      `AWS::EC2::EIP`](`AWS::EC2::EIP`.apply)

  @deprecated(message = "Use .vpc() or .classic() instead.", since = "v3.0.6")
  def apply(name:                   String,
            Domain:                 String,
            InstanceId:             Token[ResourceRef[`AWS::EC2::Instance`]],
            Condition: Option[ConditionRef] = None,
            DependsOn: Option[Seq[String]] = None): `AWS::EC2::EIP` =
    `AWS::EC2::EIP`(name, Some(Domain), Some(InstanceId), Condition, DependsOn)

  def vpc(name:                   String,
            InstanceId:             Option[Token[ResourceRef[`AWS::EC2::Instance`]]],
            Condition:              Option[ConditionRef] = None,
            DependsOn:              Option[Seq[String]] = None): `AWS::EC2::EIP` =
    `AWS::EC2::EIP`(name, Some("vpc"), InstanceId, Condition, DependsOn)

  def classic(name:                 String,
            InstanceId:             Option[Token[ResourceRef[`AWS::EC2::Instance`]]],
            Condition:              Option[ConditionRef] = None,
            DependsOn:              Option[Seq[String]] = None): `AWS::EC2::EIP` =
    `AWS::EC2::EIP`(name, None, InstanceId, Condition, DependsOn)
}

case class `AWS::EC2::EIPAssociation`(
  name:                   String,
  AllocationId:           Option[Token[String]],
  InstanceId:             Token[ResourceRef[`AWS::EC2::Instance`]],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::EC2::EIPAssociation`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::EC2::EIPAssociation` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::EIPAssociation`] = jsonFormat5(`AWS::EC2::EIPAssociation`.apply)
}

case class AMIId(id: String)
object AMIId extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AMIId] = new JsonFormat[AMIId] {
    def write(obj: AMIId) = JsString(obj.id)
    def read(json: JsValue) = AMIId(json.convertTo[String])
  }
}

case class EC2MountPoint(Device: String, VolumeId: Token[String])
object EC2MountPoint extends DefaultJsonProtocol {
  implicit val format: JsonFormat[EC2MountPoint] = jsonFormat2(EC2MountPoint.apply)
}

case class `AWS::EC2::Instance`(
  name:                   String,
  InstanceType:           Token[String],
  KeyName:                Token[String],
  SubnetId:               Token[ResourceRef[`AWS::EC2::Subnet`]],
  ImageId:                Token[AMIId],
  Tags:                   Seq[AmazonTag],
  SecurityGroupIds:       Seq[ResourceRef[`AWS::EC2::SecurityGroup`]] = Seq.empty[ResourceRef[`AWS::EC2::SecurityGroup`]],
  Metadata:               Option[Map[String, String]] = None,
  IamInstanceProfile:     Option[Token[ResourceRef[`AWS::IAM::InstanceProfile`]]] = None,
  SourceDestCheck:        Option[String] = None,
  UserData:               Option[`Fn::Base64`] = None,
  Monitoring:             Option[Boolean] = None,
  Volumes:                Option[Seq[EC2MountPoint]] = None,
  PrivateIpAddress:       Option[Token[IPAddress]] = None,
  DisableApiTermination:  Option[String] = None,
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::EC2::Instance`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::Instance` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::Instance`] = jsonFormat17(`AWS::EC2::Instance`.apply)
}

case class `AWS::EC2::InternetGateway`(name: String, Tags: Seq[AmazonTag],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::InternetGateway`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::InternetGateway` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::InternetGateway`] = jsonFormat3(`AWS::EC2::InternetGateway`.apply)
}

case class `AWS::EC2::KeyPair::KeyName`(name: String,
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::KeyPair::KeyName`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::KeyPair::KeyName` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::KeyPair::KeyName`] = jsonFormat2(`AWS::EC2::KeyPair::KeyName`.apply)
}

case class `AWS::EC2::CustomerGateway`(
  name: String,
  BgpAsn: Token[Int],
  IpAddress: Token[IPAddress],
  Tags: Seq[AmazonTag],
  Type: VPNType = VPNType("ipsec.1"),
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::CustomerGateway`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::CustomerGateway` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::CustomerGateway`] = jsonFormat6(`AWS::EC2::CustomerGateway`.apply)
}

case class `AWS::EC2::VPNGateway`(
  name: String,
  Type: Token[VPNType] = VPNType("ipsec.1"),
  Tags: Seq[AmazonTag],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::VPNGateway`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::VPNGateway` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPNGateway`] = jsonFormat4(`AWS::EC2::VPNGateway`.apply)
}

case class VPNType(value: String) { require( value.equals("ipsec.1"), "only ipsec.1 is valid") }
object VPNType extends DefaultJsonProtocol {
  implicit def toString(s: String): VPNType = VPNType(s)
  implicit val format: JsonFormat[VPNType] = new JsonFormat[VPNType] {

    override def read(json: JsValue): VPNType = ???

    override def write(obj: VPNType): JsValue =  JsString(obj.value)

  }
}

case class `AWS::EC2::VPNConnection` (
  name: String,
  CustomerGatewayId: Token[ResourceRef[`AWS::EC2::CustomerGateway`]],
  StaticRoutesOnly: Boolean,
  VpnGatewayId: Token[ResourceRef[`AWS::EC2::VPNGateway`]],
  Tags: Seq[AmazonTag],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::VPNConnection`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::VPNConnection` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPNConnection`] = jsonFormat6(`AWS::EC2::VPNConnection`.apply)
}

case class `AWS::EC2::VPNConnectionRoute`(
  name: String,
  DestinationCidrBlock: Token[CidrBlock],
  VpnConnectionId: Token[ResourceRef[`AWS::EC2::VPNConnection`]],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::VPNConnectionRoute`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::VPNConnectionRoute` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPNConnectionRoute`] = jsonFormat4(`AWS::EC2::VPNConnectionRoute`.apply)
}

case class `AWS::EC2::NetworkAcl`(
  name: String,
  VpcId: Token[ResourceRef[`AWS::EC2::VPC`]],
  Tags: Seq[AmazonTag],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::NetworkAcl`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::NetworkAcl` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::NetworkAcl`] = jsonFormat4(`AWS::EC2::NetworkAcl`.apply)
}

case class `AWS::EC2::NetworkAclEntry`(
  name: String,
  CidrBlock: Token[CidrBlock],
  Egress: Token[Boolean],
  Icmp: Token[EC2IcmpProperty],
  NetworkAclId: Token[ResourceRef[`AWS::EC2::NetworkAcl`]],
  PortRange: Token[PortRange],
  Protocol: Token[Protocol],
  RuleAction: Token[RuleAction],
  RuleNumber: Token[RuleNumber],
override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::NetworkAclEntry`] {

def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::NetworkAclEntry` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::NetworkAclEntry`] = jsonFormat10(`AWS::EC2::NetworkAclEntry`.apply)
}

/**
  * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-ec2-icmp.html
  *
  * @param Code
  * @param Type
  */
case class EC2IcmpProperty(Code: Int, Type: Int)
object EC2IcmpProperty extends DefaultJsonProtocol {
  implicit val format: JsonFormat[EC2IcmpProperty] = jsonFormat2(EC2IcmpProperty.apply)
}

case class PortRange(From: Int, To: Int)
object PortRange extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PortRange] = jsonFormat2(PortRange.apply)
}

case class RuleAction(value: String) { require( value.equals("allow") || value.equals("deny"), "must be allow or deny") }
object RuleAction {
  implicit def toString(s: String): RuleAction = RuleAction(s)
  implicit val format: JsonFormat[RuleAction] = new JsonFormat[RuleAction] {
    override def read(json: JsValue): RuleAction = ???

    override def write(obj: RuleAction): JsValue = JsString(obj.value)
  }
}

case class RuleNumber(value: Int) { require( value <= 32766 && value >= 1, "must be between 1 and 32766") }
object RuleNumber {
  implicit def toInt(i: Int): RuleNumber = RuleNumber(i)
  implicit val format: JsonFormat[RuleNumber] = new JsonFormat[RuleNumber] {
    override def read(json: JsValue): RuleNumber = ???

    override def write(obj: RuleNumber): JsValue = JsNumber(obj.value)
  }
}

case class Protocol(value: Int) { require( value == -1 || (value <= 255 && value >= 1), "must be -1 or between 1 and 255") }
object Protocol {
  implicit def toInt(i: Int): Protocol = Protocol(i)
  implicit val format: JsonFormat[Protocol] = new JsonFormat[Protocol] {
    override def read(json: JsValue): Protocol = ???

    override def write(obj: Protocol): JsValue = JsNumber(obj.value)
  }
}

@implicitNotFound("A Route can only have exactly ONE of GatewayId, InstanceId, NetworkInterfaceId or VpcPeeringConnectionId set")
sealed trait ValidRouteComboOption
case class InternetGatewayRoute(v:Token[ResourceRef[`AWS::EC2::InternetGateway`]]) extends ValidRouteComboOption
case class EC2InstanceRoute(v:Token[ResourceRef[`AWS::EC2::Instance`]]) extends ValidRouteComboOption
case class VPCPeeringRoute(v:Token[ResourceRef[`AWS::EC2::VPCPeeringConnection`]]) extends ValidRouteComboOption

object ValidRouteComboOption {
  implicit def toInternetGateway[T](v: T)(implicit t:T => Token[ResourceRef[`AWS::EC2::InternetGateway`]]) = InternetGatewayRoute(v)
  implicit def toEC2InstanceRoute[T](v:T)(implicit t :T => Token[ResourceRef[`AWS::EC2::Instance`]]) = EC2InstanceRoute(v)
  implicit def toVPCPeeringRoute[T](v:T)(implicit t :T => Token[ResourceRef[`AWS::EC2::VPCPeeringConnection`]]) = VPCPeeringRoute(v)
}

class `AWS::EC2::Route` private (
  val name:                   String,
  val RouteTableId:           Token[ResourceRef[`AWS::EC2::RouteTable`]],
  val DestinationCidrBlock:   Token[CidrBlock],
  val GatewayId:              Option[Token[ResourceRef[`AWS::EC2::InternetGateway`]]] = None,
  val InstanceId:             Option[Token[ResourceRef[`AWS::EC2::Instance`]]] = None,
  val VpcPeeringConnectionId: Option[Token[ResourceRef[`AWS::EC2::VPCPeeringConnection`]]] = None,
  override val Condition:     Option[ConditionRef] = None,
  override val DependsOn:     Option[Seq[String]] = None
) extends Resource[`AWS::EC2::Route`] {
  private val asSeq = Seq(name, RouteTableId, DestinationCidrBlock, GatewayId, InstanceId, VpcPeeringConnectionId,
    Condition, DependsOn)

  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::EC2::Route`(name, RouteTableId, DestinationCidrBlock, GatewayId, InstanceId, VpcPeeringConnectionId,
      newCondition, DependsOn)
}
object `AWS::EC2::Route` extends DefaultJsonProtocol {

  private def writeField[T: JsonFormat](t: T) = {
    val writer = implicitly[JsonFormat[T]]
    writer match {
      case _: OptionFormat[_] if t == None => None
      case _ => Some(writer.write(t))
    }
  }

  // Because we dont want the default case class apply method without our checks
  implicit val format: JsonFormat[`AWS::EC2::Route`] = new JsonFormat[`AWS::EC2::Route`]{
    def write(p: `AWS::EC2::Route`) = {
      JsObject(
        Map(
          "name"                   -> writeField(p.name),
          "RouteTableId"           -> writeField(p.RouteTableId),
          "DestinationCidrBlock"   -> writeField(p.DestinationCidrBlock),
          "GatewayId"              -> writeField(p.GatewayId),
          "InstanceId"             -> writeField(p.InstanceId),
          "VpcPeeringConnectionId" -> writeField(p.VpcPeeringConnectionId),
          "Condition"              -> writeField(p.Condition),
          "DependsOn"              -> writeField(p.DependsOn)
        ).filter(_._2.isDefined).mapValues(_.get)
      )
    }

    // TODO
    def read(json: JsValue) = ???
  }

  def apply(
             name:                         String,
             RouteTableId:                 Token[ResourceRef[`AWS::EC2::RouteTable`]],
             DestinationCidrBlock:         Token[CidrBlock],
             connectionBobber:             ValidRouteComboOption,
             Condition: Option[ConditionRef] = None,
             DependsOn: Option[Seq[String]] = None
           ) =
    connectionBobber match {
      case InternetGatewayRoute(v) => new `AWS::EC2::Route`(name, RouteTableId, DestinationCidrBlock,
        Some(v), None,None, Condition, DependsOn)
      case EC2InstanceRoute(v)     => new `AWS::EC2::Route`(name, RouteTableId, DestinationCidrBlock,
        None, Some(v), None, Condition, DependsOn)
      case VPCPeeringRoute(v)      => new `AWS::EC2::Route`(name, RouteTableId, DestinationCidrBlock,
        None, None ,Some(v), Condition, DependsOn)
    }
}

case class `AWS::EC2::RouteTable`(name: String, VpcId: Token[ResourceRef[`AWS::EC2::VPC`]], Tags: Seq[AmazonTag],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::RouteTable`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::RouteTable` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::RouteTable`] = jsonFormat4(`AWS::EC2::RouteTable`.apply)
}

case class `AWS::EC2::SecurityGroup`(
  name:                 String,
  GroupDescription:     String,
  VpcId:                Token[ResourceRef[`AWS::EC2::VPC`]],
  SecurityGroupIngress: Option[Seq[IngressSpec]],
  SecurityGroupEgress:  Option[Seq[EgressSpec]] = None,
  Tags:                 Seq[AmazonTag],
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::EC2::SecurityGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::SecurityGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::SecurityGroup`] = jsonFormat7(`AWS::EC2::SecurityGroup`.apply)
}

sealed trait IngressSpec
object IngressSpec extends DefaultJsonProtocol {
  implicit val format: JsonFormat[IngressSpec] = new JsonFormat[IngressSpec] {
    def write(obj: IngressSpec) =
      obj match {
        case i: CidrIngressSpec => i.toJson
        case i: SGIngressSpec => i.toJson
      }
    //TODO
    def read(json: JsValue) = ???
  }
}
case class CidrIngressSpec(IpProtocol: String, CidrIp: Token[CidrBlock], FromPort: String, ToPort: String) extends IngressSpec
object CidrIngressSpec extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CidrIngressSpec] = jsonFormat4(CidrIngressSpec.apply)
}
case class SGIngressSpec(IpProtocol: String, SourceSecurityGroupId: Token[ResourceRef[`AWS::EC2::SecurityGroup`]], FromPort: String, ToPort: String) extends IngressSpec
object SGIngressSpec extends DefaultJsonProtocol {
  implicit val format: JsonFormat[SGIngressSpec] = jsonFormat4(SGIngressSpec.apply)
}

case class IPAddressSegment(value: Int){ require( value <= 255 && value >= 0 ) }
object IPAddressSegment {
  implicit def fromInt(i: Int): IPAddressSegment = IPAddressSegment(i)
}

case class IPMask(value: Int){ require( value <= 32 && value >= 0 ) }
object IPMask {
  implicit def fromInt(i: Int): IPMask = IPMask(i)
}

case class CidrBlock(a: IPAddressSegment, b: IPAddressSegment, c: IPAddressSegment, d: IPAddressSegment, mask: IPMask) {
  def toJsString: JsString =  JsString( Seq(a, b, c, d).map(_.value.toString).mkString(".") + "/" + mask.value.toString )
}
object CidrBlock extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CidrBlock] = new JsonFormat[CidrBlock] {
    def write(obj: CidrBlock) = obj.toJsString

    def read(json: JsValue) = {
      val parts = json.convertTo[String].split(Array('.','/')).map(_.toInt)

      CidrBlock(parts(0), parts(1), parts(2), parts(3), parts(4))
    }
  }
}

case class IPAddress(a: IPAddressSegment, b: IPAddressSegment, c: IPAddressSegment, d: IPAddressSegment) {
  def toJsString: JsString =  JsString( Seq(a, b, c, d).map(_.value.toString).mkString("."))
}
object IPAddress extends DefaultJsonProtocol {
  implicit val format: JsonFormat[IPAddress] = new JsonFormat[IPAddress] {
    def write(obj: IPAddress) = obj.toJsString

    def read(json: JsValue) = {
      val parts = json.convertTo[String].split(Array('.')).map(_.toInt)

      IPAddress(parts(0), parts(1), parts(2), parts(3))
    }
  }
}

sealed trait EgressSpec
object EgressSpec extends DefaultJsonProtocol {
  implicit val format: JsonFormat[EgressSpec] = new JsonFormat[EgressSpec] {
    def write(obj: EgressSpec) =
      obj match {
        case i: CidrEgressSpec => i.toJson
        case i: SGEgressSpec   => i.toJson
      }
    //TODO
    def read(json: JsValue) = ???
  }
}
case class CidrEgressSpec(IpProtocol: String, CidrIp: Token[CidrBlock], FromPort: String, ToPort: String) extends EgressSpec
object CidrEgressSpec extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CidrEgressSpec] = jsonFormat4(CidrEgressSpec.apply)
}
case class SGEgressSpec(IpProtocol: String, DestinationSecurityGroupId: Token[String], FromPort: String, ToPort: String) extends EgressSpec
object SGEgressSpec extends DefaultJsonProtocol {
  implicit val format: JsonFormat[SGEgressSpec] = jsonFormat4(SGEgressSpec.apply)
}

case class `AWS::EC2::SecurityGroupEgress`(
  name:                       String,
  GroupId:                    Token[ResourceRef[`AWS::EC2::SecurityGroup`]],
  IpProtocol:                 String,
  FromPort:                   String,
  ToPort:                     String,
  CidrIp:                     Option[Token[CidrBlock]] = None, // either CidrIp or SourceSecurityGroupId required
  DestinationSecurityGroupId: Option[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None, // either CidrIp or SourceSecurityGroupId required
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::EC2::SecurityGroupEgress`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::SecurityGroupEgress` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::SecurityGroupEgress`] = jsonFormat8(`AWS::EC2::SecurityGroupEgress`.apply)
}

case class `AWS::EC2::SecurityGroupIngress`(
  name:                  String,
  GroupId:               Token[ResourceRef[`AWS::EC2::SecurityGroup`]],
  IpProtocol:            String,
  FromPort:              String,
  ToPort:                String,
  CidrIp:                Option[Token[CidrBlock]] = None, // either CidrIp or SourceSecurityGroupId required
  SourceSecurityGroupId: Option[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None, // either CidrIp or SourceSecurityGroupId required
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::EC2::SecurityGroupIngress`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::SecurityGroupIngress` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::SecurityGroupIngress`] = jsonFormat8(`AWS::EC2::SecurityGroupIngress`.apply)
}

case class `AWS::EC2::Subnet`(
  name:                String,
  VpcId:               Token[ResourceRef[`AWS::EC2::VPC`]],
  AvailabilityZone:    Option[Token[String]] = None,
  CidrBlock:           Token[CidrBlock],
  Tags:                Seq[AmazonTag],
  MapPublicIpOnLaunch: Option[Token[Boolean]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::EC2::Subnet`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::Subnet` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::Subnet`] = jsonFormat7(`AWS::EC2::Subnet`.apply)
}

case class `AWS::EC2::SubnetRouteTableAssociation`(
  name:         String,
  SubnetId:     Token[ResourceRef[`AWS::EC2::Subnet`]],
  RouteTableId: Token[ResourceRef[`AWS::EC2::RouteTable`]],
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::EC2::SubnetRouteTableAssociation`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::SubnetRouteTableAssociation` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::SubnetRouteTableAssociation`] = jsonFormat4(`AWS::EC2::SubnetRouteTableAssociation`.apply)
}

case class `AWS::EC2::VPC`(name: String, CidrBlock: Token[CidrBlock], Tags: Seq[AmazonTag], EnableDnsSupport: Boolean = true, EnableDnsHostnames: Boolean = false,
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::EC2::VPC`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::VPC` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPC`] = jsonFormat6(`AWS::EC2::VPC`.apply)
}

case class `AWS::EC2::VPCPeeringConnection`(
  name: String,
  PeerVpcId: Token[String],
  Tags: Seq[AmazonTag],
  VpcId: Token[String],
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::EC2::VPCPeeringConnection`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::VPCPeeringConnection` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPCPeeringConnection`] = jsonFormat5(`AWS::EC2::VPCPeeringConnection`.apply)
}

sealed trait VPCGatewayOptions
object VPCGatewayOptions {
  implicit def toVPNGateway[T](v: T)(implicit t: T => Token[ResourceRef[`AWS::EC2::VPNGateway`]]) = VPNGateway(v)
  implicit def toInternetGateway[T](v: T)(implicit t: T => Token[ResourceRef[`AWS::EC2::InternetGateway`]]) = InternetGateway(v)
}

case class VPNGateway(v: Token[ResourceRef[`AWS::EC2::VPNGateway`]]) extends VPCGatewayOptions
case class InternetGateway(v: Token[ResourceRef[`AWS::EC2::InternetGateway`]]) extends VPCGatewayOptions

class `AWS::EC2::VPCGatewayAttachment` private (
  val name:              String,
  val VpcId:             Token[ResourceRef[`AWS::EC2::VPC`]],
  val VpnGatewayId:      Option[Token[ResourceRef[`AWS::EC2::VPNGateway`]]] = None,
  val InternetGatewayId: Option[Token[ResourceRef[`AWS::EC2::InternetGateway`]]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::EC2::VPCGatewayAttachment`]{
  private val asSeq = Seq(name, VpcId, VpnGatewayId, InternetGatewayId,
    Condition)

  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::EC2::VPCGatewayAttachment`(name, VpcId, VpnGatewayId, InternetGatewayId,
      newCondition )

}

object `AWS::EC2::VPCGatewayAttachment` extends DefaultJsonProtocol {

  private def writeField[T: JsonFormat](t: T) = {
    val writer = implicitly[JsonFormat[T]]
    writer match {
      case _: OptionFormat[_] if t == None => None
      case _ => Some(writer.write(t))
    }
  }

  // Because we dont want the default case class apply method without our checks
  implicit val format: JsonFormat[`AWS::EC2::VPCGatewayAttachment`] = new JsonFormat[`AWS::EC2::VPCGatewayAttachment`]{
    def write(p: `AWS::EC2::VPCGatewayAttachment`) = {
      JsObject(
        Map(
          "name"                   -> writeField(p.name),
          "VpcId"                  -> writeField(p.VpcId),
          "VpnGatewayId"           -> writeField(p.VpnGatewayId),
          "InternetGatewayId"      -> writeField(p.InternetGatewayId),
          "Condition"              -> writeField(p.Condition)
        ).filter(_._2.isDefined).mapValues(_.get)
      )
    }

    // TODO
    def read(json: JsValue) = ???
  }

  def apply(
    name: String,
    VpcId: Token[ResourceRef[`AWS::EC2::VPC`]],
    gatewayId: VPCGatewayOptions,
    Condition: Option[ConditionRef] = None
  ) =
    gatewayId match {
      case VPNGateway(e) =>   new `AWS::EC2::VPCGatewayAttachment`(name, VpcId, Some(e), None, Condition )
      case InternetGateway(e) =>   new `AWS::EC2::VPCGatewayAttachment`(name, VpcId,None, Some(e), Condition )
    }

}

case class `AWS::EC2::Volume` private (
  name:                 String,
  AvailabilityZone:     Token[String],
  Encrypted:            Option[Boolean],
  Iops:                 Option[Int],
  Size:                 Option[Token[Int]],
  SnapshotId:           Option[String],
  Tags:                 Seq[AmazonTag],
  VolumeType:           String,
  override val Condition: Option[ConditionRef] = None
                               ) extends Resource[`AWS::EC2::Volume`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::Volume` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::Volume`] = jsonFormat9(`AWS::EC2::Volume`.apply)

  //require( size >= 1 && size <= 16384 )
  def gp2(name: String, az: Token[String], size: Token[Int], tags: Seq[AmazonTag], encrypted: Boolean = true ) =
    `AWS::EC2::Volume`(name, az, Some(encrypted), None, Some(size), None, tags, "gp2")

  //require( size >= 4 && size <= 16384 )
  //require( iops <= size * 30 && iops >= 100 && iops <= 20000)
  def io2(name: String, az: Token[String], size: Token[Int], tags: Seq[AmazonTag], iops: Int, encrypted: Boolean = true ) =
    `AWS::EC2::Volume`(name, az, Some(encrypted), None, Some(size), None, tags, "io2")

  //require( size >= 1 && size <= 1024 )
  def standard(name: String, az: Token[String], size: Token[Int], tags: Seq[AmazonTag], encrypted: Boolean = true ) =
    `AWS::EC2::Volume`(name, az, Some(encrypted), None, Some(size), None, tags, "standard")

  def gp2Snapshot(name: String, az: Token[String], snapshotID: String, tags: Seq[AmazonTag], encrypted: Boolean = true ) =
    `AWS::EC2::Volume`(name, az, Some(encrypted), None, None, Some(snapshotID), tags, "gp2")

  def io2Snapshot(name: String, az: Token[String], snapshotID: String, tags: Seq[AmazonTag], encrypted: Boolean = true ) =
    `AWS::EC2::Volume`(name, az, Some(encrypted), None, None, Some(snapshotID), tags, "io2")

  def standardSnapshot(name: String, az: Token[String], snapshotID: String, tags: Seq[AmazonTag], encrypted: Boolean = true ) =
    `AWS::EC2::Volume`(name, az, Some(encrypted), None, None, Some(snapshotID), tags, "standard")
}

case class `AWS::EC2::VolumeAttachment`(
                                        name:         String,
                                        Device:       String,
                                        InstanceId:   ResourceRef[`AWS::EC2::Instance`],
                                        VolumeId:     ResourceRef[`AWS::EC2::Volume`],
                                        override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::EC2::VolumeAttachment`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::EC2::VolumeAttachment` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VolumeAttachment`] = jsonFormat5(`AWS::EC2::VolumeAttachment`.apply)
}
