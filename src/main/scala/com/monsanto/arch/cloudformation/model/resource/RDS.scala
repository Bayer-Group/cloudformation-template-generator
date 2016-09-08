package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/** There are several mutually exclusive options when creating an RDS instance.
  * Therefore we make its case class package private and use a builder
  * pattern to constrain use to those which will create valid instances.
  *
  * The builder approach we use does its best to make sure you cannot create a template
  * with an invalid RDS instance. It may not be perfect, so be sure to double check the
  * resulting template and let us know of any bugs.
  *
  * See http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-rds-database-instance.html
  * and http://docs.aws.amazon.com/AmazonRDS/latest/APIReference/API_CreateDBInstance.html
  *
  * @param name
  * @param AllocatedStorage
  * @param DBInstanceClass
  * @param AllowMajorVersionUpgrade
  * @param AutoMinorVersionUpgrade
  * @param AvailabilityZone cannot be set when MultiAZ set
  * @param BackupRetentionPeriod cannot be used with SourceDBInstanceIdentifier
  * @param DBInstanceIdentifier
  * @param DBName cannot be used with SourceDBInstanceIdentifier
  * @param DBParameterGroupName
  * @param DBSecurityGroups cannot be set when VPCSecurityGroups set
  * @param DBSnapshotIdentifier
  * @param DBSubnetGroupName required for VPC RDS
  * @param Engine required if no DBSnapshotIdentifier given
  * @param EngineVersion
  * @param Iops various complicated restrictions, see http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_Storage.html
  * @param KmsKeyId does not work with DBSnapshotIdentifier or SourceDBInstanceIdentifier, ignored if DBSecurityGroups set
  * @param LicenseModel
  * @param MasterUsername required if no DBSnapshotIdentifier given, cannot be used with SourceDBInstanceIdentifier
  * @param MasterUserPassword required if no DBSnapshotIdentifier given, cannot be used with SourceDBInstanceIdentifier
  * @param MultiAZ cannot be set when AvailabilityZone set, cannot be used with SourceDBInstanceIdentifier
  * @param OptionGroupName
  * @param Port
  * @param PreferredBackupWindow cannot be used with SourceDBInstanceIdentifier
  * @param PreferredMaintenanceWindow
  * @param PubliclyAccessible
  * @param SourceDBInstanceIdentifier does not work with MultiAZ, DBSnapshotIdentifier, etc.
  * @param StorageEncrypted ignored if DBSecurityGroups set, does not work with DBSnapshotIdentifier or SourceDBInstanceIdentifier, required with KmsKeyId
  * @param StorageType ignored if DBSecurityGroups set
  * @param Tags
  * @param VPCSecurityGroups cannot be set when DBSecurityGroups set
  * @param Condition
  * @param DependsOn
  * @param DeletionPolicy
  */
