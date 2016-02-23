package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.review.model.Statistics;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;

import java.io.IOException;

import static com.jayway.restassured.path.json.JsonPath.from;
import static org.junit.Assert.assertEquals;

public class ReviewMultipropertyCustomerSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_CUSTOMER = "/review/analytics/customer/";

    public ReviewMultipropertyCustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMER);
    }

    public void getCustomerPropertiesMetric(String metric, String customerCode, String since, String until, String granularity, String limit, String cursor) {
        CustomerSteps customerStep = new CustomerSteps();
        Customer c = customerStep.getCustomerByCodeInternal(customerCode);

        Response customerProperties = getSecondLevelEntitiesForDates(c.getCustomerId(), metric, limit, cursor, since, until, granularity, null, null, null);
        setSessionResponse(customerProperties);
    }
}
