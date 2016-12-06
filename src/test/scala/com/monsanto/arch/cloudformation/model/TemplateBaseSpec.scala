package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.`AWS::SQS::Queue`
import org.scalatest.{FunSpec, Matchers}

class TemplateBaseSpec extends FunSpec with Matchers {

  it("should find ") {
    object MyTemplate extends TemplateBase {
      val param1 = StringParameter("test1", "desc1")
      def resource1 = `AWS::SQS::Queue`(
        name = "resource1",
        QueueName = "test1",
        DelaySeconds = 5,
        MessageRetentionPeriod = 2,
        ReceiveMessageWaitTimeSeconds = 9,
        VisibilityTimeout = 4
      )
      lazy val out1 = Output(name = "out1", Description = "desc", Value = `AWS::AccountId`)
    }

    MyTemplate.template.Outputs.toSeq.flatten should contain(MyTemplate.out1)
    MyTemplate.template.Parameters.toSeq.flatten should contain(MyTemplate.param1)
    MyTemplate.template.Resources.toSeq.flatten should contain(MyTemplate.resource1)
  }

}