case class `AWS::RDS::DBInstance` private[resource] (
  name:                        String,
  AllocatedStorage:            Either[Int, Token[Int]],
  DBInstanceClass:             Token[String],
  AllowMajorVersionUpgrade:    Option[Boolean],
  AutoMinorVersionUpgrade:     Option[Boolean],
  AvailabilityZone:            Option[String],
  BackupRetentionPeriod:       Option[String],
  //CharacterSetName:          Option[Token[`AWS::RDS::DBInstance::CharacterSetName`]], // Oracle specific http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Appendix.OracleCharacterSets.html
  //DBClusterIdentifier:       Option[Token[String]], // only for aurora
  DBInstanceIdentifier:        Option[Token[String]],
  DBName:                      Option[Token[String]],
  DBParameterGroupName:        Option[ResourceRef[`AWS::RDS::DBParameterGroup`]],
  DBSecurityGroups:            Option[Seq[ResourceRef[`AWS::RDS::DBSecurityGroup`]]],
  DBSnapshotIdentifier:        Option[String],
  DBSubnetGroupName:           Option[Token[ResourceRef[`AWS::RDS::DBSubnetGroup`]]],
  Engine:                      Option[Token[`AWS::RDS::DBInstance::Engine`]],
  EngineVersion:               Option[String],
  Iops:                        Option[Either[Int, Token[Int]]],
  KmsKeyId:                    Option[Token[String]],
  LicenseModel:                Option[`AWS::RDS::DBInstance::LicenseModel`],
  MasterUsername:              Option[Token[String]],
  MasterUserPassword:          Option[Token[String]],
  MultiAZ:                     Option[Boolean],
  OptionGroupName:             Option[String],
  Port:                        Option[Token[String]],
  PreferredBackupWindow:       Option[String],
  PreferredMaintenanceWindow:  Option[String],
  PubliclyAccessible:          Option[Boolean],
  SourceDBInstanceIdentifier:  Option[Token[ResourceRef[`AWS::RDS::DBInstance`]]],
  StorageEncrypted:            Option[Boolean],
  StorageType:                 Option[`AWS::RDS::DBInstance::StorageType`],
  Tags:                        Option[Seq[AmazonTag]],
  VPCSecurityGroups:           Option[Seq[ResourceRef[`AWS::EC2::SecurityGroup`]]],
  override val Condition:      Option[ConditionRef],
  override val DependsOn:      Option[Seq[String]],
  override val DeletionPolicy: Option[DeletionPolicy]
) extends Resource[`AWS::RDS::DBInstance`] {
  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBInstance` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBInstance`] = new RootJsonFormat[`AWS::RDS::DBInstance`] {
    def write(d: `AWS::RDS::DBInstance`) = {
      val obj = JsObject(
        "AllocatedStorage"           -> d.AllocatedStorage.toJson,
        "DBInstanceClass"            -> d.DBInstanceClass.toJson,
        "AllowMajorVersionUpgrade"   -> d.AllowMajorVersionUpgrade.toJson,
        "AutoMinorVersionUpgrade"    -> d.AutoMinorVersionUpgrade.toJson,
        "AvailabilityZone"           -> d.AvailabilityZone.toJson,
        "BackupRetentionPeriod"      -> d.BackupRetentionPeriod.toJson,
        "DBInstanceIdentifier"       -> d.DBInstanceIdentifier.toJson,
        "DBName"                     -> d.DBName.toJson,
        "DBParameterGroupName"       -> d.DBParameterGroupName.toJson,
        "DBSecurityGroups"           -> d.DBSecurityGroups.toJson,
        "DBSnapshotIdentifier"       -> d.DBSnapshotIdentifier.toJson,
        "DBSubnetGroupName"          -> d.DBSubnetGroupName.toJson,
        "Engine"                     -> d.Engine.toJson,
        "EngineVersion"              -> d.EngineVersion.toJson,
        "Iops"                       -> d.Iops.toJson,
        "KmsKeyId"                   -> d.KmsKeyId.toJson,
        "LicenseModel"               -> d.LicenseModel.toJson,
        "MasterUsername"             -> d.MasterUsername.toJson,
        "MasterUserPassword"         -> d.MasterUserPassword.toJson,
        "MultiAZ"                    -> d.MultiAZ.toJson,
        "OptionGroupName"            -> d.OptionGroupName.toJson,
        "Port"                       -> d.Port.toJson,
        "PreferredBackupWindow"      -> d.PreferredBackupWindow.toJson,
        "PreferredMaintenanceWindow" -> d.PreferredMaintenanceWindow.toJson,
        "PubliclyAccessible"         -> d.PubliclyAccessible.toJson,
        "SourceDBInstanceIdentifier" -> d.SourceDBInstanceIdentifier.toJson,
        "StorageEncrypted"           -> d.StorageEncrypted.toJson,
        "StorageType"                -> d.StorageType.toJson,
        "Tags"                       -> d.Tags.toJson,
        "VPCSecurityGroups"          -> d.VPCSecurityGroups.toJson
      )
      obj.copy(fields = obj.fields.filter(_._2 != JsNull))
    }
    def read(value: JsValue) = ???
  }
}

