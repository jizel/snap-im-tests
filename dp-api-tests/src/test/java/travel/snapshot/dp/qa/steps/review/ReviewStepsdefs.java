package travel.snapshot.dp.qa.steps.review;


import cucumber.api.PendingException;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.review.model.*;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.review.model.*;
import travel.snapshot.dp.qa.serenity.analytics.ReviewSteps;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;


public class ReviewStepsdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private ReviewSteps reviewSteps;

    @Steps
    private ReviewLocationSteps reviewLocationSteps;

    @Steps
    private ReviewTravelersSteps reviewTravelersSteps;

    @Steps
    private ReviewMultipropertyCustomerSteps reviewMultipropertyCustomerSteps;

    @Steps
    private ReviewMultipropertyPropertySetSteps reviewMultipropertyPropertySetSteps;

    @Steps
    private ReviewMultipropertySinglePropertySteps reviewMultipropertySinglePropertySteps;

    @Given("^Set access token for review steps defs$")
    public void setAccessTokenForReviewStepsDefs() throws Throwable {
        reviewSteps.setAccessTokenParamFromSession();
        reviewLocationSteps.setAccessTokenParamFromSession();
        reviewMultipropertyCustomerSteps.setAccessTokenParamFromSession();
        reviewMultipropertyPropertySetSteps.setAccessTokenParamFromSession();
        reviewMultipropertySinglePropertySteps.setAccessTokenParamFromSession();
    }

    @When("^Get trip advisor \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void Get_social_media_data_with_granularity_for_since_until(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        reviewSteps.getPropertiesWithDate("/review" + url, granularity, propertyId, since, until);
    }

    @Then("^Response of bubble rating contains \"([^\"]*)\" values$")
    public void response_of_bubble_rating_contains_values(List<Float> values) throws Throwable {
        reviewSteps.checkBubbleRatingStats(values);
    }


    @When("^Get trip advisor \"([^\"]*)\" with missing property header$")
    public void get_trip_advisor_with_missing_property_header(String url) throws Throwable {
        reviewSteps.getPropertiesWithDate("/review" + url, "day", null, null, null);
    }

    @Then("^Response of number of reviews contains \"([^\"]*)\" values$")
    public void response_of_number_of_reviews_contains_values(List<Integer> values) throws Throwable {
        reviewSteps.checkNumberOfReviewsStats(values);
    }


    @Then("^Response contains (\\d+) number of analytics$")
    public void response_contains_count_number_of_analytics(int count) throws Throwable {
        reviewSteps.checkNumberOfAnalyticsReturned(count);
    }

    @When("^List of trip advisor locations \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void list_of_trip_advisor_locations_for_property_id_is_got_with_limit_and_cursor(String url,
                                                                                            @Transform(NullEmptyStringConverter.class) String limit,
                                                                                            @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {

        reviewSteps.getPropertiesWithPaging("/review" + url, "", limit, cursor);

    }

    @When("^List of locations is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_locations_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                 @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                 @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                 @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                 @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        reviewLocationSteps.listOfLocationsIsGot(limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are \"([^\"]*)\" locations returned$")
    public void There_are_returned_locations_returned(String count) throws Throwable {
        reviewLocationSteps.numberOfEntitiesInResponse(Location.class, Integer.valueOf(count));
    }

    @Then("^There are locations with following name returned in order: \"([^\"]*)\"$")
    public void thereAreLocationsWithFollowingNameReturnedInOrder(List<String> names) throws Throwable {
        reviewLocationSteps.locationNamesAreInResponseInOrder(names);
    }

    @When("^Get trip advisor \"([^\"]*)\" for \"([^\"]*)\"$")
    public void getTripAdvisorFor(String url, String property) throws Throwable {
        reviewSteps.getPropertiesWithPaging("review" + url, property, null, null);
    }

    @When("^List of location properties is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\" for location id \"([^\"]*)\"$")
    public void listOfLocationPropertiesIsGotWithLimitAndCursorAndFilterAndSortAndSort_descForLocationId(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                         @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                         @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                         @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                         @Transform(NullEmptyStringConverter.class) String sortDesc,
                                                                                                         String configurationType) throws Throwable {
        reviewLocationSteps.listOfLocationPropertiesIsGot(limit, cursor, filter, sort, sortDesc, configurationType);
    }

    @Then("^There are properties for location id are returned in order: \"([^\"]*)\"$")
    public void thereArePropertiesForLocationIdAreReturnedInOrder(List<String> ids) throws Throwable {
        reviewLocationSteps.locationPropertiesAreInResponseInOrder(ids);
    }

    @Then("^There are \"([^\"]*)\" location properties returned$")
    public void thereAreLocationPropertiesReturned(String count) throws Throwable {
        reviewLocationSteps.numberOfEntitiesInResponse(Property.class, Integer.valueOf(count));
    }

    @Then("^Response contains (\\d+) number of analytics for travelers$")
    public void responseContainsNumberOfAnalyticsForTravelers(int count) throws Throwable {
        reviewTravelersSteps.checkAnalyticsReturnedForType(t -> assertThat(t.getData(), everyItem(hasProperty("numberOfReviews", hasSize(count)))), TravellersOverallStatisticsDto.class);
        reviewTravelersSteps.checkAnalyticsReturnedForType(t -> assertThat(t.getData(), everyItem(hasProperty("aspectsOfBusiness", hasSize(count)))), TravellersOverallStatisticsDto.class);
        reviewTravelersSteps.checkAnalyticsReturnedForType(t -> assertThat(t.getData(), everyItem(hasProperty("overall", hasSize(count)))), TravellersOverallStatisticsDto.class);
    }

    @When("^Get trip advisor travellers \"([^\"]*)\" data with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getTripAdvisorTravellersDataWithGranularityForSinceUntil(String url, String granularity, String propertyId, String since, String until) throws Throwable {
        reviewTravelersSteps.getPropertiesWithDate(url, granularity, propertyId, since, until);
    }

    @Then("^Response contains (\\d+) number of analytics for travelers number of reviews$")
    public void responseContainsCountNumberOfAnalyticsForTravelersNumberOfReviews(int count) throws Throwable {
        reviewTravelersSteps.checkAnalyticsReturnedForType(t -> assertThat(t.getData(), everyItem(hasProperty("numberOfReviews", hasSize(count)))), TravellersOverallBubbleRatingStatsDto.class);
    }

    @Then("^Response contains (\\d+) number of analytics for travelers for aspect of business$")
    public void responseContainsCountNumberOfAnalyticsForTravelersForAspectOfBusiness(int count) throws Throwable {
        reviewTravelersSteps.checkAnalyticsReturnedForType(t -> assertThat(t.getData(), everyItem(hasProperty("aspectsOfBusiness", hasSize(count)))), TravellersAspectsOfBusinessStatsDto.class);
    }

    @Then("^Response contains (\\d+) number of analytics for travelers overall bubble rating$")
    public void responseContainsCountNumberOfAnalyticsForTravelersOverallBubbleRating(int count) throws Throwable {
        reviewTravelersSteps.checkAnalyticsReturnedForType(t -> assertThat(t.getData(), everyItem(hasProperty("overall", hasSize(count)))), TravellersOverallBubbleRatingStatsDto.class);
    }

    @Then("^Review file \"([^\"]*)\" is equals to previous response for analytics$")
    public void reviewFileIsEqualsToPreviousResponse(String filename) throws Throwable {
        reviewSteps.checkFileAgainstResponseForAnalytics("/messages/review" + filename);
    }

    @Then("^Review file \"([^\"]*)\" is equals to previous response for aspects of business$")
    public void reviewFileIsEqualsToPreviousResponseForAspectsOfBusiness(String filename) throws Throwable {
        reviewSteps.checkFileAgainstResponseAspectsOfBusiness("/messages/review" + filename);
    }

    @Then("^Review travellers file \"([^\"]*)\" is equals to previous response$")
    public void reviewTravellersFileIsEqualsToPreviousResponse(String filename) throws Throwable {
        String path = "/messages/review/travellers" + filename;
        reviewTravelersSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getData().sort((t1, t2) -> t1.getType().compareTo(t2.getType()));
            u.getData().sort((u1, u2) -> u1.getType().compareTo(u2.getType()));
            assertThat(t, is(u));
        }, TravellersOverallStatisticsDto.class);
    }

    @Then("^Review travellers file \"([^\"]*)\" is equals to previous response for bubble rating$")
    public void reviewTravellersFileIsEqualsToPreviousResponseForBubbleRating(String filename) throws Throwable {
        String path = "/messages/review/travellers" + filename;
        reviewTravelersSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getData().sort((t1, t2) -> t1.getType().compareTo(t2.getType()));
            u.getData().sort((u1, u2) -> u1.getType().compareTo(u2.getType()));
            assertThat(t, is(u));
        }, TravellersOverallBubbleRatingStatsDto.class);
    }

    @Then("^Review travellers file \"([^\"]*)\" is equals to previous response for acpects of business$")
    public void reviewTravellersFileIsEqualsToPreviousResponseForAcpectsOfBusiness(String filename) throws Throwable {
        String path = "/messages/review/travellers" + filename;
        reviewTravelersSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getData().sort((t1, t2) -> t1.getType().compareTo(t2.getType()));
            u.getData().sort((u1, u2) -> u1.getType().compareTo(u2.getType()));
            assertThat(t, is(u));
        }, TravellersAspectsOfBusinessStatsDto.class);
    }

    @Then("^Review travellers file \"([^\"]*)\" is equals to previous response for number of reviews$")
    public void reviewTravellersFileIsEqualsToPreviousResponseForNumberOfReviews(String filename) throws Throwable {
        String path = "/messages/review/travellers" + filename;
        reviewTravelersSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getData().sort((t1, t2) -> t1.getType().compareTo(t2.getType()));
            u.getData().sort((u1, u2) -> u1.getType().compareTo(u2.getType()));
            assertThat(t, is(u));
        }, TravellersNumberOfReviewsStatsDto.class);
    }

    @When("^Get \"([^\"]*)\" for list of properties for customer \"([^\"]*)\" with since \"([^\"]*)\" until \"([^\"]*)\" granularity \"([^\"]*)\" limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void getForListOfPropertiesForCustomerWithSinceUntilAndGranularity(String metric, String customerCode,
                                                                              @Transform(NullEmptyStringConverter.class) String since,
                                                                              @Transform(NullEmptyStringConverter.class) String until,
                                                                              @Transform(NullEmptyStringConverter.class) String granularity,
                                                                              @Transform(NullEmptyStringConverter.class) String limit,
                                                                              @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        reviewMultipropertyCustomerSteps.getCustomerPropertiesMetric(metric, customerCode, since, until, granularity, limit, cursor);
    }


    @When("^Get \"([^\"]*)\" for single property \"([^\"]*)\" with since \"([^\"]*)\" until \"([^\"]*)\" and granularity \"([^\"]*)\"$")
    public void getForSinglePropertyWithSinceUntilAndGranularity(String metric, String property_id,
                                                                 @Transform(NullEmptyStringConverter.class) String since,
                                                                 @Transform(NullEmptyStringConverter.class) String until,
                                                                 @Transform(NullEmptyStringConverter.class) String granularity) throws Throwable {
        reviewMultipropertySinglePropertySteps.getStatisticsForSingleProperty(metric, property_id, since, until, granularity);
    }

    @When("^Get \"([^\"]*)\" for statistics agregated for property set \"([^\"]*)\" for customer \"([^\"]*)\" with since \"([^\"]*)\" until \"([^\"]*)\" granularity \"([^\"]*)\" limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void getForStatisticsAgregatedForPropertySetForCustomerWithSinceUntilGranularityLimitAndCursor(String metric, String pSetCode, String customer,
                                                                                                          @Transform(NullEmptyStringConverter.class) String since,
                                                                                                          @Transform(NullEmptyStringConverter.class) String until,
                                                                                                          @Transform(NullEmptyStringConverter.class) String granularity,
                                                                                                          @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                          @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        reviewMultipropertyPropertySetSteps.getAggregatedStatisticsForPropertySet(metric, pSetCode, customer, since, until, granularity, limit, cursor);
    }

    @Then("^Response properties contains \"([^\"]*)\" values$")
    public void responsePropertiesContainsValues(int count) throws Throwable {
        reviewSteps.checkNumberOfValuesReturnedForEachProperty(count);
    }

    @When("^Get trip advisor travellers \"([^\"]*)\" data for \"([^\"]*)\" with \"([^\"]*)\" granularity for \"([^\"]*)\" since \"([^\"]*)\" until \"([^\"]*)\"$")
    public void getTripAdvisorTravellersDataForWithGranularityForSinceUntil(String url, String traveler, String granularity, String propertyId, String since, String until) throws Throwable {
        reviewTravelersSteps.getDataForSpecificTraveler(url, traveler, granularity, propertyId, since, until);
    }


    @And("^Response contains correct granularity \"([^\"]*)\" between \"([^\"]*)\" and \"([^\"]*)\"$")
    public void responseContainsCorrectGranularityBetweenAnd(String granularity, String since, String until) throws Throwable {
        reviewSteps.responseContainsCorrectValuesFor(granularity, since, until);
    }

    @When("^Get review \"([^\"]*)\" data with \"([^\"]*)\" granularity with since \"([^\"]*)\" until \"([^\"]*)\" limit \"([^\"]*)\" and cursor \"([^\"]*)\"$")
    public void getReviewDataWithGranularityWithSinceUntilLimitAndCursor(String url,
                                                                         @Transform(NullEmptyStringConverter.class) String granularity,
                                                                         @Transform(NullEmptyStringConverter.class) String since,
                                                                         @Transform(NullEmptyStringConverter.class) String until,
                                                                         @Transform(NullEmptyStringConverter.class) String limit,
                                                                         @Transform(NullEmptyStringConverter.class) String cursor) throws Throwable {
        reviewSteps.getReviewAnalyticsData("/review" + url, granularity, since, until, limit, cursor);
    }

    @Then("^Review file \"([^\"]*)\" equals to previous response for popularity index$")
    public void reviewFileEqualsToPreviousResponseForPopularityIndex(String filename) throws Throwable {
        String path = "/messages/review" + filename;
        reviewSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getProperties().sort((t1, t2) -> t1.getPropertyId().compareTo(t2.getPropertyId()));
            u.getProperties().sort((u1, u2) -> u1.getPropertyId().compareTo(u2.getPropertyId()));
            assertThat(t, is(u));
        }, PopularityIndexRankStatsDto.class);
    }

    @Then("^Review file \"([^\"]*)\" equals to previous response for aspects of business$")
    public void reviewFileEqualsToPreviousResponseForAspectsOfBusiness(String filename) throws Throwable {
        String path = "/messages/review" + filename;
        reviewSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getProperties().sort((t1, t2) -> t1.getPropertyId().compareTo(t2.getPropertyId()));
            u.getProperties().sort((u1, u2) -> u1.getPropertyId().compareTo(u2.getPropertyId()));
            assertThat(t, is(u));
        }, AspectsOfBusinessStatsDto.class);
    }

    @Then("^Review file \"([^\"]*)\" equals to previous response for number of reviews$")
    public void reviewFileEqualsToPreviousResponseForNumberOfReviews(String filename) throws Throwable {
        String path = "/messages/review" + filename;
        reviewSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getProperties().sort((t1, t2) -> t1.getPropertyId().compareTo(t2.getPropertyId()));
            u.getProperties().sort((u1, u2) -> u1.getPropertyId().compareTo(u2.getPropertyId()));
            assertThat(t, is(u));
        }, NumberOfReviewsStatsDto.class);
    }

    @Then("^Review file \"([^\"]*)\" equals to previous response for overall bubble rating$")
    public void reviewFileEqualsToPreviousResponseForOverallBubbleRating(String filename) throws Throwable {
        String path = "/messages/review" + filename;
        reviewSteps.checkFileAgainstResponse(path, (t, u) -> {
            t.getProperties().sort((t1, t2) -> t1.getPropertyId().compareTo(t2.getPropertyId()));
            u.getProperties().sort((u1, u2) -> u1.getPropertyId().compareTo(u2.getPropertyId()));
            assertThat(t, is(u));
        }, OverallBubbleRatingStatsDto.class);

    }

    @Then("^Review file \"([^\"]*)\" is equals to previous response for rating score$")
    public void reviewFileIsEqualsToPreviousResponseForRatingScore(String filename) throws Throwable {
        String path = "/messages/review" + filename;
        reviewSteps.checkFileAgainstResponse(path, (t, u) -> {
            assertThat(t, is(u));
        }, RatingScoreStatsDto.class);
    }
}
