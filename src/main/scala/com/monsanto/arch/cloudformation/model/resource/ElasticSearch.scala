package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, Token}
import spray.json.{DefaultJsonProtocol, JsonFormat}

case class EBSOptions(
  EBSEnabled:     Option[Token[Boolean]],
  Iops:           Option[Token[Int]],
  VolumeType:     Option[Token[VolumeType]],
  VolumeSize:     Option[Token[Int]]
)
object EBSOptions extends DefaultJsonProtocol {
  implicit val format = jsonFormat4(EBSOptions.apply)
}

case class ElasticsearchClusterConfig(
  DedicatedMasterCount:     Option[Token[Int]],
  DedicatedMasterEnabled:   Option[Token[Boolean]],
  DedicatedMasterType:      Option[Token[String]],
  InstanceCount:            Option[Token[Int]],
  InstanceType:             Option[Token[String]],
  ZoneAwarenessEnabled:     Option[Token[Boolean]]
)
object ElasticsearchClusterConfig extends DefaultJsonProtocol {
  implicit val format = jsonFormat6(ElasticsearchClusterConfig.apply)
}

case class SnapshotOptions(AutomatedSnapshotStartHour: Option[Token[Int]])
object SnapshotOptions extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(SnapshotOptions.apply)
}

case class `AWS::Elasticsearch::Domain` (
                                          name:                       String,
                                          DomainName:                 Token[String],
                                          AccessPolicies:             Option[PolicyDocument]                = None,
                                          AdvancedOptions:            Option[Token[Map[String, String]]]    = None,
                                          EBSOptions:                 Option[EBSOptions]                    = None,
                                          ElasticsearchClusterConfig: Option[ElasticsearchClusterConfig]    = None,
                                          SnapshotOptions:            Option[SnapshotOptions]               = None,
                                          override val Condition:     Option[ConditionRef]                  = None,
                                          override val DependsOn:     Option[Seq[String]]                   = None
                                        ) extends Resource[`AWS::Elasticsearch::Domain`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::Elasticsearch::Domain` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Elasticsearch::Domain`] = jsonFormat9(`AWS::Elasticsearch::Domain`.apply)
}
