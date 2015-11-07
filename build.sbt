name := "reactive-influxdb"

scalaVersion := "2.11.7"

Defaults.itSettings

scalariformSettings

lazy val `reactive-influxdb` = project.in(file(".")).configs(IntegrationTest)

val akkaStreamsVersion = "2.0-M1"

val configVersion = "1.3.0"

val slf4jVersion = "1.7.12"

val logbackVersion = "1.1.3"

val scalatestVersion = "2.2.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamsVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamsVersion,
  "com.typesafe" % "config" % configVersion,
  "org.slf4j" % "slf4j-api" % slf4jVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "it, test"
)
