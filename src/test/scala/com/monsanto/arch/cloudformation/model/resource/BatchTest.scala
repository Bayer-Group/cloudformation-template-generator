package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class BatchTest extends FunSpec with Matchers {
  describe("The AWS::Batch::ComputeEnvironment") {

    it("should serialize") {
      val theEnv = `AWS::Batch::ComputeEnvironment`(
        name = "bob",
        Type = ComputeEnvironmentType.MANAGED,
        ServiceRole = ResourceRef(`AWS::Batch::ComputeEnvironment`.minimumServiceRole("bob-role")),
        ComputeEnvironmentName = Some("clusterbob"),
        ComputeResources = ComputeResources(
          Type = ComputeResourcesType.EC2,
          InstanceTypes = ComputeResources.OptimalInstanceType,
          MinvCpus = 0,
          MaxvCpus = 500,
          DesiredvCpus = None,
          SecurityGroupIds = Seq.empty,
          Subnets = Seq(),
          ImageId = None,
          Ec2KeyPair = None,
          InstanceRole = ResourceRef(`AWS::IAM::InstanceProfile`(
            name = "clusterBobInstanceProfile",
            Path = "somewhere",
            Roles = Seq(ComputeResources.minimumInstanceRole("clusterbobrole")),
            InstanceProfileName = Some("clusterBobInstanceProfile")
          )),
          SpotIamFleetRole = ResourceRef(ComputeResources.minimumSpotFleetRole("clusterbobspot")),
          BidPercentage = Some(80),
          Tags = None
        ),
        State = Some(ComputeEnvironmentState.ENABLED)
      )

      theEnv.toJson.prettyPrint shouldBe """{
                                           |  "name": "bob",
                                           |  "ServiceRole": {
                                           |    "Ref": "bob-role"
                                           |  },
                                           |  "ComputeEnvironmentName": "clusterbob",
                                           |  "ComputeResources": {
                                           |    "InstanceTypes": ["optimal"],
                                           |    "Subnets": [],
                                           |    "SpotIamFleetRole": {
                                           |      "Ref": "clusterbobspot"
                                           |    },
                                           |    "MaxvCpus": 500,
                                           |    "BidPercentage": 80,
                                           |    "SecurityGroupIds": [],
                                           |    "InstanceRole": {
                                           |      "Ref": "clusterBobInstanceProfile"
                                           |    },
                                           |    "MinvCpus": 0,
                                           |    "Type": "EC2"
                                           |  },
                                           |  "State": "ENABLED",
                                           |  "Type": "MANAGED"
                                           |}""".stripMargin
    }

  }

  describe("The AWS::Batch::JobDefinition") {

    it("should serialize") {
      val theDef = `AWS::Batch::JobDefinition`(
        name = "bob",
        Type = JobDefinitionType.container,
        Parameters = Map("someParam" -> "hallo"),
        ContainerProperties = JobContainerProperties(
          MountPoints = None,
          User = Some("billybob"),
          Volumes = None,
          Command = Some(Seq("do", "a", "thing", "with", "Ref:someParam")),
          Memory = 128,
          Privileged = Some(false),
          Environment = Seq(Environment("EDITOR", "vi")),
          JobRoleArn = Some("arn:blah:blah:blah:role"),
          ReadonlyRootFilesystem = Some(true),
          Ulimits = None,
          Vcpus = 1000,
          Image = "centos:latest"
        ),
        JobDefinitionName = Some("bobjob"),
        RetryStrategy = JobRetryStrategy(Attempts = Some(7))
      )

      theDef.toJson.prettyPrint shouldBe """{
                                           |  "name": "bob",
                                           |  "ContainerProperties": {
                                           |    "Environment": [{
                                           |      "Name": "EDITOR",
                                           |      "Value": "vi"
                                           |    }],
                                           |    "Memory": 128,
                                           |    "Command": ["do", "a", "thing", "with", "Ref:someParam"],
                                           |    "Privileged": false,
                                           |    "Vcpus": 1000,
                                           |    "User": "billybob",
                                           |    "JobRoleArn": "arn:blah:blah:blah:role",
                                           |    "Image": "centos:latest",
                                           |    "ReadonlyRootFilesystem": true
                                           |  },
                                           |  "JobDefinitionName": "bobjob",
                                           |  "RetryStrategy": {
                                           |    "Attempts": 7
                                           |  },
                                           |  "Parameters": {
                                           |    "someParam": "hallo"
                                           |  },
                                           |  "Type": "container"
                                           |}""".stripMargin
    }

  }

  describe("The AWS::Batch::JobQueue") {

    it("should serialize") {
      val theQueue = `AWS::Batch::JobQueue`(
        name = "bobq",
        JobQueueName = Some("bobqueue"),
        ComputeEnvironmentOrder = Seq(
          ComputeEnvironmentOrder("abc", 123),
          ComputeEnvironmentOrder("xyz", 890)
        ),
        Priority = 7,
        State = Some(JobQueueState.ENABLED)
      )

      theQueue.toJson.prettyPrint shouldBe """{
                                             |  "name": "bobq",
                                             |  "ComputeEnvironmentOrder": [{
                                             |    "ComputeEnvironment": "abc",
                                             |    "Order": 123
                                             |  }, {
                                             |    "ComputeEnvironment": "xyz",
                                             |    "Order": 890
                                             |  }],
                                             |  "JobQueueName": "bobqueue",
                                             |  "Priority": 7,
                                             |  "State": "ENABLED"
                                             |}""".stripMargin
    }

  }
}
