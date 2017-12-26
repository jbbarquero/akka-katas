package com.malsolo.akka.supervision

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout

import scala.util.{Failure, Success}

class LifeCycleHooks extends Actor with ActorLogging {

  println("Constructor")

  override def preStart(): Unit = println("preStart")

  override def postStop(): Unit = println("postStop")

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("preRestart")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("postRestart")
    super.postRestart(reason)
  }

  override def receive = {
    case "restart" =>
      throw new IllegalStateException("Force restart")
    case msg: AnyRef =>
      log.info(s"Received message $msg, sending it back")
      sender() ! msg
  }
}

object LifeCycleHooksMain extends App {

  import scala.concurrent.duration._
  import akka.pattern.ask
  import scala.concurrent.ExecutionContext.Implicits.global

  val system = ActorSystem("LifeCycleHooks")
  val lifeCycleHooks = system.actorOf(Props[LifeCycleHooks], "lifeCycleHooks")

  lifeCycleHooks ! "restart"

  implicit val timeout = Timeout(1 second)
  val future = lifeCycleHooks ? "HELLO"
  future.onComplete {
    case Success(msg) => println(s"Received back $msg")
    case Failure(e) => e printStackTrace
  }

  Thread.sleep(100)

  system.terminate()

}
