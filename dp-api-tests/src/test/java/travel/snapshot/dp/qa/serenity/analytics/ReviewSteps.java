package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.seleniumhq.jetty7.util.ajax.JSON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.review.model.AspectsOfBusiness;
import travel.snapshot.dp.qa.model.review.model.NumberOfReviewsStats;
import travel.snapshot.dp.qa.model.review.model.OverallBubbleRatingStats;
import travel.snapshot.dp.qa.model.review.model.PopularityIndexRank;
import travel.snapshot.dp.qa.model.review.model.RatingScore;
import travel.snapshot.dp.qa.model.review.model.Statistics;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.assertEquals;

public class ReviewSteps extends AnalyticsBaseSteps {

    public ReviewSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
    }

    @Step
    public void checkBubbleRatingStats(List<Float> expectedValues) {
        OverallBubbleRatingStats actualOverStat = getOverallBubbleRatingStatsFromResponse();
        assertEquals(actualOverStat.getValues(), expectedValues);
    }

    private OverallBubbleRatingStats getOverallBubbleRatingStatsFromResponse() {
        Response response = getSessionResponse();

        ArrayList responseValues = response.path("values");
        OverallBubbleRatingStats overStat = new OverallBubbleRatingStats();
        overStat.setValues(responseValues);
        return overStat;
    }


    public void checkNumberOfReviewsStats(List<Integer> expectedValues) {
        NumberOfReviewsStats actualReviews = getNumberOfReviewsStatsFromResponse();
        assertEquals(actualReviews.getValues(), expectedValues);
    }


    private NumberOfReviewsStats getNumberOfReviewsStatsFromResponse() {
        Response response = getSessionResponse();

        ArrayList responseValues = response.path("values");
        NumberOfReviewsStats overStat = new NumberOfReviewsStats();
        overStat.setValues(responseValues);
        return overStat;
    }


    public AspectsOfBusiness[] getAspectOfBusinessFromResponse() {
        Response response = getSessionResponse();
        AspectsOfBusiness[] aspects = from(response.getBody().asString()).getObject("values", AspectsOfBusiness[].class);
        return aspects;
    }

    public void checkNumberOfAnalyticsReturned(int count) {
        Statistics statistics = getAnalyticsFromResponse();

        assertEquals(statistics.getAspectsOfBusiness().size(), count);
        assertEquals(statistics.getAspectsOfBusiness().size(), count);
        assertEquals(statistics.getRatingScore().size(), count);
        assertEquals(statistics.getOverallBubbleRating().size(), count);
        assertEquals(statistics.getNumberOfReviews().size(), count);
    }

    private Statistics getAnalyticsFromResponse() {
        Response response = getSessionResponse();
        String data = response.getBody().asString();

        Statistics statistics = new Statistics();

        PopularityIndexRank[] popularity = from(data).getObject("popularity_index_rank", PopularityIndexRank[].class);
        AspectsOfBusiness[] aspects = from(data).getObject("aspects_of_business", AspectsOfBusiness[].class);
        RatingScore[] ratings = from(data).getObject("rating_score", RatingScore[].class);

        HashMap<String, Double> parsed = (HashMap) JSON.parse(data);
        Object[] overBubble = (Object[]) (Object) parsed.get("overall_bubble_rating");
        Object[] reviews = (Object[]) (Object) parsed.get("number_of_reviews");

        List<Double> bubble = new ArrayList<Double>();
        List<Integer> parsedNumberOfReviews = new ArrayList<Integer>();

        //can be use same for amount of returned values is always same
        for (int i = 0; i < ((Object[]) overBubble).length; i++) {
            bubble.add((Double) overBubble[i]);

            if (reviews[i] != null) {
                parsedNumberOfReviews.add(Integer.valueOf(reviews[i].toString()));
            } else {
                parsedNumberOfReviews.add(null);
            }
        }

        statistics.setPopularityIndexRank(Arrays.asList(popularity));
        statistics.setAspectsOfBusiness(Arrays.asList(aspects));
        statistics.setRatingScore(Arrays.asList(ratings));
        statistics.setOverallBubbleRating(bubble);
        statistics.setNumberOfReviews(parsedNumberOfReviews);
        return statistics;
    }

    public void checkFileAgainstResponseForAnalytics(String filename) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        Statistics expectedStatistics = from(data).getObject("", Statistics.class);

        Statistics currentStatistics = getAnalyticsFromResponse();
        assertEquals(expectedStatistics, currentStatistics);
    }

    public void checkFileAgainstResponseAspectsOfBusiness(String filename) throws Exception {
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        AspectsOfBusiness[] expected = from(data).getObject("values", AspectsOfBusiness[].class);

        AspectsOfBusiness[] actual = getAspectOfBusinessFromResponse();
        assertEquals(expected, actual);
    }

    public void checkNumberOfValuesReturnedForEachProperty(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        ArrayList propertyValues = response.jsonPath().get("properties.values");
        for (Object property : propertyValues) {
            assertEquals(((ArrayList) property).size(), count);
        }
    }

    public void checkFileAgainstResponse(String filename) throws IOException {
        String expected = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        expected = expected.replaceAll("\r|\n|\t| ", "");

        String actual = getSessionResponse().getBody().asString();
        assertEquals(expected, actual);
    }
}
