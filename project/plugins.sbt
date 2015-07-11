addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.5.3")

addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
