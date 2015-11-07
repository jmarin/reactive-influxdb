package influxdb

import influxdb.model.InfluxDBStatus

import scala.concurrent.Await
import scala.concurrent.duration._
import org.scalatest.{FlatSpec, MustMatchers}

import scala.util.{ Success, Failure }

class InfluxDBSpec extends FlatSpec with MustMatchers {

  val timeout = 5.seconds

  "A Ping request" must "return a response" in {
    val maybePing = Await.result(InfluxDB.ping, timeout)
    maybePing mustBe InfluxDBStatus("OK")
  }

}
