package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.DefaultJsonProtocol._
import spray.json.{ JsonFormat, RootJsonFormat }

case class CodeBuildProjectArtifacts(
                                      Location: Option[Token[String]],
                                      Name: Option[Token[String]],
                                      NamespaceTag: Option[Token[String]] = None,
                                      Packaging: Option[String] = None,
                                      Path: Option[Token[String]] = None,
                                      Type: String
                                    )
object CodeBuildProjectArtifacts {
  implicit lazy val format = jsonFormat6(CodeBuildProjectArtifacts.apply)
}

case class ProjectEnvironmentVariable(Name: String, Value: Token[String])
object ProjectEnvironmentVariable {
  implicit lazy val format = jsonFormat2(ProjectEnvironmentVariable.apply)
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

case class CodeBuildProjectSource(
                                   BuildSpec: Option[String] = None,
                                   Location: Option[Token[String]],
                                   Type: String
                                 )
object CodeBuildProjectSource {
  implicit lazy val format = jsonFormat3(CodeBuildProjectSource.apply)
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

case class `AWS::CodeBuild::Project`(
                                      name: String,
                                      Artifacts: CodeBuildProjectArtifacts,
                                      Cache: Option[CodeBuildProjectCache] = None,
                                      Description: Option[Token[String]] = None,
                                      EncryptionKey: Option[Token[String]] = None,
                                      Environment: CodeBuildProjectEnvironment,
                                      Name: Token[String],
                                      ServiceRole: Token[String],
                                      Source: CodeBuildProjectSource,
                                      Tags: Option[Seq[AmazonTag]] = None,
                                      TimeoutInMinutes: Option[Int] = None,
                                      override val Condition: Option[ConditionRef] = None,
                                      override val DependsOn: Option[Seq[String]] = None
                                    ) extends Resource[`AWS::CodeBuild::Project`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::CodeBuild::Project` = copy(Condition = newCondition)
}

object `AWS::CodeBuild::Project` {
  implicit lazy val format : RootJsonFormat[`AWS::CodeBuild::Project`] = jsonFormat13(`AWS::CodeBuild::Project`.apply)
}
