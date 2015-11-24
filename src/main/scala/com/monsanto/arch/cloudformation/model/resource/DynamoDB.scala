package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json.{JsString, JsValue, JsonFormat, DefaultJsonProtocol}

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

object AttributeDefinition extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AttributeDefinition] = jsonFormat2(AttributeDefinition.apply)

  implicit def tuple2AttributeDefinition(t : (String, AttributeType)) : AttributeDefinition = AttributeDefinition(
    AttributeName = t._1,
    AttributeType = t._2
  )
}

case class GlobalSecondaryIndex(
                                 IndexName: Token[String],
                                 KeySchema: Seq[KeySchema],
                                 Projection: Projection,
                                 ProvisionedThroughput: ProvisionedThroughput
                               )

object GlobalSecondaryIndex extends DefaultJsonProtocol {
  implicit val format: JsonFormat[GlobalSecondaryIndex] = jsonFormat4(GlobalSecondaryIndex.apply)
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

object KeySchema extends DefaultJsonProtocol {
  implicit val format: JsonFormat[KeySchema] = jsonFormat2(KeySchema.apply)
}

case class ProvisionedThroughput(
                                  ReadCapacityUnits: Token[Int],
                                  WriteCapacityUnits: Token[Int]
                                )

object ProvisionedThroughput extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ProvisionedThroughput] = jsonFormat2(ProvisionedThroughput.apply)
}

sealed trait ProjectionType

case object AllProjectionType extends ProjectionType

case object KeysOnlyProjectionType extends ProjectionType

case object IncludeProjectionType extends ProjectionType

case object ProjectionType {
  implicit object format extends JsonFormat[ProjectionType] {
    override def write(obj: ProjectionType) = obj match {
      case AllProjectionType => JsString("ALL")
      case KeysOnlyProjectionType=> JsString("KEYS_ONLY")
      case IncludeProjectionType=> JsString("INCLUDE")
    }

    override def read(json: JsValue): ProjectionType = ???
  }
}

/**
  * http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-dynamodb-projectionobject.html
  */
case class Projection(
                       NonKeyAttributes: Option[Seq[String]] = None,
                       ProjectionType: ProjectionType
                     )

object Projection extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Projection] = jsonFormat2(Projection.apply)
}

case class LocalSecondaryIndex(
                                IndexName: String,
                                KeySchema: Seq[KeySchema],
                                Projection: Projection
                              )

object LocalSecondaryIndex extends DefaultJsonProtocol {
  implicit val format: JsonFormat[LocalSecondaryIndex] = jsonFormat3(LocalSecondaryIndex.apply)
}
