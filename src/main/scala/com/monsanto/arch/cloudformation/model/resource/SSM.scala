package com.monsanto.arch.cloudformation.model.resource


import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
  * The AWS::SSM::Document resource creates an Amazon EC2 Systems Manager (SSM) document that describes an instance
  * configuration, which you can use to set up and run commands on your instances.
  *
  * @param name CloudFormation logical name
  * @param Content A JSON object that describes an instance configuration. For more information, see
  *                [[http://docs.aws.amazon.com/AWSEC2/latest/DeveloperGuide/create-ssm-doc.html Creating SSM Documents]]
  *                in the Amazon EC2 User Guide for Linux Instances. Note: The Content property is a non-stringified
  *                property. For more information about automation actions, see
  *                [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ami-actions.html Systems Manager Automation Actions]]
  *                in the Amazon EC2 Systems Manager User Guide.
  * @param DocumentType The type of document to create that relates to the purpose of your document, such as running commands,
  *                     bootstrapping software, or automating tasks. For valid values, see the
  *                     [[http://docs.aws.amazon.com/ssm/latest/APIReference/API_CreateDocument.html CreateDocument]]
  *                     action in the Amazon EC2 Systems Manager API Reference.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::SSM::Document`(
  name:                   String,
  Content:                DocumentContent,
  DocumentType:           Option[DocumentType],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::SSM::Document`] {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::SSM::Document` = copy(Condition = newCondition)
}

object `AWS::SSM::Document` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SSM::Document`] = jsonFormat5(`AWS::SSM::Document`.apply)

  /**
    * Creates a Command document with schemaVersion 2.2
    *
    * Examples:
    * {{{
    * `AWS::SSM::Document`.command_22(
    *   name = "SomeSampleCommandDocument",
    *   description = Some("Example document"),
    *   parameters = Some(Map(
    *     "ids" -> DocumentParameter(ParameterType.StringList, "The association IDs to update")
    *   )),
    *   mainSteps = Some(Seq(
    *     DocumentStep.`aws:refreshAssociation`(
    *       name = "refresh",
    *       associationIds = "{{ ids }}"
    *     )
    *   ))
    * )
    *
    * `AWS::SSM::Document`.command_22(
    *   name = "SomeOtherSampleCommandDocument",
    *   description = Some("Example document"),
    *   mainSteps = Some(Seq(
    *     DocumentStep.`aws:updateSsmAgent`(
    *       name = "updateAgent"
    *     ),
    *     DocumentStep.`aws:runShellScript`(
    *       name = "doLs",
    *       runCommand = Seq("ls"),
    *       workingDirectory = Some("/usr/bin")
    *     )
    *   ))
    * )
    * }}}
    *
    * @param name CloudFormation logical name
    * @param description Information you provide to describe the purpose of the document.
    * @param parameters The parameters the document accepts. For parameters that you reference often, we recommend that
    *                   you store those parameters in Systems Manager Parameter Store and then reference them. You can
    *                   reference String and StringList Systems Manager parameters in this section of a document. You
    *                   can't reference Secure String Systems Manager parameters in this section of a document.
    *                   For more information, see
    *                   [[http://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html Systems Manager Parameter Store]].
    * @param mainSteps An object that can include multiple steps (plugins). Steps include one or more actions, an optional
    *                  precondition, a unique name of the action, and inputs (parameters) for those actions. For a list of
    *                  supported plugins and plugin properties, see
    *                  [[http://docs.aws.amazon.com/ssm/latest/APIReference/ssm-plugins.html SSM Plugins]]
    *                  in the Amazon EC2 Systems Manager API Reference.
    * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
    *                  CloudFormation creates the associated resources.
    * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
    * @return The AWS::SSM::Document
    */
  def command_22(name:        String,
                 mainSteps:   Option[Seq[DocumentStep]],
                 description: Option[Token[String]] = None,
                 parameters:  Option[Map[String, DocumentParameter]] = None,
                 Condition:   Option[ConditionRef] = None,
                 DependsOn:   Option[Seq[String]] = None): `AWS::SSM::Document` =
    `AWS::SSM::Document`(
      name = name,
      Content = DocumentContent(
        schemaVersion = "2.2",
        description = description,
        parameters = parameters,
        mainSteps = mainSteps
      ),
      DocumentType = Some(DocumentType.Command),
      Condition = Condition,
      DependsOn = DependsOn
    )

  /**
    * Creates a Policy document with schemaVersion 2.0
    *
    * @param name CloudFormation logical name
    * @param description Information you provide to describe the purpose of the document.
    * @param parameters The parameters the document accepts. For parameters that you reference often, we recommend that
    *                   you store those parameters in Systems Manager Parameter Store and then reference them. You can
    *                   reference String and StringList Systems Manager parameters in this section of a document. You
    *                   can't reference Secure String Systems Manager parameters in this section of a document.
    *                   For more information, see
    *                   [[http://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html Systems Manager Parameter Store]].
    * @param mainSteps An object that can include multiple steps (plugins). Steps include one or more actions, an optional
    *                  precondition, a unique name of the action, and inputs (parameters) for those actions. For a list of
    *                  supported plugins and plugin properties, see
    *                  [[http://docs.aws.amazon.com/ssm/latest/APIReference/ssm-plugins.html SSM Plugins]]
    *                  in the Amazon EC2 Systems Manager API Reference.
    * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
    *                  CloudFormation creates the associated resources.
    * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
    * @return The AWS::SSM::Document
    */
  def policy_20(name:        String,
                mainSteps:   Option[Seq[DocumentStep]],
                description: Option[Token[String]] = None,
                parameters:  Option[Map[String, DocumentParameter]] = None,
                Condition:   Option[ConditionRef] = None,
                DependsOn:   Option[Seq[String]] = None): `AWS::SSM::Document` =
    `AWS::SSM::Document`(
      name = name,
      Content = DocumentContent(
        schemaVersion = "2.0",
        description = description,
        parameters = parameters,
        mainSteps = mainSteps
      ),
      DocumentType = Some(DocumentType.Policy),
      Condition = Condition,
      DependsOn = DependsOn
    )

  /**
    * Creates an Automation document with schemaVersion 0.3
    *
    * @param name CloudFormation logical name
    * @param description Information you provide to describe the purpose of the document.
    * @param parameters The parameters the document accepts. For parameters that you reference often, we recommend that
    *                   you store those parameters in Systems Manager Parameter Store and then reference them. You can
    *                   reference String and StringList Systems Manager parameters in this section of a document. You
    *                   can't reference Secure String Systems Manager parameters in this section of a document.
    *                   For more information, see
    *                   [[http://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html Systems Manager Parameter Store]].
    * @param mainSteps An object that can include multiple steps (plugins). Steps include one or more actions, an optional
    *                  precondition, a unique name of the action, and inputs (parameters) for those actions. For a list of
    *                  supported plugins and plugin properties, see
    *                  [[http://docs.aws.amazon.com/ssm/latest/APIReference/ssm-plugins.html SSM Plugins]]
    *                  in the Amazon EC2 Systems Manager API Reference.
    * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
    *                  CloudFormation creates the associated resources.
    * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
    * @return The AWS::SSM::Document
    */
  def automation_03(name:        String,
                    mainSteps:   Option[Seq[DocumentStep]],
                    description: Option[Token[String]] = None,
                    parameters:  Option[Map[String, DocumentParameter]] = None,
                    Condition:   Option[ConditionRef] = None,
                    DependsOn:   Option[Seq[String]] = None): `AWS::SSM::Document` =
    `AWS::SSM::Document`(
      name = name,
      Content = DocumentContent(
        schemaVersion = "0.3",
        description = description,
        parameters = parameters,
        mainSteps = mainSteps
      ),
      DocumentType = Some(DocumentType.Automation),
      Condition = Condition,
      DependsOn = DependsOn
    )
}


sealed trait DocumentType
object DocumentType extends DefaultJsonProtocol {
  case object Command    extends DocumentType
  case object Policy     extends DocumentType
  case object Automation extends DocumentType
  val values = Seq(Command, Policy, Automation)
  implicit val format: JsonFormat[DocumentType] = new EnumFormat[DocumentType](values)
}


/**
  * The AWS::SSM::Association resource associates an Amazon EC2 Systems Manager (SSM) document with EC2 instances that
  * contain a configuration agent to process the document.
  *
  * @param name CloudFormation logical name
  * @param DocumentVersion The version of the SSM document to associate with the target.
  * @param InstanceId The ID of the instance that the SSM document is associated with. You must specify either the
  *                   InstanceId or Targets property.
  * @param Name The name of the SSM document.
  * @param Parameters Parameter values that the SSM document uses at runtime.
  * @param ScheduleExpression A Cron expression that specifies when the association is applied to the target. For
  *                           supported expressions, see the ScheduleExpression parameter for the
  *                           [[http://docs.aws.amazon.com/ssm/latest/APIReference/API_CreateAssociation.html CreateAssociation]]
  *                           action in the Amazon EC2 Systems Manager API Reference.
  * @param Targets The targets that the SSM document sends commands to. You must specify either the InstanceId or
  *                Targets property.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::SSM::Association`(
  name:                   String,
  DocumentVersion:        Option[Token[String]],
  InstanceId:             Option[Token[String]],
  Name:                   Either[Token[String], ResourceRef[`AWS::SSM::Document`]],
  Parameters:             Option[Map[String, Seq[Token[String]]]],
  ScheduleExpression:     ScheduleExpression,
  Targets:                Option[Seq[Target]],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::SSM::Association`] {
  require(InstanceId.nonEmpty ^ Targets.nonEmpty, "You must specify either the InstanceId or Targets property.")

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::SSM::Association` = copy(Condition = newCondition)
}

object `AWS::SSM::Association` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SSM::Association`] = jsonFormat9(`AWS::SSM::Association`.apply)

  def forDoc(name:               String,
             document:           ResourceRef[`AWS::SSM::Document`],
             scheduleExpression: ScheduleExpression,
             instanceId:         Option[Token[String]] = None,
             targets:            Option[Seq[Target]] = None,
             parameters:         Option[Map[String, Seq[Token[String]]]] = None,
             documentVersion:    Option[Token[String]] = None,
             condition:          Option[ConditionRef] = None,
             dependsOn:          Option[Seq[String]] = None): `AWS::SSM::Association` =
    `AWS::SSM::Association`(
      name,
      documentVersion,
      instanceId,
      Right(document),
      parameters,
      scheduleExpression,
      targets,
      condition,
      dependsOn
    )

  def forDocName(name:               String,
                 document:           Token[String],
                 scheduleExpression: ScheduleExpression,
                 instanceId:         Option[Token[String]] = None,
                 targets:            Option[Seq[Target]] = None,
                 parameters:         Option[Map[String, Seq[Token[String]]]] = None,
                 documentVersion:    Option[Token[String]] = None,
                 condition:          Option[ConditionRef] = None,
                 dependsOn:          Option[Seq[String]] = None): `AWS::SSM::Association` =
    `AWS::SSM::Association`(
      name,
      documentVersion,
      instanceId,
      Left(document),
      parameters,
      scheduleExpression,
      targets,
      condition,
      dependsOn
    )
}


/**
  * The AWS::SSM::Parameter resource creates an Amazon EC2 Systems Manager (SSM) parameter in Parameter Store.
  *
  * @param name CloudFormation logical name
  * @param Name The name of the parameter. Names must not be prefixed with aws or ssm.
  * @param Description Information about the parameter that you want to add to the system.
  * @param Type The type of parameter. Valid values include the following: String or StringList. NOTE: AWS
  *             CloudFormation doesn't support the SecureString parameter type.
  * @param Value The parameter value. Value must not nest another parameter. Do not use {{}} in the value.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  */
case class `AWS::SSM::Parameter`(
  name:                   String,
  Name:                   Option[Token[String]],
  Description:            Option[Token[String]],
  Type:                   ParameterType,
  Value:                  Token[String],
  override val Condition: Option[ConditionRef] = None,
  override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::SSM::Parameter`] {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::SSM::Parameter` = copy(Condition = newCondition)

  def typeAttribute: Token[String] = `Fn::GetAtt`(Seq(name, "Type"))
  def valueAttribute: Token[String] = `Fn::GetAtt`(Seq(name, "Value"))
}

object `AWS::SSM::Parameter` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::SSM::Parameter`] = jsonFormat7(`AWS::SSM::Parameter`.apply)
}


sealed trait ParameterType
object ParameterType extends DefaultJsonProtocol {
  case object String     extends ParameterType
  case object StringList extends ParameterType
  val values = Seq(String, StringList)
  implicit val format: JsonFormat[ParameterType] = new EnumFormat[ParameterType](values)
}


/**
  *
  * @param Key User-defined criteria for sending commands that target instances that meet the criteria. Key can be
  *            `tag:<Amazon EC2 tag>` or InstanceIds. For more information about how to send commands that target
  *            instances using Key,Value parameters, see
  *            [[http://docs.aws.amazon.com/systems-manager/latest/userguide/send-commands-multiple.html Executing a Command Using Systems Manager Run Command]].
  * @param Values User-defined criteria that maps to Key. For example, if you specified `tag:ServerRole`, you could
  *               specify `value:WebServer` to execute a command on instances that include Amazon EC2 tags of
  *               ServerRole,WebServer. For more information about how to send commands that target instances using
  *               Key,Value parameters, see
  *               [[http://docs.aws.amazon.com/systems-manager/latest/userguide/send-commands-multiple.html Executing a Command Using Systems Manager Run Command]].
  */
case class Target(Key: TargetKey, Values: Seq[Token[String]]) {
  require(Values.length <= 50, "Maximum number of 50 values in Target")
}
object Target extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Target] = jsonFormat2(Target.apply)
}


sealed trait TargetKey
object TargetKey extends DefaultJsonProtocol {
  implicit val format: JsonFormat[TargetKey] = new JsonFormat[TargetKey] {
    def write(obj: TargetKey): JsValue = obj match {
      case k: TagKey   => JsString("tag:" + k.tagName)
      case InstanceIds => JsString("InstanceIds")
    }

    def read(json: JsValue): TargetKey = deserializationError("TargetKey not readable")
  }
}
case object InstanceIds extends TargetKey
case class TagKey(tagName: String) extends TargetKey


/**
  * The syntax of your document is defined by the schema version used to create it. We recommended that you use schema
  * version 2.2 or higher. Documents that use this schema version include the following top-level elements. For
  * information about the properties that you can specify in these elements, see
  * [[http://docs.aws.amazon.com/systems-manager/latest/APIReference/ssm-plugins.html#top-level Top-level Elements]]
  * in the Amazon EC2 Systems Manager API Reference.
  *
  * @param schemaVersion The schema version to use.
  * @param description Information you provide to describe the purpose of the document.
  * @param parameters The parameters the document accepts. For parameters that you reference often, we recommend that
  *                   you store those parameters in Systems Manager Parameter Store and then reference them. You can
  *                   reference String and StringList Systems Manager parameters in this section of a document. You
  *                   can't reference Secure String Systems Manager parameters in this section of a document.
  *                   For more information, see
  *                   [[http://docs.aws.amazon.com/systems-manager/latest/userguide/systems-manager-paramstore.html Systems Manager Parameter Store]].
  * @param mainSteps An object that can include multiple steps (plugins). Steps include one or more actions, an optional
  *                  precondition, a unique name of the action, and inputs (parameters) for those actions. For a list of
  *                  supported plugins and plugin properties, see
  *                  [[http://docs.aws.amazon.com/ssm/latest/APIReference/ssm-plugins.html SSM Plugins]]
  *                  in the Amazon EC2 Systems Manager API Reference.
  */
case class DocumentContent(
  schemaVersion: String,
  description: Option[Token[String]],
  parameters:  Option[Map[String, DocumentParameter]],
  mainSteps:   Option[Seq[DocumentStep]]
)

object DocumentContent extends DefaultJsonProtocol {
  implicit val format: JsonFormat[DocumentContent] = jsonFormat4(DocumentContent.apply)
}


case class DocumentParameter(`type`: ParameterType,
                             description: Token[String],
                             default: Option[Token[String]] = None,
                             allowedPattern: Option[Token[String]] = None,
                             allowedValues: Option[Seq[Token[String]]] = None)
object DocumentParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[DocumentParameter] = jsonFormat5(DocumentParameter.apply)
}


case class DocumentStep (
  action:       String,
  name:         String,
  precondition: Option[Precondition],
  inputs:       Option[Map[String, JsValue]]
) {
  require(! name.contains(" "), "The name of the action can't include a space. If a name includes a space, you will receive an InvalidDocumentContent error.")
}

object DocumentStep extends DefaultJsonProtocol {
  implicit val format: JsonFormat[DocumentStep] = jsonFormat4(DocumentStep.apply)

  /**
    * Install, repair, or uninstall applications on an EC2 instance. This plugin only runs on Microsoft Windows
    * operating systems. For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param action The action to take.
    * @param source The URL of the `.msi` file for the application.
    * @param parameters The parameters for the installer.
    * @param sourceHash The SHA256 hash of the `.msi` file.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:applications`(name:         String,
                         action:       InstallRepairUninstall,
                         source:       Token[String],
                         parameters:   Option[Token[String]] = None,
                         sourceHash:   Option[Token[String]] = None,
                         precondition: Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:applications",
      name = name,
      precondition = precondition,
      inputs = Some(
        Map(
          "action" -> Some(action.token.toJson),
          "parameters" -> parameters.map(_.toJson),
          "source" -> Some(source.toJson),
          "sourceHash" -> sourceHash.map(_.toJson)
        ).flatMap { case (k, ov) => ov.map(v => k -> v) }
      )
    )

  /**
    * (Schema version 2.0 or later) Configure an instance to work with containers and Docker. This plugin runs only on
    * Microsoft Windows operating systems. For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param action The type of action to perform. (Install/Uninstall)
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:configureDocker`(name:         String,
                            action:       InstallUninstall,
                            precondition: Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:configureDocker",
      name = name,
      precondition = precondition,
      inputs = Some(Map(
        "action" -> action.token.toJson
      ))
    )

  /**
    * (Schema version 2.0 or later) Install or uninstall an AWS package. This plugin runs on Linux and Microsoft Windows
    * operating systems. For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param packageName The name of the AWS package to install or uninstall.
    * @param action Install or uninstall a package.
    * @param version A specific version of the package to install or uninstall. If installing, the system installs the
    *                latest published version, by default. If uninstalling, the system uninstalls the currently
    *                installed version, by default. If no installed version is found, the latest published version is
    *                downloaded, and the uninstall action is run.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:configurePackage`(name:         String,
                             packageName:  Token[String],
                             action:       InstallUninstall,
                             version:      Option[Token[String]] = None,
                             precondition: Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:configurePackage",
      name = name,
      precondition = precondition,
      inputs = Some(
        Map(
          "name"    -> Some(packageName.toJson),
          "action"  -> Some(action.token.toJson),
          "version" -> version.map(_.toJson)
        ).flatMap { case (k, ov) => ov.map(v => k -> v) }
      )
    )

  /**
    * (Schema version 2.0 or later) Refresh (force apply) an association on demand. This action will change the system
    * state based on what is defined in the selected association or all associations bound to the targets. This plugin
    * runs on Linux and Microsoft Windows operating systems. For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param associationIds List of association IDs. If empty, all associations bound to the specified target are applied.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:refreshAssociation`(name:           String,
                               associationIds: Token[String],
                               precondition:   Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:refreshAssociation",
      name = name,
      precondition = precondition,
      inputs = Some(Map(
        "associationIds" -> associationIds.toJson
      ))
    )

  /**
    * (Schema version 2.0 or later) Run Docker actions on containers. This plugin runs on Linux and Microsoft Windows
    * operating systems. For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param action The type of Docker action to perform.
    * @param container The Docker container ID.
    * @param image The Docker image name.
    * @param cmd The container command.
    * @param memory The container memory limit.
    * @param cpuShares The container CPU shares (relative weight).
    * @param volume The container volume mounts.
    * @param env The container environment variables.
    * @param user The container user name.
    * @param publish The container published ports.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:runDockerAction`(name:         String,
                            action:       Token[String],
                            container:    Option[Token[String]] = None,
                            image:        Option[Token[String]] = None,
                            cmd:          Option[Token[String]] = None,
                            memory:       Option[Token[String]] = None,
                            cpuShares:    Option[Token[String]] = None,
                            volume:       Option[Token[String]] = None,
                            env:          Option[Token[String]] = None,
                            user:         Option[Token[String]] = None,
                            publish:      Option[Token[String]] = None,
                            precondition: Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:runDockerAction",
      name = name,
      precondition = precondition,
      inputs = Some(
        Map(
          "action"    -> Some(action.toJson),
          "container" -> container.map(_.toJson),
          "image"     -> image.map(_.toJson),
          "cmd"       -> cmd.map(_.toJson),
          "memory"    -> memory.map(_.toJson),
          "cpuShares" -> cpuShares.map(_.toJson),
          "volume"    -> volume.map(_.toJson),
          "env"       -> env.map(_.toJson),
          "user"      -> user.map(_.toJson),
          "publish"   -> publish.map(_.toJson)
        ).flatMap { case (k, ov) => ov.map(v => k -> v) }
      )
    )

  /**
    * Run Linux shell scripts or specify the path to a script to run. This plugin only runs on Linux operating systems.
    * For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param runCommand Specify the command(s) to run or the path to an existing script on the instance.
    * @param timeoutSeconds The time in seconds for a command to be completed before it is considered to have failed.
    * @param workingDirectory The path to the working directory on your instance.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:runShellScript`(name:             String,
                           runCommand:       Seq[Token[String]],
                           timeoutSeconds:   Option[Token[String]] = None,
                           workingDirectory: Option[Token[String]] = None,
                           precondition:     Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:runShellScript",
      name = name,
      precondition = precondition,
      inputs = Some(
        Map(
          "runCommand"       -> Some(runCommand.toJson),
          "timeoutSeconds"   -> timeoutSeconds.map(_.toJson),
          "workingDirectory" -> workingDirectory.map(_.toJson)
        ).flatMap { case (k, ov) => ov.map(v => k -> v) }
      )
    )

  /**
    * (Schema version 2.0 or later) Gather an inventory of applications, AWS components, network configuration, Windows
    * Updates, and custom inventory from an instance. This plugin runs on Linux and Microsoft Windows operating systems.
    * For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param applications Collect data for installed applications.
    * @param awsComponents Collect data for AWS components like amazon-ssm-agent.
    * @param networkConfig Collect data for network configuration.
    * @param windowsUpdates Collect data for all Windows updates.
    * @param customInventory Collect data for custom inventory.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:softwareInventory`(name:            String,
                              applications:    Option[Token[String]] = None,
                              awsComponents:   Option[Token[String]] = None,
                              networkConfig:   Option[Token[String]] = None,
                              windowsUpdates:  Option[Token[String]] = None,
                              customInventory: Option[Token[String]] = None,
                              precondition:    Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:softwareInventory",
      name = name,
      precondition = precondition,
      inputs = Some(
        Map(
          "applications"    -> applications.map(_.toJson),
          "awsComponents"   -> awsComponents.map(_.toJson),
          "networkConfig"   -> networkConfig.map(_.toJson),
          "windowsUpdates"  -> windowsUpdates.map(_.toJson),
          "customInventory" -> customInventory.map(_.toJson)
        ).flatMap { case (k, ov) => ov.map(v => k -> v) }
      )
    )

  /**
    * Update the SSM Agent to the latest version or specify an older version. This plugin runs on Linux and Windows
    * operating systems. For more information, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/ssm-agent.html Installing SSM Agent]].
    * For more information about documents, see
    * [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-ssm-docs.html Systems Manager Documents]].
    *
    * @param name Step name
    * @param allowDowngrade Allow the SSM Agent to be downgraded to an earlier version. If set to false, the agent can
    *                       be upgraded to newer versions only (default). If set to true, specify the earlier version.
    * @param targetVersion A specific version of the SSM Agent to install. If not specified, the agent will be updated
    *                      to the latest version.
    * @param precondition A condition that must be satisfied. If not satisfied, the step will be skipped.
    * @return The document main step
    */
  def `aws:updateSsmAgent`(name:           String,
                           allowDowngrade: Token[String] = "false",
                           targetVersion:  Option[Token[String]] = None,
                           precondition:   Option[Precondition] = None): DocumentStep =
    DocumentStep(
      action = "aws:updateSsmAgent",
      name = name,
      precondition = precondition,
      inputs = Some(
        Map(
          "agentName"     -> Some(JsString("amazon-ssm-agent")),
          "allowDowngrade"-> Some(allowDowngrade.toJson),
          "source"        -> Some(JsString("https://s3.{Region}.amazonaws.com/amazon-ssm-{Region}/ssm-agent-manifest.json")),
          "targetVersion" -> targetVersion.map(_.toJson)
        ).flatMap { case (k, ov) => ov.map(v => k -> v) }
      )
    )
}


