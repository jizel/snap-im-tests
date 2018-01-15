package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;

public class CommonAccessCheckByUserTest extends CommonTest {

    protected UUID requestorId;

    @Before
    @BeforeAll
    public void setUp() {
        super.setUp();
    }

    // CREATE

    <CDTO> Response createEntityFails(UUID requestorId, CDTO entity) {
        return createEntity(requestorId, entity)
                .then().statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_NON_EXISTING_REFERENCE)).extract().response();
    }

    <CDTO> Response createEntitySucceeds(UUID requestorId, CDTO entity) {
        return createEntity(requestorId, entity)
                .then().statusCode(SC_CREATED).extract().response();
    }

    private <CDTO> Response createEntity(UUID requestorId, CDTO entity) {
        return createEntityByUserForApplication(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, entity);
    }


    // GET
    void getEntityFails(UUID requestorId, String endpoint, UUID id) {
        getEntity(requestorId, endpoint, id)
                .then().statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
    }

    void getEntitySucceeds(UUID requestorId, String endpoint, UUID id) {
        getEntity(requestorId, endpoint, id)
                .then().statusCode(SC_OK);
    }

    private Response getEntity(UUID requestorId, String endpoint, UUID id) {
        return getEntityByUserForApplication(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, id);
    }

    // UPDATE

    void updateEntityFails(UUID requestorId, String endpoint, UUID id, Object update) {
        updateEntity(requestorId, endpoint, id, update).then().statusCode(SC_NOT_FOUND).assertThat().body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
    }

    void updateEntitySucceeds(UUID requestorId, String endpoint, UUID id, Object update) {
        updateEntity(requestorId, endpoint, id, update).then().statusCode(SC_OK);
    }

    Response updateEntity(UUID requestorId, String endpoint, UUID id, Object update) {
        return updateEntityByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, id, update);
    }

    // DELETE
    void deleteEntityFails(UUID requestorId, String endpoint, UUID id, Integer responseCode) {
        deleteEntity(requestorId, endpoint, id).then().statusCode(responseCode);
    }

    void deleteEntitySucceeds(UUID requestorId, String endpoint, UUID id) {
        deleteEntity(requestorId, endpoint, id).then().statusCode(SC_NO_CONTENT);
    }

    private Response deleteEntity(UUID requestorId, String endpoint, UUID id) {
        return deleteEntityByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, id);
    }

}
