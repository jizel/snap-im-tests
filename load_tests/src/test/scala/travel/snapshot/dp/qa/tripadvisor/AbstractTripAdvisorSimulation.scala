package travel.snapshot.dp.qa.tripadvisor

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import travel.snapshot.dp.qa.AbstractSimulation
import travel.snapshot.dp.qa.utils.{QueryUtils, Granularity}
import travel.snapshot.dp.qa.utils.tripadvisor.ReviewAnalyticsContext
import travel.snapshot.dp.qa.utils.tripadvisor.ReviewAnalyticsTravellersContext
import travel.snapshot.dp.qa.utils.tripadvisor.TripAdvisorTraveller

abstract class AbstractTripAdvisorSimulation extends AbstractSimulation {

  /**
    * Encapsulates requests for endpoints with suffixes
    *
    * analytics
    * analytics/overall_bubble_rating
    * analytics/number_of_reviews
    * analytics/aspect_of_business
    * analytics/rating_score
    * analytics/popularity_index_rank
    */
  object GetReviewAnalytics {
    def apply(requestMessage: String, context: ReviewAnalyticsContext.Value, granularity: Granularity.Value = Granularity.DAY, range: Integer = 20) =
      request(requestMessage, context, granularity, range)

    def request(request: String, context: ReviewAnalyticsContext.Value, granularity: Granularity.Value, range: Int) = exec(http(request)
      .get(session => s"$context?access_token=$accessTokenParam&${randomUtils.randomSinceUntil(range)}&granularity=$granularity")
      .header("X-Property", session => randomUtils.randomTripAdvisorPropertyId)
      .check(status.is(200)))
  }

  /**
    * Encapsulates requests for endpoints with suffixes
    *
    * analytics/travellers
    * analytics/travellers/overall_bubble_rating
    * analytics/travellers/number_of_reviews
    * analytics/travellers/aspects_of_business
    */
  object GetReviewAnalyticsForTravellers {

    def apply(requestMessage: String,
              context: ReviewAnalyticsTravellersContext.Value,
              granularity: Granularity.Value = Granularity.DAY,
              range: Integer = 20,
              travellerFilter: TripAdvisorTraveller.Value = null) =
      request(requestMessage, context, granularity, range, travellerFilter)

    def request(request: String,
                context: ReviewAnalyticsTravellersContext.Value,
                granularity: Granularity.Value,
                range: Int,
                travellerFilter: TripAdvisorTraveller.Value = null) =
      exec(http(request).get(session => {

        val traveller = if (travellerFilter != null) s"&traveller=$travellerFilter" else ""

        s"$context?access_token=$accessTokenParam&${randomUtils.randomSinceUntil(range)}&granularity=$granularity$traveller"
      })
        .header("X-Property", session => randomUtils.randomTripAdvisorPropertyId)
        .check(status.is(200)))
  }

  object GetLocations {
    def apply(requestMessage: String,
              limit: Integer = 50,
              cursor: Integer = 0,
              filter: String = null,
              sort: String = "location_id") =
      request(requestMessage, limit, cursor, filter, sort)

    def request(request: String, limit: Integer, cursor: Integer, filter: String, sort: String) =
      exec(http(request).get(session => {

        val additionalQueries = new QueryUtils().buildAdditionalQueries(filter, sort, cursor, limit)

        s"review/locations?access_token=$accessTokenParam$additionalQueries"
      })
        .header("X-Property", session => randomUtils.randomTripAdvisorPropertyId)
        .check(status.is(200)))
  }

  object GetLocationProperties {
    def apply(request: String) =
      exec(http(request).get(session => {
        s"review/locations/${randomUtils.getRandomLocationId}/properties?access_token=$accessTokenParam"
      }).check(status.is(200)))
  }

}
