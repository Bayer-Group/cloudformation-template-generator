package com.monsanto.arch.cloudformation.model.resource

import com.monsanto.arch.cloudformation.model._
import spray.json._

import scala.language.implicitConversions
import scala.reflect.ClassTag
import scala.reflect.NameTransformer

/**
 * Created by Ryan Richt on 2/15/15
 */

// serializes to Type and Properties
abstract class Resource[R <: Resource[R] : ClassTag : JsonFormat]{ self: Resource[R] =>
  val Type = NameTransformer.decode( implicitly[ClassTag[R]].runtimeClass.getSimpleName )
  val name: String

  val Condition: Option[ConditionRef] = None
  val DependsOn: Option[Seq[String]] = None

  private val _format: JsonFormat[R] = implicitly[JsonFormat[R]] // the magic
  type RR = Resource[R] // and his assistant

  def when(newCondition: Option[ConditionRef] = Condition): R
}
object Resource extends DefaultJsonProtocol {
  implicit object seqFormat extends JsonWriter[Seq[Resource[_]]]{

    implicit object format extends JsonWriter[Resource[_]]{

      def write(obj: Resource[_]) = {
        val bar: obj.RR = obj.asInstanceOf[obj.RR]
        val raw = bar._format.asInstanceOf[JsonFormat[obj.RR]].write(bar).asJsObject

        val outputFields = Map(
          "Type" -> JsString(obj.Type),
          "Metadata" -> raw.fields.getOrElse("Metadata", JsNull),
          "Properties" -> JsObject(raw.fields - "name" - "Metadata" - "UpdatePolicy" - "Condition" - "DependsOn"),
          "UpdatePolicy" -> raw.fields.getOrElse("UpdatePolicy", JsNull),
          "Condition" -> obj.Condition.map(_.toJson).getOrElse(JsNull),
          "DependsOn" -> obj.DependsOn.map(dependencies => JsArray(dependencies.map(JsString(_)).toVector)).getOrElse(JsNull)
        ).filter(_._2 != JsNull)

        JsObject(outputFields)
      }
    }

    def write(objs: Seq[Resource[_]]) = JsObject( objs.map( o => o.name -> format.write(o) ).toMap )
  }
}