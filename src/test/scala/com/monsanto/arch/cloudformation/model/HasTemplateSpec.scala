package com.monsanto.arch.cloudformation.model

import org.scalatest.{FunSpec, Matchers}

class HasTemplateSpec extends FunSpec with Matchers {

  it("should concat") {
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
}
