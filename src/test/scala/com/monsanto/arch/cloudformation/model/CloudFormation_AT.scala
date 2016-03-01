package com.monsanto.arch.cloudformation.model

import java.io.{PrintWriter, File}

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.resource._
import com.monsanto.arch.cloudformation.model.simple.Builders._
import org.scalatest._
import spray.json._

/**
 * Created by Ryan Richt on 1/18/15
 */


class CloudFormation_AT extends FunSpec with Matchers {

  describe("The condensation generator") {

    val handGenerated = new File(getClass.getResource("/cloudformation-template-vpc-example.json").toURI)
    val handContents = io.Source.fromFile(handGenerated).getLines().mkString("\n")
    val generatedContents = StaxTemplate.itsaDockerStack.toJson

    //print out the file to make it easier to debug
    val newTemplate = new File("src/test/resources/cloudformation-template-vpc-example.json.generated")
    val tWriter = new PrintWriter(newTemplate)
    tWriter.print(generatedContents.prettyPrint)
    tWriter.close()


    it("Should correctly recapitulate our template's AWSTemplateFormatVersion") {
          generatedContents.asJsObject.fields("AWSTemplateFormatVersion") should be(handContents.parseJson.asJsObject.fields("AWSTemplateFormatVersion"))
        }

        it("Should correctly recapitulate our template's Description") {
          generatedContents.asJsObject.fields("Description") should be(handContents.parseJson.asJsObject.fields("Description"))
        }

        it("Should correctly recapitulate our template's Parameters") {
          generatedContents.asJsObject.fields("Parameters") should be(handContents.parseJson.asJsObject.fields("Parameters"))
        }

        it("Should correctly recapitulate our template's Mappings") {
          generatedContents.asJsObject.fields("Mappings") should be(handContents.parseJson.asJsObject.fields("Mappings"))
        }

        it("Should correctly recapitulate our template's Resources") {
          generatedContents.asJsObject.fields("Resources") should be(handContents.parseJson.asJsObject.fields("Resources"))
        }

        it("Should correctly recapitulate our template's Outputs") {
          generatedContents.asJsObject.fields("Outputs") should be (handContents.parseJson.asJsObject.fields("Outputs"))
        }
  }
}

object StaxTemplate {

  def standardTagsNoNetworkPropagate(resourceName: String) = standardTagsNoNetwork(resourceName).map(_.copy(PropagateAtLaunch = Some(true)))

  def standardTags(resourceName: String, network: String) = AmazonTag("Network", network) +: standardTagsNoNetwork(resourceName)

  val vpcCidrParam = CidrBlockParameter(
    name        = "VpcCidr",
    Description = Some("CIDR address range for the VPC to be created"),
    Default     = Some(CidrBlock(10,183,0,0,16))
  )

  val allowSSHFromParam = CidrBlockParameter(
    name        = "AllowSSHFrom",
    Description = Some("The net block (CIDR) that SSH is available to."),
    Default     = Some(CidrBlock(0,0,0,0,0))
  )

  val allowHTTPFromParam = CidrBlockParameter(
    name        = "AllowHTTPFrom",
    Description = Some("The net block (CIDR) that can connect to the ELB."),
    Default     = Some(CidrBlock(0,0,0,0,0))
  )


  private val coreOSAlphaAMIMapping = Mapping[AMIId](
    "CoreOSAlphaAMI",
    Map(
      "us-east-1" -> Map("AMI" -> AMIId("ami-52396c3a")),
      "us-west-1" -> Map("AMI" -> AMIId("ami-240f1561")),
      "us-west-2" -> Map("AMI" -> AMIId("ami-6d85a15d")),
      "eu-west-1" -> Map("AMI" -> AMIId("ami-f56ee482"))
    )
  )

  private val coreOSBetaAMIMapping = Mapping[AMIId](
    "CoreOSBetaAMI",
    Map(
      "us-east-1" -> Map("AMI" -> AMIId("ami-509bd838")),
      "us-west-1" -> Map("AMI" -> AMIId("ami-4ab7af0f")),
      "us-west-2" -> Map("AMI" -> AMIId("ami-07762d37")),
      "eu-west-1" -> Map("AMI" -> AMIId("ami-19af216e"))
    )
  )

  private val coreOSStableAMIMapping = Mapping[AMIId](
    "CoreOSStableAMI",
    Map(
      "us-east-1" -> Map("AMI" -> AMIId("ami-8297d4ea")),
      "us-west-1" -> Map("AMI" -> AMIId("ami-24b5ad61")),
      "us-west-2" -> Map("AMI" -> AMIId("ami-f1702bc1")),
      "eu-west-1" -> Map("AMI" -> AMIId("ami-5d911f2a"))
    )
  )

  private val amazonLinuxAMIMapping = Mapping[AMIId](
    "AmazonLinuxAMI",
    Map(
      "us-east-1" -> Map("AMI" -> AMIId("ami-146e2a7c")),
      "us-west-1" -> Map("AMI" -> AMIId("ami-42908907")),
      "us-west-2" -> Map("AMI" -> AMIId("ami-dfc39aef")),
      "eu-west-1" -> Map("AMI" -> AMIId("ami-9d23aeea"))
    )
  )

  private val awsNATAMIMapping = Mapping[AMIId](
    "AWSNATAMI",
    Map(
      "us-east-1" -> Map("AMI" -> AMIId("ami-184dc970")),
      "us-west-1" -> Map("AMI" -> AMIId("ami-a98396ec")),
      "us-west-2" -> Map("AMI" -> AMIId("ami-290f4119")),
      "eu-west-1" -> Map("AMI" -> AMIId("ami-14913f63"))
    )
  )

  val coreOSChannelAMIParameter = AMIIdParameter(
    name                  = "CoreOSChannelAMI",
    Description           = Some("MapName for the update channel AMI to use when launching CoreOS instances"),
    AllowedValues         = Some(Seq("CoreOSStableAMI","CoreOSBetaAMI","CoreOSAlphaAMI")),
    ConstraintDescription = Some("Value should be 'CoreOSStableAMI', 'CoreOSBetaAMI', or 'CoreOSAlphaAMI'"),
    Default               = Some(UNSAFEToken[MappingRef[AMIId]]("CoreOSStableAMI"))
  )

