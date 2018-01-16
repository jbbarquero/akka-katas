package com.malsolo.akka.sample.scala

object DeviceManager {

  final case class RequrestTrackDevice(groupId: String, deviceId: String)
  final case class DeviceRegistered(groupId: String, deviceId: String)
  final case class DeviceAlreadyRegistered(groupId: String, deviceId: String)

}
