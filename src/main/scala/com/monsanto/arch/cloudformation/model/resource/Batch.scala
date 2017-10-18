package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.{DefaultJsonProtocol, JsonFormat}

/**
  * The `AWS::Batch::ComputeEnvironment` resource to define your AWS Batch compute environment. For more information, see
  * [[http://docs.aws.amazon.com/batch/latest/userguide/compute_environments.html Compute Environments]] in the AWS
  * Batch User Guide.
  *
  * @param name CloudFormation logical name.
  * @param Type The type of the compute environment.
  * @param ServiceRole The service role ARN associated with the compute environment that allows AWS Batch to make calls
  *                    to AWS API operations on your behalf.
  * @param ComputeEnvironmentName The name of the compute environment.
  * @param ComputeResources The compute resources defined for the compute environment.
  * @param State The state of the compute environment. The valid values are ENABLED or DISABLED. An ENABLED state
  *              indicates that you can register instances with the compute environment and that the associated
  *              instances can accept jobs.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Batch::ComputeEnvironment`(
  name:                   String,
  ComputeEnvironmentName: Option[Token[String]],
  Type:                   ComputeEnvironmentType,
  ComputeResources:       ComputeResources,
  ServiceRole:            Token[String],
  State:                  Option[ComputeEnvironmentState],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::Batch::ComputeEnvironment`] with HasArn {

  def arn: Token[String] = ResourceRef(this)
  def when(newCondition: Option[ConditionRef]): `AWS::Batch::ComputeEnvironment` = copy(Condition = newCondition)
}

object `AWS::Batch::ComputeEnvironment` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Batch::ComputeEnvironment`] = jsonFormat8(`AWS::Batch::ComputeEnvironment`.apply)

  /**
    * Create the minimum required AWS Batch Service Role
    *
    * @param name CloudFormation logical name.
    * @return the `AWS::IAM::Role` resource
    */
  def minimumServiceRole(name: String): `AWS::IAM::Role` =
    `AWS::IAM::Role`(
      name = name,
      RoleName = Some(`Fn::Join`("-", Seq(name, `AWS::StackName`))),
      Path = Some(`Fn::Join`("/", Seq("", `AWS::StackName`, ""))),
      ManagedPolicyArns = Seq(
        ManagedPolicyARN.fromAWSManagedPolicy(AWSManagedPolicy("service-role/AWSBatchServiceRole"))
      ),
      AssumeRolePolicyDocument = PolicyDocument(
        Statement = Seq(PolicyStatement(
          Effect    = "Allow",
          Principal = Some(DefinedPrincipal(Map("Service" -> "batch.amazonaws.com"))),
          Action    = Seq("sts:AssumeRole")
        ))
      )
    )
}

/**
  * The `ComputeResources` property type specifies details of the compute resources managed by the compute environment.
  * This parameter is required for managed compute environments. For more information, see
  * [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-batch-computeenvironment.html Compute Environments]]
  * in the AWS Batch User Guide.
  *
  * ComputeResources is a property of the [[com.monsanto.arch.cloudformation.model.resource.`AWS::Batch::ComputeEnvironment`]] resource.
  *
  * @param Type The type of compute environment: EC2 or SPOT.
  * @param InstanceTypes The instances types that may launched.
  * @param MinvCpus The minimum number of EC2 vCPUs that an environment should maintain.
  * @param MaxvCpus The maximum number of EC2 vCPUs that an environment can reach.
  * @param DesiredvCpus The desired number of EC2 vCPUS in the compute environment.
  * @param SecurityGroupIds The EC2 security group that is associated with instances launched in the compute environment.
  * @param Subnets The VPC subnets into which the compute resources are launched.
  * @param ImageId The Amazon Machine Image (AMI) ID used for instances launched in the compute environment.
  * @param Ec2KeyPair The EC2 key pair that is used for instances launched in the compute environment.
  * @param InstanceRole The Amazon ECS instance profile ARN applied to Amazon EC2 instances in a compute environment.
  * @param SpotIamFleetRole The Amazon Resource Name (ARN) of the Amazon EC2 Spot Fleet IAM role applied to a SPOT
  *                         compute environment.
  * @param BidPercentage The minimum percentage that a Spot Instance price must be when compared with the On-Demand price
  *                      for that instance type before instances are launched. For example, if your bid percentage is 20%,
  *                      then the Spot price must be below 20% of the current On-Demand price for that EC2 instance.
  * @param Tags Key-value pair tags to be applied to resources that are launched in the compute environment.
  */
