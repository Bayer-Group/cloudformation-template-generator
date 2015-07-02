package com.monsanto.arch.cloudformation.model

import java.io.{File, PrintWriter}

import spray.json._

/**
 * Created by Ryan Richt on 6/19/15
 */

trait VPCWriter {
  def jsonToFile[T : JsonWriter](fileName: String, subDir: String, jsObj: T) {
    val json = jsObj.toJson
    val filePath = new File("target/" + subDir + "/" + fileName)
    filePath.getParentFile.mkdirs()
    val printWriter = new PrintWriter(filePath)
    printWriter.print(json.prettyPrint)
    printWriter.close()
  }

  def writeStaxModule(fileName: String, template: Template) {
    jsonToFile(fileName, "template", template)
    jsonToFile(fileName, "config", InputParameter.templateParameterToInputParameter(template.Parameters))
  }
}
