package com.monsanto.arch.cloudformation.model

import java.io.File

import org.scalatest.{Matchers, FunSpec}
import spray.json._

/**
 * Created by djdool on 6/30/15.
 */
class VPCWriter_UT extends FunSpec with Matchers {

  case class Currency(value: Int)
  object Currency extends DefaultJsonProtocol {
    implicit val format: RootJsonFormat[Currency] = jsonFormat1(Currency.apply)
  }

  describe("VPCWriter") {
    it("creates the file with proper contents under target") {

      val actualFile = new File("target/scala-test/test.json")
      try{
        val rich = Currency(1000000)

        object MyVPCWriter extends VPCWriter

        MyVPCWriter.jsonToFile("test.json", "scala-test", rich)

        assert(actualFile.exists())

        val actual = io.Source.fromFile(actualFile).getLines().mkString("").parseJson

        actual should be( rich.toJson )
      } finally {
        actualFile.delete()
        actualFile.getParentFile.delete()
      }
    }
  }
}
