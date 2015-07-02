package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

/**
 * Created by Ryan Richt on 2/28/15
 */

case class AmazonTag(Key: String, Value: Token[String], PropagateAtLaunch: Option[Boolean] = None)
object AmazonTag extends DefaultJsonProtocol {
  implicit val format: JsonFormat[AmazonTag] = jsonFormat3(AmazonTag.apply)

  def fromName(name: String) = Seq(AmazonTag("Name", `Fn::Join`("-", Seq(name, `AWS::StackName`))))
  def fromNamePropagate(resourceName: String) =
    Seq(AmazonTag("Name", `Fn::Join`("-", Seq(resourceName, `AWS::StackName`)), PropagateAtLaunch = Some(true)))
}
