package travel.snapshot.dp.qa.utils.tripadvisor

import java.util.concurrent.ThreadLocalRandom

object TripAdvisorTraveller extends Enumeration {
  type TripAdvisorTraveller = Value

  val BUSINESS = Value("business")
  val COUPLES = Value("couples")
  val SOLO = Value("solo")
  val FAMILY = Value("family")
  val FRIENDS = Value("friends")

  val seq = values.toSeq
  val size = seq.size

  def getRandom: TripAdvisorTraveller = seq(ThreadLocalRandom.current().nextInt(size))
}
