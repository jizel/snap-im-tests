package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.util.TextUtils.isBlank;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRoleRelationshipDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.fail;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
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
import static travel.snapshot.dp.json.ObjectMappers.createObjectMapper;

public class CommonHelpers extends BasicSteps {

    public static final String ENTITIES_TO_DELETE = "deleteThese";


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

    private <T> T parseEntity(Class<T> clazz) throws IOException {
        Response response = getSessionResponse();
        return createObjectMapper().readValue(response.asString(), TypeFactory.defaultInstance().constructType(clazz));
    }

    private UUID getResponseEntityId(String basePath) throws IOException {
        UUID id = null;
        switch (basePath) {
            case USERS_PATH:
                id = parseEntity(UserDto.class).getId();
                break;
            case CUSTOMERS_PATH:
                id = parseEntity(CustomerDto.class).getId();
                break;
            case ROLES_PATH:
                id = parseEntity(RoleDto.class).getId();
                break;
            case PROPERTIES_PATH:
                id = parseEntity(PropertyDto.class).getId();
                break;
            case PROPERTY_SETS_PATH:
                id = parseEntity(PropertySetDto.class).getId();
                break;
            case APPLICATIONS_PATH:
                id = parseEntity(ApplicationDto.class).getId();
                break;
            case APPLICATION_VERSIONS_PATH:
                id = parseEntity(ApplicationVersionDto.class).getId();
                break;
            case COMMERCIAL_SUBSCRIPTIONS_PATH:
                id = parseEntity(CommercialSubscriptionDto.class).getId();
                break;
            case PARTNERS_PATH:
                id = parseEntity(PartnerDto.class).getId();
                break;
            case USER_CUSTOMER_RELATIONSHIPS_PATH:
                id = parseEntity(UserCustomerRelationshipDto.class).getId();
                break;
            case USER_PROPERTY_RELATIONSHIPS_PATH:
                id = parseEntity(UserPropertyRelationshipDto.class).getId();
                break;
            case USER_PROPERTY_SET_RELATIONSHIPS_PATH:
                id = parseEntity(UserPropertySetRelationshipDto.class).getId();
                break;
            case USER_PARTNER_RELATIONSHIPS_PATH:
                id = parseEntity(UserPartnerRelationshipDto.class).getId();
                break;
            case USER_GROUP_USER_RELATIONSHIPS_PATH:
                id = parseEntity(UserGroupUserRelationshipDto.class).getId();
                break;
            case CUSTOMER_PROPERTY_RELATIONSHIPS_PATH:
                id = parseEntity(CustomerPropertyRelationshipDto.class).getId();
                break;
            case USER_GROUPS_PATH:
                id = parseEntity(UserGroupDto.class).getId();
                break;
            case USER_CUSTOMER_ROLES_PATH:
                id = parseEntity(RoleDto.class).getId();
                break;
            case USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH:
                id = parseEntity(UserCustomerRoleRelationshipDto.class).getId();
                break;
            case USER_GROUP_PROPERTY_RELATIONSHIPS_PATH:
                id = parseEntity(UserGroupPropertyRelationshipDto.class).getId();
                break;
            case USER_GROUP_PROPERTY_ROLE_RELATIONSHIPS_PATH:
                id = parseEntity(UserGroupPropertyRoleRelationshipDto.class).getId();
                break;
            case USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH:
                id = parseEntity(UserGroupPropertySetRelationshipDto.class).getId();
                break;
            case USER_GROUP_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH:
                id = parseEntity(UserGroupPropertySetRoleRelationshipDto.class).getId();
                break;
            case USER_GROUP_ROLE_RELATIONSHIPS_PATH:
                id = parseEntity(UserGroupRoleRelationshipDto.class).getId();
                break;
            case USER_PROPERTY_ROLE_RELATIONSHIPS_PATH:
                id = parseEntity(UserPropertyRoleRelationshipDto.class).getId();
                break;
            case USER_PROPERTY_ROLES_PATH:
                id = parseEntity(RoleDto.class).getId();
                break;
            case USER_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH:
                id = parseEntity(UserPropertySetRoleRelationshipDto.class).getId();
                break;
            case USER_PROPERTY_SET_ROLES_PATH:
                id = parseEntity(RoleDto.class).getId();
                break;
        }
        return id;
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
        requestSpecification.parameters(queryParams);
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

    public <T> T getEntityAsType(String basePath, Class<T> type,  UUID entityId){
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
        createEntity(basePath, entity);
        responseCodeIs(SC_CREATED);
        return getResponseEntityId(basePath);
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

    public Response updateEntityWithEtag(String basePath, UUID entityId, Object data) {
        return updateEntityByUserForAppWithEtag(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

    public Response updateEntityByUserForAppWithEtag(UUID userId, UUID applicationId, String basePath, UUID entityId, Object data) {
        if (userId == null) {
            fail("User ID to be send in request header is null.");
        }
        String etag = getEntityEtagByUserForApp(userId, applicationId, basePath, entityId);
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

    public Response updateEntity(String basePath, UUID entityId, Object data, String etag) {
        return updateEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data, etag);
    }

    public Response updateEntityByUserForApp(UUID userId, UUID applicationId, String basePath, UUID entityId, Object data, String etag) {
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

    // Delete

    // Manual delete method. Expect explicitly passed etag

    public void entityIsDeleted(String basePath, UUID entityId, String etag) {
        deleteEntity(basePath, entityId, etag);
        responseCodeIs(SC_NO_CONTENT);
        getEntity(basePath, entityId);
        responseCodeIs(SC_NOT_FOUND);
    }

    public void deleteEntity(String basePath, UUID entityId, String etag) {
        deleteEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, etag);
    }

    public void deleteEntityByUserForApplication(UUID userId, UUID applicationId, String basePath, UUID entityId, String etag) {
        if (userId == null) {
            fail("User ID to be send in request header is blank.");
        }
        if (isBlank(etag)) {
            fail("Etag to be send in request header is blank.");
        }

        RequestSpecification requestSpecification = given()
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_ETAG, etag)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        Response response = requestSpecification
                .when()
                .delete("/{id}", entityId);
        setSessionResponse(response);
    }

    // Automatic delete methods - do not require explicit etags, get it themselves

    public void entityIsDeletedWithEtag(String basePath, UUID entityId) {
        deleteEntityWithEtag(basePath, entityId);
        responseCodeIs(SC_NO_CONTENT);
        getEntity(basePath, entityId);
        responseCodeIs(SC_NOT_FOUND);
    }

    public void deleteEntityWithEtag(String basePath, UUID entityId) {
        deleteEntityByUserForAppWithEtag(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public void deleteEntityByUserForAppWithEtag(UUID userId, UUID applicationId, String basePath, UUID entityId) {
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

}
