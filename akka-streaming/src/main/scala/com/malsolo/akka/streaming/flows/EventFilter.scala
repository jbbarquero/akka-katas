package com.malsolo.akka.streaming.flows

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString

import scala.concurrent.Future

object EventFilter extends App with EventMarshalling {

  if (args.length != 3) {
    System.err.print("Usage. Provide three args: input-file, output-file, event-state-to-filter")
    System.exit(1)
  }

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get(args(0)))
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get(args(1)), Set(CREATE, WRITE, APPEND))
  val filterState = args(2) match {
    case State(state) => state
    case unknown =>
      System.err.print(s"Unknown state $unknown, exiting")
      System.exit(2)
  }





}
