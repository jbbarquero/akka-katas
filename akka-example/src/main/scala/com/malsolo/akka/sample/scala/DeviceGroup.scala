package com.malsolo.akka.sample.scala

import akka.actor.{Actor, ActorLogging, Props}

object DeviceGroup {
  def props(groupId: String): Props = Props(new DeviceGroup(groupId))
}

class DeviceGroup(groupId: String) extends Actor with ActorLogging {
  override def receive: Receive = ???
}
