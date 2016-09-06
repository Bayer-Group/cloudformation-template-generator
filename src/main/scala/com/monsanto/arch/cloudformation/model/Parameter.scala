package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import spray.json._
import DefaultJsonProtocol._
import scala.collection.immutable.ListMap
import scala.language.implicitConversions

sealed trait DefaultType

case class StringDefaultType(string: String) extends DefaultType
object StringDefaultType extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[StringDefaultType] {
    def read(json: JsValue): StringDefaultType = json.convertTo[String]
    def write(s: StringDefaultType) = JsString(s.string)
  }
}

case class IntDefaultType(int: Int) extends DefaultType
object IntDefaultType extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[IntDefaultType] {
    def read(json: JsValue): IntDefaultType = json.convertTo[String].toInt
    def write(i: IntDefaultType) = JsString(i.int.toString)
  }
}

case class StringListDefaultType(stringList: Seq[String]) extends DefaultType
object StringListDefaultType extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[StringListDefaultType] {
    def read(json: JsValue): StringListDefaultType = json.convertTo[String].split(",").toSeq
    def write(sl: StringListDefaultType) = JsString(sl.stringList.mkString(","))
  }
}

case class CidrBlockDefaultType(c: CidrBlock) extends DefaultType
object CidrBlockDefaultType extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[CidrBlockDefaultType] {
    def read(json: JsValue): CidrBlockDefaultType = CidrBlock(json.convertTo[String])
    def write(c: CidrBlockDefaultType) = c.c.toJsString
  }
}

case class CidrBlockListDefaultType(cl: Seq[CidrBlock]) extends DefaultType
object CidrBlockListDefaultType extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[CidrBlockListDefaultType] {
    def read(json: JsValue): CidrBlockListDefaultType = json.convertTo[String].split(",").toSeq.map(CidrBlock(_))
    def write(cl: CidrBlockListDefaultType) = JsString(cl.cl.map(_.toPlainString).mkString(","))
  }
}

case class DBInstanceEngineDefaultType(e: `AWS::RDS::DBInstance::Engine`) extends DefaultType
object DBInstanceEngineDefaultType extends DefaultJsonProtocol {
  implicit object format extends JsonFormat[DBInstanceEngineDefaultType] {
    def read(json: JsValue): DBInstanceEngineDefaultType = json.convertTo[`AWS::RDS::DBInstance::Engine`]
    def write(e: DBInstanceEngineDefaultType) = e.e.toJson
  }
}

object DefaultType extends DefaultJsonProtocol {

  implicit object format extends JsonWriter[DefaultType] {
    def write(d: DefaultType) = d.toJson
  }

  implicit def toStringDefaultType(s: String): StringDefaultType = StringDefaultType(s)
  implicit def toIntDefaultType(i: Int): IntDefaultType = IntDefaultType(i)
  implicit def toIntDefaultType(s: StringBackedInt): IntDefaultType = IntDefaultType(s.value)
  implicit def toStringListDefaultType(sl: Seq[String]): StringListDefaultType = StringListDefaultType(sl)
  implicit def toCidrBlockDefaultType(c: CidrBlock): CidrBlockDefaultType = CidrBlockDefaultType(c)
  implicit def toCidrBlockListDefaultType(cl: Seq[CidrBlock]): CidrBlockListDefaultType = CidrBlockListDefaultType(cl)
  implicit def toDBInstanceEngineDefaultType(e: `AWS::RDS::DBInstance::Engine`): DBInstanceEngineDefaultType = DBInstanceEngineDefaultType(e)
}

sealed abstract class Parameter(val Type: String) {
  type Rep // what logical type does this represent in real life? irrespective of CF file format
  def name:          String
  def Description:   Option[String]

  // this is the value that goes to the CloudFormation json file
  def Default:       Option[DefaultType]

