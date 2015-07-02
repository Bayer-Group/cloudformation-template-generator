package com.monsanto.arch.cloudformation.model

import spray.json._
import DefaultJsonProtocol._

/**
 * Created by Ryan Richt on 2/15/15
 */

case class Mapping[A](name: String, map: Map[String, Map[String, A]])(implicit val formatter: JsonFormat[A]){
  type T = A
}

object Mapping extends DefaultJsonProtocol {

  implicit object seqFormat extends JsonWriter[Seq[Mapping[_]]] {

    implicit val format: JsonWriter[Mapping[_]] = new JsonWriter[Mapping[_]]{
      def write(obj: Mapping[_]) = {

        implicit val foo: JsonFormat[obj.T] = obj.formatter.asInstanceOf[JsonFormat[obj.T]]
        val raw = obj.asInstanceOf[Mapping[obj.T]].map.toJson

        JsObject(raw.asJsObject.fields - "name")
      }
    }

    def write(objs: Seq[Mapping[_]]) = JsObject(objs.map(o => o.name -> format.write(o)).toMap)
  }
}