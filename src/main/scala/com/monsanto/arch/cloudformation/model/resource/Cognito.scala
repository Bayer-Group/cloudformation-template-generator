package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{ConditionRef, ResourceRef, Token, `AWS::AccountId`, `AWS::Region`}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}
import com.monsanto.arch.cloudformation.model._
import spray.json._
import DefaultJsonProtocol._
import com.monsanto.arch.cloudformation.model.Token.TokenSeq

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-identitypool.html
case class `AWS::Cognito::IdentityPool`(
                                         /*The name of your Amazon Cognito identity pool.  */
                                         IdentityPoolName : Option[Token[String]],
                                         /*Specifies whether the identity pool supports unauthenticated logins.  */
                                         AllowUnauthenticatedIdentities : Boolean,
                                         /*The "domain" by which Amazon Cognito will refer to your users. This name acts as a placeholder that allows your backend and the Amazon Cognito service to communicate about the developer provider. For the DeveloperProviderName, you can use letters and periods (.), underscores (_), and dashes (-).  */
                                         DeveloperProviderName : Option[Token[String]],
                                         /*Key-value pairs that map provider names to provider app IDs.  */
                                         SupportedLoginProviders : Option[Map[String, String]],
                                         /*An array of Amazon Cognito user pools and their client IDs.  */
                                         CognitoIdentityProviders : Option[Seq[CognitoIdentityProvider]],
                                         /*A list of Amazon Resource Names (ARNs) of Security Assertion Markup Language (SAML) providers.  */
                                         SamlProviderARNs : Option[TokenSeq[String]],
                                         /*A list of ARNs for the OpendID Connect provider.  */
                                         OpenIdConnectProviderARNs : Option[Seq[String]],
                                         /*Configuration options for configuring Amazon Cognito streams.  */
                                         CognitoStreams : Option[CognitoStreams],
                                         /*Configuration options to be applied to the identity pool.  */
                                         PushSync : Option[PushSync],
                                         /*The events to configure.  */
                                         CognitoEvents : Option[Map[String, String]]
)

