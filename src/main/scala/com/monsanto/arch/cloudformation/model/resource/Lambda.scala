package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._
import com.monsanto.arch.cloudformation.model.Token.TokenSeq

class Runtime(val runtime: String)

@deprecated("Node v0.10.42 is currently marked as deprecated by AWS.", "3.6.3")
case object NodeJS extends Runtime("nodejs")

case object `NodeJS4.3` extends Runtime("nodejs4.3")

case object `NodeJS4.3-edge` extends Runtime("nodejs4.3-edge")

case object `NodeJS6.10` extends Runtime("nodejs6.10")

case object Java8 extends Runtime("java8")

case object Python27 extends Runtime("python2.7")

case object Python36 extends Runtime("python3.6")

case object DotNetCore10 extends Runtime("dotnetcore-1.0")

object Runtime {

  implicit object format extends JsonFormat[Runtime] {
    override def write(obj: Runtime) = JsString(obj.runtime)

    override def read(json: JsValue): Runtime = json match {
      case JsString(runtime) => new Runtime(runtime)
      case x => deserializationError(s"Expected string for Lambda Runtime, but got $x")
    }
  }

}


/**
  * The AWS::Lambda::Function resource creates an AWS Lambda (Lambda) function that can run code in response to events.
  * For more information, see [[http://docs.aws.amazon.com/lambda/latest/dg/API_CreateFunction.html CreateFunction]]
  * in the AWS Lambda Developer Guide.
  *
  * @param name CloudFormation logical name
  * @param FunctionName A name for the function. If you don't specify a name, AWS CloudFormation generates a unique
  *                     physical ID and uses that ID for the function's name. *Important:* If you specify a name, you
  *                     cannot perform updates that require replacement of this resource. You can perform updates that
  *                     require no or some interruption. If you must replace the resource, specify a new name.
  * @param Code The source code of your Lambda function. You can point to a file in an Amazon Simple Storage Service
  *             (Amazon S3) bucket or specify your source code as inline text.
  * @param Description A description of the function.
  * @param Handler The name of the function (within your source code) that Lambda calls to start running your code.
  *                *Note:* If you specify your source code as inline text by specifying the ZipFile property within the
  *                Code property, specify index.function_name as the handler.
  * @param Runtime The runtime environment for the Lambda function that you are uploading.
  * @param Role The Amazon Resource Name (ARN) of the AWS Identity and Access Management (IAM) execution role that
  *             Lambda assumes when it runs your code to access AWS services.
  * @param DeadLetterConfig Configures how Lambda handles events that it can't process. If you don't specify a Dead
  *                         Letter Queue (DLQ) configuration, Lambda discards events after the maximum number of retries.
  * @param MemorySize The amount of memory, in MB, that is allocated to your Lambda function. Lambda uses this value to
  *                   proportionally allocate the amount of CPU power. Your function use case determines your CPU and
  *                   memory requirements. For example, a database operation might need less memory than an image
  *                   processing function. You must specify a value that is greater than or equal to 128, and it must
  *                   be a multiple of 64. You cannot specify a size larger than 1536. The default value is 128 MB.
  * @param Timeout The function execution time (in seconds) after which Lambda terminates the function. Because the
  *                execution time affects cost, set this value based on the function's expected execution time. By
  *                default, Timeout is set to 3 seconds.
  * @param TracingConfig The parent object that contains your Lambda function's tracing settings. By default, the Mode
  *                      property is set to PassThrough.
  * @param Environment Key-value pairs that Lambda caches and makes available for your Lambda functions. Use environment
  *                    variables to apply configuration changes, such as test and production environment configurations,
  *                    without changing your Lambda function source code.
  * @param KmsKeyArn The Amazon Resource Name (ARN) of an AWS Key Management Service (AWS KMS) key that Lambda uses to
  *                  encrypt and decrypt environment variable values.
  * @param VpcConfig If the Lambda function requires access to resources in a VPC, specify a VPC configuration that
  *                  Lambda uses to set up an elastic network interface (ENI). The ENI enables your function to connect
  *                  to other resources in your VPC, but it doesn't provide public Internet access. If your function
  *                  requires Internet access (for example, to access AWS services that don't have VPC endpoints),
  *                  configure a Network Address Translation (NAT) instance inside your VPC or use an Amazon Virtual
  *                  Private Cloud (Amazon VPC) NAT gateway. *Note:* When you specify this property, AWS CloudFormation
  *                  might not be able to delete the stack if another resource in the template (such as a security group)
  *                  requires the attached ENI to be deleted before it can be deleted. We recommend that you run AWS
  *                  CloudFormation with the ec2:DescribeNetworkInterfaces permission, which enables AWS CloudFormation
  *                  to monitor the state of the ENI and to wait (up to 40 minutes) for Lambda to delete the ENI.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Lambda::Function`(name: String,
                                   Code: Code,
                                   Handler: String,
                                   Runtime: Runtime,
                                   Role: Token[String],
                                   FunctionName: Option[Token[String]] = None,
                                   Description: Option[Token[String]] = None,
                                   DeadLetterConfig: Option[DeadLetterConfig] = None,
                                   MemorySize: Option[Token[Int]] = None,
                                   Timeout: Option[Token[Int]] = None,
                                   TracingConfig: Option[TracingConfig] = None,
                                   Environment : Option[LambdaEnvironment] = None,
                                   KmsKeyArn : Option[Token[String]] = None,
                                   VpcConfig : Option[LambdaVpcConfig] = None,
                                   override val DependsOn: Option[Seq[String]] = None,
                                   override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Lambda::Function`] with HasArn with Subscribable {

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Lambda::Function` = copy(Condition = newCondition)

  override def asSubscription = Subscription(
    Endpoint = arn,
    Protocol = "lambda"
  )

}

object `AWS::Lambda::Function` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::Function`] = jsonFormat16(`AWS::Lambda::Function`.apply)
}


/**
  *Environment is a property of the AWS::Lambda::Function resource that specifies key-value pairs that the AWS Lambda
  * (Lambda) function can access so that you can apply configuration changes, such as test and production environment
  * configurations, without changing the function code.
  *
  * @param Variables A map of key-value pairs that the Lambda function can access.
  */
