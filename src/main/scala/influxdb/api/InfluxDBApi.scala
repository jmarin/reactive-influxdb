package influxdb.api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.stream.{ StreamTcpException, ActorMaterializer }
import akka.stream.scaladsl.{ Sink, Source, Flow }
import akka.util.{ByteString, Timeout}
import com.typesafe.config.Config
import influxdb.model.LineProtocol.Line
import influxdb.model.{ InfluxDBResults, InfluxDBStatus }
import org.slf4j.LoggerFactory
import scala.collection.immutable.Seq
import scala.concurrent.{ Future, ExecutionContext }

trait InfluxDBApi {

  implicit val askTimeout: Timeout
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val ec: ExecutionContext = system.dispatcher

  val host: String
  val port: Int

  val config: Config

  private val logger = LoggerFactory.getLogger(getClass)

  val connFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]]

  val connectionFlow = Http().outgoingConnection(host, port)

  def createdb(): Future[InfluxDBResults] = ???

  def ping: Future[InfluxDBStatus] = {
    for {
      r <- sendGetRequest("/ping")
      headers = r.headers
      h = if (isVersionHeader(headers)) {
        InfluxDBStatus("OK", version(headers))
      } else {
        InfluxDBStatus("InfluxDB is Down!", version(headers))
      }
    } yield h
  }

  def read(): Unit = ???

  def write(db: String, line: Line): Unit = {
    sendPostRequest(s"/write?db=$db", line.toString)
  }

  private def sendGetRequest(path: Uri): Future[HttpResponse] = {
    val request = HttpRequest(GET, uri = path)
    processRequest(request)
  }

  private def sendPostRequest(path: Uri, data: String): Future[HttpResponse] = {
    val request= HttpRequest(POST, uri = path, entity = ByteString(data))
    processRequest(request)
  }

  private def processRequest(request: HttpRequest): Future[HttpResponse] = {
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

  private def version(headers: Seq[HttpHeader]): String = {
    if (isVersionHeader(headers))
      headers.find(h => h.name == "X-Influxdb-Version").get.value().toString
    else
      ""
  }

}
