package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ParameterRef, StringParameter, Template}
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class CloudFormation_UT extends FunSpec with Matchers{

  describe("No args, no special title"){
    it ("should serialize as expected") {

      val customResource = `AWS::CloudFormation::CustomResource`(
        name = "TestResource",
        ServiceToken = "TestToken"
      )

      println(Template.fromResource(customResource).toJson)
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "TestResource": {
          |      "Properties": {
          |        "ServiceToken": "TestToken"
          |      },
          |      "Type": "AWS::CloudFormation::CustomResource"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(customResource).toJson should be (expectedJson)
    }
  }
  describe("Parameter for service token"){
    it ("should serialize as expected") {

      val param = StringParameter(
        name = "TestParam"
      )
      val customResource = `AWS::CloudFormation::CustomResource`(
        name = "TestResource",
        ServiceToken = ParameterRef(param)
      )

      println(Template.fromResource(customResource).toJson)
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "TestResource": {
          |      "Properties": {
          |        "ServiceToken": {"Ref": "TestParam"}
          |      },
          |      "Type": "AWS::CloudFormation::CustomResource"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(customResource).toJson should be (expectedJson)
    }
  }


  describe("Custom type"){
    it ("should serialize as expected") {

      val customResource = `AWS::CloudFormation::CustomResource`(
        name = "TestResource",
        ServiceToken = "TestToken",
        CustomResourceTypeName = Some("HeyThere")
      )

      println(Template.fromResource(customResource).toJson)
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "TestResource": {
          |      "Properties": {
          |        "ServiceToken": "TestToken"
          |      },
          |      "Type": "Custom::HeyThere"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(customResource).toJson should be (expectedJson)
    }
  }
  describe("Custom type with Custom included"){
    it ("should serialize as expected") {

      val customResource = `AWS::CloudFormation::CustomResource`(
        name = "TestResource",
        ServiceToken = "TestToken",
        CustomResourceTypeName = Some("Custom::HeyThere")
      )

      println(Template.fromResource(customResource).toJson)
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "TestResource": {
          |      "Properties": {
          |        "ServiceToken": "TestToken"
          |      },
          |      "Type": "Custom::HeyThere"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(customResource).toJson should be (expectedJson)
    }
  }

  describe("String parameter"){
    it ("should serialize as expected") {

      import DefaultJsonProtocol._
      val customResource = `AWS::CloudFormation::CustomResource`(
        name = "TestResource",
        ServiceToken = "TestToken",
        Parameters = Some(Map("Hi" -> "There"))
      )

      println(Template.fromResource(customResource).toJson)
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "TestResource": {
          |      "Properties": {
          |        "ServiceToken": "TestToken",
          |        "Hi": "There"
          |      },
          |      "Type": "AWS::CloudFormation::CustomResource"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(customResource).toJson should be (expectedJson)
    }
  }

  describe("String and number parameter"){
    it ("should serialize as expected") {

      import DefaultJsonProtocol._
      val customResource = `AWS::CloudFormation::CustomResource`(
        name = "TestResource",
        ServiceToken = "TestToken",
        Parameters = Some(Map(
          "Hi" -> "There",
          "Number" -> 1))
      )

      println(Template.fromResource(customResource).toJson)
      val expectedJson =
        """
          |{
          |  "Resources": {
          |    "TestResource": {
          |      "Properties": {
          |        "ServiceToken": "TestToken",
          |        "Hi": "There",
          |        "Number": 1
          |      },
          |      "Type": "AWS::CloudFormation::CustomResource"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(customResource).toJson should be (expectedJson)
    }
  }

}
