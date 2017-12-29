package com.malsolo.akka.streaming

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, RunnableGraph, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future

object StreamingCopy extends App {

  if (args.length != 2) {
    System.err.print("Usage. Provide two args: input-file output-file")
    System.exit(1)
  }

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get(args(0)))
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get(args(1)), Set(CREATE, WRITE, APPEND))
  val runnableGraph: RunnableGraph[Future[IOResult]] = source.to(sink)

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  runnableGraph.run().foreach { result =>
    println(s"${result.status}, ${result.count} bytes read")
    system.terminate
  }



}
