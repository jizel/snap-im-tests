package travel.snapshot.dp.qa.utils.tripadvisor

import java.util.concurrent.ThreadLocalRandom

object ReviewAnalyticsTravellersContext extends Enumeration {
  type ReviewAnalyticsTravellersContext = Value

  val TRAVELLERS = Value("review/analytics/travellers")
  val OVERALL_BUBBLE_RATING = Value("review/analytics/travellers/overall_bubble_rating")
  val NUMBER_OF_REVIEWS = Value("review/analytics/travellers/number_of_reviews")
  val ASPECT_OF_BUSINESS = Value("review/analytics/travellers/aspects_of_business")

  val seq = values.toSeq
  val size = seq.size

  def getRandom: Value = seq(ThreadLocalRandom.current().nextInt(size))
}
