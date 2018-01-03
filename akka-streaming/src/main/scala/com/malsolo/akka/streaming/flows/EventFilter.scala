package com.malsolo.akka.streaming.flows

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Keep, RunnableGraph, Sink, Source}
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

  val sourceFile = Paths.get(args(0))
  val sinkFile = Paths.get(args(1))

  val source: Source[ByteString, Future[IOResult]] = FileIO.fromPath(sourceFile)
  val sink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(sinkFile, Set(CREATE, WRITE, APPEND))
  val filterState = args(2) match {
    case State(state) => state
    case unknown =>
      System.err.print(s"Unknown state $unknown, exiting")
      System.exit(2)
  }

  val frame: Flow[ByteString, String, NotUsed] = Framing.delimiter(ByteString("\n"), maxLine).map(_.decodeString("UTF8"))

  val parse: Flow[String, Event, NotUsed] = Flow[String].map(LogStreamProcessor.parseLineEx).collect {
    case Some(e) => e
  }

  val filter: Flow[Event, Event, NotUsed] = Flow[Event].filter(_.state == filterState)

  import spray.json._
  val serialize: Flow[Event, ByteString, NotUsed] = Flow[Event].map(event => ByteString(event.toJson.compactPrint + "\n"))

  val composedFlow: Flow[ByteString, ByteString, NotUsed] = frame.via(parse).via(filter).via(serialize)

  val runnableGraph: RunnableGraph[Future[IOResult]] = source.via(composedFlow).toMat(sink)(Keep.right)

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  runnableGraph.run().foreach { result =>
    println(s"Wrote ${result.count} bytes from ${sourceFile.toFile.getAbsolutePath} to ${sinkFile.toFile.getAbsolutePath} filtered with $filterState")
    system.terminate()
  }



}
