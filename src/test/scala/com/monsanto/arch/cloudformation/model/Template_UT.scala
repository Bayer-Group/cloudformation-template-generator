package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import com.monsanto.arch.cloudformation.model.simple._
import org.scalatest.{Matchers, FlatSpec}
import spray.json.DefaultJsonProtocol._

class Template_UT extends FlatSpec with Matchers with VPC with Subnet with AvailabilityZone with SecurityGroup with EC2 {

  val parameter1 = StringParameter(name = "param1")
  val parameter2 = StringParameter(name = "param2")

  val condition1 = Condition(name = "cond1", function = "fn1")
  val condition2 = Condition(name = "cond2", function = "fn2")

  val mapping1 = Mapping[String](name = "map1", Map())
  val mapping2 = Mapping[String](name = "map2", Map())

  val resource1 = `AWS::S3::Bucket`(name = "bucket1", None)
  val resource2 = `AWS::S3::Bucket`(name = "bucket2", None)

  val output1 = Output("output1", "", `Fn::Join`("", Seq()))
  val output2 = Output("output2", "", `Fn::Join`("", Seq()))

  it should "throw an exception if some resources have the same name" in {

    val thrown = the[IllegalArgumentException] thrownBy Template.collapse(Seq(
      `AWS::EC2::VPC`("repeat", CidrBlock(10, 10, 10, 10, 16), Seq(AmazonTag("a", "B"))),
      `AWS::EC2::VPC`("repeat", CidrBlock(10, 10, 10, 12, 16), Seq(AmazonTag("c", "C")))
    ))
    thrown.getMessage should include("repeat")
  }

  it should "be able to add a parameter" in {
    val template = Template.EMPTY ++ parameter1
    template should equal(Template.EMPTY.copy(Parameters = Some(Seq(parameter1))))
  }

  it should "be able to add parameters" in {
    val params = Seq(parameter1, parameter2)
    val template = Template.EMPTY ++ params
    template should equal(Template.EMPTY.copy(Parameters = Some(params)))
  }

  it should "be able to add a condition" in {
    val template = Template.EMPTY ++ condition1
    template should equal(Template.EMPTY.copy(Conditions = Some(Seq(condition1))))
  }

  it should "be able to add conditions" in {
    val conditions = Seq(condition1, condition2)
    val template = Template.EMPTY ++ conditions
    template should equal(Template.EMPTY.copy(Conditions = Some(conditions)))
  }

  it should "be able to add a mapping" in {
    val template = Template.EMPTY ++ mapping1
    template should equal(Template.EMPTY.copy(Mappings = Some(Seq(mapping1))))
  }

  it should "be able to add mappings" in {
    val mappings = Seq(mapping1, mapping2)
    val template = Template.EMPTY ++ mappings
    template should equal(Template.EMPTY.copy(Mappings = Some(mappings)))
  }

  it should "be able to add a resource" in {
    val template = Template.EMPTY ++ resource1
    template should equal(Template.fromResource(resource1))
  }

  it should "be able to add resources" in {
    val resources = Seq(resource1, resource2)
    val template = Template.EMPTY ++ resources
    template should equal(Template.fromResources(resources))
  }

  val cidr = CidrBlock(10,1,1,1,32)

  it should "be able to add a security group routable" in {
    withVpc(cidr) { implicit vpc =>
      withAZ("") { implicit az =>
        withSubnet("subnet", cidr) { implicit subnet =>
          val sg = securityGroup("sg", "")
          val sgr = ec2("instance", "", "", AMIId(""), Seq(sg), Seq())

          val template = Template.EMPTY ++ sgr
          template should equal(Template.EMPTY.copy(Routables = Seq(sgr), Resources = sgr.resources))
          template
        }
      }
    }
  }

  it should "be able to add security group routables" in {
    withVpc(cidr) { implicit vpc =>
      withAZ("") { implicit az =>
        withSubnet("subnet", cidr) { implicit subnet =>
          val sg = securityGroup("sg", "")
          val sgr1 = ec2("instance1", "", "", AMIId(""), Seq(sg), Seq())
          val sgr2 = ec2("instance2", "", "", AMIId(""), Seq(sg), Seq())

          val sgrs = Seq(sgr1, sgr2)

          val template = Template.EMPTY ++ sgrs
          template should equal(Template.EMPTY.copy(Routables = sgrs, Resources = sgrs.flatMap(_.resources)))
          template
        }

      }
    }
  }

