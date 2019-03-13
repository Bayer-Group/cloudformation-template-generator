package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, EnumFormat, Token, `Fn::GetAtt`}
import spray.json._

case class StreamEncryption(
  KeyId: Token[String],
  EncryptionType: String = "KMS"
)

object StreamEncryption extends DefaultJsonProtocol {
  implicit val format: JsonFormat[StreamEncryption] = jsonFormat2(StreamEncryption.apply)
}

case class `AWS::Kinesis::Stream`(
  name: String,
  ShardCount: Token[Int],
  Name: Option[String] = None,
  RetentionPeriodHours: Option[Token[Int]] = None,
  Tags: Option[Seq[AmazonTag]] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None,
  StreamEncryption: Option[StreamEncryption] = None
) extends Resource[`AWS::Kinesis::Stream`] with HasArn {
  override def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)
  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::Kinesis::Stream` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Kinesis::Stream`] = jsonFormat8(`AWS::Kinesis::Stream`.apply)
}

case class BufferingHints(
  IntervalInSeconds: Option[Int],
  SizeInMBs: Option[Int]
)

case object BufferingHints extends DefaultJsonProtocol {
  implicit val format: JsonFormat[BufferingHints] = jsonFormat2(BufferingHints.apply)
}

case class CloudWatchLoggingOptions(
  Enabled: Option[Boolean],
  LogGroupName: Option[Token[String]],
  LogStreamName: Option[Token[String]]
)

case object CloudWatchLoggingOptions extends DefaultJsonProtocol {
  def disabled = CloudWatchLoggingOptions(Some(false), None, None)

  def enabled(LogGroupName: Token[String], LogStreamName: Token[String]) =
    CloudWatchLoggingOptions(Some(true), Some(LogGroupName), Some(LogStreamName))

  implicit val format: JsonFormat[CloudWatchLoggingOptions] = jsonFormat3(CloudWatchLoggingOptions.apply)
}

case class KMSEncryptionConfig(
  AWSKMSKeyARN: Token[String]
)

object KMSEncryptionConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[KMSEncryptionConfig] = jsonFormat1(KMSEncryptionConfig.apply)
}


case class EncryptionConfiguration(
  KMSEncryptionConfig: Option[KMSEncryptionConfig],
  NoEncryptionConfig: Option[String]
)

object EncryptionConfiguration extends DefaultJsonProtocol {
  def withKMS(config:KMSEncryptionConfig) = new EncryptionConfiguration(Some(config),None)
  def noEncryption = new EncryptionConfiguration(None,Some("NoEncryption"))
  implicit val format: JsonFormat[EncryptionConfiguration] = jsonFormat2(EncryptionConfiguration.apply)
}

case class ProcessorParameter(
  ParameterName: String,
  ParameterValue: Token[String])

object ProcessorParameter extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ProcessorParameter] = jsonFormat2(ProcessorParameter.apply)
}

case class Processor(
  Parameters: Seq[ProcessorParameter],
  Type: String = "Lambda"
)
object Processor extends DefaultJsonProtocol{
  implicit val format: JsonFormat[Processor] = jsonFormat2(Processor.apply)

}

case class ProcessingConfiguration(
  Enabled: Option[Boolean],
  Processors: Option[Seq[Processor]]
)

object ProcessingConfiguration extends DefaultJsonProtocol {
  def disabled = ProcessingConfiguration(Some(false), None)

  def enabled(processors: Seq[Processor]) = ProcessingConfiguration(Some(true), Some(processors))

  implicit val format: JsonFormat[ProcessingConfiguration] = jsonFormat2(ProcessingConfiguration.apply)
}

case class S3DestinationConfiguration(
  BucketARN: String,
  BufferingHints: BufferingHints,
  CloudWatchLoggingOptions: CloudWatchLoggingOptions,
  CompressionFormat: String,
  EncryptionConfiguration: EncryptionConfiguration,
  Prefix: String,
  RoleARN: String
)

object S3DestinationConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[S3DestinationConfiguration] = jsonFormat7(S3DestinationConfiguration.apply)
}

sealed trait CompressionFormat

object CompressionFormat {

  case object UNCOMPRESSED extends CompressionFormat

  case object GZIP extends CompressionFormat

  case object ZIP extends CompressionFormat

  case object Snappy extends CompressionFormat

  val values = List(UNCOMPRESSED, GZIP, ZIP, Snappy)
  implicit val format: JsonFormat[CompressionFormat] = new EnumFormat[CompressionFormat](values)
}