/** Enforces that either an RDS Instance is in a VPC or not. */
sealed trait RdsLocation {
  private[resource] def location(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance`
}

/** RDS instance in a VPC.
  *
  * @param dbSubnetGroupName AWS::RDS::DBInstance(DBSubnetGroupName)
  * @param vpcSecurityGroups AWS::RDS::DBInstance(VPCSecurityGroups)
  */
case class RdsVpc(
  dbSubnetGroupName:   Token[ResourceRef[`AWS::RDS::DBSubnetGroup`]],
  vpcSecurityGroups:   Option[Seq[ResourceRef[`AWS::EC2::SecurityGroup`]]] = None
) extends RdsLocation {
  private[resource] def location(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(
      DBSecurityGroups  = None,
      DBSubnetGroupName = Some(dbSubnetGroupName),
      VPCSecurityGroups = vpcSecurityGroups
    )

}

/** RDS instance not in a VPC, a "classic".
  *
  * @param dbSecurityGroups AWS::RDS::DBInstance(DBSecurityGroups)
  */
case class RdsClassic(
  dbSecurityGroups: Option[Seq[ResourceRef[`AWS::RDS::DBSecurityGroup`]]] = None
) extends RdsLocation {
  private[resource] def location(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(
      DBSecurityGroups  = dbSecurityGroups,
      DBSubnetGroupName = None,
      VPCSecurityGroups = None
    )
}

/** This trait enforces that either a single AZ is given, MultiAZ specified, or the default is used. */
sealed trait RdsAvailabilityZone {
  private[resource] def az(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance`
}

/** Single availability zone RDS instance.
  *
  * @param az AWS::RDS::DBInstance(AvailabilityZone), e.g., Some("us-east-1a")
  */
case class RdsSingleAZ(az: Option[String] = None) extends RdsAvailabilityZone {
  private[resource] def az(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(AvailabilityZone = az, MultiAZ = None)
}

/** Multi-availability zone RDS instance.
  *
  * @param multiAz AWS::RDS::DBInstance(MultiAZ)
  */
case class RdsMultiAZ(multiAz: Boolean = true) extends RdsAvailabilityZone {
  private[resource] def az(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(MultiAZ = Some(multiAz), AvailabilityZone = None)
}
case class RdsNoAZ() extends RdsAvailabilityZone {
  private[resource] def az(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` = rdsInstance
}

/** You can only enable encryption on new RDS instances created in a VPC.
  * Therefore only NewRDS from RdsSource takes a parameters of this type.
  * The enforcement of "within a VPC" is done at run time in the RdsBuilder.
  */
sealed trait RdsEncryption {
  /** Must set the AWS::RDS::DBInstance fields related to encryption.
    *
    * The fields are StorageEncrypted and KmsKeyId
    *
    * @param rdsInstance The RDS instance to modify.
    * @return A new RDS instances with the appropriate elements set.
    */
  private[resource] def encryption(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance`
}

/** You must have StorageEncrypted set to true is setting KmsKeyId,
  * so we only take this value in this case class, which sets
  * StorageEncrypted to true.
  *
  * @param kmsKeyId AWS::RDS::DBInstance(KmsKeyId)
  */
case class RdsEncryptionStorage(kmsKeyId: Option[Token[String]] = None) extends RdsEncryption {
  private[resource] def encryption(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(StorageEncrypted = Some(true), KmsKeyId = kmsKeyId)
}

/** Sets both StorageEncrypted and KmsKeyId to None. */
case class RdsEncryptionNone() extends RdsEncryption {
  private[resource] def encryption(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(StorageEncrypted = None, KmsKeyId = None)
}

/** Enforces the constraint that an RDS instance can only be new,
  * created from a snapshot, or a read replica of another RDS instance.
  */
sealed trait RdsSource {
  /** Must set the AWS::RDS::DBInstance fields relevant to the origin of an RDS instance.
    *
    * The fields are BackupRetentionPeriod, DBName, DBSnapshotIdentifier,
    * Engine, MasterUsername, MasterUserPassword, PreferredBackupWindow,
    * SourceDBInstanceIdentifier, and DeletionPolicy.
    *
    * @param rdsInstance The RDS instance to modify.
    * @return A new RDS instances with the appropriate elements set.
    */
  private[resource] def source(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance`
}

/** Provides valid options for a new RDS instance.
  *
  * Most parameters map directly to their similarly named parameters for
  * the AWS::RDS::DBInstance case class.
  *
  * @param rdsAvailabilityZone See [[RdsAvailabilityZone]]
  * @param rdsEncryption See [[RdsEncryptionStorage]]
  * @param engine AWS::RDS::DBInstance(Engine)
  * @param masterUsername AWS::RDS::DBInstance(MasterUsername)
  * @param masterUserPassword AWS::RDS::DBInstance(MasterUserPassword)
  * @param backupRetentionPeriod AWS::RDS::DBInstance(BackupRetentionPeriod)
  * @param dbName AWS::RDS::DBInstance(DBName)
  * @param preferredBackupWindow AWS::RDS::DBInstance(PreferredBackupWindow)
  * @param deletionPolicy AWS::RDS::DBInstance(DeletionPolicy)
  */
case class NewRds(
  rdsAvailabilityZone:   RdsAvailabilityZone,
  rdsEncryption:         RdsEncryption,
  engine:                Token[`AWS::RDS::DBInstance::Engine`],
  masterUsername:        Token[String],
  masterUserPassword:    Token[String],
  backupRetentionPeriod: Option[String]         = None,
  dbName:                Option[Token[String]]  = None,
  preferredBackupWindow: Option[String]         = None,
  deletionPolicy:        Option[DeletionPolicy] = None
) extends RdsSource {
  private[resource] def source(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsAvailabilityZone.az(rdsEncryption.encryption(rdsInstance.copy(
      BackupRetentionPeriod      = backupRetentionPeriod,
      DBName                     = dbName,
      DBSnapshotIdentifier       = None,
      Engine                     = Some(engine),
      MasterUsername             = Some(masterUsername),
      MasterUserPassword         = Some(masterUserPassword),
      PreferredBackupWindow      = preferredBackupWindow,
      SourceDBInstanceIdentifier = None,
      DeletionPolicy             = deletionPolicy
    )))
}

/** Provides valid options for an RDS instance from a snapshot.
  *
  * Most parameters map directly to their similarly named parameters for
  * the AWS::RDS::DBInstance case class.
  *
  * @param rdsAvailabilityZone See [[RdsAvailabilityZone]]
  * @param dbSnapshotIdentifier AWS::RDS::DBInstance(DBSnapshotIdentifier)
  * @param backupRetentionPeriod AWS::RDS::DBInstance(BackupRetentionPeriod)
  * @param dbName AWS::RDS::DBInstance(DBName)
  * @param engine AWS::RDS::DBInstance(Engine)
  * @param masterUsername AWS::RDS::DBInstance(MasterUsername)
  * @param masterUserPassword AWS::RDS::DBInstance(MasterUserPassword)
  * @param preferredBackupWindow AWS::RDS::DBInstance(PreferredBackupWindow)
  * @param deletionPolicy AWS::RDS::DBInstance(DeletionPolicy)
  */
case class FromSnapshot(
  rdsAvailabilityZone:   RdsAvailabilityZone,
  dbSnapshotIdentifier:  String,
  backupRetentionPeriod: Option[String]                                = None,
  dbName:                Option[Token[String]]                         = None,
  engine:                Option[Token[`AWS::RDS::DBInstance::Engine`]] = None,
  masterUsername:        Option[Token[String]]                         = None,
  masterUserPassword:    Option[Token[String]]                         = None,
  preferredBackupWindow: Option[String]                                = None,
  deletionPolicy:        Option[DeletionPolicy]                        = None
) extends RdsSource {
  private[resource] def source(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsAvailabilityZone.az(RdsEncryptionNone().encryption(rdsInstance.copy(
      BackupRetentionPeriod      = backupRetentionPeriod,
      DBName                     = dbName,
      DBSnapshotIdentifier       = Some(dbSnapshotIdentifier),
      Engine                     = engine,
      MasterUsername             = masterUsername,
      MasterUserPassword         = masterUserPassword,
      PreferredBackupWindow      = preferredBackupWindow,
      SourceDBInstanceIdentifier = None,
      DeletionPolicy             = deletionPolicy
    )))
}

/** Provide valid options for creating a read replica RDS instance
  *
  * @param sourceDBInstanceIdentifier AWS::RDS::DBInstance(SourceDBInstanceIdentifier)
  * @param availabilityZone AWS::RDS::DBInstance(AvailabilityZone)
  */
case class ReadReplica(
  sourceDBInstanceIdentifier: Token[ResourceRef[`AWS::RDS::DBInstance`]],
  availabilityZone:           Option[String] = None
) extends RdsSource {
  private[resource] def source(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    RdsSingleAZ(availabilityZone).az(RdsEncryptionNone().encryption(rdsInstance.copy(
      BackupRetentionPeriod      = None,
      DBName                     = None,
      Engine                     = None,
      MasterUsername             = None,
      MasterUserPassword         = None,
      PreferredBackupWindow      = None,
      SourceDBInstanceIdentifier = Some(sourceDBInstanceIdentifier),
      DeletionPolicy             = None
    )))
}

/** Enforce that specified storage has appropriate other values set or not. */
sealed trait RdsStorageType {
  /** Must set the AWS::RDS::DBInstance fields relevant to storage.
    *
    * Fields relevant to storage are Iops and StorageType.
    *
    * @param rdsInstance The RDS instance to modify.
    * @return A new RDS instances with the appropriate elements set.
    */
  private[resource] def storage(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance`
}

/** No options for standard storage type */
case class RdsStorageTypeStandard() extends RdsStorageType {
  private[resource] def storage(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(
      Iops        = None,
      StorageType = Some(`AWS::RDS::DBInstance::StorageType`.standard)
    )
}

/** No options for gp2 storage type. */
case class RdsStorageTypeGp2() extends RdsStorageType {
  private[resource] def storage(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` =
    rdsInstance.copy(
      Iops        = None,
      StorageType = Some(`AWS::RDS::DBInstance::StorageType`.gp2)
    )
}

/** Provide options for an RDS instance with IOPS specified.
  *
  * @param iops This value is constrained. See http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_Storage.html for details.
  */
case class RdsStorageTypeIo1(iops: Either[Int, Token[Int]]) extends RdsStorageType {
  private def isIopsValueValid(storage: Either[Int, Token[Int]]): Boolean = (iops, storage) match {
    case (Left(i), Left(s)) =>
      i >= 1000 &&
        i <= 30000 &&
        i % s == 0 &&
        i / s >= 3 &&
        i / s <= 10
    case _ => true
  }

  private[resource] def storage(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` = {
    val storage = rdsInstance.AllocatedStorage
    if (!isIopsValueValid(storage))
      throw new IllegalArgumentException(s"invalid Iops value $iops for AllocatedStorage $storage. See http://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_Storage.html")
    rdsInstance.copy(
        Iops        = Some(iops),
        StorageType = Some(`AWS::RDS::DBInstance::StorageType`.io1)
    )
  }
}

/** No options for the default storage type, as you might expect. */
case class RdsStorageTypeDefault() extends RdsStorageType {
  private[resource] def storage(rdsInstance: `AWS::RDS::DBInstance`): `AWS::RDS::DBInstance` = rdsInstance
}

/** Generic, safe builder for AWS::RDS::DBInstance. */
object RdsBuilder {
  /** AWS::RDS::DBInstance builder.
    *
    * @param name RDS instance name
    * @param rdsSource See [[RdsSource]]
    * @param rdsLocation See [[RdsLocation]]
    * @param allocatedStorage AWS::RDS::DBInstance(AllocatedStorage)
    * @param dbInstanceClass AWS::RDS::DBInstance(DBInstanceClass)
    * @param rdsStorageType See [[RdsStorageType]]
    * @param allowMajorVersionUpgrade AWS::RDS::DBInstance(AllowMajorVersionUpgrade)
    * @param autoMinorVersionUpgrade AWS::RDS::DBInstance(AutoMinorVersionUpgrade)
    * @param dbInstanceIdentifier AWS::RDS::DBInstance(DBInstanceIdentifier)
    * @param dbParameterGroupName AWS::RDS::DBInstance(DBParameterGroupName)
    * @param engineVersion AWS::RDS::DBInstance(EngineVersion)
    * @param licenseModel AWS::RDS::DBInstance(LicenseModel)
    * @param optionGroupName AWS::RDS::DBInstance(OptionGroupName)
    * @param port AWS::RDS::DBInstance(Port)
    * @param preferredMaintenanceWindow AWS::RDS::DBInstance(PreferredMaintenanceWindow)
    * @param publiclyAccessible AWS::RDS::DBInstance(PubliclyAccessible)
    * @param tags AWS::RDS::DBInstance(Tags)
    * @param condition AWS::RDS::DBInstance(Condition)
    * @param dependsOn AWS::RDS::DBInstance(DependsOn)
    * @return An instance of [[`AWS::RDS::DBInstance`]].
    */
  def buildRds(
    name:                       String,
    rdsSource:                  RdsSource,
    rdsLocation:                RdsLocation,
    allocatedStorage:           Either[Int,Token[Int]],
    dbInstanceClass:            Token[String],
    rdsStorageType:             RdsStorageType                                    = RdsStorageTypeDefault(),
    allowMajorVersionUpgrade:   Option[Boolean]                                   = None,
    autoMinorVersionUpgrade:    Option[Boolean]                                   = None,
    dbInstanceIdentifier:       Option[Token[String]]                             = None,
    dbParameterGroupName:       Option[ResourceRef[`AWS::RDS::DBParameterGroup`]] = None,
    engineVersion:              Option[String]                                    = None,
    licenseModel:               Option[`AWS::RDS::DBInstance::LicenseModel`]      = None,
    optionGroupName:            Option[String]                                    = None,
    port:                       Option[Token[String]]                             = None,
    preferredMaintenanceWindow: Option[String]                                    = None,
    publiclyAccessible:         Option[Boolean]                                   = None,
    tags:                       Option[Seq[AmazonTag]]                            = None,
    condition:                  Option[ConditionRef]                              = None,
    dependsOn:                  Option[Seq[String]]                               = None
  ): `AWS::RDS::DBInstance` = {
    // ensure encryption only requested on VPC RDS instances
    val isEncrypted: Boolean = rdsSource match {
      case NewRds(_, RdsEncryptionStorage(_), _, _, _, _, _, _, _) => true
      case _                                                      => false
    }
    val isVpc: Boolean = rdsLocation match {
      case RdsVpc(_, _) => true
      case _            => false
    }
    if (isEncrypted && !isVpc)
      throw new IllegalArgumentException("You cannot use storage encryption in non-VPC RDS instances")
    rdsSource.source(rdsLocation.location(rdsStorageType.storage(`AWS::RDS::DBInstance`(
      name                       = name,
      AllocatedStorage           = allocatedStorage,
      DBInstanceClass            = dbInstanceClass,
      AllowMajorVersionUpgrade   = allowMajorVersionUpgrade,
      AutoMinorVersionUpgrade    = autoMinorVersionUpgrade,
      AvailabilityZone           = None,
      BackupRetentionPeriod      = None,
      DBInstanceIdentifier       = dbInstanceIdentifier,
      DBName                     = None,
      DBParameterGroupName       = dbParameterGroupName,
      DBSecurityGroups           = None,
      DBSnapshotIdentifier       = None,
      DBSubnetGroupName          = None,
      Engine                     = None,
      EngineVersion              = engineVersion,
      Iops                       = None,
      KmsKeyId                   = None,
      LicenseModel               = licenseModel,
      MasterUsername             = None,
      MasterUserPassword         = None,
      MultiAZ                    = None,
      OptionGroupName            = optionGroupName,
      Port                       = port,
      PreferredBackupWindow      = None,
      PreferredMaintenanceWindow = preferredMaintenanceWindow,
      PubliclyAccessible         = publiclyAccessible,
      SourceDBInstanceIdentifier = None,
      StorageEncrypted           = None,
      StorageType                = None,
      Tags                       = tags,
      VPCSecurityGroups          = None,
      Condition                  = condition,
      DependsOn                  = dependsOn,
      DeletionPolicy             = None
    ))))
  }
}

sealed trait `AWS::RDS::DBInstance::Engine`
object `AWS::RDS::DBInstance::Engine` extends DefaultJsonProtocol {
  case object MySQL    extends `AWS::RDS::DBInstance::Engine`
  case object postgres extends `AWS::RDS::DBInstance::Engine`
  case object MariaDB extends `AWS::RDS::DBInstance::Engine`
  val values = Seq(MySQL, postgres, MariaDB)
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::Engine`] =
    new EnumFormat[`AWS::RDS::DBInstance::Engine`](values)
}

sealed trait `AWS::RDS::DBInstance::StorageType`
object `AWS::RDS::DBInstance::StorageType` extends DefaultJsonProtocol {
  case object standard extends `AWS::RDS::DBInstance::StorageType`
  case object gp2      extends `AWS::RDS::DBInstance::StorageType`
  case object io1      extends `AWS::RDS::DBInstance::StorageType`
  val values = Seq(standard, gp2, io1)
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::StorageType`] =
    new EnumFormat[`AWS::RDS::DBInstance::StorageType`](values)
}

sealed trait `AWS::RDS::DBInstance::LicenseModel`
object `AWS::RDS::DBInstance::LicenseModel` extends DefaultJsonProtocol {
  case object `license-included`       extends `AWS::RDS::DBInstance::LicenseModel`
  case object `bring-your-own-license` extends `AWS::RDS::DBInstance::LicenseModel`
  case object `general-public-license` extends `AWS::RDS::DBInstance::LicenseModel`
  val values = Seq(`license-included`, `bring-your-own-license`, `general-public-license`)
  implicit val format: JsonFormat[`AWS::RDS::DBInstance::LicenseModel`] =
    new EnumFormat[`AWS::RDS::DBInstance::LicenseModel`](values)
}

case class `AWS::RDS::DBParameterGroup`(
  name:        String,
  Description: String,
  Family:      String,
  Parameters:  Option[Map[String,String]] = None,
  Tags:        Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::RDS::DBParameterGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBParameterGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBParameterGroup`] = jsonFormat6(`AWS::RDS::DBParameterGroup`.apply)
}

/** Database security group.
  *
  * @param name
  * @param DBSecurityGroupIngress Finite list of keys: http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-rds-security-group-rule.html
  * @param GroupDescription
  * @param EC2VpcId
  * @param Tags
  * @param Condition
  */
case class `AWS::RDS::DBSecurityGroup`(
  name:                   String,
  DBSecurityGroupIngress: Seq[RDSDBSecurityGroupRule],
  GroupDescription:       String,
  EC2VpcId:               Option[Token[ResourceRef[`AWS::EC2::VPC`]]] = None,
  Tags:                   Option[Seq[AmazonTag]]               = None,
  override val Condition: Option[ConditionRef]                 = None
) extends Resource[`AWS::RDS::DBSecurityGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBSecurityGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBSecurityGroup`] = jsonFormat6(`AWS::RDS::DBSecurityGroup`.apply)
}

case class RDSDBSecurityGroupRule(
  CIDRIP:                  Option[Token[CidrBlock]],
  EC2SecurityGroupId:      Option[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
  EC2SecurityGroupName:    Option[Token[ResourceRef[`AWS::EC2::SecurityGroup`]]],
  EC2SecurityGroupOwnerId: Option[String]
)
object RDSDBSecurityGroupRule extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RDSDBSecurityGroupRule] = jsonFormat4(RDSDBSecurityGroupRule.apply)
}

case class `AWS::RDS::DBSubnetGroup`(
  name:                     String,
  DBSubnetGroupDescription: String,
  SubnetIds:                Token[Seq[ResourceRef[`AWS::EC2::Subnet`]]],
  Tags:                     Option[Seq[AmazonTag]] = None,
  override val Condition: Option[ConditionRef]     = None
) extends Resource[`AWS::RDS::DBSubnetGroup`]{

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
}
object `AWS::RDS::DBSubnetGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::RDS::DBSubnetGroup`] = jsonFormat5(`AWS::RDS::DBSubnetGroup`.apply)
}
