package com.malsolo.akka.gettingstarted.scala

import akka.actor.{Actor, ActorSystem, Props}

class SupervisedActor extends Actor {

  override def preStart(): Unit = println("supervised actor started")

  override def postStop(): Unit = println("supervised actor stopped")

  override def receive: Receive = {
    case "fail" =>
      println("Supervised actor fails now")
      throw new Exception("I failed!")
  }
}

class SupervisingActor extends Actor {

  val child = context.actorOf(Props[SupervisedActor], "supervised-actor")

  override def receive: Receive = {
    case "failChild" => child ! "fail"
  }
}

object FailureHandling extends App {
  val system = ActorSystem("test-failurehandling")
  val supervising = system.actorOf(Props[SupervisingActor], "supervising-actor")
  supervising ! "failChild"

  Thread.sleep(1000)
  system.terminate()

}