  private val appParam = StringParameter(
    name                  = "App",
    Description           = Some("Name for this ecosystem of services"),
    MinLength             = Some(StringBackedInt(1)),
    MaxLength             = Some(StringBackedInt(64)),
    AllowedPattern        = Some("[-_ a-zA-Z0-9]*"),
    ConstraintDescription = Some("Can contain only alphanumeric characters, spaces, dashes and underscores."),
    Default               = Some("REPLACE APP")
  )

  private val groupParam = StringParameter(
    name                  = "Group",
    Description           = Some("Group responsible for this ecosystem of services"),
    MinLength             = Some(StringBackedInt(1)),
    MaxLength             = Some(StringBackedInt(64)),
    AllowedPattern        = Some("[-_ a-zA-Z0-9]*"),
    ConstraintDescription = Some("Can contain only alphanumeric characters, spaces, dashes and underscores."),
    Default               = Some("REPLACE GROUP")
  )

  private val serviceDomainParam = StringParameter(
    name        = "ServiceDomain",
    Description = Some("Domain to register for services"),
    MinLength   = Some(StringBackedInt(1)),
    MaxLength   = Some(StringBackedInt(64)),
    Default     = Some("REPLACE DOMAIN")
  )

  private val ownerParam = StringParameter(
    name                  = "Owner",
    Description           = Some("Individual responsible for this ecosystem of services"),
    MinLength             = Some(StringBackedInt(1)),
    MaxLength             = Some(StringBackedInt(64)),
    AllowedPattern        = Some("[-_ a-zA-Z0-9]*"),
    ConstraintDescription = Some("Can contain only alphanumeric characters, spaces, dashes and underscores."),
    Default               = Some("REPLACE OWNER")
  )

  private val environmentParam = StringParameter(
    name                  = "Environment",
    Description           = Some("Description of deployment environment, e. g., test or production"),
    MinLength             = Some(StringBackedInt(1)),
    MaxLength             = Some(StringBackedInt(64)),
    AllowedPattern        = Some("[-_ a-zA-Z0-9]*"),
    ConstraintDescription = Some("Can contain only alphanumeric characters, spaces, dashes and underscores."),
    Default               = Some("test")
  )

  private val keepAliveParam = StringParameter(
    name                  = "KeepAlive",
    Description           = Some("Boolean to indicate whether to allow resource to be kept alive during nightly reaping"),
    MinLength             = Some(StringBackedInt(4)),
    MaxLength             = Some(StringBackedInt(5)),
    AllowedValues         = Some(Seq("true", "false")),
    ConstraintDescription = Some("Value should be 'true' or 'false'"),
    Default               = Some("false")
  )

  private val costCenterParam = StringParameter(
    name                  = "CostCenter",
    Description           = Some("Cost center to be charged for this ecosystem of services"),
    MinLength             = Some(StringBackedInt(18)),
    MaxLength             = Some(StringBackedInt(18)),
    AllowedPattern        = Some("\\d{4}-\\d{4}-[A-Z]{3}\\d{5}"),
    ConstraintDescription = Some("Format for cost center is ####-####-XYZ#####"),
    Default               = Some("0000-0000-ABC00000")
  )

  private val dockerRegistryUrlParam = StringParameter(
    name        = "DockerRegistryUrl",
    Description = Some("URL for private Docker Registry"),
    MinLength   = Some(StringBackedInt(8)),
    MaxLength   = Some(StringBackedInt(200)),
    Default     = Some("https://index.docker.io/v1/")
  )

  private val dockerRegistryUserParam = StringParameter(
    name        = "DockerRegistryUser",
    Description = Some("User name for private Docker Registry"),
    MinLength   = Some(StringBackedInt(1)),
    MaxLength   = Some(StringBackedInt(60)),
    Default     = Some("nobody")
  )

  private val dockerRegistryPassParam = StringParameter(
    name        = "DockerRegistryPass",
    Description = Some("Password for private Docker Registry"),
    MinLength   = Some(StringBackedInt(1)),
    MaxLength   = Some(StringBackedInt(60)),
    Default     = Some("null")
  )

  private val dockerRegistryEmailParam = StringParameter(
    name        = "DockerRegistryEmail",
    Description = Some("Email address for private Docker Registry"),
    MinLength   = Some(StringBackedInt(1)),
    MaxLength   = Some(StringBackedInt(60)),
    Default     = Some("nobody@null.com")
  )

  private val keyNameParam = `AWS::EC2::KeyPair::KeyName_Parameter`(
    name                  = "KeyName",
    Description           = Some("Name of an existing EC2 KeyPair to enable SSH access to the instances"),
    ConstraintDescription = Some("Value must be a valid AWS key pair name in your account.")
  )

  private val jumpInstanceTypeParam = StringParameter(
    name                  = "JumpInstanceType",
    Description           = "Instance type for public subnet jump nodes",
    AllowedValues         = Seq("m3.medium", "m3.large", "m3.xlarge", "m3.2xlarge", "c3.large","c3.xlarge", "c3.2xlarge", "c3.4xlarge","c3.8xlarge", "cc2.8xlarge","cr1.8xlarge","hi1.4xlarge", "hs1.8xlarge", "i2.xlarge", "i2.2xlarge", "i2.4xlarge", "i2.8xlarge", "r3.large", "r3.xlarge", "r3.2xlarge","r3.4xlarge", "r3.8xlarge", "t2.micro", "t2.small", "t2.medium"),
    ConstraintDescription = "Must be a valid EC2 instance type.",
    Default               = "t2.micro"
  )

  private val natInstanceTypeParam = StringParameter(
    name                  = "NATInstanceType",
    Description           = "Instance type for public subnet NAT nodes",
    AllowedValues         = Seq("m3.medium", "m3.large", "m3.xlarge", "m3.2xlarge", "c3.large","c3.xlarge", "c3.2xlarge", "c3.4xlarge","c3.8xlarge", "cc2.8xlarge","cr1.8xlarge","hi1.4xlarge", "hs1.8xlarge", "i2.xlarge", "i2.2xlarge", "i2.4xlarge", "i2.8xlarge", "r3.large", "r3.xlarge", "r3.2xlarge","r3.4xlarge", "r3.8xlarge", "t2.micro", "t2.small", "t2.medium"),
    ConstraintDescription = "Must be a valid EC2 instance type.",
    Default               = "t2.micro"
  )

