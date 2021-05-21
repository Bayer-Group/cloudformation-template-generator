sonatypeProfileName := "com.bayer"

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeCredentialHost := "s01.oss.sonatype.org"

sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
publishTo := sonatypePublishToBundle.value

import xerial.sbt.Sonatype._

publishMavenStyle := true

sonatypeProjectHosting := Some(GitHubHosting(user="bayer-group", repository="cloudformation-template-generator", email="brian.rodgers@bayer.com"))
developers := List(
  Developer(id = "bkrodgers", name = "Brian Rodgers", email = "brian.rodgers@bayer.com", url = url("https://github.com/bkrodgers"))
)
licenses := Seq("BSD3" -> url("https://opensource.org/licenses/BSD-3-Clause"))



//sonatypeLogLevel := "DEBUG"