case class LambdaEnvironment(Variables : Option[Map[String, Token[String]]])
object LambdaEnvironment extends DefaultJsonProtocol {
  implicit val format : JsonFormat[LambdaEnvironment] = jsonFormat1(LambdaEnvironment.apply)
}


/**
  * VpcConfig is a property of the AWS::Lambda::Function resource that enables your AWS Lambda (Lambda) function to
  * access resources in a VPC. For more information, see
  * [[http://docs.aws.amazon.com/lambda/latest/dg/vpc.html Configuring a Lambda Function to Access Resources]]
  * in an Amazon VPC in the AWS Lambda Developer Guide.
  *
  * @param SecurityGroupIds A list of one or more security groups IDs in the VPC that includes the resources to which
  *                         your Lambda function requires access.
  * @param SubnetIds A list of one or more subnet IDs in the VPC that includes the resources to which your Lambda
  *                  function requires access.
  */
case class LambdaVpcConfig(SecurityGroupIds : TokenSeq[String], SubnetIds : TokenSeq[String])

object LambdaVpcConfig extends DefaultJsonProtocol {
  implicit val format : JsonFormat[LambdaVpcConfig] = jsonFormat2(LambdaVpcConfig.apply)
}


/**
  * Code is a property of the AWS::Lambda::Function resource that enables you to specify the source code of an AWS
  * Lambda function. Your source code can be located in either the template or a file in an Amazon Simple Storage
  * Service (Amazon S3) bucket. For nodejs4.3, nodejs6.10, python2.7, and python3.6 runtime environments only, you can
  * provide source code as inline text in your template.
  *
  * *Note:*
  * To update a Lambda function whose source code is in an Amazon S3 bucket, you must trigger an update by updating the
  * S3Bucket, S3Key, or S3ObjectVersion property. Updating the source code alone doesn't update the function.
  *
  * @param S3Bucket The name of the Amazon S3 bucket where the .zip file that contains your deployment package is stored.
  *                 This bucket must reside in the same AWS Region that you're creating the Lambda function in. You can
  *                 specify a bucket from another AWS account as long as the Lambda function and the bucket are in the
  *                 same region. *Note:* The cfn-response module isn't available for source code that's stored in Amazon
  *                 S3 buckets. To send responses, write your own functions.
  * @param S3Key The location and name of the .zip file that contains your source code. If you specify this property,
  *              you must also specify the S3Bucket property.
  * @param S3ObjectVersion If you have S3 versioning enabled, the version ID of the.zip file that contains your source
  *                        code. You can specify this property only if you specify the S3Bucket and S3Key properties.
  * @param ZipFile For nodejs4.3, nodejs6.10, python2.7, and python3.6 runtime environments, the source code of your
  *                Lambda function. You can't use this property with other runtime environments.
  *                You can specify up to 4096 characters. You must precede certain special characters in your source
  *                code (such as quotation marks ("), newlines (\n), and tabs (\t)) with a backslash (\). For a list
  *                of special characters, see http://json.org/.
  *                If you specify a function that interacts with an AWS CloudFormation custom resource, you don't have
  *                to write your own functions to send responses to the custom resource that invoked the function. AWS
  *                CloudFormation provides a response module that simplifies sending responses. For more information, see
  *                [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-lambda-function-code.html#cfn-lambda-function-code-cfnresponsemodule cfn-response Module.]]
  */
