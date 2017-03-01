package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import com.monsanto.arch.cloudformation.model._
import spray.json._

import scala.language.implicitConversions

case class `AWS::ECS::Cluster`(name: String,
                               ClusterName: Option[Token[String]] = None,
                               override val Condition: Option[ConditionRef] = None
                              ) extends Resource[`AWS::ECS::Cluster`] {
  override def when(newCondition: Option[ConditionRef]): `AWS::ECS::Cluster` = copy(Condition = newCondition)
}

object `AWS::ECS::Cluster` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ECS::Cluster`] = jsonFormat3(`AWS::ECS::Cluster`.apply)
}

case class `AWS::ECS::Service`(name: String,
                               Cluster: Option[Token[String]] = None,
                               DeploymentConfiguration: Option[DeploymentConfiguration] = None,
                               DesiredCount: Int,
                               LoadBalancers: Option[Seq[EcsLoadBalancer]] = None,
                               Role: Option[Token[String]] = None,
                               TaskDefinition: Token[String],
                               override val Condition: Option[ConditionRef] = None
                              ) extends Resource[`AWS::ECS::Service`] with HasArn {
  override def when(newCondition: Option[ConditionRef]): `AWS::ECS::Service` = copy(Condition = newCondition)

  override def arn: Token[String] = ResourceRef(this)
}

object `AWS::ECS::Service` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ECS::Service`] = jsonFormat8(`AWS::ECS::Service`.apply)
}

case class DeploymentConfiguration(MaximumPercent: Option[Int], MinimumHealthyPercent: Option[Int])

object DeploymentConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[DeploymentConfiguration] = jsonFormat2(DeploymentConfiguration.apply)
}

case class EcsLoadBalancer private[resource](ContainerName: Token[String],
                                             ContainerPort: Int,
                                             LoadBalancerName: Option[Token[String]] = None,
                                             TargetGroupArn: Option[Token[String]] = None
                                            ) {
  require(!(LoadBalancerName.isDefined && TargetGroupArn.isDefined), "either LoadBalancerName or TargetGroupArn can be defined, but not both")
}

object EcsLoadBalancer extends DefaultJsonProtocol {
  implicit val format: JsonFormat[EcsLoadBalancer] = jsonFormat4(EcsLoadBalancer.apply)

  def apply(ContainerName: Token[String],
            ContainerPort: Int,
            LoadBalancerName: LoadBalancerName): EcsLoadBalancer = EcsLoadBalancer(ContainerName, ContainerPort, LoadBalancerName = Option(LoadBalancerName.name))

  def apply(ContainerName: Token[String],
            ContainerPort: Int,
            TargetGroupArn: TargetGroupArn): EcsLoadBalancer = EcsLoadBalancer(ContainerName, ContainerPort, TargetGroupArn = Option(TargetGroupArn.arn))
}

case class LoadBalancerName(name: Token[String])

case class TargetGroupArn(arn: Token[String])

case class `AWS::ECS::TaskDefinition`(name: String,
                                      ContainerDefinitions: Seq[ContainerDefinition],
                                      Family: Option[Token[String]] = None,
                                      NetworkMode: Option[Token[String]] = None,
                                      TaskRoleArn: Option[Token[String]] = None,
                                      Volumes: Seq[VolumeDefinition] = Seq.empty[VolumeDefinition],
                                      override val Condition: Option[ConditionRef] = None) extends Resource[`AWS::ECS::TaskDefinition`] with HasArn {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::ECS::TaskDefinition` = copy(Condition = newCondition)

  override def arn: Token[String] = ResourceRef(this)
}

object `AWS::ECS::TaskDefinition` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::ECS::TaskDefinition`] = jsonFormat7(`AWS::ECS::TaskDefinition`.apply)
}

