package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsObject, JsString, JsValue, RootJsonFormat}


case class EncryptionKey(
                          Id : Token[String],
                          Type : String
                        )
object EncryptionKey {
  implicit lazy val format = jsonFormat2(EncryptionKey.apply)
}

case class ArtifactStore(EncryptionKey : Option[EncryptionKey]= None,
                         Location : Token[String],
                         Type : String)
object ArtifactStore {
  implicit lazy val format = jsonFormat3(ArtifactStore.apply)
}

case class DisableInboundStageTransition(Reason : String, StageName : String)
object DisableInboundStageTransition {
  implicit lazy val format = jsonFormat2(DisableInboundStageTransition.apply)
}



case class ActionTypeId(Category : String, Owner : String, Provider : String, Version : String)
object ActionTypeId {
  implicit lazy val format = jsonFormat4(ActionTypeId.apply)
}
case class PipelineArtifact(Name : Token[String])
object PipelineArtifact {
  implicit lazy val format = jsonFormat1(PipelineArtifact.apply)
}

case class PipelineStageBlocker(Name : String, Type : String)
object PipelineStageBlocker {
  implicit lazy val format = jsonFormat2(PipelineStageBlocker.apply)
}

sealed abstract class ConfigurationPropertyType(private[ConfigurationPropertyType] val `type` : String)
object ConfigurationPropertyType{
  implicit object format extends RootJsonFormat[ConfigurationPropertyType] {
    override def read(json: JsValue): ConfigurationPropertyType = ???

    override def write(obj: ConfigurationPropertyType): JsValue = JsString(obj.`type`)
  }
}
case object StringConfigurationPropertyType extends ConfigurationPropertyType("string")
case object NumberConfigurationPropertyType extends ConfigurationPropertyType("number")
case object BooleanConfigurationPropertyType extends ConfigurationPropertyType("boolean")

case class CustomActionTypeInputArtifactDetails(MaximumCount : Int, MinimumCount : Int)
object CustomActionTypeInputArtifactDetails {
  implicit lazy val format = jsonFormat2(CustomActionTypeInputArtifactDetails.apply)
}

case class CustomActionTypeSettings(
                                     EntityUrlTemplate : Option[Token[String]],
                                     ExecutionUrlTemplate : Option[Token[String]],
                                     RevisionUrlTemplate : Option[Token[String]],
                                     ThirdPartyConfigurationUrl : Option[Token[String]]
                                   )
object CustomActionTypeSettings {
  implicit lazy val format = jsonFormat4(CustomActionTypeSettings.apply)
}

case class PipelineStageAction(ActionTypeId : ActionTypeId,
                               Configuration : Option[JsObject] = None,
                               InputArtifacts: Option[Seq[PipelineArtifact]] = None,
                               Name : Token[String],
                               OutputArtifacts : Option[Seq[PipelineArtifact]] = None,
                               RoleArn : Option[Token[String]] = None,
                               RunOrder : Option[Token[Int]] = None
                              )
object PipelineStageAction {
  implicit lazy val format = jsonFormat7(PipelineStageAction.apply)
}

case class PipelineStage(Actions : Seq[PipelineStageAction],
                         Blockers : Option[Seq[PipelineStageBlocker]] = None,
                         Name : Token[String])
object PipelineStage {
  implicit lazy val format = jsonFormat3(PipelineStage.apply)
}

case class `AWS::CodePipeline::Pipeline`(
                                          name: String,
                                          ArtifactStore: ArtifactStore,
                                          DisableInboundStageTransitions: Option[Seq[DisableInboundStageTransition]] = None,
                                          Name: Option[Token[String]] = None,
                                          RestartExecutionOnUpdate: Option[Boolean] = None,
                                          RoleArn: Token[String],
                                          Stages: Seq[PipelineStage],
                                          override val DependsOn: Option[Seq[String]] = None,
                                          override val Condition: Option[ConditionRef] = None
                                        ) extends Resource[`AWS::CodePipeline::Pipeline`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)

  def pipelineName : Token[String] = ResourceRef(this)
}

object `AWS::CodePipeline::Pipeline` {
  implicit lazy val format : RootJsonFormat[`AWS::CodePipeline::Pipeline`] = jsonFormat9(`AWS::CodePipeline::Pipeline`.apply)
}

case class CustomActionConfigurationProperty(
                                              Description : Option[Token[String]],
                                              Key : Boolean,
                                              Name : Token[String],
                                              Queryable : Option[Boolean],
                                              Required : Boolean,
                                              Secret : Boolean,
                                              Type : ConfigurationPropertyType
                                            )
object CustomActionConfigurationProperty {
  implicit lazy val format = jsonFormat7(CustomActionConfigurationProperty.apply)
}

case class `AWS::CodePipeline::CustomActionType`(
                                                  name : String,
                                                  Category : Token[String],
                                                  ConfigurationProperties : Option[Seq[CustomActionConfigurationProperty]],
                                                  InputArtifactDetails : CustomActionTypeInputArtifactDetails,
                                                  OutputArtifactDetails : CustomActionTypeInputArtifactDetails,
                                                  Provider : Token[String],
                                                  Settings : Option[CustomActionTypeSettings],
                                                  Version : Option[Token[String]],
                                                  override val DependsOn: Option[Seq[String]] = None,
                                                  override val Condition : Option[ConditionRef] = None
                                                ) extends Resource[`AWS::CodePipeline::CustomActionType`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::CodePipeline::CustomActionType` = copy(Condition = newCondition)
}
object  `AWS::CodePipeline::CustomActionType` {
  implicit lazy val format : RootJsonFormat[`AWS::CodePipeline::CustomActionType`] = jsonFormat10(`AWS::CodePipeline::CustomActionType`.apply)
}
