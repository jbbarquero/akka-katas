package com.malsolo.akka.supervision

package dbstrategy1 {

  import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
  import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}

  object LogProcessing extends App {
    val sources = Vector("file:///source1", "file:///source2")
    val databseurl = "http://mydatabase1"

    val system = ActorSystem("logprocessing1")

    system.actorOf(LogProcessingSupervisor.props(sources, databseurl), LogProcessingSupervisor.name)



  }

  class LogProcessingSupervisor(sources: Vector[String], databaseUrl: String) extends Actor with ActorLogging {

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: CorruptedFileException => Resume
      case _: DbBrokenConnectionException => Restart
      case _: DiskError => Stop
    }

    override def receive = ???
  }

  object LogProcessingSupervisor {
    def props(sources: Vector[String], databaseUrl: String) = Props(new LogProcessingSupervisor(sources, databaseUrl))
    def name = "file-watcher-supervisor"
  }
}
