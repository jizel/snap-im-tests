package travel.snapshot.dp.qa.serenity;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;

import static net.serenitybdd.rest.SerenityRest.rest;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class BasicSteps {

    public static final String BASE_URI = "baseURI";
    public static final String SESSION_RESPONSE = "response";
    public static final String SOCIAL_MEDIA_BASE_URI = "social_media.baseURI";
    public static final String IDENTITY_BASE_URI = "identity.baseURI";
    public static final String CONFIGURATION_BASE_URI = "configuration.baseURI";
    private static final String CONFIGURATION_HTTP_LOG_LEVEL = "http_log_level";

    protected RequestSpecification spec = null;

    public BasicSteps() {
        RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.setContentType(ContentType.JSON);
        String logLevel = PropertiesHelper.getProperty(CONFIGURATION_HTTP_LOG_LEVEL);

        LogDetail logDetail = null;

        switch (logLevel) {
            case "ALL": {
                logDetail = LogDetail.ALL;
                break;
            }
            case "HEADERS": {
                logDetail = LogDetail.HEADERS;
                break;
            }
            case "BODY": {
                logDetail = LogDetail.BODY;
                break;
            }
            case "METHOD": {
                logDetail = LogDetail.METHOD;
                break;
            }
            case "PARAMS": {
                logDetail = LogDetail.PARAMS;
                break;
            }
            case "PATH": {
                logDetail = LogDetail.PATH;
                break;
            }
            case "STATUS": {
                logDetail = LogDetail.STATUS;
                break;
            }
            default:

        }

        if (logDetail != null) {
            builder.log(logDetail);
        }
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
