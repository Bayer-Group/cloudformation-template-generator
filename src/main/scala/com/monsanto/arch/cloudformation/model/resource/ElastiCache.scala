package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token}
import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
  * Cache security group.
  *
  * @param name
  * @param Description
  * @param SubnetIds
  * @param Condition
  */
case class `AWS::ElastiCache::SubnetGroup`(
  name:                     String,
  Description:              String,
  SubnetIds:                Token[Seq[ResourceRef[`AWS::EC2::Subnet`]]],
  override val Condition:    Option[ConditionRef] = None
  ) extends Resource[`AWS::ElastiCache::SubnetGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

/**
  * Json format definition for subnet group.
  */
object `AWS::ElastiCache::SubnetGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElastiCache::SubnetGroup`] = jsonFormat4(`AWS::ElastiCache::SubnetGroup`.apply)
}

/**
  * Cache cluster definition.
  *
  * @param name
  * @param ClusterName
  * @param CacheNodeType
  * @param CacheSubnetGroupName
  * @param VpcSecurityGroupIds
  * @param Engine
  * @param NumCacheNodes
  * @param Tags
  * @param Condition
  */
case class `AWS::ElastiCache::CacheCluster`(
  name:                     String,
  ClusterName:              String,
  CacheNodeType:            String,
  CacheSubnetGroupName:     Token[ResourceRef[`AWS::ElastiCache::SubnetGroup`]],
  VpcSecurityGroupIds:      Option[Seq[Token[String]]],
  Engine:                   String,
  NumCacheNodes:            Token[String],
  Tags:                     Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef]     = None
  ) extends Resource[`AWS::ElastiCache::CacheCluster`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

/**
  * Json format definition for cache cluster
  */
object `AWS::ElastiCache::CacheCluster` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElastiCache::CacheCluster`] = jsonFormat9(`AWS::ElastiCache::CacheCluster`.apply)
}
