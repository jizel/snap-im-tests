package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;

import com.jayway.restassured.specification.RequestSpecification;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.model.review.model.Traveller;
import travel.snapshot.dp.qa.model.review.model.TravellerAspectsOfBusiness;
import travel.snapshot.dp.qa.model.review.model.TravellerNumberOfReviews;
import travel.snapshot.dp.qa.model.review.model.TravellerOverall;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static junit.framework.TestCase.assertEquals;

public class ReviewTravelersSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/analytics/";

    public ReviewTravelersSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    public TravellerOverall[] parseOverallTravelersFromSession() {
        Response response = getSessionResponse();
        String responseAsString = response.getBody().asString();
        return from(responseAsString).getObject("data", TravellerOverall[].class);
    }

    public TravellerAspectsOfBusiness[] parseAspectsTravelersFromSession() {
        Response response = getSessionResponse();
        String responseAsString = response.getBody().asString();
        return from(responseAsString).getObject("data", TravellerAspectsOfBusiness[].class);
    }

    public TravellerNumberOfReviews[] parseReviewsTravelersFromSession() {
        Response response = getSessionResponse();
        String responseAsString = response.getBody().asString();
        return from(responseAsString).getObject("data", TravellerNumberOfReviews[].class);
    }

    public Traveller[] parseTravellersFromSession() {
        Response response = getSessionResponse();
        String responseAsString = response.getBody().asString();
        return from(responseAsString).getObject("data", Traveller[].class);
    }

    public void checkNumberOfAnalyticsReturnedForTravelers(int count) {
        Traveller[] traveller = parseTravellersFromSession();
        for (Traveller aTraveller : traveller) {
            assertEquals(aTraveller.getAspectsOfBusiness().size(), count);
            assertEquals(aTraveller.getNumberOfReviews().size(), count);
            assertEquals(aTraveller.getOverall().size(), count);
        }
    }

    public void checkNumberOfAnalyticsReturnedForTravelersReviews(int count) {
        TravellerNumberOfReviews[] traveller = parseReviewsTravelersFromSession();
        for (TravellerNumberOfReviews aTraveller : traveller) {
            assertEquals(aTraveller.getNumberOfReviews().size(), count);
        }
    }

    public void checkNumberOfAnalyticsReturnedForTravelersAspects(int count) {
        TravellerAspectsOfBusiness[] traveller = parseAspectsTravelersFromSession();
        for (TravellerAspectsOfBusiness aTraveller : traveller) {
            assertEquals(aTraveller.getAspectsOfBusiness().size(), count);
        }
    }

    public void checkNumberOfAnalyticsReturnedForTravelersOverallBubble(int count) {
        TravellerOverall[] traveller = parseOverallTravelersFromSession();
        for (TravellerOverall aTraveller : traveller) {
            assertEquals(aTraveller.getOverall().size(), count);
        }
    }

    public void checkFileAgainstResponseTravellers(String filename) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        Traveller[] expectedStatistics = from(data).getObject("data", Traveller[].class);

        Traveller[] actualStatistics = parseTravellersFromSession();
        for (int i = 0; i < actualStatistics.length; i++) {
            assertEquals(expectedStatistics[i].getType(), actualStatistics[i].getType());
            assertEquals(expectedStatistics[i].getOverall(), actualStatistics[i].getOverall());
            assertEquals(expectedStatistics[i].getNumberOfReviews(), actualStatistics[i].getNumberOfReviews());
            assertEquals(expectedStatistics[i].getAspectsOfBusiness(), actualStatistics[i].getAspectsOfBusiness());
        }
    }

    public void checkFileAgainstResponseTravellersBubbleRating(String filename) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        TravellerOverall[] expectedStatistics = from(data).getObject("data", TravellerOverall[].class);

        Traveller[] actualStatistics = parseTravellersFromSession();
        for (int i = 0; i < actualStatistics.length; i++) {
            assertEquals(expectedStatistics[i].getType(), actualStatistics[i].getType());
            assertEquals(expectedStatistics[i].getOverall(), actualStatistics[i].getOverall());
        }
    }

    public void checkFileAgainstResponseTravellersAspectsOfBusiness(String filename) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        TravellerAspectsOfBusiness[] expectedStatistics = from(data).getObject("data", TravellerAspectsOfBusiness[].class);

        Traveller[] actualStatistics = parseTravellersFromSession();
        for (int i = 0; i < actualStatistics.length; i++) {
            assertEquals(expectedStatistics[i].getType(), actualStatistics[i].getType());
            assertEquals(expectedStatistics[i].getAspectsOfBusiness(), actualStatistics[i].getAspectsOfBusiness());
        }
    }

    public void checkFileAgainstResponseTravellersNumberOfReviews(String filename) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        TravellerNumberOfReviews[] expectedStatistics = from(data).getObject("data", TravellerNumberOfReviews[].class);

        Traveller[] actualStatistics = parseTravellersFromSession();
        for (int i = 0; i < actualStatistics.length; i++) {
            assertEquals(expectedStatistics[i].getType(), actualStatistics[i].getType());
            assertEquals(expectedStatistics[i].getNumberOfReviews(), actualStatistics[i].getNumberOfReviews());
        }
    }

    public void getDataForSpecificTraveler(String url, String traveler, String granularity, String propertyId, String since, String until) {
        RequestSpecification requestSpecification = given().spec(spec);
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);

        if (sinceDate != null) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (untilDate != null) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (granularity != null) {
            requestSpecification.parameter("granularity", granularity);
        }
        if (propertyId != null) {
            requestSpecification.parameter("property", propertyId);
        }
        if (traveler != null) {
            requestSpecification.parameter("traveller", traveler);
        }

        Response response = requestSpecification.when().get(url);
        setSessionResponse(response);
    }
}