sealed trait InstallUninstall {
  lazy val token: Token[String] = StringToken(this.toString)
}

object InstallUninstall extends DefaultJsonProtocol {
  case object Install    extends InstallUninstall
  case object Uninstall  extends InstallUninstall
  val values = Seq(Install, Uninstall)
  implicit val format: JsonFormat[InstallUninstall] = new EnumFormat[InstallUninstall](values)
}


sealed trait InstallRepairUninstall {
  lazy val token: Token[String] = StringToken(this.toString)
}

object InstallRepairUninstall extends DefaultJsonProtocol {
  case object Install    extends InstallRepairUninstall
  case object Repair     extends InstallRepairUninstall
  case object Uninstall  extends InstallRepairUninstall
  val values = Seq(Install, Repair, Uninstall)
  implicit val format: JsonFormat[InstallRepairUninstall] = new EnumFormat[InstallRepairUninstall](values)
}


case class Precondition(operation: String, args: Seq[Token[String]])
object Precondition extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Precondition] = new JsonFormat[Precondition]{
    def write(obj: Precondition): JsValue = {
      JsObject(
        obj.operation -> obj.args.toJson
      )
    }

    def read(json: JsValue): Precondition = deserializationError("Precondition not readable")
  }

  val PlatformTypeLinux: Precondition = Precondition("StringEquals", Seq("platformType", "Linux"))
  val PlatformTypeWindows: Precondition = Precondition("StringEquals", Seq("platformType", "Windows"))
}

