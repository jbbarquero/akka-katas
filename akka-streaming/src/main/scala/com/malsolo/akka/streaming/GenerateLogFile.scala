package com.malsolo.akka.streaming

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString

object GenerateLogFile extends App {

  if (args.length != 2) {
    System.err.print("Usage. Provide two args: file-path number-of-lines")
    System.exit(1)
  }

  val filePath = args(0)
  val numberOfLines = args(1).toInt
  val rnd = new java.util.Random()

  val sink = FileIO.toPath(Paths.get(filePath), Set(CREATE, WRITE, APPEND))

  def hostname() = {
    val os = System.getProperty("os.name").toLowerCase

    if (os.indexOf("win") >= 0) System.getenv("COMPUTERNAME") else System.getenv("HOSTNAME")
  }

  def lines(i: Int) = {
    val host = hostname()
    val service = "service"
    val time = ZonedDateTime.now.format(DateTimeFormatter.ISO_INSTANT)
    val state = if (i % 10 == 0) "warning"
      else if (i % 101 == 0) "error"
      else if (i% 1002 == 0) "critical"
      else "ok"
    val description = "description"
    val tag = "tag"
    val metric = rnd.nextDouble() * 100

    s"$host | $service | $state | $time | $description | $tag | $metric \n"
  }

  val graph = Source.fromIterator { () =>
    Iterator.tabulate(numberOfLines)(lines)
  }.map(line => ByteString(line)).toMat(sink)(Keep.right)

  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  graph.run().foreach { result =>
    println(s"Wrote ${result.count} bytes to $filePath")
    system.terminate()
  }

}
