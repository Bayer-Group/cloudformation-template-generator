package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.resource._
import org.scalatest.{FunSpec, Matchers}
import spray.json._
import DefaultJsonProtocol._
/**
 * Created by Ryan Richt on 2/26/15
 */
class ConditionFunctionNestable_UT extends FunSpec with Matchers {

  describe("Fn::Not(Fn::Equals)"){

    it("Should serialize correctly"){

      val test: Token[String] = `Fn::Not`(`Fn::Equals`("hello", "there"))

      val expected = JsObject(
        "Fn::Not"-> JsArray(
          JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there")))
        )
      )
      test.toJson should be(expected)
    }
  }

  describe("Fn::And(Fn::Equals, Fn::Equals)"){

    it("Should serialize correctly"){

      val test: Token[String] = `Fn::And`(Seq(
        `Fn::Equals`("hello", "there"),
        `Fn::Equals`("is it me", "you're looking for"))
      )
      val expected = JsObject(
        "Fn::And"-> JsArray(
          JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there"))),
          JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
        )
      )
      test.toJson should be(expected)
    }
  }

  describe("Fn::Or(Fn::Equals, Fn::Equals)"){

    it("Should serialize correctly"){

      val test: Token[String] = `Fn::Or`(Seq(
        `Fn::Equals`("hello", "there"),
        `Fn::Equals`("is it me", "you're looking for"))
      )
      val expected = JsObject(
        "Fn::Or"-> JsArray(
          JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there"))),
          JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
        )
      )
      test.toJson should be(expected)
    }
  }


  describe("Fn::Or(Condition, Fn::And(Fn::Equals, Fn::Equals))"){

    it("Should serialize correctly"){
      val cond = ConditionFnRef(Condition(name="blah", function = `Fn::Equals`("hello", "there")))

      val test: Token[String] = `Fn::Or`(Seq(
        cond,
        `Fn::And`(Seq(
          `Fn::Equals`("hello", "there"),
          `Fn::Equals`("is it me", "you're looking for"))
        ))
      )
      val expected =
        JsObject("Fn::Or" -> JsArray(
          JsObject("Condition" -> JsString("blah")),
          JsObject("Fn::And"-> JsArray(
            JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there"))),
            JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
          )
        ))
      )
      test.toJson should be(expected)
    }
  }
  describe("Fn::Not(Fn::Or(Fn::Equals, Fn::Equals))"){

    it("Should serialize correctly"){

      val test: Token[String] = `Fn::Not`(`Fn::Or`(Seq(
        `Fn::Equals`("hello", "there"),
        `Fn::Equals`("is it me", "you're looking for"))
      ))
      val expected = JsObject("Fn::Not" -> JsArray(
        JsObject(
          "Fn::Or"-> JsArray(
            JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there"))),
            JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
          )
        )
      ))
      test.toJson should be(expected)
    }
  }

  describe("Fn::Not(Fn::And(Fn::Equals, Fn::Equals))"){

    it("Should serialize correctly"){

      val test: Token[String] = `Fn::Not`(`Fn::And`(Seq(
        `Fn::Equals`("hello", "there"),
        `Fn::Equals`("is it me", "you're looking for"))
      ))
      val expected = JsObject("Fn::Not" -> JsArray(
        JsObject(
          "Fn::And"-> JsArray(
            JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there"))),
            JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
          )
        )
      ))
      test.toJson should be(expected)
    }
  }

  describe("Fn::Or(Condition, Fn::Equals)"){

    it("Should serialize correctly"){

      val cond = ConditionFnRef(Condition(name="blah", function = `Fn::Equals`("hello", "there")))
      val test: Token[String] = `Fn::Or`(Seq(
        cond,
        `Fn::Equals`("is it me", "you're looking for"))
      )
      val expected = JsObject(
        "Fn::Or"-> JsArray(
          JsObject("Condition" -> JsString("blah")),
          JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
        )
      )
      test.toJson should be(expected)
    }
  }

  describe("Fn::And(Condition, Fn::Equals)"){

    it("Should serialize correctly"){

      val cond = ConditionFnRef(Condition(name="blah", function = `Fn::Equals`("hello", "there")))
      val test: Token[String] = `Fn::And`(Seq(
        cond,
        `Fn::Equals`("is it me", "you're looking for"))
      )
      val expected = JsObject(
        "Fn::And"-> JsArray(
          JsObject("Condition" -> JsString("blah")),
          JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
        )
      )
      test.toJson should be(expected)
    }
  }

  describe("Fn::And(Fn::Not(Fn::Equals), Fn::Equals)"){

    it("Should serialize correctly"){

      val test: Token[String] = `Fn::And`(Seq(
        `Fn::Not`(`Fn::Equals`("hello", "there")),
        `Fn::Equals`("is it me", "you're looking for"))
      )
      val expected = JsObject(
        "Fn::And"-> JsArray(
          JsObject("Fn::Not" -> JsArray(
            JsObject("Fn::Equals" -> JsArray(JsString("hello"), JsString("there"))))
          ),
          JsObject("Fn::Equals" -> JsArray(JsString("is it me"), JsString("you're looking for")))
        )
      )
      test.toJson should be(expected)
    }
  }
}
