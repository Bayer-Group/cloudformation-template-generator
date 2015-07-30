package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.Resource
import spray.json._
import DefaultJsonProtocol._
import scala.language.existentials
import scala.language.implicitConversions

/**
 * Created by Ryan Richt on 2/15/15
 */

sealed abstract class AmazonFunctionCall[LogicalReturnType](val funName: String){type CFBackingType ; val arguments: CFBackingType}
object AmazonFunctionCall extends DefaultJsonProtocol {

  def lazyWriter[T](format: => JsonWriter[T]) = new JsonWriter[T] {
    lazy val delegate = format
    def write(x: T) = delegate.write(x)
  }

  //TODO: one day if we carry around T in Token[T], and dont erase as in AFC[_], this could be more generic
  implicit val format: JsonWriter[AmazonFunctionCall[_]] = lazyWriter(new JsonWriter[AmazonFunctionCall[_]] with DefaultJsonProtocol {
     def write(obj: AmazonFunctionCall[_]) = {

      val value = obj match{
        case r:   ParameterRef[_]      => implicitly[JsonWriter[ParameterRef[_]#CFBackingType]     ].write(r.arguments)
        case r:   ResourceRef[_]       => implicitly[JsonWriter[ResourceRef[_]#CFBackingType]      ].write(r.arguments)
        case ga:  `Fn::GetAtt`         => implicitly[JsonWriter[`Fn::GetAtt`#CFBackingType]        ].write(ga.arguments)
        case j:   `Fn::Join`           => implicitly[JsonWriter[`Fn::Join`#CFBackingType]          ].write(j.arguments)
        case fim: `Fn::FindInMap`[_]   => {
                                            implicit val foo = MappingRef.formatUnderscore
                                            implicitly[JsonWriter[`Fn::FindInMap`[_]#CFBackingType]  ].write(fim.arguments)
                                          }
        case b64: `Fn::Base64`         => implicitly[JsonWriter[`Fn::Base64`#CFBackingType]        ].write(b64.arguments)
        case eq: `Fn::Equals`          => implicitly[JsonWriter[`Fn::Equals`#CFBackingType]        ].write(eq.arguments)
        case not: `Fn::Not`            => implicitly[JsonWriter[`Fn::Not`#CFBackingType]           ].write(not.arguments)
        case and: `Fn::And`            => implicitly[JsonWriter[`Fn::And`#CFBackingType]           ].write(and.arguments)
        case or: `Fn::Or`              => implicitly[JsonWriter[`Fn::Or`#CFBackingType]            ].write(or.arguments)
        case f: `Fn::If`[_]            => f.serializeArguments
        case f: If[_]                  => f.serializeArguments
      }

      JsObject(
        obj.funName -> value
      )
    }
  })
}

case class ParameterRef[R](p: Parameter{type Rep = R})
  extends AmazonFunctionCall[R]("Ref"){type CFBackingType = String ; val arguments = p.name}

//extends AmazonFunctionCall[R]("Ref"){type CFBackingType = String ; val arguments = p.name}
case class MappingRef[R](m: Mapping[R])
object MappingRef extends DefaultJsonProtocol {

  implicit def format[R]: JsonFormat[MappingRef[R]] = new JsonFormat[MappingRef[R]] {

    def write(obj: MappingRef[R]) = JsString(obj.m.name)
    def read(json: JsValue) = ???
  }

  val formatUnderscore: JsonFormat[MappingRef[_]] = new JsonFormat[MappingRef[_]] {
    def write(obj: MappingRef[_]) = JsString(obj.m.name)
    def read(json: JsValue) = ???
  }
}

case class ConditionRef(c: Condition)
object ConditionRef extends DefaultJsonProtocol {

  implicit def fromCondition(c: Condition): ConditionRef = ConditionRef(c)

  implicit val format: JsonFormat[ConditionRef] = new JsonFormat[ConditionRef] {
    def write(c: ConditionRef) = JsString(c.c.name)

    def read(json: JsValue) = ??? // TODO
  }
}

case class ResourceRef[R <: Resource[R]](r: R)
  extends AmazonFunctionCall[R]("Ref") with Token[String]{type CFBackingType = String ; val arguments = r.name}
object ResourceRef extends DefaultJsonProtocol {

  implicit def fromResource[R <: Resource[R]](r: R): ResourceRef[R] = ResourceRef(r)


  implicit def format[R <: Resource[R]]: JsonFormat[ResourceRef[R]] = new JsonFormat[ResourceRef[R]] {
    def write(obj: ResourceRef[R]) = AmazonFunctionCall.format.write(obj)

    //TODO: Implement Readers, but this is necessary to get Seq[T] JsonFormat for ResourceRef's
    def read(json: JsValue) = ???
  }

  implicit def format2: JsonFormat[ResourceRef[_]] = new JsonFormat[ResourceRef[_]] {
    def write(obj: ResourceRef[_]) = AmazonFunctionCall.format.write(obj)

    //TODO: Implement Readers, but this is necessary to get Seq[T] JsonFormat for ResourceRef's
    def read(json: JsValue) = ???
  }
}

case class `Fn::GetAtt`(args: Seq[String])
  extends AmazonFunctionCall[String]("Fn::GetAtt"){type CFBackingType = Seq[String] ; val arguments = args}

case class `Fn::Join`(joinChar: String, toJoin: Seq[Token[String]])
  extends AmazonFunctionCall[String]("Fn::Join"){type CFBackingType = (String, Seq[Token[String]]) ; val arguments = (joinChar, toJoin)}

case class `Fn::FindInMap`[R](mapName: Token[MappingRef[R]], outerKey: Token[String], innerKey: Token[String])
  extends AmazonFunctionCall[R]("Fn::FindInMap"){type CFBackingType = (Token[MappingRef[_]], Token[String], Token[String]); val arguments = (mapName.asInstanceOf[Token[MappingRef[_]]], outerKey, innerKey)}

case class `Fn::Base64`(toEncode: Token[String])
  extends AmazonFunctionCall[String]("Fn::Base64"){type CFBackingType = Token[String] ; val arguments = toEncode}

case class `Fn::Equals`(a: Token[String], b: Token[String])
  extends AmazonFunctionCall[String]("Fn::Equals"){type CFBackingType = (Token[String], Token[String]) ; val arguments = (a, b)}

case class `Fn::Not`(fn: Token[String])
  extends AmazonFunctionCall[String]("Fn::Not"){type CFBackingType = (Seq[Token[String]]) ; val arguments = Seq(fn)}

case class `Fn::And`(fn: Seq[Token[String]])
  extends AmazonFunctionCall[String]("Fn::And"){type CFBackingType = (Seq[Token[String]]) ; val arguments = fn}

case class `Fn::Or`(fn: Seq[Token[String]])
  extends AmazonFunctionCall[String]("Fn::Or"){type CFBackingType = (Seq[Token[String]]) ; val arguments = fn}

case class `Fn::If`[R : JsonFormat](conditionName : Token[String], valIfTrue: Token[R], valIfFalse: Token[R])
  extends AmazonFunctionCall[R]("Fn::If"){type CFBackingType = (Token[String], Token[R], Token[R]) ; val arguments = (conditionName, valIfTrue, valIfFalse)

  def serializeArguments = arguments.toJson
}

case class If[R : JsonFormat](conditionName : Token[String], valIfTrue: Token[R], valIfFalse: Token[String] = `AWS::NoValue`)
  extends AmazonFunctionCall[R]("Fn::If"){type CFBackingType = (Token[String], Token[R], Token[String]) ; val arguments = (conditionName, valIfTrue, valIfFalse)
  def serializeArguments = arguments.toJson
}

object `Fn::Base64` extends DefaultJsonProtocol {
  implicit val format: JsonFormat[`Fn::Base64`] = new JsonFormat[`Fn::Base64`] {

    def write(obj: `Fn::Base64`) = implicitly[JsonWriter[AmazonFunctionCall[_]]].write(obj)

    //TODO
    def read(json: JsValue) = ???
  }
}

// TODO: Parameterize Token with its return type and enforce type safety of things like
// TODO: actual strings vs. CIDR blocks vs ARNs...
// Why do you need a Token[R]? Especially a Token[ResourceRef[R]]?
// Token[R] abstracts over literal R's or Amazon Functions that return an R
// For instance, you could write a Fn::If that returns a ResourceRef[R] in either case
// but you also want to be able to pass a literal ResourceRef[R]
sealed trait Token[R]
object Token extends DefaultJsonProtocol {
  implicit def fromAny[R: JsonFormat](r: R): AnyToken[R] = AnyToken(r)
  implicit def fromOptionAny[R: JsonFormat](or: Option[R]): Option[AnyToken[R]] = or.map(r => Token.fromAny(r))
  implicit def fromString(s: String): StringToken = StringToken(s)
  implicit def fromBoolean(s: Boolean): BooleanToken = BooleanToken(s)
  implicit def fromInt(s: Int): IntToken = IntToken(s)
  implicit def fromFunction[R](f: AmazonFunctionCall[R]): FunctionCallToken[R] = FunctionCallToken[R](f)
  implicit def fromSome[R](oR: Some[R])(implicit ev1: R => Token[R]): Some[Token[R]] = oR.map(ev1).asInstanceOf[Some[Token[R]]]
  implicit def fromOption[R](oR: Option[R])(implicit ev1: R => Token[R]): Option[Token[R]] = oR.map(ev1)

  implicit def fromResource[R <: Resource[R]](r: R)(implicit conv: (R) => ResourceRef[R]): Token[ResourceRef[R]] = fromAny(conv(r))

  implicit def fromSeq[R <: Resource[R]](sR: Seq[R])(implicit toRef: R => ResourceRef[R]): Seq[Token[ResourceRef[R]]] = sR.map(r => fromAny(toRef(r)))

  // lazyFormat b/c Token and AmazonFunctionCall are mutually recursive
  implicit def format[R : JsonFormat]: JsonFormat[Token[R]] = lazyFormat(new JsonFormat[Token[R]] {
    def write(obj: Token[R]) = {
      obj match {
        case a: AnyToken[R]          => a.value.toJson
        case s: StringToken          => s.value.toJson
        case i: IntToken             => i.value.toJson
        case b: BooleanToken         => b.value.toJson
        case s: UNSAFEToken[_]       => s.value.toJson
          // its OK to erase the return type of AmazonFunctionCalls b/c they are only used at compile time for checking
          // not for de/serialization logic or JSON representation
        case f: FunctionCallToken[_] => implicitly[JsonWriter[AmazonFunctionCall[_]]].write(f.call)
        case p: PseudoParameterRef   => p.toJson
        case r: ResourceRef[_]       => r.toJson
      }
    }

    // TODO: BLERG, for now, to make Tuple formats work
    def read(json: JsValue) = ???
  })
}
case class AnyToken[R : JsonFormat](value: R) extends Token[R]
case class StringToken(value: String) extends Token[String]
case class BooleanToken(value: Boolean) extends Token[Boolean]
case class IntToken(value: Int) extends Token[Int]
case class FunctionCallToken[R](call: AmazonFunctionCall[R]) extends Token[R]

@deprecated("use ParameterRef or ResourceRef instead", "Feb 20 2015")
case class UNSAFEToken[R](value: String) extends Token[R]

sealed abstract class PseudoParameterRef(val name: String) extends Token[String]
object PseudoParameterRef extends DefaultJsonProtocol {
  implicit val format: JsonFormat[PseudoParameterRef] = new JsonFormat[PseudoParameterRef] {
    def read(json: JsValue) = json.asJsObject.fields.apply("Ref").convertTo[String] match {
      case "AWS::AccountId"        => `AWS::AccountId`
      case "AWS::NotificationARNs" => `AWS::NotificationARNs`
      case "AWS::NoValue"          => `AWS::NoValue`
      case "AWS::Region"           => `AWS::Region`
      case "AWS::StackId"          => `AWS::StackId`
      case "AWS::StackName"        => `AWS::StackName`
    }

    def write(obj: PseudoParameterRef) = JsObject("Ref" -> JsString(obj.name))
  }
}
case object `AWS::AccountId`        extends PseudoParameterRef("AWS::AccountId")
case object `AWS::NotificationARNs` extends PseudoParameterRef("AWS::NotificationARNs")
case object `AWS::NoValue`          extends PseudoParameterRef("AWS::NoValue")
case object `AWS::Region`           extends PseudoParameterRef("AWS::Region")
case object `AWS::StackId`          extends PseudoParameterRef("AWS::StackId")
case object `AWS::StackName`        extends PseudoParameterRef("AWS::StackName")