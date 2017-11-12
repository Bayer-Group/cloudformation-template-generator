package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.ResourceRef
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class EFS_UT extends FunSpec with Matchers {
  describe("AWS::EFS::FileSystem") {
    val resource = `AWS::EFS::FileSystem`(
      "test",
      FileSystemTags = Some(
        List(AmazonTag("Foo", "Bar"))
      ),
      Encrypted = Some(true),
      KmsKeyId = Some(`AWS::KMS::Key`(
        name = "test",
        KeyPolicy = PolicyDocument(
          Statement = List(
            PolicyStatement(
              Effect = "Allow",
              Action = List("dynamodb:*")
            )
          )
        )
      )),
      PerformanceMode = PerformanceMode.generalPurpose
    )

    it("should serialize to JSON") {
      resource.toJson.prettyPrint shouldBe """{
                                             |  "name": "test",
                                             |  "KmsKeyId": {
                                             |    "Ref": "test"
                                             |  },
                                             |  "Encrypted": true,
                                             |  "PerformanceMode": "generalPurpose",
                                             |  "FileSystemTags": [{
                                             |    "Key": "Foo",
                                             |    "Value": "Bar"
                                             |  }]
                                             |}""".stripMargin
    }

    it("throws an exception when KmsKeyId is set but Encrypted is false") {
      an [IllegalArgumentException] should be thrownBy resource.copy(Encrypted = false)
    }
  }

  describe("AWS::EFS::MountTarget") {
    val vpc = `AWS::EC2::VPC`(
      "vpc",
      CidrBlock(198,51,100,0,24),
      List()
    )
    val subnet = `AWS::EC2::Subnet`(
      "test",
      VpcId = ResourceRef(vpc),
      CidrBlock = CidrBlock(198,51,100,129,25),
      Tags = List()
    )
    val sg = `AWS::EC2::SecurityGroup`(
      "test",
      GroupDescription = "Test",
      VpcId = ResourceRef(vpc),
      None,
      None,
      List()
    )

    val resource = `AWS::EFS::MountTarget`(
      "test",
      FileSystemId = ResourceRef(`AWS::EFS::FileSystem`("test")),
      IpAddress = Some("198.51.100.1"),
      SecurityGroups = List(ResourceRef(sg)),
      SubnetId = ResourceRef(subnet)
    )
    it("should serialize to JSON") {
      resource.toJson.prettyPrint shouldBe """{
                                             |  "name": "test",
                                             |  "SecurityGroups": [{
                                             |    "Ref": "test"
                                             |  }],
                                             |  "IpAddress": "198.51.100.1",
                                             |  "FileSystemId": {
                                             |    "Ref": "test"
                                             |  },
                                             |  "SubnetId": {
                                             |    "Ref": "test"
                                             |  }
                                             |}""".stripMargin
    }
  }
}
