package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import spray.json._
import DefaultJsonProtocol._

import scala.collection.immutable.ListMap
import scala.language.implicitConversions

/**
 * Created by Ryan Richt on 2/15/15
 */

sealed abstract class Parameter(val Type: String) {
  type Rep // what logical type does this represent in real life? irrespective of CF file format
  def name:          String
  def Description:   Option[String]
  def ConfigDefault: Option[String]
}
object Parameter extends DefaultJsonProtocol {
  implicit object seqFormat extends JsonWriter[Seq[Parameter]]{

    implicit object format extends JsonWriter[Parameter]{
      def write(obj: Parameter) = {

        val raw = obj match {
          case s:  StringParameter                          => s.toJson
          case sl: StringListParameter                      => sl.toJson(StringListParameter.format)
          case n:  NumberParameter                          => n.toJson
          case c:  CidrBlockParameter                       => c.toJson
          case cl: CidrBlockListParameter                   => cl.toJson(CidrBlockListParameter.format)
          case b:  BooleanParameter                         => b.toJson
          case a:  AMIIdParameter                           => a.toJson
          case c:  `AWS::EC2::SecurityGroup_Parameter`      => c.toJson
          case k:  `AWS::EC2::KeyPair::KeyName_Parameter`   => k.toJson
          case v:  `AWS::EC2::VPC_Parameter`                => v.toJson
          case e:  `AWS::RDS::DBInstance::Engine_Parameter` => e.toJson
          case s:  `AWS::RDS::DBSubnetGroup_Parameter`      => s.toJson
          case s:  `AWS::EC2::Subnet_Parameter_List`        => s.toJson
        }

        JsObject( raw.asJsObject.fields - "name" - "ConfigDefault" + ("Type" -> JsString(obj.Type)) )
      }
    }

    def write(objs: Seq[Parameter]) = JsObject( ListMap(objs.map( o => o.name -> o.toJson ): _*) )
  }
}

case class StringBackedInt(value: Int)
object StringBackedInt extends DefaultJsonProtocol {
  implicit def fromInt(i: Int): StringBackedInt = StringBackedInt(i)

  implicit val format: JsonFormat[StringBackedInt] = new JsonFormat[StringBackedInt]{
    def write(obj: StringBackedInt) = JsString(obj.value.toString)
    def read(json: JsValue) = StringBackedInt( json.convertTo[String].toInt )
  }
}

case class BooleanParameter(name:                  String,
                            Description:           Option[String]          = None,
                            Default:               Option[Boolean]          = None,
                            ConfigDefault:         Option[String]          = None
                           ) extends Parameter("String"){
  type Rep = Boolean
  val AllowedValues = Seq("true", "false")
}

object BooleanParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[BooleanParameter] = new RootJsonFormat[BooleanParameter] {
    override def write(p: BooleanParameter) = JsObject(Map(
      "name" -> JsString(p.name),
      "AllowedValues" -> JsArray(p.AllowedValues.map(JsString(_)).toVector))++
      p.Description.map("Description" -> JsString(_)) ++
      p.Default.map(s => "Default" -> JsString(s.toString)) ++
      p.ConfigDefault.map("ConfigDefault" -> JsString(_))
    )

    override def read(json: JsValue) = ???
  }
}

case class StringParameter (
                            name:                  String,
                            Description:           Option[String]          = None,
                            MinLength:             Option[StringBackedInt] = None,
                            MaxLength:             Option[StringBackedInt] = None,
                            AllowedPattern:        Option[String]          = None,
                            ConstraintDescription: Option[String]          = None,
                            Default:               Option[String]          = None,
                            AllowedValues:         Option[Seq[String]]     = None,
                            NoEcho:                Option[Boolean]         = None,
                            ConfigDefault:         Option[String]          = None
                            ) extends Parameter("String"){type Rep = String}
object StringParameter extends DefaultJsonProtocol {

  implicit val format: JsonFormat[StringParameter] =
    jsonFormat10[String, Option[String], Option[StringBackedInt], Option[StringBackedInt], Option[String],
      Option[String], Option[String], Option[Seq[String]], Option[Boolean], Option[String], StringParameter](StringParameter.apply)

  def apply(name: String, Description: String): StringParameter = StringParameter(name, Some(Description), None, None, None, None, None, None, None)
  def apply(name: String, Description: String, Default: String): StringParameter = StringParameter(name, Some(Description), None, None, None, None, Some(Default), None, None)
  def apply(name: String, Description: String, AllowedValues: Seq[String], Default: String): StringParameter = StringParameter(name, Some(Description), None, None, None, None, Some(Default), Some(AllowedValues), None)
  def apply(name: String, Description: String, AllowedValues: Seq[String], ConstraintDescription: String, Default: String): StringParameter = StringParameter(name, Some(Description), None, None, None, Some(ConstraintDescription), Some(Default), Some(AllowedValues), None)
}

