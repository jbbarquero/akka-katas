package com.malsolo.akka.sample.scala

import java.util.Optional

import akka.actor.{Actor, ActorLogging, Props}

object Device {
  def props(groupId: String, deviceId: String): Props = Props(new Device(groupId, deviceId))

  final case class ReadTemperature(requestId: Long)
  final case class RespondTemperature(requesId: Long, value: Option[Double])
}

class Device(groupId: String, deviceId: String) extends Actor with ActorLogging {

  import Device._

  var lastTemperaturaReading: Option[Double] = None

  override def receive: Receive = {
    case ReadTemperature(id) =>
      sender() ! RespondTemperature(id, lastTemperaturaReading)
  }
}
