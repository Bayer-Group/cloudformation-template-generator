package com.monsanto.arch.cloudformation.model

import spray.json.{JsValue, JsonWriter}


/**
  * Wrapper for anything that can be written to Json.  Useful to allow specifying
  * a Map of String -> JsonWritable that can take any value that can be written out as Json.
  */
case class JsonWritable[T: JsonWriter](thing: T) {
  implicit val fmt = implicitly[JsonWriter[T]]
}

object JsonWritable {
  import scala.language.implicitConversions

  implicit def fmt[T] = new JsonWriter[JsonWritable[T]] {
    override def write(obj: JsonWritable[T]): JsValue = obj.fmt.write(obj.thing)
  }

  implicit def wrap[T: JsonWriter](thing: T) = JsonWritable(thing)
}
