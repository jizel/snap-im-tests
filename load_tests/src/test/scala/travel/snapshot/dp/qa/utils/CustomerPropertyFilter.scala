package travel.snapshot.dp.qa.utils

object CustomerPropertyFilter extends Enumeration {
  type CustomerFilter = Value
  val RELATIONSHIP_ID = Value("relationship_id")
  val PROPERTY_NAME = Value("property_name")
  val PROPERTY_ID = Value("property_id")
  val TYPE = Value("type")
  val VALID_FROM = Value("valid_from")
  val VALID_TO = Value("valid_to")

  val seq = values.toSeq

  def getRandom: Array[CustomerPropertyFilter.Value] = getRandom(1)

  def getRandom(count: Integer): Array[CustomerPropertyFilter.Value] = scala.util.Random.shuffle(seq).take(count).toArray
}
