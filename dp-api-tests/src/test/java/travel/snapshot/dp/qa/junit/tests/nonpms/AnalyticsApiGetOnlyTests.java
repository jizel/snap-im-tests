package travel.snapshot.dp.qa.junit.tests.nonpms;

import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.HEADER_XPROPERTY;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.CONFIGURATION_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.RATE_SHOPPER_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.REVIEW_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.REVIEW_LOCATIONS_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.RS_MARKET;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.RS_MARKET_PROPERTIES;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.RS_PROPERTY;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.SOCIAL_MEDIA_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.WEB_PERFORMANCE_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.AnalyticsHelpers.propertyId;

import com.jayway.restassured.response.Response;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import travel.snapshot.dp.qa.junit.tests.common.CommonProductionTest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsApiGetOnlyTests extends CommonProductionTest {

    private Map<String, String> RATE_SHOPPER_PARAMETERS;
    private Map<String, String> SINCE_UNTIL;
    private Map<String, String> PROPERTY_EQUALS;
    private Map<String, String> HEADERS;

    private static String X_APP_CONTEXT_HEADER = "x-application-context";

    @Before
    public void setUp() {
        super.setUp();
        String SINCE = LocalDate.now().minusMonths(2).toString();
        String UNTIL = LocalDate.now().toString();
        SINCE_UNTIL = ImmutableMap.of(
                "since", SINCE,
                "until", UNTIL
        );
        PROPERTY_EQUALS = singletonMap(PROPERTY_ID, propertyId);
        HEADERS = singletonMap(HEADER_XPROPERTY, propertyId);
        RATE_SHOPPER_PARAMETERS = new HashMap<>();
        RATE_SHOPPER_PARAMETERS.putAll(SINCE_UNTIL);
        RATE_SHOPPER_PARAMETERS.putAll(PROPERTY_EQUALS);
    }

    @Test
    public void TestConfigurationEndpoints() {
        CONFIGURATION_ENDPOINTS.forEach(endpoint ->
                assertResponseIsOk(authorizationHelpers.sendGetRequest(endpoint, null, null))
        );
    }

    @Test
    @Ignore("DPNP-90")
    public void TestRateShopperEndpoints() {
        assertResponseIsOk(
                authorizationHelpers.sendGetRequest(RATE_SHOPPER_ENDPOINTS.get(RS_MARKET), RATE_SHOPPER_PARAMETERS, null)
        );

        assertResponseIsOk(
                authorizationHelpers.sendGetRequest(RATE_SHOPPER_ENDPOINTS.get(RS_MARKET_PROPERTIES), PROPERTY_EQUALS, null)
        );

        assertResponseIsOk(
                authorizationHelpers.sendGetRequest(RATE_SHOPPER_ENDPOINTS.get(RS_PROPERTY), SINCE_UNTIL, null)
        );
    }

    @Test
    public void TestReviewEndpoints() {
        REVIEW_ENDPOINTS.forEach(endpoint -> assertResponseIsOk(
                authorizationHelpers.sendGetRequest(endpoint, SINCE_UNTIL, HEADERS)
        ));

        REVIEW_LOCATIONS_ENDPOINTS.forEach(endpoint -> assertResponseIsOk(
                authorizationHelpers.sendGetRequest(endpoint, null, HEADERS)
        ));
    }

    @Test
    public void TestSocialMediaEndpoints() {
        SOCIAL_MEDIA_ENDPOINTS.forEach(endpoint -> assertResponseIsOk(
                authorizationHelpers.sendGetRequest(endpoint, SINCE_UNTIL, HEADERS)
        ));
    }

    @Test
    public void TestWebPerformanceEndpoints() {
        WEB_PERFORMANCE_ENDPOINTS.forEach(endpoint -> assertResponseIsOk(
                authorizationHelpers.sendGetRequest(endpoint, SINCE_UNTIL, HEADERS)
        ));
    }

    /**
     * Checking also that x-application-context header is not present - DPNP-109
     */
    private void assertResponseIsOk(Response response) {
        response.then()
                .statusCode(SC_OK)
                .header(X_APP_CONTEXT_HEADER, is(nullValue()));
    }
}
