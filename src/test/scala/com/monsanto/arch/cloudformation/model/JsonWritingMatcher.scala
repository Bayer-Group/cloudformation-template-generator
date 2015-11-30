package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.Resource
import org.scalatest.Matchers
import spray.json.{JsonWriter, JsObject, JsValue, JsonFormat}

/**
 * Created by Tyler Southwick on 11/18/15.
 */
trait JsonWritingMatcher extends Matchers {

  implicit class JsonMatchResource(val value : Resource[_]) extends JsonMatcher[Resource[_]] {
    val format = Resource.seqFormat.format
  }

  implicit class JsonMatch[A](val value : A)(implicit val format: JsonWriter[A]) extends JsonMatcher[A]

  sealed trait JsonMatcher[A] {
    def value : A
    def format : JsonWriter[A]
    def shouldMatch(policy : String): Unit = {

      import spray.json._

      val jsonPolicy = value.toJson(format)
      println(jsonPolicy)
      val parsedPolicy = policy.parseJson
      jsonEquals(Seq(), jsonPolicy, parsedPolicy)
    }
  }

  def jsonEquals(path : Seq[String], v1 : JsValue, v2 : JsValue): Unit = withClue("Path: [" + path.mkString(" -> ") + "]") {
    (v1, v2) match {
      case (JsObject(o1), JsObject(o2)) =>
        o1.seq.keySet shouldEqual o2.seq.keySet
        for {
          key <- o1.seq.keySet
        } {
          jsonEquals(path ++ Seq(key), o1.seq(key), o2.seq(key))
        }
      case (j1, j2) => {
        j1 shouldEqual j2
      }
    }
  }
}
