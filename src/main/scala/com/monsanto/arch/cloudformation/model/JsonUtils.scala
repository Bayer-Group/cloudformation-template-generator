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

  // wrap[T: JsonWriter] no longer works in Scala 2.13 for basic types in pairs because of changes to the implicit resolution
  // (it does work though for case classes where the format is defined as a JsonWriter on the companion object).
  // Added the following implicits as a workaround for basic types, although not sure if JsonWritable is a good idea in the long run.
  
  implicit def wrapString(thing: String)(implicit fmt: JsonWriter[String]) = wrap(thing)

  implicit def wrapInt(thing: Int)(implicit fmt: JsonWriter[Int]) = wrap(thing)

  implicit def wrapLong(thing: Long)(implicit fmt: JsonWriter[Long]) = wrap(thing)

  implicit def wrapFloat(thing: Float)(implicit fmt: JsonWriter[Float]) = wrap(thing)

  implicit def wrapDouble(thing: Double)(implicit fmt: JsonWriter[Double]) = wrap(thing)

  implicit def wrapBigDecimal(thing: BigDecimal)(implicit fmt: JsonWriter[BigDecimal]) = wrap(thing)

  implicit def wrapBoolean(thing: Boolean)(implicit fmt: JsonWriter[Boolean]) = wrap(thing)
}
