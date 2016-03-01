package com.monsanto.arch.cloudformation.model

import spray.json._
import DefaultJsonProtocol._
import scala.reflect.ClassTag

import scala.collection.immutable.ListMap

/**
  * Created by Ryan Richt on 2/15/15
  */

case class Output[R](name: String, Description: String, Value: Token[R])(implicit format: JsonFormat[Token[R]]) {
  def valueAsJson = Token.format[Token[R]].write(Value)
}

object Output extends DefaultJsonProtocol {

  implicit def format[A] : JsonWriter[Output[A]] = new JsonWriter[Output[A]]  {
    override def write(obj: Output[A]) = JsObject(
      "Description" -> JsString(obj.Description),
      "Value" -> obj.valueAsJson
    )
  }

  implicit val seqFormat: JsonWriter[Seq[Output[_]]] = new JsonWriter[Seq[Output[_]]] {
    def write(objs: Seq[Output[_]]) = JsObject(ListMap(objs.map(o => o.name -> format.write(o)): _*))
  }
}
