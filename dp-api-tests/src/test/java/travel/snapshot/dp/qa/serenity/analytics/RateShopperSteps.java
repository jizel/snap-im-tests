package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.sun.media.jfxmedia.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import cucumber.api.java.en.When;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import java.time.LocalDate;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;

public class RateShopperSteps extends BasicSteps {


    public RateShopperSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(RATE_SHOPPER_BASE_URI));
    }

    //GET Requests
    
    public void emptyGetRequest(String url) {
    	Serenity.setSessionVariable(SESSION_RESPONSE).to(given(spec).get(url));
    }
    
    @Step
    public void getPropertyRateData(String property_id, String since, String until, String fetched) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(until);
        
        RequestSpecification requestSpecification = given().spec(spec);

        if (sinceDate != null) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }

        if (untilDate != null) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }
        
        if(StringUtils.isNotBlank(fetched)) {
        	requestSpecification.parameter("fetch_datetime", fetched);
        }
        
        Response response = requestSpecification.get("/rate_shopper/analytics/property/{id}", property_id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    @Step
    public void getMarketRateData(String property_id, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(since);

        RequestSpecification requestSpecification = given().spec(spec)
        		.param("property_id", property_id);

        if (since != null && !"".equals(since)) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }

        if (until != null && !"".equals(until)) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get("/rate_shopper/analytics/market");
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    @Step
    public void getProperties(String propertyId, String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .parameter("property_id", propertyId);

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        Response response = requestSpecification.when().get("/rate_shopper/analytics/market/properties");
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    // Response validation
    
    public void dateFieldForProperty(String fieldName, String propertyId, String value) {
    	Response response = getSessionResponse();
    	String unparsedExpectedDate;
    	LocalDate actualDate = StringUtil.parseDate(response.body().path(fieldName));
    	
    	if(value.equals("first_fetch_date")) unparsedExpectedDate = getFirstFetchDateTime(propertyId).substring(0, 10);
    	else if(value.equals("last_fetch_date")) unparsedExpectedDate = getLastFetchDateTime(propertyId).substring(0, 10);
    	else unparsedExpectedDate = value;
    	
    	LocalDate expectedDate = StringUtil.parseDate(unparsedExpectedDate); 
    	assertEquals(expectedDate, actualDate);
    }
    
    public String getFirstFetchDateTime(String property_id){
    	return given().spec(spec).param("fetch_datetime","2001-01-01T00:00:01").get("/rate_shopper/analytics/property/{id}", property_id).path("fetch_datetime");
    }
    
    public String getLastFetchDateTime(String property_id){
    	return given().spec(spec).get("/rate_shopper/analytics/property/{id}", property_id).path("fetch_datetime");
    }
}