  /*
   This is the value that goes to the parameters file that you would submit when instantiating
   the cloud formation stack. These values do NOT have to correspond with the validation rules of the parameter.
   It is intended to be used when you would like to have the parameter file users fill out to contain additional information
   or hints as to what the value should be. For example, the Default for a CidrBlock paramater might be "10.0.0.0/24"
   but the ConfigDefault could be "<changeme>" or "The CidrBlock of the prod VPC".
    */
  //
  def ConfigDefault: Option[String]
}
object Parameter extends DefaultJsonProtocol {
  implicit object seqFormat extends JsonWriter[Seq[Parameter]]{

    implicit object format extends JsonWriter[Parameter]{
      def write(obj: Parameter) = {
        val raw = obj.toJson
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

case class StringParameter (
                             name:                  String,
                             Description:           Option[String]              = None,
                             MinLength:             Option[StringBackedInt]     = None,
                             MaxLength:             Option[StringBackedInt]     = None,
                             AllowedPattern:        Option[String]              = None,
                             ConstraintDescription: Option[String]              = None,
                             Default:               Option[StringDefaultType]   = None,
                             AllowedValues:         Option[Seq[String]]         = None,
                             NoEcho:                Option[Boolean]             = None,
                             ConfigDefault:         Option[String]   = None
                            ) extends Parameter("String"){type Rep = String}
object StringParameter extends DefaultJsonProtocol {

  implicit val format: JsonFormat[StringParameter] = jsonFormat10(StringParameter.apply)

  def apply(name: String, Description: String): StringParameter =
    StringParameter(name, Some(Description), None, None, None, None, None, None, None)
  def apply(name: String, Description: String, Default: String): StringParameter =
    StringParameter(name, Some(Description), None, None, None, None, Some(Default), None, None)
  def apply(name: String, Description: String, AllowedValues: Seq[String], Default: String): StringParameter =
    StringParameter(name, Some(Description), None, None, None, None, Some(Default), Some(AllowedValues), None)
  def apply(name: String, Description: String, AllowedValues: Seq[String], ConstraintDescription: String, Default: String): StringParameter =
    StringParameter(name, Some(Description), None, None, None, Some(ConstraintDescription), Some(Default), Some(AllowedValues), None)
  def apply(name: String, Description: Option[String], AllowedValues: Option[Seq[String]], ConstraintDescription: Option[String], Default: Option[String]): StringParameter =
    StringParameter(name, Description, None, None, None, ConstraintDescription, Default.map(StringDefaultType(_)), AllowedValues, None)
}

case class StringListParameter (
                             name:                  String,
                             Description:           Option[String]                  = None,
                             Default:               Option[StringListDefaultType]   = None,
                             NoEcho:                Option[Boolean]                 = None,
                             ConfigDefault:         Option[String]                  = None
                             ) extends Parameter("CommaDelimitedList"){type Rep = Seq[String]}
object StringListParameter extends DefaultJsonProtocol {

  implicit val format: JsonFormat[StringListParameter] = jsonFormat5(StringListParameter.apply)

  def apply(name: String, Description: String): StringListParameter =
    StringListParameter(name, Some(Description), None, None, None)
  def apply(name: String, Description: String, Default: Seq[String]): StringListParameter =
    StringListParameter(name, Some(Description), Some(Default), None, None)
  def apply(name: String, Description: Option[String], Default: Option[Seq[String]]): StringListParameter =
    StringListParameter(name, Description, Default.map(StringListDefaultType(_)), None, None)
}


case class NumberParameter (
                            name:                  String,
                            Description:           Option[String]               = None,
                            MinValue:              Option[StringBackedInt]      = None,
                            MaxValue:              Option[StringBackedInt]      = None,
                            ConstraintDescription: Option[String]               = None,
                            Default:               Option[IntDefaultType]       = None,
                            AllowedValues:         Option[Seq[StringBackedInt]] = None,
                            ConfigDefault:         Option[String]               = None
                            ) extends Parameter("Number"){type Rep = Int}
object NumberParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[NumberParameter] = jsonFormat8(NumberParameter.apply)
}

case class `AWS::EC2::KeyPair::KeyName_Parameter`(
                                                  name:                  String,
                                                  Description:           Option[String],
                                                  ConstraintDescription: Option[String]             = None,
                                                  Default:               Option[StringDefaultType]  = None,
                                                  ConfigDefault:         Option[String]             = None
                                                  ) extends Parameter("AWS::EC2::KeyPair::KeyName"){type Rep = String}
object `AWS::EC2::KeyPair::KeyName_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::KeyPair::KeyName_Parameter`] = jsonFormat5(`AWS::EC2::KeyPair::KeyName_Parameter`.apply)
}

case class CidrBlockParameter(
                            name:          String,
                            Description:   Option[String],
                            Default:       Option[CidrBlockDefaultType] = None,
                            ConfigDefault: Option[String] = None
                          ) extends Parameter("String"){type Rep = CidrBlock}
object CidrBlockParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CidrBlockParameter] = jsonFormat4(CidrBlockParameter.apply)
}

case class CidrBlockListParameter(
                               name:          String,
                               Description:   Option[String],
                               Default:       Option[CidrBlockListDefaultType] = None,
                               ConfigDefault: Option[String] = None
                               ) extends Parameter("CommaDelimitedList"){type Rep = Seq[CidrBlock]}
object CidrBlockListParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CidrBlockListParameter] = jsonFormat4(CidrBlockListParameter.apply)
}


case class `AWS::EC2::SecurityGroup_Parameter`(
                                                name:          String,
                                                Description:   Option[String],
                                                Default:       Option[StringDefaultType] = None,
                                                ConfigDefault: Option[String] = None
                                                ) extends Parameter("String"){type Rep = ResourceRef[`AWS::EC2::SecurityGroup`]}
object `AWS::EC2::SecurityGroup_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::SecurityGroup_Parameter`] = jsonFormat4(`AWS::EC2::SecurityGroup_Parameter`.apply)
}

case class AMIIdParameter(
  name:                  String,
  Description:           Option[String],
  Default:               Option[StringDefaultType],
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
                                      Default:       Option[StringDefaultType] = None,
                                      ConfigDefault: Option[String] = None
                                      ) extends Parameter("AWS::EC2::VPC::Id"){type Rep = ResourceRef[`AWS::EC2::VPC`]}
object `AWS::EC2::VPC_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::VPC_Parameter`] = jsonFormat4(`AWS::EC2::VPC_Parameter`.apply)
}

case class `AWS::RDS::DBInstance::Engine_Parameter`(
                                                     name:          String,
                                                     Description:   Option[String],
                                                     Default:       Option[DBInstanceEngineDefaultType] = None,
                                                     ConfigDefault: Option[String]                      = None
                                                     ) extends Parameter("String"){type Rep = `AWS::RDS::DBInstance::Engine`}
object `AWS::RDS::DBInstance::Engine_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::Engine_Parameter`] = jsonFormat4(`AWS::RDS::DBInstance::Engine_Parameter`.apply)
}

case class `AWS::RDS::DBSubnetGroup_Parameter`(name:          String,
                                               Description:   Option[String],
                                               Default:       Option[StringDefaultType] = None,
                                               ConfigDefault: Option[String]            = None
                                              ) extends Parameter("String"){type Rep = ResourceRef[`AWS::RDS::DBSubnetGroup`]}
object `AWS::RDS::DBSubnetGroup_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBSubnetGroup_Parameter`] = jsonFormat4(`AWS::RDS::DBSubnetGroup_Parameter`.apply)
}

case class `AWS::EC2::Subnet_Parameter_List`(
                                              name:           String,
                                              Description:    Option[String],
                                              Default:        Option[StringListDefaultType]   = None,
                                              ConfigDefault:  Option[String]                  = None
                                            ) extends Parameter("List<AWS::EC2::Subnet::Id>"){type Rep = Seq[ResourceRef[`AWS::EC2::Subnet`]]}
object `AWS::EC2::Subnet_Parameter_List` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EC2::Subnet_Parameter_List`] = jsonFormat4(`AWS::EC2::Subnet_Parameter_List`.apply)
}

case class `AWS::S3::Bucket_Parameter`(
                                        name:          String,
                                        Description:   Option[String],
                                        Default:       Option[StringDefaultType]  = None,
                                        ConfigDefault: Option[String]             = None
                                      ) extends Parameter("String"){type Rep = ResourceRef[`AWS::S3::Bucket`]}
object `AWS::S3::Bucket_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::S3::Bucket_Parameter`] = jsonFormat4(`AWS::S3::Bucket_Parameter`.apply)
}

case class `AWS::SNS::Topic_Parameter`(
                                        name:          String,
                                        Description:   Option[String],
                                        Default:       Option[StringDefaultType] = None,
                                        ConfigDefault: Option[String] = None
                                      ) extends Parameter("String"){type Rep = ResourceRef[`AWS::SNS::Topic`]}
object `AWS::SNS::Topic_Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SNS::Topic_Parameter`] = jsonFormat4(`AWS::SNS::Topic_Parameter`.apply)
}

case class InputParameter(ParameterKey: String, ParameterValue: JsValue = "<changeMe>".toJson)
object InputParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[InputParameter] = jsonFormat2(InputParameter.apply)

  def templateParameterToInputParameter(Parameters: Option[Seq[Parameter]]): Option[Seq[InputParameter]] =
  Parameters.map{ _.map{ p:Parameter =>
    (p.Default, p.ConfigDefault) match {
      case (_      , Some(cd)) => InputParameter(p.name, cd.toJson)
      case (Some(d), None    ) => InputParameter(p.name, d.toJson)
      case (_      , _       ) => InputParameter(p.name)
     }
  }}
}
