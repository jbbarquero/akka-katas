import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.malsolo",
      scalaVersion := "2.12.3",
      version      := "0.0.1-SNAPSHOT"
    )),
    name := "akka-log-processor",
    libraryDependencies += scalaTest % Test
  )
