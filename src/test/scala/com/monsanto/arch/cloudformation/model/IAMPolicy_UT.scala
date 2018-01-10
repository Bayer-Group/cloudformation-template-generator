package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import org.scalatest.{FunSpec, Matchers}

class IAMPolicy_UT extends FunSpec with Matchers with JsonWritingMatcher {

  describe("AWS::IAM::Policy") {
    it("should create object from roles") {
      val instanceRole = `AWS::IAM::Role`(
        name = "S3Role",
        AssumeRolePolicyDocument =
          PolicyDocument(
            Statement = Seq(
              PolicyStatement(
                Effect = "Allow",
                Principal = Some(DefinedPrincipal(Map("Service" -> Seq("ec2.amazonaws.com")))),
                Action = Seq("sts:AssumeRole")
              )
            )
          ),
        Path = Some("/")
      )
      val policyForRoles = `AWS::IAM::Policy`(
        "S3GetPolicy",
        PolicyDocument(
          Statement = Seq(
            PolicyStatement(
              Effect = "Allow",
              Action = Seq("s3:GetObject"),
              Resource = Some("*")
            )
          )
        ),
        "StaxS3Access",
        None,
        Some(Seq(ResourceRef(instanceRole))),
        None
      )
    }
    it("should generate policy document with NO conditions") {
      PolicyStatement(
        Effect = "Allow",
        Action = Seq("*")
      ) shouldMatch
        """
          |{
          | "Effect": "Allow",
          | "Action": ["*"]
          |}
        """.stripMargin
    }
    it("should generate policy document with conditions") {
      PolicyStatement(
        Effect = "Allow",
        Action = Seq("*"),
        Condition = Map(
          "StringLike" -> Map[String, PolicyConditionValue] (
            "ec2:InstanceType" -> Seq("t1.*", "t2.*")
          )
        )
      ) shouldMatch
        """
          |{
          | "Effect": "Allow",
          | "Action": ["*"],
          | "Condition": {
          |   "StringLike": {
          |     "ec2:InstanceType": ["t1.*", "t2.*"]
          |   }
          | }
          |}
        """.stripMargin
    }
    it("should generate policy document with single conditions") {
      PolicyStatement(
        Effect = "Allow",
        Action = Seq("*"),
        Condition = Map(
          "StringLike" -> Map[String, PolicyConditionValue] (
            "ec2:InstanceType" -> "t1.*"
          )
        )
      ) shouldMatch
        """
          |{
          | "Effect": "Allow",
          | "Action": ["*"],
          | "Condition": {
          |   "StringLike": {
          |     "ec2:InstanceType": "t1.*"
          |   }
          | }
          |}
        """.stripMargin
    }

    it("should generate policy document with a version") {
      PolicyDocument(
        Statement = Seq(PolicyStatement(Effect = "Allow",Action = Seq("*"))),
        Version =  Some(IAMPolicyVersion.`2012-10-17`)
      ) shouldMatch
        """
          |{
          |  "Statement": [{
          |    "Effect": "Allow",
          |    "Action": ["*"]
          |  }],
          |  "Version": "2012-10-17"
          |}
        """.stripMargin
    }

    it("should generate policy statement with resources") {
      PolicyDocument(
        Statement = Seq(PolicyStatement(Effect = "Allow",Action = Seq("*"), Resource = Some(Seq("arn:1", "arn:2"))))
      ) shouldMatch
        """
          |{
          |  "Statement": [{
          |    "Effect": "Allow",
          |    "Action": ["*"],
          |    "Resource": ["arn:1", "arn:2"]
          |  }]
          |}
        """.stripMargin
    }

    it("should allow existing IAM role") {
      `AWS::IAM::Policy`(
        name = "Foo",
        PolicyDocument = PolicyDocument(Seq.empty),
        PolicyName = "Foo",
        Roles = Some(Seq("ExistingFoo"))
      ) shouldMatch
        """
          |{
          | "Type": "AWS::IAM::Policy",
          | "Properties": {
          |   "PolicyDocument": {
          |     "Statement": []
          |   },
          |   "PolicyName": "Foo",
          |   "Roles": ["ExistingFoo"]
          | }
          |}
        """.stripMargin
    }
  }
}
