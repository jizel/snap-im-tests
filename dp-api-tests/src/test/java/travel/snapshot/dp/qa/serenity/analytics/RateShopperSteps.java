package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import com.sun.media.jfxmedia.logging.Logger;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;

public class RateShopperSteps extends BasicSteps {


    public RateShopperSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(RATE_SHOPPER_BASE_URI));
    }

    //GET Requests

    @Step
    public void getPropertyRateData(String property_id, String since, String until, String fetched) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(since);
        
        RequestSpecification requestSpecification = given().spec(spec);

        if (since != null) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }

        if (until != null) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }
        
        requestSpecification.parameter("fetch_datetime",
        		fetched == "last fetch" ?  getLastFetchDateTime() : fetched);
        
        
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
    public void getItems(String url, String propertyId, String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(url)
                .parameter("property_id", propertyId);

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        Response response = requestSpecification.when().get();
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    public String getLastFetchDateTime(){
    	return given().spec(spec).get("/rate_shopper/analytics/property/10010003").path("fetch_datetime");
    }
}