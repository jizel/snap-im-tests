package travel.snapshot.dp.qa.serenity.analytics;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.StringUtil;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
<<<<<<< Upstream, based on origin/master
import static org.hamcrest.Matchers.lessThanOrEqualTo;
=======
import static org.hamcrest.Matchers.not;

>>>>>>> f377379 Added a feature file for authorization, defined some basic tests
import static org.junit.Assert.assertEquals;

/**
 * Created by sedlacek on 10/5/2015.
 */
public class AnalyticsSteps extends BasicSteps {


    public AnalyticsSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
    }
    
    //GET and POST Requests
    
    @Step
    public void emptyRequest(String url) {
        Response response = given().spec(spec).get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void getDataWithoutProperty(String url) {
        Response response = given().spec(spec).parameter("access_token", "aaa").get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void getDataWithoutAccessToken(String url) {
        Response response = given().spec(spec).header("x-property", "property").get(url);
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
        if (since != null && !"".equals(since)) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }
        if (until != null && !"".equals(until)) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    public void postData(String url, String username, String password) {
        RequestSpecification requestSpecification = given().spec(spec)
                .parameter("grant_type", "password")
                .parameter("username", username)
                .parameter("password", password);

        Response response = requestSpecification.when().post(url);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void getPropertyRateData(String property_id, String since, String until) {
        LocalDate sinceDate = StringUtil.parseDate(since);
        LocalDate untilDate = StringUtil.parseDate(since);

        RequestSpecification requestSpecification = given().spec(spec)
                .parameter("access_token", "aaa");

        if (since != null && !"".equals(since)) {
            requestSpecification.parameter("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
        }

        if (until != null && !"".equals(until)) {
            requestSpecification.parameter("until", untilDate.format(DateTimeFormatter.ISO_DATE));
        }

        Response response = requestSpecification.when().get("rate_shopper/analytics/property/" + property_id);
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
        response.then().body("size()", lessThanOrEqualTo(count));
    }

    @Step
    public void maximumNumberOfValuesInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("values.size()", lessThanOrEqualTo(count));
    }

    @Step
    public void valueIsLessThanOrEqualTo(String pathToValue1, String pathToValue2) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(pathToValue1, lessThanOrEqualTo(pathToValue2));
    }

    @Step
    public void dateFieldIs(String fieldName, String value) {
        LocalDate expectedDate = StringUtil.parseDate(value);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);

        LocalDate actualDate = LocalDate.parse(response.getBody().path(fieldName));
        assertEquals(expectedDate, actualDate);
    }

    @Step
    public void textFieldIs(String fieldName, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(fieldName, is(value));
    }

    @Step
    public void fieldContains(String fieldName, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(fieldName, hasItem(value));
    }
    
    @Step
    public void parameterIsValue(String parameter, String value){
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(parameter, is(value));
    }
    
    @Step
    public void parameterIsNotNull(String parameter){
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(parameter, not(null));
    }
}
