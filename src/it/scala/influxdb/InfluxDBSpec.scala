package influxdb

import influxdb.model.InfluxDBStatus

import scala.concurrent.Await
import scala.concurrent.duration._
import org.scalatest.{FlatSpec, MustMatchers}

import scala.util.{ Success, Failure }

class InfluxDBSpec extends FlatSpec with MustMatchers {

  val timeout = 5.seconds

  "InfluxDB" must "respond to ping" in {
    val maybePing = Await.result(InfluxDB.ping, timeout)
    maybePing mustBe InfluxDBStatus("OK", "0.9.4.1")
  }



}
