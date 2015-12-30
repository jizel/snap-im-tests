package travel.snapshot.dp.qa.utils.tripadvisor

object ReviewAnalyticsContext extends Enumeration {
  type ReviewAnalyticsContext = Value

  val BASE = Value("review/analytics")
  val OVERALL_BUBBLE_RATING = Value("review/analytics/overall_bubble_rating")
  val NUMBER_OF_REVIEWS = Value("review/analytics/number_of_reviews")
  val ASPECTS_OF_BUSINESS = Value("review/analytics/aspects_of_business")
  val RATING_SCORE = Value("review/analytics/rating_score")
  val POPULARITY_INDEX_RANK = Value("review/analytics/popularity_index_rank")
}
