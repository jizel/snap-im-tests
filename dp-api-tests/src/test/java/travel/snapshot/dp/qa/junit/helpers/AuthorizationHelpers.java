package travel.snapshot.dp.qa.junit.helpers;

import com.fasterxml.jackson.databind.type.TypeFactory;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.authorization.AuthorizationSteps;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.jayway.restassured.RestAssured.given;
import static net.serenitybdd.core.Serenity.sessionVariableCalled;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;

public class AuthorizationHelpers extends AuthorizationSteps {


    public AuthorizationHelpers() {
        super();
    }

    public RequestSpecification constructRequestSpecification(String basePath, String etag) {
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

    public <T> List<T> getEntities(String basePath, Class<T> clazz, Map<String, String> queryParams) {
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
