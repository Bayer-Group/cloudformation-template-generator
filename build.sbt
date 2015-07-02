import AssemblyKeys._
import sbt.Keys._

val _name = "cloud-formation-template-generator"

val _organization = "com.monsanto.arch"

val _version = "1.0.1-SNAPSHOT"

name := _name

organization := _organization

version := _version

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

publishMavenStyle := true

startYear := Some(2014)

/* scala versions and options */
scalaVersion := "2.11.6"

// These options will be used for *all* versions.
scalacOptions ++= Seq(
  "-deprecation"
  ,"-unchecked"
  ,"-encoding", "UTF-8"
  ,"-Xlint"
  // "-optimise"   // this option will slow your build
)

scalacOptions ++= Seq(
  "-Yclosure-elim",
  "-Yinline"
)

// These language flags will be used only for 2.10.x.
scalacOptions <++= scalaVersion map { sv =>
  if (sv startsWith "2.11") List(
    "-Xverify"
    ,"-feature"
    ,"-language:postfixOps"
  )
  else Nil
}

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

val akka = "2.3.9"
val spray = "1.3.2"

/* dependencies */
libraryDependencies ++= Seq (
  "org.scala-lang.modules"     %% "scala-xml"                % "1.0.2"
  ,"com.github.nscala-time"     %% "nscala-time"              % "1.2.0"
  // -- testing --
  , "org.scalatest"             %% "scalatest"                % "2.2.1"  % "test"
  // -- Logging --
  ,"ch.qos.logback"              % "logback-classic"          % "1.1.2"
  ,"com.typesafe.scala-logging" %% "scala-logging-slf4j"      % "2.1.2"
  // -- json --
  ,"io.spray"                   %%  "spray-json"              % "1.3.1"
  // -- config --
  ,"com.typesafe"                % "config"                   % "1.2.1"
  // -- file io --
  ,"org.apache.commons"          % "commons-io"               % "1.3.2"
).map(_.force())

/* avoid duplicate slf4j bindings */
libraryDependencies ~= { _.map(_.exclude("org.slf4j", "slf4j-jdk14")) }

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  "splunk repo" at "http://splunk.artifactoryonline.com/splunk/ext-releases-local",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

// ScalaStyle
org.scalastyle.sbt.ScalastylePlugin.Settings

lazy val testScalaStyle = taskKey[Unit]("testScalaStyle")

testScalaStyle := {
  org.scalastyle.sbt.PluginKeys.scalastyle.toTask("").value
}

//(test in Test) <<= (test in Test) dependsOn testScalaStyle

val testSettings = Seq(
  fork in Test := true
)

testSettings

assemblySettings

test in assembly := {}

fork in run := true
