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
import influxdb.model.{ InfluxDBResults, InfluxDBStatus }
import org.slf4j.LoggerFactory
import scala.collection.immutable.Seq
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

  def createdb(): Future[InfluxDBResults] = ???

  def ping: Future[InfluxDBStatus] = {
    for {
      r <- sendGetRequest("/ping")
      headers = r.headers
      h = if (isVersionHeader(headers)) {
        InfluxDBStatus("OK", version(headers, true))
      } else {
        InfluxDBStatus("InfluxDB is Down!", version(headers, false))
      }
    } yield h
  }

  def read(): Unit = ???

  def write(): Unit = ???

  private def sendGetRequest(path: Uri): Future[HttpResponse] = {
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

  private def isVersionHeader(headers: Seq[HttpHeader]): Boolean = {
    headers.exists(h => h.name == "X-Influxdb-Version")
  }

  private def version(headers: Seq[HttpHeader], isHeaderVersion: Boolean): String = {
    if (isHeaderVersion)
      headers.find(h => h.name == "X-Influxdb-Version").get.value().toString
    else
      ""
  }

}
