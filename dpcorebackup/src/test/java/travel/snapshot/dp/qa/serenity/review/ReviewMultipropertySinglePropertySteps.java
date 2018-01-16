package travel.snapshot.dp.qa.serenity.review;

import com.jayway.restassured.response.Response;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.serenity.analytics.AnalyticsBaseSteps;

import java.util.UUID;

public class ReviewMultipropertySinglePropertySteps extends AnalyticsBaseSteps {

    private static final String BASE_PATH_PROPERTY_SET = "/review/analytics/property/";

    public ReviewMultipropertySinglePropertySteps() {
        spec.baseUri(propertiesHelper.getProperty(REVIEW_BASE_URI));
        spec.basePath(BASE_PATH_PROPERTY_SET);
    }

    @Step
    public void getStatisticsForSingleProperty(String metric, UUID property_id, String since, String until, String granularity) {
        Response propertyStatistics = getSecondLevelEntitiesForDates(property_id, metric, null, null, since, until, granularity, null, null, null);

        setSessionResponse(propertyStatistics);
    }
}
