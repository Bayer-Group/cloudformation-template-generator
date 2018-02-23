package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class Lambda_UT extends FunSpec with Matchers {
  describe("TracingConfig") {
    it("should serialize") {
      def test(t: TracingConfig, rep: String) = {
        t.toJson shouldEqual JsString(rep)
        s""" "${rep}" """.parseJson.convertTo[TracingConfig] shouldEqual t
      }
      test(TracingConfig.Active, "Active")
      test(TracingConfig.PassThrough, "PassThrough")
    }
  }
}
