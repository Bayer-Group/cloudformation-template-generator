package com.monsanto.arch.cloudformation

import java.io.File
import java.nio.file.Files
import java.rmi.activation.UnknownObjectException

import spray.json._
import DefaultJsonProtocol._

import scala.io.Source
import scala.util.Try


case class AWSDocsProperty(name: String, fieldType: String, reference: String){


  def attArrayRepr(string: String) = {
    val firstIdx = string.indexOf("*")
    val lastIdx = string.lastIndexOf("*")
    val attrType = string.substring(firstIdx + 1, lastIdx)

    s"Seq[$attrType]"
  }

  val fieldTypeRepr = fieldType match {
    case seqString if fieldType.contains("[String]") => "Seq[String]"
    case seqString if name.contains("Tags") && fieldType.contains("{StringString...}") => "Seq[AmazonTag]"
    case seqString if fieldType.startsWith("[[") => attArrayRepr(seqString)
    case seqString if fieldType.startsWith("[") && fieldType.endsWith("]") =>
      val innerType = fieldType.replace('.',']').replace("[","").replace("]","")
      s"Seq[${innerType}]"
    case seqString if fieldType.contains("{StringString...}") => s"Map[String, String]"
    case _ => fieldType
  }

  def toScala = s"${name} : ${fieldTypeRepr}"
}
case class AWSDocsType(fileName: String,
                       name: Option[String],
                       docType: DocType,
                       attributes: Seq[AWSDocsProperty]){


  def toScala(typeRefMap: Map[String, String]) = {

    val className = name.getOrElse{
      typeRefMap.getOrElse(fileName.replace("aws-properties-","")+".md", {
        println(s"Finding ${fileName.replace("aws-properties-","")}")
        typeRefMap.find(_._1.contains(fileName.replace("aws-properties-","")))
          .getOrElse(fileName.replace("aws-properties-",""))
      })
    }

    s"""//docfile: ${fileName}.md
       |//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/${fileName}.html
       |case class ${if (docType == Resource) "`" + className + "`" else className}(
       |   ${attributes.map(_.toScala).mkString(",\n   ")}
       |)
     """.stripMargin
  }
}
sealed trait DocType
case object Properties extends DocType
case object Resource extends DocType
case object Unknown extends DocType
class DocsParser(directory: String) {



  case class FileType(docType: DocType, resourceName: String, file: File)
  object FileType {
    def fromFile(file: File) = Try{
      val arr = file.getName.split("-")

      val docType = if(arr(1) == "resource") {
        Resource
      } else if(arr(1) == "properties") {
        Properties
      } else Unknown

      val resourcename = arr(2)

      FileType(
        docType = docType,
        resourceName = resourcename,
        file = file
      )
    }
  }

  lazy val files = new File(directory)

  lazy val docFiles = files.listFiles().map(FileType.fromFile).flatMap(_.toOption)


  val mkdownJsonStartKey = "### JSON<a name="
  val mkdownJsonEndKey = "### YAML<a name=\""

  val typeJsonKey = "\"Type\" : "

  def parseAndMakeTypes(docFile: FileType): AWSDocsType = parseAndMakeTypes(docFile.docType, docFile.file)
  def parseAndMakeTypes(docType: DocType, file: File): AWSDocsType = {
    println(s"Parsing ${file.getName}")
    val lines = Source.fromFile(file).getLines().toSeq.zipWithIndex

    val startLine = lines.find(_._1.startsWith(mkdownJsonStartKey)).map(_._2)
    val endLine = lines.find(_._1.startsWith(mkdownJsonEndKey)).map(_._2)

    val mkdown = lines.slice(startLine.get + 1, endLine.get)

    val typeLine = mkdown.find(_._1.contains(typeJsonKey))

    val propertiesStart = if(typeLine.isDefined) mkdown.find(_._1.contains("\"Properties\" : {")).get._2 else mkdown.reverse.find(_._1 == "{").get._2

    val propertiesEnd = if(typeLine.isDefined) mkdown.reverse.find(_._1.endsWith("}")).get._2 else mkdown.reverse.find(_._1 == "}").get._2 + 1

    val basetype = typeLine.map(_._1.replace("  \"Type\" : \"","").replace("\",",""))

    val scalaAttributes = lines.slice(propertiesStart + 1, propertiesEnd -1 ).map{ field =>
      println(field._1)
      val split = field._1.replace("\"","").replace(" ","").split(":")
      val rawfieldName = split(0)
      println(split(0))
      val nameCamel = rawfieldName.substring(rawfieldName.indexOf('[') + 1, rawfieldName.indexOf(']'))
      val reference = rawfieldName.substring(rawfieldName.indexOf('(') + 1, rawfieldName.indexOf(')'))

      val typeValue = split.tail.mkString.replace(",","")
      println(typeValue)
      AWSDocsProperty(
        name = nameCamel,
        fieldType = typeValue,
        reference = reference
      )

    }

    AWSDocsType(
      file.getName.split('.').head,
      basetype,
      docType,
      scalaAttributes.toList
    )
  }

}

object TestApp extends App {
  val parser = new DocsParser("/Users/chrisshafer/HubProjects/aws-cloudformation-user-guide/doc_source")


  val cognitoStructure = parser.docFiles.filter(_.resourceName.contains("cognito")).map(parser.parseAndMakeTypes)
  val fileToTypeMap = cognitoStructure.flatMap(_.attributes.map(x => x.reference.replace("#cfn-","") + ".md" -> x.name).toMap).toMap

  cognitoStructure.foreach(x => println(x.toScala(fileToTypeMap)))

}