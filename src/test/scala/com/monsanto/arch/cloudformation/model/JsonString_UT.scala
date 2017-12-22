package com.monsanto.arch.cloudformation.model

import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class JsonString_UT extends FunSpec with Matchers {
  final case class Foo(x: Int, y: Boolean)
  object Foo extends DefaultJsonProtocol {
    implicit val format: JsonFormat[Foo] = jsonFormat2(apply)
  }

  describe("JsonString") {
    it("serializes properly") {
      val js = JsonString(Foo(5, true).toJson)
      val expected = """"{\"x\":5,\"y\":true}""""
      js.toJson.compactPrint shouldEqual expected
      expected.parseJson.convertTo[JsonString] shouldEqual js
    }
  }
}
