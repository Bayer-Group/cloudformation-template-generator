package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{JsonWritingMatcher, ResourceRef}
import org.scalatest.{FunSpec, Matchers}

class ApplicationAutoScaling_UT extends FunSpec with Matchers with JsonWritingMatcher {

  val scalableTarget = `AWS::ApplicationAutoScaling::ScalableTarget`(
    name = "myScalableTarget",
    MaxCapacity = 100,
    MinCapacity = 1,
    ResourceId = "myResourceId",
    RoleARN = "myRoleArn",
    ScalableDimension = ApplicationAutoScaling.ScalableDimension.`custom-resource:ResourceType:Property`,
    ServiceNamespace = ApplicationAutoScaling.ServiceNamespace.`custom-resource`)


  it("should generate scalable target policy document") {

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
        |    "ScalableDimension": "custom-resource:ResourceType:Property",
        |    "ServiceNamespace": "custom-resource"
        |  }
        |}
      """.stripMargin
  }

  it("should error if ScalableDimension doesn't match the ServiceNamespace") {
    an [java.lang.AssertionError] should be thrownBy
      `AWS::ApplicationAutoScaling::ScalableTarget`(
        name = "myScalableTarget",
        MaxCapacity = 100,
        MinCapacity = 1,
        ResourceId = "myResourceId",
        RoleARN = "myRoleArn",
        ScalableDimension = ApplicationAutoScaling.ScalableDimension.`custom-resource:ResourceType:Property`,
        ServiceNamespace = ApplicationAutoScaling.ServiceNamespace.dynamodb
      )
  }

  it("should generate scaling policy document") {
    val scalingPolicy = `AWS::ApplicationAutoScaling::ScalingPolicy`(
      name = "myScalingPolicy",
      PolicyName = "myPolicyName",
      PolicyType = ApplicationAutoScaling.PolicyType.StepScaling,
      ScalingTargetId = Some(ResourceRef(scalableTarget))
    )

    val resource: Resource[`AWS::ApplicationAutoScaling::ScalingPolicy`] = scalingPolicy

    resource shouldMatch
      """
        |{
        |  "Type": "AWS::ApplicationAutoScaling::ScalingPolicy",
        |  "Properties": {
        |    "PolicyName": "myPolicyName",
        |    "PolicyType": "StepScaling",
        |    "ScalingTargetId": { "Ref":"myScalableTarget" }
        |  }
        |}
      """.stripMargin
  }
}
