package com.malsolo.akka.quickstart.scala

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

object Printer {
  final case class Greeting(greeting: String)

  def props(): Props = Props[Printer]
}

class Printer extends Actor with ActorLogging {
  override def receive: Receive = ???
}

object Greeter {
  final case class WhoToGreet(who: String)
  case object Greet

  def props(message: String, printerActor: ActorRef): Props = Props(new Greeter(message, printerActor))
}

class Greeter(message: String, printerActor: ActorRef) extends Actor {
  override def receive: Receive = ???
}

object AkkaQuickstart extends App {

}
