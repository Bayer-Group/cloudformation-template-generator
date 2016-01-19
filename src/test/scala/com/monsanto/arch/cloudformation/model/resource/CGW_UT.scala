package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class CGW_UT extends FunSpec with Matchers {
  describe("AWS::EC2::CustomerGateway") {
    val bpgAsn = 1234
    val ipAddr = IPAddress(8, 8, 8, 8)
    val cgwType = "ipsec.1"
    val cgw = `AWS::EC2::CustomerGateway`(
      name = "cgw",
      BgpAsn = 1234,
      IpAddress = ipAddr,
      Tags = Seq(),
      Type = "ipsec.1"
    )
    it("should create a valid new Customer Gateway") {
      val expected = JsObject(
        "cgw" -> JsObject(
          "Type" -> JsString("AWS::EC2::CustomerGateway"),
          "Properties" -> JsObject(
            "BgpAsn" -> JsNumber(bpgAsn),
            "IpAddress" -> ipAddr.toJsString,
            "Tags" -> JsArray(),
            "Type" -> JsString(cgwType)
          )
        )
      )
      Seq[Resource[_]](cgw).toJson should be(expected)
    }
  }
}
