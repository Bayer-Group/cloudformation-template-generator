package com.monsanto.arch.cloudformation.model

import spray.json._

import scala.collection.immutable.ListMap

/**
 * Created by bkrodg on 2/16/15.
 */
case class Condition(name: String, function: Token[String])
object Condition extends DefaultJsonProtocol {
  implicit object seqFormat extends JsonWriter[Seq[Condition]]{
    implicit object format extends JsonWriter[Condition]{
      def write(obj: Condition) = obj.function.toJson
    }

    def write(objs: Seq[Condition]) = JsObject( ListMap(objs.map( o => o.name -> o.toJson ): _*) )
  }
}
