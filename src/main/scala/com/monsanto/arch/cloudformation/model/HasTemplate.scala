package com.monsanto.arch.cloudformation.model

trait HasTemplate {

  def template : Template

  def ++(hasTemplate : HasTemplate) : HasTemplate = {
    val me = this
    new HasTemplate {
      lazy val template : Template = me.template ++ hasTemplate.template
    }
  }
}

object HasTemplate {
  import scala.language.implicitConversions

  implicit def toTemplate(hasTemplate : HasTemplate) : Template = hasTemplate.template
}
