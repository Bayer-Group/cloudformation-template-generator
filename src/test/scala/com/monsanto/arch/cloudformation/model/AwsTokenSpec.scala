package com.monsanto.arch.cloudformation.model

import org.scalatest.{Matchers, FunSpec}

class AwsTokenSpec extends FunSpec with Matchers {

  it("should generate simple string if no expressions") {
    val fun = aws"test"

    fun shouldEqual StringToken("test")
  }

  it("should join parameter ref tokens") {
    val param = ParameterRef(StringParameter("that"))
    val fun = aws"test$param"

    fun shouldEqual FunctionCallToken(`Fn::Join`("", Seq(
      StringToken("test"),
      param
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

  describe("zipper") {
    it("should combine unevent lists") {
      val zipper = Zipper(Seq("a", "b", "c"), Seq("d", "e"), Seq("f"))
      zipper.toSeq shouldEqual Seq(
        "a", "d", "f", "b", "e", "c"
      )
    }
  }
}
