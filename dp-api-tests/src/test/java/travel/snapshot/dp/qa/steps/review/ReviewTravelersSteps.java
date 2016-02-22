package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.analytics.ReviewSteps;

import java.util.HashMap;
import java.util.Map;


public class ReviewTravelersSteps extends ReviewSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/analytics/";

    public ReviewTravelersSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    @Step
    public void getDataForSpecificTraveler(String url, String traveler, String granularity, String propertyId, String since, String until) {
        Map<String, String> queryParams = new HashMap<>();
        if (propertyId != null) {
            queryParams.put("property", propertyId);
        }
        if (traveler != null) {
            queryParams.put("traveller", traveler);
        }

        Response response = getEntitiesForUrlWihDates(url, null, null, since, until, granularity, queryParams);
        setSessionResponse(response);
    }
}
