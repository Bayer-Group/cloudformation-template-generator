package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.ResourceRef
import org.scalatest.{FunSpec, Matchers}
import spray.json.{JsObject, JsString, _}


class IAMRole_UT extends FunSpec with Matchers {
  describe("AWS::IAM::Role") {

    it("should handle both AWS Managed and Customer policies into valid json") {
      val customerPolicy = `AWS::IAM::ManagedPolicy`("customer-policy", PolicyDocument(Seq()))
      val awsPolicy = AWSManagedPolicy("AdministratorAccess")

      val fakePolicyDoc = PolicyDocument(Seq(
        PolicyStatement(
          "Allow",
          Some(DefinedPrincipal(Map("Service" -> Seq("config.amazonaws.com")))),
          Seq("sts:AssumeRole")
        )
      ))

      val expectedJson = JsObject(
        "name" -> JsString("role"),
        "AssumeRolePolicyDocument" -> fakePolicyDoc.toJson,
        "ManagedPolicyArns" -> JsArray(
          JsObject("Ref" -> JsString("customer-policy")),
          JsString("arn:aws:iam::aws:policy/AdministratorAccess")
        )
      )

      val role = `AWS::IAM::Role`(
        "role",
        fakePolicyDoc,
        ManagedPolicyArns = Some(Seq(ResourceRef(customerPolicy), awsPolicy))
      )

      role.toJson should be(expectedJson)
    }
  }
}
