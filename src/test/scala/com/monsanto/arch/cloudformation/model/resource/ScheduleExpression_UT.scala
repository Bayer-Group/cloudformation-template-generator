package com.monsanto.arch.cloudformation.model.resource

import org.scalatest.{FunSpec, Matchers}
import spray.json._

class ScheduleExpression_UT extends FunSpec with Matchers {
  describe("The CronSchedule ScheduleExpression model") {
    it("should render the default") {
      val sched: ScheduleExpression = CronSchedule()

      sched.toJson.prettyPrint shouldBe "\"cron(* * * * * *)\""
    }

    it("should render whatever is entered") {
      val sched: ScheduleExpression = CronSchedule("alpha", "bravo", "charlie", "delta", "echo", "foxtrot")

      sched.toJson.prettyPrint shouldBe "\"cron(alpha bravo charlie delta echo foxtrot)\""
    }
  }

  describe("The MinuteRateSchedule ScheduleExpression model") {
    it("should not allow negative rates") {
      intercept[IllegalArgumentException] {
        MinuteRateSchedule(-1)
      }
    }

    it("should not allow a zero rate") {
      intercept[IllegalArgumentException] {
        MinuteRateSchedule(0)
      }
    }

    it("should render a single positive rate") {
      val sched: ScheduleExpression = MinuteRateSchedule(1)
      sched.toJson.prettyPrint shouldBe "\"rate(1 minute)\""
    }

    it("should render a GT 1 positive rate") {
      val sched: ScheduleExpression = MinuteRateSchedule(7)
      sched.toJson.prettyPrint shouldBe "\"rate(7 minutes)\""
    }
  }

  describe("The HourRateSchedule ScheduleExpression model") {
    it("should not allow negative rates") {
      intercept[IllegalArgumentException] {
        HourRateSchedule(-1)
      }
    }

    it("should not allow a zero rate") {
      intercept[IllegalArgumentException] {
        HourRateSchedule(0)
      }
    }

    it("should render a single positive rate") {
      val sched: ScheduleExpression = HourRateSchedule(1)
      sched.toJson.prettyPrint shouldBe "\"rate(1 hour)\""
    }

    it("should render a GT 1 positive rate") {
      val sched: ScheduleExpression = HourRateSchedule(7)
      sched.toJson.prettyPrint shouldBe "\"rate(7 hours)\""
    }
  }

  describe("The DayRateSchedule ScheduleExpression model") {
    it("should not allow negative rates") {
      intercept[IllegalArgumentException] {
        DayRateSchedule(-1)
      }
    }

    it("should not allow a zero rate") {
      intercept[IllegalArgumentException] {
        DayRateSchedule(0)
      }
    }

    it("should render a single positive rate") {
      val sched: ScheduleExpression = DayRateSchedule(1)
      sched.toJson.prettyPrint shouldBe "\"rate(1 day)\""
    }

    it("should render a GT 1 positive rate") {
      val sched: ScheduleExpression = DayRateSchedule(7)
      sched.toJson.prettyPrint shouldBe "\"rate(7 days)\""
    }
  }
}
