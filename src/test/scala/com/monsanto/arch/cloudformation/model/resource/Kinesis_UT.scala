package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.resource.S3VersioningStatus.Enabled
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class Kinesis_UT extends FunSpec with Matchers {
  describe("Stream") {
    val streamName = "stream"
    val shardCount = 1
    val retentionPeriodHours = 5
    val stream = `AWS::Kinesis::Stream`(
      name = streamName,
      Name = Some("Foo"),
      RetentionPeriodHours = Some(retentionPeriodHours),
      ShardCount = shardCount,
      Tags = Seq(AmazonTag("Name", streamName))
    )

    it("should write a valid Kinesis stream") {
      stream.toJson shouldEqual JsObject(Map(
        "name" -> JsString("stream"),
        "Name" -> JsString("Foo"),
        "RetentionPeriodHours" -> JsNumber(5),
        "ShardCount" -> JsNumber(1),
        "Tags" -> JsArray(JsObject(Map("Key" -> JsString("Name"), "Value" -> JsString("stream"))))
      ))
    }

    it("should have properly set public fields") {
      stream.name shouldEqual streamName
      stream.ShardCount shouldEqual IntToken(shardCount)
      stream.RetentionPeriodHours foreach (_ shouldEqual IntToken(retentionPeriodHours))
      stream.Tags.get shouldEqual Seq(AmazonTag("Name", streamName))
    }
  }

  describe("FirehoseDeliveryStream") {
    it("should create a plausible S3 firehose stream config") {
      val bucket = `AWS::S3::Bucket`("s3bucket", None,
        VersioningConfiguration = Some(S3VersioningConfiguration(Enabled)))
      val deliveryRole = `AWS::IAM::Role`("deliveryRole",
        PolicyDocument(Seq(PolicyStatement(
          "Allow",
          Some(DefinedPrincipal(Map("Service" -> Token.fromString("firehose.amazonaws.com")))),
          Seq("sts:AssumeRole"),
          Sid = Some(" "),
          Condition = Some(Map("StringEquals" -> Map("sts:ExternalId" -> SimplePolicyConditionValue("AWS::AccountId"))))
        ))))
      val policy = `AWS::IAM::Policy`("deliveryPolicy",
        PolicyDocument(Seq(
          PolicyStatement("Allow",
            Action = Seq("s3:AbortMultipartUpload",
              "s3:GetBucketLocation",
              "s3:GetObject",
              "s3:ListBucket",
              "s3:ListBucketMultipartUploads",
              "s3:PutObject"
            ),
            Resource = Some(Seq(`Fn::Join`("", Seq(s"arn:aws:s3:::", ResourceRef(bucket))),
              `Fn::Join`("", Seq(s"arn:aws:s3:::", ResourceRef(bucket), "/*"))))

          ))
        ), "firehose_delivery_policy",
        Roles = Some(Seq(ResourceRef(deliveryRole))))

      val stream = `AWS::KinesisFirehose::DeliveryStream`.s3(
        "deliveryStream",
        ExtendedS3DestinationConfiguration(`Fn::Join`("", Seq(s"arn:aws:s3:::", ResourceRef(bucket))),
          ResourceRef(deliveryRole),
          Some(BufferingHints(Some(60), Some(50))), None,
          Some(CompressionFormat.UNCOMPRESSED), None, Some("firehose/"),
          Some(ProcessingConfiguration.enabled(
            Seq(Processor(
              Seq(ProcessorParameter("LambdaArn",
                Token.fromFunction(`Fn::GetAtt`(Seq("myLambda","Arn")))))
            ))))
        ), DependsOn = Some(Seq(policy.name)))
      val tJson = Template(Resources = Seq(stream, policy,deliveryRole,bucket))

      val deliveryJson = tJson.toJson.asJsObject.fields("Resources")
        .asJsObject.fields("deliveryStream").asJsObject()
        deliveryJson.fields("DependsOn") shouldBe JsArray(JsString("deliveryPolicy"))

      deliveryJson.fields("Properties").asJsObject().fields("ExtendedS3DestinationConfiguration")
        .asJsObject.fields("BufferingHints") shouldBe JsObject(
        Map("IntervalInSeconds" -> JsNumber(60), "SizeInMBs" -> JsNumber(50))
      )
    }
  }
}
