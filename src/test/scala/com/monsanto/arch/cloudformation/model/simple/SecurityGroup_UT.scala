package com.monsanto.arch.cloudformation.model.simple

import com.monsanto.arch.cloudformation.model.resource._
import com.monsanto.arch.cloudformation.model.simple.Builders._
import org.scalatest.{Matchers, FunSpec}
import PortProtocolFragment._
import spray.json._

class SecurityGroup_UT extends FunSpec with Matchers {
  describe("SecurityGroup Builder") {
    implicit val vpc: `AWS::EC2::VPC` = `AWS::EC2::VPC`("VPC", CidrBlock(10,10,10,10,16), Seq())
    val securityGroupA = securityGroup("A", "Group A")
    val securityGroupB = securityGroup("B", "Group B")

    it("should create SSH ingress rule from A to B") {
      val ingress  = securityGroupA ->- 22 ->- securityGroupB
      val expected = JsObject(
        "AToBIngressPrototcpFromPort22ToPort22" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("22"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("tcp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("22")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create UDP ingress rule from A to B") {
      val ingress  = securityGroupA ->- 53 / UDP ->- securityGroupB
      val expected = JsObject(
        "AToBIngressProtoudpFromPort53ToPort53" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("53"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("udp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("53")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create all protocols ingress rule from A to B") {
      val ingress  = securityGroupA ->- 21 / ALL ->- securityGroupB
      val expected = JsObject(
        "AToBIngressProtoNeg1FromPort21ToPort21" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("21"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("-1"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("21")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create ICMP ingress rule from A to B") {
      val ingress  = securityGroupA ->- -1 / ICMP ->- securityGroupB
      val expected = JsObject(
        "AToBIngressProtoicmpFromPortNeg1ToPortNeg1" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("-1"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("icmp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("-1")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create range ingress rule from A to B") {
      val ingress  = securityGroupA ->- (1 to 65536) ->- securityGroupB
      val expected = JsObject(
        "AToBIngressPrototcpFromPort1ToPort65536" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("1"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("tcp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("65536")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create range UDP ingress rule from A to B") {
      val ingress  = securityGroupA ->- (1 to 65536) / UDP ->- securityGroupB
      val expected = JsObject(
        "AToBIngressProtoudpFromPort1ToPort65536" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("1"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("udp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("65536")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create Sequence ingress rule from A to B") {
      val ingress  = securityGroupA ->- Seq(22, 5601) ->- securityGroupB
      val expected = JsObject(
        "AToBIngressPrototcpFromPort22ToPort22" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("22"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("tcp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("22")
          )
        ),
        "AToBIngressPrototcpFromPort5601ToPort5601" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("5601"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("tcp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("5601")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }

    it("should create many ingress rules") {
      val ingress = securityGroupA ->- Seq(22, (5601 to 5602) / UDP, -1 / ICMP, 14 / TCP) ->- securityGroupB
      val expected = JsObject(
        "AToBIngressPrototcpFromPort22ToPort22" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("22"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("tcp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("22")
          )
        ),
        "AToBIngressProtoudpFromPort5601ToPort5602" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("5601"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("udp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("5602")
          )
        ),
        "AToBIngressProtoicmpFromPortNeg1ToPortNeg1" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("-1"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("icmp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("-1")
          )
        ),
        "AToBIngressPrototcpFromPort14ToPort14" -> JsObject(
          "Type" -> JsString("AWS::EC2::SecurityGroupIngress"),
          "Properties" -> JsObject(
            "FromPort" -> JsString("14"),
            "GroupId" -> JsObject("Ref" -> JsString("B")),
            "IpProtocol" -> JsString("tcp"),
            "SourceSecurityGroupId" -> JsObject("Ref" -> JsString("A")),
            "ToPort" -> JsString("14")
          )
        )
      )
      val resources = Seq[Resource[_]]() ++ ingress
      resources.toJson should be (expected)
    }
  }
}
