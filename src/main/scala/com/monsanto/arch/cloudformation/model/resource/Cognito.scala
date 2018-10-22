package com.monsanto.arch.cloudformation.model.resource


case class MessageTemplateType(

                              )

case class AdminCreateUserConfig(

  AllowAdminCreateUserOnly : Boolean,
  InviteMessageTemplate : MessageTemplateType,
  UnusedAccountValidityDays : Option[Int])

case class DeviceConfiguration()
case class LambdaConfig()
case class EmailConfiguration()
case class PasswordPolicy()
case class UserPoolPolicies(PasswordPolicy: PasswordPolicy)
case class SchemaAttribute()
case class SmsConfiguration()
