package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.TemplateBase.ExtractType
import com.monsanto.arch.cloudformation.model.resource.Resource
import com.monsanto.arch.cloudformation.model.simple.SecurityGroupRoutable

/**
  * Finds all val/lazy val/def in the class (using reflection) that produce elements of a template
  * and builds a template from them. This search for:
  *
  * <ul>
  *   <li>Parameter</li>
  *   <li>Condition</li>
  *   <li>Mapping</li>
  *   <li>Resource</li>
  *   <li>SecurityGroupRoutable</li>
  *   <li>Output</li>
  *   <li>Template</li>
  *   <li>HasTemplate</li>
  * </ul>
  */
trait TemplateBase extends HasTemplate {

  private def extract[A: Manifest]: Option[Seq[A]] = {
    val objs = getClass.getMethods
      //make sure that we don't call template method again
      .filterNot(m => m.getName == "template" && m.getReturnType.isAssignableFrom(classOf[Template]))
      .filter(_.getParameterCount == 0)
      .filter(x => manifest[A].runtimeClass.isAssignableFrom(x.getReturnType))
      .map(_.invoke(this).asInstanceOf[A])

    logExtraction(ExtractType(manifest[A].runtimeClass.getName, objs.length))
    if (objs.isEmpty) {
      None
    } else {
      Some(objs)
    }
  }

  import TemplateBase._
  protected def logExtraction(status : TemplateExtractionStatus): Unit = status match {
    case ProcessedTemplate(name) => println(s"Processed template: $name")
    case StartingTemplateProcessing(name) => println(s"Processing template: $name")
    case ExtractType(extractType, totalCount) => println(s"Extracted $totalCount $extractType")
  }

  lazy final val template : Template = {
    logExtraction(StartingTemplateProcessing(getClass.getName))
    val t = Template(
      Description = "",
      Parameters = extract[Parameter],
      Conditions = extract[Condition],
      Mappings = extract[Mapping[_]],
      Resources = extract[Resource[_]] getOrElse Seq(),
      Routables = extract[SecurityGroupRoutable[_ <: Resource[_]]],
      Outputs = extract[Output[_]]
    )

    val subHasTemplates = extract[HasTemplate].toSeq.flatten
    val subTemplates = extract[Template].toSeq.flatten
    try {
      (subHasTemplates.map(_.template) ++ subTemplates).foldLeft(t) {_ ++ _}
    } finally {
      logExtraction(ProcessedTemplate(getClass.getName))
    }
  }
}

object TemplateBase {
  sealed trait TemplateExtractionStatus
  case class ProcessedTemplate(name : String) extends TemplateExtractionStatus
  case class StartingTemplateProcessing(name : String) extends TemplateExtractionStatus
  case class ExtractType(`type`: String, totalCount : Int) extends TemplateExtractionStatus
}
