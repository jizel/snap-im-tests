package travel.snapshot.dp.qa.junit.helpers;

import static com.jayway.restassured.RestAssured.given;
import static net.serenitybdd.core.Serenity.sessionVariableCalled;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_AUTHORIZATION;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_ETAG;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_IF_MATCH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.SESSION_TOKEN;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.setSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ENTITIES_TO_DELETE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getCreateBasePath;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;
import static travel.snapshot.dp.qa.junit.utils.RestAssuredConfig.setupRequestDefaults;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthorizationHelpers {

    private final CommonHelpers commonHelpers = new CommonHelpers();
    protected RequestSpecification spec = null;
    protected static PropertiesHelper propertiesHelper = new PropertiesHelper();

    public AuthorizationHelpers() {
        spec = setupRequestDefaults();
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

    public Response sendGetRequest(String path, Map<String, String> queryParams, Map<String, String> headers) {
        RequestSpecification specification = constructRequestSpecification(path, null);
        specification.baseUri(propertiesHelper.getProperty("authorization.baseURI") + propertiesHelper.getProperty("version"));
        if (queryParams != null) {
            specification.parameters(queryParams);
        }
        if (headers != null) {
            specification.headers(headers);
        }
        return specification.when().get();
    }

    private RequestSpecification constructRequestSpecification(String basePath, String etag) {
        spec.baseUri(propertiesHelper.getProperty("identity_nginx.baseURI"));
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

    public <CDTO> Response createEntity(CDTO entity) {
        String basePath = getCreateBasePath(entity);
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .body(entity)
                .when()
                .post();
        setSessionResponse(response);
        return response;
    }

    public <DTO> UUID entityIsCreated(DTO entity) {
        String basePath = getCreateBasePath(entity);
        createEntity(entity)
            .then().statusCode(SC_CREATED);
        UUID result = null;
        try {
            result = commonHelpers.getDtoFromResponse(getSessionResponse(), basePath).getId();
        } catch (Exception e) {
            fail(e.getMessage());
        }
        updateRegistryOfDeletables(basePath, result);
        return result;
    }


    public void entityIsUpdated(String basePath, UUID entityId, Object data) {
        updateEntity(basePath, entityId, data)
                .then()
                .statusCode(SC_NO_CONTENT);
    }

    public Response updateEntity(String basePath, UUID entityId, Object data) {
        String etag = getEntityEtag(basePath, entityId);
        RequestSpecification specification = constructRequestSpecification(basePath, etag);
        Response response = specification
                .body(data)
                .when()
                .post("/{id}", entityId);
        setSessionResponse(response);
        return response;
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
        deleteEntity(basePath, entityId)
                .then()
                .statusCode(SC_NO_CONTENT);
        // verify it's not there
        getEntity(basePath, entityId)
                .then()
                .statusCode(SC_NOT_FOUND);
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
