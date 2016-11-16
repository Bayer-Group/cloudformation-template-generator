package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.AmazonFunctionCall._
import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.resource._
import Token._
import org.scalatest.{FunSpec, Matchers}
import spray.json._
import DefaultJsonProtocol._

/**
 * Created by Ryan Richt on 2/26/15
 */
class IntrinsicFunctions_UT extends FunSpec with Matchers {

  describe("Fn::Sub"){

    it("no args"){

      val test: Token[String] = `Fn::Sub`(s"This is a $${test} template")

      val expected = JsObject(
        "Fn::Sub"-> JsString(s"This is a $${test} template")
      )

      test.toJson should be(expected)
    }

    it("one arg"){

      val test: Token[String] = `Fn::Sub`(
        s"This is a $${test} template",
        Some(Map("test" -> "value"))
      )

      val expected = JsObject(
        "Fn::Sub"-> JsArray(
          JsString(s"This is a $${test} template"),
          JsObject("test" -> JsString("value"))
        )
      )

      test.toJson should be(expected)
    }

    it("two args"){

      val test: Token[String] = `Fn::Sub`(
        s"This is a $${test} template",
        Some(Map("test" -> "value", "test2" -> "value2"))
      )

      val expected = JsObject(
        "Fn::Sub"-> JsArray(
          JsString(s"This is a $${test} template"),
          JsObject("test" -> JsString("value"), "test2" -> JsString("value2"))
        )
      )

      test.toJson should be(expected)
    }
  }

  describe("Fn::ImportValue") {
    it("should serialize with static string") {
      val test: Token[String] = `Fn::ImportValue`("Test-Import-Value")

      val expected = JsObject(
        "Fn::ImportValue" -> JsString("Test-Import-Value")
      )
      test.toJson should be(expected)
    }

    it("should serialize with an embedded function") {
      val test: Token[String] = `Fn::ImportValue`(`Fn::Join`("", Seq("str1", "str2")))

      val expected = JsObject(
        "Fn::ImportValue" -> JsObject("Fn::Join" -> JsArray(
          JsString(""),
          JsArray(JsString("str1"), JsString("str2"))
        )
      ))
      test.toJson should be(expected)
    }
  }
}
