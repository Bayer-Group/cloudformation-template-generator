package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json.{JsNumber, JsString, _}

class Subnet_Parameter_List_UT extends FunSpec with Matchers {
  describe("AWS::EC2::Subnet_Parameter_List") {

    it("should serialize into valid json") {
      val subnetListParam = `AWS::EC2::Subnet_Parameter_List`("subnets", "Select subnets where the RDS instances should be created")
      val expectedJson = JsObject(
        "subnets" -> JsObject(
          "Description" -> JsString("Select subnets where the RDS instances should be created"),
          "Type" -> JsString("List<AWS::EC2::Subnet::Id>")
        )
      )
      Seq[Parameter](subnetListParam).toJson should be (expectedJson)
    }

    it("should serialize into valid json as InputParameter") {
      val subnetListParam = `AWS::EC2::Subnet_Parameter_List`("subnets", "Select subnets where the RDS instances should be created")
      val expectedJson = JsObject(
        "ParameterKey" -> JsString("subnets"),
        "ParameterValue" -> JsString("")
      )
      val inputParam = InputParameter.templateParameterToInputParameter(Some(Seq(subnetListParam)))
      inputParam.get(0).toJson should be (expectedJson)
    }

    it("can be passed as ParameterRef to AWS::RDS::DBSubnetGroup") {
      val subnetListParam = `AWS::EC2::Subnet_Parameter_List`("subnets", "Select subnets where the RDS instances should be created")
      val dbSubnetGroup = `AWS::RDS::DBSubnetGroup`(
        name = "dbSubnetGroup",
        DBSubnetGroupDescription = "DB subnet group",
        SubnetIds = ParameterRef(subnetListParam)
      )
      val expected = JsObject(
        "dbSubnetGroup" -> JsObject(
          "Type" -> JsString("AWS::RDS::DBSubnetGroup"),
          "Properties" -> JsObject(
            "DBSubnetGroupDescription" -> JsString("DB subnet group"),
            "SubnetIds" -> JsObject("Ref" -> JsString("subnets"))
          )
        )
      )
      Seq[Resource[_]](dbSubnetGroup).toJson should be (expected)
    }
  }
}
