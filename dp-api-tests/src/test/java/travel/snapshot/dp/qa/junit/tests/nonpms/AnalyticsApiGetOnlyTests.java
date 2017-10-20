package travel.snapshot.dp.qa.junit.tests.nonpms;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.qa.junit.tests.common.CommonSmokeTest;


import java.time.LocalDate;
import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.Map;

import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_XPROPERTY;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_ID;
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

public class AnalyticsApiGetOnlyTests extends CommonSmokeTest {

    Map RATE_SHOPPER_PARAMETERS;
    Map SINCE_UNTIL;
    Map PROPERTY_EQUALS;
    Map HEADERS;

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
        CONFIGURATION_ENDPOINTS.forEach(endpoint -> {
            authorizationHelpers
                    .sendGetRequest(endpoint, null, null)
                    .then()
                    .statusCode(SC_OK);
        });
    }

    @Test
    public void TestRateShopperEndpoints() {
        authorizationHelpers
                .sendGetRequest(RATE_SHOPPER_ENDPOINTS.get(RS_MARKET), RATE_SHOPPER_PARAMETERS, null)
                .then()
                .statusCode(SC_OK);

        authorizationHelpers
                .sendGetRequest(RATE_SHOPPER_ENDPOINTS.get(RS_MARKET_PROPERTIES), PROPERTY_EQUALS, null)
                .then()
                .statusCode(SC_OK);

        authorizationHelpers
                .sendGetRequest(RATE_SHOPPER_ENDPOINTS.get(RS_PROPERTY), SINCE_UNTIL, null)
                .then()
                .statusCode(SC_OK);
    }

    @Test
    public void TestReviewEndpoints() {
        REVIEW_ENDPOINTS.forEach(endpoint -> {
            authorizationHelpers
                    .sendGetRequest(endpoint, SINCE_UNTIL, HEADERS)
                    .then()
                    .statusCode(SC_OK);
        });

        REVIEW_LOCATIONS_ENDPOINTS.forEach(endpoint -> {
            authorizationHelpers
                    .sendGetRequest(endpoint, null, HEADERS)
                    .then()
                    .statusCode(SC_OK);

        });
    }

    @Test
    public void TestSocialMediaEndpoints() {
        SOCIAL_MEDIA_ENDPOINTS.forEach(endpoint -> {
            authorizationHelpers
                    .sendGetRequest(endpoint, SINCE_UNTIL, HEADERS)
                    .then()
                    .statusCode(SC_OK);
        });
    }

    @Test
    public void TestWebPerformanceEndpoints() {
        WEB_PERFORMANCE_ENDPOINTS.forEach(endpoint -> {
            authorizationHelpers
                    .sendGetRequest(endpoint, SINCE_UNTIL, HEADERS)
                    .then()
                    .statusCode(SC_OK);
        });
    }
}
