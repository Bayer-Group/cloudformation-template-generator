package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token, `Fn::GetAtt`}
import spray.json.{DefaultJsonProtocol, JsonFormat}
import DefaultJsonProtocol._

case class `AWS::ApiGateway::Account`(
                                       name: String,
                                       CloudWatchRoleArn: Option[Token[String]] = None,
                                       override val Condition: Option[ConditionRef] = None
                                     ) extends Resource[`AWS::ApiGateway::Account`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Account` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Account`] = jsonFormat3(`AWS::ApiGateway::Account`.apply)
}

case class `AWS::ApiGateway::ApiKey`(
                                      name: String,
                                      Description: Option[String] = None,
                                      Enabled: Option[Token[Boolean]] = None,
                                      Name: Option[Token[String]] = None,
                                      StageKeys: Option[Seq[StageKey]] = None,
                                      override val Condition: Option[ConditionRef] = None
                                    ) extends Resource[`AWS::ApiGateway::ApiKey`] {
  def apiKey: Token[String] = ResourceRef(this)

  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::ApiKey` {
  implicit val format: JsonFormat[`AWS::ApiGateway::ApiKey`] = jsonFormat6(`AWS::ApiGateway::ApiKey`.apply)
}

case class StageKey(RestApiId: Option[Token[String]] = None, StageName: Option[Token[String]] = None)

object StageKey {
  implicit val format : JsonFormat[StageKey] = jsonFormat2(StageKey.apply)
}

case class `AWS::ApiGateway::Authorizer`(
                                          name: String,
                                          AuthorizerCredentials: Option[Token[String]] = None,
                                          AuthorizerResultTtlInSeconds: Option[Token[Int]] = None,
                                          AuthorizerUri: Token[String],
                                          IdentitySource: Option[Token[String]] = None,
                                          IdentityValidationExpression: Option[Token[String]] = None,
                                          Name: Option[Token[String]] = None,
                                          RestApiId: Option[Token[String]] = None,
                                          Type: Option[String] = None,
                                          override val Condition: Option[ConditionRef] = None
                                        ) extends Resource[`AWS::ApiGateway::Authorizer`] {
  def authorizerId: Token[String] = ResourceRef(this)

  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Authorizer` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Authorizer`] = jsonFormat10(`AWS::ApiGateway::Authorizer`.apply)
}


case class `AWS::ApiGateway::BasePathMapping`(
                                               name: String,
                                               BasePath: Option[Token[String]] = None,
                                               DomainName: Option[Token[String]] = None,
                                               RestApiId: Option[Token[String]] = None,
                                               Stage: Option[Token[String]] = None,
                                               override val Condition: Option[ConditionRef] = None
                                             ) extends Resource[`AWS::ApiGateway::BasePathMapping`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::BasePathMapping` {
  implicit val format: JsonFormat[`AWS::ApiGateway::BasePathMapping`] = jsonFormat6(`AWS::ApiGateway::BasePathMapping`.apply)
}

case class `AWS::ApiGateway::ClientCertificate`(
                                                 name: String,
                                                 Description: Option[Token[String]] = None,
                                                 override val Condition: Option[ConditionRef] = None
                                               ) extends Resource[`AWS::ApiGateway::ClientCertificate`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::ClientCertificate` {
  implicit val format: JsonFormat[`AWS::ApiGateway::ClientCertificate`] = jsonFormat3(`AWS::ApiGateway::ClientCertificate`.apply)
}

case class `AWS::ApiGateway::Deployment`(
                                          name: String,
                                          Description: Option[String] = None,
                                          RestApiId: Token[String],
                                          StageDescription: Option[StageDescription] = None,
                                          StageName: Option[Token[String]] = None,
                                          override val Condition: Option[ConditionRef] = None,
                                          override val DependsOn: Option[Seq[String]] = None
                                        ) extends Resource[`AWS::ApiGateway::Deployment`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Deployment` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Deployment`] = jsonFormat7(`AWS::ApiGateway::Deployment`.apply)
}

case class StageDescription(
                             CacheClusterEnabled: Option[Boolean] = None,
                             CacheClusterSize: Option[String] = None,
                             ClientCertificateId: Option[Token[String]] = None,
                             DeploymentId: Option[Token[String]] = None,
                             Description: Option[String] = None,
                             MethodSettings: Option[Seq[MethodSetting]] = None,
                             RestApiId: Option[Token[String]] = None,
                             StageName: Option[Token[String]] = None,
                             Variables: Option[Map[String, Token[String]]] = None
                           )
object StageDescription {
  implicit val format : JsonFormat[StageDescription] = jsonFormat9(StageDescription.apply)
}

case class MethodSetting(
                          CacheDataEncrypted: Option[Boolean] = None,
                          CacheTtlInSeconds: Option[Int] = None,
                          CachingEnabled: Option[Boolean] = None,
                          DataTraceEnabled: Option[Boolean] = None,
                          HttpMethod: Option[String] = None,
                          LoggingLevel: Option[String] = None,
                          MetricsEnabled: Option[Boolean] = None,
                          ResourcePath: Option[String] = None,
                          ThrottlingBurstLimit: Option[Int] = None,
                          ThrottlingRateLimit: Option[Double] = None
                        )
object MethodSetting {
  implicit val format : JsonFormat[MethodSetting] = jsonFormat10(MethodSetting.apply)
}

case class `AWS::ApiGateway::Method`(
                                      name: String,
                                      ApiKeyRequired: Option[Boolean] = None,
                                      AuthorizationType: String,
                                      AuthorizerId: Option[String] = None,
                                      HttpMethod: String,
                                      Integration: Option[Integration] = None,
                                      MethodResponses: Option[Seq[MethodResponse]] = None,
                                      RequestModels: Option[Map[String, Token[String]]] = None,
                                      RequestParameters: Option[Map[String, Token[Boolean]]] = None,
                                      ResourceId: Token[String],
                                      RestApiId: Token[String],
                                      override val Condition: Option[ConditionRef] = None
                                    ) extends Resource[`AWS::ApiGateway::Method`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Method` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Method`] = jsonFormat12(`AWS::ApiGateway::Method`.apply)
}

case class Integration(
                        CacheKeyParameters: Option[Seq[String]] = None,
                        CacheNamespace: Option[String] = None,
                        Credentials: Option[Token[String]] = None,
                        IntegrationHttpMethod: Option[String] = None,
                        IntegrationResponses: Option[Seq[IntegrationResponse]] = None,
                        RequestParameters: Option[Map[String, Token[String]]] = None,
                        RequestTemplates: Option[Map[String, Token[String]]] = None,
                        Type: Option[String] = None,
                        Uri: Option[Token[String]] = None
                      )
object Integration {
  implicit val format : JsonFormat[Integration] = jsonFormat9(Integration.apply)
}

case class IntegrationResponse(
                                ResponseParameters: Option[Map[String, String]] = None,
                                ResponseTemplates: Option[Map[String, Token[String]]] = None,
                                SelectionPattern: Option[String] = None,
                                StatusCode: Option[String] = None
                              )
object IntegrationResponse {
  implicit val format : JsonFormat[IntegrationResponse] = jsonFormat4(IntegrationResponse.apply)
}

case class MethodResponse(
                           ResponseModels: Option[Map[String, Token[String]]] = None,
                           ResponseParameters: Option[Map[String, Token[Boolean]]] = None,
                           StatusCode: Option[String] = None
                         )
object MethodResponse {
  implicit val format : JsonFormat[MethodResponse] = jsonFormat3(MethodResponse.apply)
}

case class `AWS::ApiGateway::Model`(
                                     name: String,
                                     ContentType: Option[String] = None,
                                     Description: Option[String] = None,
                                     Name: Option[String] = None,
                                     RestApiId: Token[String],
                                     Schema: Option[String] = None,
                                     override val Condition: Option[ConditionRef] = None
                                   ) extends Resource[`AWS::ApiGateway::Model`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Model` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Model`] = jsonFormat7(`AWS::ApiGateway::Model`.apply)
}

case class `AWS::ApiGateway::Resource`(
                                        name: String,
                                        ParentId: Token[String],
                                        PathPart: Option[String] = None,
                                        RestApiId: Token[String],
                                        override val Condition: Option[ConditionRef] = None
                                      ) extends Resource[`AWS::ApiGateway::Resource`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Resource` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Resource`] = jsonFormat5(`AWS::ApiGateway::Resource`.apply)
}

case class `AWS::ApiGateway::RestApi`(
                                       name: String,
                                       Name: Token[String],
                                       Body: Option[String] = None,
                                       BodyS3Location: Option[S3Location] = None,
                                       CloneFrom: Option[Token[String]] = None,
                                       Description: Option[String] = None,
                                       FailOnWarnings: Option[Boolean] = None,
                                       Parameters: Option[Seq[Token[String]]] = None,
                                       override val Condition: Option[ConditionRef] = None
                                     ) extends Resource[`AWS::ApiGateway::RestApi`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)

  def RootResourceId : Token[String] = `Fn::GetAtt`(Seq(name, "RootResourceId"))
}
object `AWS::ApiGateway::RestApi` {
  implicit val format: JsonFormat[`AWS::ApiGateway::RestApi`] = jsonFormat9(`AWS::ApiGateway::RestApi`.apply)
}

case class S3Location(
                       Bucket: Option[Token[String]] = None,
                       ETag: Option[String] = None,
                       Key: Option[Token[String]] = None,
                       Version: Option[Token[String]] = None
                     )
object S3Location {
  implicit val format : JsonFormat[S3Location] = jsonFormat4(S3Location.apply)
}

case class `AWS::ApiGateway::Stage`(
                                     name: String,
                                     CacheClusterEnabled: Option[Boolean] = None,
                                     CacheClusterSize: Option[String] = None,
                                     ClientCertificateId: Option[Token[String]] = None,
                                     DeploymentId: Token[String],
                                     Description: Option[String] = None,
                                     MethodSettings: Option[Seq[MethodSetting]] = None,
                                     RestApiId: Option[Token[String]] = None,
                                     StageName: Option[Token[String]] = None,
                                     Variables: Map[String, Token[String]],
                                     override val Condition: Option[ConditionRef] = None
                                   ) extends Resource[`AWS::ApiGateway::Stage`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}
object `AWS::ApiGateway::Stage` {
  implicit val format: JsonFormat[`AWS::ApiGateway::Stage`] = jsonFormat11(`AWS::ApiGateway::Stage`.apply)
}
