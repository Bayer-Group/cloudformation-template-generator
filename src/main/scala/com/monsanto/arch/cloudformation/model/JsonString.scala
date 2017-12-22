package com.monsanto.arch.cloudformation.model

import spray.json._

/**
  * CloudFormation has fields that are Json objects encoded as strings.
  * This class simplifies the process of writing codecs for such types.
  *
  * I tried making the argument have type A with a JsonFormat type bound,
  * but JsValue itself doesn't have an (identity) implicit defined, so
  * that made the code awkward when I really wanted a raw JsValue.  I don't think it's
  * too bad to have to call JsonString(foo.toJson), so I'm leaving it
  * simple.  Suggestions welcome.
  */
final case class JsonString(value: JsValue)
object JsonString {
  implicit val format: JsonFormat[JsonString] = new JsonFormat[JsonString] {
    override def read(json: JsValue): JsonString = json match {
      case JsString(s) => JsonString(s.parseJson)
      case _ => throw new RuntimeException(s"Not a JsString: ${json}")
    }
    override def write(obj: JsonString): JsValue = JsString(obj.value.compactPrint)
  }
}
