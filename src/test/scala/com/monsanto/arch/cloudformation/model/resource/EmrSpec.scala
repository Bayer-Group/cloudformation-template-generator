package com.monsanto.arch.cloudformation.model.resource

import org.scalatest.{FunSpec, Matchers}
import spray.json.{JsArray, JsObject, JsString, JsonWriter}

class EmrSpec extends FunSpec with Matchers {

  describe("ClusterConfiguration") {
    it("should write non recursive") {
      val clusterConfiguration = ClusterConfiguration(
        Classification = Some("hello"),
        ConfigurationProperties = Some(Map("hello" -> "world")),
        Configurations = None
      )
      val json = implicitly[JsonWriter[ClusterConfiguration]].write(clusterConfiguration)
      json should equal(JsObject(Map(
        "Classification" -> JsString("hello"),
        "ConfigurationProperties" -> JsObject(
          "hello" -> JsString("world")
        )
      )))
    }

    it("should write and read recursive") {
      val clusterConfiguration = ClusterConfiguration(
        Classification = Some("hello"),
        ConfigurationProperties = Some(Map("hello" -> "world")),
        Configurations = Some(Seq(
          ClusterConfiguration(
            Classification = Some("hello1"),
            ConfigurationProperties = Some(Map("hello2" -> "world3")),
            Configurations = None
          )
        ))
      )
      val json = implicitly[JsonWriter[ClusterConfiguration]].write(clusterConfiguration)
      json should equal(JsObject(Map(
        "Classification" -> JsString("hello"),
        "ConfigurationProperties" -> JsObject(
          "hello" -> JsString("world")
        ),
        "Configurations" -> JsArray(
          JsObject(Map(
            "Classification" -> JsString("hello1"),
            "ConfigurationProperties" -> JsObject(
              "hello2" -> JsString("world3")
            )
          ))
        )
      )))
    }
  }

}
