package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

case class `AWS::IAM::InstanceProfile`(name: String, Path: String, Roles: Seq[Token[`AWS::IAM::Role`]],
  override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::IAM::InstanceProfile`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::InstanceProfile` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::InstanceProfile`] = jsonFormat4(`AWS::IAM::InstanceProfile`.apply)
}

case class `AWS::IAM::User`(
                            name: String,
                            Path: Option[Token[String]],
                            Groups: Option[Seq[Token[String]]] = None,
                            override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::IAM::User`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::User` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::User`] = jsonFormat4(`AWS::IAM::User`.apply)
}

case class `AWS::IAM::AccessKey`(
                                  name: String,
                                  UserName: Token[String],
                                  Status: AccessKeyStatus,
                                  Serial: Option[StringBackedInt] = None,
                                  override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::IAM::AccessKey`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::AccessKey` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::AccessKey`] = jsonFormat5(`AWS::IAM::AccessKey`.apply)
}

sealed trait AccessKeyStatus
object AccessKeyStatus extends DefaultJsonProtocol{
  case object Active extends AccessKeyStatus
  case object Inactive extends AccessKeyStatus
  implicit val format : JsonFormat[AccessKeyStatus] = new JsonFormat[AccessKeyStatus] {
    override def write(obj: AccessKeyStatus): JsValue = JsString(obj.toString)

    override def read(json: JsValue): AccessKeyStatus = json.toString match {
      case "Active" => Active
      case "Inactive" => Inactive
    }
  }
}
case class `AWS::IAM::Role`(
  name:                     String,
  AssumeRolePolicyDocument: PolicyDocument,
  Policies:                 Seq[Policy],
  Path:                     String,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::IAM::Role`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::Role` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::Role`] = jsonFormat5(`AWS::IAM::Role`.apply)
}
case class PolicyStatement(
  Effect:    String,
  Principal: Option[PolicyPrincipal] = None,
  Action:    Seq[String],
  Resource:  Option[Token[String]] = None
  )
object PolicyStatement extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PolicyStatement] = jsonFormat4(PolicyStatement.apply)
}

// TODO: Make this not a string

sealed trait PolicyPrincipal
object PolicyPrincipal extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PolicyPrincipal] = new JsonFormat[PolicyPrincipal] {
    def write(obj: PolicyPrincipal) =
      obj match {
        case i: DefinedPrincipal => i.targets.toJson
        case WildcardPrincipal => JsString("*")
      }
    //TODO
    def read(json: JsValue) = ???
  }
}
case class DefinedPrincipal(targets: Map[String, Seq[Token[String]]]) extends PolicyPrincipal
case object WildcardPrincipal extends PolicyPrincipal

case class Policy(PolicyName: String, PolicyDocument: PolicyDocument)
object Policy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Policy] = jsonFormat2(Policy.apply)
}
case class PolicyDocument(Statement: Seq[PolicyStatement])
object PolicyDocument extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PolicyDocument] = jsonFormat1(PolicyDocument.apply)
}
