package travel.snapshot.dp.qa.serenity;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.filter.log.LogDetail;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.io.IOUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
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

    private String getRequestDataFromFile(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, Charset.forName("utf-8"));
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

    public void etagIsPresent() {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().header("ETag", not(isEmptyOrNullString()));
    }

    public void useFileForSendDataTo(String filename, String method, String url, String module) throws Exception {
        if (!"POST".equals(method)) {
            throw new Exception("Cannot use this method for other methods than POST");
        }
        switch (module)  {
            case "identity": {
                spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
                break;
            }
            case "configuration": {
                spec.baseUri(PropertiesHelper.getProperty(CONFIGURATION_BASE_URI));
                break;
            }
            case "social_media": {
                spec.baseUri(PropertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
                break;
            }
            default:
        }
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        Response response = given().spec(spec).basePath(url)
                .body(data)
                .when().post();
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session

    }
}
