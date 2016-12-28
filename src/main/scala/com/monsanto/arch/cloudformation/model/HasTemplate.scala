package com.monsanto.arch.cloudformation.model

trait HasTemplate {

  def template : Template

  def ++(hasTemplate : HasTemplate) = {
    val me = this
    new HasTemplate {
      lazy val template = me.template ++ hasTemplate.template
    }
  }
}

