import sbt.Keys._

name := "cloud-formation-template-generator"

organization := "com.bayer"

startYear := Some(2014)

// scala versions and options

scalaVersion := "2.13.6"
crossScalaVersions := Seq("2.11.12", "2.12.13", "2.13.6")
releaseCrossBuild := true

// These options will be used for *all* versions.

def crossVersionScalaOptions(scalaVersion: String) = {
   CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 11)) => Seq(
      "-Yclosure-elim",
      "-Yinline"
    )
    case _ => Nil
  }
}
scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-Xlint",
    "-Xverify",
    "-encoding", "UTF-8",
    "-feature",
    "-language:postfixOps"
  ) ++ crossVersionScalaOptions(scalaVersion.value)

javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")

// dependencies

libraryDependencies ++= Seq (
  // -- testing --
   "org.scalatest"  %% "scalatest"     % "3.0.8"  % Test
  // -- json --
  ,"io.spray"       %%  "spray-json"   % "1.3.6"
  // -- reflection --
  ,"org.scala-lang" %  "scala-reflect" % scalaVersion.value
).map(_.force())

resolvers ++= Seq(
  "spray repo" at "https://repo.spray.io",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

// for sonatype


import ReleaseTransformations._

releaseCrossBuild := true // true if you cross-build the project for multiple Scala versions
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  // For non cross-build projects, use releaseStepCommand("publishSigned")
  releaseStepCommandAndRemaining("+publishSigned"),
  releaseStepCommand("sonatypeBundleRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)


// for ghpages

enablePlugins(GhpagesPlugin, SiteScaladocPlugin)

git.remoteRepo := "git@github.com:bayer-group/cloudformation-template-generator.git"
