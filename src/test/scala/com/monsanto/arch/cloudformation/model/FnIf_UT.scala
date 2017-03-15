package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import org.scalatest.{Matchers, FunSpec}
import spray.json._

/**
 * Created by Ryan Richt on 2/26/15
 */
class FnIf_UT extends FunSpec with Matchers {

  describe("Fn::If"){

    it("Should serialize correctly with complex argument types"){

      val cond = Condition(
        name = "ServiceELBSSLCertNameIsNotDefined",
        function = `Fn::Equals`(a = StringToken("true"), b = StringToken("false"))
      )

      val vpcToken = UNSAFEToken[ResourceRef[`AWS::EC2::VPC`]]("vpc-b5f389d0")

      val gatewayELBSecGroupResource = `AWS::EC2::SecurityGroup`(
        "GatewayELBSecurityGroup",
        GroupDescription = "Rules for allowing access to/from service gateway ELB",
        VpcId = vpcToken,
        SecurityGroupEgress = None,
        SecurityGroupIngress = Some(Seq(
          CidrIngressSpec(
            IpProtocol = "tcp",
            CidrIp = CidrBlock(0, 0, 0, 0, 32),
            FromPort = "80",
            ToPort = "80"
          ),
          CidrIngressSpec(
            IpProtocol = "tcp",
            CidrIp = CidrBlock(0, 0, 0, 0, 32),
            FromPort = "443",
            ToPort = "443"
          )
        )
        ),
        Tags = Seq[AmazonTag](),
        Condition = Some(ConditionRef(cond))
      )

      val test: Token[ResourceRef[`AWS::EC2::SecurityGroup`]] = `Fn::If`[ResourceRef[`AWS::EC2::SecurityGroup`]](
        ConditionRef(cond),
        ResourceRef(gatewayELBSecGroupResource),
        ResourceRef(gatewayELBSecGroupResource)
      )

      val expected = JsObject(
        "Fn::If"-> JsArray(
          JsString("ServiceELBSSLCertNameIsNotDefined"),
          JsObject("Ref" -> JsString("GatewayELBSecurityGroup")),
          JsObject("Ref" -> JsString("GatewayELBSecurityGroup"))
        )
      )

      test.toJson should be(expected)
    }
  }
}
