package com.malsolo.akka.streaming.flows

import java.time.ZonedDateTime

case class Event(
  host: String,
  service: String,
  state: State,
  time: ZonedDateTime,
  description: String,
  tag: Option[String] = None,
  metric: Option[Double] = None
)

sealed trait State
case object Critical extends State
case object Error  extends State
case object Ok extends State
case object Warning extends State

object State {
  def norm(str: String): String = str.toLowerCase
  def norm(state: State): String = norm(state.toString)

  val ok = norm(Ok)
  val warning = norm(Warning)
  val error = norm(Error)
  val critical = norm(Critical)

  def unapply(str: String): Option[State] = {
    val normalized = norm(str)
    if(normalized == norm(Ok)) Some(Ok)
    else if(normalized == norm(Warning)) Some(Warning)
    else if(normalized == norm(Error)) Some(Error)
    else if(normalized == norm(Critical)) Some(Critical)
    else None
  }
}

case class LogReceipt(logId: String, written: Long)
case class ParseError(logId: String, msg: String)

