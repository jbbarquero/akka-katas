package com.malsolo.akka.cluster.simple

import akka.actor.{Actor, ActorLogging}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._

class SimpleClusterListener extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  override def receive: Receive = {
    case MemberUp(member) =>
      log.info("***** Member is UP: {}", member)
    case UnreachableMember(member) =>
      log.info("***** Member detected as unreacheable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("***** Member is removed: {} after {}", member, previousStatus)
    case MemberExited(member) =>
      log.info("***** Member EXITED: {}", member)
    case ReachableMember(member) =>
      log.info("***** Member detected as reacheable: {}", member)
    case s: CurrentClusterState =>
      log.info("***** Cluster state: {}", s)
  }
}