case class Code(S3Bucket: Option[Token[String]] = None,
                S3Key: Option[Token[String]] = None,
                S3ObjectVersion: Option[Token[String]] = None,
                ZipFile: Option[Token[String]] = None) {
  require((S3Bucket.nonEmpty && S3Key.nonEmpty) ^ ZipFile.nonEmpty, "You must specify both the S3Bucket and S3Key properties, or specify the ZipFile property.")

  if (S3ObjectVersion.nonEmpty)
    require(S3Bucket.nonEmpty && S3Key.nonEmpty, "You can specify S3ObjectVersion only if you specify the S3Bucket and S3Key properties.")
}

object Code extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Code] = jsonFormat4(Code.apply)
}


/**
  * DeadLetterConfig is a property of the AWS::Lambda::Function resource that specifies a Dead Letter Queue (DLQ) that
  * AWS Lambda (Lambda) sends events to when it can't process them. For example, you can send unprocessed events to an
  * Amazon Simple Notification Service (Amazon SNS) topic, where you can take further action.
  *
  * @param TargetArn The Amazon Resource Name (ARN) of a resource where Lambda delivers unprocessed events, such as an
  *                  Amazon SNS topic or Amazon Simple Queue Service (Amazon SQS) queue. For the Lambda function
  *                  execution role, you must explicitly provide the relevant permissions so that access to your DLQ
  *                  resource is part of the execution role for your Lambda function.
  */
case class DeadLetterConfig(TargetArn: Token[String])

object DeadLetterConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[DeadLetterConfig] = jsonFormat1(DeadLetterConfig.apply)
}

sealed trait TracingConfig extends Product with Serializable
object TracingConfig extends DefaultJsonProtocol {
  private type T = TracingConfig
  case object Active extends T
  case object PassThrough extends T
  val values = Seq(Active, PassThrough)
  implicit val format: JsonFormat[T] = new EnumFormat[T](values)
}

