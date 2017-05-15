logLevel := Level.Warn

addSbtPlugin("com.eed3si9n" %% "sbt-assembly" % "0.10.2")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += Classpaths.sbtPluginReleases

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" %% "sbt-plugin" % "2.5.9")

