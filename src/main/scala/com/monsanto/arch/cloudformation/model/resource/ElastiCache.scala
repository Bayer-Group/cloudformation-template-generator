package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, EnumFormat, ResourceRef, StringBackedInt, Token}
import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import spray.json._

/**
  * Cache security group.
  *
  * @param name
  * @param Description
  * @param SubnetIds
  * @param CacheSubnetGroupName
  * @param Condition
  */
case class `AWS::ElastiCache::SubnetGroup`(
  name:                     String,
  Description:              String,
  SubnetIds:                Token[Seq[ResourceRef[`AWS::EC2::Subnet`]]],
  CacheSubnetGroupName:     Option[String] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition:   Option[ConditionRef] = None
) extends Resource[`AWS::ElastiCache::SubnetGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

/**
  * Json format definition for subnet group.
  */
object `AWS::ElastiCache::SubnetGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElastiCache::SubnetGroup`] = jsonFormat6(`AWS::ElastiCache::SubnetGroup`.apply)
}

sealed trait ElastiCacheEngine
object ElastiCacheEngine extends DefaultJsonProtocol {
  case object memcached   extends ElastiCacheEngine
  case object redis       extends ElastiCacheEngine
  val values = Seq(memcached, redis)
  implicit val format: JsonFormat[ElastiCacheEngine] =
    new EnumFormat[ElastiCacheEngine](values)
}

sealed trait ElastiCacheAZMode
object ElastiCacheAZMode extends DefaultJsonProtocol {
  case object `single-az`     extends ElastiCacheAZMode
  case object `cross-az`      extends ElastiCacheAZMode
  val values = Seq(`single-az`, `cross-az`)
  implicit val format: JsonFormat[ElastiCacheAZMode] =
    new EnumFormat[ElastiCacheAZMode](values)
}

/**
  * Cache cluster definition.
  *
  * @param name
  * @param CacheNodeType
  * @param Engine
  * @param NumCacheNodes
  * @param AutoMinorVersionUpgrade
  * @param AZMode
  * @param CacheParameterGroupName
  * @param CacheSecurityGroupNames
  * @param CacheSubnetGroupName
  * @param ClusterName
  * @param EngineVersion
  * @param NotificationTopicArn
  * @param Port
  * @param PreferredAvailabilityZone
  * @param PreferredAvailabilityZones
  * @param PreferredMaintenanceWindow
  * @param SnapshotArns
  * @param SnapshotName
  * @param SnapshotRetentionLimit
  * @param SnapshotWindow
  * @param VpcSecurityGroupIds
  * @param Tags
  * @param Condition
  */
case class `AWS::ElastiCache::CacheCluster`(
  name:                       String,
  CacheNodeType:              Token[String],
  Engine:                     Token[ElastiCacheEngine],
  NumCacheNodes :             Token[StringBackedInt],
  AutoMinorVersionUpgrade:    Option[Token[Boolean]]                                          = None,
  AZMode:                     Option[ElastiCacheAZMode]                                       = None,
  CacheParameterGroupName:    Option[Token[String]]                                           = None,
  CacheSecurityGroupNames:    Option[Token[Seq[String]]]                                      = None,
  CacheSubnetGroupName:       Option[Token[ResourceRef[`AWS::ElastiCache::SubnetGroup`]]]     = None,
  ClusterName:                Option[Token[String]]                                           = None,
  EngineVersion :             Option[Token[String]]                                           = None,
  NotificationTopicArn :      Option[Token[String]]                                           = None,
  Port:                       Option[Token[Int]]                                              = None,
  PreferredAvailabilityZone : Option[Token[String]]                                           = None,
  PreferredAvailabilityZones: Option[TokenSeq[String]]                                        = None,
  PreferredMaintenanceWindow: Option[Token[String]]                                           = None,
  SnapshotArns:               Option[Token[Seq[String]]]                                      = None,
  SnapshotName:               Option[Token[String]]                                           = None,
  SnapshotRetentionLimit:     Option[Token[Int]]                                              = None,
  SnapshotWindow:             Option[Token[String]]                                           = None,
  VpcSecurityGroupIds:        Option[TokenSeq[String]]                                        = None,
  Tags:                       Option[Seq[AmazonTag]]                                          = None,
  override val DependsOn: Option[Seq[String]]                                                 = None,
  override val Condition:     Option[ConditionRef]                                            = None
  ) extends Resource[`AWS::ElastiCache::CacheCluster`] {

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

/**
  * Json format definition for cache cluster
  */
object `AWS::ElastiCache::CacheCluster` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ElastiCache::CacheCluster`] = new RootJsonFormat[`AWS::ElastiCache::CacheCluster`] {
    def write(e: `AWS::ElastiCache::CacheCluster`) = {
      val obj = JsObject(
        "CacheNodeType"               ->  e.CacheNodeType.toJson,
        "Engine"                      ->  e.Engine.toJson,
        "NumCacheNodes"               ->  e.NumCacheNodes.toJson,
        "AutoMinorVersionUpgrade"     ->  e.AutoMinorVersionUpgrade.toJson,
        "AZMode"                      ->  e.AZMode.toJson,
        "CacheParameterGroupName"     ->  e.CacheParameterGroupName.toJson,
        "CacheSecurityGroupNames"     ->  e.CacheSecurityGroupNames.toJson,
        "CacheSubnetGroupName"        ->  e.CacheSubnetGroupName.toJson,
        "ClusterName"                 ->  e.ClusterName.toJson,
        "EngineVersion"               ->  e.EngineVersion.toJson,
        "NotificationTopicArn"        ->  e.NotificationTopicArn.toJson,
        "Port"                        ->  e.Port.toJson,
        "PreferredAvailabilityZone"   ->  e.PreferredAvailabilityZone.toJson,
        "PreferredAvailabilityZones"  ->  e.PreferredAvailabilityZones.toJson,
        "PreferredMaintenanceWindow"  ->  e.PreferredMaintenanceWindow.toJson,
        "SnapshotArns"                ->  e.SnapshotArns.toJson,
        "SnapshotName"                ->  e.SnapshotName.toJson,
        "SnapshotRetentionLimit"      ->  e.SnapshotRetentionLimit.toJson,
        "SnapshotWindow"              ->  e.SnapshotWindow.toJson,
        "VpcSecurityGroupIds"         ->  e.VpcSecurityGroupIds.toJson,
        "Tags"                        ->  e.Tags.toJson
      )
      obj.copy(fields = obj.fields.filter(_._2 != JsNull))
    }
    def read(value: JsValue) = ???
  }

}
