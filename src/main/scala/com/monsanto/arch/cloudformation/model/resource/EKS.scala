package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token}
import spray.json.{DefaultJsonProtocol, JsonFormat}

case class `AWS::EKS::Cluster`(
                                name: String,
                                Name: Token[String],
                                ResourcesVpcConfig: Token[ResourcesVpcConfig],
                                RoleArn: Token[String],
                                Version: Option[Token[String]] = None,
                                override val DependsOn: Option[Seq[String]] = None,
                                override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::EKS::Cluster`] {
  def when(newCondition: Option[ConditionRef] = Condition) =
    new `AWS::EKS::Cluster`(name, Name, ResourcesVpcConfig, RoleArn, Version, DependsOn, newCondition)
}

case class ResourcesVpcConfig(SecurityGroupIds : Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]], SubnetIds : Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]])

object ResourcesVpcConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ResourcesVpcConfig] = jsonFormat2(ResourcesVpcConfig.apply)
}

object `AWS::EKS::Cluster` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::EKS::Cluster`] = jsonFormat7(`AWS::EKS::Cluster`.apply)
}
