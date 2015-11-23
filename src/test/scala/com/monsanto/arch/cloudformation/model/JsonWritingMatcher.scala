package com.monsanto.arch.cloudformation.model

import org.scalatest.Matchers
import spray.json.{JsObject, JsValue, JsonFormat}

/**
 * Created by Tyler Southwick on 11/18/15.
 */
trait JsonWritingMatcher extends Matchers {

  implicit class JsonMatch[A](a : A)(implicit format : JsonFormat[A]) {
    def shouldMatch(policy : String): Unit = {

      import java.io.{File, PrintWriter}

      import spray.json._

      val jsonPolicy = a.toJson
      val parsedPolicy = policy.parseJson
      jsonPolicy shouldEqual parsedPolicy
    }
  }

  def jsonEquals(v1 : JsValue, v2 : JsValue): Unit = {
    (v1, v2) match {
      case (JsObject(o1), JsObject(o2)) =>
        o1.seq.keySet shouldEqual o2.seq.keySet
        for {
          key <- o1.seq.keySet
        } {
          jsonEquals(o1.seq(key), o2.seq(key))
        }
      case (j1, j2) => j1 shouldEqual j2
    }
  }
}
