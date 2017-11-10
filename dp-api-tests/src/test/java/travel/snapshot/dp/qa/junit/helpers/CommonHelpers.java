package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_PERMISSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.EFFECTIVE_PERMISSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.json.ObjectMappers.OBJECT_MAPPER;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_COMMERCIAL_SUBSCRIPTION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_ETAG;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_ETAG;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_IF_MATCH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_XAUTH_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_XAUTH_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getRequestDataFromFile;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.responseCodeIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.setSessionResponse;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.CC_ENTITY_NOT_FOUND;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.transformNull;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;
import static travel.snapshot.dp.qa.junit.utils.EntityEndpointMapping.entityCreateDtoEndpointMap;
import static travel.snapshot.dp.qa.junit.utils.RestAssuredConfig.setupRequestDefaults;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommonHelpers {

    public static final String RESPONSE_CODE = "code";
    public static final String RESPONSE_MESSAGE = "message";
    public static final String RESPONSE_DETAILS = "details";

    protected static RequestSpecification spec;
    private BasicSteps basicSteps = new BasicSteps();

    public static final String ENTITIES_TO_DELETE = "deleteThese";

    public static final List<String> ALL_ENDPOINTS = asList(
            APPLICATIONS_PATH,
            APPLICATION_VERSIONS_PATH,
            ROLES_PATH,
            USER_CUSTOMER_ROLES_PATH,
            USER_PROPERTY_ROLES_PATH,
            PROPERTIES_PATH,
            CUSTOMERS_PATH,
            USERS_PATH,
            USER_GROUPS_PATH,
            PROPERTY_SETS_PATH,
            COMMERCIAL_SUBSCRIPTIONS_PATH,
            PARTNERS_PATH,
            CUSTOMER_PROPERTY_RELATIONSHIPS_PATH,
            PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
            USER_CUSTOMER_RELATIONSHIPS_PATH,
            USER_PARTNER_RELATIONSHIPS_PATH,
            USER_PROPERTY_RELATIONSHIPS_PATH,
            USER_PROPERTY_SET_RELATIONSHIPS_PATH,
            USER_GROUP_PROPERTY_RELATIONSHIPS_PATH,
            USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
            USER_GROUP_USER_RELATIONSHIPS_PATH,
            PLATFORM_OPERATIONS_PATH,
            APPLICATION_PERMISSIONS_PATH,
            EFFECTIVE_PERMISSIONS_PATH
    );

    public static final List<String> ENDPOINTS_WITH_IDS = asList(
            String.format("%s/%s", APPLICATIONS_PATH, DEFAULT_SNAPSHOT_APPLICATION_ID),
            String.format("%s/%s", APPLICATION_VERSIONS_PATH, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID),
            String.format("%s/%s", PROPERTIES_PATH, DEFAULT_PROPERTY_ID),
            String.format("%s/%s", CUSTOMERS_PATH, DEFAULT_SNAPSHOT_CUSTOMER_ID),
            String.format("%s/%s", USERS_PATH, DEFAULT_SNAPSHOT_USER_ID),
            String.format("%s/%s", PARTNERS_PATH, DEFAULT_SNAPSHOT_PARTNER_ID),
            String.format("%s/%s", COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID)
    );

    public static final List<String> READONLY_ENDPOINTS = asList(
            EFFECTIVE_PERMISSIONS_PATH
    );

    public static List<String> readWriteEndpoints() {
        List<String> endpoints = new ArrayList<>(ALL_ENDPOINTS);
        endpoints.removeAll(READONLY_ENDPOINTS);
        return endpoints;
    }


    /**
     * Restassured initialization is copied from BasicSteps. This class should replace basic steps once all cucumber
     * tests are gone and should not inherit from it. However some functionality is similar.
     */
    static {
        spec = setupRequestDefaults();
    }

    // Get all

    public static Response getEntities(String basePath, Map<String, String> queryParams) {
        return getEntitiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, queryParams);
    }

    public static Response getEntitiesByUserForApp(UUID userId, UUID appId, String basePath, Map<String, String> queryParams) {
        assertThat("User ID to be send in request header is null.", userId, is(notNullValue()));
        spec.basePath(basePath);
        RequestSpecification requestSpecification = givenContext(userId, appId).spec(spec);
        if (queryParams != null) requestSpecification.parameters(queryParams);
        Response response = requestSpecification.when().get();
        setSessionResponse(response);
        return response;
    }

    public static <DTO> List<DTO> getEntitiesAsType(String basePath, Class<DTO> clazz, Map<String, String> queryParams) {
        getEntities(basePath, queryParams).then().statusCode(SC_OK);
        return parseResponseAsListOfObjects(clazz);
    }

    public static <DTO> List<DTO> getEntitiesAsTypeByUserForApp(UUID userId, UUID appId, String basePath, Class<DTO> clazz, Map<String, String> queryParams) {
        getEntitiesByUserForApp(userId, appId, basePath, queryParams).then().statusCode(SC_OK);
        return parseResponseAsListOfObjects(clazz);
    }

    // Get

    public static Response getEntity(String basePath, UUID entityId) {
        return getEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public static Response getEntityByUserForApplication(UUID userId, UUID applicationId, String basePath, UUID entityId) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = givenContext(userId, applicationId).spec(spec);
        assertNotNull("User ID to be send in request header is null.", userId);
        Response response = requestSpecification.when().get("/{id}", entityId);
        setSessionResponse(response);

        return response;
    }

    public static <DTO> DTO getEntityAsType(String basePath, Class<DTO> type, UUID entityId) {
        return getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, type, entityId);
    }

    public static <DTO> DTO getEntityAsTypeByUserForApp(UUID userId, UUID appVersionId, String basePath, Class<DTO> type, UUID entityId) {
        Response response = getEntityByUserForApplication(userId, appVersionId, basePath, entityId);
        assertThat(String.format("%s entity with id %s does not exist. Response is: %s", basePath, entityId, response.toString()),
                response.getStatusCode(), is(SC_OK));

        return response.as(type);
    }

    // Get etag

    public static String getEntityEtag(String basePath, UUID entityId) {
        spec.basePath(basePath);

        return givenContext(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .spec(spec)
                .head("/{id}", entityId)
                .getHeader(HEADER_ETAG);
    }

    // Create

    public static <DTO> UUID entityIsCreated(DTO entity) {
        Response response = createEntity(entity)
                .then()
                .statusCode(SC_CREATED)
                .extract().response();
        String basePath = getCreateBasePath(entity);
        return getDtoFromResponse(response, basePath).getId();
    }

    public static <DTO, CDTO> DTO entityIsCreatedAs(Class<DTO> type, CDTO entity) {
        Response response = createEntity(entity)
                .then()
                .statusCode(SC_CREATED)
                .extract().response();
        return response.as(type);
    }

    public static <CDTO> Response createEntity(CDTO entity) {
        return createEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entity);
    }

    /**
     * Specify context user and application for access check testing.
     */
    public static <CDTO> Response createEntityByUserForApplication(UUID userId, UUID applicationVersionId, CDTO entity) {
        spec.basePath(getCreateBasePath(entity));
        Response response = givenContext(userId, applicationVersionId)
                .spec(spec)
                .body(entity)
                .post();
        // Cucumber leftovers. Leaving it for now, will be removed in the future
        setSessionResponse(response);
        return response;
    }

    /**
     * Create entity with with path specified. This method is useful for negative test scenaries, i.e. enables sending
     * Map<String, Object> as entity with invalid values to any endpoint.
     */
    public static Response createEntity(String basePath, Object entity) {
        spec.basePath(basePath);
        Response response = givenContext(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID).spec(spec)
                .body(entity)
                .post();
        // Cucumber leftovers. Leaving it for now, will be removed in the future
        setSessionResponse(response);
        return response;
    }


    // Update

    //    Deprecated update using POST method. Will be removed completely in the future.
    @Deprecated
    public Response updateEntityPost(String basePath, UUID entityId, Object data) {
        return updateEntityByUserForAppPost(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

    @Deprecated
    public Response updateEntityByUserForAppPost(UUID userId, UUID applicationVersionId, String basePath, UUID entityId, Object data) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        String etag = getEntityEtag(basePath, entityId);
        Response response = givenContext(userId, applicationVersionId)
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_IF_MATCH, etag)
                .body(data)
                .when()
                .post("/{id}", entityId);
        setSessionResponse(response);

        return response;
    }

    public static void entityIsUpdated(String basePath, UUID entityId, Object data) {
        updateEntity(basePath, entityId, data);
        responseCodeIs(SC_OK);
    }

    public static Response updateEntity(String basePath, UUID entityId, Object data) {
        return updateEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

    public static Response updateEntityByUserForApp(UUID userId, UUID applicationVersionId, String basePath, UUID entityId, Object data) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
//        If entity does not exist, default etag is used so update method can be used for negative tests as well.
//        Raw etag is got first, stored into var and then valuated to save api calls.
        String rawEtag = getEntityEtag(basePath, entityId);
        String usedEtag = (rawEtag == null) ? DEFAULT_SNAPSHOT_ETAG : rawEtag;
        Response response = givenContext(userId, applicationVersionId)
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_IF_MATCH, usedEtag)
                .body(data)
                .when()
                .patch("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    public static Response updateEntityWithEtagByUserForApp(UUID userId, UUID appId, String basePath, UUID entityId, Object data, String etag) {
        RequestSpecification requestSpecification = givenContext(userId, appId)
                .spec(spec)
                .basePath(basePath);
        if (etag != null) {
            requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        Response response = requestSpecification
                .body(data)
                .when()
                .patch("/{id}", entityId);
        return response;
    }

    public static Response updateEntityWithEtag(String basePath, UUID entityId, Object data, String etag) {
        return updateEntityWithEtagByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data, etag);
    }

    // Delete

    public static void entityIsDeleted(String basePath, UUID entityId) {
        deleteEntity(basePath, entityId)
                .then()
                .statusCode(SC_NO_CONTENT);
        getEntity(basePath, entityId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
    }

    public static Response deleteEntity(String basePath, UUID entityId) {
        return deleteEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public static Response deleteEntityByUserForApp(UUID userId, UUID applicationVersionId, String basePath, UUID entityId) {
        assertNotNull("User ID to be send in request header is blank.", userId);
        Response response = givenContext(userId, applicationVersionId)
                .spec(spec)
                .basePath(basePath)
                .delete("/{id}", entityId);
        setSessionResponse(response);

        return response;
    }

//    Second level entities generic methods - CREATE

    /**
     * Create relationship (second level entity) between two entities.
     *
     * @param basePath        - path of the first level entity
     * @param secondLevelPath - path of the first level entity (to be created)
     * @param body            - the relationship object, usually Dto
     * @return response of the request
     */

//    Second level entities generic methods - GET

    /**
     * Get relationships (of second level type) for given entity
     *
     * @param basePath        - path of the first level entity
     * @param secondLevelPath - path of the first level entity (relationship type)
     * @param queryParams     - request params: filter, sort, cursor, limit etc.
     */
    public Response getRelationships(String basePath, UUID firstLevelEntityId, String secondLevelPath, Map<String, String> queryParams) {
        return getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, firstLevelEntityId, secondLevelPath, queryParams);
    }

    public Response getRelationshipsByUserForApp(UUID userId, UUID applicationVersionId, String basePath, UUID firstLevelEntityId, String secondLevelResource, Map<String, String> queryParams) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = givenContext(userId, applicationVersionId)
                .spec(spec);
        if (queryParams != null) {
            requestSpecification.parameters(queryParams);
        }
        Response response = requestSpecification.get("{id}/{secondLevelName}", firstLevelEntityId, stripSlash(secondLevelResource));
        setSessionResponse(response);

        return response;
    }


