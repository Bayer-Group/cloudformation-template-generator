package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource._
import org.scalatest.{Matchers, FunSpec}

/**
  * Created by Tyler Southwick on 11/24/15.
  */
class DynamoDBSpec extends FunSpec with Matchers with JsonWritingMatcher {
  it("should generate policy document") {
    val dynamoDbTable = `AWS::DynamoDB::Table`(
      name = "mytable",
      AttributeDefinitions = Seq(
        "name" -> StringAttributeType
      ),
      GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
        IndexName = "globalIndex1",
        KeySchema = Seq(KeySchema(
          AttributeName = "gKey1",
          KeyType = HashKeyType
        )),
        Projection = Projection(
          ProjectionType = AllProjectionType
        ),
        ProvisionedThroughput = ProvisionedThroughput(
          ReadCapacityUnits = 1,
          WriteCapacityUnits = 1
        )
      )),
      KeySchema = Seq(KeySchema(
        AttributeName = "key1",
        KeyType = RangeKeyType
      )),
      LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
        IndexName = "localIndex1",
        KeySchema = Seq(KeySchema(
          AttributeName = "key2",
          KeyType = HashKeyType
        )),
        Projection = Projection(
          NonKeyAttributes = Seq("test1"),
          ProjectionType = IncludeProjectionType
        )
      )),
      ProvisionedThroughput = ProvisionedThroughput(
        ReadCapacityUnits = 1,
        WriteCapacityUnits = 1
      ),
      TableName = "Table1"
    )
    val resource: Resource[`AWS::DynamoDB::Table`] = dynamoDbTable

    resource shouldMatch
      """
        |{
        | "Type": "AWS::DynamoDB::Table",
        | "Properties": {
        | "LocalSecondaryIndexes":[
        |   {
        |     "IndexName":"localIndex1",
        |     "KeySchema":[
        |       {
        |         "AttributeName":"key2",
        |         "KeyType":"HASH"
        |       }
        |     ],
        |     "Projection":{
        |       "NonKeyAttributes":["test1"],
        |       "ProjectionType":"INCLUDE"
        |     }
        |  }],
        | "ProvisionedThroughput":{
        |   "ReadCapacityUnits":1,
        |   "WriteCapacityUnits":1
        | },
        | "AttributeDefinitions":[
        |   {
        |     "AttributeName":"name",
        |     "AttributeType":"S"
        |   }],
        | "GlobalSecondaryIndexes":[
        |   {
        |     "IndexName":"globalIndex1",
        |     "KeySchema":[
        |       {
        |         "AttributeName":"gKey1",
        |         "KeyType":"HASH"
        |       }],
        |     "Projection":{
        |       "ProjectionType":"ALL"
        |     },
        |     "ProvisionedThroughput":{
        |       "ReadCapacityUnits":1,
        |       "WriteCapacityUnits":1
        |     }
        |   }],
        |  "KeySchema":[
        |     {
        |       "AttributeName":"key1",
        |       "KeyType":"RANGE"
        |     }],
        |  "TableName":"Table1"
        |  }
        |}
      """.stripMargin
  }
}
