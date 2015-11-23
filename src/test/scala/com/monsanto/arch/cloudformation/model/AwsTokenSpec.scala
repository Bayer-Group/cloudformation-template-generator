package com.monsanto.arch.cloudformation.model

import com.monsanto.arch.cloudformation.model.AwsStringInterpolation.Zipper
import org.scalatest.{Matchers, FunSpec}

class AwsTokenSpec extends FunSpec with Matchers {

  it("should generate simple string if no expressions") {
    val fun = aws"test"

    fun shouldEqual StringToken("test")
  }

  it("should join ParameterRef tokens") {
    val param = ParameterRef(StringParameter("that"))
    val fun = aws"test$param"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      param
    )))
  }

  it("should join Parameter") {
    val param = StringParameter("that")
    val fun = aws"test$param"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      ParameterRef(param)
    )))
  }

  it("should join multiple Parameters") {
    val param1 = StringParameter("that")
    val param2 = StringParameter("this")
    val param3 = StringParameter("these")
    val fun = aws"test$param1${param2}hello$param3"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      ParameterRef(param1),
      ParameterRef(param2),
      StringToken("hello"),
      ParameterRef(param3)
    )))
  }

  it("should join Fn::GetAtt ref tokens") {
    val getAtt = `Fn::GetAtt`(Seq("that"))
    val fun = aws"test${getAtt}something"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      getAtt,
      StringToken("something")
    )))
  }
  it("should join tokens") {
    val getAtt : Token[String] = `Fn::GetAtt`(Seq("that"))
    val fun = aws"test${getAtt}something"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      getAtt,
      StringToken("something")
    )))
  }

  it("should optimize join tokens") {
    val getAtt : Token[String] = `Fn::GetAtt`(Seq("that"))
    val test1 = "test1"
    val test2 = "test2"
    val fun = aws"test$getAtt${test1}something$test2"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      getAtt,
      StringToken(s"${test1}something$test2")
    )))
  }

  describe("zipper") {
    it("should combine unevent lists") {
      val zipper = Zipper(Seq("a", "b", "c"), Seq("d", "e"), Seq("f"))
      zipper.toSeq shouldEqual Seq(
        "a", "d", "f", "b", "e", "c"
      )
    }
  }
}
