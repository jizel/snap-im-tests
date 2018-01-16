package travel.snapshot.dp.qa.cucumber.serenity.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.cucumber.serenity.analytics.AnalyticsBaseSteps;

import java.util.UUID;

public class ReviewMultipropertyCustomerSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_CUSTOMER = "/review/analytics/customer/";

    public ReviewMultipropertyCustomerSteps() {
        spec.baseUri(propertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMER);
    }

    @Step
    public void getCustomerPropertiesMetric(String metric, UUID customerId, String since, String until, String granularity, String limit, String cursor) {
        Response customerProperties = getSecondLevelEntitiesForDates(customerId, metric, limit, cursor, since, until, granularity, null, null, null);
        setSessionResponse(customerProperties);
    }
}