case class ComputeResources(
  Type:             ComputeResourcesType,
  InstanceTypes:    Token.TokenSeq[String],
  MinvCpus:         Token[Int],
  MaxvCpus:         Token[Int],
  SecurityGroupIds: Seq[ResourceRef[`AWS::EC2::SecurityGroup`]],
  Subnets:          Seq[ResourceRef[`AWS::EC2::Subnet`]],
  ImageId:          Option[Token[String]] = None,
  Ec2KeyPair:       Option[Token[String]] = None,
  InstanceRole:     Token[String],
  DesiredvCpus:     Option[Token[Int]] = None,
  SpotIamFleetRole: Option[Token[String]] = None,
  BidPercentage:    Option[Token[Int]] = None,
  Tags:             Option[Seq[AmazonTag]] = None
)

object ComputeResources extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ComputeResources] = jsonFormat13(ComputeResources.apply)

  val OptimalInstanceType: Seq[String] = Seq("optimal")

  /**
    * Create the minimum required AWS Batch EC2 Instance Role
    *
    * @param name CloudFormation logical name.
    * @return the `AWS::IAM::Role` resource
    */
  def minimumInstanceRole(name: String): `AWS::IAM::Role` =
    `AWS::IAM::Role`(
      name = name,
      RoleName = Some(`Fn::Join`("-", Seq(name, `AWS::StackName`))),
      Path = Some(`Fn::Join`("/", Seq("", `AWS::StackName`, ""))),
      ManagedPolicyArns = Seq(
        ManagedPolicyARN.fromAWSManagedPolicy(AWSManagedPolicy("service-role/AmazonEC2ContainerServiceforEC2Role"))
      ),
      AssumeRolePolicyDocument = PolicyDocument(
        Statement = Seq(
          PolicyStatement(
            Effect    = "Allow",
            Principal = Some(DefinedPrincipal(Map("Service" -> "ec2.amazonaws.com"))),
            Action    = Seq("sts:AssumeRole")
          )
        )
      )
    )

  /**
    * Create the minimum required AWS Batch EC2 Spot Fleet Role
    *
    * @param name CloudFormation logical name.
    * @return the `AWS::IAM::Role` resource
    */
  def minimumSpotFleetRole(name: String): `AWS::IAM::Role` =
    `AWS::IAM::Role`(
      name = name,
      RoleName = Some(`Fn::Join`("-", Seq(name, `AWS::StackName`))),
      Path = Some(`Fn::Join`("/", Seq("", `AWS::StackName`, ""))),
      ManagedPolicyArns = Seq(
        ManagedPolicyARN.fromAWSManagedPolicy(AWSManagedPolicy("service-role/AmazonEC2SpotFleetRole"))
      ),
      AssumeRolePolicyDocument = PolicyDocument(
        Statement = Seq(
          PolicyStatement(
            Effect    = "Allow",
            Principal = Some(DefinedPrincipal(Map("Service" -> "spotfleet.amazonaws.com"))),
            Action    = Seq("sts:AssumeRole")
          )
        )
      )
    )
}

sealed trait ComputeEnvironmentState
object ComputeEnvironmentState extends DefaultJsonProtocol {

  case object ENABLED  extends ComputeEnvironmentState
  case object DISABLED extends ComputeEnvironmentState

  val values = Seq(ENABLED, DISABLED)
  implicit val format: JsonFormat[ComputeEnvironmentState] = new EnumFormat[ComputeEnvironmentState](values)
}

sealed trait ComputeResourcesType
object ComputeResourcesType extends DefaultJsonProtocol {

  case object EC2  extends ComputeResourcesType
  case object SPOT extends ComputeResourcesType

  val values = Seq(EC2, SPOT)
  implicit val format: JsonFormat[ComputeResourcesType] = new EnumFormat[ComputeResourcesType](values)
}

sealed trait ComputeEnvironmentType
object ComputeEnvironmentType extends DefaultJsonProtocol {

  case object MANAGED   extends ComputeEnvironmentType
  case object UNMANAGED extends ComputeEnvironmentType

  val values = Seq(MANAGED, UNMANAGED)
  implicit val format: JsonFormat[ComputeEnvironmentType] = new EnumFormat[ComputeEnvironmentType](values)
}


