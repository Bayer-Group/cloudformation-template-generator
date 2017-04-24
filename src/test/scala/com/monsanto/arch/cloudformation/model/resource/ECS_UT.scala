package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ParameterRef, ResourceRef, StringParameter}
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ECS_UT extends FunSpec with Matchers {

  describe("AWS::ECS::TaskDefinition") {

    it("should serialize to JSON") {
      val resource = `AWS::ECS::TaskDefinition`("test",
        ContainerDefinitions = Seq(
          ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Some(64))
        )
      )

      val expected = JsObject(
        "name" → JsString("test"),
        "ContainerDefinitions" → JsArray(
          JsObject(
            "Name" → JsString("hello"),
            "Image" → JsString("hello-world"),
            "Memory" → JsNumber(64)
          )
        ),
        "Volumes" → JsArray()
      )

      resource.toJson should be(expected)
    }

    it("should support functions in strings") {
      val param = StringParameter("param", "desc")

      val resource = `AWS::ECS::TaskDefinition`("test",
        ContainerDefinitions = Seq(
          ContainerDefinition(
            Name = "hello",
            Image = ParameterRef(param),
            Hostname = Some(ParameterRef(param)),
            MemoryReservation = Option(64)
          )
        )
      )

      val expected = JsObject(
        "name" → JsString("test"),
        "ContainerDefinitions" → JsArray(
          JsObject(
            "Name" → JsString("hello"),
            "Image" → JsObject(
              "Ref" → JsString("param")
            ),
            "Hostname" → JsObject(
              "Ref" → JsString("param")
            ),
            "MemoryReservation" → JsNumber(64)
          )
        ),
        "Volumes" → JsArray()
      )

      resource.toJson should be(expected)
    }

    it("should serialize Map[String, String] to key/value pairs") {
      val resource = LogConfiguration("driver", Option(Map("key" → "value")))

      val expected = JsObject(
        "LogDriver" → JsString("driver"),
        "Options" → JsObject(
          "key" → JsString("value")
        )
      )

      resource.toJson should be(expected)
    }

    it("should return a Ref to the resource when retrieving its arn") {
      val resource = `AWS::ECS::TaskDefinition`("test",
        ContainerDefinitions = Seq.empty[ContainerDefinition]
      )

      resource.arn should be(ResourceRef(resource))
    }

    it("should not allow MountPoint without matching VolumeDefinition") {

      val ex = intercept[IllegalArgumentException] {
        `AWS::ECS::TaskDefinition`("test",
          ContainerDefinitions = Seq(
            ContainerDefinition(
              Name = "hello",
              Image = "",
              MemoryReservation = Option(64),
              MountPoints = Seq(
                MountPoint(
                  ContainerPath = "aba",
                  SourceVolume = "aba"
                )
              )
            )
          )
        )
      }

      ex.getMessage should include ("MountPoint(StringToken(aba),aba,None) specifies a source volume, aba, that does not exist in task definition test")
    }

    it("should allow MountPoint with matching VolumeDefinition") {
      `AWS::ECS::TaskDefinition`("test",
        Volumes = Seq(
          VolumeDefinition("aba", Host = Some(Host(Some("/somewhere-on-the-host"))))
        ),
        ContainerDefinitions = Seq(
          ContainerDefinition(
            Name = "hello",
            Image = "",
            MemoryReservation = Option(64),
            MountPoints = Seq(
              MountPoint(
                ContainerPath = "/somewhere-on-the-container",
                SourceVolume = "aba"
              )
            )
          )
        )
      )
    }
  }

  describe("AWS::ECS::Service") {
    it("should throw an exception if LoadBalancerName and TargetGroupArn are both defined") {
      assertThrows[IllegalArgumentException] {
        EcsLoadBalancer("name", 42, Some("elb"), Some("target group"))
      }
    }

    it("should allow construction of an EcsLoadBalancer with an ELB") {
      val resource = EcsLoadBalancer("name", 42, LoadBalancerName("elb"))

      val expected = EcsLoadBalancer("name", 42, Some("elb"), None)

      resource should be(expected)
    }

    it("should allow construction of an EcsLoadBalancer with a Target Group") {
      val resource = EcsLoadBalancer("name", 42, TargetGroupArn("arn"))

      val expected = EcsLoadBalancer("name", 42, None, Some("arn"))

      resource should be(expected)
    }

    it("should allow the minimal definition of an ECS Service") {
      val resource = `AWS::ECS::Service`("service",
        DesiredCount = 1,
        TaskDefinition = "arn"
      )

      val expected = JsObject(
        "name" → JsString("service"),
        "DesiredCount" → JsNumber(1),
        "TaskDefinition" → JsString("arn")
      )

      resource.toJson should be(expected)
    }

    it("should allow the full definition of an ECS Service") {
      val resource = `AWS::ECS::Service`("service",
        Cluster = Some("cluster"),
        DeploymentConfiguration = DeploymentConfiguration(MaximumPercent = Option(200), MinimumHealthyPercent = Option(100)),
        DesiredCount = 1,
        LoadBalancers = Option(Seq(EcsLoadBalancer("container", 42, TargetGroupArn("target-group-arn")))),
        Role = Some("role-arn"),
        TaskDefinition = "task-definition-arn"
      )

      val expected = JsObject(
        "name" → JsString("service"),
        "Cluster" → JsString("cluster"),
        "DeploymentConfiguration" → JsObject(
          "MaximumPercent" → JsNumber(200),
          "MinimumHealthyPercent" → JsNumber(100)
        ),
        "DesiredCount" → JsNumber(1),
        "LoadBalancers" → JsArray(JsObject(
          "ContainerName" → JsString("container"),
          "ContainerPort" → JsNumber(42),
          "TargetGroupArn" → JsString("target-group-arn")
        )),
        "Role" → JsString("role-arn"),
        "TaskDefinition" → JsString("task-definition-arn")
      )

      resource.toJson should be(expected)
    }

    it("should allow an AWS::ECS::TaskDefinition reference to be passed to the service definition") {
      val taskDefinition = `AWS::ECS::TaskDefinition`("taskDefinition", Seq.empty[ContainerDefinition])

      val resource = `AWS::ECS::Service`("service",
        DesiredCount = 1,
        TaskDefinition = taskDefinition.arn
      )

      val expected = JsObject(
        "name" → JsString("service"),
        "DesiredCount" → JsNumber(1),
        "TaskDefinition" → JsObject(
          "Ref" → JsString("taskDefinition")
        )
      )

      resource.toJson should be(expected)
    }

    it("should return a Ref to the resource when retrieving its arn") {
      val resource = `AWS::ECS::Service`("service",
        DesiredCount = 1,
        TaskDefinition = "arn"
      )

      resource.arn should be(ResourceRef(resource))
    }
  }

  describe("AWS::ECS::Cluster") {
    it("should allow a cluster name to be set") {
      val resource = `AWS::ECS::Cluster`("name", Some("cluster"))

      val expected = JsObject(
        "name" → JsString("name"),
        "ClusterName" → JsString("cluster")
      )

      resource.toJson should be(expected)
    }

    it("should not require a cluster name to be set") {
      val resource = `AWS::ECS::Cluster`("name")

      val expected = JsObject(
        "name" → JsString("name")
      )

      resource.toJson should be(expected)
    }
  }

  describe("ContainerDefinition") {
    it("should accept Memory as an Option[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Option(64))
    }

    it("should accept Memory as a Some[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Some(64))
    }

    it("should accept MemoryReservation as an Option[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", MemoryReservation = Option(64))
    }

    it("should accept MemoryReservation as a Some[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", MemoryReservation = Some(64))
    }

    it("should accept both Memory and MemoryReservation as Option[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Option(128), MemoryReservation = Option(64))
    }

    it("should accept Memory and MemoryReservation as Some[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Some(128), MemoryReservation = Some(64))
    }

    it("should accept Memory as Option[Int] and MemoryReservation as Some[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Option(128), MemoryReservation = Some(64))
    }

    it("should accept Memory as Some[Int] and MemoryReservation as Option[Int]") {
      ContainerDefinition(Name = "hello", Image = "hello-world", Memory = Some(128), MemoryReservation = Option(64))
    }

    it("should fail to compile if neither Memory nor MemoryReservation is specified") {
      assertTypeError(
        """`AWS::ECS::TaskDefinition`("test",
          |  ContainerDefinitions = Seq(
          |    ContainerDefinition(Name = "hello", Image = "hello-world")
          |  )
          |)
        """.stripMargin)
    }

    it("should throw an exception if Memory is less than MemoryReservation if both are set") {
      assertThrows[IllegalArgumentException] {
        ContainerDefinition(
          Name = "hello",
          Image = "hello-world",
          Memory = Option(0),
          MemoryReservation = Option(1)
        )
      }
    }
  }
}