object `AWS::Cognito::IdentityPool` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::IdentityPool`] = jsonFormat10(`AWS::Cognito::IdentityPool`.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-smsconfiguration.html
case class SmsConfiguration(
   /*The external ID used in IAM role trust relationships.  
For more information about using external IDs, see [How to Use an External ID When Granting Access to Your AWS Resources to a Third Party](http://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_create_for-user_externalid.html) in the *AWS Identity and Access Management User Guide*.  */
   ExternalId : Option[String],
   /*The Amazon Resource Name (ARN) of the Amazon Simple Notification Service (SNS) caller.  */
   SnsCallerArn : Token[String]
)

object SmsConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[SmsConfiguration] = jsonFormat2(SmsConfiguration.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-identitypool-cognitostreams.html
case class CognitoStreams(
   /*The Amazon Resource Name (ARN) of the role Amazon Cognito can assume to publish to the stream. This role must grant access to Amazon Cognito (cognito-sync) to invoke PutRecord on your Amazon Cognito stream.  */
   RoleArn : Option[Token[String]],
   /*Status of the Cognito streams. Valid values are: ENABLED or DISABLED.  */
   StreamingStatus : Option[String],
   /*The name of the Amazon Cognito stream to receive updates. This stream must be in the developer's account and in the same region as the identity pool.  */
   StreamName : Option[String]
)

object CognitoStreams extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CognitoStreams] = jsonFormat3(CognitoStreams.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-policies.html
case class Policies(
   /*Specifies information about the user pool password policy.  */
   PasswordPolicy : Option[PasswordPolicy]
)

object Policies extends DefaultJsonProtocol {
  implicit val format: JsonFormat[Policies] = jsonFormat1(Policies.apply)
}

sealed trait CognitoAttributeType
case object StringAT extends CognitoAttributeType
case object NumberAT extends CognitoAttributeType
case object BinaryAT extends CognitoAttributeType
case object DateTimeAT extends CognitoAttributeType

object CognitoAttributeType {
  implicit object format extends JsonFormat[CognitoAttributeType] {
    override def write(obj: CognitoAttributeType) = JsString(obj match {
      case StringAT => "String"
      case NumberAT => "Number"
      case BinaryAT => "Boolean"
      case DateTimeAT => "DateTime"
    })

    override def read(json: JsValue): CognitoAttributeType = ???
  }
}

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-schemaattribute.html
case class SchemaAttribute(
   /*The attribute data type. Can be one of the following: String, Number, DateTime, or Boolean.  */
   AttributeDataType : Option[CognitoAttributeType],
   /*Specifies whether the attribute type is developer only.  */
   DeveloperOnlyAttribute : Option[Boolean],
   /*Specifies whether the attribute can be changed after it has been created. True means mutable and False means immutable.  */
   Mutable : Option[Boolean],
   /*A schema attribute of the name type.  */
   Name : Option[String],
   /*Specifies the constraints for an attribute of the number type.  */
   NumberAttributeConstraints : Option[NumberAttributeConstraints],
   /*Specifies the constraints for an attribute of the string type.  */
   StringAttributeConstraints : Option[StringAttributeConstraints],
   /*Specifies whether a user pool attribute is required. If the attribute is required and the user does not provide a value, registration or sign-in fails.  */
   Required : Option[Boolean]
)

object SchemaAttribute extends DefaultJsonProtocol {
  implicit val format: JsonFormat[SchemaAttribute] = jsonFormat7(SchemaAttribute.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-identitypool-pushsync.html
case class PushSync(
   /*List of Amazon SNS platform application ARNs that could be used by clients.  */
   ApplicationArns : Option[Seq[Token[String]]],
   /*An IAM role configured to allow Amazon Cognito to call SNS on behalf of the developer.  */
   RoleArn : Option[Token[String]]
)

object PushSync extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PushSync] = jsonFormat2(PushSync.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-userpoolusertogroupattachment.html
case class `AWS::Cognito::UserPoolUserToGroupAttachment`(
   /*The name of the group.  */
   GroupName : Token[String],
   /*The user's user name.  */
   Username : String,
   /*The ID of the user pool.  */
   UserPoolId : Token[String]
)

object `AWS::Cognito::UserPoolUserToGroupAttachment` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::UserPoolUserToGroupAttachment`] = jsonFormat3(`AWS::Cognito::UserPoolUserToGroupAttachment`.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-identitypoolroleattachment-rolemapping.html
case class RoleMapping(
   /*Specifies the action to be taken if either no rules match the claim value for the Rules type, or there is no cognito:preferred_role claim and there are multiple cognito:roles matches for the Token type. If you specify Token or Rules as the Type, AmbiguousRoleResolution is required.  
Valid values are AuthenticatedRole or Deny.  */
   AmbiguousRoleResolution : Option[String],
   /*The rules to be used for mapping users to roles. If you specify Rules as the role mapping type, RulesConfiguration is required.  */
   RulesConfiguration : Option[RulesConfiguration],
   /*The role mapping type. Token will use cognito:roles and cognito:preferred_role claims from the Amazon Cognito identity provider token to map groups to roles. Rules will attempt to match claims from the token to map to a role.   
Valid values are Token or Rules.  */
   Type : String
)

object RoleMapping extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RoleMapping] = jsonFormat3(RoleMapping.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-stringattributeconstraints.html
case class StringAttributeConstraints(
   /*The maximum value of an attribute that is of the string data type.  */
   MaxLength : Option[StringBackedInt],
   /*The minimum value of an attribute that is of the string data type.  */
   MinLength : Option[StringBackedInt]
)

object StringAttributeConstraints extends DefaultJsonProtocol {
  implicit val format: JsonFormat[StringAttributeConstraints] = jsonFormat2(StringAttributeConstraints.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-identitypoolroleattachment.html
case class `AWS::Cognito::IdentityPoolRoleAttachment`(
                                                       /*An identity pool ID in the format REGION:GUID.  */
                                                       IdentityPoolId : Token[String],
                                                       /*How users for a specific identity provider are to mapped to roles. This is a string to RoleMapping object map. The string identifies the identity provider, for example, "graph.facebook.com" or "cognito-idp-east-1.amazonaws.com/us-east-1_abcdefghi:app_client_id"  */
                                                       RoleMappings : Option[RoleMapping],
                                                       /*The map of roles associated with this pool. For a given role, the key will be either "authenticated" or "unauthenticated" and the value will be the Role ARN.  */
                                                       Roles : Option[Map[String, String]]
)

object `AWS::Cognito::IdentityPoolRoleAttachment` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::IdentityPoolRoleAttachment`] = jsonFormat3(`AWS::Cognito::IdentityPoolRoleAttachment`.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-identitypoolroleattachment-mappingrule.html
case class MappingRule(
   /*The claim name that must be present in the token, for example, "isAdmin" or "paid."  */
   Claim : String,
   /*The match condition that specifies how closely the claim value in the IdP token must match Value.  
Valid values are: Equals, Contains, StartsWith, and NotEqual.  */
   MatchType : String,
   /*The Amazon Resource Name (ARN) of the role.  */
   RoleARN : Token[String],
   /*A brief string that the claim must match, for example, "paid" or "yes."  */
   Value : String
)

object MappingRule extends DefaultJsonProtocol {
  implicit val format: JsonFormat[MappingRule] = jsonFormat4(MappingRule.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-userpoolclient.html
case class `AWS::Cognito::UserPoolClient`(
   name: String,
   /*The client name for the user pool client that you want to create.  */
   ClientName : Option[Token[String]],
   /*The explicit authentication flows, which can be one of the following: ADMIN_NO_SRP_AUTH, CUSTOM_AUTH_FLOW_ONLY, or USER_PASSWORD_AUTH.  */
   ExplicitAuthFlows : Option[Seq[String]],
   /*Specifies whether you want to generate a secret for the user pool client being created.  */
   GenerateSecret : Option[Boolean],
   /*The read attributes.  */
   ReadAttributes : Option[Seq[String]],
   /*The time limit, in days, after which the refresh token is no longer valid.  */
   RefreshTokenValidity : Option[Int],
   /*The user pool ID for the user pool where you want to create a client.  */
   UserPoolId : Token[String],
   /*The write attributes.  */
   WriteAttributes : Option[Seq[String]],
   override val Condition:   Option[ConditionRef] = None,
   override val DependsOn:   Option[Seq[String]] = None
) extends Resource[`AWS::Cognito::UserPoolClient`] {

  override def when(newCondition: Option[ConditionRef]): `AWS::Cognito::UserPoolClient` = copy(Condition = newCondition)
}

object `AWS::Cognito::UserPoolClient` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::UserPoolClient`] = jsonFormat10(`AWS::Cognito::UserPoolClient`.apply)
}


//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-userpoolgroup.html
case class `AWS::Cognito::UserPoolGroup`(
   /*A description of the user group.  */
   Description : Option[String],
   /*The name of the user group. GroupName must be unique.  */
   GroupName : Token[String],
   /*A nonnegative Int value that specifies the precedence of this group relative to the other groups that a user can belong to in the user pool. Zero is the highest Precedence value. Groups with lower Precedence values take precedence over groups with higher or null Precedence values. If a user belongs to two or more groups, the role ARN of the group with the lowest precedence value is used in the cognito:roles and cognito:preferred_role claims in the user's tokens.  
Two groups can have the same Precedence value. If this happens, neither group takes precedence over the other. If two groups with the same Precedence value have the same role ARN, that role is used in the cognito:preferred_role claim in tokens for users in each group. If the two groups have different role ARNs, the cognito:preferred_role claim is not set in users' tokens.  
The default Precedence value is null.  */
   Precedence : Option[Int],
   /*The role ARN for the group.  */
   RoleArn : Option[Token[String]],
   /*The user pool ID.  */
   UserPoolId : Token[String]
)

object `AWS::Cognito::UserPoolGroup` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::UserPoolGroup`] = jsonFormat5(`AWS::Cognito::UserPoolGroup`.apply)
}

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-invitemessagetemplate.html
//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-admincreateuserconfig-invitemessagetemplate.html
case class InviteMessageTemplate(
   /*The message template for email messages.  */
   EmailMessage : Option[String],
   /*The subject line for email messages.  */
   EmailSubject : Option[String],
   /*The message template for SMS messages.  */
   SMSMessage : Option[String]
)

object InviteMessageTemplate extends DefaultJsonProtocol {
  implicit val format: JsonFormat[InviteMessageTemplate] = jsonFormat3(InviteMessageTemplate.apply)
}


//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-admincreateuserconfig.html
case class AdminCreateUserConfig(
   /*Set to True if only the administrator is allowed to create user profiles. Set to False if users can sign themselves up via an app.  */
   AllowAdminCreateUserOnly : Option[Boolean],
   /*The message template to be used for the welcome message to new users.  */
   InviteMessageTemplate : Option[InviteMessageTemplate],
   /*The user account expiration limit, in days, after which the account is no longer usable. To reset the account after that time limit, you must call AdminCreateUser again, specifying RESEND for the MessageAction parameter. The default value for this parameter is 7.  */
   UnusedAccountValidityDays : Option[Int]
)

object AdminCreateUserConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AdminCreateUserConfig] = jsonFormat3(AdminCreateUserConfig.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-userpool.html
case class `AWS::Cognito::UserPool`(
   name: String,
   /*The type of configuration for creating a new user profile.  */
   AdminCreateUserConfig : Option[AdminCreateUserConfig] = None,
   /*Attributes supported as an alias for this user pool. Possible values: phone_number, email, or preferred_username.   */
   AliasAttributes : Option[Seq[String]] = None,
   /*The attributes to be auto-verified. Possible values: email or phone_number.   */
   AutoVerifiedAttributes : Option[Seq[String]] = None,
   /*The type of configuration for the user pool's device tracking.  */
   DeviceConfiguration : Option[DeviceConfiguration] = None,
   /*The email configuration.  */
   EmailConfiguration : Option[EmailConfiguration] = None,
   /*A string representing the email verification message. Must contain {####} in the description.  */
   EmailVerificationMessage : Option[String] = None,
   /*A string representing the email verification subject.  */
   EmailVerificationSubject : Option[String] = None,
   /*The AWS Lambda trigger configuration information for the Amazon Cognito user pool.  */
   LambdaConfig : Option[LambdaConfig] = None,
   /*Specifies multi-factor authentication (MFA) configuration details. Can be one of the following values:  
OFF - MFA tokens are not required and cannot be specified during user registration.  
ON - MFA tokens are required for all user registrations. You can only specify required when you are initially creating a user pool.  
OPTIONAL - Users have the option when registering to create an MFA token.  */
   MfaConfiguration : Option[String] = None,
   /*The policies associated with the Amazon Cognito user pool.  */
   Policies : Option[Policies] = None,
   /*A list of schema attributes for the new user pool. These attributes can be standard or custom attributes.  */
   Schema : Option[Seq[SchemaAttribute]] = None,
   /*A string representing the SMS authentication message. Must contain {####} in the message.  */
   SmsAuthenticationMessage : Option[String] = None,
   /*The Short Message Service (SMS) configuration.  */
   SmsConfiguration : Option[SmsConfiguration] = None,
   /*A string representing the SMS verification message. Must contain {####} in the message.  */
   SmsVerificationMessage : Option[String] = None,
   /*Specifies whether email addresses or phone numbers can be specified as usernames when a user signs up. Possible values: phone_number or email.  */
   UsernameAttributes : Option[Seq[String]] = None,
   /*A string used to name the user pool.  */
   UserPoolName : Token[String],
   /*The cost allocation tags for the user pool. For more information, see [Adding Cost Allocation Tags to Your User Pool](https://docs.aws.amazon.com//cognito/latest/developerguide/cognito-user-pools-cost-allocation-tagging.html) in the *Amazon Cognito Developer Guide*.  */
   UserPoolTags : Option[Seq[AmazonTag]] = None,
   override val Condition: Option[ConditionRef] = None,
   override val DeletionPolicy: Option[DeletionPolicy] = None,
   override val DependsOn: Option[Seq[String]] = None
 ) extends Resource[`AWS::Cognito::UserPool`] with HasArn {

  def when(newCondition: Option[ConditionRef]): `AWS::Cognito::UserPool` = copy(Condition = newCondition)

  override def arn: Token[String] = ResourceRef(this)
}

object `AWS::Cognito::UserPool` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::UserPool`] = jsonFormat21(`AWS::Cognito::UserPool`.apply)
}
     
