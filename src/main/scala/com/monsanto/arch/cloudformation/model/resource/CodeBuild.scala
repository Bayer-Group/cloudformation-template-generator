package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

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

case class `AWS::CodeBuild::Project`(
                                      name: String,
                                      Artifacts: CodeBuildProjectArtifacts,
                                      Description: Option[Token[String]] = None,
                                      EncryptionKey: Option[Token[String]] = None,
                                      Environment: CodeBuildProjectEnvironment,
                                      Name: Token[String],
                                      ServiceRole: Token[String],
                                      Source: CodeBuildProjectSource,
                                      Tags: Option[Seq[AmazonTag]] = None,
                                      TimeoutInMinutes: Option[Int] = None,
                                      override val Condition: Option[ConditionRef] = None
                                    ) extends Resource[`AWS::CodeBuild::Project`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::CodeBuild::Project` = copy(Condition = newCondition)
}

object `AWS::CodeBuild::Project` {
  implicit lazy val format : RootJsonFormat[`AWS::CodeBuild::Project`] = jsonFormat11(`AWS::CodeBuild::Project`.apply)
}
