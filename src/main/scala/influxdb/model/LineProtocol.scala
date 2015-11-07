package influxdb.model

object LineProtocol {

  case class Line(key: Key, fields: Seq[Field], time: Option[Timestamp]) {
    override def toString = {
      val timestamp = time.getOrElse("")
      s"${key.toString} ${fields.toString} ${timestamp}".trim
    }
  }
  case class Tag(name: String, value: String) {
    override def toString = {
      s"${name}=${value}"
    }
  }

  case class Key(name: String, tags: Seq[Tag]) {
    override def toString = {
      s"${name},${tags.mkString(",")}"
    }
  }

  case class Field()

  case class Timestamp(now: Long)

  object Timestamp {
    def apply(): Timestamp = {
      val currentMillis = System.currentTimeMillis()
      Timestamp(currentMillis * 1000000)
    }
  }

}
