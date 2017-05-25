package com.monsanto.arch.cloudformation.model

import org.scalatest.{FunSpec, Matchers}

class HasTemplateSpec extends FunSpec with Matchers {

  it("should concat two instances of HasTemplate") {
    object template1 extends HasTemplate {
      val param1 = StringParameter("test1")
      override def template: Template = Template.EMPTY ++ param1
    }
    object template2 extends TemplateBase {
      val param = StringParameter("test2")
    }

    val template = (template1 ++ template2).template
    template.Parameters.get.contains(template1.param1) should be(true)
    template.Parameters.get.contains(template2.param) should be(true)
  }

  it("should concat HasTemplate with Template") {
    object hasTemplate1 extends HasTemplate {
      val param1 = StringParameter("test1")
      override def template: Template = Template.EMPTY ++ param1
    }
    object hasTemplate2 extends TemplateBase {
      val param = StringParameter("test2")
    }

    val template = (hasTemplate1 ++ hasTemplate2.template).template
    template.Parameters.get.contains(hasTemplate1.param1) should be(true)
    template.Parameters.get.contains(hasTemplate2.param) should be(true)
  }

  it("should concat Template with HasTemplate") {
    object hasTemplate1 extends HasTemplate {
      val param1 = StringParameter("test1")
      override def template: Template = Template.EMPTY ++ param1
    }
    object hasTemplate2 extends TemplateBase {
      val param = StringParameter("test2")
    }

    val template : Template = hasTemplate1.template ++ hasTemplate2
    template.Parameters.get.contains(hasTemplate1.param1) should be(true)
    template.Parameters.get.contains(hasTemplate2.param) should be(true)
  }
}
