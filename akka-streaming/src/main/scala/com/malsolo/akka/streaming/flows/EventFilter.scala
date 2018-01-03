package com.malsolo.akka.streaming.flows

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString
import com.typesafe.config.ConfigFactory

import scala.concurrent.Future

object EventFilter extends App with EventMarshalling {

  if (args.length != 3) {
    System.err.print("Usage. Provide three args: input-file, output-file, event-state-to-filter")
    System.exit(1)
  }

  val config = ConfigFactory.load()
  val maxLine = config.getInt("log-stream-processor.max-line")

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(Paths.get(args(0)))
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get(args(1)), Set(CREATE, WRITE, APPEND))
  val filterState = args(2) match {
    case State(state) => state
    case unknown =>
      System.err.print(s"Unknown state $unknown, exiting")
      System.exit(2)
  }

  val frame: Flow[ByteString, String, NotUsed] = Framing.delimiter(ByteString("/n"), maxLine).map(_.decodeString("UTF8"))

  val parse: Flow[String, Event, NotUsed] = Flow[String].map(LogStreamProcessor.parseLineEx).collect {
    case Some(e) => e
  }

  val filter: Flow[Event, Event, NotUsed] = Flow[Event].filter(_.state == filterState)

  import spray.json._
  val serialize: Flow[Event, ByteString, NotUsed] = Flow[Event].map(event => ByteString(event.toJson.compactPrint))



}