/**
  * The AWS::Lambda::Permission resource associates a policy statement with a specific AWS Lambda (Lambda) function's
  * access policy. The function policy grants a specific AWS service or application permission to invoke the function.
  *
  * @param name CloudFormation logical name
  * @param Action The Lambda actions that you want to allow in this statement. For example, you can specify
  *               lambda:CreateFunction to specify a certain action, or use a wildcard (lambda:*) to grant permission
  *               to all Lambda actions.
  * @param EventSourceToken A unique token that must be supplied by the principal invoking the function.
  * @param FunctionName The name (physical ID), Amazon Resource Name (ARN), or alias ARN of the Lambda function that
  *                     you want to associate with this statement. Lambda adds this statement to the function's access
  *                     policy.
  * @param Principal The entity for which you are granting permission to invoke the Lambda function. This entity can be
  *                  any valid AWS service principal, such as s3.amazonaws.com or sns.amazonaws.com, or, if you are
  *                  granting cross-account permission, an AWS account ID. For example, you might want to allow a custom
  *                  application in another AWS account to push events to Lambda by invoking your function.
  * @param SourceAccount The AWS account ID (without hyphens) of the source owner. For example, if you specify an S3
  *                      bucket in the SourceArn property, this value is the bucket owner's account ID. You can use this
  *                      property to ensure that all source principals are owned by a specific account.
  * @param SourceArn The ARN of a resource that is invoking your function. When granting Amazon Simple Storage Service
  *                  (Amazon S3) permission to invoke your function, specify this property with the bucket ARN as its
  *                  value. This ensures that events generated only from the specified bucket, not just any bucket from
  *                  any AWS account that creates a mapping to your function, can invoke the function.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Lambda::Permission`(name: String,
                                     Action: String,
                                     FunctionName: Token[ResourceRef[`AWS::Lambda::Function`]],
                                     Principal: Token[String],
                                     EventSourceToken: Option[Token[String]] = None,
                                     SourceAccount: Option[Token[String]] = None,
                                     SourceArn: Option[Token[String]] = None,
                                     override val DependsOn: Option[Seq[String]] = None,
                                     override val Condition: Option[ConditionRef] = None)
  extends Resource[`AWS::Lambda::Permission`] {
  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Lambda::Permission` = copy(Condition = newCondition)
}

object `AWS::Lambda::Permission` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::Permission`] = jsonFormat9(`AWS::Lambda::Permission`.apply)
}


/**
  * The AWS::Lambda::EventSourceMapping resource specifies a stream as an event source for an AWS Lambda (Lambda)
  * function. The stream can be an Kinesis stream or an Amazon DynamoDB (DynamoDB) stream. Lambda invokes the associated
  * function when records are posted to the stream.
  *
  * @param name CloudFormation logical name
  * @param BatchSize The largest number of records that Lambda retrieves from your event source when invoking your
  *                  function. Your function receives an event with all the retrieved records.
  * @param Enabled Indicates whether Lambda begins polling the event source.
  * @param EventSourceArn The Amazon Resource Name (ARN) of the Kinesis or DynamoDB stream that is the source of events.
  *                       Any record added to this stream can invoke the Lambda function.
  * @param FunctionName The name or ARN of a Lambda function to invoke when Lambda detects an event on the stream.
  * @param StartingPosition The position in the stream where Lambda starts reading.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Lambda::EventSourceMapping`(
                                              name: String,
                                              BatchSize: Option[Token[Int]] = None,
                                              Enabled: Option[Token[Boolean]] = None,
                                              EventSourceArn: Token[String],
                                              FunctionName: Token[ResourceRef[`AWS::Lambda::Function`]],
                                              StartingPosition: StartingPosition,
                                              override val DependsOn: Option[Seq[String]] = None,
                                              override val Condition: Option[ConditionRef] = None
                                              ) extends Resource[`AWS::Lambda::EventSourceMapping`] {

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Lambda::EventSourceMapping` = copy(Condition = newCondition)
}

object `AWS::Lambda::EventSourceMapping` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::EventSourceMapping`] = jsonFormat8(`AWS::Lambda::EventSourceMapping`.apply)
}

sealed trait StartingPosition
object StartingPosition extends DefaultJsonProtocol {
  case object TRIM_HORIZON extends StartingPosition
  case object LATEST       extends StartingPosition
  case object AT_TIMESTAMP extends StartingPosition
  val values = Seq(TRIM_HORIZON, LATEST, AT_TIMESTAMP)
  implicit val format: JsonFormat[StartingPosition] = new EnumFormat[StartingPosition](values)
}


/**
  * The AWS::Lambda::Version resource publishes a specified version of an AWS Lambda (Lambda) function. When publishing
  * a new version of your function, Lambda copies the latest version of your function.
  *
  * @param name CloudFormation logical name
  * @param FunctionName The Lambda function for which you want to publish a version. You can specify the function's
  *                     name or its Amazon Resource Name (ARN).
  * @param Description A description of the version you are publishing. If you don't specify a value, Lambda copies the
  *                    description from the $LATEST version of the function.
  * @param CodeSha256 The SHA-256 hash of the deployment package that you want to publish. This value must match the
  *                   SHA-256 hash of the $LATEST version of the function. Specify this property to validate that you
  *                  are publishing the correct package.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Lambda::Version`(name: String,
                                  FunctionName: Token[ResourceRef[`AWS::Lambda::Function`]],
                                  Description: Option[String] = None,
                                  CodeSha256: Option[Token[String]] = None,
                                  override val DependsOn: Option[Seq[String]] = None,
                                  override val Condition: Option[ConditionRef] = None)
    extends Resource[`AWS::Lambda::Version`]
    with HasArn {

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))

  def version = `Fn::GetAtt`(Seq(name, "Version"))

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Lambda::Version` = copy(Condition = newCondition)
}

object `AWS::Lambda::Version` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::Version`] = jsonFormat6(
      `AWS::Lambda::Version`.apply)
}


/**
  * The AWS::Lambda::Alias resource creates an alias that points to the version of an AWS Lambda (Lambda) function that
  * you specify. Use aliases when you want to control which version of your function other services or applications
  * invoke. Those services or applications can use your function's alias so that they don't need to be updated whenever
  * you release a new version of your function.
  *
  * @param name CloudFormation logical name
  * @param Name A name for the alias.
  * @param FunctionName The Lambda function that you want to associate with this alias. You can specify the function'
  *                     name or its Amazon Resource Name (ARN).
  * @param FunctionVersion The version of the Lambda function that you want to associate with this alias.
  * @param Description Information about the alias, such as its purpose or the Lambda function that is associated with
  *                    it.
  * @param RoutingConfig Use this parameter to point your alias to two different function versions, allowing you to
  *                      dictate what percentage of traffic will invoke each version.
  * @param DependsOn Declare dependencies for resources that must be created or deleted in a specific order.
  * @param Condition Define conditions by using the intrinsic condition functions. These conditions determine when AWS
  *                  CloudFormation creates the associated resources.
  */
