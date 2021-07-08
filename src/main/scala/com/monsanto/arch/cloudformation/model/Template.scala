package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.resource.Resource
import com.monsanto.arch.cloudformation.model.simple.SecurityGroupRoutable
import spray.json._
import DefaultJsonProtocol._

import scala.collection.immutable.ListMap
import scala.language.implicitConversions

/**
 * Template is the container for all the elements of your
 * [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/Welcome.html AWS CloudFormation]]
 * [[http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-anatomy.html template]].
 * {{{
 *   // create the template
 *   val simpleTemplate = Template(
 *     Description = Some("Simple S3 Bucket Template"),
 *     Resources = Seq(
 *       `AWS::S3::Bucket`(
 *         name = "S3Bucket",
 *         BucketName = Some("UniqueBucketForSimpleTemplate")
 *       )
 *     )
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
                    Description: Option[String] = None,
                    Parameters:  Option[Seq[Parameter]] = None,
                    Conditions:  Option[Seq[Condition]] = None,
                    Mappings:    Option[Seq[Mapping[_]]] = None,
                    Resources:   Seq[Resource[_]],
                    Routables:   Option[Seq[SecurityGroupRoutable[_ <: Resource[_]]]] = None,
                    Outputs:     Option[Seq[Output[_]]] = None,
                    AWSTemplateFormatVersion: Option[String] = None
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

  private def mergeOption[T](s1: Option[T], s2: Option[T]): Option[T] =
    if (s2.isDefined) s2 else s1

  def ++(t: Template) = Template(
    mergeOption(Description, t.Description),
    mergeOptionSeq(Parameters, t.Parameters ),
    mergeOptionSeq(Conditions, t.Conditions ),
    mergeOptionSeq(Mappings,   t.Mappings   ),
    Resources ++ t.Resources,
    mergeOptionSeq(Routables,  t.Routables  ),
    mergeOptionSeq(Outputs,    t.Outputs    ),
    mergeOption(AWSTemplateFormatVersion, t.AWSTemplateFormatVersion)
  )
}
object Template extends DefaultJsonProtocol {

  val EMPTY = Template(None, None, None, None, Seq(), None, None)

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
      if(p.AWSTemplateFormatVersion.nonEmpty) fields ++= productElement2Field[Option[String]]("AWSTemplateFormatVersion", p, 7)(optionWriter)
      if(p.Description.nonEmpty) fields ++= productElement2Field[Option[String]]("Description", p, 0)(optionWriter)
      if(p.Parameters.nonEmpty) fields ++= productElement2Field[Option[Seq[Parameter]]]("Parameters", p, 1)
      if(p.Conditions.nonEmpty) fields ++= productElement2Field[Option[Seq[Condition]]]("Conditions", p, 2)
      if(p.Mappings.nonEmpty) fields ++= productElement2Field[Option[Seq[Mapping[_]]]]("Mappings", p, 3)
      fields ++= productElement2Field[Seq[Resource[_]]]("Resources", p, 4)
      if(p.Outputs.nonEmpty) fields ++= productElement2Field[Option[Seq[Output[_]]]]("Outputs", p, 6)
      JsObject(ListMap(fields.toSeq: _*))
    }
  }

  implicit def fromParameter(p: Parameter): Template = EMPTY.copy(Parameters = Seq(p))
  implicit def fromParameters(ps: Seq[Parameter]): Template = EMPTY.copy(Parameters = ps)

  implicit def fromCondition(c: Condition): Template = EMPTY.copy(Conditions = Seq(c))
  implicit def fromConditions(cs: Seq[Condition]): Template = EMPTY.copy(Conditions = cs)

  implicit def fromMapping(m: Mapping[_]): Template = EMPTY.copy(Mappings = Seq(m))
  implicit def fromMappings(ms: Seq[Mapping[_]]): Template = EMPTY.copy(Mappings = ms)

  implicit def fromResource[R <: Resource[R]](r: R): Template = EMPTY.copy(Resources = Seq(r))
  implicit def fromResources[R <: Resource[R]](rs: Seq[R]): Template = EMPTY.copy(Resources = rs)

  implicit def fromSecurityGroupRoutable[R <: Resource[R]](sgr: SecurityGroupRoutable[R]): Template =
    Template(None, None, None, None, sgr.resources, Some(Seq(sgr)), None)
  implicit def fromSecurityGroupRoutables[R <: Resource[R]](sgrs: Seq[SecurityGroupRoutable[R]]): Template =
    Template(None, None, None, None, sgrs.flatMap(_.resources), Some(sgrs), None)

  implicit def fromOutput(o: Output[_]): Template = EMPTY.copy(Outputs = Some(Seq(o)))
  implicit def fromOutputs(os: Seq[Output[_]]): Template = EMPTY.copy(Outputs = Some(os))

  implicit def toHasTemplate(template : Template) : HasTemplate = {
    val thatTemplate = template
    new HasTemplate {
      override def template: Template = thatTemplate
    }
  }
}
