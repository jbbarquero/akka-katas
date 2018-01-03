name := "akka-quickstart-scala"

version      := "0.0.1-SNAPSHOT"

organization := "com.malsolo"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val scalaTestVersion = "3.0.3"
  val akkaVersion = "2.5.8"
  Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
  )
}