/**
  * The `AWS::Batch::JobDefinition` resource specifies the parameters for an AWS Batch job definition. For more information, see
  * [[http://docs.aws.amazon.com/batch/latest/userguide/job_definitions.html Job Definitions]] in the AWS Batch User Guide.
  *
  * @param name CloudFormation logical name.
  * @param Type The type of job definition.
  * @param Parameters Default parameters or parameter substitution placeholders that are set in the job definition.
  *                   Parameters are specified as a key-value pair mapping.
  *                   See [[http://docs.aws.amazon.com/batch/latest/userguide/job_definition_parameters.html Job Definition Parameters]].
  * @param ContainerProperties An object with various properties specific to container-based jobs.
  * @param JobDefinitionName The name of the job definition.
  * @param RetryStrategy The retry strategy to use for failed jobs that are submitted with this job definition.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Batch::JobDefinition`(
  name:                   String,
  JobDefinitionName:      Option[Token[String]],
  Type:                   JobDefinitionType,
  ContainerProperties:    JobContainerProperties,
  Parameters:             Map[String, Token[String]] = Map.empty,
  RetryStrategy:          Option[JobRetryStrategy] = None,
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::Batch::JobDefinition`] with HasArn {

  def arn: Token[String] = ResourceRef(this)
  def when(newCondition: Option[ConditionRef]): `AWS::Batch::JobDefinition` = copy(Condition = newCondition)
}

object `AWS::Batch::JobDefinition` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Batch::JobDefinition`] = jsonFormat8(`AWS::Batch::JobDefinition`.apply)
}

/**
  * The `RetryStrategy` property type specifies the retry strategy to use for failed jobs that are submitted with this
  * job definition.
  *
  * @param Attempts The number of times to move a job to the RUNNABLE status. You may specify between 1 and 10 attempts.
  *                 If attempts is greater than one, the job is retried if it fails until it has moved to RUNNABLE that
  *                 many times.
  */
case class JobRetryStrategy(Attempts: Option[Token[Int]])

object JobRetryStrategy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[JobRetryStrategy] = jsonFormat1(JobRetryStrategy.apply)
}

sealed trait JobDefinitionType
object JobDefinitionType extends DefaultJsonProtocol {

  case object container extends JobDefinitionType

  val values = Seq(container)
  implicit val format: JsonFormat[JobDefinitionType] = new EnumFormat[JobDefinitionType](values)
}

/**
  * The `ContainerProperties` property type specifies various properties specific to container-based jobs.
  * @param MountPoints The mount points for data volumes in your container. This parameter maps to `Volumes` in the
  *                    [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *                    [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--volume` option to
  *                    [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param User The user name to use inside the container. This parameter maps to `User` in the
  *             [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *             [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--user` option to
  *             [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param Volumes A list of data volumes used in a job.
  * @param Command The command that is passed to the container. This parameter maps to `Cmd` in the
  *                [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *                [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `COMMAND` parameter to
  *                [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param Memory The hard limit (in MiB) of memory to present to the container. If your container attempts to exceed the
  *               memory specified here, the container is killed. This parameter maps to `Memory` in the
  *               [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *               [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--memory` option to
  *               [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param Privileged When this parameter is true, the container is given elevated privileges on the host container
  *                   instance (similar to the `root` user). This parameter maps to `Privileged` in the
  *                   [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *                   [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--privileged` option to
  *                   [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param Environment The environment variables to pass to a container. This parameter maps to `Env` in the
  *                    [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *                    [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--env` option to
  *                    [[https://docs.docker.com/engine/reference/run/ docker run]].
  *
  *                    '''Important'''
  *                    We do not recommend using plain text environment variables for sensitive information, such as credential data.
  * @param JobRoleArn The Amazon Resource Name (ARN) of the IAM role that the container can assume for AWS permissions.
  * @param ReadonlyRootFilesystem When this parameter is true, the container is given read-only access to its root file
  *                               system. This parameter maps to `ReadonlyRootfs` in the
  *                               [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *                               [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--read-only` option to
  *                               [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param Ulimits A list of `ulimits` to set in the container. This parameter maps to `Ulimits` in the
  *                [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *                [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--ulimit` option to
  *                [[https://docs.docker.com/engine/reference/run/ docker run]].
  * @param Vcpus The number of vCPUs reserved for the container. This parameter maps to `CpuShares` in the
  *              [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *              [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `--cpu-shares` option to
  *              [[https://docs.docker.com/engine/reference/run/ docker run]]. Each vCPU is equivalent to 1,024 CPU shares.
  * @param Image The image used to start a container. This string is passed directly to the Docker daemon. Images in the
  *              Docker Hub registry are available by default. Other repositories are specified with `repository-url/image:tag`.
  *              Up to 255 letters (uppercase and lowercase), numbers, hyphens, underscores, colons, periods, forward
  *              slashes, and number signs are allowed. This parameter maps to `Image` in the
  *              [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/#create-a-container Create a container]] section of the
  *              [[https://docs.docker.com/engine/reference/api/docker_remote_api_v1.19/ Docker Remote API]] and the `IMAGE` parameter of
  *              [[https://docs.docker.com/engine/reference/run/ docker run]].
  *
  *              - Images in Amazon ECR repositories use the full registry and repository URI (for example, `012345678910.dkr.ecr.region-name.amazonaws.com/repository-name`).
  *              - Images in official repositories on Docker Hub use a single name (for example, `ubuntu` or `mongo`).
  *              - Images in other repositories on Docker Hub are qualified with an organization name (for example, `amazon/amazon-ecs-agent`).
  *              - Images in other online repositories are qualified further by a domain name (for example, `quay.io/assemblyline/ubuntu`).
  */
