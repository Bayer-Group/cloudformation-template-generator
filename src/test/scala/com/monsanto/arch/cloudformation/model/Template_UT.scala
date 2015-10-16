package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.{CidrBlock, AmazonTag, `AWS::EC2::VPC`}
import org.scalatest.{Matchers, FlatSpec}

class Template_UT extends FlatSpec with Matchers{
  it should "throw an exception if some resources have the same name" in {

    val thrown = the[IllegalArgumentException] thrownBy Template.collapse(Seq(
      `AWS::EC2::VPC`("repeat", CidrBlock(10, 10, 10, 10, 16), Seq(AmazonTag("a", "B"))),
      `AWS::EC2::VPC`("repeat", CidrBlock(10, 10, 10, 12, 16), Seq(AmazonTag("c", "C")))
    ))
    thrown.getMessage should include("repeat")
  }
}
