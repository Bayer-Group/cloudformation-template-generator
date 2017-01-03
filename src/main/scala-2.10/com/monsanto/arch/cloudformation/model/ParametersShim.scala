package com.monsanto.arch.cloudformation.model

import spray.json.{DefaultJsonProtocol, JsValue, JsonFormat}

import spray.json._

object ParameterFormatExt {
  object format {
    def orElse(f :  PartialFunction[Parameter, JsValue]) = f
  }

  object inputParameters {
    def orElse(f : PartialFunction[Parameter, InputParameter]) = f
  }
}
