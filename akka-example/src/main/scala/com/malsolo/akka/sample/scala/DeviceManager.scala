package com.malsolo.akka.sample.scala

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}
import com.malsolo.akka.sample.scala.DeviceManager.RequrestTrackDevice

object DeviceManager {

  final case class RequrestTrackDevice(groupId: String, deviceId: String)
  final case class DeviceRegistered(groupId: String, deviceId: String)
  final case class IncorrectRequestTrackDevice(IncorrectGroupId: String, IncorrectDeviceId: String, managedGroupId: String, managedDeviceId: String)
  final case class IncorrectRequestTrackDeviceForGroup(IncorrectGroupId: String, managedGroupId: String)

}

class DeviceManager extends Actor with ActorLogging {

  var groupIdToActor = Map.empty[String, ActorRef]
  var actorToGroupId = Map.empty[ActorRef, String]

  override def preStart(): Unit = log.info("DeviceManager started")

  override def postStop(): Unit = log.info("DeviceManager stopped")

  override def receive: Receive = {
    case trackMsg @ RequrestTrackDevice(groupId, _) =>
      groupIdToActor.get(groupId) match {
        case Some(ref) =>
          ref forward trackMsg
        case None =>
          log.info("Creating device group actor for {}", groupId)
          val deviceGroup = context.actorOf(DeviceGroup.props(groupId), "group-" + groupId)
          context.watch(deviceGroup)
          actorToGroupId += deviceGroup -> groupId
          groupIdToActor += groupId -> deviceGroup
      }
    case Terminated(groupActor) =>
      val groupId = actorToGroupId(groupActor)
      log.info("Device group actor for {} has been terminated", groupId)
      actorToGroupId -= groupActor
      groupIdToActor -= groupId
  }
}
