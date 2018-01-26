package com.malsolo.akka.sample.scala

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import com.malsolo.akka.sample.scala.DeviceManager.IncorrectRequestTrackDeviceForGroup
import com.malsolo.akka.sample.scala.DeviceManager.RequrestTrackDevice

object DeviceGroup {
  def props(groupId: String): Props = Props(new DeviceGroup(groupId))
}

class DeviceGroup(groupId: String) extends Actor with ActorLogging {

  var deviceIdToActor = Map.empty[String, ActorRef]
  var actorToDeviceId = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("Device group {} started", groupId)

  override def postStop(): Unit = log.info("Device group {} stopped", groupId)

  override def receive: Receive = {
    case trackMsg @ RequrestTrackDevice(`groupId`, _) =>
      deviceIdToActor.get(trackMsg.deviceId) match {
        case Some(deviceActor) =>
          deviceActor forward trackMsg
        case None =>
          log.info("Creating device actor for {}", trackMsg.deviceId)
          val deviceACtor = context.actorOf(Device.props(groupId, trackMsg.deviceId), s"device-${trackMsg.deviceId}")
          deviceIdToActor += trackMsg.deviceId -> deviceACtor
          deviceACtor forward trackMsg
      }
    case RequrestTrackDevice(groupId, deviceId) =>
      log.warning("Ignoring TrackDevice request for {}. This actor is responsible for {}.", groupId, this.groupId)
      sender() ! IncorrectRequestTrackDeviceForGroup(IncorrectGroupId = groupId, managedGroupId = this.groupId)
    case Terminated(deviceActor) =>
      val deviceActorId = actorToDeviceId.get(deviceActor)
      log.info("Device actor for {} has been terminated", deviceActorId)
      actorToDeviceId -= deviceActor
      deviceIdToActor -= deviceActorId.get
  }
}
