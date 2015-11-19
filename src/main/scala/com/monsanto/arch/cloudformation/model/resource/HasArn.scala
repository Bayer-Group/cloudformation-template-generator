package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model.{FunctionCallToken, `Fn::GetAtt`, Token}

/**
 * Created by Tyler Southwick on 11/19/15.
 */
trait HasArn {

  def name : String

  def arn : Token[String] = FunctionCallToken(`Fn::GetAtt`(Seq(name, "Arn")))
}
