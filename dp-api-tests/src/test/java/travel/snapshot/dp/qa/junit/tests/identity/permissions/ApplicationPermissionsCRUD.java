package travel.snapshot.dp.qa.junit.tests.identity.permissions;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_PERMISSIONS_PATH;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.PermissionHelpers.constructAppPermission;
import static travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest.RESTRICTIONS_APPLICATIONS_ENDPOINT;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.ApplicationPermissionCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationPermissionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * CRUD tests for /identity/application_permissions endpoint
 */
public class ApplicationPermissionsCRUD extends CommonTest {

    private ApplicationPermissionCreateDto testApplicationPermission;
    private UUID createdApplicationId;

    private final UUID TEST_PLATFORM_OPERATION_ID = dbSteps.getApplicationPermissionId(RESTRICTIONS_APPLICATIONS_ENDPOINT, GET);

    @Override
    @Before
    public void setUp() {
        super.setUp();
        createdApplicationId = entityIsCreated(testApplication1);
        testApplicationPermission = constructAppPermission(createdApplicationId, TEST_PLATFORM_OPERATION_ID);
    }

    @Test
    public void createAndGetAppPermission() {
        Response response = createEntity(testApplicationPermission);
        responseCodeIs(SC_CREATED);
        ApplicationPermissionDto createdAppPermission = response.as(ApplicationPermissionDto.class);

        ApplicationPermissionDto returnedAppPermission = getEntityAsType(APPLICATION_PERMISSIONS_PATH, ApplicationPermissionDto.class, createdAppPermission.getId());
        assertThat(returnedAppPermission, is(createdAppPermission));
    }

    @Test
    public void deleteAppPermission() {
        UUID createdAppPermissionId = entityIsCreated(testApplicationPermission);

        deleteEntity(APPLICATION_PERMISSIONS_PATH, createdAppPermissionId);
        responseCodeIs(SC_NO_CONTENT);
        assertThat("Deleted App permission is still visible",
                getEntity(APPLICATION_PERMISSIONS_PATH, createdAppPermissionId).getStatusCode(), is(SC_NOT_FOUND));
    }

    @Test
    public void appPermissionsFilteringSorting() {
        UUID createdAppPermissionId = entityIsCreated(testApplicationPermission);

        getEntities(APPLICATION_PERMISSIONS_PATH, buildQueryParamMapForPaging("10", null,
                "id==" + createdAppPermissionId, "application_id", null, null))
                .then()
                .statusCode(SC_OK)
                .header(TOTAL_COUNT_HEADER, "1");

        getEntities(APPLICATION_PERMISSIONS_PATH, buildQueryParamMapForPaging("10", null,
                "application_id==" + testApplicationPermission.getApplicationId(), null, "id", null))
                .then()
                .statusCode(SC_OK)
                .header(TOTAL_COUNT_HEADER, "1");
    }

    @Test
    public void appPermissionsAccessCheck() {
        UUID createdAppPermissionId = entityIsCreated(testApplicationPermission);

        UUID userWithoutPermissionsID = entityIsCreated(testUser2);
        getEntityByUserForApplication(userWithoutPermissionsID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                APPLICATION_PERMISSIONS_PATH, createdAppPermissionId)
                .then()
                .statusCode(SC_NOT_FOUND);

        getEntitiesByUserForApp(userWithoutPermissionsID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, APPLICATION_PERMISSIONS_PATH,
                buildQueryParamMapForPaging(null, null, "id==" + createdAppPermissionId, null, null, null))
                .then()
                .statusCode(SC_OK)
                .header(TOTAL_COUNT_HEADER, "0");
    }
}
