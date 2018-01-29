package com.malsolo.akka.sample.scala

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import scala.concurrent.duration.FiniteDuration

object DeviceGroupQuery {

  case object CollectionTimeout

  def props(
     actorToDeviceId: Map[ActorRef, String],
     requestId: Long,
     requester: ActorRef,
     timeout: FiniteDuration
   ) : Props = Props(new DeviceGroupQuery(actorToDeviceId, requestId, requester, timeout))

}

class DeviceGroupQuery(
  actorToDeviceId: Map[ActorRef, String],
  requestId: Long,
  requester: ActorRef,
  timeout: FiniteDuration
) extends Actor with ActorLogging {

  import DeviceGroupQuery._
  import context.dispatcher
  val queryTimeoutTimer = context.system.scheduler.scheduleOnce(timeout, self, CollectionTimeout)

  override def preStart(): Unit = {
    actorToDeviceId.keysIterator.foreach { deviceActor =>
      context.watch(deviceActor)
      deviceActor ! Device.ReadTemperature(0)
    }
  }

  override def postStop(): Unit = {
    queryTimeoutTimer.cancel()
  }

  override def receive: Receive = ???

}
