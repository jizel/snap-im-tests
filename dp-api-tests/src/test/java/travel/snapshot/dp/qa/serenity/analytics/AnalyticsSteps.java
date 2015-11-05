package travel.snapshot.dp.qa.serenity.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.model.Stats;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class AnalyticsSteps extends BasicSteps {


    public AnalyticsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
    }
    
    //GET Requests
    
    @Step
    public void getDataWithoutProperty(String url) {
        Response response = given().spec(spec).get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    @Step
    public void getDataWithoutAccessToken(String url) {
        Response response = given().spec(spec).header("x-property","property").get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void getData(String url, String granularity, String property, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(since);

        RequestSpecification requestSpecification = given().spec(spec)
                .header("x-property", property).parameter("access_token", "aaa");

        if (granularity != null && !"".equals(granularity)) {
            requestSpecification.parameter("granularity", granularity);
        }
        if (since!= null && !"".equals(since)) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (until!= null && !"".equals(until)) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    @Step
    public void getItems(String url, String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(url)
                .header("x-property", "property")
                .parameter("access_token", "aaa");

        if (cursor != null && !"".equals(cursor)) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null && !"".equals(limit)) {
            requestSpecification.parameter("limit", limit);
        }
        Response response = requestSpecification.when().get();
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    //Response Validation
    
    @Step
    public void responseContainsValues(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("values.size()", is(count));
    }
    
    @Step
    public void responseContainsValuesForAllMetrics(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("data.values.size()", is(count));
    }
    
    @Step
    public void maximumNumberOfItemsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("size()",lessThanOrEqualTo(count));
    }
    
    @Step
    public void dateFieldIs(String fieldName, String value){
        LocalDate expectedDate = StringUtil.parseDate(value);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);

        LocalDate actualDate = LocalDate.parse(response.getBody().path(fieldName));
    	assertEquals(expectedDate, actualDate);
    }
    
    @Step
    public void textFieldIs(String fieldName, String value){
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(fieldName, is(value));
    }
    
    @Step
    public void fieldContains(String fieldName, String value){
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(fieldName, hasItem(value));
    }
}