  private val numberOfPingsParam = StringParameter(
    name        = "NumberOfPings",
    Description = "The number of times the health check will ping the alternate NAT node",
    Default     = "3"
  )

  private val pingTimeoutParam = StringParameter(
    name        = "PingTimeout",
    Description = "The number of seconds to wait for each ping response before determining that the ping has failed",
    Default     = "10"
  )

  private val waitBetweenPingsParam = StringParameter(
    name        = "WaitBetweenPings",
    Description = "The number of seconds to wait between health checks",
    Default     = "2"
  )

  private val waitForInstanceStopParam = StringParameter(
    name        = "WaitForInstanceStop",
    Description = "The number of seconds to wait for alternate NAT Node to stop before attempting to stop it again",
    Default     = "60"
  )

  private val waitForInstanceStartParam = StringParameter(
    name        = "WaitForInstanceStart",
    Description = "The number of seconds to wait for alternate NAT node to restart before resuming health checks again",
    Default     = "300"
  )

  private val dockerInstanceTypeParam = StringParameter(
    name                  = "DockerInstanceType",
    Description           = "EC2 instance type for the Docker autoscaling group",
    AllowedValues         = Seq("m3.medium", "m3.large", "m3.xlarge", "m3.2xlarge", "c3.large","c3.xlarge", "c3.2xlarge", "c3.4xlarge","c3.8xlarge", "cc2.8xlarge","cr1.8xlarge","hi1.4xlarge", "hs1.8xlarge", "i2.xlarge", "i2.2xlarge", "i2.4xlarge", "i2.8xlarge", "r3.large", "r3.xlarge", "r3.2xlarge","r3.4xlarge", "r3.8xlarge", "t2.micro", "t2.small", "t2.medium"),
    ConstraintDescription = "Must be a valid EC2 HVM instance type.",
    Default               = "m3.medium"
  )

  private val clusterSizeParam = NumberParameter(
    name        = "ClusterSize",
    Description = Some("Number of nodes in cluster (2-12)"),
    MinValue    = Some(StringBackedInt(3)),
    MaxValue    = Some(StringBackedInt(12)),
    Default     = Some(StringBackedInt(3))
  )

  private val routerClusterSizeParam = NumberParameter(
    name        = "RouterClusterSize",
    Description = Some("Number of nodes in cluster (2-12)"),
    MinValue    = Some(StringBackedInt(2)),
    MaxValue    = Some(StringBackedInt(12)),
    Default     = Some(StringBackedInt(2))
  )

  private val autoScaleCoolDownParam = NumberParameter(
    name        = "AutoScaleCooldown",
    Description = Some("Time in seconds between autoscaling events"),
    MinValue    = Some(StringBackedInt(60)),
    MaxValue    = Some(StringBackedInt(3600)),
    Default     = Some(StringBackedInt(300))
  )

  private val discoveryURLParam = StringParameter(
    name        = "DiscoveryURL",
    Description = "An unique etcd cluster discovery URL. Grab a new token from https://discovery.etcd.io/new"
  )

  private val advertizedIPAddressParam = StringParameter(
    name          = "AdvertisedIPAddress",
    Description   = "Use 'private' if your etcd cluster is within one region or 'public' if it spans regions or cloud providers.",
    AllowedValues = Seq("private","public"),
    Default       = "private"
  )

  val vpcResource = `AWS::EC2::VPC`(
    "VPC",
    CidrBlock = ParameterRef(vpcCidrParam),
    Tags = standardTags("vpc", "Public"),
    true,
    true
  )

  val natRoleResource = `AWS::IAM::Role`("NATRole",
    AssumeRolePolicyDocument =
      PolicyDocument(
        Statement = Seq(
          PolicyStatement(
            Effect = "Allow",
            Principal = Some(DefinedPrincipal(Map("Service" -> Seq("ec2.amazonaws.com")))),
            Action = Seq("sts:AssumeRole")
          )
        )
      ),
    Path = Some("/"),
    Policies = Some(Seq(
      Policy(
        PolicyName = "NAT_Takeover",
        PolicyDocument =
          PolicyDocument(
            Statement = Seq(
              PolicyStatement(
                Effect = "Allow",
                Principal = None, // Did this ever work?
                Action = Seq("ec2:DescribeInstances", "ec2:DescribeRouteTables", "ec2:CreateRoute", "ec2:ReplaceRoute", "ec2:StartInstances", "ec2:StopInstances"),
                Resource = Some("*")
              )
            )
          )
      ),
      Policy(
        PolicyName = "StaxS3Access",
        PolicyDocument =
          PolicyDocument(
            Statement = Seq(
              PolicyStatement(
                Effect = "Allow",
                Action = Seq("s3:GetObject"),
                Resource = Some(`Fn::Join`("", Seq("arn:aws:s3:::", `AWS::StackName`, "/*"))),
                Principal = None // Did this ever work?
              )
            )
          )
      )
    ))
  )

  val azParam = StringListParameter(
    name = "AvailabilityZones",
    Description = Some("A comma separated list of availability zones"),
    Default = Some(Seq("us-east-1a", "us-east-1b"))
  )

  val publicSubnet1Param = CidrBlockParameter(
    name        = "PublicSubnet1",
    Description = Some("CIDR address range for the public subnet to be created in the first AZ"),
    Default     = Some(CidrBlock(10,183,1,0,24))
  )

  val publicSubnet2Param = CidrBlockParameter(
    name        = "PublicSubnet2",
    Description = Some("CIDR address range for the public subnet to be created in the second AZ"),
    Default     = Some(CidrBlock(10,183,3,0,24))
  )

  val privateSubnetsParam = CidrBlockListParameter(
      name        = "PrivateSubnets",
      Description = Some("A comma separated list of CIDR blocks to be used for private subnet creation"),
      Default     = Some(Seq(CidrBlock(10,183,0,0,24), CidrBlock(10,183,2,0,24)))
  )

  val internetGatewayResource = `AWS::EC2::InternetGateway`(
    "InternetGateway",
    Tags = standardTags("igw", "Public")
  )

  val gatewayAttachmentResource = `AWS::EC2::VPCGatewayAttachment`(
    "GatewayToInternet",
    VpcId = ResourceRef(vpcResource),
    gatewayId = ResourceRef(internetGatewayResource)
  )

  val publicRouteTableResource = `AWS::EC2::RouteTable`(
    "PublicRouteTable1",
    VpcId = ResourceRef(vpcResource),
    Tags = standardTags("pubrt1", "Public")
  )

