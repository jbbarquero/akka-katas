name := "akka-katas"
version := "0.0.1-SNAPSHOT"
organization := "com.malsolo"
scalaVersion := "2.12.3"

lazy val log             = project.in(file("akka-log-processor"))
lazy val streaming       = project.in(file("akka-streaming"))
lazy val quickstartScala = project.in(file("akka-quickstart-scala"))
lazy val example         = project.in(file("akka-example"))
lazy val cluster         = project.in(file("akka-cluster"))
