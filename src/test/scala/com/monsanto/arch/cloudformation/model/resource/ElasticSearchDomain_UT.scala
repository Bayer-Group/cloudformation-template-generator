package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.UNSAFEToken
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ElasticSearchDomain_UT extends FunSpec with Matchers {
  describe("An Elasticsearch::Domain") {
    it("should be serialize correctly") {
      val domain = `AWS::Elasticsearch::Domain`(
        name="testdomain",
        DomainName="testDomainName",
        Tags=Some(Seq(AmazonTag("testkey", "testValue"))),
        VPCOptions = Some(VPCOptions(Seq(UNSAFEToken("sg-1234567")), Seq("subnet-f1234567")))
      )

      val result = """{
                     |  "name": "testdomain",
                     |  "DomainName": "testDomainName",
                     |  "Tags": [{
                     |    "Key": "testkey",
                     |    "Value": "testValue"
                     |  }],
                     |  "VPCOptions": {
                     |    "SecurityGroupIds": ["sg-1234567"],
                     |    "SubnetIds": ["subnet-f1234567"]
                     |  }
                     |}""".stripMargin.parseJson

      domain.toJson shouldBe result
    }
  }
}

