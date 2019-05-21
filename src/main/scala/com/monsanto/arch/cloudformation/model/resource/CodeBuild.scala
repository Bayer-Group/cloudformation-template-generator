package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.DefaultJsonProtocol._
import spray.json.{ JsonFormat, RootJsonFormat }

case class CodeBuildProjectArtifacts(
                                      Location: Option[Token[String]] = None,
                                      Name: Option[Token[String]] = None,
                                      NamespaceTag: Option[Token[String]] = None,
                                      Packaging: Option[String] = None,
                                      Path: Option[Token[String]] = None,
                                      Type: Option[String] = None
                                    )
object CodeBuildProjectArtifacts {
  implicit lazy val format = jsonFormat6(CodeBuildProjectArtifacts.apply)
}

case class ProjectEnvironmentVariable(Name: String, Value: Token[String], Type: Option[String] = None)
object ProjectEnvironmentVariable {
  implicit lazy val format = jsonFormat3(ProjectEnvironmentVariable.apply)
}

case class CodeBuildProjectEnvironment(
                                        ComputeType: Token[String],
                                        EnvironmentVariables: Option[Seq[ProjectEnvironmentVariable]] = None,
                                        Image: Token[String],
                                        Type: String
                                      )
object CodeBuildProjectEnvironment {
  implicit lazy val format = jsonFormat4(CodeBuildProjectEnvironment.apply)
}

case class CodeBuildSourceAuth(
  Type: String = "OAUTH",
  Resource: Option[String] = None
)

object CodeBuildSourceAuth {
  implicit val format: JsonFormat[CodeBuildSourceAuth] = jsonFormat2(apply)
}

case class CodeBuildProjectSource(
  Auth: Option[CodeBuildSourceAuth] = None,
  BuildSpec: Option[String] = None,
  GitCloneDepth: Option[Int] = None,
  InsecureSsl: Option[Boolean] = None,
  Location: Option[Token[String]] = None,
  ReportBuildStatus: Option[Boolean] = None,
  SourceIdentifier: Option[String] = None,
  Type: String
)

object CodeBuildProjectSource {
  implicit lazy val format = jsonFormat8(CodeBuildProjectSource.apply)
}

final case class CodeBuildProjectCache(Location: Token[String], Type: CodeBuildProjectCacheType)

object CodeBuildProjectCache {
  private type T = CodeBuildProjectCache
  implicit lazy val format : RootJsonFormat[T] = jsonFormat2(apply)
}

sealed trait CodeBuildProjectCacheType extends Product with Serializable

object CodeBuildProjectCacheType {
  private type T = CodeBuildProjectCacheType
  case object NO_CACHE extends T
  case object S3 extends T
  val values = Seq(NO_CACHE, S3)
  implicit val format: JsonFormat[T] = new EnumFormat[T](values)
}

sealed trait CodeBuildWebhookFilterType extends Product with Serializable
object CodeBuildWebhookFilterType {
  case object EVENT extends CodeBuildWebhookFilterType
  case object ACTOR_ACCOUNT_ID extends CodeBuildWebhookFilterType
  case object HEAD_REF extends CodeBuildWebhookFilterType
  case object BASE_REF extends CodeBuildWebhookFilterType
  case object FILE_PATH extends CodeBuildWebhookFilterType
  val values: Seq[CodeBuildWebhookFilterType] = Seq(EVENT, ACTOR_ACCOUNT_ID, HEAD_REF, BASE_REF, FILE_PATH)
  implicit val format: JsonFormat[CodeBuildWebhookFilterType] = new EnumFormat[CodeBuildWebhookFilterType](values)
}

case class CodeBuildWebhookFilter(
  ExcludeMatchedPatter: Option[Boolean] = None,
  Pattern: String,
  Type: CodeBuildWebhookFilterType
)

object CodeBuildWebhookFilter {
  implicit val format: JsonFormat[CodeBuildWebhookFilter] = jsonFormat3(apply)
}

case class CodeBuildTriggers(
  Webhook: Boolean = false,
  FilterGroups: Seq[Seq[CodeBuildWebhookFilter]] = Seq.empty
)

object CodeBuildTriggers {
  implicit val format: JsonFormat[CodeBuildTriggers] = jsonFormat2(apply)
}

case class CodeBuildVpcConfig(
  SecurityGroupIds: Option[Seq[Token[String]]] = None,
  Subnets: Option[Seq[Token[String]]] = None,
  VpcId: Option[Token[String]] = None
)

object CodeBuildVpcConfig {
  implicit val format: JsonFormat[CodeBuildVpcConfig] = jsonFormat3(apply)
}

case class `AWS::CodeBuild::Project`(
  name: String,
  Artifacts: CodeBuildProjectArtifacts,
  BadgeEnabled: Option[Boolean] = None,
  Cache: Option[CodeBuildProjectCache] = None,
  Description: Option[Token[String]] = None,
  EncryptionKey: Option[Token[String]] = None,
  Environment: CodeBuildProjectEnvironment,
  Name: Option[Token[String]] = None,
  QueuedTimeoutInMinutes: Option[Int] = None,
  SecondaryArtifacts: Option[Seq[CodeBuildProjectArtifacts]] = None,
  SecondarySources: Option[Seq[CodeBuildProjectSource]] = None,
  ServiceRole: Token[String],
  Source: CodeBuildProjectSource,
  Tags: Option[Seq[AmazonTag]] = None,
  TimeoutInMinutes: Option[Int] = None,
  Triggers: Option[CodeBuildTriggers] = None,
  VpcConfig: Option[CodeBuildVpcConfig] = None,
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::CodeBuild::Project`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::CodeBuild::Project` = copy(Condition = newCondition)
}

object `AWS::CodeBuild::Project` {
  implicit lazy val format : RootJsonFormat[`AWS::CodeBuild::Project`] = jsonFormat19(`AWS::CodeBuild::Project`.apply)
}