case class UserPoolUserAttributeType(
                                     Name: String,
                                     Value: String
                                    )

object UserPoolUserAttributeType {
  implicit val formation: JsonFormat[UserPoolUserAttributeType] = jsonFormat2(UserPoolUserAttributeType.apply)
}
//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cognito-userpooluser.html
case class `AWS::Cognito::UserPoolUser`(
   name: String,
   /*Specifies how the welcome message will be sent. For email, specify EMAIL. To use a phone number, specify SMS. You can specify more than one value. The default value is SMS.   */
   DesiredDeliveryMediums : Option[Seq[String]],
   /*Use this parameter only if the phone_number_verified attribute or the email_verified attribute is set to True. Otherwise, it is ignored. The default value is False.  
If this parameter is set to True and the phone number or email address specified in the UserAttributes parameter already exists as an alias with a different user, the API call migrates the alias from the previous user to the newly created user. The previous user can no longer log in using that alias.  
If this parameter is set to False and the alias already exists, the API throws an AliasExistsException error.   */
   ForceAliasCreation : Option[Boolean],
   /*A list of name-value pairs that contain user attributes and attribute values to be set for the user that you are creating. You can create a user without specifying any attributes other than Username. However, any attributes that you specify as required (in CreateUserPool or in the **Attributes** tab of the console) must be supplied either by you (in your call to AdminCreateUser) or by the user (when signing up in response to your welcome message).  */
   UserAttributes : Option[Seq[UserPoolUserAttributeType]],
   /*Specifies the action you'd like to take for the message. Valid values are RESEND and SUPPRESS.  
To resend the invitation message to a user that already exists and reset the expiration limit on the user's account, set this parameter to RESEND. To suppress sending the message, set it to SUPPRESS. You can specify only one value.  */
   MessageAction : Option[String],
   /*The user name for the user. Username must be unique within the user pool. It must be a UTF-8 string between 1 and 128 characters. You can't change the username.  */
   Username : Option[Token[String]],
   /*The ID for the user pool where the user will be created.  */
   UserPoolId : Token[String],
   /*The user's validation data. This is a list of name-value pairs that contain user attributes and attribute values that you can use for custom validation, such as restricting the types of user accounts that can be registered. For example, you might choose to allow or disallow user sign-up based on the user's domain.  
To configure custom validation, you must create a Pre Sign-up Lambda trigger for the user pool. The Lambda trigger receives the validation data and uses it in the validation process. For more information, see [Customizing User Pool Workflows by Using AWS Lambda Triggers](http://docs.aws.amazon.com/cognito/latest/developerguide/cognito-user-identity-pools-working-with-aws-lambda-triggers.html) in the *Amazon Cognito Developer Guide*.  */
   ValidationData : Option[Seq[AttributeType]],
   override val Condition: Option[ConditionRef] = None,
   override val DependsOn: Option[Seq[String]] = None
) extends Resource[`AWS::Cognito::UserPoolUser`] {
  def when(newCondition: Option[ConditionRef]): `AWS::Cognito::UserPoolUser` = copy(Condition = newCondition)
}

