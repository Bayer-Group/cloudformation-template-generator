package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.JsonWritingMatcher
import com.monsanto.arch.cloudformation.model.resource.`AWS::CloudWatch::Alarm::Namespace`.CustomNamespace
import org.scalatest.{FunSpec, Matchers}

class ApplicationAutoScaling_UT extends FunSpec with Matchers with JsonWritingMatcher {

  it("should generate scalable target policy document") {
    val scalableTarget = `AWS::ApplicationAutoScaling::ScalableTarget`(
      name = "myScalableTarget",
      MaxCapacity = 100,
      MinCapacity = 1,
      ResourceId = "myResourceId",
      RoleARN = "myRoleArn",
      ScalableDimension = "myScalableDimension",
      ServiceNamespace = CustomNamespace("custom-resource"))

    val resource: Resource[`AWS::ApplicationAutoScaling::ScalableTarget`] = scalableTarget

    resource shouldMatch
      """
        |{
        |  "Type": "AWS::ApplicationAutoScaling::ScalableTarget",
        |  "Properties": {
        |    "MaxCapacity": 100,
        |    "MinCapacity": 1,
        |    "ResourceId": "myResourceId",
        |    "RoleARN": "myRoleArn",
        |    "ScalableDimension": "myScalableDimension",
        |    "ServiceNamespace": "custom-resource"
        |  }
        |}
      """.stripMargin
  }

  it("should generate scaling policy document") {
    val scalingPolicy = `AWS::ApplicationAutoScaling::ScalingPolicy`(
      name = "myScalingPolicy",
      PolicyName = "myPolicyName",
      ScalingType = ApplicationAutoScaling.ScalingType.StepScaling,
      ScalingTargetId = Some("myScalingTargetId"))

    val resource: Resource[`AWS::ApplicationAutoScaling::ScalingPolicy`] = scalingPolicy

    resource shouldMatch
      """
        |{
        |  "Type": "AWS::ApplicationAutoScaling::ScalingPolicy",
        |  "Properties": {
        |    "PolicyName": "myPolicyName",
        |    "PolicyType": "StepScaling",
        |    "ScalingTargetId": "myScalingTargetId"
        |  }
        |}
      """.stripMargin
  }
}
