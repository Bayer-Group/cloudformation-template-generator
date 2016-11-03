package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class Kinesis_UT extends FunSpec with Matchers {
  describe("Stream") {
    it("should write a valid Kinesis stream") {
      val stream = `AWS::Kinesis::Stream`("stream", 1, Seq(AmazonTag("Name", "stream")))
      stream.toJson shouldEqual JsObject(Map(
        "name" -> JsString("stream"),
        "ShardCount" -> JsNumber(1),
        "Tags" -> JsArray(JsObject(Map("Key" -> JsString("Name"), "Value" -> JsString("stream"))))
      ))
    }
  }
}
