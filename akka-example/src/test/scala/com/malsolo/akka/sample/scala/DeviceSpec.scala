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
    response.requesId must be (42)
    response.value must be (None)


  }

}
