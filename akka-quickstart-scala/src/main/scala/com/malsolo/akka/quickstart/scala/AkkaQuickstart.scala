package com.malsolo.akka.quickstart.scala

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object Printer {

  final case class Greeting(greeting: String)

  def props(): Props = Props[Printer]
}

class Printer extends Actor with ActorLogging {

  import Printer._

  override def receive = {
    case Greeting(greeting) =>
      log.info(s"Greeting received from ${sender()}: $greeting")
  }
}

object Greeter {

  final case class WhoToGreet(who: String)

  case object Greet

  def props(message: String, printerActor: ActorRef): Props = Props(new Greeter(message, printerActor))
}

class Greeter(message: String, printerActor: ActorRef) extends Actor {

  import Greeter._
  import Printer._

  var greeting = ""

  override def receive = {
    case WhoToGreet(who) =>
      greeting = s"$message, $who"
    case Greet =>
      printerActor ! Greeting(greeting)
  }
}

object AkkaQuickstart extends App {
  import Greeter._

  val system = ActorSystem()

  val printer = system.actorOf(Printer.props())

  val helloGreeter = system.actorOf(Greeter.props("Hello", printer))
  val byeGreeter = system.actorOf(Greeter.props("Good bye", printer))

  helloGreeter ! WhoToGreet("World")
  helloGreeter ! Greet

  byeGreeter ! WhoToGreet("American pie")
  byeGreeter ! Greet

  Thread.sleep(1000)

  system.terminate()

}
