package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ECR_UT extends FunSpec with Matchers {
  describe("AWS::ECR::Repository") {

    val fakePolicyDoc = PolicyDocument(Seq(
      PolicyStatement(
        "Allow",
        Some(DefinedPrincipal(Map("Service" -> Seq("fakePrincipal")))),
        Seq("fakeAction")
      )
    ))

    val repositoryName = "repository"
    val repository = `AWS::ECR::Repository`(
      repositoryName,
      Some("myFakeDockerRepository"),
      Some(fakePolicyDoc)
    )

    it("should create a valid new ECR repository") {
      val expected = JsObject(
        repositoryName -> JsObject(
          "Type" -> JsString("AWS::ECR::Repository"),
          "Properties" -> JsObject(
            "RepositoryName" -> JsString("myFakeDockerRepository"),
            "RepositoryPolicyText" -> fakePolicyDoc.toJson
          )))
      Seq[Resource[_]](repository).toJson should be(expected)
    }
  }
}