  val privateRouteTable1Resource = `AWS::EC2::RouteTable`(
    "PrivateRouteTable1",
    VpcId = ResourceRef(vpcResource),
    Tags = standardTags("privrt1", "Private")
  )

  val privateRouteTable2Resource = `AWS::EC2::RouteTable`(
    "PrivateRouteTable2",
    VpcId = ResourceRef(vpcResource),
    Tags = standardTags("privrt2", "Private")
  )

  val routerELBSecGroupResource = `AWS::EC2::SecurityGroup`(
    "RouterELBSecurityGroup",
    GroupDescription = "Rules for allowing access to/from service router ELB",
    VpcId = ResourceRef(vpcResource),
    SecurityGroupEgress = None,
    SecurityGroupIngress = Some(Seq(
      CidrIngressSpec(
        IpProtocol = "tcp",
        CidrIp = ParameterRef(allowHTTPFromParam),
        FromPort = "80",
        ToPort = "80"
      ),
      CidrIngressSpec(
        IpProtocol = "tcp",
        CidrIp = ParameterRef(allowHTTPFromParam),
        FromPort = "443",
        ToPort = "443"
      )
    )),
    Tags = standardTagsNoNetwork("router-elbsg")
  )

  val routerCoreOSSecGroupResource = `AWS::EC2::SecurityGroup`(
    "RouterCoreOSSecurityGroup",
    GroupDescription = "Router CoreOS SecurityGroup",
    VpcId = ResourceRef(vpcResource),
    SecurityGroupIngress = Some(Seq(
      SGIngressSpec(
        IpProtocol = "tcp",
        SourceSecurityGroupId = ResourceRef(routerELBSecGroupResource),
        FromPort = "80",
        ToPort = "80"
      ),
      SGIngressSpec(
        IpProtocol = "tcp",
        SourceSecurityGroupId = ResourceRef(routerELBSecGroupResource),
        FromPort = "4001",
        ToPort = "4001"
      )
    )),
    SecurityGroupEgress = None,
    Tags = standardTagsNoNetwork("routersg")
  )

  val jumpSecGroupResource = `AWS::EC2::SecurityGroup`(
    "JumpSecurityGroup",
    GroupDescription = "Rules for allowing access to public subnet nodes",
    VpcId = ResourceRef(vpcResource),
    SecurityGroupEgress = None,
    SecurityGroupIngress =
      Some(Seq(
        CidrIngressSpec(
          IpProtocol = "tcp",
          CidrIp = ParameterRef(allowSSHFromParam),
          FromPort = "22",
          ToPort = "22"
        )
      )),
    Tags = standardTagsNoNetwork("jumpsg")
  )

  val natSecGroupResource = `AWS::EC2::SecurityGroup`(
    "NATSecurityGroup",
    GroupDescription = "Rules for allowing access to public subnet nodes",
    VpcId = ResourceRef(vpcResource),
    SecurityGroupIngress = Some(Seq(
      SGIngressSpec(
        IpProtocol = "tcp",
        SourceSecurityGroupId = ResourceRef(jumpSecGroupResource),
        FromPort = "22",
        ToPort = "22"
      ),
      CidrIngressSpec(
        IpProtocol = "-1",
        CidrIp = ParameterRef(vpcCidrParam),
        FromPort = "0",
        ToPort = "65535"
      )
    )),
    SecurityGroupEgress = Some(Seq(
      CidrEgressSpec(
        IpProtocol = "-1",
        CidrIp = CidrBlock(0,0,0,0,0),
        FromPort = "0",
        ToPort = "65535"
      )
    )),
    Tags = standardTagsNoNetwork("natsg")
  )

  val coreOSFromJumpSecGroupResource = `AWS::EC2::SecurityGroup`(
    "CoreOSFromJumpSecurityGroup",
    GroupDescription = "Allow general CoreOS/Docker access from the jump box",
    VpcId = ResourceRef(vpcResource),
    SecurityGroupEgress = None,
    SecurityGroupIngress = Some(Seq(
      SGIngressSpec(
        IpProtocol = "tcp",
        SourceSecurityGroupId = ResourceRef(jumpSecGroupResource),
        FromPort = "22",
        ToPort = "22"
      ),
      SGIngressSpec(
        IpProtocol = "tcp",
        SourceSecurityGroupId = ResourceRef(jumpSecGroupResource),
        FromPort = "80",
        ToPort = "80"
      ),
      SGIngressSpec(
        IpProtocol = "tcp",
        SourceSecurityGroupId = ResourceRef(jumpSecGroupResource),
        FromPort = "4001",
        ToPort = "4001"
      )
    )),
    Tags = standardTagsNoNetwork("coreos-from-jumpsg")
  )

  val coreOSSecGroupResource = `AWS::EC2::SecurityGroup`(
    "CoreOSSecurityGroup",
    GroupDescription = "Security Group for microservices CoreOS Auto Scaling Group",
    VpcId = ResourceRef(vpcResource),
    SecurityGroupIngress = Some(Seq(
      SGIngressSpec(
        IpProtocol = "-1",
        SourceSecurityGroupId = ResourceRef(routerCoreOSSecGroupResource),
        FromPort = "0",
        ToPort = "65535"
      )
    )
    ),
    SecurityGroupEgress = None,
    Tags = standardTagsNoNetwork("containersg")
  )

  val pubSubnet1 = `AWS::EC2::Subnet`(
    "PubSubnet1",
    VpcId = ResourceRef(vpcResource),
    AvailabilityZone = Some("us-east-1a"),
    CidrBlock = ParameterRef(publicSubnet1Param),
    MapPublicIpOnLaunch = Some(true),
    Tags = standardTags("pubsubnet1", "Public")
  )

  val priSubnet1 = `AWS::EC2::Subnet`(
    "PriSubnet1",
    VpcId = ResourceRef(vpcResource),
    AvailabilityZone = Some("us-east-1a"),
    CidrBlock = `Fn::Select`(StringBackedInt(0), ParameterRef(privateSubnetsParam)),
    Tags = standardTags("prisubnet1", "Private")
  )

  val pubSubnet2 = `AWS::EC2::Subnet`(
    "PubSubnet2",
    VpcId = ResourceRef(vpcResource),
    AvailabilityZone = Some("us-east-1b"),
    CidrBlock = ParameterRef(publicSubnet2Param),
    Tags = standardTags("pubsubnet2", "Public")
  )

