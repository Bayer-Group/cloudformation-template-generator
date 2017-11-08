package com.monsanto.arch.cloudformation.model

import spray.json._

case class JsonString[A](value: A)

object JsonString extends DefaultJsonProtocol {
  implicit def format[A](implicit F: JsonFormat[A]): JsonFormat[JsonString[A]] = new JsonFormat[JsonString[A]] {
    override def write(obj: JsonString[A]) = JsString(obj.value.toJson.compactPrint)
    override def read(json: JsValue): JsonString[A] = JsonString(F.read(json))
  }
}
