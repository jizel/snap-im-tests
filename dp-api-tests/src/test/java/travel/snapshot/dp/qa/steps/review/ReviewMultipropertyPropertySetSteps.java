package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.PropertySet;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;

public class ReviewMultipropertyPropertySetSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_PROPERTY_SET = "/review/analytics/property_set/";

    public ReviewMultipropertyPropertySetSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_PROPERTY_SET);
    }

    public void getAggregatedStatisticsForPropertySet(String metric, String pSetCode, String customerCode, String since, String until, String granularity, String limit, String cursor) {
        CustomerSteps customerSteps = new CustomerSteps();
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);

        PropertySetSteps propertySetSteps = new PropertySetSteps();
        PropertySet propertySet = propertySetSteps.getPropertySetByNameForCustomer(pSetCode, c.getCustomerId());

        Response aggregatedPropertySet = getSecondLevelEntitiesForDates(propertySet.getPropertySetId(), metric, limit, cursor, since, until, granularity);
        setSessionResponse(aggregatedPropertySet);
    }
}
