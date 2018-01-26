package com.malsolo.akka.sample.scala

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{MustMatchers, WordSpecLike}

class DeviceGroupSpec extends TestKit(ActorSystem("devicegroup-test-system"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "be able to register a device actor" in {
    val probe = TestProbe()
    val groupActor = system.actorOf(DeviceGroup.props("group"))

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device1"))
    val deviceActor1 = probe.lastSender

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device2"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device2"))
    val deviceActor2 = probe.lastSender

    deviceActor1 mustNot be (deviceActor2)
  }

  "ignore request for wrong groupId" in {
    val probe = TestProbe()
    val groupActor = system.actorOf(DeviceGroup.props("group"))

    groupActor.tell(DeviceManager.RequrestTrackDevice("wronggroup", "device"), probe.ref)
    probe.expectMsg(DeviceManager.IncorrectRequestTrackDeviceForGroup("wronggroup", "group"))
  }

}
