package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.{`AWS::RDS::DBInstance::Engine`, `AWS::RDS::DBSubnetGroup`}
import spray.json.{DefaultJsonProtocol, JsValue, JsonFormat}


case class `AWS::RDS::DBInstance::Engine_Parameter`(
                                                     name:          String,
                                                     Description:   Option[String],
                                                     Default:       Option[Token[`AWS::RDS::DBInstance::Engine`]] = None,
                                                     ConfigDefault: Option[String] = None
                                                   ) extends Parameter("String"){type Rep = `AWS::RDS::DBInstance::Engine`}
object `AWS::RDS::DBInstance::Engine_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::Engine_Parameter`] = jsonFormat4(`AWS::RDS::DBInstance::Engine_Parameter`.apply)
}

case class `AWS::RDS::DBSubnetGroup_Parameter`(name:          String,
                                               Description:   Option[String],
                                               Default:       Option[String] = None,
                                               ConfigDefault: Option[String] = None
                                              ) extends Parameter("String"){type Rep = ResourceRef[`AWS::RDS::DBSubnetGroup`]}
object `AWS::RDS::DBSubnetGroup_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBSubnetGroup_Parameter`] = jsonFormat4(`AWS::RDS::DBSubnetGroup_Parameter`.apply)
}

import spray.json._
import DefaultJsonProtocol._

object ParameterFormatExt {
  val format :  PartialFunction[Parameter, JsValue] = {
    case e:  `AWS::RDS::DBInstance::Engine_Parameter` => e.toJson
    case s:  `AWS::RDS::DBSubnetGroup_Parameter`      => s.toJson
  }

  val inputParameters : PartialFunction[Parameter, InputParameter] = {
    case `AWS::RDS::DBInstance::Engine_Parameter`(n, _, _, Some(d)) => InputParameter(n, d.toJson)
    case `AWS::RDS::DBInstance::Engine_Parameter`(n, _, Some(d), None) => InputParameter(n, d.toJson)
    case `AWS::RDS::DBInstance::Engine_Parameter`(n, _, None, None) => InputParameter(n)
    case `AWS::RDS::DBSubnetGroup_Parameter`(n, None, _, _) => InputParameter(n)
    case `AWS::RDS::DBSubnetGroup_Parameter`(n, Some(d), None, _) => InputParameter(n, d.toJson)
    case `AWS::RDS::DBSubnetGroup_Parameter`(n, _, Some(d), _) => InputParameter(n, d.toJson)
  }
}