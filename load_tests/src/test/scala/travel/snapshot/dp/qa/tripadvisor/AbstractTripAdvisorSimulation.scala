package travel.snapshot.dp.qa.tripadvisor

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._
import org.apache.http.HttpStatus
import travel.snapshot.dp.qa.{AbstractIntegrationsSimulation, AbstractSimulation}
import travel.snapshot.dp.qa.utils.{AbstractRequest, Granularity, QueryUtils}
import travel.snapshot.dp.qa.utils.tripadvisor.ReviewAnalyticsContext
import travel.snapshot.dp.qa.utils.tripadvisor.ReviewAnalyticsTravellersContext
import travel.snapshot.dp.qa.utils.tripadvisor.TripAdvisorTraveller

abstract class AbstractTripAdvisorSimulation extends AbstractIntegrationsSimulation {

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
  object GetReviewAnalytics extends AbstractRequest {
    def apply(requestMessage: String, context: ReviewAnalyticsContext.Value, granularity: Granularity.Value = Granularity.DAY, range: Integer = 20): ChainBuilder =
      request(requestMessage, context, granularity, range)

    def request(request: String, context: ReviewAnalyticsContext.Value, granularity: Granularity.Value, range: Int): ChainBuilder = exec(getRequest(
      request,
      None,
      s"$context?${randomUtils.randomSinceUntil(range)}&granularity=$granularity",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  /**
    * Encapsulates requests for endpoints with suffixes
    *
    * analytics/travellers
    * analytics/travellers/overall_bubble_rating
    * analytics/travellers/number_of_reviews
    * analytics/travellers/aspects_of_business
    */
  object GetReviewAnalyticsForTravellers extends AbstractRequest {

    def apply(requestMessage: String,
              context: ReviewAnalyticsTravellersContext.Value,
              granularity: Granularity.Value = Granularity.DAY,
              range: Integer = 20,
              travellerFilter: TripAdvisorTraveller.Value = null): ChainBuilder =
      request(requestMessage, context, granularity, range, travellerFilter)

    def request(request: String,
                context: ReviewAnalyticsTravellersContext.Value,
                granularity: Granularity.Value,
                range: Int,
                travellerFilter: TripAdvisorTraveller.Value): ChainBuilder = exec(getRequest(
      request,
      None,
      s"$context?${randomUtils.randomSinceUntil(range)}&granularity=$granularity&${if (travellerFilter != null) s"&traveller=$travellerFilter" else ""}",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetLocations extends AbstractRequest {
    def apply(requestMessage: String,
              limit: Integer = 50,
              cursor: Integer = 0,
              filter: String = null,
              sort: String = "location_id"): ChainBuilder =
      request(requestMessage, limit, cursor, filter, sort)

    def request(request: String, limit: Integer, cursor: Integer, filter: String, sort: String): ChainBuilder = exec(getRequest(
      request,
      Option(new QueryUtils().buildAdditionalQueries(filter, sort, cursor, limit)),
      s"review/locations?",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

  object GetLocationProperties extends AbstractRequest {
    def apply(request: String): ChainBuilder = exec(getRequest(
      request,
      None,
      s"review/locations/${randomUtils.getRandomLocationId}/properties",
      HttpStatus.SC_OK
    ).header("X-Property", integrationsPropertyId))
  }

}
