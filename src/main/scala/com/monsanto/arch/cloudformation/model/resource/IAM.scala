package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import spray.json._

import scala.annotation.implicitNotFound
import scala.language.implicitConversions

/**
 * Created by Ryan Richt on 2/28/15
 */

case class `AWS::IAM::InstanceProfile`(
  name:  String,
  Path:  String,
  Roles: Seq[ResourceRef[`AWS::IAM::Role`]],
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::IAM::InstanceProfile`] with HasArn {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

  override def arn : Token[String] = `Fn::GetAtt`(Seq(name, "Arn"))
}
object `AWS::IAM::InstanceProfile` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::InstanceProfile`] = jsonFormat4(`AWS::IAM::InstanceProfile`.apply)
}

case class `AWS::IAM::User`(
                            name: String,
                            Path: Option[Token[String]],
                            Groups: Option[Seq[ResourceRef[`AWS::IAM::Group`]]] = None,
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

case class `AWS::IAM::ManagedPolicy`(
  name:           String,
  PolicyDocument: PolicyDocument,
  Description:    Option[String] = None,
  Path:           Option[String] = None,
  Groups:         Option[Seq[ResourceRef[`AWS::IAM::Group`]]] = None,
  Roles:          Option[Seq[ResourceRef[`AWS::IAM::Role`]]] = None,
  Users:          Option[Seq[ResourceRef[`AWS::IAM::User`]]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::IAM::ManagedPolicy`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::ManagedPolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::ManagedPolicy`] = jsonFormat8(`AWS::IAM::ManagedPolicy`.apply)
}


case class AWSManagedPolicy(name: String) {
  def buildARN = s"arn:aws:iam::aws:policy/$name"
}

case class ManagedPolicyARN private(resource: Either[ResourceRef[`AWS::IAM::ManagedPolicy`], AWSManagedPolicy])
object ManagedPolicyARN extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ManagedPolicyARN] = new JsonFormat[ManagedPolicyARN]{
    def write(obj: ManagedPolicyARN) =
      obj.resource match {
        case Left(ref) => ref.toJson
        case Right(arn) => JsString(arn.buildARN)
      }
    def read(json: JsValue) = ???
  }

  implicit def fromAWSManagedPolicy(p: AWSManagedPolicy): ManagedPolicyARN = ManagedPolicyARN(Right(p))
  implicit def fromManagedPolicy(p: ResourceRef[`AWS::IAM::ManagedPolicy`]): ManagedPolicyARN = ManagedPolicyARN(Left(p))
}

case class `AWS::IAM::Role`(
  name:                     String,
  AssumeRolePolicyDocument: PolicyDocument,
  ManagedPolicyArns:        Option[Seq[ManagedPolicyARN]] = None,
  Path:                     Option[Token[String]] = None,
  Policies:                 Option[Seq[Policy]] = None,
  RoleName:                 Option[Token[String]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::IAM::Role`] with HasArn {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))
}
object `AWS::IAM::Role` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::Role`] = jsonFormat7(`AWS::IAM::Role`.apply)
}

sealed trait PolicyConditionValue
case class ListPolicyConditionValue(values : Seq[String]) extends PolicyConditionValue
case class SimplePolicyConditionValue(value : String) extends PolicyConditionValue
case class TokenPolicyConditionValue(value : Token[String]) extends PolicyConditionValue
case class TokenListPolicyConditionValue(value : TokenSeq[String]) extends PolicyConditionValue

object PolicyConditionValue extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[PolicyConditionValue] {
    override def read(json: JsValue) : PolicyConditionValue = ??? //ListPolicyConditionValue(implicitly[JsonFormat[Seq[String]]].read(json))

    override def write(obj: PolicyConditionValue) = obj match {
      case ListPolicyConditionValue(values)       => values.toJson
      case SimplePolicyConditionValue(value)      => value.toJson
      case TokenPolicyConditionValue(value)       => value.toJson
      case TokenListPolicyConditionValue(values)  => values.toJson
    }
  }
  implicit def seq2Value(s : Seq[String]): PolicyConditionValue = ListPolicyConditionValue(s)
  implicit def simple2Value(s : String): PolicyConditionValue = SimplePolicyConditionValue(s)
  implicit def token2Value(s : Token[String]): PolicyConditionValue = TokenPolicyConditionValue(s)
  implicit def tokenList2Value(s : Seq[Token[String]]): PolicyConditionValue = TokenListPolicyConditionValue(s)
}


