package travel.snapshot.dp.qa.utils

import java.util.concurrent.ThreadLocalRandom

object Granularity extends Enumeration {
  type Granularity = Value
  val DAY = Value("day")
  val WEEK = Value("week")
  val MONTH = Value("month")

  val seq = values.toSeq
  val bound = seq.size

  def getRandom: Value = seq(ThreadLocalRandom.current().nextInt(bound))
}
