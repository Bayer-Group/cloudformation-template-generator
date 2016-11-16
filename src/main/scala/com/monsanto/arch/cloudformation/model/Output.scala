package com.monsanto.arch.cloudformation.model

import spray.json._
import DefaultJsonProtocol._
import scala.reflect.ClassTag

import scala.collection.immutable.ListMap

/**
  * Created by Ryan Richt on 2/15/15
  */

case class Output[R](name: String, Description: String, Value: Token[R], Export: Option[Token[String]] = None)(implicit format: JsonFormat[Token[R]]) {
  def valueAsJson = Token.format[Token[R]].write(Value)
}

object Output extends DefaultJsonProtocol {

  implicit def format[A] : JsonWriter[Output[A]] = new JsonWriter[Output[A]]  {
    override def write(obj: Output[A]) = {
      JsObject(Map(
        "Description" -> JsString(obj.Description),
        "Value" -> obj.valueAsJson)
        ++ exportAsJsonTuple(obj))
    }

    def exportAsJsonTuple(obj: Output[A]): Option[(String, JsValue)] = obj.Export match {
      case None => None
      case Some(x) => "Export" -> JsObject("Name" -> x.toJson)
    }
  }

  implicit val seqFormat: JsonWriter[Seq[Output[_]]] = new JsonWriter[Seq[Output[_]]] {
    def write(objs: Seq[Output[_]]) = JsObject(ListMap(objs.map(o => o.name -> format.write(o)): _*))
  }
}
