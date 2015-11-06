package influxdb.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.{StreamTcpException, ActorMaterializer}
import akka.stream.scaladsl.{Sink, Source}
import org.slf4j.LoggerFactory

trait InfluxApi {

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  def host: String
  def port: Int

  private val logger = LoggerFactory.getLogger(getClass)

  private val connFlow = Http().cachedHostConnectionPool[String](host = host, port =  port)
  private val connFlow2 = Http().outgoingConnection(host, port)

  def ping = {
    val request = HttpRequest(GET, "/ping")
    Source.single(request)
      .via(connFlow2)
      .runWith(Sink.head)
      .recover {
        case e: StreamTcpException =>
          HttpResponse(
            ServiceUnavailable,
            Nil,
            HttpEntity.empty(ContentTypes.NoContentType),
            HttpProtocols.`HTTP/1.1`
          )
      }
//    Source.single(HttpRequest(GET, "/ping"))
//      .via(connFlow)
//      .runWith(Sink.head)

  }

  def write(): Unit = ???

  def read(): Unit = ???

}
