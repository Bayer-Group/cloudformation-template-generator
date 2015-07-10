package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import org.scalatest.FunSpec
import org.scalatest.Matchers

class TemplateLookupTest_UT extends FunSpec with Matchers {
  describe("CrazyLookup") {
    it("Should lookup resources with the correct type") {
      val expected = `AWS::EC2::VPC`(
        name = "TestVPC",
        CidrBlock = CidrBlock(0,0,0,0,0),
        Tags = Seq.empty[AmazonTag]
      )

      val template = Template.fromResource(expected)

      assert(expected === template.lookup[`AWS::EC2::VPC`]("TestVPC"))
    }

    it("Should throw exception when given the wrong type") {
      val expected = `AWS::EC2::VPC`(
        name = "TestVPC",
        CidrBlock = CidrBlock(0,0,0,0,0),
        Tags = Seq.empty[AmazonTag]
      )

      val template = Template.fromResource(expected)

      intercept[ClassCastException] {
        template.lookup[`AWS::EC2::Subnet`]("TestVPC")
      }
    }

    it("Should throw exception when resources is empty") {
      val template = Template.EMPTY

      intercept[RuntimeException] {
        template.lookup[`AWS::EC2::Subnet`]("TestVPC")
      }
    }

    it("Should throw exception when resource doesn't exist") {
      val otherThing = `AWS::EC2::VPC`(
        name = "TestVPC",
        CidrBlock = CidrBlock(0,0,0,0,0),
        Tags = Seq.empty[AmazonTag]
      )

      val template = Template.fromResource(otherThing)

      intercept[RuntimeException] {
        template.lookup[`AWS::EC2::VPC`]("NoVPC")
      }
    }

    it("Should throw exception when multiple resources of same name") {
      val expected = `AWS::EC2::VPC`(
        name = "TestVPC",
        CidrBlock = CidrBlock(0,0,0,0,0),
        Tags = Seq.empty[AmazonTag]
      )

      val template = Template.fromResource(expected) ++ expected

      intercept[RuntimeException] {
        template.lookup[`AWS::EC2::Subnet`]("TestVPC")
      }
    }
  }
}
