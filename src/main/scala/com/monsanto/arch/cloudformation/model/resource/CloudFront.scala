package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import com.monsanto.arch.cloudformation.model.resource._
import spray.json.{JsonFormat, DefaultJsonProtocol}

case class `AWS::CloudFront::Distribution`(
                                            name: String,
                                            DistributionConfig: DistributionConfig,
                                            override val Condition: Option[ConditionRef] = None

                                          ) extends Resource[`AWS::CloudFront::Distribution`] {
  def domainName : Token[String] = `Fn::GetAtt`(Seq(name, "DomainName"))

  def distributionId : Token[String] = ResourceRef(this)

  override def when(newCondition: Option[ConditionRef]): `AWS::CloudFront::Distribution` = copy(Condition = newCondition)

}

import DefaultJsonProtocol._

object `AWS::CloudFront::Distribution` {
  implicit val format: JsonFormat[`AWS::CloudFront::Distribution`] = jsonFormat3(`AWS::CloudFront::Distribution`.apply)
}

case class DistributionConfig(

                               Aliases: Option[Seq[String]] = None,
                               CacheBehaviors: Option[Seq[CacheBehavior]] = None,
                               Comment: Option[String] = None,
                               CustomErrorResponses: Option[Seq[CustomErrorResponse]] = None,
                               DefaultCacheBehavior: DefaultCacheBehavior,
                               DefaultRootObject: Option[String] = None,
                               Enabled: Boolean,
                               Logging: Option[Logging] = None,
                               Origins: Seq[Origin],
                               PriceClass: Option[String] = None,
                               Restrictions: Option[Restriction] = None,
                               ViewerCertificate: Option[ViewerCertificate] = None,
                               WebACLId: Option[Token[String]] = None
                             )

object DistributionConfig {
  implicit val format: JsonFormat[DistributionConfig] = jsonFormat13(DistributionConfig.apply)
}

case class ViewerCertificate(
                              CloudFrontDefaultCertificate: Option[Boolean],
                              IamCertificateId: Option[Token[String]],
                              MinimumProtocolVersion: Option[String],
                              SslSupportMethod: Option[String]
                            )

object ViewerCertificate {
  implicit val format: JsonFormat[ViewerCertificate] = jsonFormat4(ViewerCertificate.apply)
}

case class Restriction(
                        GeoRestriction: GeoRestriction
                      )

object Restriction {
  implicit val format: JsonFormat[Restriction] = jsonFormat1(Restriction.apply)
}

//http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cloudfront-distributionconfig-restrictions-georestriction.html
case class GeoRestriction(
                           Locations: Option[Seq[String]],
                           RestrictionType: String
                         )

object GeoRestriction {
  implicit val format: JsonFormat[GeoRestriction] = jsonFormat2(GeoRestriction.apply)
}


case class Origin(
                   DomainName: Token[String],
                   Id: String,
                   //either one of
                   CustomOriginConfig: Option[CustomOrigin],
                   //OR
                   S3OriginConfig: Option[S3Origin],
                   OriginPath: Option[Token[String]]

                 )

object Origin {
  implicit val format: JsonFormat[Origin] = jsonFormat5(Origin.apply)
}

case class CustomOrigin(
                         HTTPPort: Option[String],
                         HTTPSPort: Option[String],
                         OriginProtocolPolicy: String
                       )

object CustomOrigin {
  implicit val format: JsonFormat[CustomOrigin] = jsonFormat3(CustomOrigin.apply)
}

case class S3Origin(
                     OriginAccessIdentity: Option[String]
                   )

object S3Origin {
  implicit val format: JsonFormat[S3Origin] = jsonFormat1(S3Origin.apply)
}

case class Logging(
                    Bucket: Token[String],
                    IncludeCookies: Option[Boolean] = None,
                    Prefix: Option[Token[String]] = None
                  )

object Logging {
  implicit val format: JsonFormat[Logging] = jsonFormat3(Logging.apply)
}

case class DefaultCacheBehavior(
                                 AllowedMethods: Option[Seq[String]] = None,
                                 CachedMethods: Option[Seq[String]] = None,
                                 DefaultTTL: Option[Int] = None,
                                 ForwardedValues: ForwardedValues,
                                 MaxTTL: Option[Int] = None,
                                 MinTTL: Option[Int] = None,
                                 SmoothStreaming: Option[Boolean] = None,
                                 TargetOriginId: Token[String],
                                 TrustedSigners: Option[Seq[String]] = None,
                                 ViewerProtocolPolicy: String
                               )

object DefaultCacheBehavior {
  implicit val format: JsonFormat[DefaultCacheBehavior] = jsonFormat10(DefaultCacheBehavior.apply)
}

case class CustomErrorResponse(
                                ErrorCachingMinTTL: Option[Int],
                                ErrorCode: Int,
                                //these two are together
                                ResponseCode: Option[Int],
                                ResponsePagePath: Option[Token[String]]
                              )

object CustomErrorResponse {
  implicit val format: JsonFormat[CustomErrorResponse] = jsonFormat4(CustomErrorResponse.apply)
}

case class CacheBehavior(
                          AllowedMethods: Option[Seq[String]] = None,
                          CachedMethods: Option[Seq[String]] = None,
                          DefaultTTL: Option[Int] = None,
                          ForwardedValues: ForwardedValues,
                          MaxTTL: Option[Int] = None,
                          MinTTL: Option[Int] = None,
                          PathPattern: String,
                          SmoothStreaming: Option[Boolean] = None,
                          TargetOriginId: Token[String],
                          TrustedSigners: Option[Seq[String]] = None,
                          ViewerProtocolPolicy: String
                        )

object CacheBehavior {
  implicit val format: JsonFormat[CacheBehavior] = jsonFormat11(CacheBehavior.apply)
}

case class ForwardedValues(
                            Cookies: Option[Cookies],
                            Headers: Option[Seq[String]],
                            QueryString: Boolean
                          )

object ForwardedValues {
  implicit val format: JsonFormat[ForwardedValues] = jsonFormat3(ForwardedValues.apply)
}

case class Cookies(
                    Forward: String,
                    WhitelistedNames: Option[Seq[String]]
                  )

object Cookies {
  implicit val format: JsonFormat[Cookies] = jsonFormat2(Cookies.apply)
}
