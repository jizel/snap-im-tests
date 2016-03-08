package travel.snapshot.dp.qa.serenity.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;

public class ReviewMultipropertyCustomerSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_CUSTOMER = "/review/analytics/customer/";

    public ReviewMultipropertyCustomerSteps() {
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMER);
    }

    @Step
    public void getCustomerPropertiesMetric(String metric, String customerCode, String since, String until, String granularity, String limit, String cursor) {
        CustomerSteps customerStep = new CustomerSteps();
        Customer customer = customerStep.getCustomerByCodeInternal(customerCode);

        Response customerProperties = getSecondLevelEntitiesForDates(customer.getCustomerId(), metric, limit, cursor, since, until, granularity, null, null, null);
        setSessionResponse(customerProperties);
    }
}
