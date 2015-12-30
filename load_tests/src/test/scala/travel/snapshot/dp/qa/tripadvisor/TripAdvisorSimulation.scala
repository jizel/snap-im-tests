package travel.snapshot.dp.qa.tripadvisor

import io.gatling.core.Predef._
import travel.snapshot.dp.qa.utils.tripadvisor.{ReviewAnalyticsTravellersContext, ReviewAnalyticsContext}

class TripAdvisorSimulation extends AbstractTripAdvisorSimulation {

  runScenario(
    scenario("TripAdvisor analytics").exec(
      GetReviewAnalytics("Returns key review metrics for the property.", ReviewAnalyticsContext.BASE),
      GetReviewAnalytics("Returns overall bubble rating.", ReviewAnalyticsContext.OVERALL_BUBBLE_RATING),
      GetReviewAnalytics("Returns number of reviews.", ReviewAnalyticsContext.NUMBER_OF_REVIEWS),
      GetReviewAnalytics("Returns aspects of business statistics.", ReviewAnalyticsContext.ASPECTS_OF_BUSINESS),
      GetReviewAnalytics("Returns rating score for the given property.", ReviewAnalyticsContext.RATING_SCORE),
      GetReviewAnalytics("Returns popularity index rank.", ReviewAnalyticsContext.POPULARITY_INDEX_RANK),

      GetReviewAnalyticsForTravellers("Returns statistics for random type of traveller", ReviewAnalyticsTravellersContext.TRAVELLERS),
      GetReviewAnalyticsForTravellers("Returns overall bubble rating for random traveller", ReviewAnalyticsTravellersContext.OVERALL_BUBBLE_RATING),
      GetReviewAnalyticsForTravellers("Returns number of reviews for random traveller", ReviewAnalyticsTravellersContext.NUMBER_OF_REVIEWS),
      GetReviewAnalyticsForTravellers("Returns aspects of business statistics for random traveller", ReviewAnalyticsTravellersContext.ASPECT_OF_BUSINESS)
    ))
}