case class ExtendedS3DestinationConfiguration(
  BucketARN: Token[String],
  RoleARN: Token[String],
  BufferingHints: Option[BufferingHints] = None,
  CloudWatchLoggingOptions: Option[CloudWatchLoggingOptions] = None,
  CompressionFormat: Option[CompressionFormat] = None,
  EncryptionConfiguration: Option[EncryptionConfiguration] = None,
  Prefix: Option[String] = None,
  ProcessingConfiguration: Option[ProcessingConfiguration] = None,
  S3BackupConfiguration: Option[S3DestinationConfiguration] = None,
  S3BackupMode: Option[String] = None
)

object ExtendedS3DestinationConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ExtendedS3DestinationConfiguration] = jsonFormat10(ExtendedS3DestinationConfiguration.apply)
}

case class ElasticsearchDestinationConfiguration(CloudWatchLoggingOptions: CloudWatchLoggingOptions,
  BufferingHints: Option[BufferingHints])
object ElasticsearchDestinationConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ElasticsearchDestinationConfiguration] = jsonFormat2(ElasticsearchDestinationConfiguration.apply)
}


case class KinesisStreamSourceConfiguration(
  KinesisStreamARN : Token[String],
  RoleARN : Token[String]
)
object KinesisStreamSourceConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[KinesisStreamSourceConfiguration] = jsonFormat2(KinesisStreamSourceConfiguration.apply)
}

case class RedshiftDestinationConfiguration(CloudWatchLoggingOptions: CloudWatchLoggingOptions)
object RedshiftDestinationConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RedshiftDestinationConfiguration] = jsonFormat1(RedshiftDestinationConfiguration.apply)
}

case class SplunkDestinationConfiguration(CloudWatchLoggingOptions: CloudWatchLoggingOptions)
object SplunkDestinationConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[SplunkDestinationConfiguration] = jsonFormat1(SplunkDestinationConfiguration.apply)
}

sealed trait DeliveryStreamType

object DeliveryStreamType extends DefaultJsonProtocol {

  case object DirectPut extends DeliveryStreamType

  case object KinesisStreamAsSource extends DeliveryStreamType

  val values = List(DirectPut, KinesisStreamAsSource)
  implicit val format: JsonFormat[DeliveryStreamType] = new EnumFormat[DeliveryStreamType](values)
}


case class `AWS::KinesisFirehose::DeliveryStream`(
  name: String,
  DeliveryStreamName: Option[String] = None,
  DeliveryStreamType: Option[DeliveryStreamType] = None,
  ExtendedS3DestinationConfiguration: Option[ExtendedS3DestinationConfiguration] = None,
  ElasticsearchDestinationConfiguration: Option[ElasticsearchDestinationConfiguration] = None,
  KinesisStreamSourceConfiguration: Option[KinesisStreamSourceConfiguration] = None,
  RedshiftDestinationConfiguration: Option[RedshiftDestinationConfiguration] = None,
  SplunkDestinationConfiguration: Option[SplunkDestinationConfiguration] = None,
  override val DependsOn: Option[Seq[String]] = None,
  override val Condition: Option[ConditionRef] = None
) extends Resource[`AWS::KinesisFirehose::DeliveryStream`] with HasArn {
  override def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))
}

object `AWS::KinesisFirehose::DeliveryStream` extends DefaultJsonProtocol{

  def s3(name: String,
    ExtendedS3DestinationConfiguration: ExtendedS3DestinationConfiguration,
    KinesisStreamSourceConfiguration :Option[KinesisStreamSourceConfiguration] = None,
    DeliveryStreamName: Option[String] = None,
    DeliveryStreamType: Option[DeliveryStreamType] = None,
    DependsOn: Option[Seq[String]] = None,
    Condition: Option[ConditionRef] = None) =
    new `AWS::KinesisFirehose::DeliveryStream`(
      name,
      DeliveryStreamName,
      DeliveryStreamType,
      Some(ExtendedS3DestinationConfiguration),
      KinesisStreamSourceConfiguration = KinesisStreamSourceConfiguration,
      DependsOn = DependsOn,
      Condition = Condition
    )

  implicit val format: JsonFormat[`AWS::KinesisFirehose::DeliveryStream`] = jsonFormat10(`AWS::KinesisFirehose::DeliveryStream`.apply)
}
