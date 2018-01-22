package com.malsolo.akka.sample.scala

object DeviceManager {

  final case class RequrestTrackDevice(groupId: String, deviceId: String)
  final case class DeviceRegistered(groupId: String, deviceId: String)
  final case class IncorrectRequestTrackDevice(IncorrectGroupId: String, IncorrectDeviceId: String, managedGroupId: String, managedDeviceId: String)
  final case class IncorrectRequestTrackDeviceForGroup(IncorrectGroupId: String, managedGroupId: String)

}
