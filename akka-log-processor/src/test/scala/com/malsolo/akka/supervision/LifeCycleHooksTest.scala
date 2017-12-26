package com.malsolo.akka.supervision

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

class LifeCycleHooksTest extends TestKit(ActorSystem("LifeCycleHooksTest")) with WordSpecLike with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    system terminate
  }

  "The Child" must {
    "log lifecycle hooks" in {
      val testActorRef = system.actorOf(Props[LifeCycleHooks], "LifeCycleHooks")
      watch(testActorRef)
      testActorRef ! "restart"
      testActorRef.tell("hello", testActor)
      expectMsg("hello")
      system.stop(testActorRef)
      expectTerminated(testActorRef)
    }
  }

}
