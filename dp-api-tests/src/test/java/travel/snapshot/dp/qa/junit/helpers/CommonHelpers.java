package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.util.TextUtils.isBlank;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIP_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMap.endpointEntityMap;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.api.model.VersionedEntityDto;
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
        CUSTOMER_PROPERTY_RELATIONSHIP_PATH,
        APPLICATIONS_PATH,
        APPLICATION_VERSIONS_PATH,
        ROLES_PATH,
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
        USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH,
        USER_PROPERTY_RELATIONSHIPS_PATH,
        USER_PROPERTY_ROLE_RELATIONSHIPS_PATH,
        USER_PROPERTY_SET_RELATIONSHIPS_PATH,
        USER_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH,
        USER_GROUP_PROPERTY_RELATIONSHIPS_PATH,
        USER_GROUP_PROPERTY_ROLE_RELATIONSHIPS_PATH,
        USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
        USER_GROUP_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH,
        USER_GROUP_ROLE_RELATIONSHIPS_PATH,
        USER_GROUP_USER_RELATIONSHIPS_PATH,
        PLATFORM_OPERATIONS_PATH
    );


    public CommonHelpers() {
        spec.baseUri(getBaseUriForModule("identity"));
    }

    public void updateRegistryOfDeletables(String entityType, UUID id) {
        // Retrieve the map from serenity session variable
        Map<String, ArrayList<UUID>> registry = Serenity.sessionVariableCalled(ENTITIES_TO_DELETE);
        // Retrieve the array of ids of the certain enity type
        ArrayList<UUID> listIds = getArrayFromMap(entityType, registry);
        // Update this list
        listIds.add(id);
        // Put it back to the map and map to session variable
        registry.put(entityType, listIds);
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(registry);
    }

    public ArrayList<UUID> getArrayFromMap(String aKey, Map<String, ArrayList<UUID>> inputMap) {
        return (inputMap.get(aKey) == null) ? new ArrayList<>() : inputMap.get(aKey);
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
        RequestSpecification requestSpecification = given().spec(spec);
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId);
        requestSpecification = requestSpecification.header(HEADER_XAUTH_APPLICATION_ID, appId);
        if(queryParams != null) requestSpecification.parameters(queryParams);
        Response response = requestSpecification.when().get();
        setSessionResponse(response);
        return response;
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
        return getEntity(basePath, entityId).as(type);
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

    public UUID entityIsCreated(String basePath, Object entity) throws IOException {
        Response response = createEntity(basePath, entity);
        responseCodeIs(SC_CREATED);
        return getDtoFromResponse(response, basePath).getId();
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
    public Response updateEntityPost(String basePath, UUID entityId, Object data) {
        return updateEntityByUserForAppPost(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

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
        if (isBlank(etag)) {
            fail("Etag to be send in request header is null.");
        }
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(basePath)
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID)
                .header(HEADER_IF_MATCH, etag);
        Response response = requestSpecification
                .body(data)
                .when()
                .post("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    /**
     * Update any IM entity via REST api using PATCH method. Default snapshot type user and application version is used.
     * @param basePath - endpoint corresponding to the entity
     * @param entityId
     * @param data - entity update object, i.e. CustomerUpdateDto
     * @return Restassured Response type. Response should contain the updated object body according to DPIM-28.
     */
    public Response updateEntity(String basePath, UUID entityId, Object data){
        return updateEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

    /**
     * Update of any IM entity using defined user and application. Should be used for update access check testing.
     * @param userId - context user, user performing the update
     * @param applicationId - context application
     * @param basePath
     * @param entityId
     * @param data
     * @return
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

    // Manual delete method. Expect explicitly passed etag

    public void deleteEntityWithEtag(String basePath, UUID entityId, String etag) {
        if (isBlank(etag)) {
            fail("Etag to be send in request header is blank.");
        }
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_IF_MATCH, etag)
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        Response response = requestSpecification
                .when()
                .delete("/{id}", entityId);
        setSessionResponse(response);
    }

    // Automatic delete methods - do not require explicit etags, get it themselves

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
        String etag = getEntityEtag(basePath, entityId);
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .header(HEADER_IF_MATCH, etag);
        Response response = requestSpecification.when().delete("/{id}", entityId);
        setSessionResponse(response);
    }

//    Private help methods

    private VersionedEntityDto getDtoFromResponse(Response response, String basePath) {
        if (endpointEntityMap.get(basePath) == null) {
            throw new NoSuchElementException("There is no key " + basePath + " in " + EndpointEntityMap.class.getCanonicalName() + ". It should probably be added.");
        }
        return response.as(endpointEntityMap.get(basePath));
    }
}