  val priSubnet2 = `AWS::EC2::Subnet`(
    "PriSubnet2",
    VpcId = ResourceRef(vpcResource),
    AvailabilityZone = Some("us-east-1b"),
    CidrBlock = `Fn::Select`(StringBackedInt(1), ParameterRef(privateSubnetsParam)),
    Tags = standardTags("prisubnet2", "Private")
  )

  private val natRoleProfileResource = `AWS::IAM::InstanceProfile`(
    "NATRoleProfile",
    Path = "/",
    Roles = Seq(ResourceRef(natRoleResource))
  )

  private def natInstance(number: Int, subnet: `AWS::EC2::Subnet`, orderedRouteTables: Seq[`AWS::EC2::RouteTable`]) =
    `AWS::EC2::Instance`(
      "NAT" + number + "Instance",
      Metadata = Some(Map("Comment1" -> ("Create NAT #" + number))),
      InstanceType = ParameterRef(natInstanceTypeParam),
      KeyName = ParameterRef(keyNameParam),
      IamInstanceProfile = Some(ResourceRef(natRoleProfileResource)),
      SubnetId = ResourceRef(subnet),
      SourceDestCheck = Some("false"),
      ImageId = `Fn::FindInMap`[AMIId](MappingRef(awsNATAMIMapping), `AWS::Region`, "AMI"),
      SecurityGroupIds = Seq( ResourceRef(natSecGroupResource) ),
      Tags = standardTagsNoNetwork("nat" + number),
      DisableApiTermination = Some("false"),
      PrivateIpAddress = Some(IPAddress(1, 2, 3, 4)),
      UserData = Some(`Fn::Base64`(
        `Fn::Join`("",
          Seq[Token[String]](
            "#!/bin/bash -v\n",
            "yum update -y aws*\n",
            ". /etc/profile.d/aws-apitools-common.sh\n",
            "cd /root\n",
            "aws s3 cp s3://",
            `AWS::StackName`,
            "/user-data-nat.sh user-data-nat.sh\n",
            "/bin/bash user-data-nat.sh",
            " ") ++

          orderedRouteTables.map(rt => ResourceRef(rt)).foldLeft( Seq[Token[String]]() ){ (acc, e) =>
            if(acc.isEmpty) acc :+ e else acc ++ Seq[Token[String]](" ", e)
          } ++

          Seq[Token[String]](" ", `AWS::Region`,
            " ", `AWS::StackName`, "\n",
            "# EOF\n"
          )
        )
      ))
    )

  private val nat1InstanceResource = natInstance(1, pubSubnet1, Seq(privateRouteTable2Resource, privateRouteTable1Resource))
  private val nat2InstanceResource = natInstance(2, pubSubnet2, Seq(privateRouteTable1Resource, privateRouteTable2Resource))

  private val jumpInstanceResource = `AWS::EC2::Instance`(
    "JumpInstance",
    InstanceType = ParameterRef(jumpInstanceTypeParam),
    KeyName = ParameterRef(keyNameParam),
    SubnetId = ResourceRef(pubSubnet1),
    ImageId = `Fn::FindInMap`[AMIId](MappingRef(amazonLinuxAMIMapping), `AWS::Region`, "AMI"),
    SecurityGroupIds = Seq( ResourceRef(jumpSecGroupResource)),
    Tags = standardTagsNoNetwork("jump")
  )

  private val routerCoreOSServerLaunchConfigResource = `AWS::AutoScaling::LaunchConfiguration`(
    "RouterCoreOSServerLaunchConfig",
    ImageId = `Fn::FindInMap`[AMIId](ParameterRef(coreOSChannelAMIParameter), `AWS::Region`, "AMI"),
    InstanceType = ParameterRef(dockerInstanceTypeParam),
    KeyName = ParameterRef(keyNameParam),
    SecurityGroups = Seq(ResourceRef(routerCoreOSSecGroupResource), ResourceRef(coreOSFromJumpSecGroupResource)),
    IamInstanceProfile = None,
    UserData = `Fn::Base64`(
      `Fn::Join`(
        "",
        Seq(
          "#cloud-config\n\n",
          "coreos:\n",
          "  etcd:\n",
          "    discovery: ", ParameterRef(discoveryURLParam), "\n",
          "    addr: $", ParameterRef(advertizedIPAddressParam), "_ipv4:4001\n",
          "    peer-addr: $", ParameterRef(advertizedIPAddressParam), "_ipv4:7001\n",
          "  units:\n",
          "    - name: etcd.service\n",
          "      command: start\n",
          "    - name: consul.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Consul Agent\n",
          "        After=docker.service\n",
          "        After=etcd.service\n",
          "        [Service]\n",
          "        Restart=on-failure\n",
          "        RestartSec=240\n",
          "        ExecStartPre=-/usr/bin/docker kill consul\n",
          "        ExecStartPre=-/usr/bin/docker rm consul\n",
          "        ExecStartPre=/usr/bin/docker pull progrium/consul\n",
          "        ExecStart=/usr/bin/docker run -h %H --name consul -p 8300:8300 -p 8301:8301 -p 8301:8301/udp -p 8302:8302 -p 8302:8302/udp -p 8400:8400 -p 8500:8500 -p 53:53/udp -e SERVICE_IGNORE=true progrium/consul -advertise $", ParameterRef(advertizedIPAddressParam), "_ipv4\n",
          "        ExecStop=/usr/bin/docker stop consul\n",
          "    - name: consul-announce.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Consul Server Announcer\n",
          "        PartOf=consul.service\n",
          "        After=consul.service\n",
          "        [Service]\n",
          "        ExecStart=/bin/sh -c \"while true; do etcdctl set /consul/bootstrap/machines/$(cat /etc/machine-id) $", ParameterRef(advertizedIPAddressParam), "_ipv4 --ttl 60; /usr/bin/docker exec consul consul join $(etcdctl ls /consul/bootstrap/machines | xargs -n 1 etcdctl get | tr '\\n' ' '); sleep 45; done\"\n",
          "        ExecStop=/bin/sh -c \"/usr/bin/etcdctl rm /consul/bootstrap/machines/$(cat /etc/machine-id)\"\n",
          "    - name: docker-login.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Log in to private Docker Registry\n",
          "        After=docker.service\n",
          "        [Service]\n",
          "        Type=oneshot\n",
          "        RemainAfterExit=yes\n",
          "        ExecStart=/usr/bin/docker login -e ", ParameterRef(dockerRegistryEmailParam), " -u ", ParameterRef(dockerRegistryUserParam), " -p ", ParameterRef(dockerRegistryPassParam), " ", ParameterRef(dockerRegistryUrlParam), "\n",
          "        ExecStop=/usr/bin/docker logout ", ParameterRef(dockerRegistryUrlParam), "\n",
          "    - name: axon-router.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Run axon-router\n",
          "        After=docker.service\n",
          "        Requires=docker.service\n\n",
          "        [Service]\n",
          "        Restart=always\n",
          "        ExecStartPre=-/usr/bin/docker kill axon-router\n",
          "        ExecStartPre=-/usr/bin/docker rm axon-router\n",
          "        ExecStartPre=/usr/bin/docker pull monsantoco/axon-router:latest\n",
          "        ExecStart=/usr/bin/docker run -t -e \"NS_IP=172.17.42.1\" --name axon-router -p 80:80 monsantoco/axon-router:latest\n",
          "        ExecStop=/usr/bin/docker stop axon-router\n",
          "    - name: settimezone.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Set the timezone\n",
          "        [Service]\n",
          "        ExecStart=/usr/bin/timedatectl set-timezone UTC\n",
          "        RemainAfterExit=yes\n",
          "        Type=oneshot\n",
          "write_files:\n",
          "  - path: /etc/ntp.conf\n",
          "    content: |\n",
          "      server 0.pool.ntp.org\n",
          "      server 1.pool.ntp.org\n",
          "      server 2.pool.ntp.org\n",
          "      server 3.pool.ntp.org\n",
          "      restrict default nomodify nopeer noquery limited kod\n",
          "      restrict 127.0.0.1\n"
        )
      )
    )
  )

