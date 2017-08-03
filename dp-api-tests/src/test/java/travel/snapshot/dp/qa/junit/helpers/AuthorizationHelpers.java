package travel.snapshot.dp.qa.junit.helpers;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.authorization.AuthorizationSteps;

import static com.jayway.restassured.RestAssured.given;
import static net.serenitybdd.core.Serenity.sessionVariableCalled;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

public class AuthorizationHelpers extends AuthorizationSteps {


    public AuthorizationHelpers() {super(); }

    public RequestSpecification constructRequestSpecification(String basePath, String etag) {
        spec.baseUri(PropertiesHelper.getProperty("identity_nginx.baseURI"));
        spec.basePath(basePath);
        RequestSpecification specification = given().spec(spec);
        specification = specification.header(HEADER_AUTHORIZATION, "Bearer " + sessionVariableCalled(SESSION_TOKEN));
        if (etag != null) {
            specification = specification.header(HEADER_IF_MATCH, etag);
        }
        return specification;
    }

    protected Response createEntity(String basePath, Object entity) {
        RequestSpecification specification = constructRequestSpecification(basePath,null);
        Response response = specification
                .body(entity)
                .when()
                .post();
        setSessionResponse(response);
        return response;
    }

    protected Response updateEntity(String basePath, String entityId, Object data, String etag) {
        RequestSpecification specification = constructRequestSpecification(basePath, etag);
        Response response = specification
                .body(data)
                .when()
                .post("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    public Response deleteEntity(String basePath, String entityId) {
        RequestSpecification specification = constructRequestSpecification(basePath, getEntityEtag(basePath, entityId));
        Response response = specification
                .when()
                .delete("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    public void entityIsDeleted(String basePath, String entityId) {
        deleteEntity(basePath, entityId);
        responseCodeIs(SC_NO_CONTENT);
    }

    public String getEntityEtag(String basePath, String entityId) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        return specification
                .when()
                .head("/{id}", entityId)
                .getHeader(HEADER_ETAG);
    }

    public Response getEntity(String basePath, String entityId) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .when()
                .get("/{id}", entityId);
        setSessionResponse(response);
        return response;
    }

    protected Response createSecondLevelRelation(String basePath, String firstLevelId, String secondLevelName, Object jsonBody) {
        RequestSpecification specification = constructRequestSpecification(basePath, null);
        Response response = specification
                .body(jsonBody)
                .post("/" + firstLevelId + "/" + secondLevelName);
        setSessionResponse(response);
        return response;
    }

    protected String getSecondLevelEntityEtag(String basePath, String firstLevelId, String secondLevelObjectName, String secondLevelId) {
        RequestSpecification specification = constructRequestSpecification(basePath,null);
        return specification
                .when()
                .head("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId)
                .getHeader(HEADER_ETAG);
    }

    protected void deleteSecondLevelEntity(String basePath, String firstLevelId, String secondLevelObjectName, String secondLevelId) {
        String etag = getSecondLevelEntityEtag(basePath, firstLevelId, secondLevelObjectName, secondLevelId);
        RequestSpecification specification = constructRequestSpecification(basePath, etag);
        Response response = specification
                .when()
                .delete("/{firstLevelId}/{secondLevelName}/{secondLevelId}", firstLevelId, secondLevelObjectName, secondLevelId);
        setSessionResponse(response);
    }
}
