package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{Template, `Fn::GetAtt`, ResourceRef}
import org.scalatest.{FunSpec, Matchers}
import spray.json
import spray.json._

class Route53_UT extends FunSpec with Matchers {

  describe("Custom::RemoteRecordSet"){
    it ("should serialize as expected") {
      val record = `Custom::RemoteRoute53RecordSet`.generalRecord(
      "TestRecord",
      "TestServiceToken",
      "TestDestinationRole",
      "TestHostName",
      Route53RecordType.CNAME,
      "TestZone",
      Seq("cnn.com"),
      "60")

      val expectedJson =
        """
          |{
          |  "AWSTemplateFormatVersion": "2010-09-09",
          |  "Description": "",
          |  "Resources": {
          |    "TestRecord": {
          |      "Properties": {
          |        "DestinationRole": "TestDestinationRole",
          |        "Name": "TestHostName",
          |        "ServiceToken": "TestServiceToken",
          |        "HostedZoneName": "TestZone",
          |        "ResourceRecords": [
          |          "cnn.com"
          |        ],
          |        "TTL": "60",
          |        "Type": "CNAME"
          |      },
          |      "Type": "Custom::RemoteRoute53RecordSet"
          |    }
          |  }
          |}
        """.stripMargin.parseJson
      Template.fromResource(record).toJson should be (expectedJson)
    }
  }
}
