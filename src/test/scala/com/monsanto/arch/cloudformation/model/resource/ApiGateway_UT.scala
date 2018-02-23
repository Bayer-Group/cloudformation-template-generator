package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ResourceRef, Template, Token}
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ApiGateway_UT extends FunSpec with Matchers {
  val api = `AWS::ApiGateway::RestApi`(
    name = "RestApi",
    Name = Token.fromString("RestApi")
  )

  val stage = `AWS::ApiGateway::Stage`(
    name = "Stage",
    DeploymentId = Token.fromString("123"),
    Variables = Map()
  )

  val apiKey = `AWS::ApiGateway::ApiKey`(
    name = "ApiKey"
  )

  val usagePlan = `AWS::ApiGateway::UsagePlan`(
    name = "UsagePlan",
    ApiStages = Some(Seq(
      ApiStage(
        ResourceRef(api),
        ResourceRef(stage)
      )
    )),
    Description = Some("UsagePlanDescription"),
    Quota = Some(QuotaSettings(
      Limit = Some(1),
      Offset = Some(2),
      Period = Some(Period.WEEK))
    ),
    Throttle = Some(ThrottleSettings(
      BurstLimit = Some(1),
      RateLimit = Some(2.0)
    )),
    UsagePlanName = Some(Token.fromString("UsagePlanName"))
  )

  val usagePlanKey = `AWS::ApiGateway::UsagePlanKey`(
    name = "UsagePlanKey",
    KeyId = ResourceRef(apiKey),
    KeyType = UsagePlanKeyType.API_KEY,
    UsagePlanId = ResourceRef(usagePlan)
  )

  describe("UsagePlan"){
    it ("should serialize as expected") {
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "UsagePlan": {
          |      "Properties": {
          |        "ApiStages": [{"ApiId": {"Ref": "RestApi"}, "Stage": {"Ref": "Stage"}}],
          |        "Description": "UsagePlanDescription",
          |        "Quota": {"Limit": 1, "Offset": 2, "Period": "WEEK"},
          |        "Throttle": {"BurstLimit": 1, "RateLimit": 2.0},
          |        "UsagePlanName": "UsagePlanName"
          |      },
          |      "Type": "AWS::ApiGateway::UsagePlan"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(usagePlan).toJson should be (expectedJson)
    }
  }

  describe("UsagePlanKey"){
    it ("should serialize as expected") {
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "UsagePlanKey": {
          |      "Properties": {
          |        "KeyId": {"Ref": "ApiKey"},
          |        "KeyType": "API_KEY",
          |        "UsagePlanId": {"Ref": "UsagePlan"}
          |      },
          |      "Type": "AWS::ApiGateway::UsagePlanKey"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(usagePlanKey).toJson should be (expectedJson)
    }
  }
}
