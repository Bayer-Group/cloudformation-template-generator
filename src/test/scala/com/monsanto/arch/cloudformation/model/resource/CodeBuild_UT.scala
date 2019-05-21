package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class CodeBuild_UT extends FunSpec with Matchers {

  // Example from https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-codebuild-project.html
  val expectedJson: JsValue =
    """
      |{
      |  "Resources": {
      |    "CodeBuildProject": {
      |      "Type": "AWS::CodeBuild::Project",
      |      "Properties": {
      |        "ServiceRole": {
      |          "Fn::GetAtt": [
      |            "CodeBuildRole",
      |            "Arn"
      |          ]
      |        },
      |        "Artifacts": {
      |          "Type": "CODEPIPELINE"
      |        },
      |        "Environment": {
      |          "Type": "LINUX_CONTAINER",
      |          "ComputeType": "BUILD_GENERAL1_SMALL",
      |          "Image": "aws/codebuild/ubuntu-base:14.04",
      |          "EnvironmentVariables": [
      |            {
      |              "Name": "varName1",
      |              "Value": "varValue1"
      |            },
      |            {
      |              "Name": "varName2",
      |              "Value": "varValue2",
      |              "Type": "PLAINTEXT"
      |            },
      |            {
      |              "Name": "varName3",
      |              "Value": "/CodeBuild/testParameter",
      |              "Type": "PARAMETER_STORE"
      |            }
      |          ]
      |        },
      |        "Source": {
      |          "Type": "CODEPIPELINE"
      |        },
      |        "TimeoutInMinutes": 10,
      |        "VpcConfig": {
      |          "VpcId": {
      |            "Ref": "CodeBuildVPC"
      |          },
      |          "Subnets": [
      |            {
      |              "Ref": "CodeBuildSubnet"
      |            }
      |          ],
      |          "SecurityGroupIds": [
      |            {
      |              "Ref": "CodeBuildSecurityGroup"
      |            }
      |          ]
      |        },
      |        "Cache": {
      |          "Type": "S3",
      |          "Location": "mybucket/prefix"
      |        }
      |      }
      |    }
      |  }
      |}
    """.stripMargin.parseJson

  def createRef(name: String): ResourceRef[`AWS::EC2::VPC`] = ResourceRef(
    `AWS::EC2::VPC`(
      name = name,
      CidrBlock = CidrBlock(10, 0, 0, 0, 24),
      Tags = Seq.empty
    )
  )

  describe("Project") {
    it("emits the right thing") {

      val project = `AWS::CodeBuild::Project`(
        name = "CodeBuildProject",
        ServiceRole = `Fn::GetAtt`(Seq("CodeBuildRole", "Arn")),
        Artifacts = CodeBuildProjectArtifacts(
          Type = "CODEPIPELINE"
        ),
        Environment = CodeBuildProjectEnvironment(
          Type = "LINUX_CONTAINER",
          ComputeType = "BUILD_GENERAL1_SMALL",
          Image = "aws/codebuild/ubuntu-base:14.04",
          EnvironmentVariables = Some(
            Seq(
              ProjectEnvironmentVariable(Name = "varName1", Value = "varValue1"),
              ProjectEnvironmentVariable(Name = "varName2", Value = "varValue2", Type = Some("PLAINTEXT")),
              ProjectEnvironmentVariable(
                Name = "varName3", Value = "/CodeBuild/testParameter", Type = "PARAMETER_STORE"
              )
            )
          )
        ),
        Source = CodeBuildProjectSource(
          Type = "CODEPIPELINE"
        ),
        TimeoutInMinutes = Some(10),
        VpcConfig = Some(
          CodeBuildVpcConfig(
            VpcId = Some(createRef("CodeBuildVPC")),
            Subnets = Some(
              Seq(
                createRef("CodeBuildSubnet")
              )
            ),
            SecurityGroupIds = Some(
              Seq(
                createRef("CodeBuildSecurityGroup")
              )
            )
          )
        ),
        Cache = Some(
          CodeBuildProjectCache(
            Type = CodeBuildProjectCacheType.S3,
            Location = "mybucket/prefix"
          )
        )
      )

      Template.fromResource(project).toJson shouldBe expectedJson
    }
  }

}
