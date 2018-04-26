package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
  * Created by bkrodg on 1/13/16.
  */

case class `AWS::CloudFormation::WaitConditionHandle`(name: String,
                                                      override val Condition: Option[ConditionRef] = None,
                                                      override val DependsOn: Option[Seq[String]] = None)
  extends Resource[`AWS::CloudFormation::WaitConditionHandle`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::CloudFormation::WaitConditionHandle` {

  import spray.json.DefaultJsonProtocol._

  implicit val format: JsonFormat[`AWS::CloudFormation::WaitConditionHandle`] = jsonFormat3(`AWS::CloudFormation::WaitConditionHandle`.apply)
}

case class `AWS::CloudFormation::WaitCondition`(name: String,
                                                Handle: Token[`AWS::CloudFormation::WaitConditionHandle`],
                                                Timeout: Token[Int],
                                                Count: Option[Token[Int]],
                                                override val Condition: Option[ConditionRef] = None,
                                                override val DependsOn: Option[Seq[String]] = None)
  extends Resource[`AWS::CloudFormation::WaitCondition`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::CloudFormation::WaitCondition` {

  import spray.json.DefaultJsonProtocol._

  implicit val format: JsonFormat[`AWS::CloudFormation::WaitCondition`] = jsonFormat6(`AWS::CloudFormation::WaitCondition`.apply)
}

case class `AWS::CloudFormation::Stack`(name: String,
                                        TemplateURL: Token[String],
                                        TimeoutInMinutes: Option[StringBackedInt] = None,
                                        Parameters: Option[Map[String, Token[String]]] = None,
                                        NotificationARNs: Option[TokenSeq[String]] = None,
                                        override val DependsOn: Option[Seq[String]] = None,
                                        override val Condition: Option[ConditionRef] = None)
    extends Resource[`AWS::CloudFormation::Stack`]
    with HasArn {

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::CloudFormation::Stack` {

  import spray.json.DefaultJsonProtocol._

  implicit val format: JsonFormat[`AWS::CloudFormation::Stack`] = jsonFormat7(
      `AWS::CloudFormation::Stack`.apply)
}

case class `AWS::CloudFormation::CustomResource`(name: String,
                                                 ServiceToken: Token[String],
                                                 Parameters: Option[Map[String, JsonWritable[_]]] = None,
                                                 CustomResourceTypeName: Option[String] = None,
                                                 override val DependsOn: Option[Seq[String]] = None,
                                                 override val Condition: Option[ConditionRef] = None
                                                )
  extends Resource[`AWS::CloudFormation::CustomResource`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
  override val ResourceType = CustomResourceTypeName match {
    case None => "AWS::CloudFormation::CustomResource"
    case Some(x) => if (x.startsWith("Custom::")) x else (s"Custom::${x}")
  }
}


object `AWS::CloudFormation::CustomResource` extends spray.json.DefaultJsonProtocol {

  implicit val format: RootJsonFormat[`AWS::CloudFormation::CustomResource`] = new RootJsonFormat[`AWS::CloudFormation::CustomResource`] {
    override def read(json: JsValue): `AWS::CloudFormation::CustomResource` = ???

    override def write(obj: `AWS::CloudFormation::CustomResource`): JsValue = {
      val st = ("ServiceToken" -> obj.ServiceToken.toJson)
      obj.Parameters match {
        case Some(p) => JsObject(p.mapValues(v => v.toJson) + st)
        case None => JsObject(st)
      }
    }
  }
}
