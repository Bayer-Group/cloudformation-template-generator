package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.UNSAFEToken
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class EKS_UT extends FunSpec with Matchers {
  describe("AWS::EKS::Cluster") {

    val resourceVpcConfig: ResourcesVpcConfig = ResourcesVpcConfig(
      SecurityGroupIds = Seq(UNSAFEToken("sg-01234567")),
      SubnetIds = Seq(UNSAFEToken("subnet-12345678"))
    )

    val clusterName = "cluster"
    val cluster = `AWS::EKS::Cluster`(
      clusterName,
      "Name",
      ResourcesVpcConfig = resourceVpcConfig,
      RoleArn = "ARN"
    )

    it("should create a valid new EKS cluster") {
      val expected = JsObject(
        clusterName -> JsObject(
          "Properties" -> JsObject(
            "Name" -> JsString("Name"),
            "ResourcesVpcConfig" -> resourceVpcConfig.toJson,
            "RoleArn" -> JsString("ARN")
          ),
          "Type" -> JsString("AWS::EKS::Cluster")
        ))
      Seq[Resource[_]](cluster).toJson should be(expected)
    }
  }
}
