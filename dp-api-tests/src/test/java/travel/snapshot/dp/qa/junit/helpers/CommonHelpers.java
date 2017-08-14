package travel.snapshot.dp.qa.junit.helpers;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.util.TextUtils.isBlank;
import static org.junit.Assert.fail;

public class CommonHelpers extends BasicSteps {

    public static final String ENTITIES_TO_DELETE = "deleteThese";

    public void updateRegistryOfDeletables(String entityType, String id) {
        // Retrieve the map from serenity session variable
        Map<String, ArrayList<String>> registry = Serenity.sessionVariableCalled(ENTITIES_TO_DELETE);
        // Retrieve the array of ids of the certain enity type
        ArrayList<String> listIds = getArrayFromMap(entityType, registry);
        // Update this list
        listIds.add(id);
        // Put it back to the map and map to session variable
        registry.put(entityType, listIds);
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(registry);
    }

    public ArrayList<String> getArrayFromMap(String aKey, Map<String, ArrayList<String>> inputMap) {
        return (inputMap.get(aKey) == null) ? new ArrayList<>() : inputMap.get(aKey);
    }

    // Get all

    public Response getEntities(String basePath, Map<String, String> queryParams) {
        return getEntitiesByUser(DEFAULT_SNAPSHOT_USER_ID, basePath, queryParams);
    }

    public Response getEntitiesByUser(String userId, String basePath, Map<String, String> queryParams)  {
        return getEntitiesByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, queryParams);
    }

    public Response getEntitiesByUserForApp(String userId, String appId, String basePath, Map<String, String> queryParams) {
        if (isBlank(userId)) {
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


    public Response getEntity(String basePath, String entityId) {
        return getEntityByUser(DEFAULT_SNAPSHOT_USER_ID, basePath, entityId);
    }

    public Response getEntityByUser(String userId, String basePath, String entityId) {
        return getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public Response getEntityByUserForApplication(String userId, String applicationId, String basePath, String entityId) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec);
        if (isBlank(userId)) {
            fail("User ID to be send in request header is null.");
        }
        requestSpecification = requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);
        Response response = requestSpecification.when().get("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    // Get etag

    public String getEntityEtag(String basePath, String entityId) {
        return getEntityEtagByUser(DEFAULT_SNAPSHOT_USER_ID, basePath, entityId);
    }

    public String getEntityEtagByUser(String userId, String basePath, String entityId) {
        return getEntityEtagByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public String getEntityEtagByUserForApp(String userId, String applicationId, String basePath, String entityId) {
        spec.basePath(basePath);
        RequestSpecification requestSpecification = given().spec(spec);
        requestSpecification.header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId);

        return requestSpecification.when().head("/{id}", entityId).getHeader(HEADER_ETAG);
    }



    // Create

    public void entityIsCreated(String basePath, Object entity) throws IOException {
        createEntity(basePath, entity);
        responseCodeIs(SC_CREATED);
    }

    public Response createEntity(String basePath, Object entity) {
        return createEntityByUser(DEFAULT_SNAPSHOT_USER_ID, basePath, entity);
    }

    public Response createEntityByUser(String userId, String basePath, Object entity) {
        return createEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entity);
    }

    public Response createEntityByUserForApplication(String userId, String applicationId, String basePath, Object entity) {
        spec.basePath(basePath);
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).header(HEADER_XAUTH_APPLICATION_ID, applicationId).body(entity).when().post();
        setSessionResponse(response);
        return response;
    }

    // Update

    public Response updateEntityWithEtag(String basePath, String entityId, Object data) {
        return updateEntityByUserWithEtag(DEFAULT_SNAPSHOT_USER_ID, basePath, entityId, data);
    }

    public Response updateEntityByUserWithEtag(String userId, String basePath, String entityId, Object data) {
        return updateEntityByUserForAppWithEtag(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data);
    }

    public Response updateEntityByUserForAppWithEtag(String userId, String applicationId, String basePath, String entityId, Object data) {
        if (isBlank(userId)) {
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

    public Response updateEntity(String basePath, String entityId, Object data, String etag) {
        return updateEntityByUser(DEFAULT_SNAPSHOT_USER_ID, basePath, entityId, data, etag);
    }

    public Response updateEntityByUser(String userId, String basePath, String entityId, Object data, String etag) {
        return updateEntityByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, data, etag);
    }

    public Response updateEntityByUserForApp(String userId, String applicationId, String basePath, String entityId, Object data, String etag) {
        if (isBlank(userId)) {
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

    public void deleteEntity(String basePath, String entityId, String etag) {
        deleteEntityByUser(DEFAULT_SNAPSHOT_USER_ID, basePath, entityId, etag);
    }

    public void deleteEntityByUser(String userId, String basePath, String entityId, String etag) {
        deleteEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId, etag);
    }

    public void deleteEntityByUserForApplication(String userId, String applicationId, String basePath, String entityId, String etag) {
        if (isBlank(userId)) {
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

    public void deleteEntityWithEtag(String basePath, String entityId) {
        deleteEntityByUserWithEtag(DEFAULT_SNAPSHOT_USER_ID, basePath, entityId);
    }

    public void deleteEntityByUserWithEtag(String userId, String basePath, String entityId) {
        deleteEntityByUserForAppWithEtag(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, basePath, entityId);
    }

    public void deleteEntityByUserForAppWithEtag(String userId, String applicationId, String basePath, String entityId) {
        if (isBlank(userId)) {
            fail("User ID to be send in request header is blank.");
        }
        String etag = getEntityEtag(basePath, entityId);
        RequestSpecification requestSpecification = given()
                .spec(spec)
                .basePath(basePath)
                .header(HEADER_XAUTH_USER_ID, userId)
                .header(HEADER_XAUTH_APPLICATION_ID, applicationId)
                .header(HEADER_ETAG, etag);
        Response response = requestSpecification.when().delete("/{id}", entityId);
        setSessionResponse(response);
    }

}
