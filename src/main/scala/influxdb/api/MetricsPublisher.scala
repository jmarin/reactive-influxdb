package influxdb.api

import akka.actor.Actor.Receive
import akka.actor.Props
import akka.stream.actor.ActorPublisher
import influxdb.model.LineProtocol.Line
import org.slf4j.LoggerFactory

object MetricsPublisher {
  def props: Props = Props[MetricsPublisher]
}

class MetricsPublisher(bufferSize: Int) extends ActorPublisher[Line] {

  private val logger = LoggerFactory.getLogger(getClass)

  val MaxBufferSize = bufferSize
  var buf = Vector.empty[Line]


  override def receive: Receive = {
    case line: Line =>


    case _ =>
      logger.warn("This message doesn't conform to the Line Protocol")
  }
}
