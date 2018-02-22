package com.malsolo.akka.sample.scala

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{MustMatchers, WordSpecLike}

class DeviceGroupQuerySpec extends TestKit(ActorSystem("devicegroupquery-test-system"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {



}
