package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.JsonWritingMatcher
import com.monsanto.arch.cloudformation.model.resource.BillingMode.{PAY_PER_REQUEST, PROVISIONED}
import com.monsanto.arch.cloudformation.model.resource.DeletionPolicy.Retain
import org.scalatest.{FunSpec, Matchers}
import spray.json.{JsString, JsonWriter}

/**
  * Created by Tyler Southwick on 11/24/15.
  */
class DynamoDBSpec extends FunSpec with Matchers with JsonWritingMatcher {
  it("should generate policy document with NO Billing Mode with Provisioned Throughput defined") {
    val dynamoDbTable = `AWS::DynamoDB::Table`(
      name = "mytable",
      AttributeDefinitions = Seq(
        "name" -> StringAttributeType
      ),
      GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
        IndexName = "globalIndex1",
        KeySchema = Seq(
          "gKey1" -> HashKeyType
        ),
        Projection = AllProjection,
        ProvisionedThroughput = ProvisionedThroughput(
          ReadCapacityUnits = 1,
          WriteCapacityUnits = 1
        )
      )),
      KeySchema = Seq(
        "key1" -> RangeKeyType
      ),
      LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
        IndexName = "localIndex1",
        KeySchema = Seq(
          "key2" -> HashKeyType
        ),
        Projection = IncludeProjection(Seq("test1"))
      )),
      ProvisionedThroughput = ProvisionedThroughput(
        ReadCapacityUnits = 1,
        WriteCapacityUnits = 1
      ),
      TableName = Some("Table1"),
      DeletionPolicy = Some(Retain),
      DependsOn = Some(Seq("myothertable")),
      TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true)),
      PointInTimeRecoverySpecification = Some(PointInTimeRecoverySpecification(PointInTimeRecoveryEnabled = Some(true))),
      Tags = Some(Seq(AmazonTag(Key = "Key", Value = "Value")))
    )
    val resource: Resource[`AWS::DynamoDB::Table`] = dynamoDbTable

    resource shouldMatch
      """
        |{
        | "Type": "AWS::DynamoDB::Table",
        | "DeletionPolicy" : "Retain",
        | "DependsOn": ["myothertable"],
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
        |  "TableName":"Table1",
        |  "TimeToLiveSpecification": {
        |    "AttributeName": "ttl",
        |    "Enabled": true
        |  },
        |  "PointInTimeRecoverySpecification": {
        |    "PointInTimeRecoveryEnabled": true
        |  },
        |  "Tags": [
        |    {
        |      "Key": "Key",
        |      "Value": "Value"
        |    }
        |  ]
        |  }
        |}
      """.stripMargin
  }
  it("should generate policy document for Billing Mode PROVISIONED with Provisioned Throughput defined") {
    val dynamoDbTable = `AWS::DynamoDB::Table`(
      name = "mytable",
      AttributeDefinitions = Seq(
        "name" -> StringAttributeType
      ),
      BillingMode = Some(PROVISIONED),
      GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
        IndexName = "globalIndex1",
        KeySchema = Seq(
          "gKey1" -> HashKeyType
        ),
        Projection = AllProjection,
        ProvisionedThroughput = ProvisionedThroughput(
          ReadCapacityUnits = 1,
          WriteCapacityUnits = 1
        )
      )),
      KeySchema = Seq(
        "key1" -> RangeKeyType
      ),
      LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
        IndexName = "localIndex1",
        KeySchema = Seq(
          "key2" -> HashKeyType
        ),
        Projection = IncludeProjection(Seq("test1"))
      )),
      ProvisionedThroughput = ProvisionedThroughput(
        ReadCapacityUnits = 1,
        WriteCapacityUnits = 1
      ),
      TableName = Some("Table1"),
      DeletionPolicy = Some(Retain),
      DependsOn = Some(Seq("myothertable")),
      TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true))
    )
    val resource: Resource[`AWS::DynamoDB::Table`] = dynamoDbTable

    resource shouldMatch
      """
        |{
        | "Type": "AWS::DynamoDB::Table",
        | "DeletionPolicy" : "Retain",
        | "DependsOn": ["myothertable"],
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
        | "BillingMode":"PROVISIONED",
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
        |  "TableName":"Table1",
        |  "TimeToLiveSpecification": {
        |    "AttributeName": "ttl",
        |    "Enabled": true
        |  }
        |  }
        |}
      """.stripMargin
  }
  it("should generate policy document for Billing Mode PAY_PER_REQUEST without Provisioned Throughput defined") {
    val dynamoDbTable = `AWS::DynamoDB::Table`(
      name = "mytable",
      AttributeDefinitions = Seq(
        "name" -> StringAttributeType
      ),
      BillingMode = Some(PAY_PER_REQUEST),
      GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
        IndexName = "globalIndex1",
        KeySchema = Seq(
          "gKey1" -> HashKeyType
        ),
        Projection = AllProjection
      )),
      KeySchema = Seq(
        "key1" -> RangeKeyType
      ),
      LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
        IndexName = "localIndex1",
        KeySchema = Seq(
          "key2" -> HashKeyType
        ),
        Projection = IncludeProjection(Seq("test1"))
      )),
      TableName = Some("Table1"),
      DeletionPolicy = Some(Retain),
      DependsOn = Some(Seq("myothertable")),
      TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true))
    )
    val resource: Resource[`AWS::DynamoDB::Table`] = dynamoDbTable

    resource shouldMatch
      """
        |{
        | "Type": "AWS::DynamoDB::Table",
        | "DeletionPolicy" : "Retain",
        | "DependsOn": ["myothertable"],
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
        | "AttributeDefinitions":[
        |   {
        |     "AttributeName":"name",
        |     "AttributeType":"S"
        |   }],
        | "BillingMode":"PAY_PER_REQUEST",
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
        |     }
        |   }],
        |  "KeySchema":[
        |     {
        |       "AttributeName":"key1",
        |       "KeyType":"RANGE"
        |     }],
        |  "TableName":"Table1",
        |  "TimeToLiveSpecification": {
        |    "AttributeName": "ttl",
        |    "Enabled": true
        |  }
        |  }
        |}
      """.stripMargin
  }
  it("should NOT generate policy document if Billing Mode is PAY_PER_REQUEST and has Provisioned Throughput defined") {
    val ex = intercept[IllegalArgumentException] {
      val dynamoDbTable = `AWS::DynamoDB::Table`(
        name = "mytable",
        AttributeDefinitions = Seq(
          "name" -> StringAttributeType
        ),
        BillingMode = Some(PAY_PER_REQUEST),
        GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
          IndexName = "globalIndex1",
          KeySchema = Seq(
            "gKey1" -> HashKeyType
          ),
          Projection = AllProjection,
          ProvisionedThroughput = ProvisionedThroughput(
            ReadCapacityUnits = 1,
            WriteCapacityUnits = 1
          )
        )),
        KeySchema = Seq(
          "key1" -> RangeKeyType
        ),
        LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
          IndexName = "localIndex1",
          KeySchema = Seq(
            "key2" -> HashKeyType
          ),
          Projection = IncludeProjection(Seq("test1"))
        )),
        ProvisionedThroughput = ProvisionedThroughput(
          ReadCapacityUnits = 1,
          WriteCapacityUnits = 1
        ),
        TableName = Some("Table1"),
        DeletionPolicy = Some(Retain),
        DependsOn = Some(Seq("myothertable")),
        TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true))
      )

    }
    assert(ex.getMessage === "requirement failed: Provisioned Throughput is mandatory if Billing mode is NOT provided or PROVISIONED. Also You cannot specify provisioned throughput for PAY_PER_REQUEST billing mode")
  }
  it("should NOT generate policy document if Billing Mode is PAY_PER_REQUEST and has Provisioned Throughput defined in GlobalSecondaryIndexes") {
    val ex = intercept[IllegalArgumentException] {
      val dynamoDbTable = `AWS::DynamoDB::Table`(
        name = "mytable",
        AttributeDefinitions = Seq(
          "name" -> StringAttributeType
        ),
        BillingMode = Some(PAY_PER_REQUEST),
        GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
          IndexName = "globalIndex1",
          KeySchema = Seq(
            "gKey1" -> HashKeyType
          ),
          Projection = AllProjection,
          ProvisionedThroughput = ProvisionedThroughput(
            ReadCapacityUnits = 1,
            WriteCapacityUnits = 1
          )
        )),
        KeySchema = Seq(
          "key1" -> RangeKeyType
        ),
        LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
          IndexName = "localIndex1",
          KeySchema = Seq(
            "key2" -> HashKeyType
          ),
          Projection = IncludeProjection(Seq("test1"))
        )),
        TableName = Some("Table1"),
        DeletionPolicy = Some(Retain),
        DependsOn = Some(Seq("myothertable")),
        TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true))
      )

    }
    assert(ex.getMessage === "requirement failed: Provisioned Throughput is mandatory if Billing mode is NOT provided or PROVISIONED. Also You cannot specify provisioned throughput for PAY_PER_REQUEST billing mode")
  }
  it("should NOT generate policy document if Billing Mode is PROVISIONED and DOES NOT have Provisioned Throughput defined") {
    val ex = intercept[IllegalArgumentException] {
      val dynamoDbTable = `AWS::DynamoDB::Table`(
        name = "mytable",
        AttributeDefinitions = Seq(
          "name" -> StringAttributeType
        ),
        BillingMode = Some(PROVISIONED),
        GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
          IndexName = "globalIndex1",
          KeySchema = Seq(
            "gKey1" -> HashKeyType
          ),
          Projection = AllProjection,
          ProvisionedThroughput = ProvisionedThroughput(
            ReadCapacityUnits = 1,
            WriteCapacityUnits = 1
          )
        )),
        KeySchema = Seq(
          "key1" -> RangeKeyType
        ),
        LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
          IndexName = "localIndex1",
          KeySchema = Seq(
            "key2" -> HashKeyType
          ),
          Projection = IncludeProjection(Seq("test1"))
        )),
        TableName = Some("Table1"),
        DeletionPolicy = Some(Retain),
        DependsOn = Some(Seq("myothertable")),
        TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true))
      )
    }
    assert(ex.getMessage === "requirement failed: Provisioned Throughput is mandatory if Billing mode is NOT provided or PROVISIONED. Also You cannot specify provisioned throughput for PAY_PER_REQUEST billing mode")
  }
  it("should NOT generate policy document if Billing Mode is NOT provided and DOES NOT have Provisioned Throughput defined") {
    val ex = intercept[IllegalArgumentException] {
      val dynamoDbTable = `AWS::DynamoDB::Table`(
        name = "mytable",
        AttributeDefinitions = Seq(
          "name" -> StringAttributeType
        ),
        GlobalSecondaryIndexes = Seq(GlobalSecondaryIndex(
          IndexName = "globalIndex1",
          KeySchema = Seq(
            "gKey1" -> HashKeyType
          ),
          Projection = AllProjection,
          ProvisionedThroughput = ProvisionedThroughput(
            ReadCapacityUnits = 1,
            WriteCapacityUnits = 1
          )
        )),
        KeySchema = Seq(
          "key1" -> RangeKeyType
        ),
        LocalSecondaryIndexes = Seq(LocalSecondaryIndex(
          IndexName = "localIndex1",
          KeySchema = Seq(
            "key2" -> HashKeyType
          ),
          Projection = IncludeProjection(Seq("test1"))
        )),
        TableName = Some("Table1"),
        DeletionPolicy = Some(Retain),
        DependsOn = Some(Seq("myothertable")),
        TimeToLiveSpecification = Some(TimeToLiveSpecification(AttributeName = "ttl", Enabled = true))
      )
    }
    assert(ex.getMessage === "requirement failed: Provisioned Throughput is mandatory if Billing mode is NOT provided or PROVISIONED. Also You cannot specify provisioned throughput for PAY_PER_REQUEST billing mode")
  }
  for {(obj, value) <- Seq[(StreamViewType, String)](
    NEW_IMAGE -> "NEW_IMAGE",
    OLD_IMAGE -> "OLD_IMAGE",
    NEW_AND_OLD_IMAGES -> "NEW_AND_OLD_IMAGES",
    KEYS_ONLY -> "KEYS_ONLY"
  )} {
    it(s"should render $obj as $value") {
      implicitly[JsonWriter[StreamViewType]].write(obj) shouldEqual JsString(value)
    }
  }
}
