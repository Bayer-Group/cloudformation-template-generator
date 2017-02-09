package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.Token.TokenSeq
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

case class `AWS::AutoScaling::AutoScalingGroup`(
    name:                    String,
    AvailabilityZones:       TokenSeq[String],
    LaunchConfigurationName: Token[ResourceRef[`AWS::AutoScaling::LaunchConfiguration`]],
    MinSize:                 StringBackedInt,
    MaxSize:                 StringBackedInt,
    DesiredCapacity:         Token[Int],
    HealthCheckType:         String,
    VPCZoneIdentifier:       Seq[Token[ResourceRef[`AWS::EC2::Subnet`]]],
    Tags:                    Seq[AmazonTag],
    LoadBalancerNames:       Option[Seq[Token[ResourceRef[`AWS::ElasticLoadBalancing::LoadBalancer`]]]],
    UpdatePolicy:            Option[UpdatePolicy] = None,
    override val Condition:  Option[ConditionRef] = None,
    override val DependsOn : Option[Seq[String]] = None
) extends Resource[`AWS::AutoScaling::AutoScalingGroup`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::AutoScaling::AutoScalingGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::AutoScaling::AutoScalingGroup`] = jsonFormat13(`AWS::AutoScaling::AutoScalingGroup`.apply)
}

case class `AWS::AutoScaling::LaunchConfiguration`(
    name:               String,
    ImageId:            Token[AMIId],
    InstanceType:       Token[String],
    KeyName:            Token[String],
    SecurityGroups:     Seq[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
    UserData:           `Fn::Base64`,
    IamInstanceProfile: Option[Token[ResourceRef[`AWS::IAM::InstanceProfile`]]] = None,
    BlockDeviceMappings: Option[Seq[BlockDeviceMapping]] = None,
    override val Condition: Option[ConditionRef] = None,
    override val DependsOn : Option[Seq[String]] = None
) extends Resource[`AWS::AutoScaling::LaunchConfiguration`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::AutoScaling::LaunchConfiguration` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::AutoScaling::LaunchConfiguration`] = jsonFormat10(`AWS::AutoScaling::LaunchConfiguration`.apply)
}
case class BlockDeviceMapping private(
  DeviceName:   Token[String],
  Ebs:  Option[AutoScalingEBS] = None,
  NoDevice: Option[Token[Boolean]] = None,
  VirtualName: Option[Token[String]] = None
)
object BlockDeviceMapping extends DefaultJsonProtocol {
  implicit val format: JsonFormat[BlockDeviceMapping] = jsonFormat4(BlockDeviceMapping.apply)

  def ebs(
           DeviceName:   Token[String],
           Ebs:  AutoScalingEBS,
           NoDevice: Option[Token[Boolean]] = None
           ) = BlockDeviceMapping(DeviceName, Some(Ebs), NoDevice, None)

  def virtual(
           DeviceName:   Token[String],
           VirtualName:  Token[String],
           NoDevice: Option[Token[Boolean]] = None
           ) = BlockDeviceMapping(DeviceName, None, NoDevice, Some(VirtualName))
}

case class AutoScalingEBS(
   DeleteOnTermination: Option[Token[Boolean]] = None,
   Iops: Option[Token[Int]] = None,
   SnapshotId: Option[Token[String]] = None,
   VolumeSize: Option[Token[Int]] = None,
   VolumeType: Option[VolumeType] = None
 )
object AutoScalingEBS extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AutoScalingEBS] = jsonFormat5(AutoScalingEBS.apply)
}

sealed trait VolumeType
object VolumeType extends DefaultJsonProtocol{
  case object Standard extends VolumeType
  case object IO1 extends VolumeType
  case object GP2 extends VolumeType

  implicit val format: JsonFormat[VolumeType] = new JsonFormat[VolumeType] {
    override def write(obj: VolumeType): JsValue = JsString(obj.toString.toLowerCase())

    override def read(json: JsValue): VolumeType = json.toString() match {
      case "standard" => Standard
      case "io1" => IO1
      case "gp2" => GP2
    }
  }
}
case class `AWS::AutoScaling::ScalingPolicy`(
    name:                 String,
    AdjustmentType:       String,
    AutoScalingGroupName: Token[ResourceRef[`AWS::AutoScaling::AutoScalingGroup`]],
    Cooldown:             Token[Int],
    ScalingAdjustment:    String,
    override val Condition: Option[ConditionRef] = None,
    override val DependsOn : Option[Seq[String]] = None
) extends Resource[`AWS::AutoScaling::ScalingPolicy`]{
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}

object `AWS::AutoScaling::ScalingPolicy` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::AutoScaling::ScalingPolicy`] = jsonFormat7(`AWS::AutoScaling::ScalingPolicy`.apply)
}

case class UpdatePolicy(
    AutoScalingScheduledAction: ScheduledAction,
    AutoScalingRollingUpdate: AutoScalingRollingUpdate)
object UpdatePolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[UpdatePolicy] = jsonFormat2(UpdatePolicy.apply)
}

case class ScheduledAction(IgnoreUnmodifiedGroupSizeProperties: Token[Boolean])

object ScheduledAction extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ScheduledAction] = jsonFormat1(ScheduledAction.apply)
}

case class AutoScalingRollingUpdate(
    MaxBatchSize: Option[Token[Int]] = None,
    MinInstancesInService: Option[Token[Int]] = None,
    PauseTime: Option[Token[String]] = None,
    SuspendProcesses: Option[TokenSeq[String]] = None,
    WaitOnResourceSignals: Option[Token[Boolean]] = None)

object AutoScalingRollingUpdate extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AutoScalingRollingUpdate] = jsonFormat5(AutoScalingRollingUpdate.apply)
}
