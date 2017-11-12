package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class `AWS::KMS::Alias`(
                            name : String,
                            AliasName : Token[String],
                            TargetKeyId : Token[String],
                            override val DependsOn: Option[Seq[String]] = None,
                            override val Condition : Option[ConditionRef] = None
                            ) extends Resource[`AWS::KMS::Alias`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::KMS::Alias` = copy(Condition = newCondition)
}
object `AWS::KMS::Alias` {

  implicit val format : RootJsonFormat[`AWS::KMS::Alias`] = jsonFormat5(`AWS::KMS::Alias`.apply)
}

case class `AWS::KMS::Key`(
                          name : String,
                          Description : Option[Token[String]] = None,
                          Enabled : Option[Token[Boolean]] = None,
                          EnableKeyRotation : Option[Token[Boolean]] = None,
                          KeyPolicy : PolicyDocument,
                          override val DependsOn: Option[Seq[String]] = None,
                          override val Condition: Option[ConditionRef] = None
                          ) extends Resource[`AWS::KMS::Key`] with HasArn {
  override def when(newCondition: Option[ConditionRef]): `AWS::KMS::Key` = copy(Condition = newCondition)

  override def arn: Token[String] = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::KMS::Key` {

  implicit val format : RootJsonFormat[`AWS::KMS::Key`] = jsonFormat7(`AWS::KMS::Key`.apply)
}
