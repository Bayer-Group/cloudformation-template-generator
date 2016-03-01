import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.resource._
import org.scalatest.FunSpec
import org.scalatest.Matchers
import spray.json._

/**
 * Created by Ryan Richt on 2/26/15
 */
class ResourceRef_UT extends FunSpec with Matchers {

  describe("ResourceRefs"){

    it("should serialize correctly"){


      val vpcToken = UNSAFEToken[ResourceRef[`AWS::EC2::VPC`]]("vpc-b5f389d0")

      val privateDBSubnet1CidrParam = CidrBlockParameter(
        name        = "PrivateSubnet1",
        Description = Some("CIDR address range for the private subnet to be created in the second AZ"),
        Default     = Some(CidrBlock(10,56,0,0,25))
      )

      val DBPriSubnet1Resource = `AWS::EC2::Subnet`(
        "DBPriSubnet1",
        VpcId = vpcToken,
        AvailabilityZone = Some("us-east-1a"),
        CidrBlock = ParameterRef(privateDBSubnet1CidrParam),
        Tags = Seq[AmazonTag]()
      )

      val DBPriSubnet2Resource = `AWS::EC2::Subnet`(
        "DBPriSubnet2",
        VpcId = vpcToken,
        AvailabilityZone = Some("us-east-1b"),
        CidrBlock = ParameterRef(privateDBSubnet1CidrParam),
        Tags = Seq[AmazonTag]()
      )

      val dbSubnetGroupResource = `AWS::RDS::DBSubnetGroup`(
        name = "DBSubnetGroup",
        SubnetIds = Seq( ResourceRef(DBPriSubnet1Resource), ResourceRef(DBPriSubnet2Resource)),
        DBSubnetGroupDescription =  "DB Subnet Group"
      )

      val expected = JsObject(
        "DBSubnetGroup" -> JsObject(
          "Type" -> JsString("AWS::RDS::DBSubnetGroup"),
          "Properties" -> JsObject(
            "SubnetIds" -> JsArray(
              JsObject("Ref" -> JsString("DBPriSubnet1")),
              JsObject("Ref" -> JsString("DBPriSubnet2"))
            ),
            "DBSubnetGroupDescription" -> JsString("DB Subnet Group")
          )
        )
      )

      Seq[Resource[_]](dbSubnetGroupResource).toJson should be (expected)
    }
  }
}
