package com.monsanto.arch.cloudformation.codegen

import java.io.{File, PrintWriter}
import scala.annotation.tailrec
import scala.io.Source
import scala.util.Try

/*
Use this to generate somewhat accurate type structures
The result will not likely compile
This functionality is intended to help prime new types not instantiate them perfectly.
To run:
clone the docs repo and and run over this directory:
https://github.com/awsdocs/aws-cloudformation-user-guide/tree/master/doc_source

see ExampleApp for an example
 */
case class AWSPropertyDocsElement(name: String, required: Boolean, description: String)

case class AWSDocsProperty(name: String, fieldType: String, reference: String){


  def attArrayRepr(string: String) = {
    val firstIdx = string.indexOf("*")
    val lastIdx = string.lastIndexOf("*")
    val attrType = string.substring(firstIdx + 1, lastIdx)

    s"Seq[$attrType]"
  }

  val fieldTypeRepr = fieldType match {
    case seqString if fieldType.contains("[String]") => "Seq[String]"
    case tags if name.contains("Tags") && fieldType.contains("{StringString...}") => "Seq[AmazonTag]"
    case arrType if fieldType.startsWith("[[") => attArrayRepr(arrType)
    case innerTpe if fieldType.startsWith("[") && fieldType.endsWith("]") =>
      val innerType = fieldType.replace('.',']').replace("[","").replace("]","")
      s"Seq[${innerType}]"
    case seqString if fieldType.contains("{StringString...}") => s"Map[String, String]"
    case integer if fieldType.equals("Integer") => "Int"
    case number if fieldType.equals("Number") => "Number"
    case _ => fieldType
  }

  def toScala(opt: Option[AWSPropertyDocsElement]) = {

    val propline = if (opt.map(_.required).getOrElse(false)) s"${name} : ${fieldTypeRepr}"
                    else s"${name} : Option[${fieldTypeRepr}]"

   opt.map(x => s"   /*${x.description.replace('\\','`').replace("`","")}*/\n").getOrElse("") + "   " + propline
  }
}
case class AWSDocsType(fileName: String,
                       name: Option[String],
                       docType: DocType,
                       attributes: Seq[AWSDocsProperty],
                       propertyDocs: Seq[AWSPropertyDocsElement]){

  lazy val propertyDocsMap = propertyDocs.map(x => x.name -> x).toMap

  def toScala(typeRefMap: Map[String, String], typeNames: Seq[String]) = {

    val className: String = name.getOrElse{
      typeRefMap.getOrElse(fileName.replace("aws-properties-","")+".md", {
          typeNames.find(_.equalsIgnoreCase(s"${fileName.split("-").last.toUpperCase}"))
            .getOrElse(s"${fileName.split("-").last.toUpperCase}")
      })
    }

    val fullClassName = if (docType == Resource) "`" + className + "`" else className
    s"""
       |//docurl: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/${fileName}.html
       |case class ${fullClassName}(
       |${attributes.map(x => x.toScala(propertyDocsMap.get(x.name))).mkString(",\n")}
       |)
       |
       |object ${fullClassName} extends DefaultJsonProtocol {
       |  implicit val format: JsonFormat[${fullClassName}] = jsonFormat${attributes.length}(${fullClassName}.apply)
       |}
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

  lazy val docFiles = files.listFiles().filter(x => x.getName.startsWith("aws-properties") || x.getName.startsWith("aws-resource")).map(FileType.fromFile).flatMap(_.toOption)


  val mkdownJsonStartKey = "### JSON<a name="
  val mkdownJsonEndKey = "### YAML<a name=\""

  val typeJsonKey = "\"Type\" : "

  def parseAndMakeTypes(docFile: FileType): AWSDocsType = parseAndMakeTypes(docFile.docType, docFile.file)

  val propertyDocsStartKey = "## Properties<a name="
  val propertyDocsEndKey = "## Return Value"


  @tailrec
  final def splitBySeq(seq: Seq[String], predicate: String => Boolean, accum: Seq[Seq[String]] = Seq()): Seq[Seq[String]] = {
    val first = seq.zipWithIndex.find(x => predicate(x._1)).map(_._2)

    if(first.isDefined){
      val section = seq.slice(0, first.get)
      splitBySeq(seq.drop(section.length + 1), predicate, accum :+ section )
    } else accum :+ seq

  }

  def parsePropertyBlock(seq: Seq[String]) = Try{
    val name: String = seq.head.substring(seq.head.indexOf("`") + 1, seq.head.lastIndexOf("`"))
    val required: Boolean = seq.find(_.startsWith("*Required*")).map{
      case tru if tru.contains("Yes") => true
      case fal if fal.contains("No") => false
    }.getOrElse(throw new Exception("Bad require"))

    val description = seq.drop(1).takeWhile(!_.startsWith("*")).mkString("\n")

    AWSPropertyDocsElement(
      name = name,
      description = description,
      required = required
    )
  }
  def parsePropertyDetails(lines: List[(String, Int)]): Seq[AWSPropertyDocsElement] = {

    val start = lines.find(_._1.startsWith(propertyDocsStartKey)).get._2
    val end = lines.find(_._1.startsWith(propertyDocsEndKey)).map(_._2).getOrElse(lines.length)

    val propertyDocsBlock = lines.slice(start + 2, end).map(_._1)
    val blocks = splitBySeq(propertyDocsBlock, x => x == "").map(_.toList)

    blocks.map(parsePropertyBlock).flatMap(_.toOption)

  }
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
      val split = field._1.replace("\"","").replace(" ","").split(":")
      val rawfieldName = split(0)
      val nameCamel = rawfieldName.substring(rawfieldName.indexOf('[') + 1, rawfieldName.indexOf(']'))
      val reference = rawfieldName.substring(rawfieldName.indexOf('(') + 1, rawfieldName.indexOf(')'))

      val typeValue = split.tail.mkString.replace(",","")
      val trueTypeValue = if(typeValue.startsWith("[*")) nameCamel
                          else typeValue
      AWSDocsProperty(
        name = nameCamel,
        fieldType = trueTypeValue,
        reference = reference
      )

    }

    val properties = parsePropertyDetails(lines.toList.sortBy(_._2))

    AWSDocsType(
      file.getName.split('.').head,
      basetype,
      docType,
      scalaAttributes.toList,
      properties
    )
  }

}

object ExampleApp extends App {

  def run(dir: String, resourceName: String) = {
    val parser = new DocsParser(dir)

    val cognitoStructure = parser.docFiles.filter(_.resourceName.contains(resourceName)).map(parser.parseAndMakeTypes)
    val fileToTypeMap = cognitoStructure.flatMap(_.attributes.map(x => x.reference.replace("#cfn-", "") + ".md" -> x.name).toMap).toMap
    val typeNames = cognitoStructure.flatMap(_.attributes.map(_.name))

    val outputfileName = s"${resourceName.capitalize}.scala"

    val writer = new PrintWriter(new File(s"${resourceName.capitalize}.scala"))

    cognitoStructure.toList.foreach(x => writer.println(x.toScala(fileToTypeMap, typeNames)))

    writer.close()
  }

  // directory of raw md docs
  run(s"/aws-cloudformation-user-guide/doc_source", "cognito")
}