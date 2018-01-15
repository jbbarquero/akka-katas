package com.malsolo.akka.sample.scala

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.{MustMatchers, WordSpecLike}

class DeviceSpec extends TestKit(ActorSystem("device-test-system"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "reply with empty reading if no temperature is known" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("groupId", "deviceId"))

    deviceActor.tell(Device.ReadTemperature(42), probe.ref)
    val response = probe.expectMsgType[Device.RespondTemperature]
    response.requestId must be (42)
    response.value must be (None)
  }

  "response with temperature recorded" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))

    deviceActor.tell(Device.RecordTemperature(41, 30), probe.ref)
    val response = probe.expectMsgType[Device.TemperatureRecorded]
    response.requestId must be (41)
  }

  "reply with last temperature reading" in {
    val probe = TestProbe()
    val deviceActor = system.actorOf(Device.props("group", "device"))

    deviceActor.tell(Device.RecordTemperature(1, 24.0), probe.ref)
    probe.expectMsg(Device.TemperatureRecorded(1))

    deviceActor.tell(Device.ReadTemperature(2), probe.ref)
    val response1 = probe.expectMsgType[Device.RespondTemperature]
    response1.requestId must be (2)
    response1.value must be (Some(24.0))
  }

}