case class JobContainerProperties(
  Image:                  Token[String],
  Memory:                 Token[Int],
  Vcpus:                  Token[Int],
  Command:                Option[Token.TokenSeq[String]] = None,
  Environment:            Option[Seq[Environment]] = None,
  JobRoleArn:             Option[Token[String]] = None,
  MountPoints:            Option[Seq[MountPoint]] = None,
  Volumes:                Option[Seq[VolumeDefinition]] = None,
  User:                   Option[Token[String]] = None,
  Privileged:             Option[Boolean] = None,
  ReadonlyRootFilesystem: Option[Boolean] = None,
  Ulimits:                Option[Seq[Ulimit]] = None
)

object JobContainerProperties extends DefaultJsonProtocol {
  implicit val format: JsonFormat[JobContainerProperties] = jsonFormat12(JobContainerProperties.apply)
}


/**
  * The `AWS::Batch::JobQueue` resource defines your AWS Batch job queue. For more information, see
  * [[http://docs.aws.amazon.com/batch/latest/userguide/job_queues.html Job Queues]] in the AWS Batch User Guide.
  *
  * @param name CloudFormation logical name.
  * @param ComputeEnvironmentOrder The compute environments that are attached to the job queue and the order in which
  *                                job placement is preferred. Compute environments are selected for job placement in
  *                                ascending order.
  * @param Priority The priority of the job queue.
  * @param State The status of the job queue (for example, CREATING or VALID).
  * @param JobQueueName The name of the job queue.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Batch::JobQueue`(
  name:                    String,
  JobQueueName:            Option[Token[String]],
  ComputeEnvironmentOrder: Seq[ComputeEnvironmentOrder],
  Priority:                Token[Int],
  State:                   Option[JobQueueState],
  override val Condition:  Option[ConditionRef] = None,
  override val DependsOn:  Option[Seq[String]] = None
) extends Resource[`AWS::Batch::JobQueue`] with HasArn {
  def arn: Token[String] = ResourceRef(this)

  def when(newCondition: Option[ConditionRef]): `AWS::Batch::JobQueue` = copy(Condition = newCondition)
}

object `AWS::Batch::JobQueue` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Batch::JobQueue`] = jsonFormat7(`AWS::Batch::JobQueue`.apply)
}

sealed trait JobQueueState
object JobQueueState extends DefaultJsonProtocol {

  case object ENABLED  extends JobQueueState
  case object DISABLED extends JobQueueState

  val values = Seq(ENABLED, DISABLED)
  implicit val format: JsonFormat[JobQueueState] = new EnumFormat[JobQueueState](values)
}

/**
  * The `ComputeEnvironmentOrder` property type specifies the order in which compute environments are tried for job
  * placement within a queue. Compute environments are tried in ascending order. For example, if two compute
  * environments are associated with a job queue, the compute environment with a lower order integer value is tried for
  * job placement first.
  *
  * @param ComputeEnvironment The Amazon Resource Name (ARN) of the compute environment.
  * @param Order The order of the compute environment.
  */
case class ComputeEnvironmentOrder(
  ComputeEnvironment: Token[String],
  Order:              Token[Int]
)

object ComputeEnvironmentOrder extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ComputeEnvironmentOrder] = jsonFormat2(ComputeEnvironmentOrder.apply)
}
