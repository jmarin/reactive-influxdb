package influxdb.model

import influxdb.model.LineProtocol._
import org.scalatest.{ MustMatchers, FlatSpec }

class LineProtocolSpec extends FlatSpec with MustMatchers {

  "A Tag" should "serialize to string representation" in {
    val tag = Tag("host", "server01")
    tag.toString mustBe "host=server01"
  }

  "A Key" should "serialize to string representation" in {
    val tag1 = Tag("host", "server01")
    val tag2 = Tag("region", "us-east")
    val tag3 = Tag("os", "linux")
    val tags = Seq(tag1, tag2, tag3)
    val key = Key("cpu", tags)
    key.toString mustBe "cpu,host=server01,region=us-east,os=linux"
  }

  "A Line" must "not have empty fields" in {
    val tag = Tag("host", "server01")
    val key = Key("cpu", Seq(tag))
    val badLine = intercept[Exception] {
      Line(key, Nil, None)
    }
    assert(badLine.getMessage.trim === "requirement failed: A Line must contain at least one field")
  }

  "A Field" must "serialize int values to line protocol" in {
    val v = IntValue(2)
    v.toString mustBe "2i"
  }

  it must "serialize string values to line protocol" in {
    val s = StringValue("this is a value")
    s.toString mustBe "\"" + "this is a value" + "\""
  }

  it must "serialize float values to line protocol" in {
    val f1 = FloatValue(1.0)
    f1.toString mustBe "1.0"
    val f2 = FloatValue(2)
    f2.toString mustBe "2.0"
  }

  it must "serialize boolean values to line protocol" in {
    val b = BooleanValue(true)
    b.toString mustBe "true"
    val f = BooleanValue(false)
    f.toString mustBe "false"
  }

  it must "serialize key, value pair" in {
    val key1 = "temperature"
    val value1 = 37.2
    val field1 = Field(key1, FloatValue(value1))
    field1.toString mustBe "temperature=37.2"
  }

}
