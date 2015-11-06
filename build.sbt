name := "reactive-influxdb"

scalaVersion := "2.11.7"

Defaults.itSettings

lazy val `it-config-sbt-project` = project.in(file(".")).configs(IntegrationTest)

val akkaStreamsVersion = "1.0"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "it, test",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0" % "it, test"
)
