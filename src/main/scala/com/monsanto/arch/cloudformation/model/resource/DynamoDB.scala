package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

import scala.language.implicitConversions

/**
  * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-dynamodb-table.html
  * Created by Tyler Southwick on 11/24/15.
  */
case class `AWS::DynamoDB::Table`(
                                   name: String,
                                   AttributeDefinitions: Seq[AttributeDefinition],
                                   GlobalSecondaryIndexes: Seq[GlobalSecondaryIndex],
                                   KeySchema: Seq[KeySchema],
                                   LocalSecondaryIndexes: Seq[LocalSecondaryIndex],
                                   ProvisionedThroughput: ProvisionedThroughput,
                                   TableName: Token[String],
                                   override val Condition: Option[ConditionRef] = None
                                 ) extends Resource[`AWS::DynamoDB::Table`] with HasArn {

  override def arn = aws"arn:aws:dynamodb:${`AWS::Region`}:${`AWS::AccountId`}:table/${ResourceRef(this)}"

  private def indexArns(indexes : Seq[_ <: DynamoIndex]) = indexes.map(_.IndexName).map(indexName => aws"$arn/index/$indexName")
  def localSecondaryIndexArns = indexArns(LocalSecondaryIndexes)
  def globalSecondaryIndexArns = indexArns(GlobalSecondaryIndexes)

  def when(newCondition: Option[ConditionRef] = Condition) = copy(Condition = newCondition)

}

object `AWS::DynamoDB::Table` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::DynamoDB::Table`] = jsonFormat8(`AWS::DynamoDB::Table`.apply)
}

sealed trait AttributeType
case object StringAttributeType extends AttributeType
case object NumberAttributeType extends AttributeType
case object BinaryAttributeType extends AttributeType
object AttributeType extends DefaultJsonProtocol {
  implicit object format extends JsonWriter[AttributeType] {
    override def write(obj: AttributeType) = JsString(obj match {
      case StringAttributeType => "S"
      case NumberAttributeType => "N"
      case BinaryAttributeType => "B"
    })
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

object AttributeDefinition extends DefaultJsonProtocol {
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
  implicit object format extends JsonWriter[KeyType] {
    override def write(obj: KeyType) = JsString(obj match {
      case HashKeyType => "HASH"
      case RangeKeyType => "RANGE"
    })
  }
}

case class KeySchema(
                      AttributeName: String,
                      KeyType: KeyType
                    )

object KeySchema extends DefaultJsonProtocol {
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

object ProvisionedThroughput extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ProvisionedThroughput] = jsonFormat2(ProvisionedThroughput.apply)
}

sealed trait Projection

case object AllProjection extends Projection

case object KeysOnlyProjection extends Projection

case class IncludeProjection(keys : Seq[String]) extends Projection

/**
  * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-dynamodb-projectionobject.html
  */
case object Projection extends DefaultJsonProtocol {
  implicit object format extends JsonWriter[Projection] {
    override def write(obj: Projection) = obj match {
      case AllProjection => Map("ProjectionType" -> "ALL").toJson
      case KeysOnlyProjection => Map("ProjectionType" -> "KEYS_ONLY").toJson
      case IncludeProjection(keys) => Map(
        "ProjectionType" -> "INCLUDE".toJson,
        "NonKeyAttributes" -> keys.toJson
      ).toJson
    }
  }
}

case class LocalSecondaryIndex(
                                IndexName: String,
                                KeySchema: Seq[KeySchema],
                                Projection: Projection
                              ) extends DynamoIndex

object LocalSecondaryIndex extends DefaultJsonProtocol {
  implicit val format: JsonFormat[LocalSecondaryIndex] = jsonFormat3(LocalSecondaryIndex.apply)
}

case class GlobalSecondaryIndex (
                                  IndexName: String,
                                  KeySchema: Seq[KeySchema],
                                  Projection: Projection,
                                  ProvisionedThroughput: ProvisionedThroughput
                                ) extends DynamoIndex

object GlobalSecondaryIndex extends DefaultJsonProtocol {
  implicit val format: JsonFormat[GlobalSecondaryIndex] = jsonFormat4(GlobalSecondaryIndex.apply)
}
