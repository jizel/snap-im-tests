package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Collections.emptyMap;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_IF_MATCH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_XAUTH_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.HEADER_XAUTH_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_DETAILS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityEtag;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.ENDPOINTS_WITH_IDS_MAP;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.READ_WRITE_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.utils.RestAssuredConfig.setupRequestDefaults;

import com.jayway.restassured.specification.RequestSpecification;
import lombok.extern.java.Log;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * Tests common for all endpoints
 * TODO: rewrite to JUnit5 dynamic tests for fun.
 */
@Log
public class AllEndpointsTests extends CommonTest {

    private final Map<String, String> emptyUpdate = emptyMap();
    private static final String EMPTY_BODY_MESSAGE = "Request body is missing";


    @Test
    public void getRemoveNonExistentEntity() {
        READ_WRITE_ENDPOINTS.forEach(endpoint -> {
            log.info("Endpoint is " + endpoint);
            getEntity(endpoint, NON_EXISTENT_ID);
            responseIsEntityNotFound();
            deleteEntity(endpoint, NON_EXISTENT_ID);
            responseIsEntityNotFound();
        });
    }

    @Test
    public void sendEmptyPostCreate() {
        READ_WRITE_ENDPOINTS.forEach(e -> prepareEmptyRequest()
                .basePath(e)
                .post()
                .then().statusCode(SC_UNPROCESSABLE_ENTITY)
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                .body(RESPONSE_DETAILS, hasItem(EMPTY_BODY_MESSAGE)));
    }

    @Test
    public void sendEmptyPostUpdate(){
        ENDPOINTS_WITH_IDS_MAP.forEach((e, id) ->
                prepareEmptyUpdate(e, id)
                        .post("/{id}", id)
                        .then()
                        .statusCode(SC_UNPROCESSABLE_ENTITY)
                        .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                        .body(RESPONSE_DETAILS, hasItem(EMPTY_BODY_MESSAGE)));
    }

    @Test
    public void sendEmptyBodyPatchTest() {
        ENDPOINTS_WITH_IDS_MAP.forEach((e, id) ->
                prepareEmptyUpdate(e, id)
                        .patch("/{id}", id)
                        .then()
                        .statusCode(SC_UNPROCESSABLE_ENTITY)
                        .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS))
                        .body(RESPONSE_DETAILS, hasItem(EMPTY_BODY_MESSAGE)));
    }

    @Test
    @Jira("DPIM-214")
    public void sendEmptyJsonPatchTest() {
        ENDPOINTS_WITH_IDS_MAP.forEach((k, v) ->
                updateEntity(k, v, emptyUpdate)
                        .then().statusCode(SC_UNPROCESSABLE_ENTITY));
    }

    private RequestSpecification prepareEmptyUpdate(String basePath, UUID entityId) {
        log.info("Endpoint is " + basePath);

        return prepareEmptyRequest()
                .header(HEADER_IF_MATCH, getEntityEtag(basePath, entityId))
                .basePath(basePath);
    }

    private RequestSpecification prepareEmptyRequest() {
        return given().spec(setupRequestDefaults())
                .header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
    }
}
