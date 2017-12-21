package com.malsolo.akka.supervision

import akka.actor.{Actor, ActorLogging}

class LifeCycleHooks extends Actor with ActorLogging {
  override def receive = {
    case msg: AnyRef =>
      log.info(s"Received message $msg, sending it back")
      sender() ! msg
  }
}
