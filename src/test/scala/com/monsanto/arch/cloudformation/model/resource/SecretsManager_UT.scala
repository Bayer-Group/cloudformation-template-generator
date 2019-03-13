package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{Template, Token, `Fn::Join`}
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class SecretManager_UT extends FunSpec with Matchers{

  describe("secret target attachment"){
    it("should generate code like the documentation example"){
      val secret = `AWS::SecretsManager::Secret`("MyRDSSecret",
        Description = Some("This is a Secrets Manager secret for an RDS DB instance"),
        GenerateSecretString = GenerateSecretString(
          SecretStringTemplate = Some("{\"username\": \"admin\"}"),
          GenerateStringKey = Some("password"),
          PasswordLength = Some(16),
          ExcludeCharacters = Some("\"@/\\")
        )
      )
      val rdsInstance = `AWS::RDS::DBInstance`("MyRDSInstance",Some(Left(20)),Token.fromString("db.t2.micro"),None,
        None,None,Some(Token.fromString("0")),Some("rotation-instance"),None,None,None,None,None,
        Some(`AWS::RDS::DBInstance::Engine`.MySQL),None,None,None,None,
        Some(`Fn::Join`("",Seq(
          Token.fromString("{{resolve:secretsmanager:"),
          Token.fromString(secret.name),
          Token.fromString(":SecretString:username}}")))),
        Some(`Fn::Join`("",Seq(
          Token.fromString("{{resolve:secretsmanager:"),
          Token.fromString(secret.name),
          Token.fromString(":SecretString:password}}")))),
        None,None,None,None,None,None,None,None,None,None,None,None,None,None
      )

      val attachment = `AWS::SecretsManager::SecretTargetAttachment`(
        "SecretRDSInstanceAttachment",
        Token.fromResource(secret),
        Token.fromResource(rdsInstance))
      val json = Template(Resources = Seq(secret,rdsInstance,attachment)).toJson
      val attachmentJson = json.asJsObject.fields("Resources").asJsObject.fields("SecretRDSInstanceAttachment")
      attachmentJson shouldBe JsObject(Map(
        "Properties" -> JsObject( Map(
          "SecretId" -> JsObject(Map("Ref" -> JsString("MyRDSSecret"))),
          "TargetId" -> JsObject(Map("Ref" -> JsString("MyRDSInstance"))),
          "TargetType" -> JsString("AWS::RDS::DBInstance")
        )),
        "Type" -> JsString("AWS::SecretsManager::SecretTargetAttachment"),
      ))
    }
  }
}