case class StringListParameter (
                             name:                  String,
                             Description:           Option[String]                = None,
                             Default:               Option[Seq[String]]           = None,
                             NoEcho:                Option[Boolean]               = None,
                             ConfigDefault:         Option[String]                = None
                             ) extends Parameter("CommaDelimitedList"){type Rep = Seq[String]}
object StringListParameter extends DefaultJsonProtocol {

  implicit object format extends JsonWriter[StringListParameter]{
    def write(p: StringListParameter) =
      JsObject(Map("name" -> JsString(p.name)) ++
        p.Description.map("Description" -> JsString(_)) ++
        p.Default.map(s => "Default" -> JsString(s.mkString(","))) ++
        p.NoEcho.map("NoEcho" -> JsBoolean(_)) ++
        p.ConfigDefault.map("ConfigDefault" -> JsString(_))
      )
  }

  def apply(name: String, Description: String): StringListParameter = StringListParameter(name, Some(Description), None, None, None)
  def apply(name: String, Description: String, Default: Seq[String]): StringListParameter = StringListParameter(name, Some(Description), Some(Default), None, None)
}


case class NumberParameter (
                            name:                  String,
                            Description:           Option[String] = None,
                            MinValue:              Option[StringBackedInt] = None,
                            MaxValue:              Option[StringBackedInt] = None,
                            ConstraintDescription: Option[String] = None,
                            Default:               Option[StringBackedInt] = None,
                            AllowedValues:         Option[Seq[StringBackedInt]] = None,
                            ConfigDefault:         Option[String] = None
                            ) extends Parameter("Number"){type Rep = Int}
object NumberParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[NumberParameter] = jsonFormat8(NumberParameter.apply)
}

case class `AWS::EC2::KeyPair::KeyName_Parameter`(
                                                  name:                  String,
                                                  Description:           Option[String],
                                                  ConstraintDescription: Option[String] = None,
                                                  Default:               Option[String] = None,
                                                  ConfigDefault:         Option[String] = None
                                                  ) extends Parameter("AWS::EC2::KeyPair::KeyName"){type Rep = String}
object `AWS::EC2::KeyPair::KeyName_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::KeyPair::KeyName_Parameter`] = jsonFormat5(`AWS::EC2::KeyPair::KeyName_Parameter`.apply)
}

case class CidrBlockParameter(
                            name:          String,
                            Description:   Option[String],
                            Default:       Option[CidrBlock] = None,
                            ConfigDefault: Option[String] = None
                          ) extends Parameter("String"){type Rep = CidrBlock}
object CidrBlockParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CidrBlockParameter] = jsonFormat4(CidrBlockParameter.apply)
}

case class CidrBlockListParameter(
                               name:          String,
                               Description:   Option[String],
                               Default:       Option[Seq[CidrBlock]] = None,
                               ConfigDefault: Option[String] = None
                               ) extends Parameter("CommaDelimitedList"){type Rep = Seq[CidrBlock]}
object CidrBlockListParameter extends DefaultJsonProtocol {
  implicit object format extends JsonWriter[CidrBlockListParameter]{
    def write(p: CidrBlockListParameter) =
      JsObject(Map("name" -> JsString(p.name)) ++
        p.Description.map("Description" -> JsString(_)) ++
        p.Default.map(l => "Default" -> JsString(l.map(c => c.toJsString.value).mkString(","))) ++
        p.ConfigDefault.map("ConfigDefault" -> JsString(_))
      )
  }
}


case class `AWS::EC2::SecurityGroup_Parameter`(
                                                name:          String,
                                                Description:   Option[String],
                                                Default:       Option[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None,
                                                ConfigDefault: Option[String] = None
                                                ) extends Parameter("String"){type Rep = ResourceRef[`AWS::EC2::SecurityGroup`]}
object `AWS::EC2::SecurityGroup_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::SecurityGroup_Parameter`] = jsonFormat4(`AWS::EC2::SecurityGroup_Parameter`.apply)
}

case class AMIIdParameter(
  name:                  String,
  Description:           Option[String],
  Default:               Option[Token[MappingRef[AMIId]]],
  AllowedValues:         Option[Seq[String]],
  ConstraintDescription: Option[String],
  ConfigDefault:         Option[String] = None
  ) extends Parameter("String"){type Rep = MappingRef[AMIId]}
object AMIIdParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AMIIdParameter] = jsonFormat6(AMIIdParameter.apply)
}

case class `AWS::EC2::VPC_Parameter`(
                                      name:          String,
                                      Description:   Option[String],
                                      Default:       Option[Token[ResourceRef[`AWS::EC2::VPC`]]] = None,
                                      ConfigDefault: Option[String] = None
                                      ) extends Parameter("AWS::EC2::VPC::Id"){type Rep = ResourceRef[`AWS::EC2::VPC`]}
object `AWS::EC2::VPC_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPC_Parameter`] = jsonFormat4(`AWS::EC2::VPC_Parameter`.apply)
}

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

