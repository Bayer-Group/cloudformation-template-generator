package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{Token, ConditionRef}
import spray.json.JsonFormat

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

