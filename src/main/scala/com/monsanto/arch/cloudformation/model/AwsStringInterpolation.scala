package com.monsanto.arch.cloudformation.model

/**
  * Created by Tyler Southwick on 11/20/15.
  */
object AwsStringInterpolation {

  /**
    * Takes a sequence of objects and zips them together such that
    */
  case class Zipper[A](parts: Seq[A]*) extends Traversable[A] {
    override def foreach[U](f: (A) => U): Unit = {
      val i = parts.map(_.iterator)
      while (i.exists(_.hasNext))
        i.foreach { v =>
          if (v.hasNext) {
            f(v.next)
          }
        }
    }
  }

  def apply(sc : StringContext, tokens : Seq[Token[String]]) : Token[String] = {
      if (tokens.isEmpty) {
        sc.parts.mkString("")
      } else {
        val zippedTokens = Zipper(sc.parts.filterNot(_.isEmpty).map(StringToken), tokens).toArray.toSeq
        `Fn::Join`("", zippedTokens)
      }
    }
}
