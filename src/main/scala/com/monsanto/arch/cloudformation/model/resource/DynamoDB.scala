package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._
import DefaultJsonProtocol._

import scala.language.implicitConversions

/**
  * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-dynamodb-table.html
  * Created by Tyler Southwick on 11/24/15.
  */
case class `AWS::DynamoDB::Table`(
                                   name: String,
                                   AttributeDefinitions: Seq[AttributeDefinition],
                                   BillingMode: Option[BillingMode],
                                   GlobalSecondaryIndexes: Seq[GlobalSecondaryIndex],
                                   KeySchema: Seq[KeySchema],
                                   LocalSecondaryIndexes: Seq[LocalSecondaryIndex],
                                   ProvisionedThroughput: ProvisionedThroughput,
                                   StreamSpecification : Option[StreamSpecification] = None,
                                   TableName: Option[Token[String]],
                                   TimeToLiveSpecification: Option[TimeToLiveSpecification] = None,
                                   override val Condition: Option[ConditionRef] = None,
                                   override val DeletionPolicy: Option[DeletionPolicy] = None,
                                   override val DependsOn: Option[Seq[String]] = None
                                 ) extends Resource[`AWS::DynamoDB::Table`] with HasArn {

  override def arn = aws"arn:aws:dynamodb:${`AWS::Region`}:${`AWS::AccountId`}:table/${ResourceRef(this)}"

  def streamArn : Token[String] = `Fn::GetAtt`(Seq("StreamArn", name))

  def tableName : Token[String] = ResourceRef(this)

  private def indexArns(indexes : Seq[_ <: DynamoIndex]) = indexes.map(_.IndexName).map(indexName => aws"$arn/index/$indexName")
  def localSecondaryIndexArns = indexArns(LocalSecondaryIndexes)
  def globalSecondaryIndexArns = indexArns(GlobalSecondaryIndexes)

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

}

object `AWS::DynamoDB::Table` {
  implicit val format: JsonFormat[`AWS::DynamoDB::Table`] = jsonFormat13(`AWS::DynamoDB::Table`.apply)
}

sealed abstract class StreamViewType(val name : String)
case object NEW_IMAGE extends StreamViewType("NEW_IMAGE")
case object OLD_IMAGE extends StreamViewType("OLD_IMAGE")
case object NEW_AND_OLD_IMAGES extends StreamViewType("NEW_AND_OLD_IMAGES")
case object KEYS_ONLY extends StreamViewType("KEYS_ONLY")

object StreamViewType {
  implicit object format extends JsonFormat[StreamViewType] {
    override def read(json: JsValue): StreamViewType = ???

    override def write(obj: StreamViewType): JsValue = JsString(obj.name)
  }
}

case class StreamSpecification(StreamViewType : StreamViewType)
object StreamSpecification {
  implicit val format : JsonFormat[StreamSpecification] = jsonFormat1(StreamSpecification.apply)
}

sealed trait AttributeType
case object StringAttributeType extends AttributeType
case object NumberAttributeType extends AttributeType
case object BinaryAttributeType extends AttributeType
object AttributeType {
  implicit object format extends JsonFormat[AttributeType] {
    override def write(obj: AttributeType) = JsString(obj match {
      case StringAttributeType => "S"
      case NumberAttributeType => "N"
      case BinaryAttributeType => "B"
    })

    override def read(json: JsValue): AttributeType = ???
  }
}
/**
  * @param AttributeName The name of an attribute. Attribute names can be 1 – 255 characters long and have no character restrictions.
  * @param AttributeType The data type for the attribute. You can specify S for string data, N for numeric data, or B for binary data.
  */
case class AttributeDefinition(
                                AttributeName: String,
                                AttributeType: AttributeType
                              )

object AttributeDefinition {
  implicit val format: JsonFormat[AttributeDefinition] = jsonFormat2(AttributeDefinition.apply)

  implicit def tuple2AttributeDefinition(t : (String, AttributeType)) : AttributeDefinition = AttributeDefinition(
    AttributeName = t._1,
    AttributeType = t._2
  )
}

sealed trait DynamoIndex {
  def IndexName : String
}

sealed trait KeyType

case object HashKeyType extends KeyType

case object RangeKeyType extends KeyType

object KeyType {
  implicit object format extends JsonFormat[KeyType] {
    override def write(obj: KeyType) = JsString(obj match {
      case HashKeyType => "HASH"
      case RangeKeyType => "RANGE"
    })

    override def read(json: JsValue): KeyType = ???
  }
}

case class KeySchema(
                      AttributeName: String,
                      KeyType: KeyType
                    )

object KeySchema {
  implicit val format: JsonFormat[KeySchema] = jsonFormat2(KeySchema.apply)

  implicit def tuple2KeySchema(t : (String, KeyType)) : KeySchema = KeySchema(
    AttributeName = t._1,
    KeyType = t._2
  )
}

case class ProvisionedThroughput(
                                  ReadCapacityUnits: Token[Int],
                                  WriteCapacityUnits: Token[Int]
                                )

object ProvisionedThroughput {
  implicit val format: JsonFormat[ProvisionedThroughput] = jsonFormat2(ProvisionedThroughput.apply)
}

sealed trait Projection

case object AllProjection extends Projection

case object KeysOnlyProjection extends Projection

case class IncludeProjection(keys : Seq[String]) extends Projection

/**
  * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-dynamodb-projectionobject.html
  */
case object Projection {
  implicit object format extends JsonFormat[Projection] {
    override def write(obj: Projection) = obj match {
      case AllProjection => Map("ProjectionType" -> "ALL").toJson
      case KeysOnlyProjection => Map("ProjectionType" -> "KEYS_ONLY").toJson
      case IncludeProjection(keys) => Map(
        "ProjectionType" -> "INCLUDE".toJson,
        "NonKeyAttributes" -> keys.toJson
      ).toJson
    }

    override def read(json: JsValue): Projection = ???
  }
}

case class LocalSecondaryIndex(
                                IndexName: String,
                                KeySchema: Seq[KeySchema],
                                Projection: Projection
                              ) extends DynamoIndex

object LocalSecondaryIndex {
  implicit val format: JsonFormat[LocalSecondaryIndex] = jsonFormat3(LocalSecondaryIndex.apply)
}

case class GlobalSecondaryIndex (
                                  IndexName: String,
                                  KeySchema: Seq[KeySchema],
                                  Projection: Projection,
                                  ProvisionedThroughput: ProvisionedThroughput
                                ) extends DynamoIndex

object GlobalSecondaryIndex {
  implicit val format: JsonFormat[GlobalSecondaryIndex] = jsonFormat4(GlobalSecondaryIndex.apply)
}

case class TimeToLiveSpecification(AttributeName: String, Enabled: Boolean)

object TimeToLiveSpecification {
  implicit val format: RootJsonFormat[TimeToLiveSpecification] = jsonFormat2(TimeToLiveSpecification.apply)
}