case class PolicyStatement(
  Effect:    String,
  Principal: Option[PolicyPrincipal] = None,
  Action:    Seq[String],
  Resource:  Option[Token[String]] = None,
  Condition: Option[Map[String, Map[String, PolicyConditionValue]]] = None
)
object PolicyStatement extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PolicyStatement] = jsonFormat5(PolicyStatement.apply)
}

case class `AWS::IAM::Group`(
  name:              String,
  ManagedPolicyArns: Option[Seq[ResourceRef[`AWS::IAM::ManagedPolicy`]]] = None,
  Path:              Option[Token[String]] = None,
  Policies:          Option[Seq[Policy]] = None,
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::IAM::Group`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::Group` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::Group`] = jsonFormat5(`AWS::IAM::Group`.apply)
}

@implicitNotFound("you can only specify one of Groups, Roles, or Users")
trait ValidPolicyCombo[G,R,U]
object ValidPolicyCombo {
  implicit object onlyG extends ValidPolicyCombo[Some[Seq[ResourceRef[`AWS::IAM::Group`]]], None.type, None.type]
  implicit object onlyR extends ValidPolicyCombo[None.type, Some[Seq[ResourceRef[`AWS::IAM::Role`]]], None.type]
  implicit object onlyU extends ValidPolicyCombo[None.type, None.type, Some[Seq[ResourceRef[`AWS::IAM::User`]]]]
}

case class `AWS::IAM::Policy` private (
  name:           String,
  PolicyDocument: PolicyDocument,
  PolicyName:     String,
  Groups:         Option[Seq[ResourceRef[`AWS::IAM::Group`]]],
  Roles:          Option[Seq[ResourceRef[`AWS::IAM::Role`]]],
  Users:          Option[Seq[ResourceRef[`AWS::IAM::User`]]],
  override val Condition: Option[ConditionRef] = None
  ) extends Resource[`AWS::IAM::Policy`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::IAM::Policy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::IAM::Policy`] = jsonFormat7(`AWS::IAM::Policy`.apply)

  def from[
    G <: Option[Seq[ResourceRef[`AWS::IAM::Group`]]],
    R <: Option[Seq[ResourceRef[`AWS::IAM::Role`]]],
    U <: Option[Seq[ResourceRef[`AWS::IAM::User`]]]
  ](
    name:           String,
    PolicyDocument: PolicyDocument,
    PolicyName:     String,
    Groups:         G,
    Roles:          R,
    Users:          U,
    Condition: Option[ConditionRef] = None
  )(implicit ev1: ValidPolicyCombo[G,R,U]) =
    new `AWS::IAM::Policy`(name, PolicyDocument, PolicyName, Groups, Roles, Users, Condition)
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
case class DefinedPrincipal(targets: Map[String, TokenSeq[String]]) extends PolicyPrincipal
case object WildcardPrincipal extends PolicyPrincipal

case class Policy(PolicyName: String, PolicyDocument: PolicyDocument)
object Policy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Policy] = jsonFormat2(Policy.apply)
}

case class PolicyDocument(Statement: Seq[PolicyStatement], Version : Option[IAMPolicyVersion] = None)
object PolicyDocument extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PolicyDocument] = jsonFormat2(PolicyDocument.apply)
}

sealed trait IAMPolicyVersion
object IAMPolicyVersion extends DefaultJsonProtocol {
  case object `2012-10-17`      extends IAMPolicyVersion
  case object `2008-10-17`      extends IAMPolicyVersion

  val values = Seq(`2012-10-17`, `2008-10-17`)
  implicit val format: JsonFormat[IAMPolicyVersion] = new EnumFormat[IAMPolicyVersion](values)
}
