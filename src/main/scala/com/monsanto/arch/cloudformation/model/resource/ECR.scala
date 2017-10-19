package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token}
import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
  * Created by Berthold Alheit on 13/09/16.
  */
case class `AWS::ECR::Repository`(
  name: String,
  RepositoryName: Option[Token[String]],
  RepositoryPolicyText: Option[Token[PolicyDocument]] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::ECR::Repository`] {
  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::ECR::Repository`(name, RepositoryName, RepositoryPolicyText, DependsOn, newCondition)
}
object `AWS::ECR::Repository` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ECR::Repository`] = jsonFormat5(`AWS::ECR::Repository`.apply)
}
