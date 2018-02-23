package com.monsanto.arch.cloudformation.model.resource

import java.time.Instant
import java.time.format.DateTimeFormatter

import spray.json._

/**
  * Represents either a Rate or Cron schedule expression.
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-cron.html Cron Schedules for Systems Manager]]
  * @see [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html Schedule Expressions for Rules]]
  */
sealed trait ScheduleExpression

/**
  * A Cron-format schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-cron.html Cron Schedules for Systems Manager]]
  * @see [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html Schedule Expressions for Rules]]
  */
case class CronSchedule(minute: String = "*",
                        hour: String = "*",
                        dayOfMonth: String = "*",
                        month: String = "*",
                        dayOfWeek: String = "*",
                        year: String = "*") extends ScheduleExpression

/**
  * A minute-rate schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-cron.html Cron Schedules for Systems Manager]]
  * @see [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html Schedule Expressions for Rules]]
  */
case class MinuteRateSchedule(value: Int) extends ScheduleExpression {
  require(value > 0, "Minute rate schedule must be greater than 0")
}

/**
  * An hour-rate schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-cron.html Cron Schedules for Systems Manager]]
  * @see [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html Schedule Expressions for Rules]]
  */
case class HourRateSchedule(value: Int) extends ScheduleExpression {
  require(value > 0, "Hour rate schedule must be greater than 0")
}

/**
  * A day-rate schedule expression
  *
  * @see [[http://docs.aws.amazon.com/systems-manager/latest/userguide/sysman-cron.html Cron Schedules for Systems Manager]]
  * @see [[http://docs.aws.amazon.com/AmazonCloudWatch/latest/events/ScheduledEvents.html Schedule Expressions for Rules]]
  * @see [[https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-applicationautoscaling-scalabletarget-scheduledaction.html Scheduled actions for Application AutoScaling]]
  */
case class DayRateSchedule(value: Int) extends ScheduleExpression {
  require(value > 0, "Day rate schedule must be greater than 0")
}

case class AtSchedule(value: Instant) extends ScheduleExpression

object ScheduleExpression extends DefaultJsonProtocol {
  implicit val format: JsonFormat[ScheduleExpression] = new JsonFormat[ScheduleExpression]{
    def write(obj: ScheduleExpression): JsValue = obj match {
      case se: CronSchedule =>
        JsString(s"cron(${se.minute} ${se.hour} ${se.dayOfMonth} ${se.month} ${se.dayOfWeek} ${se.year})")

      case se: MinuteRateSchedule if se.value == 1 => JsString(s"rate(1 minute)")
      case se: MinuteRateSchedule                  => JsString(s"rate(${se.value} minutes)")
      case se: HourRateSchedule if se.value == 1   => JsString(s"rate(1 hour)")
      case se: HourRateSchedule                    => JsString(s"rate(${se.value} hours)")
      case se: DayRateSchedule if se.value == 1    => JsString(s"rate(1 day)")
      case se: DayRateSchedule                     => JsString(s"rate(${se.value} days)")
      case AtSchedule(v)                           => JsString(DateTimeFormatter.ISO_INSTANT.format(v).replaceAll("\\..*", ""))
    }

    def read(json: JsValue): ScheduleExpression = deserializationError("ScheduleExpression not readable")
  }
}
