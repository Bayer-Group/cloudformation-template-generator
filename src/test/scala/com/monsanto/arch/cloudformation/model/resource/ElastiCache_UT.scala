package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ElastiCache_UT extends FunSpec with Matchers {
  describe("AWS::ElastiCache::SubnetGroup") {
    val vpc = `AWS::EC2::VPC`(
      name = "VPC",
      CidrBlock = CidrBlock(10, 10, 10, 10, 16),
      Tags = Seq()
    )
    val subnetName = "subnet"
    val subnet = `AWS::EC2::Subnet`(
      name = subnetName,
      VpcId = ResourceRef(vpc),
      AvailabilityZone = Some("us-east-1a"),
      CidrBlock = CidrBlock(10, 10, 10, 10, 24),
      Tags = Seq()
    )
    val cacheSubnetGroupName = "cacheSubnetGroup"
    val cacheSubnetGroup = `AWS::ElastiCache::SubnetGroup`(
      name = cacheSubnetGroupName,
      Description = "Not a real group",
      SubnetIds = Seq(ResourceRef(subnet))
    )

    it("should create a valid new ElastiCache subnet group") {
      val expected = JsObject(
        cacheSubnetGroupName -> JsObject(
          "Type" -> JsString("AWS::ElastiCache::SubnetGroup"),
          "Properties" -> JsObject(
            "Description" -> JsString("Not a real group"),
            "SubnetIds" -> JsArray(JsObject("Ref" -> JsString(subnetName)))
          )))
      Seq[Resource[_]](cacheSubnetGroup).toJson should be(expected)
    }
  }

  describe("AWS::ElastiCache::CacheCluster") {
    val vpc = `AWS::EC2::VPC`(
      name = "VPC",
      CidrBlock = CidrBlock(10, 10, 10, 10, 16),
      Tags = Seq()
    )
    val subnetName = "subnet"
    val subnet = `AWS::EC2::Subnet`(
      name = subnetName,
      VpcId = ResourceRef(vpc),
      AvailabilityZone = Some("us-east-1a"),
      CidrBlock = CidrBlock(10, 10, 10, 10, 24),
      Tags = Seq()
    )
    val cacheSubnetGroupName = "cacheSubnetGroup"
    val cacheSubnetGroup = `AWS::ElastiCache::SubnetGroup`(
      name = cacheSubnetGroupName,
      Description = "Not a real group",
      SubnetIds = Seq(ResourceRef(subnet))
    )

    val cacheSgName = "cacheSecurityGroup"
    val cacheSg = `AWS::EC2::SecurityGroup`(
      name = cacheSgName,
      VpcId = vpc,
      GroupDescription = s"Not a real security group",
      SecurityGroupIngress = None,
      Tags = Seq()
    )

    val cacheName = "cache"
    val cache = `AWS::ElastiCache::CacheCluster`(
      name = cacheName,
      ClusterName = Some("fakeCluster"),
      CacheNodeType = "cache.t1.micro",
      CacheSubnetGroupName = Some(cacheSubnetGroup),
      VpcSecurityGroupIds = Some(
        Seq(`Fn::Join`("", Seq(`Fn::GetAtt`(Seq(cacheSg.name, "GroupId")))))
      ),
      Engine = ElastiCacheEngine.redis,
      NumCacheNodes = StringBackedInt(1)
    )

    it("should create a valid new ElastiCache instance") {
      val expected = JsObject(
        cacheName -> JsObject(
          "Type" -> JsString("AWS::ElastiCache::CacheCluster"),
          "Properties" -> JsObject(
            "ClusterName" -> JsString("fakeCluster"),
            "CacheNodeType" -> JsString("cache.t1.micro"),
            "CacheSubnetGroupName" -> JsObject("Ref" -> JsString(cacheSubnetGroupName)),
            "VpcSecurityGroupIds" -> JsArray(
              JsObject("Fn::Join" ->
                JsArray(
                  JsString(""),
                  JsArray(
                    JsObject("Fn::GetAtt" ->
                      JsArray(
                        JsString("cacheSecurityGroup"),
                        JsString("GroupId"))
                  ))))),
            "Engine" -> JsString("redis"),
            "NumCacheNodes" -> JsString("1")
          )
        )
      )
      Seq[Resource[_]](cache).toJson should be(expected)
    }
  }
}
