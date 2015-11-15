package influxdb.model

object LineProtocol {

  case class Line(key: Key, fields: Seq[Field], time: Option[Timestamp]) {
    require(fields.nonEmpty, "A Line must contain at least one field")
    override def toString = {
      val timestamp = time.getOrElse("")
      s"${key.toString} ${fields.toString} ${timestamp}".trim
    }
  }
  case class Tag(name: String, value: String) {
    override def toString = {
      s"${name}=${value}".trim
    }
  }

  case class Key(name: String, tags: Seq[Tag]) {
    override def toString = {
      s"${name},${tags.mkString(",")}".trim
    }
  }

  sealed trait FieldValue
  case class IntValue(v: Int) extends FieldValue {
    override def toString = v + "i".toString
  }
  case class FloatValue(v: Double) extends FieldValue {
    override def toString = v.toString
  }
  case class StringValue(s: String) extends FieldValue {
    override def toString = "\"" + s + "\""
  }
  case class BooleanValue(isTrue: Boolean) extends FieldValue {
    override def toString = if (isTrue) "true" else "false"
  }

  case class Field(key: String, value: FieldValue) {
    override def toString = s"${key}=${value}"
  }

  case class Timestamp(now: Long)

  object Timestamp {
    def apply(): Timestamp = {
      val currentMillis = System.currentTimeMillis()
      Timestamp(currentMillis * 1000000)
    }
  }

}
