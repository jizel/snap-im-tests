package travel.snapshot.dp.qa.cucumber.serenity.analytics;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;

import travel.snapshot.dp.qa.cucumber.helpers.StringUtil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RateShopperSteps extends AnalyticsBaseSteps {


    public RateShopperSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(RATE_SHOPPER_BASE_URI));
    }

    public void emptyGetRequest(String url) {
        Serenity.setSessionVariable(SESSION_RESPONSE).to(given(spec).get(url));
    }

    @Step
    public void getPropertyRateData(UUID property_id, String since, String until, String fetched) {
        Map<String, String> queryParams = new HashMap<>();

        if (StringUtils.isNotBlank(fetched)) {
            queryParams.put("fetch_datetime", fetched);
        }

        Response response = getEntitiesForUrlWihDates("/rate_shopper/analytics/property/" + property_id, null, null, since, until, null, queryParams);
        setSessionResponse(response);
    }

    @Step
    public void getMarketRateData(UUID propertyId, String since, String until) {
        Map<String, String> queryParams = new HashMap<>();

        queryParams.put("property_id", propertyId.toString());

        Response response = getEntitiesForUrlWihDates("/rate_shopper/analytics/market", null, null, since, until, null, queryParams);
        setSessionResponse(response);
    }

    @Step
    public void getProperties(UUID propertyId, String limit, String cursor, String fetchDateTime) {
        Map<String, String> queryParams = new HashMap<>();

        queryParams.put("property_id", propertyId.toString());

        if (fetchDateTime != null) {
            queryParams.put("fetch_datetime", fetchDateTime);
        }

        Response response = getEntitiesForUrlWihDates("/rate_shopper/analytics/market/properties", limit, cursor, null, null, null, queryParams);
        setSessionResponse(response);
    }

    // Response validation

    public void dateFieldForProperty(String fieldName, UUID propertyId, String value) {
        Response response = getSessionResponse();
        String unparsedExpectedDate;
        LocalDate actualDate = StringUtil.parseDate(response.body().path(fieldName));

        if (value.equals("first_fetch_date"))
            unparsedExpectedDate = getFirstFetchDateTime(propertyId).substring(0, 10);
        else if (value.equals("last_fetch_date"))
            unparsedExpectedDate = getLastFetchDateTime(propertyId).substring(0, 10);
        else unparsedExpectedDate = value;

        LocalDate expectedDate = StringUtil.parseDate(unparsedExpectedDate);
        assertEquals(expectedDate, actualDate);
    }

    public String getFirstFetchDateTime(UUID property_id) {
        return given().spec(spec).param("fetch_datetime", "2001-01-01T00:00:01").get("/rate_shopper/analytics/property/{id}", property_id).path("fetch_datetime");
    }

    public String getLastFetchDateTime(UUID property_id) {
        return given().spec(spec).get("/rate_shopper/analytics/property/{id}", property_id).path("fetch_datetime");
    }

    @Override
    public void responseContainsValues(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("properties.size()", is(count));
    }
}
