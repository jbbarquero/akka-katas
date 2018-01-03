name := "akka-log-processor"

version      := "0.0.1-SNAPSHOT"

organization := "com.malsolo"

scalaVersion := "2.12.3"

libraryDependencies ++= {
  val scalaTestVersion = "3.0.3"
  val akkaActorVersion = "2.5.8"
  Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "com.typesafe.akka" %% "akka-actor" % akkaActorVersion,
    "com.typesafe.akka" %% "akka-testkit" % akkaActorVersion % Test
  )
}
