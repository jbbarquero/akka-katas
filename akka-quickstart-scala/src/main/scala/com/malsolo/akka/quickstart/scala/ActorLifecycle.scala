package com.malsolo.akka.quickstart.scala

import akka.actor.{Actor, ActorSystem, Props}

class StartStopActor2 extends Actor {

  override def preStart(): Unit = {
    println(s"Second started: $self")
  }

  override def postStop(): Unit = {
    println(s"Second stopped")
  }

  override def receive: Receive = Actor.emptyBehavior
}

class StartStopActor1 extends Actor {

  override def preStart(): Unit = {
    println(s"First started: $self")
    context.actorOf(Props[StartStopActor2], "second")
  }

  override def postStop(): Unit = {
    println("First stopped")
  }

  override def receive: Receive = {
    case "stop" => context stop self
  }
}

object ActorLifecycle extends App {
  val system = ActorSystem("test-lifecycle")
  val first = system.actorOf(Props[StartStopActor1])
  first ! "stop"

  Thread.sleep(1000)
  system.terminate()

}
