package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.Resource
import com.monsanto.arch.cloudformation.model.simple.SecurityGroupRoutable
import spray.json._
import DefaultJsonProtocol._

import scala.collection.immutable.ListMap
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
 * Template is the container for all the elements of your
 * [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html AWS CloudFormation]]
 * [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-anatomy.html template]].
 * {{{
 *   // create the template
 *   val simpleTemplate = Template(
 *     AWSTemplateFormatVersion = "2010-09-09",
 *     Description = "Simple S3 Bucket Template",
 *     Resources = Some(
 *       Seq(
 *         `AWS::S3::Bucket`(
 *           name = "S3Bucket",
 *           BucketName = Some("UniqueBucketForSimpleTemplate")
 *         )
 *       )
 *     ),
 *     Parameters = None,
 *     Conditions = None,
 *     Mappings = None,
 *     Outputs = None
 *   )
 * }}}
 * @param Description See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-description-structure.html description]]
 * @param Parameters See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/parameters-section-structure.html parameters]]
 * @param Conditions See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/conditions-section-structure.html conditionals]]
 * @param Mappings See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/mappings-section-structure.html mappings]]
 * @param Resources See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/resources-section-structure.html resources]]
 * @param Outputs See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/outputs-section-structure.html outputs]]
 * @param AWSTemplateFormatVersion See [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/format-version-structure.html version]]
 *
 */
case class Template(
                    Description: String,
                    Parameters:  Option[Seq[Parameter]],
                    Conditions:  Option[Seq[Condition]],
                    Mappings:    Option[Seq[Mapping[_]]],
                    Resources:   Option[Seq[Resource[_]]],
                    Routables:   Option[Seq[SecurityGroupRoutable[_ <: Resource[_]]]] = None,
                    Outputs:     Option[Seq[Output[_]]],
                    AWSTemplateFormatVersion: String = "2010-09-09"
                   ){

  def lookupResource[R <: Resource[R]](name: String): R = {
    if(Resources.isEmpty) throw new RuntimeException("You cannot lookup in a None map")
    val candidates = Resources.get.filter{r => r.name == name}

    if(candidates.isEmpty) throw new RuntimeException(s"Resource name $name not found in template: ${this.Description}")
    if(candidates.length > 1) throw new RuntimeException(s"Name $name is not unique")

    candidates.head.asInstanceOf[R]
  }

  def lookupRoutable[R <: Resource[R]](name: String): SecurityGroupRoutable[R] = {
    if(Resources.isEmpty) throw new RuntimeException("You cannot lookup in a None map")
    val candidates = Routables.get.filter{r => r.resource.name == name}

    if(candidates.isEmpty) throw new RuntimeException(s"Resource name $name not found in template: ${this.Description}")
    if(candidates.length > 1) throw new RuntimeException(s"Name $name is not unique")

    candidates.head.asInstanceOf[SecurityGroupRoutable[R]]
  }

  private def mergeOptionSeq[T](s1: Option[Seq[T]], s2: Option[Seq[T]]): Option[Seq[T]] =
    if(s1.isEmpty && s2.isEmpty) None
    else Some(s1.getOrElse(Seq.empty[T]) ++ s2.getOrElse(Seq.empty[T]))

  def ++(t: Template) = Template(
    Description + t.Description,
    mergeOptionSeq(Parameters, t.Parameters ),
    mergeOptionSeq(Conditions, t.Conditions ),
    mergeOptionSeq(Mappings,   t.Mappings   ),
    mergeOptionSeq(Resources,  t.Resources  ),
    mergeOptionSeq(Routables,  t.Routables  ),
    mergeOptionSeq(Outputs,    t.Outputs    ),
    this.AWSTemplateFormatVersion
  )
}
object Template extends DefaultJsonProtocol {

  val EMPTY = Template("", None, None, None, None, None, None)

  def collapse[R <: Resource[R]](rs: Seq[R]): Template = {
    val dupes = rs.groupBy(_.name).collect{case(y,xs) if xs.size>1 => y}
    if (dupes.nonEmpty) throw new IllegalArgumentException(s"Multiple resources with the same name would clobber each other. Found duplicates of $dupes")
    rs.foldLeft(Template.EMPTY)(_ ++ _)
  }

  // b/c we really dont need to implement READING yet, and its a bit trickier
  implicit def optionWriter[T : JsonWriter]: JsonWriter[Option[T]] = new JsonWriter[Option[T]] {
    def write(option: Option[T]) = option match {
      case Some(x) => x.toJson
      case None => JsNull
    }
  }

  implicit val format: JsonWriter[Template] = new JsonWriter[Template]{
    def write(p: Template) = {
      val fields = new collection.mutable.ListBuffer[(String, JsValue)]
      fields ++= productElement2Field[String]("AWSTemplateFormatVersion", p, 7)
      fields ++= productElement2Field[String]("Description", p, 0)
      if(p.Parameters.nonEmpty) fields ++= productElement2Field[Option[Seq[Parameter]]]("Parameters", p, 1)
      if(p.Conditions.nonEmpty) fields ++= productElement2Field[Option[Seq[Condition]]]("Conditions", p, 2)
      if(p.Mappings.nonEmpty) fields ++= productElement2Field[Option[Seq[Mapping[_]]]]("Mappings", p, 3)
      if(p.Resources.nonEmpty) fields ++= productElement2Field[Option[Seq[Resource[_]]]]("Resources", p, 4)
      if(p.Outputs.nonEmpty) fields ++= productElement2Field[Option[Seq[Output[_]]]]("Outputs", p, 6)
      JsObject(ListMap(fields: _*))
    }
  }

  implicit def fromResource[R <: Resource[R]](r: R): Template = Template("", None, None, None, Some(Seq(r)), None, None)
  implicit def fromResources[R <: Resource[R]](r: Seq[R]): Template = Template("", None, None, None, Some(r), None, None)
  implicit def fromOutput(o: Output[_]): Template = Template("", None, None, None, None, None, Some(Seq(o)))
  implicit def fromOutputs(o: Seq[Output[_]]): Template = Template("", None, None, None, None, None, Some(o))
  implicit def fromSecurityGroupRoutable[R <: Resource[R]](sgr: SecurityGroupRoutable[R]): Template =
    Template("", None, None, None, Some(sgr.resources), Some(Seq(sgr)), None)
  implicit def fromSecurityGroupRoutables[R <: Resource[R]](sgrs: Seq[SecurityGroupRoutable[R]]): Template =
    Template("", None, None, None, Some(sgrs.flatMap(sgr => sgr.resources)), Some(sgrs), None)
}
