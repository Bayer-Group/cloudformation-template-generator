package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{Token, ConditionRef}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsValue, JsonFormat}

sealed trait Field {
  def Key: String
}

object Field {

  implicit object format extends JsonFormat[Field] {
    override def read(json: JsValue): Field = ???

    override def write(obj: Field) = obj match {
      case rf@RefField(_, _) => RefField.format.write(rf)
      case sf@StringField(_, _) => StringField.format.write(sf)
    }
  }

}

case class RefField(
                     Key: String,
                     RefValue: Token[String]
                   ) extends Field

object RefField {
  implicit val format: JsonFormat[RefField] = jsonFormat2(RefField.apply)
}

case class StringField(
                        Key: String,
                        StringValue: Token[String]
                      ) extends Field

object StringField {
  implicit val format: JsonFormat[StringField] = jsonFormat2(StringField.apply)
}


case class `AWS::DataPipeline::Pipeline`(
                                          name: String,
                                          Activate: Option[Token[Boolean]],
                                          Description: Option[Token[String]],
                                          Name: Token[String],
                                          ParameterObjects: Option[Seq[ParameterObject]],
                                          ParameterValues: Option[Seq[ParameterValue]],
                                          PipelineObjects: Seq[PipelineObject],
                                          PipelineTags: Option[Seq[PipelineTag]],
                                          override val Condition: Option[ConditionRef]
                                        ) extends Resource[`AWS::DataPipeline::Pipeline`] {
  override def when(newCondition: Option[ConditionRef]) = copy(Condition = newCondition)
}

object `AWS::DataPipeline::Pipeline` {
  implicit val format: JsonFormat[`AWS::DataPipeline::Pipeline`] = jsonFormat9(`AWS::DataPipeline::Pipeline`.apply)
}

case class ParameterObject(
                            Attributes: Seq[Attribute],
                            Id: String
                          )

object ParameterObject {
  implicit val foramt: JsonFormat[ParameterObject] = jsonFormat2(ParameterObject.apply)
}

case class Attribute(
                      Key: String,
                      StringValue: Option[Token[String]] = None
                    )

object Attribute {
  implicit val format: JsonFormat[Attribute] = jsonFormat2(Attribute.apply)
}

case class ParameterValue(
                           Id: String,
                           StringValue: Token[String]
                         )

object ParameterValue {
  implicit val format: JsonFormat[ParameterValue] = jsonFormat2(ParameterValue.apply)
}

case class PipelineObject(
                           Fields: Seq[Field],
                           Id: String,
                           Name: String
                         )

object PipelineObject {
  implicit val format: JsonFormat[PipelineObject] = jsonFormat3(PipelineObject.apply)
}

case class PipelineTag(
                        Key: String,
                        Value: Token[String]
                      )

object PipelineTag {
  implicit val format: JsonFormat[PipelineTag] = jsonFormat2(PipelineTag.apply)
}
