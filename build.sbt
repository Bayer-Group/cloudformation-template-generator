import sbt.Keys._

name := "cloud-formation-template-generator"

organization := "com.monsanto.arch"

startYear := Some(2014)

// scala versions and options

scalaVersion := "2.11.7"

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

// dependencies

libraryDependencies ++= Seq (
  // -- testing --
   "org.scalatest"  %% "scalatest"     % "2.2.1"  % "test"
  // -- json --
  ,"io.spray"       %%  "spray-json"   % "1.3.2"
  // -- reflection --
  ,"org.scala-lang" %  "scala-reflect" % scalaVersion.value
).map(_.force())

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io",
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

// for ghpages

site.settings

site.includeScaladoc()

ghpages.settings

git.remoteRepo := "git@github.com:MonsantoCo/cloudformation-template-generator.git"

// for bintray

bintrayOrganization := Some("monsanto")

licenses += ("BSD", url("http://opensource.org/licenses/BSD-3-Clause"))

bintrayReleaseOnPublish := ! isSnapshot.value

publishTo := {
  if (isSnapshot.value)
    Some("Artifactory Realm" at "https://oss.jfrog.org/oss-snapshot-local/")
  else
    publishTo.value /* Value set by bintray-sbt plugin */
}

credentials := {
  if (isSnapshot.value)
    List(Path.userHome / ".bintray" / ".artifactory").filter(_.exists).map(Credentials(_))
  else
    credentials.value /* Value set by bintray-sbt plugin */
}