/**
  * Represents either a Rate or Cron schedule expression.
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-maintenance-cron.html Cron Schedules for Systems Manager]]
  */
sealed trait ScheduleExpression

/**
  * A Cron-format schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-maintenance-cron.html Cron Schedules for Systems Manager]]
  */
case class CronSchedule(minute: String = "*",
                        hour: String = "*",
                        dayOfMonth: String = "*",
                        month: String = "*",
                        dayOfWeek: String = "*",
                        year: String = "*") extends ScheduleExpression

/**
  * A minute-rate schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-maintenance-cron.html Cron Schedules for Systems Manager]]
  */
case class MinuteRateSchedule(value: Int) extends ScheduleExpression {
  require(value > 0, "Minute rate schedule must be greater than 0")
}

/**
  * An hour-rate schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-maintenance-cron.html Cron Schedules for Systems Manager]]
  */
case class HourRateSchedule(value: Int) extends ScheduleExpression {
  require(value > 0, "Hour rate schedule must be greater than 0")
}

/**
  * A day-rate schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-maintenance-cron.html Cron Schedules for Systems Manager]]
  */
case class DayRateSchedule(value: Int) extends ScheduleExpression {
  require(value > 0, "Day rate schedule must be greater than 0")
}

object ScheduleExpression extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ScheduleExpression] = new JsonFormat[ScheduleExpression]{
    def write(obj: ScheduleExpression): JsValue = obj match {
      case se: CronSchedule =>
        JsString(s"cron(${se.minute} ${se.hour} ${se.dayOfMonth} ${se.month} ${se.dayOfWeek} ${se.year})")

      case se: MinuteRateSchedule if se.value == 1 => JsString(s"rate(1 minute)")
      case se: MinuteRateSchedule                  => JsString(s"rate(${se.value} minutes)")
      case se: HourRateSchedule if se.value == 1   => JsString(s"rate(1 hour)")
      case se: HourRateSchedule                    => JsString(s"rate(${se.value} hours)")
      case se: DayRateSchedule if se.value == 1    => JsString(s"rate(1 day)")
      case se: DayRateSchedule                     => JsString(s"rate(${se.value} days)")
    }

    def read(json: JsValue): ScheduleExpression = deserializationError("ScheduleExpression not readable")
  }
}
