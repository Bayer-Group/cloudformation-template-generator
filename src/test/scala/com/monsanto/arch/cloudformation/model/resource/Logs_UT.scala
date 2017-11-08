package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class Logs_UT extends FunSpec with Matchers {
  describe("Destination") {
    val dest = `AWS::Logs::Destination`(
      name = "foo",
      DestinationName = "bar",
      DestinationPolicy = JsonString(
        PolicyDocument(
          Statement = Seq(
            PolicyStatement(
              Effect = "Allow",
              Action = Seq("fire")
            )
          )
        )
      ),
      RoleArn = "role",
      TargetArn = "target"
    )

    it("should write a valid Log Destination") {
      dest.toJson shouldEqual JsObject(
        "name" -> JsString("foo"),
        "DestinationName" -> JsString("bar"),
        "DestinationPolicy" -> JsString("""{"Statement":[{"Effect":"Allow","Action":["fire"]}]}"""),
        "RoleArn" -> JsString("role"),
        "TargetArn" -> JsString("target")
      )
    }
  }
}
