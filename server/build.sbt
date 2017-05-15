name := "server"

version := "1.0.0-SNAPSHOT"

libraryDependencies ++= Seq(cache)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "org.scalaj" %% "scalaj-http" % "2.3.0"
)
