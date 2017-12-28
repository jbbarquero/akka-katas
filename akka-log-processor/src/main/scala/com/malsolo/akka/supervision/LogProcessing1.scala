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

  class DbCon(url: String) {
    /**
      * Writes a map to a database.
      * @param map the map to write to the database.
      * @throws DbBrokenConnectionException when the connection is broken. It might be back later
      * @throws DbNodeDownException when the database Node has been removed from the database cluster. It will never work again.
      */
    def write(map: Map[Symbol, Any]): Unit =  {
      map.foreach(s => println(s"Storing ${s._1} ${s._2}"))
    }

    def close(): Unit = {
      println("Closing connection")
    }
  }

  class DbWriter(databaseUrl: String) extends Actor {
    val connection  = new DbCon(databaseUrl)

    override def receive = ???
  }


}
