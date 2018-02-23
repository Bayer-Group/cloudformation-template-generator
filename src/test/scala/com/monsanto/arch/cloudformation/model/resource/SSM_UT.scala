package com.monsanto.arch.cloudformation.model.resource

import org.scalatest.{FunSpec, Matchers}
import com.monsanto.arch.cloudformation.model._
import spray.json._

class SSM_UT extends FunSpec with Matchers {

  describe("AWS::SSM::Document") {

    it("should serialize the most basic content") {
      val doc = `AWS::SSM::Document`(
        "cfname",
        DocumentContent("2.2", None, None, None),
        DocumentType.Command
      )

      doc.toJson shouldBe """{
                                        |  "name": "cfname",
                                        |  "Content": {
                                        |    "schemaVersion": "2.2"
                                        |  },
                                        |  "DocumentType": "Command"
                                        |}""".stripMargin.parseJson
    }

    it("should serialize the whole mess") {
      val doc = `AWS::SSM::Document`(
        "cfname",
        DocumentContent(
          schemaVersion = "2.2",
          description = Some("this is my fancy document"),
          parameters = Some(Map(
            "myParam1" -> DocumentParameter(
              `type` = ParameterType.String,
              description = "this is my fancy parameter",
              default = Some("default value")
            ),
            "myParam2" -> DocumentParameter(
              `type` = ParameterType.String,
              description = "this is my other fancy parameter",
              default = Some("default value")
            )
          )),
          mainSteps = Some(Seq(
            DocumentStep.`aws:configureDocker`(
              name = "step1",
              action = InstallUninstall.Install
            ),
            DocumentStep.`aws:updateSsmAgent`(
              name = "step2"
            ),
            DocumentStep.`aws:runShellScript`(
              name = "stepName",
              runCommand = Seq("{{ myParam1 }}"),
              timeoutSeconds = Some("1"),
              workingDirectory = Some("{{ myParam2 }}")
            )
          ))
        ),
        DocumentType.Command
      )

      doc.toJson shouldBe """{
                                        |  "name": "cfname",
                                        |  "Content": {
                                        |    "schemaVersion": "2.2",
                                        |    "description": "this is my fancy document",
                                        |    "parameters": {
                                        |      "myParam1": {
                                        |        "type": "String",
                                        |        "description": "this is my fancy parameter",
                                        |        "default": "default value"
                                        |      },
                                        |      "myParam2": {
                                        |        "type": "String",
                                        |        "description": "this is my other fancy parameter",
                                        |        "default": "default value"
                                        |      }
                                        |    },
                                        |    "mainSteps": [{
                                        |      "action": "aws:configureDocker",
                                        |      "name": "step1",
                                        |      "inputs": {
                                        |        "action": "Install"
                                        |      }
                                        |    }, {
                                        |      "action": "aws:updateSsmAgent",
                                        |      "name": "step2",
                                        |      "inputs": {
                                        |        "agentName": "amazon-ssm-agent",
                                        |        "allowDowngrade": "false",
                                        |        "source": "https://s3.{Region}.amazonaws.com/amazon-ssm-{Region}/ssm-agent-manifest.json"
                                        |      }
                                        |    }, {
                                        |      "action": "aws:runShellScript",
                                        |      "name": "stepName",
                                        |      "inputs": {
                                        |        "runCommand": ["{{ myParam1 }}"],
                                        |        "timeoutSeconds": "1",
                                        |        "workingDirectory": "{{ myParam2 }}"
                                        |      }
                                        |    }]
                                        |  },
                                        |  "DocumentType": "Command"
                                        |}""".stripMargin.parseJson
    }

    describe("helper constructors should construct the correct doc type and schema") {
      it("Command with schemaVersion 2.2") {
        val doc = `AWS::SSM::Document`.command_22("cfname", None, None, None)

        doc.DocumentType shouldBe Some(DocumentType.Command)
        doc.Content.schemaVersion shouldBe "2.2"
      }

      it("Policy with schemaVersion 2.0") {
        val doc = `AWS::SSM::Document`.policy_20("cfname", None, None, None)

        doc.DocumentType shouldBe Some(DocumentType.Policy)
        doc.Content.schemaVersion shouldBe "2.0"
      }

      it("Automation with schemaVersion 0.3") {
        val doc = `AWS::SSM::Document`.automation_03("cfname", None, None, None)

        doc.DocumentType shouldBe Some(DocumentType.Automation)
        doc.Content.schemaVersion shouldBe "0.3"
      }
    }

    Some(Map(
      "param1" -> DocumentParameter(
        `type` = ParameterType.String,
        description = "some parameter",
        default = Some("hello"),
        allowedPattern = Some(".*"),
        allowedValues = Some(Seq("hello", "goodbye"))
      )
    ))

    it("SSMDocumentContent should serialize most basic content") {
      val content = DocumentContent(
        schemaVersion = "2.2",
        description = Some("this is the stuff"),
        parameters = None,
        mainSteps = None
      )

      content.toJson shouldBe """{
                                            |  "schemaVersion": "2.2",
                                            |  "description": "this is the stuff"
                                            |}""".stripMargin.parseJson
    }

    it("DocumentParameter should serialize most basic content") {
      val param = DocumentParameter(
        `type` = ParameterType.String,
        description = "some parameter",
        default = Some("hello"),
        allowedPattern = Some(".*"),
        allowedValues = Some(Seq("hello", "goodbye"))
      )

      param.toJson shouldBe """{
                                          |  "description": "some parameter",
                                          |  "allowedValues": ["hello", "goodbye"],
                                          |  "default": "hello",
                                          |  "type": "String",
                                          |  "allowedPattern": ".*"
                                          |}""".stripMargin.parseJson
    }

    it("DocumentStep should serialize most basic content") {
      val step = DocumentStep(
        action = "aws:something",
        name = "stepName",
        precondition = None,
        inputs = None
      )

      step.toJson shouldBe """{
                                         |  "action": "aws:something",
                                         |  "name": "stepName"
                                         |}""".stripMargin.parseJson
    }

    it("DocumentStep should fail construction if name includes a space") {
      intercept[IllegalArgumentException] {
        DocumentStep(
          action = "aws:something",
          name = "step name",
          precondition = None,
          inputs = None
        )
      }
    }

    describe("mainStep helpers should construct proper DocumentSteps") {
      it("aws:applications") {
        val step = DocumentStep.`aws:applications`(
          name = "stepName",
          action = InstallRepairUninstall.Repair,
          parameters = Some("bob"),
          source = "somewhere",
          sourceHash = Some("abc123"),
          precondition = Some(Precondition.PlatformTypeWindows)
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:applications",
                                           |  "name": "stepName",
                                           |  "precondition": {
                                           |    "StringEquals": ["platformType", "Windows"]
                                           |  },
                                           |  "inputs": {
                                           |    "action": "Repair",
                                           |    "parameters": "bob",
                                           |    "source": "somewhere",
                                           |    "sourceHash": "abc123"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:configureDocker") {
        val step = DocumentStep.`aws:configureDocker`(
          name = "stepName",
          action = InstallUninstall.Install,
          precondition = Some(Precondition.PlatformTypeLinux)
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:configureDocker",
                                           |  "name": "stepName",
                                           |  "precondition": {
                                           |    "StringEquals": ["platformType", "Linux"]
                                           |  },
                                           |  "inputs": {
                                           |    "action": "Install"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:configurePackage") {
        val step = DocumentStep.`aws:configurePackage`(
          name = "stepName",
          packageName = "some_package",
          action = InstallUninstall.Uninstall,
          version = Some("1.2.3")
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:configurePackage",
                                           |  "name": "stepName",
                                           |  "inputs": {
                                           |    "name": "some_package",
                                           |    "action": "Uninstall",
                                           |    "version": "1.2.3"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:refreshAssociation") {
        val step = DocumentStep.`aws:refreshAssociation`(
          name = "stepName",
          associationIds = "abc123,def456,ghi789"
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:refreshAssociation",
                                           |  "name": "stepName",
                                           |  "inputs": {
                                           |    "associationIds": "abc123,def456,ghi789"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:runDockerAction") {
        val step = DocumentStep.`aws:runDockerAction`(
          name = "stepName",
          action = "run",
          container = Some("hghg7575"),
          image = Some("soemthing:latest"),
          cmd = Some("cmd args"),
          memory = Some("mem config"),
          cpuShares = Some("cpu config"),
          volume = Some("volume config"),
          env = Some("env config"),
          user = Some("root"),
          publish = Some("port config")
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:runDockerAction",
                                           |  "name": "stepName",
                                           |  "inputs": {
                                           |    "image": "soemthing:latest",
                                           |    "cpuShares": "cpu config",
                                           |    "container": "hghg7575",
                                           |    "cmd": "cmd args",
                                           |    "publish": "port config",
                                           |    "action": "run",
                                           |    "user": "root",
                                           |    "volume": "volume config",
                                           |    "env": "env config",
                                           |    "memory": "mem config"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:runShellScript") {
        val step = DocumentStep.`aws:runShellScript`(
          name = "stepName",
          runCommand = Seq("ls"),
          timeoutSeconds = Some("1"),
          workingDirectory = Some("/tmp")
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:runShellScript",
                                           |  "name": "stepName",
                                           |  "inputs": {
                                           |    "runCommand": ["ls"],
                                           |    "timeoutSeconds": "1",
                                           |    "workingDirectory": "/tmp"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:softwareInventory") {
        val step = DocumentStep.`aws:softwareInventory`(
          name = "stepName",
          applications = Some("applications-value"),
          awsComponents = Some("awscomponents-value"),
          networkConfig = Some("networkconfig-value"),
          windowsUpdates = Some("windowsupdates-value"),
          customInventory = Some("custominventory-value")
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:softwareInventory",
                                           |  "name": "stepName",
                                           |  "inputs": {
                                           |    "networkConfig": "networkconfig-value",
                                           |    "applications": "applications-value",
                                           |    "windowsUpdates": "windowsupdates-value",
                                           |    "customInventory": "custominventory-value",
                                           |    "awsComponents": "awscomponents-value"
                                           |  }
                                           |}""".stripMargin.parseJson
      }

      it("aws:updateSsmAgent") {
        val step = DocumentStep.`aws:updateSsmAgent`(
          name = "stepName",
          allowDowngrade = "true",
          targetVersion = Some("1.2.3")
        )

        step.toJson shouldBe """{
                                           |  "action": "aws:updateSsmAgent",
                                           |  "name": "stepName",
                                           |  "inputs": {
                                           |    "agentName": "amazon-ssm-agent",
                                           |    "allowDowngrade": "true",
                                           |    "source": "https://s3.{Region}.amazonaws.com/amazon-ssm-{Region}/ssm-agent-manifest.json",
                                           |    "targetVersion": "1.2.3"
                                           |  }
                                           |}""".stripMargin.parseJson
      }
    }
  }

  describe("AWS::SSM::Association") {
    it("does not allow both InstanceId and Targets to be specified") {
      intercept[IllegalArgumentException] {
        `AWS::SSM::Association`(
          name = "cfname",
          DocumentVersion = Some("1"),
          InstanceId = Some("i-75675757575"),
          Name = Left("AssociationName"),
          Parameters = Some(Map(
            "param1" -> Seq("paramValue")
          )),
          ScheduleExpression = CronSchedule(),
          Targets = Some(Seq(Target(Key = TagKey("someTag"), Values = Seq("someValue"))))
        )
      }
    }

    it("requires either InstanceId and Targets to be specified") {
      intercept[IllegalArgumentException] {
        `AWS::SSM::Association`(
          name = "cfname",
          DocumentVersion = Some("1"),
          InstanceId = None,
          Name = Left("AssociationName"),
          Parameters = Some(Map(
            "param1" -> Seq("paramValue")
          )),
          ScheduleExpression = CronSchedule(),
          Targets = None
        )
      }
    }

    it("should serialize an association for an external document") {
      val assoc = `AWS::SSM::Association`(
        name = "cfname",
        DocumentVersion = Some("1"),
        InstanceId = Some("i-7893456789345"),
        Name = Left("AssociationName"),
        Parameters = Some(Map(
          "param1" -> Seq("paramValue")
        )),
        ScheduleExpression = CronSchedule(),
        Targets = None
      )

      assoc.toJson shouldBe """{
                                          |  "name": "cfname",
                                          |  "Name": "AssociationName",
                                          |  "Parameters": {
                                          |    "param1": ["paramValue"]
                                          |  },
                                          |  "DocumentVersion": "1",
                                          |  "InstanceId": "i-7893456789345",
                                          |  "ScheduleExpression": "cron(* * * * * *)"
                                          |}""".stripMargin.parseJson
    }

    it("should serialize an association for a managed document") {
      val doc = `AWS::SSM::Document`(
        "cfname",
        DocumentContent("2.2", None, None, None),
        DocumentType.Command
      )

      val assoc = `AWS::SSM::Association`(
        name = "cfname",
        DocumentVersion = Some("1"),
        InstanceId = Some("i-7893456789345"),
        Name = Right(ResourceRef(doc)),
        Parameters = Some(Map(
          "param1" -> Seq("paramValue")
        )),
        ScheduleExpression = DayRateSchedule(3),
        Targets = None
      )

      assoc.toJson shouldBe """{
                                          |  "name": "cfname",
                                          |  "Name": {
                                          |    "Ref": "cfname"
                                          |  },
                                          |  "Parameters": {
                                          |    "param1": ["paramValue"]
                                          |  },
                                          |  "DocumentVersion": "1",
                                          |  "InstanceId": "i-7893456789345",
                                          |  "ScheduleExpression": "rate(3 days)"
                                          |}""".stripMargin.parseJson
    }

    it("should serialize an association with InstanceIds targets") {
      val assoc = `AWS::SSM::Association`(
        name = "cfname",
        DocumentVersion = Some("1"),
        InstanceId = None,
        Name = Left("AssociationName"),
        Parameters = Some(Map(
          "param1" -> Seq("paramValue")
        )),
        ScheduleExpression = HourRateSchedule(1),
        Targets = Some(Seq(
          Target(
            Key = InstanceIds,
            Values = Seq("i-3457894578945"))
        ))
      )

      assoc.toJson shouldBe """{
                                          |  "name": "cfname",
                                          |  "Name": "AssociationName",
                                          |  "Parameters": {
                                          |    "param1": ["paramValue"]
                                          |  },
                                          |  "DocumentVersion": "1",
                                          |  "ScheduleExpression": "rate(1 hour)",
                                          |  "Targets": [{
                                          |    "Key": "InstanceIds",
                                          |    "Values": ["i-3457894578945"]
                                          |  }]
                                          |}""".stripMargin.parseJson
    }

    it("should serialize an association with tag targets") {
      val assoc = `AWS::SSM::Association`(
        name = "cfname",
        DocumentVersion = Some("1"),
        InstanceId = None,
        Name = Left("AssociationName"),
        Parameters = Some(Map(
          "param1" -> Seq("paramValue")
        )),
        ScheduleExpression = MinuteRateSchedule(15),
        Targets = Some(Seq(
          Target(
            Key = TagKey("someTag"),
            Values = Seq("someValue"))
        ))
      )

      assoc.toJson shouldBe """{
                                          |  "name": "cfname",
                                          |  "Name": "AssociationName",
                                          |  "Parameters": {
                                          |    "param1": ["paramValue"]
                                          |  },
                                          |  "DocumentVersion": "1",
                                          |  "ScheduleExpression": "rate(15 minutes)",
                                          |  "Targets": [{
                                          |    "Key": "tag:someTag",
                                          |    "Values": ["someValue"]
                                          |  }]
                                          |}""".stripMargin.parseJson
    }
  }



  describe("AWS::SSM::Parameter") {
    it("should serialize a String type") {
      val param = `AWS::SSM::Parameter`(
        "cfname",
        Some("ssmname"),
        Some("this is a parameter"),
        ParameterType.String,
        "value"
      )

      param.toJson shouldBe """{
                                          |  "name": "cfname",
                                          |  "Name": "ssmname",
                                          |  "Description": "this is a parameter",
                                          |  "Value": "value",
                                          |  "Type": "String"
                                          |}""".stripMargin.parseJson
    }

    it("should serialize a StringList type") {
      val param = `AWS::SSM::Parameter`(
        "cfname",
        Some("ssmname"),
        Some("this is a parameter"),
        ParameterType.StringList,
        "value1,value2"
      )

      param.toJson shouldBe """{
                                          |  "name": "cfname",
                                          |  "Name": "ssmname",
                                          |  "Description": "this is a parameter",
                                          |  "Value": "value1,value2",
                                          |  "Type": "StringList"
                                          |}""".stripMargin.parseJson
    }
  }
}
