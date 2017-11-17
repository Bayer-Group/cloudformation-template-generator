package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ Token, `Fn::GetArtifactAtt`, `Fn::GetParam` }
import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class CodePipeline_UT extends FunSpec with Matchers {
  describe("Fns"){
    it ("Fn::GetParam should serialize as expected") {
      val s: Token[String] = `Fn::GetParam`("foo", "bar", "baz")
      val expected = JsObject("Fn::GetParam" -> JsArray(JsString("foo"), JsString("bar"), JsString("baz")))
      s.toJson should be (expected)
    }

    it ("Fn::GetArtifactAtt should serialize as expected") {
      val s: Token[String] = `Fn::GetArtifactAtt`("foo", "bar")
      val expected = JsObject("Fn::GetArtifactAtt" -> JsArray(JsString("foo"), JsString("bar")))
      s.toJson should be (expected)
    }
  }
}
