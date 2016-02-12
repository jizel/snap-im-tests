package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;

public class ReviewMultipropertyCustomerSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_CUSTOMER = "/review/analytics/customer/";

    public ReviewMultipropertyCustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMER);
    }

    public Response getCustomerPropertiesMetric(String metric, String customerCode, String since, String until, String granularity) {
        CustomerSteps customerStep = new CustomerSteps();
        Customer c = customerStep.getCustomerByCodeInternal(customerCode);

        Response customerProperties = getSecondLevelEntities(c.getCustomerId(), metric, null, null, null, null, null);

        return customerProperties;
    }
}
