package influxdb.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.stream.{ StreamTcpException, ActorMaterializer }
import akka.stream.scaladsl.{ Sink, Source, Flow }
import akka.util.Timeout
import com.typesafe.config.Config
import influxdb.model.InfluxDBStatus
import org.slf4j.LoggerFactory
import scala.concurrent.{ Future, ExecutionContext }

trait InfluxDBApi {

  implicit val askTimeout: Timeout
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val ec: ExecutionContext

  val host: String
  val port: Int

  val config: Config

  private val logger = LoggerFactory.getLogger(getClass)

  val connFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]

  def ping: Future[InfluxDBStatus] = {
    sendGetRequest("/ping").flatMap { response =>
      val headers = response.headers
      if (headers.exists(h => h.name == "X-Influxdb-Version")) {
        Future.successful(InfluxDBStatus("OK"))
      } else {
        Future.successful(InfluxDBStatus("INFLUXDB IS DOWN"))
      }
    }
  }

  def write(): Unit = ???

  def read(): Unit = ???

  def sendGetRequest(path: Uri): Future[HttpResponse] = {
    implicit val ec: ExecutionContext = system.dispatcher
    val connectionFlow = Http().outgoingConnection(host, port)
    val request = HttpRequest(GET, path)
    Source.single(request).via(connectionFlow).runWith(Sink.head).recover {
      case e: StreamTcpException =>
        HttpResponse(
          ServiceUnavailable,
          Nil,
          HttpEntity.empty(ContentTypes.NoContentType),
          HttpProtocols.`HTTP/1.1`
        )
    }
  }

}
