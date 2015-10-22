package travel.snapshot.dp.qa.serenity;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;

import static org.hamcrest.Matchers.*;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class BasicSteps {

    public static final String BASE_URI = "baseURI";
    public static final String SESSION_RESPONSE = "response";
    public static final String SOCIAL_MEDIA_BASE_URI = "social_media.baseURI";
    public static final String IDENTITY_BASE_URI = "identity.baseURI";
    public static final String CONFIGURATION_BASE_URI = "configuration.baseURI";
    private static final String CONFIGURATION_REQUEST_HTTP_LOG_LEVEL = "http_request_log_level";
    private static final String CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL = "http_response_log_level";
    private static final String CONFIGURATION_RESPONSE_HTTP_LOG_STATUS = "http_response_log_status";

    protected RequestSpecification spec = null;

    public BasicSteps() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        RestAssured.filters(
                new ResponseLoggingFilter(
                        LogDetail.valueOf(PropertiesHelper.getProperty(CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL)),
                        true,
                        System.out,
                        not(isOneOf(PropertiesHelper.getListOfInt(CONFIGURATION_RESPONSE_HTTP_LOG_STATUS)))));

        builder.setContentType(ContentType.JSON);

        builder.log(LogDetail.valueOf(PropertiesHelper.getProperty(CONFIGURATION_REQUEST_HTTP_LOG_LEVEL)));

        spec = builder.build();
    }

    public void responseCodeIs(int responseCode) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().statusCode(responseCode);
    }

    public void contentTypeIs(String contentType) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().contentType(contentType);
    }

    public void customCodeIs(Integer customCode) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body("code", is(customCode));
    }

    public void bodyIsEmpty() {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(isEmptyOrNullString());
    }

    public void isCalledWithoutTokenUsingMethod(String service, String method) {
        Response response = spec
                .when()
                .get(service);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
}