//    Help methods

    static EntityDto getDtoFromResponse(Response response, String basePath) {
        assertThat("There is no key " + basePath + " in " + EndpointEntityMapping.class.getCanonicalName() + ". It should probably be added.",
                endpointDtoMap.containsKey(basePath), is(true));
        return response.as(endpointDtoMap.get(basePath));
    }

    public static <DTO> String getCreateBasePath(DTO entity) {
        assertThat("There is no key " + entity.getClass() + " in entityCreateDtoEndpointMap. Are you sure it is a createDto? If so then it should probably be added.",
                entityCreateDtoEndpointMap.containsKey(entity.getClass()), is(true));

        return entityCreateDtoEndpointMap.get(entity.getClass());
    }

    public AddressDto constructAddressDto(String line1, String city, String zipCode, String countryCode) {
        AddressDto address = new AddressDto();
        address.setLine1(line1);
        address.setCity(city);
        address.setZipCode(zipCode);
        address.setCountryCode(countryCode);
        return address;
    }

    /**
     * When requesting relationships, url is concatenated from various pieces. Resources (not paths, see
     * travel.snapshot.dp.api.identity.resources.IdentityDefaults) should be used for second level path params. When a
     * path is used instead, '/' character is used twice - once poorly reformatted by restassured and causes troubles.
     *
     * THIS METHOD SHOULD BE USED FOR ALL SECOND LEVEL RESOURCES!
     */
    public static String stripSlash(String string) {
        return (string.startsWith("/")) ? string.substring(1) : string;
    }

    public static <T> void assertIfNotNull(T actual, T expected) {
        if (transformNull(expected) != null) {
            assertThat(actual, is(expected));
        }
    }

    private static RequestSpecification givenContext(UUID userId, UUID applicationId) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);
    }

    //    Forwarders for necessary BasicSteps that cannot be made static used in tests. Should be refactored in the future.
    public void useFileForSendDataTo(String filename, String method, String url, String module) throws Exception {
        basicSteps.useFileForSendDataTo(filename, method, url, module);
    }

    public static Map<String, String> emptyQueryParams() {
        return buildQueryParamMapForPaging(null, null, null, null, null, null);
    }

    public static <DTO> List<DTO> parseResponseAsListOfObjects(Class<DTO> clazz) {
        Response response = getSessionResponse();
        try {
            return OBJECT_MAPPER.readValue(response.asString(), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return null;
    }

    public static Response sendPostWithBody(String url, Object body) {
        spec.basePath(url);
        return givenContext(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .spec(spec)
                .body(body)
                .post();
    }

    public static String parseResourceFile(String filename) throws IOException {
        FileInputStream inputStream = new FileInputStream(filename);
        return getRequestDataFromFile(inputStream);
    }

    public static <DTO> DTO getEntityByName(String basePath, String name) {
        Map<String, String> params = buildQueryParamMapForPaging(null, null, String.format("name==%s", name), null, null, null);
        return (DTO) getEntitiesAsType(basePath, endpointDtoMap.get(basePath), params).get(0);
    }

    public static <DTO> List<? extends EntityDto> getEntitiesByPattern(String basePath, String fieldName, String pattern) {
        return getEntitiesByPatternByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, fieldName, pattern);
    }

    public static <DTO> List<? extends EntityDto> getEntitiesByPatternByUserForApp(UUID userId, UUID appId, String basePath, String fieldName, String pattern) {
        Map<String, String> params = buildQueryParamMapForPaging(null, null, String.format("%s==%s", fieldName, pattern), null, null, null);
        return getEntitiesAsTypeByUserForApp(userId, appId, basePath, endpointDtoMap.get(basePath), params);

    }
}
