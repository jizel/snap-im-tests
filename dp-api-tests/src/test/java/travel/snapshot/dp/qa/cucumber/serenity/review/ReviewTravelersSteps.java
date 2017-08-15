package travel.snapshot.dp.qa.cucumber.serenity.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ReviewTravelersSteps extends ReviewSteps {

    private static final String BASE_PATH_LOCATIONS = "/review/analytics/";

    public ReviewTravelersSteps() {
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_LOCATIONS);
    }

    @Step
    public void getDataForSpecificTraveler(String url, String traveler, String granularity, UUID propertyId, String since, String until) {
        Map<String, String> queryParams = new HashMap<>();
        if (propertyId != null) {
            queryParams.put("property", propertyId.toString());
        }
        if (traveler != null) {
            queryParams.put("traveller", traveler);
        }

        Response response = getEntitiesForUrlWihDates(url, null, null, since, until, granularity, queryParams);
        setSessionResponse(response);
    }
}
