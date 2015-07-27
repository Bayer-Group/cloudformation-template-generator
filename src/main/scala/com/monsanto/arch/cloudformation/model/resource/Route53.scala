package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

class `AWS::Route53::RecordSet` private (
  val name:            String,
  val RecordName:      Token[String], // The subdomain, with a . after it.
  val RecordType:      Route53RecordType,
  val HostedZoneName:  Option[Token[String]], // The parent domain, with a . after it.  Must be route 53 managed already.
  val HostedZoneId:    Option[Token[String]], // the id of the hosted zone
  val ResourceRecords: Option[Seq[Token[String]]] = None,
  val TTL:             Option[Token[String]] = None,
  val AliasTarget:     Option[Route53AliasTarget] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::Route53::RecordSet`]{

  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::Route53::RecordSet`(name, RecordName, RecordType, HostedZoneName, HostedZoneId, ResourceRecords, TTL, AliasTarget, newCondition)
}
object `AWS::Route53::RecordSet` extends DefaultJsonProtocol {

  private def writeField[T: JsonFormat](t: T) = {
    val writer = implicitly[JsonFormat[T]]
    writer match {
      case _: OptionFormat[_] if t == None => None
      case _ => Some(writer.write(t))
    }
  }

  // Because we dont want the default case class apply method without our checks
  implicit val format: JsonFormat[`AWS::Route53::RecordSet`] = new JsonFormat[`AWS::Route53::RecordSet`]{
    def write(p: `AWS::Route53::RecordSet`) = {
      JsObject(
        Map(
          "name"            -> writeField(p.name),
          "Name"            -> writeField(p.RecordName),
          "Type"            -> writeField(p.RecordType),
          "HostedZoneName"  -> writeField(p.HostedZoneName),
          "HostedZoneId"    -> writeField(p.HostedZoneId),
          "ResourceRecords" -> writeField(p.ResourceRecords),
          "TTL"             -> writeField(p.TTL),
          "AliasTarget"     -> writeField(p.AliasTarget),
          "Condition"       -> writeField(p.Condition)
        ).filter(_._2.isDefined).mapValues(_.get)
      )
    }
    
    def read(json: JsValue) = ???
  }

  def generalRecord(
      name:            String,
      RecordName:      Token[String], // The subdomain, with a . after it.
      RecordType:      Route53RecordType,
      HostedZoneName:  Token[String], // The parent domain, with a . after it.  Must be route 53 managed already.
      ResourceRecords: Seq[Token[String]],
      TTL:             Token[String],
      Condition:       Option[ConditionRef] = None
    ) = new `AWS::Route53::RecordSet`(name, RecordName, RecordType, Some(HostedZoneName), None, Some(ResourceRecords), Some(TTL), Condition = Condition)

  def generalRecordByID(
                     name:            String,
                     RecordName:      Token[String], // The subdomain, with a . after it.
                     RecordType:      Route53RecordType,
                     HostedZoneID:    Token[String], 
                     ResourceRecords: Seq[Token[String]],
                     TTL:             Token[String],
                     Condition:       Option[ConditionRef] = None
     ) = new `AWS::Route53::RecordSet`(name, RecordName, RecordType, None, Some(HostedZoneID), Some(ResourceRecords), Some(TTL), Condition = Condition)


  def aliasRecord(
      name:           String,
      RecordName:     Token[String], // The subdomain, with a . after it.
      HostedZoneName: Token[String], // The parent domain, with a . after it.  Must be route 53 managed already.
      AliasTarget:    Route53AliasTarget,
      Condition: Option[ConditionRef] = None
    ) = new `AWS::Route53::RecordSet`(name, RecordName, Route53RecordType.A, Some(HostedZoneName), None, None, None, Some(AliasTarget), Condition)
}

case class `AWS::Route53::HostedZone`(
                                       name:                    String,
                                       Name:                    Token[String],
                                       VPCs:                    Seq[HostedZoneVPC],
                                       HostedZoneConfig:        HostedZoneConfig,
                                       override val Condition:  Option[ConditionRef] = None
                                       ) extends Resource[`AWS::Route53::HostedZone`]{
  def when(newCondition: Option[ConditionRef] = Condition) = this.copy(Condition = newCondition)
}
object `AWS::Route53::HostedZone` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Route53::HostedZone`] = jsonFormat5(`AWS::Route53::HostedZone`.apply)
}

case class HostedZoneVPC(VPCId: Token[String], VPCRegion:Token[String])
object HostedZoneVPC extends DefaultJsonProtocol {
  implicit val format: JsonFormat[HostedZoneVPC] = jsonFormat2(HostedZoneVPC.apply)
}

case class HostedZoneConfig(Comment: Token[String])
object HostedZoneConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[HostedZoneConfig] = jsonFormat1(HostedZoneConfig.apply)
}


sealed trait Route53RecordType

object Route53RecordType extends DefaultJsonProtocol {

  case object A     extends Route53RecordType
  case object CNAME extends Route53RecordType
  case object AAAA  extends Route53RecordType
  case object MX    extends Route53RecordType
  case object NS    extends Route53RecordType
  case object PTR   extends Route53RecordType
  case object SOA   extends Route53RecordType
  case object SPF   extends Route53RecordType
  case object SRV   extends Route53RecordType
  case object TXT   extends Route53RecordType

  implicit val format: JsonFormat[Route53RecordType] = new JsonFormat[Route53RecordType] {
    def write(f: Route53RecordType) = JsString(f.toString)

    def read(value: JsValue) = {
      value.toString() match {
        case "A"     => Route53RecordType.A
        case "CNAME" => Route53RecordType.CNAME
        case "AAAA"  => Route53RecordType.AAAA
        case "MX"    => Route53RecordType.MX
        case "NS"    => Route53RecordType.NS
        case "PTR"   => Route53RecordType.PTR
        case "SOA"   => Route53RecordType.SOA
        case "SPF"   => Route53RecordType.SPF
        case "SRV"   => Route53RecordType.SRV
        case "TXT"   => Route53RecordType.TXT
      }
    }
  }
}

case class Route53AliasTarget(
  DNSName:              Token[String],
  HostedZoneId:         Token[String],
  EvaluateTargetHealth: Boolean
  )

object Route53AliasTarget extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Route53AliasTarget] = jsonFormat3(Route53AliasTarget.apply)
}
