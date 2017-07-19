package com.monsanto.arch.cloudformation.model.resource

import org.scalatest.{FunSpec, Matchers}
import spray.json.{JsonFormat, JsString}

class CloudWatchSpec extends FunSpec with Matchers {

  it("should format AWS/EC2") {
    implicitly[JsonFormat[`AWS::CloudWatch::Alarm::Namespace`]].write(`AWS::CloudWatch::Alarm::Namespace`.`AWS/EC2`) should equal(JsString("AWS/EC2"))
  }

  it("should format custom namespace") {
    implicitly[JsonFormat[`AWS::CloudWatch::Alarm::Namespace`]].write(`AWS::CloudWatch::Alarm::Namespace`("hello")) should equal(JsString("hello"))
  }

  it("should format implicit custom namespace") {
    implicitly[JsonFormat[`AWS::CloudWatch::Alarm::Namespace`]].write("hello" : `AWS::CloudWatch::Alarm::Namespace`) should equal(JsString("hello"))
  }
}
