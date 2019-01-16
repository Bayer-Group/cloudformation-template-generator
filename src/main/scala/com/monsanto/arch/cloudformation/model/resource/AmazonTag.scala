package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

case class AmazonTag(Key: Token[String], Value: Token[String], PropagateAtLaunch: Option[Boolean] = None)
object AmazonTag extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AmazonTag] = jsonFormat3(AmazonTag.apply)

  def fromName(name: String): Seq[AmazonTag] = Seq(AmazonTag("Name", `Fn::Sub`(s"$${AWS::StackName}-${name}")))

  def fromName(name: Option[String]): Seq[AmazonTag] =
    name match {
      case Some(n) => fromName(n)
      case None => stackName()
    }

  def fromNamePropagate(resourceName: String) =
    Seq(AmazonTag("Name", `Fn::Sub`(s"$${AWS::StackName}-${resourceName}"), PropagateAtLaunch = Some(true)))

  // For anything where just tagging it with the stack name makes sense.
  def stackName() = Seq(AmazonTag("Name", `Fn::Sub`(s"$${AWS::StackName}")))

}