case class ContainerDefinition(Command: Option[TokenSeq[String]] = None,
                               Cpu: Option[Int] = None,
                               DisableNetworking: Option[Boolean] = None,
                               DnsSearchDomains: Option[TokenSeq[String]] = None,
                               DnsServers: Option[TokenSeq[String]] = None,
                               DockerLabels: Option[Map[Token[String], Token[String]]] = None,
                               DockerSecurityOptions: Option[TokenSeq[String]] = None,
                               EntryPoint: Option[TokenSeq[String]] = None,
                               Environment: Option[Seq[Environment]] = None,
                               Essential: Option[Boolean] = None,
                               ExtraHosts: Option[Seq[HostEntry]] = None,
                               Hostname: Option[Token[String]] = None,
                               Image: Token[String],
                               Links: Option[TokenSeq[String]] = None,
                               LogConfiguration: Option[LogConfiguration] = None,
                               Memory: Option[Int] = None,
                               MemoryReservation: Option[Int] = None,
                               Name: String,
                               PortMappings: Option[Seq[PortMapping]] = None,
                               Privileged: Option[Boolean] = None,
                               ReadonlyRootFilesystem: Option[Boolean] = None,
                               Ulimits: Option[Seq[Ulimit]] = None,
                               User: Option[Token[String]] = None,
                               VolumesFrom: Option[Seq[VolumesFrom]] = None,
                               WorkingDirectory: Option[Token[String]] = None) {
  require(Memory.isDefined || MemoryReservation.isDefined, "either Memory or MemoryReservation must be defined")
  require((for {
    memory ← Memory
    memoryReservation ← MemoryReservation
  } yield {
    memory > memoryReservation
  }).getOrElse(true), "if both are defined, Memory must be greater than MemoryReservation")
}

object ContainerDefinition extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ContainerDefinition] = new RootJsonFormat[ContainerDefinition] {
    override def write(cd: ContainerDefinition) = {
      val obj = JsObject(
        "Command" → cd.Command.toJson,
        "Cpu" → cd.Cpu.toJson,
        "DisableNetworking" → cd.DisableNetworking.toJson,
        "DnsSearchDomains" → cd.DnsSearchDomains.toJson,
        "DnsServers" → cd.DnsServers.toJson,
        "DockerLabels" → cd.DockerLabels.toJson,
        "DockerSecurityOptions" → cd.DockerSecurityOptions.toJson,
        "EntryPoint" → cd.EntryPoint.toJson,
        "Environment" → cd.Environment.toJson,
        "Essential" → cd.Essential.toJson,
        "ExtraHosts" → cd.ExtraHosts.toJson,
        "Hostname" → cd.Hostname.toJson,
        "Image" → cd.Image.toJson,
        "Links" → cd.Links.toJson,
        "LogConfiguration" → cd.LogConfiguration.toJson,
        "Memory" → cd.Memory.toJson,
        "MemoryReservation" → cd.MemoryReservation.toJson,
        "Name" → cd.Name.toJson,
        "PortMappings" → cd.PortMappings.toJson,
        "Privileged" → cd.Privileged.toJson,
        "ReadonlyRootFilesystem" → cd.ReadonlyRootFilesystem.toJson,
        "Ulimits" → cd.Ulimits.toJson,
        "User" → cd.User.toJson,
        "VolumesFrom" → cd.VolumesFrom.toJson,
        "WorkingDirectory" → cd.WorkingDirectory.toJson
      )
      obj.copy(fields = obj.fields.filter(_._2 != JsNull))
    }

    //noinspection NotImplementedCode
    override def read(json: JsValue) = ???
  }
}

case class VolumeDefinition(Name: String, Host: Option[Host] = None)

object VolumeDefinition extends DefaultJsonProtocol {
  implicit val format: JsonFormat[VolumeDefinition] = jsonFormat2(VolumeDefinition.apply)
}

case class Host(SourcePath: Option[Token[String]] = None)

object Host extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Host] = jsonFormat1(Host.apply)
}

case class Environment(Name: String, Value: Token[String])

object Environment extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Environment] = jsonFormat2(Environment.apply)
}

case class HostEntry(Hostname: Token[String], IpAddress: Token[String])

object HostEntry extends DefaultJsonProtocol {
  implicit val format: JsonFormat[HostEntry] = jsonFormat2(HostEntry.apply)
}

case class LogConfiguration(LogDriver: Token[String], Options: Option[Map[String, Token[String]]])

object LogConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[LogConfiguration] = jsonFormat2(LogConfiguration.apply)
}

case class PortMapping(ContainerPort: Int, HostPort: Option[Int], Protocol: Option[Token[String]])

object PortMapping extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PortMapping] = jsonFormat3(PortMapping.apply)
}

case class Ulimit(HardLimit: Int, Name: Option[String] = None, SoftLimit: Int)

object Ulimit extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Ulimit] = jsonFormat3(Ulimit.apply)
}

case class VolumesFrom(SourceContainer: Token[String], ReadOnly: Option[Boolean] = None)

object VolumesFrom extends DefaultJsonProtocol {
  implicit val format: JsonFormat[VolumesFrom] = jsonFormat2(VolumesFrom.apply)
}
