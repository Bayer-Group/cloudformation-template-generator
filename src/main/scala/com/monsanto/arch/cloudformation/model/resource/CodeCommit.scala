package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import com.monsanto.arch.cloudformation.model._
import spray.json.DefaultJsonProtocol._
import spray.json.{ DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat }

sealed trait CodeCommitEvent extends Product with Serializable

object CodeCommitEvent {
  private type T = CodeCommitEvent
  case object all extends T
  case object updateReference extends T
  case object createReference extends T
  case object deleteReference extends T
  implicit lazy val format: JsonFormat[T] = new JsonFormat[T] {
    def write(t: T): JsValue = JsString(t.toString)
    def read(json: JsValue): T = json match {
      case JsString("all") => all
      case JsString("updateReference") => updateReference
      case JsString("createReference") => createReference
      case JsString("deleteReference") => deleteReference
      case _ => throw DeserializationException(s"Can't parse as CodeCommitEvent: ${json}")
    }
  }
}

case class CodeCommitTrigger(
                              Branches: Option[TokenSeq[String]] = None,
                              CustomData: Option[String] = None,
                              DestinationArn: Option[Token[String]] = None,
                              Events: Option[Seq[CodeCommitEvent]] = None,
                              Name: String
                            )
object CodeCommitTrigger {
  private type T = CodeCommitTrigger
  implicit lazy val format: JsonFormat[T] = jsonFormat5(apply)
}

case class `AWS::CodeCommit::Repository`(
                                          name: String,
                                          RepositoryDescription: String,
                                          RepositoryName: Option[String] = None,
                                          Triggers: Option[Seq[CodeCommitTrigger]] = None,
                                          override val Condition: Option[ConditionRef] = None
                                        ) extends Resource[`AWS::CodeCommit::Repository`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::CodeCommit::Repository` = copy(Condition = newCondition)
}

object `AWS::CodeCommit::Repository` {
  implicit lazy val format : RootJsonFormat[`AWS::CodeCommit::Repository`] = jsonFormat5(apply)
}