case class `AWS::Lambda::Alias`(name: String,
                                Name: Token[String],
                                FunctionName: Token[ResourceRef[`AWS::Lambda::Function`]],
                                FunctionVersion: Token[ResourceRef[`AWS::Lambda::Version`]],
                                Description: Option[Token[String]] = None,
                                RoutingConfig: Option[RoutingConfig] = None,
                                override val DependsOn: Option[Seq[String]] = None,
                                override val Condition: Option[ConditionRef] = None)
    extends Resource[`AWS::Lambda::Alias`]
    with HasArn {

  override def arn = `Fn::GetAtt`(Seq(name, "Arn"))

  def when(newCondition: Option[ConditionRef] = Condition): `AWS::Lambda::Alias` = copy(Condition = newCondition)
}

object `AWS::Lambda::Alias` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Lambda::Alias`] = jsonFormat8(`AWS::Lambda::Alias`.apply)
}


/**
  * The AliasRoutingConfiguration property type specifies two different versions of an AWS Lambda function, allowing you
  * to dictate what percentage of traffic will invoke each version.
  *
  * @param AdditionalVersionWeights The percentage of traffic that will invoke the updated function version.
  */
case class RoutingConfig(AdditionalVersionWeights: Seq[VersionWeight])
object RoutingConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RoutingConfig] = jsonFormat1(RoutingConfig.apply)
}


/**
  * The VersionWeight property type specifies the percentages of traffic that will invoke each function versions for an
  * AWS Lambda alias.
  *
  * @param FunctionVersion Function version to which the alias points.
  * @param FunctionWeight The percentage of traffic that will invoke the function version.
  */
case class VersionWeight(FunctionVersion: Token[String],
                         FunctionWeight: Token[Double])

object VersionWeight extends DefaultJsonProtocol {
  implicit val format: JsonFormat[VersionWeight] = jsonFormat2(VersionWeight.apply)
}
