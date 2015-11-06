package influxdb

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import influxdb.api.InfluxApi


object InfluxDB extends InfluxApi {

  override implicit val system = ActorSystem("influxdb")
  override implicit val materializer = ActorMaterializer()

  val config = ConfigFactory.load()

  override lazy val host: String = config.getString("influxdb.host")

  override lazy val port: Int = config.getInt("influxdb.port")



}
