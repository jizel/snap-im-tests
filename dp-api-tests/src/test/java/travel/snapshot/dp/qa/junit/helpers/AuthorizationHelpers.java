package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static net.serenitybdd.core.Serenity.sessionVariableCalled;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ENTITIES_TO_DELETE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.authorization.AuthorizationSteps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthorizationHelpers extends AuthorizationSteps {

    private final CommonHelpers commonHelpers = new CommonHelpers();

    public AuthorizationHelpers() {
        super();
    }

    public void updateRegistryOfDeletables(String basePath, UUID id) {
        // Retrieve the map from serenity session variable
        Map<String, List<UUID>> registry = Serenity.sessionVariableCalled(ENTITIES_TO_DELETE);
        // Retrieve the array of ids of the certain entity type
        List<UUID> listIds = registry.getOrDefault(basePath, new ArrayList<>());
        // Update this list
        listIds.add(id);
        // Put it back to the map and map to session variable
        registry.put(basePath, listIds);
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(registry);
    }

    private RequestSpecification constructRequestSpecification(String basePath, String etag) {
        spec.baseUri(PropertiesHelper.getProperty("identity_nginx.baseURI"));
        spec.basePath(basePath);
        RequestSpecification specification = given().spec(spec);
        specification = specification
                .header(HEADER_AUTHORIZATION, "Bearer " + sessionVariableCalled(SESSION_TOKEN))
                .relaxedHTTPSValidation();
        if (etag != null) {
            specification = specification.header(HEADER_IF_MATCH, etag);
        }
        return specification;
    }

    public <T> List<T> getEntitiesAsType(String basePath, Class<T> clazz, Map<String, String> queryParams) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        specification.parameters(queryParams);
        Response response = specification
                .when()
                .get();
        setSessionResponse(response);
        return parseResponseAsListOfObjects(clazz);
    }

    public Response createEntity(String basePath, Object entity) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .body(entity)
                .when()
                .post();
        setSessionResponse(response);
        return response;
    }

    public UUID entityIsCreated(String basePath, Object entity) {
        Response response = createEntity(basePath, entity);
        responseCodeIs(SC_CREATED);
        UUID result = null;
        try {
            result = commonHelpers.getDtoFromResponse(response, basePath).getId();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        updateRegistryOfDeletables(basePath, result);
        return result;
    }


    public void entityIsUpdated(String basePath, UUID entityId, Object data) {
        updateEntity(basePath, entityId, data);
        responseCodeIs(SC_NO_CONTENT);
    }

    public void updateEntity(String basePath, UUID entityId, Object data) {
        String etag = getEntityEtag(basePath, entityId);
        RequestSpecification specification = constructRequestSpecification(basePath, etag);
        Response response = specification
                .body(data)
                .when()
                .post("/{id}", entityId);
        setSessionResponse(response);
    }

    public Response deleteEntity(String basePath, UUID entityId) {
        RequestSpecification specification = constructRequestSpecification(basePath, getEntityEtag(basePath, entityId));
        Response response = specification
                .when()
                .delete("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    public void entityIsDeleted(String basePath, UUID entityId) {
        // delete entity
        deleteEntity(basePath, entityId);
        responseCodeIs(SC_NO_CONTENT);
        // verify it's not there
        getEntity(basePath, entityId);
        responseCodeIs(SC_NOT_FOUND);
    }

    public String getEntityEtag(String basePath, UUID entityId) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        return specification
                .when()
                .head("/{id}", entityId)
                .getHeader(HEADER_ETAG);
    }

    public Response getEntity(String basePath, UUID entityId) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .when()
                .get("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    public Response createSecondLevelRelation(String basePath, UUID firstLevelId, String secondLevelName, Object jsonBody) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .body(jsonBody)
                .post("/" + firstLevelId + "/" + secondLevelName);
        setSessionResponse(response);
        return response;
    }

    public void createThirdLevelRelation(String basePath, UUID firstLevelId, String secondLevelName, UUID secondLevelId, String thirdLevelName, Object jsonBody) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .body(jsonBody)
                .post("/" + firstLevelId + "/" + secondLevelName + "/" + secondLevelId + "/" + thirdLevelName);
        setSessionResponse(response);
    }

    public void thirdLevelRelationIsCreated(String basePath, UUID firstLevelId, String secondLevelName, UUID secondLevelId, String thirdLevelName, Object jsonBody) {
        createThirdLevelRelation(basePath, firstLevelId, secondLevelName, secondLevelId, thirdLevelName, jsonBody);
        responseCodeIs(SC_CREATED);
    }

    public void deleteThirdLevelRelation(String basePath, UUID firstLevelId, String secondLevelName, UUID secondLevelId, String thirdLevelName, UUID thirdLevelId) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .delete("/" + firstLevelId + "/" + secondLevelName + "/" + secondLevelId + "/" + thirdLevelName + "/" + thirdLevelId);
        setSessionResponse(response);
    }

    public void thirdLevelRelationIsDeleted(String basePath, UUID firstLevelId, String secondLevelName, UUID secondLevelId, String thirdLevelName, UUID thirdLevelId) {
        deleteThirdLevelRelation(basePath, firstLevelId, secondLevelName, secondLevelId, thirdLevelName, thirdLevelId);
        responseCodeIs(SC_NO_CONTENT);
    }

    public String getSecondLevelEntityEtag(String basePath, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        return specification
                .when()
                .head("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId)
                .getHeader(HEADER_ETAG);
    }

    public void getSecondLevelEntities(String basePath, UUID firstLevelId, String secondLevelName) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .when()
                .get("/{firstLevelId}/{secondLevelName}", firstLevelId, secondLevelName);
        setSessionResponse(response);
    }

    public void deleteSecondLevelEntity(String basePath, UUID firstLevelId, String secondLevelObjectName, UUID secondLevelId) {
        String etag = getSecondLevelEntityEtag(basePath, firstLevelId, secondLevelObjectName, secondLevelId);
        RequestSpecification specification = constructRequestSpecification(basePath, etag);
        Response response = specification
                .when()
                .delete("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
        setSessionResponse(response);
    }
}
