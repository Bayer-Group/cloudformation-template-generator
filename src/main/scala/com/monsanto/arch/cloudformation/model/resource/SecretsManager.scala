package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token}
import spray.json.{DefaultJsonProtocol, JsonFormat}

case class GenerateSecretString(
  ExcludeUppercase: Option[Boolean] = None,
  RequireEachIncludedType: Option[Boolean] = None,
  IncludeSpace: Option[Boolean] = None,
  ExcludeCharacters: Option[String] = None,
  GenerateStringKey: Option[String] = None,
  PasswordLength: Option[Int] = None,
  ExcludePunctuation: Option[Boolean] = None,
  ExcludeLowercase: Option[Boolean] = None,
  SecretStringTemplate: Option[String] = None,
  ExcludeNumbers: Option[Boolean] = None
)

object GenerateSecretString extends DefaultJsonProtocol {
  implicit val format: JsonFormat[GenerateSecretString] = jsonFormat10(GenerateSecretString.apply)
}

case class `AWS::SecretsManager::Secret`(
  name: String,
  Name: Option[String] = None,
  Description: Option[String] = None,
  KmsKeyId: Option[Token[String]] = None,
  SecretString: Option[String] = None,
  GenerateSecretString: Option[GenerateSecretString] = None,
  Tags: Seq[ResourceTag] = Seq.empty,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::SecretsManager::Secret`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::SecretsManager::Secret` {

  def withSecretString(name: String,
    SecretString: String,
    Name: Option[String] = None,
    Description: Option[String] = None,
    KmsKeyId: Option[Token[String]] = None,
    Tags: Seq[ResourceTag] = Seq.empty,
    DependsOn: Option[Seq[String]] = None,
    Condition: Option[ConditionRef] = None) : `AWS::SecretsManager::Secret` = new `AWS::SecretsManager::Secret`(
    name, Name, Description, KmsKeyId, Some(SecretString), None, Tags, DependsOn, Condition)

  def withGeneratedSecret(name: String,
    GenerateSecretString: GenerateSecretString,
    Name: Option[String] = None,
    Description: Option[String] = None,
    KmsKeyId: Option[Token[String]] = None,
    Tags: Seq[ResourceTag] = Seq.empty,
    DependsOn: Option[Seq[String]] = None,
    Condition: Option[ConditionRef] = None): `AWS::SecretsManager::Secret` = new `AWS::SecretsManager::Secret`(
    name, Name, Description, KmsKeyId, None, Some(GenerateSecretString), Tags, DependsOn, Condition)

  implicit val format: JsonFormat[`AWS::SecretsManager::Secret`] = {
    import DefaultJsonProtocol._
    jsonFormat9(`AWS::SecretsManager::Secret`.apply)
  }
}

case class RotationRules(AutomaticallyAfterDays: Int)

object RotationRules extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RotationRules] = jsonFormat1(RotationRules.apply)
}

case class `AWS::SecretsManager::RotationSchedule`(
  name: String,
  SecretId: Token[`AWS::SecretsManager::Secret`],
  RotationLambdaARN: Option[Token[String]] = None,
  RotationRules: Option[RotationRules] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::SecretsManager::RotationSchedule`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::SecretsManager::RotationSchedule` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SecretsManager::RotationSchedule`] = jsonFormat6(`AWS::SecretsManager::RotationSchedule`.apply)
}

case class `AWS::SecretsManager::ResourcePolicy`(
  name: String,
  secretId: Token[`AWS::SecretsManager::Secret`],
  ResourcePolicy: Policy,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::SecretsManager::ResourcePolicy`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}


object `AWS::SecretsManager::ResourcePolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SecretsManager::ResourcePolicy`] = jsonFormat5(`AWS::SecretsManager::ResourcePolicy`.apply)


}
//In practice there is also TargetType DBCluster, but that's not in CFTG at the time of this writing
case class `AWS::SecretsManager::SecretTargetAttachment`(
  name: String,
  SecretId: Token[ResourceRef[`AWS::SecretsManager::Secret`]],
  TargetId: Token[ResourceRef[`AWS::RDS::DBInstance`]],
  TargetType: String = "AWS::RDS::DBInstance",
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::SecretsManager::SecretTargetAttachment`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::SecretsManager::SecretTargetAttachment` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SecretsManager::SecretTargetAttachment`] = jsonFormat6(`AWS::SecretsManager::SecretTargetAttachment`.apply)
}
