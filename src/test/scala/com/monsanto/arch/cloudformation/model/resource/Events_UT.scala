package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ Token, `Fn::Sub` }
import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class Events_UT extends FunSpec with Matchers {
  describe("RuleTarget") {
    it("Should serialize") {
      val t = RuleTarget(
        Arn = "arn",
        Id = "id",
        Input = Some(JsObject(
          "a" -> JsNumber(5),
          "b" -> JsBoolean(false)
        ).compactPrint))
      t.toJson.compactPrint shouldEqual
        raw"""{"Arn":"arn","Id":"id","Input":"{\"a\":5,\"b\":false}"}"""
    }

    it("Should serialize sub") {
      val sub: Token[String] =
        `Fn::Sub`(
          JsObject(
            "a" -> JsString(raw"$${AWS::Region}"),
            "b" -> JsString(raw"$${FOO}")
          ).compactPrint,
          Some(Map("FOO" -> "BAR"))
        )
      val t = RuleTarget(
        Arn = "arn",
        Id = "id",
        Input = Some(sub)
      )
      t.toJson.compactPrint shouldEqual
        raw"""{"Arn":"arn","Id":"id","Input":{"Fn::Sub":["{\"a\":\"$${AWS::Region}\",\"b\":\"$${FOO}\"}",{"FOO":"BAR"}]}}"""
    }
  }
}
