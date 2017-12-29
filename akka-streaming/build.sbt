name := "akka-streaming"

version      := "0.0.1-SNAPSHOT"

organization := "com.malsolo"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val scalaTestVersion = "3.0.3"
  val akkaVersion = "2.5.8"
  val sprayJsonVersion = "1.3.3"
  Seq(
    "org.scalatest" %% "scalatest" % "3.0.3" % Test,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "io.spray" %%  "spray-json" % sprayJsonVersion
  )
}
