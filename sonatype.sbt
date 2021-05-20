sonatypeProfileName := "com.monsanto"

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeCredentialHost := "s01.oss.sonatype.org"

sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

import xerial.sbt.Sonatype._

publishMavenStyle := true

sonatypeProjectHosting := Some(GitHubHosting(user="bayer-group", repository="cloudformation-template-generator", email="brian.rodgers@bayer.com"))
developers := List(
  Developer(id = "bkrodgers", name = "Brian Rodgers", email = "brian.rodgers@bayer.com", url = url("https://github.com/bkrodgers"))
)
licenses := Seq("BSD3" -> url("https://opensource.org/licenses/BSD-3-Clause"))



//sonatypeLogLevel := "DEBUG"