  private val routerELBResource = `AWS::ElasticLoadBalancing::LoadBalancer`.inVpc(
    "RouterELB",
    Listeners = Seq(
      ELBListener(
        LoadBalancerPort = "80",
        InstancePort = "80",
        Protocol = ELBListenerProtocol.HTTP,
        InstanceProtocol = Some(ELBListenerProtocol.HTTP),
        SSLCertificateId = None
      )
    ),
    Subnets = Seq(ResourceRef(pubSubnet1), ResourceRef(pubSubnet2)),
    CrossZone = Some(true),
    SecurityGroups = Seq(ResourceRef(routerELBSecGroupResource)),
    HealthCheck = Some(ELBHealthCheck(
      Target = "HTTP:4001/version",
      HealthyThreshold = "3",
      UnhealthyThreshold = "5",
      Interval = "30",
      Timeout = "5"
    )),
    Tags = standardTagsNoNetwork("router-elb")
  )

  private val coreOSServerLaunchConfigResource = `AWS::AutoScaling::LaunchConfiguration`(
    "CoreOSServerLaunchConfig",
    ImageId = `Fn::FindInMap`[AMIId](ParameterRef(coreOSChannelAMIParameter), `AWS::Region`, "AMI"),
    InstanceType = ParameterRef(dockerInstanceTypeParam),
    KeyName = ParameterRef(keyNameParam),
    IamInstanceProfile = None,
    SecurityGroups = Seq( ResourceRef(coreOSSecGroupResource), ResourceRef(coreOSFromJumpSecGroupResource) ),
    UserData = `Fn::Base64`(
      `Fn::Join`(
        "",
        Seq(
          "#cloud-config\n\n",
          "coreos:\n",
          "  etcd:\n",
          "    discovery: ", ParameterRef(discoveryURLParam), "\n",
          "    addr: $", ParameterRef(advertizedIPAddressParam), "_ipv4:4001\n",
          "    peer-addr: $", ParameterRef(advertizedIPAddressParam), "_ipv4:7001\n",
          "  units:\n",
          "    - name: etcd.service\n",
          "      command: start\n",
          "    - name: fleet.service\n",
          "      command: start\n",
          "    - name: fleet.socket\n",
          "      command: start\n",
          "      enable: yes\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Fleet Socket for the API\n",
          "        [Socket]\n",
          "        ListenStream=49153\n",
          "        BindIPv6Only=both\n",
          "        Service=fleet.service\n\n",
          "        [Install]\n",
          "        WantedBy=sockets.target\n",
          "    - name: consul.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Consul Server\n",
          "        After=docker.service\n",
          "        After=etcd.service\n",
          "        After=fleet.service\n",
          "        [Service]\n",
          "        Restart=on-failure\n",
          "        RestartSec=240\n",
          "        ExecStartPre=-/usr/bin/docker kill consul\n",
          "        ExecStartPre=-/usr/bin/docker rm consul\n",
          "        ExecStartPre=/usr/bin/docker pull progrium/consul\n",
          "        ExecStart=/bin/bash -c \"eval $(/usr/bin/docker run --rm progrium/consul cmd:run $", ParameterRef(advertizedIPAddressParam), "_ipv4 -e SERVICE_IGNORE=true)\"\n",
          "        ExecStop=/usr/bin/docker stop consul\n",
          "    - name: consul-announce.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Consul Server Announcer\n",
          "        PartOf=consul.service\n",
          "        After=consul.service\n",
          "        [Service]\n",
          "        ExecStart=/bin/sh -c \"while true; do etcdctl set /consul/bootstrap/machines/$(cat /etc/machine-id) $", ParameterRef(advertizedIPAddressParam), "_ipv4 --ttl 60; /usr/bin/docker exec consul consul join $(etcdctl ls /consul/bootstrap/machines | xargs -n 1 etcdctl get | tr '\\n' ' '); sleep 45; done\"\n",
          "        ExecStop=/bin/sh -c \"/usr/bin/etcdctl rm /consul/bootstrap/machines/$(cat /etc/machine-id)\"\n",
          "    - name: docker-login.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Log in to private Docker Registry\n",
          "        After=docker.service\n",
          "        [Service]\n",
          "        Type=oneshot\n",
          "        RemainAfterExit=yes\n",
          "        ExecStart=/usr/bin/docker login -e ", ParameterRef(dockerRegistryEmailParam), " -u ", ParameterRef(dockerRegistryUserParam), " -p ", ParameterRef(dockerRegistryPassParam), " ", ParameterRef(dockerRegistryUrlParam), "\n",
          "        ExecStop=/usr/bin/docker logout ", ParameterRef(dockerRegistryUrlParam), "\n",
          "# Run registrator\n",
          "    - name: registrator.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Run registrator\n",
          "        After=docker.service\n",
          "        Requires=docker.service\n\n",
          "        [Service]\n",
          "        Restart=always\n",
          "        ExecStartPre=-/usr/bin/docker kill registrator\n",
          "        ExecStartPre=-/usr/bin/docker rm registrator\n",
          "        ExecStartPre=/usr/bin/docker pull gliderlabs/registrator:latest\n",
          "        ExecStart=/usr/bin/docker run --name registrator -v /var/run/docker.sock:/tmp/docker.sock -h %H gliderlabs/registrator:latest consul://$", ParameterRef(advertizedIPAddressParam), "_ipv4:8500\n",
          "         ExecStop=/usr/bin/docker stop registrator\n",
          "# Run axon-router\n",
          "    - name: axon-router.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Run axon-router\n",
          "        After=docker.service\n",
          "        Requires=docker.service\n\n",
          "        [Service]\n",
          "        Restart=always\n",
          "        ExecStartPre=-/usr/bin/docker kill axon-router\n",
          "        ExecStartPre=-/usr/bin/docker rm axon-router\n",
          "        ExecStartPre=/usr/bin/docker pull monsantoco/axon-router:latest\n",
          "        ExecStart=/usr/bin/docker run -t -e \"NS_IP=172.17.42.1\" --name axon-router -p 80:80 monsantoco/axon-router:latest\n",
          "        ExecStop=/usr/bin/docker stop axon-router\n",
          "    - name: settimezone.service\n",
          "      command: start\n",
          "      content: |\n",
          "        [Unit]\n",
          "        Description=Set the timezone\n",
          "        [Service]\n",
          "        ExecStart=/usr/bin/timedatectl set-timezone UTC\n",
          "        RemainAfterExit=yes\n",
          "        Type=oneshot\n",
          "write_files:\n",
          "  - path: /etc/ntp.conf\n",
          "    content: |\n",
          "      server 0.pool.ntp.org\n",
          "      server 1.pool.ntp.org\n",
          "      server 2.pool.ntp.org\n",
          "      server 3.pool.ntp.org\n",
          "      restrict default nomodify nopeer noquery limited kod\n",
          "      restrict 127.0.0.1\n"
        )
      )
    )
  )