case class `AWS::EC2::Subnet_Parameter_List`(
                                              name:          String,
                                              Description:   Option[String],
                                              ConfigDefault: Option[String] = None
                                            ) extends Parameter("List<AWS::EC2::Subnet::Id>"){type Rep = Seq[ResourceRef[`AWS::EC2::Subnet`]]}
object `AWS::EC2::Subnet_Parameter_List` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::Subnet_Parameter_List`] = jsonFormat3(`AWS::EC2::Subnet_Parameter_List`.apply)
}

case class InputParameter(ParameterKey: String, ParameterValue: JsValue = "<changeMe>".toJson)
object InputParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[InputParameter] = jsonFormat2(InputParameter.apply)

  // is there a better way to do this?
  def templateParameterToInputParameter(Parameters: Option[Seq[Parameter]]): Option[Seq[InputParameter]] =
    // prefer ConfigDefault to Default when it is present
    Parameters.map(o => o.map(p => p match {
      case StringParameter(n, _, _, _, _, _, _, _, _, Some(d)) => InputParameter(n, d.toJson)
      case StringParameter(n, _, _, _, _, _, Some(d), _, _, None) => InputParameter(n, d.toJson)
      case StringParameter(n, _, _, _, _, _, None, _, _, None) => InputParameter(n)
      case StringListParameter(n, _, _, _, Some(d)) => InputParameter(n, d.toJson)
      case StringListParameter(n, _, Some(d), _, None) => InputParameter(n, JsString(d.mkString(",")))
      case StringListParameter(n, _, None, _, None) => InputParameter(n)
      case NumberParameter(n, _, _, _, _, _, _, Some(d)) => InputParameter(n, d.toJson)
      case NumberParameter(n, _, _, _, _, Some(d), _, None) => InputParameter(n, d.toJson)
      case NumberParameter(n, _, _, _, _, None, _, None) => InputParameter(n)
      case BooleanParameter(n, _, _, Some(d)) => InputParameter(n, d.toJson)
      case BooleanParameter(n, _, Some(d), None) => InputParameter(n, d.toString.toJson)
      case BooleanParameter(n, _, None, None) => InputParameter(n)
      case `AWS::EC2::KeyPair::KeyName_Parameter`(n, _, _, _, Some(d)) => InputParameter(n, d.toJson)
      case `AWS::EC2::KeyPair::KeyName_Parameter`(n, _, _, Some(d), None) => InputParameter(n, d.toJson)
      case `AWS::EC2::KeyPair::KeyName_Parameter`(n, _, _, None, None) => InputParameter(n)
      case CidrBlockParameter(n, _, _, Some(d)) => InputParameter(n, d.toJson)
      case CidrBlockParameter(n, _, Some(d), None) => InputParameter(n, d.toJson)
      case CidrBlockParameter(n, _, None, None) => InputParameter(n)
      case CidrBlockListParameter(n, _, _, Some(d)) => InputParameter(n, d.toJson)
      case CidrBlockListParameter(n, _, Some(d), None) => InputParameter(n, JsString(d.map(_.toJsString.value).mkString(",")))
      case CidrBlockListParameter(n, _, None, None) => InputParameter(n)
      case `AWS::EC2::SecurityGroup_Parameter`(n, _, _, Some(d)) => InputParameter(n, d.toJson)
      case `AWS::EC2::SecurityGroup_Parameter`(n, _, Some(d), None) => InputParameter(n, d.toJson)
      case `AWS::EC2::SecurityGroup_Parameter`(n, _, None, None) => InputParameter(n)
      case AMIIdParameter(n, _, _, _, _, Some(d)) => InputParameter(n, d.toJson)
      case AMIIdParameter(n, _, Some(d), _, _, None) => InputParameter(n, d.toJson)
      case AMIIdParameter(n, _, None, _, _, None) => InputParameter(n)
      case `AWS::EC2::VPC_Parameter`(n, _, _, Some(d)) => InputParameter(n, d.toJson)
      case `AWS::EC2::VPC_Parameter`(n, _, Some(d), None) => InputParameter(n, d.toJson)
      case `AWS::EC2::VPC_Parameter`(n, _, None, None) => InputParameter(n)
      case `AWS::RDS::DBInstance::Engine_Parameter`(n, _, _, Some(d)) => InputParameter(n, d.toJson)
      case `AWS::RDS::DBInstance::Engine_Parameter`(n, _, Some(d), None) => InputParameter(n, d.toJson)
      case `AWS::RDS::DBInstance::Engine_Parameter`(n, _, None, None) => InputParameter(n)
      case `AWS::RDS::DBSubnetGroup_Parameter`(n, None, _, _) => InputParameter(n)
      case `AWS::RDS::DBSubnetGroup_Parameter`(n, Some(d), None, _) => InputParameter(n, d.toJson)
      case `AWS::RDS::DBSubnetGroup_Parameter`(n, _, Some(d), _) => InputParameter(n, d.toJson)
      case `AWS::EC2::Subnet_Parameter_List`(n, _, _) => InputParameter(n, "".toJson)
    }))
}
