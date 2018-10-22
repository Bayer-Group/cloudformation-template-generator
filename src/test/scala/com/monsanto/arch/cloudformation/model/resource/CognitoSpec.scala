package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.JsonWritingMatcher
import org.scalatest.{FunSpec, Matchers}
import spray.json._
import com.monsanto.arch.cloudformation._

class CognitoSpec extends FunSpec with Matchers {

  describe("should generate pool document"){

    val userPool = `AWS::Cognito::UserPool`(
      name = "TestUserPool",
      UserPoolName = "TestPool"
    )


    userPool.toJson.toString() shouldEqual """{"name":"TestUserPool","UserPoolName":"TestPool"}"""

  }

}
