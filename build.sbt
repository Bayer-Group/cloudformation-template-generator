import sbt.Keys._

name := "cloud-formation-template-generator"

organization := "com.monsanto.arch"

startYear := Some(2014)

// scala versions and options

scalaVersion := "2.12.4"
crossScalaVersions := Seq("2.11.11", "2.12.4")
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
   "org.scalatest"  %% "scalatest"     % "3.0.4"  % Test
  // -- json --
  ,"io.spray"       %%  "spray-json"   % "1.3.4"
  // -- reflection --
  ,"org.scala-lang" %  "scala-reflect" % scalaVersion.value
).map(_.force())

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

// for ghpages

enablePlugins(GhpagesPlugin, SiteScaladocPlugin)

git.remoteRepo := "git@github.com:MonsantoCo/cloudformation-template-generator.git"

// for bintray

bintrayOrganization := Some("monsanto")

licenses += ("BSD", url("http://opensource.org/licenses/BSD-3-Clause"))

bintrayReleaseOnPublish := ! isSnapshot.value

publishTo := Def.taskDyn[Option[Resolver]] {
  if (isSnapshot.value)
    Def.task(Some("Artifactory Realm" at "https://oss.jfrog.org/oss-snapshot-local/"))
  else
    Def.task(publishTo.value) /* Value set by bintray-sbt plugin */
}.value

credentials := Def.taskDyn[Seq[Credentials]] {
  if (isSnapshot.value)
    Def.task(List(Path.userHome / ".bintray" / ".artifactory").filter(_.exists).map(Credentials(_)))
  else
    Def.task(credentials.value) /* Value set by bintray-sbt plugin */
}.value
