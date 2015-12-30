package travel.snapshot.dp.qa.utils

import java.util.concurrent.ThreadLocalRandom

object WebPerformanceReferralsSortKey extends Enumeration {
  type WebPerformanceReferralsSortKey = Value
  val SITE = Value("site")
  val VISITS = Value("visits")
  val VISITS_UNIQUE = Value("visits_unique")
  val REVENUE = Value("revenue")

  val seq = values.toSeq
  val bound = seq.size - 1

  def getRandom: Value = seq(ThreadLocalRandom.current().nextInt(bound))
}