package travel.snapshot.dp.qa.serenity.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;

public class ReviewMultipropertyPropertySetSteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_PROPERTY_SET = "/review/analytics/property_set/";

    public ReviewMultipropertyPropertySetSteps() {
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_PROPERTY_SET);
    }

    @Step
    public void getAggregatedStatisticsForPropertySet(String metric, String pSetCode, String customerId, String since, String until, String granularity, String limit, String cursor) {
        PropertySetSteps propertySetSteps = new PropertySetSteps();
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(pSetCode, customerId);

        Response aggregatedPropertySet = getSecondLevelEntitiesForDates(propertySet.getPropertySetId(), metric, limit, cursor, since, until, granularity, null, null, null);
        setSessionResponse(aggregatedPropertySet);
    }
}
