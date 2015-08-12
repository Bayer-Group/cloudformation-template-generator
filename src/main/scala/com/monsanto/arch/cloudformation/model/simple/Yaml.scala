package com.monsanto.arch.cloudformation.model.simple

object Yaml {

  implicit class YamlHelper(val sc: StringContext) extends AnyVal {

    private def build(parts: Seq[String], args: Seq[Any]): Seq[String] = {
      def padding(part: String) = part.length - part.lastIndexOf("\n") - 1

      def pad(part: String, pad: Int) = part.replaceAll("\n", "\n" + " " * pad)

      def readFile(path: String): String = {
        val s = getClass().getResourceAsStream(path)
        val contents: String = io.Source.fromInputStream(s).mkString
        s.close()
        contents
      }

      def resourceExists(resourcePath: String) = {
        resourcePath != null && !resourcePath.isEmpty && getClass().getResourceAsStream(resourcePath) != null
      }

      def paddedContents(part: String, arg: Any) = {
        val toPad = padding(part.stripMargin)
        val contents = arg match {
          case null => ""
          case _ => resourceExists(arg.toString) match {
            case true => readFile(arg.toString)
            case false => arg.toString
          }
        }
        pad(contents, toPad)
      }

      parts.toList match {
        case part :: otherParts => args.toList match {
          case arg :: otherArgs =>
            part.stripMargin +: paddedContents(part, arg) +: build(otherParts, otherArgs)
          case Nil => part.stripMargin +: build(otherParts, Nil)
        }
        case Nil => Nil
      }
    }

    def yaml(args: Any*): String = {
      sc.checkLengths(args)
      build(sc.parts, args).mkString.trim + "\n"
    }
  }

}
