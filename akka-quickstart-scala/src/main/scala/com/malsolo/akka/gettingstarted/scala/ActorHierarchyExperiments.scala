package com.malsolo.akka.gettingstarted.scala

import akka.actor.{Actor, ActorSystem, Props}

import scala.io.StdIn

class PrintMyActorRefActor extends Actor {
  override def receive = {
    case "printit" =>
      val secondRef = context.actorOf(Props.empty, "second-actor")
      println(s"Second: ${secondRef}")
  }
}

object  ActorHierarchyExperiments extends App {
  val system = ActorSystem("testSystem")

  val firstRef = system.actorOf(Props[PrintMyActorRefActor], "first-actor")
  println(s"First: ${firstRef}")

  firstRef ! "printit"

  Thread.sleep(1000)
  println(">>> Press ENTER to exit <<<")
  try StdIn readLine finally system terminate

}
