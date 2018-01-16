package travel.snapshot.dp.qa.serenity.review;

import com.jayway.restassured.response.Response;

import net.thucydides.core.annotations.Step;

import java.util.ArrayList;

import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import static org.junit.Assert.assertEquals;

public class ReviewSteps extends AnalyticsBaseSteps {

    public ReviewSteps() {
        spec.baseUri(propertiesHelper.getProperty(REVIEW_BASE_URI));
    }

    @Step
    public void checkNumberOfValuesReturnedForEachProperty(int count) {
        Response response = getSessionResponse();
        ArrayList propertyValues = response.jsonPath().get("properties.values");
        for (Object property : propertyValues) {
            assertEquals(((ArrayList) property).size(), count);
        }
    }

    @Step
    public void getReviewAnalyticsData(String url, String granularity, String since, String until, String limit, String cursor) {
        Response aggregatedPropertySet = getEntitiesForUrlWihDates(url, limit, cursor, since, until, granularity, null);
        setSessionResponse(aggregatedPropertySet);
    }
}
