package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
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
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_ROLES_PATH;
import static travel.snapshot.dp.json.ObjectMappers.OBJECT_MAPPER;
import static travel.snapshot.dp.qa.junit.tests.common.CommonTest.transformNull;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMap.endpointEntityMap;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.model.EntityDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.utils.EndpointEntityMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class CommonHelpers extends BasicSteps {

    public static final String ENTITIES_TO_DELETE = "deleteThese";

    public static final List<String> ALL_ENDPOINTS = Arrays.asList(
            APPLICATIONS_PATH,
            APPLICATION_VERSIONS_PATH,
            USER_CUSTOMER_ROLES_PATH,
            USER_PROPERTY_ROLES_PATH,
            USER_PROPERTY_SET_ROLES_PATH,
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
            PLATFORM_OPERATIONS_PATH
    );


    public CommonHelpers() {
        spec.baseUri(getBaseUriForModule("identity"));
    }

    public void updateRegistryOfDeletables(String basePath, UUID id) {
        // Retrieve the map from serenity session variable
        Map<String, ArrayList<UUID>> registry = Serenity.sessionVariableCalled(ENTITIES_TO_DELETE);
        // Retrieve the array of ids of the certain enity type
        ArrayList<UUID> listIds = getArrayFromMap(basePath, registry);
        // Update this list
        listIds.add(id);
        // Put it back to the map and map to session variable
        registry.put(basePath, listIds);
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(registry);
    }

    public ArrayList<UUID> getArrayFromMap(String aKey, Map<String, ArrayList<UUID>> inputMap) {
        return (inputMap.get(aKey) == null) ? new ArrayList<>() : inputMap.get(aKey);
    }

    public static <T> List<T> parseResponseAsListOfObjects(Class<T> clazz) {
        Response response = getSessionResponse();
        List<T> objects = null;
        try {
            objects = OBJECT_MAPPER.readValue(response.asString(), TypeFactory.defaultInstance().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return objects;
    }

    // Get all

    public Response getEntities(String basePath, Map<String, String> queryParams) {
        return getEntitiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, queryParams);
    }

    public Response getEntitiesByUserForApp(UUID userId, UUID appId, String basePath, Map<String, String> queryParams) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, appId);
        if (queryParams != null) requestSpecification.parameters(queryParams);
        Response response = requestSpecification.when().get();
        setSessionResponse(response);
        return response;
    }

    public <T> List<T> getEntitiesAsType(String basePath, Class<T> clazz, Map<String, String> queryParams) {
        setSessionResponse(getEntities(basePath, queryParams));
        return parseResponseAsListOfObjects(clazz);
    }

    public <T> List<T> getEntitiesAsTypeByUserForApp(UUID userId, UUID appId, String basePath, Class<T> clazz, Map<String, String> queryParams) {
        setSessionResponse(getEntitiesByUserForApp(userId, appId, basePath, queryParams));
        return parseResponseAsListOfObjects(clazz);
    }

    // Get

    public Response getEntity(String basePath, UUID entityId) {
        return getEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public Response getEntityByUserForApplication(UUID userId, UUID applicationId, String basePath, UUID entityId) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec);
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        Response response = requestSpecification.when().get("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    public <T> T getEntityAsType(String basePath, Class<T> type, UUID entityId) {
        return getEntityAsTypeByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, type, entityId);
    }

    public <T> T getEntityAsTypeByUserForApp(UUID userId, UUID appVersionId, String basePath, Class<T> type, UUID entityId) {
        Response response = getEntityByUserForApplication(userId, appVersionId, basePath, entityId);
        if (response.getStatusCode() != SC_OK) {
            throw new RuntimeException(String.format("%s entity with id %s does not exist. Response is: %s", basePath, entityId, response.toString()));
        }
        return response.as(type);
    }

    // Get etag

    public String getEntityEtag(String basePath, UUID entityId) {
        return getEntityEtagByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public String getEntityEtagByUserForApp(UUID userId, UUID applicationId, String basePath, UUID entityId) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec);
        requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);

        return requestSpecification.when().head("/{id}", entityId).getHeader(HEADER_ETAG);
    }


    // Create

    public UUID entityIsCreated(String basePath, Object entity) {
        Response response = createEntity(basePath, entity);
        responseCodeIs(SC_CREATED);
        UUID result = null;
        try {
            result = getDtoFromResponse(response, basePath).getId();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return result;
    }

    public <T> T entityWithTypeIsCreated(String basePath, Class<T> type, T entity) {
        Response response = createEntity(basePath, entity);
        responseCodeIs(SC_CREATED);
        return response.as(type);
    }

    public Response createEntity(String basePath, Object entity) {
        return createEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entity);
    }

    public Response createEntityByUserForApplication(UUID userId, UUID applicationId, String basePath, Object entity) {
        spec.basePath(basePath);
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId).body(entity).when().post();
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
    public Response updateEntityByUserForAppPost(UUID userId, UUID applicationId, String basePath, UUID entityId, Object data) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        String etag = getEntityEtagByUserForApp(userId, applicationId, basePath, entityId);
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(basePath)
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

    public Response updateEntityWithEtag(String basePath, UUID entityId, Object data, String etag) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(basePath)
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
                if(etag != null) {
                    requestSpecification.header(HEADER_IF_MATCH, etag);
                }
        Response response = requestSpecification
                .body(data)
                .when()
                .patch("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }


    /**
     * Update any IM entity via REST api using PATCH method. Verify that the response code is 200 - OK
     *
     * @param basePath - endpoint corresponding to the entity
     * @param data     - entity update object, i.e. CustomerUpdateDto
     */
    public void entityIsUpdated(String basePath, UUID entityId, Object data) {
        updateEntity(basePath, entityId, data);
        responseCodeIs(SC_OK);
    }

    /**
     * Update any IM entity via REST api using PATCH method. Default snapshot type user and application version is used.
     *
     * @param basePath - endpoint corresponding to the entity
     * @param data     - entity update object, i.e. CustomerUpdateDto
     * @return Restassured Response type. Response should contain the updated object body according to DPIM-28.
     */
    public Response updateEntity(String basePath, UUID entityId, Object data) {
        return updateEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

    /**
     * Update of any IM entity using defined user and application. Should be used for update access check testing.
     *
     * @param userId        - context user, user performing the update
     * @param applicationId - context application
     */
    public Response updateEntityByUserForApp(UUID userId, UUID applicationId, String basePath, UUID entityId, Object data) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
//        If entity does not exist, default etag is used so update method can be used for negative tests as well.
//        Raw etag is got first, stored into var and then valuated to save api calls.
        String rawEtag = getEntityEtag(basePath, entityId);
        String usedEtag = (rawEtag == null) ? DEFAULT_SNAPSHOT_ETAG : rawEtag;
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(basePath)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .header(HEADER_IF_MATCH, usedEtag);
        Response response = requestSpecification
                .body(data)
                .when()
                .patch("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    // Delete

    public void entityIsDeleted(String basePath, UUID entityId) {
        deleteEntity(basePath, entityId);
        responseCodeIs(SC_NO_CONTENT);
    }

    public void deleteEntity(String basePath, UUID entityId) {
        deleteEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public void deleteEntityByUserForApp(UUID userId, UUID applicationId, String basePath, UUID entityId) {
        if (userId == null) {
            fail("User ID to be send in request header is blank.");
        }
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        Response response = requestSpecification.when().delete("/{id}", entityId);
        setSessionResponse(response);
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
    public Response createRelationship(String basePath, UUID firstLevelEntityId, String secondLevelPath, Object body) {
        return createRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, firstLevelEntityId, secondLevelPath, body);
    }

    public Response createRelationshipByUserForApp(UUID userId, UUID applicationId, String basePath, UUID firstLevelEntityId, String secondLevelResource, Object body) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .body(body);
        Response response = requestSpecification.post("{id}/{secondLevelName}", firstLevelEntityId, stripSlash(secondLevelResource));
        setSessionResponse(response);
        return response;
    }

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

    public Response getRelationshipsByUserForApp(UUID userId, UUID applicationId, String basePath, UUID firstLevelEntityId, String secondLevelResource, Map<String, String> queryParams) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        if (queryParams != null) {
            requestSpecification.parameters(queryParams);
        }
        Response response = requestSpecification.get("{id}/{secondLevelName}", firstLevelEntityId, stripSlash(secondLevelResource));
        setSessionResponse(response);

        return response;
    }


//    Help methods

    private EntityDto getDtoFromResponse(Response response, String basePath) {
        if (endpointEntityMap.get(basePath) == null) {
            throw new NoSuchElementException("There is no key " + basePath + " in " + EndpointEntityMap.class.getCanonicalName() + ". It should probably be added.");
        }
        return response.as(endpointEntityMap.get(basePath));
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
    private String stripSlash(String string) {
        return (string.startsWith("/")) ? string.substring(1) : string;
    }

    public static <T> void assertIfNotNull(T actual, T expected){
        if(transformNull(expected) != null){
            assertThat(actual, is(expected));
        }
    }
}
