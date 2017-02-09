package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.LambdaVpcConfig
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class FnSplit_UT extends FunSpec with Matchers {

  import Token._

  describe("Fn::Split"){

    it("Should serialize correctly with simple string arguments") {
      val split: TokenSeq[String] = `Fn::Split`(",", "one,two")

      val expected = JsObject(
        "Fn::Split" → JsArray(
          JsString(","),
          JsString("one,two")
        )
      )

      split.toJson should be(expected)
    }

    it("Should serialize correctly with complex argument types") {
      val split: TokenSeq[String] = `Fn::Split`(",", `Fn::ImportValue`("importKey"))

      val expected = JsObject(
        "Fn::Split" → JsArray(
          JsString(","),
          JsObject(
            "Fn::ImportValue" → JsString("importKey")
          )
        )
      )

      split.toJson should be(expected)
    }

    it("Should serialize correctly when used inside a resource") {
      val resource = LambdaVpcConfig(Seq("sg-groupid"), `Fn::Split`(",", `Fn::ImportValue`("importKey")))

      val expected = JsObject(
        "SecurityGroupIds" → JsArray(JsString("sg-groupid")),
        "SubnetIds" → JsObject(
          "Fn::Split" → JsArray(
            JsString(","),
            JsObject(
              "Fn::ImportValue" → JsString("importKey")
            )
          )
        )
      )

      resource.toJson should be(expected)
    }

    it("Should implicitly convert inside a seq") {
      val resource: TokenSeq[String] = Seq("test")

      val expected = JsArray(JsString("test"))

      resource.toJson should be(expected)
    }

    it("Should implicitly convert inside a seq with a function call") {
      val resource: TokenSeq[String] = Seq(`Fn::Join`(",", Seq("test")))

      val expected = JsArray(JsObject(
        "Fn::Join" → JsArray(JsString(","), JsArray(JsString("test")))
      ))

      resource.toJson should be(expected)
    }
  }
}
