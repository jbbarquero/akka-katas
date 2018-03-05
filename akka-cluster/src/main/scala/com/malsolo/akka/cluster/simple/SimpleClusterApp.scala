package com.malsolo.akka.cluster.simple

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object SimpleClusterApp extends App {

  if (args.isEmpty)
    startup(Seq("2551", "2552", "2553", "0"))
  else
    startup(args)

  def startup(ports: Seq[String]) = {
    ports foreach { port =>
      val config = ConfigFactory.parseString(s"""
        akka.remote.netty.tcp.port=$port
        """)
        .withFallback(ConfigFactory.load())

      val system = ActorSystem("SimpleClusterSystem", config)
    }
  }

}