  private val coreOSServerAutoScaleResource = `AWS::AutoScaling::AutoScalingGroup`(
    "CoreOSServerAutoScale",
    AvailabilityZones = Seq("us-east-1a", "us-east-1b"),
    LaunchConfigurationName = ResourceRef(coreOSServerLaunchConfigResource),
    MinSize = StringBackedInt(2),
    MaxSize = StringBackedInt(12),
    DesiredCapacity = ParameterRef(clusterSizeParam),
    HealthCheckType = "EC2",
    VPCZoneIdentifier = Seq(ResourceRef(priSubnet1), ResourceRef(priSubnet2)),
    Tags = standardTagsNoNetworkPropagate("container"),
    LoadBalancerNames = None
  )

  def standardTagsNoNetwork(resourceName: String) = Seq(
    AmazonTag("Name", `Fn::Join`("-", Seq(resourceName, `AWS::StackName`))),
    AmazonTag("App", ParameterRef(appParam)),
    AmazonTag("Group", ParameterRef(groupParam)),
    AmazonTag("Owner", ParameterRef(ownerParam)),
    AmazonTag("Environment", ParameterRef(environmentParam)),
    AmazonTag("KeepAlive", ParameterRef(keepAliveParam)),
    AmazonTag("CostCenter", ParameterRef(costCenterParam))
  )

  private val jumpEIPResource = `AWS::EC2::EIP`.vpc(
    "JumpEIP",
    DependsOn = Some(Seq(gatewayAttachmentResource.name)),
    InstanceId = Some(ResourceRef(jumpInstanceResource))
  )

  private val nat1EIPResource = `AWS::EC2::EIP`.vpc(
    "NAT1EIP",
    DependsOn = Some(Seq(gatewayAttachmentResource.name)),
    InstanceId = Some(ResourceRef(nat1InstanceResource))
  )

  private val nat2EIPResource = `AWS::EC2::EIP`.vpc(
    "NAT2EIP",
    DependsOn = Some(Seq(gatewayAttachmentResource.name)),
    InstanceId = Some(ResourceRef(nat2InstanceResource))
  )