object `AWS::Cognito::UserPoolUser` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`AWS::Cognito::UserPoolUser`] = jsonFormat10(`AWS::Cognito::UserPoolUser`.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-lambdaconfig.html
case class LambdaConfig(
   /*Creates an authentication challenge.  */
   CreateAuthChallenge : Option[Token[String]],
   /*A custom Message AWS Lambda trigger.  */
   CustomMessage : Option[Token[String]],
   /*Defines the authentication challenge.  */
   DefineAuthChallenge : Option[Token[String]],
   /*A post-authentication AWS Lambda trigger.  */
   PostAuthentication : Option[Token[String]],
   /*A post-confirmation AWS Lambda trigger.  */
   PostConfirmation : Option[Token[String]],
   /*A pre-authentication AWS Lambda trigger.  */
   PreAuthentication : Option[Token[String]],
   /*A pre-registration AWS Lambda trigger.  */
   PreSignUp : Option[Token[String]],
   /*Verifies the authentication challenge response.  */
   VerifyAuthChallengeResponse : Option[Token[String]]
)

object LambdaConfig extends DefaultJsonProtocol {
  implicit val format: JsonFormat[LambdaConfig] = jsonFormat8(LambdaConfig.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-numberattributeconstraints.html
case class NumberAttributeConstraints(
   /*The maximum value of an attribute that is of the number data type.  */
   MaxValue : Option[String],
   /*The minimum value of an attribute that is of the number data type.  */
   MinValue : Option[String]
)

object NumberAttributeConstraints extends DefaultJsonProtocol {
  implicit val format: JsonFormat[NumberAttributeConstraints] = jsonFormat2(NumberAttributeConstraints.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-deviceconfiguration.html
case class DeviceConfiguration(
   /*Indicates whether a challenge is required on a new device. Only applicable to a new device.  */
   ChallengeRequiredOnNewDevice : Option[Boolean],
   /*If true, a device is only remembered on user prompt.  */
   DeviceOnlyRememberedOnUserPrompt : Option[Boolean]
)

object DeviceConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[DeviceConfiguration] = jsonFormat2(DeviceConfiguration.apply)
}
     



//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-identitypool-cognitoidentityprovider.html
case class CognitoIdentityProvider(
   /*The client ID for the Amazon Cognito user pool.  */
   ClientId : Option[Token[String]],
   /*The provider name for an Amazon Cognito user pool. For example, cognito-idp.us-east-2.amazonaws.com/us-east-2_123456789.  */
   ProviderName : Option[Token[String]],
   /*TRUE if server-side token validation is enabled for the identity provider’s token.  
Once you set ServerSideTokenCheck to TRUE for an identity pool, that identity pool will check with the integrated user pools to make sure that the user has not been globally signed out or deleted before the identity pool provides an OIDC token or AWS credentials for the user.  
If the user is signed out or deleted, the identity pool will return a 400 Not Authorized error.  */
   ServerSideTokenCheck : Option[Boolean]
)

object CognitoIdentityProvider extends DefaultJsonProtocol {
  implicit val format: JsonFormat[CognitoIdentityProvider] = jsonFormat3(CognitoIdentityProvider.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-passwordpolicy.html
case class PasswordPolicy(
   /*The minimum length of the password policy that you have set. Cannot be less than 6.  */
   MinimumLength : Option[Int],
   /*In the password policy that you have set, refers to whether you have required users to use at least one lowercase letter in their password.  */
   RequireLowercase : Option[Boolean],
   /*In the password policy that you have set, refers to whether you have required users to use at least one number in their password.  */
   RequireNumbers : Option[Boolean],
   /*In the password policy that you have set, refers to whether you have required users to use at least one symbol in their password.  */
   RequireSymbols : Option[Boolean],
   /*In the password policy that you have set, refers to whether you have required users to use at least one uppercase letter in their password.  */
   RequireUppercase : Option[Boolean]
)

object PasswordPolicy extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PasswordPolicy] = jsonFormat5(PasswordPolicy.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-userpool-emailconfiguration.html
case class EmailConfiguration(
   /*The REPLY-TO email address.  */
   ReplyToEmailAddress : Option[Token[String]],
   /*The Amazon Resource Name (ARN) of the email source.  */
   SourceArn : Option[Token[String]]
)

object EmailConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[EmailConfiguration] = jsonFormat2(EmailConfiguration.apply)
}
     

//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-cognito-identitypoolroleattachment-rolemapping-rulesconfiguration.html
case class RulesConfiguration(
   /*A list of rules. You can specify up to 25 rules per identity provider.  */
   Rules : Seq[MappingRule]
)

object RulesConfiguration extends DefaultJsonProtocol {
  implicit val format: JsonFormat[RulesConfiguration] = jsonFormat1(RulesConfiguration.apply)
}
     
