package travel.snapshot.dp.qa.cucumber.serenity;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.stripSlash;
import static travel.snapshot.dp.qa.junit.utils.RestAssuredConfig.setupRequestDefaults;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.api.validation.UUIDValidator;
import travel.snapshot.dp.qa.cucumber.helpers.NullStringObjectValueConverter;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.helpers.SalesforceIdStdSerializer;
import travel.snapshot.dp.qa.cucumber.helpers.StringUtil;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class BasicSteps {
    public static final String DEFAULT_CUSTOMER_TYPE = "HOTEL";
    public static final String SNAPSHOT_WEBSITE = "http://www.snapshot.travel";
    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String OAUTH_PARAMETER_NAME = "access_token";
    public static final String HEADER_XAUTH_USER_ID = "X-Auth-UserId";
    public static final String HEADER_XAUTH_APPLICATION_ID = "X-Auth-AppId";
    public static final String HEADER_XPROPERTY = "X-Property";
    public static final UUID DEFAULT_SNAPSHOT_USER_ID = UUID.fromString("0b000000-0000-4444-8888-000000000000");
    public static final String DEFAULT_SNAPSHOT_USER_NAME = "defaultSnapshotUser";
    public static final UUID DEFAULT_SNAPSHOT_APPLICATION_ID = UUID.fromString("03000000-0000-4444-8888-000000000000");
    public static final UUID DEFAULT_SNAPSHOT_PARTNER_ID = UUID.fromString("07000000-0000-4444-8888-000000000002");
    public static final String DEFAULT_SNAPSHOT_PARTNER_VAT_ID = "CZ10000001";
    public static final UUID DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID = UUID.fromString("04000000-0000-4444-8888-000000000000");
    public static final UUID DEFAULT_SNAPSHOT_CUSTOMER_ID = UUID.fromString("06000000-0000-4444-8888-000000000001");
    public static final UUID DEFAULT_PROPERTY_ID = UUID.fromString("08000000-0000-4444-8888-000000000001");
    public static final UUID DEFAULT_ADDRESS_ID = UUID.fromString("11111111-0000-4000-a000-777777777777");
    public static final UUID DEFAULT_COMMERCIAL_SUBSCRIPTION_ID = UUID.fromString("11111111-0000-4000-a000-888888888888");
    public static final String DEFAULT_SNAPSHOT_TIMEZONE = "Europe/Prague";
    public static final String DEFAULT_SNAPSHOT_SALESFORCE_ID = "DEFAULTSFID0001";
    public static final String DEFAULT_SNAPSHOT_ETAG = "11111111111111111111111111111111";
    public static final String NON_EXISTENT_ETAG = "11111111111111111111111111111112";
    public static final String DEFAULT_SNAPSHOT_PARTNER_NAME = "CoreQA Partner";
    public static final String DEFAULT_SNAPSHOT_PARTNER_EMAIL = "somemail@snapshot.travel";
    public static final UUID NON_EXISTENT_ID = UUID.fromString("00000000-0000-4000-a000-000000000000");
    public static final String SESSION_RESPONSE = "response";
    public static final String SESSION_TOKEN = "token";
    public static final String KEYCLOAK_TOKEN = "keycloak_token";
    public static final String DEFAULT_SNAPSHOT_APPLICATION_NAME = "Default Snapshot Test App";

    public static final String SESSION_RESPONSE_MAP = "response_map";
    public static final String SOCIAL_MEDIA_BASE_URI = "social_media.baseURI";
    public static final String INSTAGRAM_BASE_URI = "instagram.baseURI";
    public static final String FACEBOOK_BASE_URI = "facebook.baseURI";
    public static final String REVIEW_BASE_URI = "review.baseURI";
    public static final String TWITTER_BASE_URI = "twitter.baseURI";
    public static final String WEB_PERFORMANCE_BASE_URI = "web_performance.baseURI";
    public static final String RATE_SHOPPER_BASE_URI = "rate_shopper.baseURI";
    public static final String IDENTITY_BASE_URI = "identity.baseURI";
    public static final String IDENTITY_NGINX_BASE_URI = "identity_nginx.baseURI";
    public static final String CONFIGURATION_BASE_URI = "configuration.baseURI";
    public static final String CONFIGURATION_NGINX_BASE_URI = "configuration_nginx.baseURI";
    public static final String LIMIT_TO_ALL = "200";
    public static final String LIMIT_TO_ONE = "1";
    public static final String CURSOR_FROM_FIRST = "0";
    public static final String SECOND_LEVEL_OBJECT_API_SUBSCRIPTION = "api_subscriptions";
    public static final String AUTHORIZATION_BASE_URI = "authorization.baseURI";
    public static final String HEADER_ETAG = "ETag";
    public static final String CURLY_BRACES_EMPTY = "{}";
    public static final String CONFIGURATION_REQUEST_HTTP_LOG_LEVEL = "http_request_log_level";
    public static final String CONFIGURATION_RESPONSE_HTTP_LOG_LEVEL = "http_response_log_level";
    public static final String CONFIGURATION_RESPONSE_HTTP_LOG_STATUS = "http_response_log_status";
    public static final String REQUESTOR_ID = "requestorId";
    public static final String TARGET_ID = "targetId";
    public static final String ROLE_ID = "role_id";
    public static final String PROPERTY_CODE = "property_code";
    public static final String IS_ACTIVE = "is_active";
    public static final String PROPERTY_ID = "property_id";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String RELATIONSHIP_TYPE = "type";
    public static final String VALID_FROM = "valid_from";
    public static final String VALID_TO = "valid_to";
    public static final String VALID_FROM_VALUE = "2017-06-01";
    public static final String VALID_TO_VALUE = "2100-06-01";
    public static final String SNAPSHOT_PHONE = "+420530514301";
    public static final String DEFAULT_PASSWORD = "P@ssw0rd";
    public static final String DEFAULT_ENCRYPTED_PASSWORD = "$2a$10$vNTgpUAsWvhJQmJR2DkuYOTN5EgJQhMOqQ5xd0DmJOHdck4Sa2orq";
    public static final String WRONG_ENCRYPTED_PASSWORD = "$2a$10$iIuyGtO./6izEUk0iOZdy.5moBMuhTj0dU6sQMoiVYswO2QSd1jnO";
    public static final String DEFAULT_CLIENT_SECRET = "a4000000-0000-4444-8888-000000000000";
    public static final String PARTNERS_RESOURCE = "partners";
    public static final String ADDRESS_LINE1_PATTERN = "CoreQA";
    public static final String EXAMPLE_NULL = "/null";
    protected RequestSpecification spec = null;
    protected static PropertiesHelper propertiesHelper = new PropertiesHelper();

    public BasicSteps() {
        spec = setupRequestDefaults();
    }

    public void setAccessTokenParamFromSession() {
        String token = getSessionVariable(OAUTH_PARAMETER_NAME);
        if (isNotEmpty(token)) {
            spec.queryParam(OAUTH_PARAMETER_NAME, token);
        }
    }

    public static String getRequestDataFromFile(InputStream inputStream) throws IOException {
        return IOUtils.toString(inputStream, Charset.forName("utf-8"));
    }

    public static void responseCodeIs(int responseCode) {
        Response response = getSessionResponse();
        response.then().statusCode(responseCode);
    }

    public static void contentTypeIs(String contentType) {
        Response response = getSessionResponse();
        response.then().contentType(contentType);
    }

    public static void customCodeIs(Integer customCode) {
        Response response = getSessionResponse();
        response.then().body("code", is(customCode));
    }

    public static void bodyIsEmpty() {
        Response response = getSessionResponse();
        response.then().body(isEmptyOrNullString());
    }

    @Step
    public void bodyContainsCollectionWith(String attributeName, Object item) {
        Response response = getSessionResponse();
        response.then().body(attributeName, hasItem(item));
    }

    /**
     * This method is used instead of bodyContainsCollectionWith() when the collection contains
     * values of type Double. Only the integer part of the value is validated.
     */
    public void integerPartOfValueIs(String path, int value) {
        Response response = getSessionResponse();
        response.then().body(path, hasItem(value));
    }

    @Step
    public static void bodyContainsEntityWith(String attributeName, String attributeValue) {
        Response response = getSessionResponse();
        response.then().body(attributeName, isOneOf(attributeValue, Boolean.valueOf(attributeValue)));
    }

    @Step
    public static void bodyContainsEntityWith(String attributeName, Integer attributeValue) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(attributeValue));
    }

    public static void bodyContainsEntityWith(String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName, notNullValue());
    }

    public void bodyDoesntContainEntityWith(String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName, nullValue());
    }

    public void isCalledWithoutTokenUsingMethod(String service, String method) {
        Response response = spec
                .when()
                .get(service);
        setSessionResponse(response);
    }

    public void etagIsPresent() {
        Response response = getSessionResponse();
        response.then().header(HEADER_ETAG, not(isEmptyOrNullString()));
    }

    public void useFileForSendDataTo(String filename, String method, String url, String module) throws Exception {
        if (!"POST".equals(method)) {
            throw new Exception("Cannot use this method for other methods than POST");
        }
        setBaseUriForModule(module);
        String data = getRequestDataFromFile(this.getClass().getResourceAsStream(filename));
        Response response = given().spec(spec).basePath(url).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .body(data)
                .when().post();
        setSessionResponse(response);

    }

    public String getAttributeValue(String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName, notNullValue());

        return response.getBody().jsonPath().get(attributeName).toString();
    }

    public static String getBaseUriForModule(String module) {
        module = (module == null) ? "" : module;
        String baseUri = "";
        switch (module) {
            case "identity": {
                baseUri = propertiesHelper.getProperty(IDENTITY_BASE_URI);
                break;
            }
            case "configurations": {
                baseUri = (propertiesHelper.getProperty(CONFIGURATION_BASE_URI));
                break;
            }
            case "social_media": {
                baseUri = (propertiesHelper.getProperty(SOCIAL_MEDIA_BASE_URI));
                break;
            }
            case "facebook": {
                baseUri = (propertiesHelper.getProperty(FACEBOOK_BASE_URI));
                break;
            }
            case "instagram": {
                baseUri = (propertiesHelper.getProperty(INSTAGRAM_BASE_URI));
                break;
            }
            case "twitter": {
                baseUri = (propertiesHelper.getProperty(TWITTER_BASE_URI));
                break;
            }
            case "rate_shopper": {
                baseUri = (propertiesHelper.getProperty(RATE_SHOPPER_BASE_URI));
                break;
            }
            case "web_performance": {
                baseUri = (propertiesHelper.getProperty(WEB_PERFORMANCE_BASE_URI));
                break;
            }
            case "review": {
                baseUri = (propertiesHelper.getProperty(REVIEW_BASE_URI));
                break;
            }
            case "authorization": {
                baseUri = (propertiesHelper.getProperty(AUTHORIZATION_BASE_URI));
                break;
            }
            default:
        }
        return baseUri;
    }

    private void setBaseUriForModule(String module) {
        spec.baseUri(getBaseUriForModule(module));
    }

    protected void entityIsCreatedByUser(UUID userId, Object entity) {
        createEntityByUser(userId, entity);
        responseCodeIs(SC_CREATED);
    }

    protected Response createEntity(Object entity) {
        return createEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entity);
    }

    protected Response createEntityByUser(UUID userId, Object entity) {
        return createEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entity);
    }

    protected Response createEntityByUserForApplication(UUID userId, UUID applicationId, Object entity) {
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId).body(entity).when().post();
        setSessionResponse(response);
        return response;
    }

    protected Response updateEntity(UUID entityId, Map<String, Object> data, String etag) {
        return updateEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, data, etag);
    }

    protected Response updateEntityByUser(UUID userId, UUID entityId, Map<String, Object> data, String etag) {
        return updateEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entityId, data, etag);
    }

    protected Response updateEntityByUserForApplication(UUID userId, UUID applicationId, UUID entityId, Map<String, Object> data, String etag) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        if (isBlank(etag)) {
            fail("Etag to be send in request header is null.");
        }
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .header(HEADER_IF_MATCH, etag);
        return requestSpecification
                .body(data)
                .when()
                .post("/{id}", entityId);
    }

    protected Response updateEntity(UUID entityId, Object data, String etag) {
        return updateEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, data, etag);
    }

    protected Response updateEntityByUser(UUID userId, UUID entityId, Object data, String etag) {
        return updateEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entityId, data, etag);
    }

    protected Response updateEntityByUserForApplication(UUID userId, UUID applicationId, UUID entityId, Object data, String etag) {

        if (userId == null) {

            fail("User ID to be send in request header is null.");
        }
        if (isBlank(etag)) {
            fail("Etag to be send in request header is null.");
        }
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .header(HEADER_IF_MATCH, etag);
        Response response = requestSpecification
                .body(data)
                .when()
                .post("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    // manual delete methods - require explicitly passed etags

    public Response deleteEntity(UUID entityId, String etag) {
        Response response = deleteEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId, etag);
        setSessionResponse(response);
        return response;
    }

    protected Response deleteEntityByUser(UUID userId, UUID entityId, String etag) {
        return deleteEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entityId, etag);
    }

    protected Response deleteEntityByUserForApplication(UUID userId, UUID applicationId, UUID entityId, String etag) {
        if (userId == null) {
            fail("User ID to be send in request header is blank.");
        }
        if (isBlank(etag)) {
            fail("Etag to be send in request header is null.");
        }
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .header(HEADER_IF_MATCH, etag);
        Response response = requestSpecification
                .when()
                .delete("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }




    protected Response deleteEntityWithEtag(UUID entityId) {
        return deleteEntityWithEtagByUser(DEFAULT_SNAPSHOT_USER_ID, entityId);
    }

    protected Response deleteEntityWithEtagByUser(UUID userId, UUID entityId) {
        return deleteEntityWithEtagByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entityId);
    }

    protected Response deleteEntityWithEtagByUserForApp(UUID userId, UUID applicationVersionId, UUID entityId) {
        String etag = getEntityEtag(entityId);
        Response response = deleteEntityByUserForApplication(userId, applicationVersionId, entityId, etag);
        setSessionResponse(response);
        return response;
    }

    public String getEntityEtag(UUID entityId) {
        return getEntityEtagByUser(DEFAULT_SNAPSHOT_USER_ID, entityId);
    }

    public String getEntityEtagByUser(UUID userId, UUID entityId) {
        return getEntityEtagByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entityId);
    }

    public String getEntityEtagByUserForApplication(UUID userId, UUID applicationId, UUID entityId) {
        RequestSpecification requestSpecification = given().spec(spec);
        requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);

        return requestSpecification.when().head("/{id}", entityId).getHeader(HEADER_ETAG);
    }


    public Response getEntity(UUID entityId) {
        return getEntityByUser(DEFAULT_SNAPSHOT_USER_ID, entityId);
    }

    public Response getEntityByUser(UUID userId, UUID entityId) {
        return getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entityId);
    }

    public Response getEntityByUserForApplication(UUID userId, UUID applicationId, UUID entityId) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        return requestSpecification.when().get("/{id}", entityId);
    }

    protected Response createSecondLevelRelationship(UUID firstLevelId, String secondLevelName, Object jsonBody) {
        return createSecondLevelRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelName, jsonBody);
    }

    protected Response createSecondLevelRelationshipByUser(UUID userId, UUID firstLevelId, String secondLevelName, Object jsonBody) {
        return createSecondLevelRelationshipByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelName, jsonBody);
    }

    protected Response createSecondLevelRelationshipByUserForApplication(UUID userId, UUID applicationId, UUID firstLevelId, String secondLevelName, Object jsonBody) {
        RequestSpecification requestSpecification = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId).body(jsonBody);
        Response response = requestSpecification.post("/" + firstLevelId + "/" + stripSlash(secondLevelName));
        setSessionResponse(response);
        return response;
    }

    public Response createThirdLevelEntity(UUID firstLevelId, String secondLevelType, UUID secondLevelId, String thirdLevelType, Object jsonBody) {
        return createThirdLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelType, secondLevelId, thirdLevelType, jsonBody);
    }

    protected Response createThirdLevelEntityByUser(UUID userId, UUID firstLevelId, String secondLevelType, UUID secondLevelId, String thirdLevelType, Object jsonBody) {
        return createThirdLevelEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelType, secondLevelId, thirdLevelType, jsonBody);
    }

    protected Response createThirdLevelEntityByUserForApplication(UUID userId, UUID applicationId, UUID firstLevelId, String secondLevelType, UUID secondLevelId, String thirdLevelType, Object jsonBody) {
        RequestSpecification requestSpecification = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId).body(jsonBody);
        return requestSpecification.post("/{firstLevelId}/{secondLevelType}/{secondLevelId}/{thirdLevelType}", firstLevelId, secondLevelType, secondLevelId, thirdLevelType);
    }

    protected Response deleteThirdLevelEntity(UUID firstLevelId, String secondLevelType, UUID secondLevelId, String thirdLevelType, UUID thirdLevelId, String eTag) {
        return deleteThirdLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelType, secondLevelId, thirdLevelType, thirdLevelId, eTag);
    }

    protected Response deleteThirdLevelEntityByUser(UUID userId, UUID firstLevelId, String secondLevelType, UUID secondLevelId, String thirdLevelType, UUID thirdLevelId, String eTag) {
        return deleteThirdLevelEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelType, secondLevelId, thirdLevelType, thirdLevelId, eTag);
    }

    protected Response deleteThirdLevelEntityByUserForApplication(UUID userId, UUID applicationId, UUID firstLevelId, String secondLevelType, UUID secondLevelId, String thirdLevelType, UUID thirdLevelId, String eTag) {
        String url = "/" + firstLevelId + "/" + secondLevelType + "/" + secondLevelId + "/" + thirdLevelType + "/" + thirdLevelId;
        RequestSpecification requestSpecification = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_IF_MATCH, eTag).header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        return requestSpecification.delete(url);
    }

    protected Response getSecondLevelEntity(UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        return getSecondLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response getSecondLevelEntityByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        return getSecondLevelEntityByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response getSecondLevelEntityByUserForApp(UUID userId, UUID applicationVersionId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (userId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        if (applicationVersionId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_APPLICATION_ID, applicationVersionId);
        }
        return requestSpecification.when().get("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected String getSecondLevelEntityEtag(UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        return getSecondLevelEntityEtagByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected String getSecondLevelEntityEtagByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (userId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        return requestSpecification.when().head("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId).getHeader(HEADER_ETAG);
    }

    protected String getThirdLevelEntityEtag(UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, String thirdLevelObjectName, UUID thirdLevelId) {
        return getThirdLevelEntityEtagByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId, thirdLevelObjectName, thirdLevelId);
    }

    protected String getThirdLevelEntityEtagByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, String thirdLevelObjectName, UUID thirdLevelId) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (userId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        return requestSpecification.when().head("/{firstLevelId}/{secondLevelName}/{secondLevelId}/{thirdLevelName}/{thirdLevelId}", firstLevelId, secondLevelObjectName, secondLevelId, thirdLevelObjectName, thirdLevelId).getHeader(HEADER_ETAG);
    }

    protected Response deleteSecondLevelEntity(UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, Map<String, String> queryParams) {
        return deleteSecondLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId, queryParams);
    }

    protected Response deleteSecondLevelEntityByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, Map<String, String> queryParams) {
        return deleteSecondLevelEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelObjectName, secondLevelId, queryParams);
    }

    protected Response deleteSecondLevelEntityByUserForApplication(UUID userId, UUID applicationId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        String etag = getSecondLevelEntityEtag(firstLevelId, secondLevelObjectName, secondLevelId);
        if (isNotBlank(etag)) {
            requestSpecification.header(HEADER_IF_MATCH, etag);
        } else {
            requestSpecification.header(HEADER_IF_MATCH, DEFAULT_SNAPSHOT_ETAG);
        }
        if (userId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        if (applicationId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        }
        if (queryParams != null) {
            requestSpecification.parameters(queryParams);
        }
        return requestSpecification
                .when().delete("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    protected Response updateSecondLevelEntity(UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, Map<String, Object> objectMap, String etag) {
        JSONObject jsonBody = new JSONObject(objectMap);
        return updateSecondLevelEntity(firstLevelId, secondLevelObjectName, secondLevelId, jsonBody, etag);
    }

    protected Response updateSecondLevelEntity(UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, JSONObject object, String etag) {
        return updateSecondLevelEntityByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, secondLevelId, object, etag);
    }

    protected Response updateSecondLevelEntityByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, JSONObject object, String etag) {
        return updateSecondLevelEntityByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelObjectName, secondLevelId, object, etag);
    }

    protected Response updateSecondLevelEntityByUserForApp(UUID userId, UUID applicationVersionId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, JSONObject object, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (isNotBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        if (userId != null) {
            requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        }
        if (applicationVersionId != null) {
            requestSpecification = requestSpecification.header(HEADER_XAUTH_APPLICATION_ID, applicationVersionId);
        }
        return requestSpecification.body(object.toString()).when().post("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
    }

    @Step
    public static void sendBlankPost(String url, String module) {
        sendPostWithBody(url, module, "");
    }

    @Step
    public static void sendPostWithBody(String url, String module, String body) {
        RequestSpecification requestSpecification = given().baseUri(getBaseUriForModule(module)).header("Content-Type", "application/json;charset=UTF-8");
        Response response = given().spec(requestSpecification).basePath(url)
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .body(body).when().post();

//        If request needs ETag header (for updates). I know this looks awful and it makes a few redundant api calls but other solutions involve needles meta-information in gherkin scenario (boolean needsETag or something like that).
        if (response.getStatusCode() == HttpStatus.SC_PRECONDITION_FAILED) {
            requestSpecification.basePath(url);
            String etag = requestSpecification.header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID).when().head().getHeader(HEADER_ETAG);
            assertThat("ETag was not obtained", etag, not(isEmptyOrNullString()));
            requestSpecification.header(HEADER_IF_MATCH, etag);

            response = requestSpecification.body(body).when().post();
        }

        setSessionResponse(response);
    }

    @Step
    public void sendGetRequestToUrl(String url, String module) {
        sendGetRequestToUrlByUser(DEFAULT_SNAPSHOT_USER_ID, url, module);
    }

    @Step
    public void sendGetRequestToUrlByUser(UUID userId, String url, String module) {
        sendGetRequestToUrlByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, url, module);
    }

    @Step
    public void sendGetRequestToUrlByUserForApp(UUID userId, UUID applicationVersionId, String url, String module) {
        sendGetRequestToUrlByUserForAppWithParams(userId, applicationVersionId, url, module, null, null, null, null, null);
    }

    @Step
    public void sendGetRequestToUrlByUserForAppWithParams(UUID userId, UUID applicationVersionId, String url, String module, String since, String until, String granularity, String xproperty, String asParam) {
        setBaseUriForModule(module);
        RequestSpecification requestSpecification = given().spec(spec);
//        asParam is a workaround for Dp inconsistency. Remove when DP-1957 is solved.
        if (xproperty != null && asParam != null) {
            requestSpecification.param("property_id", xproperty);
        } else if (xproperty != null) {
            requestSpecification.header(HEADER_XPROPERTY, xproperty);
        }
        if (since != null) {
            requestSpecification.param("since", since);
        }
        if (until != null) {
            requestSpecification.param("until", until);
        }
        if (granularity != null) {
            requestSpecification.param("granularity", granularity);
        }
        Response response = requestSpecification
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationVersionId)
                .basePath(url).when().get();
        setSessionResponse(response);
    }

    @Step
    public void sendDeleteToUrl(String url, String module) {
        setBaseUriForModule(module);
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID).basePath(url).when().delete();
        setSessionResponse(response);
    }

    @Step
    public void sendGetRequestToUrlWithoutUserHeader(String url, String module) {
        setBaseUriForModule(module);
        Response response = given().spec(spec).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID).basePath(url).when().get();
        setSessionResponse(response);
    }

    @Step
    public void sendGetRequestToUrlWithoutAppHeader(String url, String module) {
        setBaseUriForModule(module);
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).basePath(url).when().get();
        setSessionResponse(response);
    }


    /**
     * getting entities over rest api, if limit and cursor is null or empty, it's not added to query
     * string
     *
     * @param sortDesc @return
     */

    protected Response getEntities(String url, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getEntitiesByUser(DEFAULT_SNAPSHOT_USER_ID, url, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected Response getEntitiesByUser(UUID userId, String url, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getEntitiesByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, url, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    public Response getEntitiesByUserForApp(UUID userId, UUID appId, String url, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        if (url == null) {
            url = "";
        }
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, appId);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, queryParams);
        requestSpecification.parameters(params);
        Response response = requestSpecification.when().get(url);
        setSessionResponse(response);
        return response;
    }

    protected Response getEntitiesForUrlWihDates(String url, String limit, String cursor, String since, String until, String granularity, Map<String, String> queryParams) {
        Map<String, String> preparedParams = buildQueryParamMapForDates(since, until, granularity);
        if (queryParams != null) {
            preparedParams.putAll(queryParams);
        }
        return getEntities(url, limit, cursor, null, null, null, preparedParams);
    }

    /**
     * Method will ignore parsing of date if it is incorrectly hand out to method for purpose of
     * negative tests
     *
     * @param since       correct format 2015-01-01 / today / today -1 month
     * @param until       same as "since"
     * @param granularity day/week/month
     * @return Map of query parameters that is passed to getEntitiesForUrlWihDates
     */
    private Map<String, String> buildQueryParamMapForDates(String since, String until, String granularity) {
        Map<String, String> queryParams = new HashMap<>();
        try {
            LocalDate sinceDate = StringUtil.parseDate(since);
            if (sinceDate != null) {
                queryParams.put("since", sinceDate.format(DateTimeFormatter.ISO_DATE));
            }
        } catch (DateTimeParseException e) {
            queryParams.put("since", since);
        }

        try {
            LocalDate untilDate = StringUtil.parseDate(until);
            if (untilDate != null) {
                queryParams.put("until", untilDate.format(DateTimeFormatter.ISO_DATE));
            }
        } catch (DateTimeParseException e) {
            queryParams.put("until", until);
        }

        if (isNotBlank(granularity)) {
            queryParams.put("granularity", granularity);
        }
        return queryParams;
    }

    public static Map<String, String> buildQueryParamMapForPaging(String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParameters) {
        Map<String, String> queryParams = new HashMap<>();
        if (cursor != null && ! cursor.equals(EXAMPLE_NULL)) {
            queryParams.put("cursor", cursor);
        }
        if (limit != null && ! limit.equals(EXAMPLE_NULL)) {
            queryParams.put("limit", limit);
        }
        if (filter != null && ! filter.equals(EXAMPLE_NULL)) {
            queryParams.put("filter", filter);
        }
        if (sort != null && ! sort.equals(EXAMPLE_NULL)) {
            queryParams.put("sort", sort);
        }
        if (sortDesc != null && ! sortDesc.equals(EXAMPLE_NULL)) {
            queryParams.put("sort_desc", sortDesc);
        }
        if (queryParameters != null) {
            queryParams.putAll(queryParameters);
        }

        return queryParams;
    }

    protected Response getSecondLevelEntities(UUID firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getSecondLevelEntitiesByUser(DEFAULT_SNAPSHOT_USER_ID, firstLevelId, secondLevelObjectName, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected Response getSecondLevelEntitiesByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getSecondLevelEntitiesByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelObjectName, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected Response getThirdLevelEntitiesByUser(UUID userId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, String thirdLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        return getThirdLevelEntitiesByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, firstLevelId, secondLevelObjectName, secondLevelId, thirdLevelObjectName, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected Response getThirdLevelEntitiesByUserForApp(UUID userId, UUID appId, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId, String thirdLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        if ( userId == null ) {
            fail("User ID to be send in request header is null.");
        }
        if (appId == null) {
            fail("Application ID to be send in request header is null.");
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, appId);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, queryParams);
        requestSpecification.parameters(params);

        return requestSpecification.when().get("{firstId}/{secondLevelName}/{secondId}/{thirdLevelName}", firstLevelId, secondLevelObjectName, secondLevelId, thirdLevelObjectName);

    }

    protected Response getSecondLevelEntitiesByUserForApp(UUID userId, UUID appId, UUID firstLevelId, String secondLevelObjectName, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        if (appId == null) {
            fail("Application ID to be send in request header is null.");
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, appId);

        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, queryParams);
        requestSpecification.parameters(params);

        Response response = requestSpecification.when().get("{id}/{secondLevelName}", firstLevelId, secondLevelObjectName);
        setSessionResponse(response);
        return response;
    }

    protected Response getSecondLevelEntitiesForDates(UUID firstLevelId, String secondLevelObjectName, String limit, String cursor, String since, String until, String granularity, String filter, String sort, String sortDesc) {
        Map<String, String> queryParams = buildQueryParamMapForDates(since, until, granularity);

        return getSecondLevelEntities(firstLevelId, secondLevelObjectName, limit, cursor, filter, sort, sortDesc, queryParams);
    }

    protected JSONObject retrieveData(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(SalesforceId.class, new SalesforceIdStdSerializer());
        mapper.registerModule(module);

        String data = mapper.writeValueAsString(value);
        return NullStringObjectValueConverter.transform(data);
    }

    protected <T> Map<String, Object> retrieveDataOld(Class<T> c, T entity) throws IntrospectionException, ReflectiveOperationException {
        Map<String, Object> data = new HashMap<>();
        for (PropertyDescriptor descriptor : Introspector.getBeanInfo(c).getPropertyDescriptors()) {
            Method getter = descriptor.getReadMethod();
            Enumeration<String> atts = descriptor.attributeNames();
            Object value = getter.invoke(entity);
            if (value != null) {
                JsonProperty jsonProperty = getter.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !value.toString().equals("/null")) {
                    data.put(getter.getAnnotation(JsonProperty.class).value(), value);
                }
            }
        }
        return data;
    }

    // --- session access ---

    public static Response getSessionResponse() {
        return Serenity.<Response>sessionVariableCalled(SESSION_RESPONSE);
    }

    public static void setSessionResponse(Response response) {
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }


    public Map<String, Response> getSessionResponseMap() {
        return Serenity.<Map<String, Response>>sessionVariableCalled(SESSION_RESPONSE_MAP);
    }

    public void setSessionResponseMap(Map<String, Response> responses) {
        Serenity.setSessionVariable(SESSION_RESPONSE_MAP).to(responses);
    }

    public void setSessionVariable(String key, Object value) {
        Serenity.setSessionVariable(key).to(value);
    }

    public <T> T getSessionVariable(String key) {
        return Serenity.<T>sessionVariableCalled(key);
    }

    @Step
    public static <T> void numberOfEntitiesInResponse(Class<T> clazz, int count) {
        List<T> objects = parseResponseAsListOfObjects(clazz);
        assertThat(String.format("Response should contain %d entities, but contains %d", count, objects.size()),
                objects.size(),
                is(count));
    }

    public static void headerIs(String headerName, String value) {
        Response response = getSessionResponse();
        response.then().header(headerName, is(value));
    }

    public static void headerContains(String headerName, String value) {
        Response response = getSessionResponse();
        response.then().header(headerName, containsString(value));
    }

    public void responseContainsNoOfAttributes(int count, String attributeName) {
        Response response = getSessionResponse();
        response.then().body(attributeName + ".size()", is(count));
    }

    public Boolean isUUID(String param) {
        UUIDValidator validator = new UUIDValidator();
        return validator.isValid(param, null);
    }

    public String resolveObjectName(String name) {
        String objectName = null;
        switch (name) {
            case "customer":
                objectName = CUSTOMERS_RESOURCE;
                break;
            case "property":
                objectName = PROPERTIES_RESOURCE;
                break;
            case "property set":
                objectName = PROPERTY_SETS_RESOURCE;
                break;
            case PROPERTIES_RESOURCE:
                objectName = PROPERTIES_RESOURCE;
                break;
            case CUSTOMERS_RESOURCE:
                objectName = CUSTOMERS_RESOURCE;
                break;
            case PROPERTY_SETS_RESOURCE:
                objectName = PROPERTY_SETS_RESOURCE;
                break;
        }
        return objectName;
    }
}
