import sbt._

object Dependencies {
  val scalaTestVersion = "3.0.3"
  val akkaActorVersion = "2.5.8"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.5.8"
  lazy val akkaActorTest = "com.typesafe.akka" %% "akka-testkit" % "2.5.8"
}