  val itsaDockerStack = Template(
    AWSTemplateFormatVersion = "2010-09-09",
    Description = "Autoscaling group of Docker engines in dual AZ VPC with two NAT nodes in an active/active configuration. After successfully launching this CloudFormation stack, you will have 4 subnets in 2 AZs (a pair of public/private subnets in each AZ), a jump box, two NAT instances routing outbound traffic for their respective private subnets.  The NAT instances will automatically monitor each other and fix outbound routing problems if the other instance is unavailable.  The Docker engine autoscaling group will deploy to the private subnets.",
    Parameters = Some(
      Seq(

        appParam,
        groupParam,
        serviceDomainParam,
        ownerParam,
        environmentParam,
        keepAliveParam,
        costCenterParam,
        dockerRegistryUrlParam,
        dockerRegistryUserParam,
        dockerRegistryPassParam,
        dockerRegistryEmailParam,
        keyNameParam,
        jumpInstanceTypeParam,
        natInstanceTypeParam,
        numberOfPingsParam,
        pingTimeoutParam,
        waitBetweenPingsParam,
        waitForInstanceStopParam,
        waitForInstanceStartParam,
        dockerInstanceTypeParam,
        clusterSizeParam,
        routerClusterSizeParam,
        autoScaleCoolDownParam,
        discoveryURLParam,
        advertizedIPAddressParam,
        vpcCidrParam,
        azParam,
        publicSubnet1Param,
        publicSubnet2Param,
        privateSubnetsParam,
        coreOSChannelAMIParameter,
        allowHTTPFromParam,
        allowSSHFromParam
      )
    ),
    Conditions = None,
    Mappings = Some(
      Seq(
        coreOSAlphaAMIMapping,
        coreOSBetaAMIMapping,
        coreOSStableAMIMapping,
        amazonLinuxAMIMapping,
        awsNATAMIMapping
      )
    ),

    Resources = Some(
      Seq(
        natRoleResource,
      natRoleProfileResource,
      vpcResource,
      pubSubnet1,
      priSubnet1,
      pubSubnet2,
      priSubnet2,
      internetGatewayResource,
      gatewayAttachmentResource,
      publicRouteTableResource,
      publicRouteTableResource.withRoute("Public", 1, 1, internetGatewayResource),
      privateRouteTable1Resource,
      privateRouteTable1Resource.withRoute("Private", 1, 1, nat1InstanceResource),
      privateRouteTable2Resource,
      privateRouteTable2Resource.withRoute("Private", 2, 1, nat2InstanceResource),
      withRouteTableAssoc("Public",  1, ResourceRef(publicRouteTableResource))(pubSubnet1),
      withRouteTableAssoc("Public",  2, ResourceRef(publicRouteTableResource))(pubSubnet2),
      withRouteTableAssoc("Private", 1, ResourceRef(privateRouteTable1Resource))(priSubnet1),
      withRouteTableAssoc("Private", 2, ResourceRef(privateRouteTable2Resource))(priSubnet2),
      jumpSecGroupResource,
      jumpInstanceResource,
      jumpEIPResource,
      natSecGroupResource,
      `AWS::EC2::SecurityGroupIngress`(
                                        "NATSecurityGroupAllowICMP",
                                        GroupId = ResourceRef(natSecGroupResource),
                                        IpProtocol = "icmp",
                                        SourceSecurityGroupId = Some(ResourceRef(natSecGroupResource)),
                                        FromPort = "-1",
                                        ToPort = "-1"
                                      ),
      nat1InstanceResource,

      nat1EIPResource,

      nat2InstanceResource,

      nat2EIPResource,

      routerELBSecGroupResource,

      `AWS::EC2::SecurityGroupEgress`(
                                       "RouterELBToRouterCoreOSRouter",
                                       GroupId = ResourceRef(routerELBSecGroupResource),
                                       IpProtocol = "tcp",
                                       DestinationSecurityGroupId = Some(ResourceRef(routerCoreOSSecGroupResource)),
                                       FromPort = "80",
                                       ToPort = "80"
                                     ),
      `AWS::EC2::SecurityGroupEgress`(
                                       "RouterELBToRouterCoreOSELB",
                                       GroupId = ResourceRef(routerELBSecGroupResource),
                                       IpProtocol = "tcp",
                                       DestinationSecurityGroupId = Some(ResourceRef(routerCoreOSSecGroupResource)),
                                       FromPort = "4001",
                                       ToPort = "4001"
                                     ),
      routerELBResource,

      coreOSFromJumpSecGroupResource,

      routerCoreOSSecGroupResource,

      `AWS::EC2::SecurityGroupIngress`(
                                        "RouterCoreOSFromRouterCoreOS",
                                        GroupId = ResourceRef(routerCoreOSSecGroupResource),
                                        IpProtocol = "-1",
                                        SourceSecurityGroupId = Some(ResourceRef(routerCoreOSSecGroupResource)),
                                        FromPort = "0",
                                        ToPort = "65535"
                                      ),

      `AWS::EC2::SecurityGroupIngress`(
                                        "RouterCoreOSFromCoreOS",
                                        GroupId = ResourceRef(routerCoreOSSecGroupResource),
                                        IpProtocol = "-1",
                                        SourceSecurityGroupId = Some(ResourceRef(coreOSSecGroupResource)),
                                        FromPort = "0",
                                        ToPort = "65535"
                                      ),

      routerCoreOSServerLaunchConfigResource,

      `AWS::AutoScaling::AutoScalingGroup`(
                                            "RouterCoreOSServerAutoScale",
                                            AvailabilityZones = Seq("us-east-1a", "us-east-1b"),
                                            LaunchConfigurationName = ResourceRef(routerCoreOSServerLaunchConfigResource),
                                            LoadBalancerNames = Some(Seq(ResourceRef(routerELBResource))),
                                            MinSize = StringBackedInt(2),
                                            MaxSize = StringBackedInt(12),
                                            DesiredCapacity = ParameterRef(routerClusterSizeParam),
                                            HealthCheckType = "EC2",
                                            VPCZoneIdentifier = Seq(ResourceRef(priSubnet1), ResourceRef(priSubnet2)),
                                            Tags = standardTagsNoNetworkPropagate("router")
                                          ),

      coreOSSecGroupResource,

      `AWS::EC2::SecurityGroupIngress`(
                                        "CoreOSFromCoreOS",
                                        GroupId = ResourceRef(coreOSSecGroupResource),
                                        IpProtocol = "-1",
                                        SourceSecurityGroupId = Some(ResourceRef(coreOSSecGroupResource)),
                                        FromPort = "0",
                                        ToPort = "65535"
                                      ),

      coreOSServerLaunchConfigResource,

      coreOSServerAutoScaleResource,

      coreOSServerAutoScaleResource.withPolicy("CoreOSServerAutoScaleUpPolicy", 1, ParameterRef(autoScaleCoolDownParam)),
      coreOSServerAutoScaleResource.withPolicy("CoreOSServerAutoScaleDownPolicy", -1, ParameterRef(autoScaleCoolDownParam))
    )
    ),

    Outputs = Some(
      Seq(
        Output("VPCID",          "VPC Info",          ResourceRef(vpcResource)                 ),
        Output("JumpEIP",        "Jump Box EIP",      ResourceRef(jumpEIPResource)             ),
        Output("NAT1EIP",        "NAT 1 EIP",         ResourceRef(nat1EIPResource)             ),
        Output("NAT2EIP",        "NAT 2 EIP",         ResourceRef(nat2EIPResource)             ),
        Output("RouterDNS",      "ELB DNS Name",      `Fn::GetAtt`(Seq("RouterELB","DNSName")) ),
        Output("PublicSubnet1",  "Public Subnet #1",  ResourceRef(pubSubnet1)                  ),
        Output("PrivateSubnet1", "Private Subnet #1", ResourceRef(priSubnet1)                  ),
        Output("PublicSubnet2",  "Public Subnet #2",  ResourceRef(pubSubnet2)                  ),
        Output("PrivateSubnet2", "Private Subnet #2", ResourceRef(priSubnet2)                  )
      )
    )
  )
}
