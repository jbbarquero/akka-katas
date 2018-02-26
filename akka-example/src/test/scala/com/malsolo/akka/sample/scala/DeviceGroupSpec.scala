package com.malsolo.akka.sample.scala

import akka.actor.{ActorSystem, PoisonPill}
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

  "return same actor for same deviceId" in {
    val probe = TestProbe()
    val groupActor = system.actorOf(DeviceGroup.props("group"))

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device1"))
    val deviceActor1 = probe.lastSender

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device1"))
    val deviceActor2 = probe.lastSender

    deviceActor1 must be (deviceActor2)
  }

  "be able to list active devices" in {

    //GIVEN
    val probe = TestProbe()
    val groupActor = system.actorOf(DeviceGroup.props("group"))

    //AND
    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device1"))

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device2"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device2"))

    //WHEN
    groupActor.tell(DeviceGroup.RequestDeviceList(1), probe.ref);

    //THEN
    probe.expectMsg(DeviceGroup.ReplyDeviceList(1, Set("device1", "device2")))
  }

  "be able to list active devices after one shuts down" in {
    //GIVEN
    val probe = TestProbe()
    val groupActor = system.actorOf(DeviceGroup.props("group"))

    //AND
    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device1"))
    val toShutDown = probe.lastSender

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device2"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device2"))

    //WHEN
    groupActor.tell(DeviceGroup.RequestDeviceList(0), probe.ref)

    //THEN
    probe.expectMsg(DeviceGroup.ReplyDeviceList(0, Set("device1", "device2")))

    //AND WHEN
    probe.watch(toShutDown)
    toShutDown ! PoisonPill
    probe.expectTerminated(toShutDown)

    //THEN
    probe.awaitAssert {
      groupActor.tell(DeviceGroup.RequestDeviceList(1), probe.ref)
      probe.expectMsg(DeviceGroup.ReplyDeviceList(1, Set("device2")))
    }

  }

  "be able to collect temperatures from all active devices" in {
    val probe = TestProbe()
    val groupActor = system.actorOf(DeviceGroup.props("group"))

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device1"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device1"))
    val deviceActor1 = probe.lastSender

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device2"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device2"))
    val deviceActor2 = probe.lastSender

    groupActor.tell(DeviceManager.RequrestTrackDevice("group", "device3"), probe.ref)
    probe.expectMsg(DeviceManager.DeviceRegistered("group", "device3"))
    val deviceActor3 = probe.lastSender

    // Check that the device actors are working
    deviceActor1.tell(Device.RecordTemperature(requestId = 1, value = 1.0), probe.ref)
    probe.expectMsg(Device.TemperatureRecorded(requestId = 1))
    deviceActor2.tell(Device.RecordTemperature(requestId = 2, value = 2.0), probe.ref)
    probe.expectMsg(Device.TemperatureRecorded(requestId = 2))
    // No temperature for device3

    groupActor.tell(DeviceGroup.RequestAllTemperatures(requestId = 0), probe.ref)
    probe.expectMsg(
      DeviceGroup.RespondAllTemperatures(
        requestId = 0,
        temperatures = Map(
          "device1" -> DeviceGroup.Temperature(1.0),
          "device2" -> DeviceGroup.Temperature(2.0),
          "device3" -> DeviceGroup.TemperatureNotAvailable
        )
      )
    )

  }

}
