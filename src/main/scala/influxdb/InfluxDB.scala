package influxdb

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpResponse, HttpRequest }
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import influxdb.api.InfluxDBApi
import org.slf4j.LoggerFactory
import scala.concurrent.Future
import scala.concurrent.duration._

object InfluxDB extends InfluxDBApi {

  private val logger = LoggerFactory.getLogger(getClass)

  override implicit val system = ActorSystem("influxdb")
  override implicit val ec = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()

  override val askTimeout: Timeout = config.getInt("influxdb.timeout").seconds

  override lazy val host: String = config.getString("influxdb.host")

  override lazy val port: Int = config.getInt("influxdb.port")

  val connFlow: Flow[HttpRequest, HttpResponse, Future[Http.OutgoingConnection]] =
    Http().outgoingConnection(host, port)

  logger.debug(s"InfluxDB client configured with host: ${host} and port: ${port}")

}
