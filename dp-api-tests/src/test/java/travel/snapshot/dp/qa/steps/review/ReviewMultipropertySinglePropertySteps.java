package travel.snapshot.dp.qa.steps.review;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

public class ReviewMultipropertySinglePropertySteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_PROPERTY_SET = "/review/analytics/property/";

    public ReviewMultipropertySinglePropertySteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_PROPERTY_SET);
    }

    public void getStatisticsForSingleProperty(String metric, String property_id, String since, String until, String granularity) {
        Response propertyStatistics = getSecondLevelEntitiesForDates(property_id, metric, null, null, since, until, granularity);
        setSessionResponse(propertyStatistics);
    }
}
