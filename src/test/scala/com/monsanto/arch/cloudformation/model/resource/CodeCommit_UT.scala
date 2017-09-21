package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ ResourceRef, Template, Token }
import org.scalatest.{ FunSpec, Matchers }
import spray.json._

class CodeCommit_UT extends FunSpec with Matchers {
  val repo = `AWS::CodeCommit::Repository`(
    name = "RepoFoo",
    RepositoryDescription = "",
    RepositoryName = Some("RepoBar"),
    Triggers = Some(Seq(
      CodeCommitTrigger(
        Branches = Some(Seq("foo")),
        CustomData = Some("bar"),
        DestinationArn = Some("arn::::baz"),
        Events = Some(Seq(
          CodeCommitEvent.updateReference,
          CodeCommitEvent.deleteReference
        )),
        Name = "BarTrigger"
      )
    ))
  )

  describe("UsagePlan"){
    it ("should serialize as expected") {
      val expectedJson =
        """
          |{
          |  "AWSTemplateFormatVersion": "2010-09-09",
          |  "Description": "",
          |  "Resources": {
          |    "RepoFoo": {
          |      "Properties": {
          |        "RepositoryDescription": "",
          |        "RepositoryName": "RepoBar",
          |        "Triggers": [
          |          {
          |            "Branches": [
          |              "foo"
          |            ],
          |            "CustomData": "bar",
          |            "DestinationArn": "arn::::baz",
          |            "Events": [
          |              "updateReference",
          |              "deleteReference"
          |            ],
          |            "Name": "BarTrigger"
          |          }
          |        ]
          |      },
          |      "Type": "AWS::CodeCommit::Repository"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(repo).toJson should be (expectedJson)
    }
  }
}
