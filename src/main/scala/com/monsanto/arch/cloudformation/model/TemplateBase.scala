package com.monsanto.arch.cloudformation.model

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
  * </ul>
  */
trait TemplateBase {

  private def extract[A: Manifest]: Option[Seq[A]] = {
    val objs = getClass.getMethods
      .filter(_.getParameterCount == 0)
      .filter(x => manifest[A].runtimeClass.isAssignableFrom(x.getReturnType))
      .map(_.invoke(this).asInstanceOf[A])
    println(s"Extracted ${objs.length} ${manifest[A].runtimeClass.getName}")
    if (objs.isEmpty) {
      None
    } else {
      Some(objs)
    }
  }

  lazy final val template = {
    println("processing template " + getClass.getName)
    val t = Template(
      Description = "",
      Parameters = extract[Parameter],
      Conditions = extract[Condition],
      Mappings = extract[Mapping[_]],
      Resources = extract[Resource[_]],
      Routables = extract[SecurityGroupRoutable[_ <: Resource[_]]],
      Outputs = extract[Output[_]]
    )
    println("processed template " + getClass.getName)
    t
  }
}
