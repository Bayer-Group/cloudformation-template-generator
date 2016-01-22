package com.monsanto.arch.cloudformation.model

import org.scalatest.{FunSpec, Matchers}
import spray.json._
import DefaultJsonProtocol._

class FnGetAZs_UT extends FunSpec with Matchers {

  describe("Fn::GetAZs") {
    it("should serialize correctly") {
      val p = StringParameter("pAZ")
      val c = Condition("cAZ", `Fn::Equals`(ParameterRef(p), ""))
      val ps = StringListParameter("s")
      val az: Token[String] = `Fn::If`[String](
        ConditionRef(c),
        `Fn::Select`(StringBackedInt(0), `Fn::GetAZs`(`AWS::Region`)),
        ParameterRef(p)
      )
      val expected = JsObject(
        "Fn::If"-> JsArray(
          JsString("cAZ"),
          JsObject("Fn::Select" -> JsArray(
            JsString("0"),
            JsObject("Fn::GetAZs" -> JsObject("Ref" -> JsString("AWS::Region")))
          )),
          JsObject("Ref" -> JsString("pAZ"))
        )
      )
      az.toJson should be(expected)
    }
  }

}
