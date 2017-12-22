package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.JsonString
import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class Events_UT extends FunSpec with Matchers {
  describe("RuleTarget") {
    it("Should serialize") {
      val t = RuleTarget(
        Arn = "arn",
        Id = "id",
        Input = Some(JsonString(JsObject(
          "a" -> JsNumber(5),
          "b" -> JsBoolean(false)
        ))))
      t.toJson.compactPrint shouldEqual """{"Arn":"arn","Id":"id","Input":"{\"a\":5,\"b\":false}"}"""
    }
  }
}
