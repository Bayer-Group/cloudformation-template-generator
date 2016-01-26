package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.JsonFormat

/**
  * Created by bkrodg on 1/11/16.
  */
case class `Custom::NatGateway`(name: String,
                                AllocationId: Token[String],
                                SubnetId: Token[`AWS::EC2::Subnet`],
                                WaitHandle: Token[`AWS::CloudFormation::WaitConditionHandle`],
                                ServiceToken: Token[String],
                                override val Condition: Option[ConditionRef] = None,
                                override val DependsOn: Option[Seq[String]] = None)
  extends Resource[`Custom::NatGateway`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `Custom::NatGateway` {
  val defaultServiceToken = `Fn::Join`(":", Seq("arn:aws:lambda", `AWS::Region`, `AWS::AccountId`, "function:cf-nat-gateway"))
  import spray.json.DefaultJsonProtocol._

  implicit def format: JsonFormat[`Custom::NatGateway`] = jsonFormat7(`Custom::NatGateway`.apply)
}

case class `Custom::NatGatewayRoute`(name: String,
                                     RouteTableId: Token[`AWS::EC2::RouteTable`],
                                     DestinationCidrBlock: Token[CidrBlock],
                                     NatGatewayId: Token[`Custom::NatGateway`],
                                     ServiceToken: Token[String],
                                     override val Condition: Option[ConditionRef] = None,
                                     override val DependsOn: Option[Seq[String]] = None)
  extends Resource[`Custom::NatGatewayRoute`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `Custom::NatGatewayRoute` {
  import spray.json.DefaultJsonProtocol._
  implicit def format: JsonFormat[`Custom::NatGatewayRoute`] = jsonFormat7(`Custom::NatGatewayRoute`.apply)
}
