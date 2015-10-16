package com.monsanto.arch.cloudformation.model

import spray.json._
import scala.reflect.runtime.universe._

class EnumFormat[T](values: Seq[T], stringifier: T => String = (x: T) => x.toString)
                   (implicit tag: TypeTag[T]) extends JsonFormat[T] {
  override def read(json: JsValue): T =
    json match {
      case s: JsString =>
        values.find(x => stringifier(x) == s.value).getOrElse(deserializationError(s.toString + " is not a valid " + tag.tpe))
      case x => deserializationError(x.toString + " is not a String")
    }
  override def write(obj: T) = JsString(stringifier(obj))
}
