package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ElasticLoadBalancingV2_UT extends FunSpec with Matchers {

  private val loadBalancer = `AWS::ElasticLoadBalancingV2::LoadBalancer`(
    name = "testy-alb",
    Subnets = Seq(UNSAFEToken[ResourceRef[`AWS::EC2::Subnet`]]("subnet-12345"), UNSAFEToken[ResourceRef[`AWS::EC2::Subnet`]]("subnet-67890")),
    LoadBalancerAttributes = Seq(LoadBalancerAttribute.`idle_timeout.timeout_seconds`("300")),
    Name = Some("Testy-ALB"),
    Scheme = ELBScheme.internal,
    SecurityGroups = Seq(UNSAFEToken[ResourceRef[`AWS::EC2::SecurityGroup`]]("sg-12345"), UNSAFEToken[ResourceRef[`AWS::EC2::SecurityGroup`]]("sg-67890"))
  )

  private val targetGroup = `AWS::ElasticLoadBalancingV2::TargetGroup`(
    name = "testy-target-group",
    Protocol = ALBProtocol.HTTPS,
    Port = 80,
    VpcId = UNSAFEToken[ResourceRef[`AWS::EC2::VPC`]]("vpc-12345"),
    Matcher = Matcher("200-201"),
    HealthCheckIntervalSeconds = Some(11),
    HealthCheckPath = Some("/hi"),
    HealthCheckPort = Some("99"),
    HealthCheckProtocol = ALBProtocol.HTTP,
    HealthCheckTimeoutSeconds = Some(12),
    HealthyThresholdCount = Some(2),
    UnhealthyThresholdCount = Some(3),
    Name = Some("testy-target-group-name"),
    TargetGroupAttributes = Seq(TargetGroupAttribute.`deregistration_delay.timeout_seconds`("30")),
    Targets = Seq(TargetDescription("ec2-12345", Some(46)))
  )

  private val httpListener = `AWS::ElasticLoadBalancingV2::Listener`.forHttp(
    name = "testy-http-listener",
    DefaultActions = Seq(ListenerAction.forward(targetGroup.arn)),
    LoadBalancerArn = loadBalancer.arn
  )

  private val httpsListener = `AWS::ElasticLoadBalancingV2::Listener`.forHttps(
    name = "testy-https-listener",
    DefaultActions = Seq(ListenerAction.forward(targetGroup.arn)),
    LoadBalancerArn = loadBalancer.arn,
    Certificates = Seq(Certificate("cert-1234")),
    SslPolicy = Some(ELBSecurityPolicy.`ELBSecurityPolicy-2016-08`)
  )

  private val listenerRule = `AWS::ElasticLoadBalancingV2::ListenerRule`(
    name = "testy-listener-rule",
    Actions = Seq(ListenerAction.forward(targetGroup.arn)),
    Conditions = Seq(RuleCondition.`path-pattern`(Seq("/"))),
    ListenerArn = httpListener.arn,
    Priority = 1
  )

  describe("AWS::ElasticLoadBalancingV2::LoadBalancer") {
    it("should serialize correctly") {
      val expectedJson = JsObject(
        "testy-alb" -> JsObject(
          "Type" -> JsString("AWS::ElasticLoadBalancingV2::LoadBalancer"),
          "Properties" -> JsObject(
            "Name" -> JsString("Testy-ALB"),
            "Scheme" -> JsString("internal"),
            "Subnets" -> JsArray(
              JsString("subnet-12345"), JsString("subnet-67890")
            ),
            "SecurityGroups" -> JsArray(
              JsString("sg-12345"), JsString("sg-67890")
            ),
            "LoadBalancerAttributes" -> JsArray(
              JsObject(
                "Key" -> JsString("idle_timeout.timeout_seconds"),
                "Value" -> JsString("300")
              )
            )
          )
        )
      )

      Seq[Resource[_]](loadBalancer).toJson shouldBe expectedJson
    }
  }

  describe("AWS::ElasticLoadBalancingV2::Listener") {
    it("forHttp should serialize correctly") {
      val expectedJson = JsObject(
        "testy-http-listener" -> JsObject(
          "Type" -> JsString("AWS::ElasticLoadBalancingV2::Listener"),
          "Properties" -> JsObject(
            "Port" -> JsNumber(80),
            "Protocol" -> JsString("HTTP"),
            "LoadBalancerArn" -> JsObject("Ref" -> JsString("testy-alb")),
            "DefaultActions" -> JsArray(
              JsObject(
                "TargetGroupArn" -> JsObject("Ref" -> JsString("testy-target-group")),
                "Type" -> JsString("forward")
              )
            )
          )
        )
      )

      Seq[Resource[_]](httpListener).toJson shouldBe expectedJson
    }

    it("forHttps should serialize correctly") {
      val expectedJson = JsObject(
        "testy-https-listener" -> JsObject(
          "Type" -> JsString("AWS::ElasticLoadBalancingV2::Listener"),
          "Properties" -> JsObject(
            "Port" -> JsNumber(443),
            "Protocol" -> JsString("HTTPS"),
            "SslPolicy" -> JsString("ELBSecurityPolicy-2016-08"),
            "LoadBalancerArn" -> JsObject("Ref" -> JsString("testy-alb")),
            "DefaultActions" -> JsArray(
              JsObject(
                "TargetGroupArn" -> JsObject("Ref" -> JsString("testy-target-group")),
                "Type" -> JsString("forward")
              )
            ),
            "Certificates" -> JsArray(
              JsObject(
                "CertificateArn" -> JsString("cert-1234")
              )
            )
          )
        )
      )

      Seq[Resource[_]](httpsListener).toJson shouldBe expectedJson
    }

    it("should prevent incorrect construction") {
      assertThrows[IllegalArgumentException] {
        `AWS::ElasticLoadBalancingV2::Listener`(
          name = "testy-http-listener",
          DefaultActions = Seq(ListenerAction.forward(targetGroup.arn)),
          LoadBalancerArn = loadBalancer.arn,
          Port = 12345,
          Protocol = ALBProtocol.HTTPS
        )
      }

      assertThrows[IllegalArgumentException] {
        `AWS::ElasticLoadBalancingV2::Listener`(
          name = "testy-http-listener",
          DefaultActions = Seq(ListenerAction.forward(targetGroup.arn)),
          LoadBalancerArn = loadBalancer.arn,
          Port = 12345,
          Protocol = ALBProtocol.HTTPS,
          Certificates = Some(Seq.empty)
        )
      }

      assertThrows[IllegalArgumentException] {
        `AWS::ElasticLoadBalancingV2::Listener`(
          name = "testy-http-listener",
          DefaultActions = Seq(ListenerAction.forward(targetGroup.arn)),
          LoadBalancerArn = loadBalancer.arn,
          Port = 12345,
          Protocol = ALBProtocol.HTTPS,
          SslPolicy = ELBSecurityPolicy.`ELBSecurityPolicy-2016-08`
        )
      }
    }
  }

  describe("AWS::ElasticLoadBalancingV2::ListenerRule") {
    it("should serialize correctly") {
      val expectedJson = JsObject(
        "testy-listener-rule" -> JsObject(
          "Type" -> JsString("AWS::ElasticLoadBalancingV2::ListenerRule"),
          "Properties" -> JsObject(
            "Priority" -> JsNumber(1),
            "ListenerArn" -> JsObject("Ref" -> JsString("testy-http-listener")),
            "Actions" -> JsArray(
              JsObject(
                "TargetGroupArn" -> JsObject("Ref" -> JsString("testy-target-group")),
                "Type" -> JsString("forward")
              )
            ),
            "Conditions" -> JsArray(
              JsObject(
                "Field" -> JsString("path-pattern"),
                "Values" -> JsArray(JsString("/"))
              )
            )
          )
        )
      )

      Seq[Resource[_]](listenerRule).toJson shouldBe expectedJson
    }
  }

  describe("AWS::ElasticLoadBalancingV2::TargetGroup") {
    it("should serialize correctly") {
      val expectedJson = JsObject(
        "testy-target-group" -> JsObject(
          "Type" -> JsString("AWS::ElasticLoadBalancingV2::TargetGroup"),
          "Properties" -> JsObject(
            "HealthCheckPort" -> JsString("99"),
            "Name" -> JsString("testy-target-group-name"),
            "UnhealthyThresholdCount" -> JsNumber(3),
            "HealthyThresholdCount" -> JsNumber(2),
            "HealthCheckProtocol" -> JsString("HTTP"),
            "HealthCheckIntervalSeconds" -> JsNumber(11),
            "HealthCheckTimeoutSeconds" -> JsNumber(12),
            "Port" -> JsNumber(80),
            "HealthCheckPath" -> JsString("/hi"),
            "Protocol" -> JsString("HTTPS"),
            "VpcId" -> JsString("vpc-12345"),
            "TargetGroupAttributes" -> JsArray(
              JsObject(
                "Key" -> JsString("deregistration_delay.timeout_seconds"),
                "Value" -> JsString("30")
              )
            ),
            "Targets" -> JsArray(
              JsObject(
                "Id" -> JsString("ec2-12345"),
                "Port" -> JsNumber(46)
              )
            ),
            "Matcher" -> JsObject(
              "HttpCode" -> JsString("200-201")
            )
          )
        )
      )

      Seq[Resource[_]](targetGroup).toJson shouldBe expectedJson
    }
  }

  describe("ELBSecurityPolicy") {
    import ELBSecurityPolicy._

    case class DeserializeHelper(secPol: ELBSecurityPolicy)
    implicit val deserializeHelperFmt = jsonFormat1(DeserializeHelper.apply)
    implicit def strToHelper(str: String): DeserializeHelper = str.parseJson.convertTo[DeserializeHelper]

    it("should de/serialize defined values") {
      `ELBSecurityPolicy-2016-08`.asInstanceOf[ELBSecurityPolicy].toJson shouldBe JsString("ELBSecurityPolicy-2016-08")
      """{"secPol": "ELBSecurityPolicy-2016-08"}""".secPol shouldBe `ELBSecurityPolicy-2016-08`

      `ELBSecurityPolicy-2015-05`.asInstanceOf[ELBSecurityPolicy].toJson shouldBe JsString("ELBSecurityPolicy-2015-05")
      """{"secPol": "ELBSecurityPolicy-2015-05"}""".secPol shouldBe `ELBSecurityPolicy-2015-05`

      `ELBSecurityPolicy-TLS-1-1-2017-01`.asInstanceOf[ELBSecurityPolicy].toJson shouldBe JsString("ELBSecurityPolicy-TLS-1-1-2017-01")
      """{"secPol": "ELBSecurityPolicy-TLS-1-1-2017-01"}""".secPol shouldBe `ELBSecurityPolicy-TLS-1-1-2017-01`

      `ELBSecurityPolicy-TLS-1-2-2017-01`.asInstanceOf[ELBSecurityPolicy].toJson shouldBe JsString("ELBSecurityPolicy-TLS-1-2-2017-01")
      """{"secPol": "ELBSecurityPolicy-TLS-1-2-2017-01"}""".secPol shouldBe `ELBSecurityPolicy-TLS-1-2-2017-01`

    }

    it("should de/serialize custom values") {
      Custom("Jim_Bob").asInstanceOf[ELBSecurityPolicy].toJson shouldBe JsString("Jim_Bob")
      """{"secPol": "Jim_Bob"}""".secPol shouldBe Custom("Jim_Bob")
    }
  }
}
