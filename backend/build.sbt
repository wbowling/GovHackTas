EclipseKeys.withSource := true

val libs = Seq(
	"com.typesafe.akka" % "akka-http-experimental_2.11" % "1.0-RC4",
	"com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "1.0-RC4",
	"org.elasticsearch" % "elasticsearch" % "1.6.0",
	"org.apache.commons" % "commons-lang3" % "3.4",
	"com.fasterxml.jackson.core" % "jackson-databind" % "2.4.4"	
)

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "memorylane",
    libraryDependencies ++= libs
  )