  it should "be able to add a output" in {
    val template = Template.EMPTY ++ output1
    template should equal(Template.EMPTY.copy(Outputs = Some(Seq(output1))))
  }

  it should "be able to add an output of type Token[String]" in new JsonWritingMatcher {
    import spray.json._
    val token : Token[String] = ResourceRef(resource1)
    val output = Output(
      name = "out1",
      Description = "test",
      Value = token
    )
    output.toJson shouldMatch
      s"""
        |{
        | "Description": "test",
        | "Value": {
        |   "Ref": "${resource1.name}"
        | }
        |}
      """.stripMargin
  }


  it should "be able to add an output of type Token[String] with a static export" in new JsonWritingMatcher {
    import spray.json._
    val token : Token[String] = ResourceRef(resource1)
    val output = Output(
      name = "out1",
      Description = "test",
      Value = token,
      Export = Some("export-test")
    )
    output.toJson shouldMatch
      s"""
         |{
         | "Description": "test",
         | "Value": {
         |   "Ref": "${resource1.name}"
         | },
         | "Export": {
         |   "Name": "export-test"
         | }
         |}
      """.stripMargin
  }


  it should "be able to add an output of type Token[String] with a function export" in new JsonWritingMatcher {
    import spray.json._
    val token : Token[String] = ResourceRef(resource1)
    val output = Output(
      name = "out1",
      Description = "test",
      Value = token,
      Export = Some(`Fn::Sub`(s"$${AWS::StackName}-test-export"))
    )
    output.toJson shouldMatch
      s"""
         |{
         | "Description": "test",
         | "Value": {
         |   "Ref": "${resource1.name}"
         | },
         | "Export": {
         |   "Name": {"Fn::Sub": "$${AWS::StackName}-test-export"}
         | }
         |}
      """.stripMargin
  }

  it should "be able to add outputs" in {
    val outputs = Seq(output1, output2)
    val template = Template.EMPTY ++ outputs
    template should equal(Template.EMPTY.copy(Outputs = Some(outputs)))
  }

  it should "take description 1 if description 2 is None" in {
    val template1 = Template(Description = Some("1"), Resources = Seq.empty)
    val template2 = Template(Description = None, Resources = Seq.empty)
    (template1 ++ template2).Description should equal (Some("1"))
  }

  it should "take description 2 if description 1 is None" in {
    val template1 = Template(Description = None, Resources = Seq.empty)
    val template2 = Template(Description = Some("2"), Resources = Seq.empty)
    (template1 ++ template2).Description should equal (Some("2"))
  }
  it should "take description 2 if description 1 is set" in {
    val template1 = Template(Description = Some("1"), Resources = Seq.empty)
    val template2 = Template(Description = Some("2"), Resources = Seq.empty)
    (template1 ++ template2).Description should equal (Some("2"))
  }

  it should "take version 1 if version 2 is None" in {
    val template1 = Template(AWSTemplateFormatVersion = Some("1"), Resources = Seq.empty)
    val template2 = Template(AWSTemplateFormatVersion = None, Resources = Seq.empty)
    (template1 ++ template2).AWSTemplateFormatVersion should equal (Some("1"))
  }

  it should "take version 2 if version 1 is None" in {
    val template1 = Template(AWSTemplateFormatVersion = None, Resources = Seq.empty)
    val template2 = Template(AWSTemplateFormatVersion = Some("2"), Resources = Seq.empty)
    (template1 ++ template2).AWSTemplateFormatVersion should equal (Some("2"))
  }
  it should "take version 2 if version 1 is set" in {
    val template1 = Template(AWSTemplateFormatVersion = Some("1"), Resources = Seq.empty)
    val template2 = Template(AWSTemplateFormatVersion = Some("2"), Resources = Seq.empty)
    (template1 ++ template2).AWSTemplateFormatVersion should equal (Some("2"))
  }
}
