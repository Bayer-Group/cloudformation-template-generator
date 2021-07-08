package com.monsanto.arch.cloudformation.model

/**
  * Created by Tyler Southwick on 11/20/15.
  */
object AwsStringInterpolation {

  /**
    * Takes a sequence of objects and zips them together such that
    */
  case class Zipper[A](parts: Seq[A]*) extends Iterable[A] {

    override def iterator: Iterator[A] =
      if (parts.isEmpty)
        Seq[A]().iterator
      else  
        parts.iterator.flatMap(_.headOption) ++ Zipper(parts.flatMap(p => p.headOption.map(_ => p.tail)): _*).iterator
  }

  def apply(sc: StringContext, tokens: Seq[Token[String]]): Token[String] = {
    val zippedTokens = Zipper(sc.parts.map(StringToken), tokens).toArray.toSeq.filterNot {
      case StringToken(s) if s.isEmpty => true
      case _ => false
    }
    val optimizedTokens = zippedTokens.foldLeft(Seq[Token[String]]()) { case (seq, token) =>
      (token, seq.headOption) match {
        case (StringToken(newToken), Some(StringToken(previousToken))) => Seq(StringToken(previousToken + newToken)) ++ seq.tail
        case _ => Seq(token) ++ seq
      }
    }.reverse

    if (optimizedTokens.size == 1) {
      optimizedTokens.head
    } else {
      `Fn::Join`("", optimizedTokens)
    }
  }
}
