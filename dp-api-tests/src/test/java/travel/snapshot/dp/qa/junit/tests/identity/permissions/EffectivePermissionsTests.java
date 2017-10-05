package travel.snapshot.dp.qa.junit.tests.identity.permissions;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.EFFECTIVE_PERMISSIONS_PATH;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.PermissionHelpers.constructAppPermission;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.EffectivePermissionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Tests for /identity/effective_permissions endpoint
 *
 * Access checks are not employed for this endpoint because only nginx has access to it
 */
public class EffectivePermissionsTests extends CommonRestrictionTest {

    private static String USER_ID = "user_id";
    private static String APP_VERSION_ID = "application_version_id";

    private Map<String, String> pathParams;
    private List<EffectivePermissionDto> effectivePermissions;
    private UUID createdUserId;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_EFFECTIVE_PERMISSIONS, GET);
        createdUserId = entityIsCreated(testUser1);
        pathParams = new HashMap<>();
        pathParams.put(USER_ID, createdUserId.toString());
        pathParams.put(APP_VERSION_ID, createdAppVersion.getId().toString());
    }

    @Test
    public void getEffectivePermissionsBasedOnAppVersion() {
        effectivePermissions = collectEffectivePermissionsWithParams(pathParams);
        assertThat(effectivePermissions)
                .hasSize(1)
                .extracting(EffectivePermissionDto::getUriTemplate)
                .containsOnly(RESTRICTIONS_EFFECTIVE_PERMISSIONS);

        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_USERS_ENDPOINT, POST);
        effectivePermissions = collectEffectivePermissionsWithParams(pathParams);
        assertThat(effectivePermissions)
                .hasSize(2)
                .extracting(EffectivePermissionDto::getUriTemplate)
                .containsOnly(RESTRICTIONS_EFFECTIVE_PERMISSIONS, RESTRICTIONS_ALL_USERS_ENDPOINT);
    }

    @Test
    public void checkMandatoryParameters() {
        getAllEffectivePermissionsWithParams(null)
                .then()
                .statusCode(SC_BAD_REQUEST);
        customCodeIs(CC_BAD_PARAMS);

        getAllEffectivePermissionsWithParams(singletonMap(USER_ID, DEFAULT_SNAPSHOT_USER_ID.toString()))
                .then()
                .statusCode(SC_BAD_REQUEST);
        customCodeIs(CC_BAD_PARAMS);

        getAllEffectivePermissionsWithParams(singletonMap(APP_VERSION_ID, createdAppVersion.getId().toString()))
                .then()
                .statusCode(SC_BAD_REQUEST);
        customCodeIs(CC_BAD_PARAMS);
    }

    @Test
    @Jira({"DPIM-127", "DPIM-130"})
    public void userPermissionsAreReturnedWhenAppRoleIsDefined() {
        //        Prepare data
        entityIsCreated(constructAppPermission(
                restrictedApp.getId(),
                platformOperationHelpers.getPlatformOperationId(GET, RESTRICTIONS_ALL_USERS_ENDPOINT, false))
        );
        testRole1.setApplicationId(restrictedApp.getId());
        UUID createdRoleId = entityIsCreated(testRole1);
        entityIsCreated(relationshipsHelpers.constructRolePermission(createdRoleId, GET, RESTRICTIONS_EFFECTIVE_PERMISSIONS, false));
        entityIsCreated(relationshipsHelpers.constructRoleAssignment(createdRoleId, createdUserId));
        pathParams.put(USER_ID, createdUserId.toString());

        //        Test with inactive role
        effectivePermissions = getEffectivePermissionsByUserForApp(createdUserId, createdAppVersion.getId(), pathParams);
        assertThat(effectivePermissions)
                .hasSize(2)
                .extracting(EffectivePermissionDto::getUriTemplate)
                .containsOnly(RESTRICTIONS_EFFECTIVE_PERMISSIONS, RESTRICTIONS_ALL_USERS_ENDPOINT);

        //        Get permissions as user - with active role
        roleHelpers.setRoleIsActive(createdRoleId, true);
        effectivePermissions = getEffectivePermissionsByUserForApp(createdUserId, createdAppVersion.getId(), pathParams);
        assertThat(effectivePermissions)
                .hasSize(1)
                .extracting(EffectivePermissionDto::getUriTemplate)
                .containsOnly(RESTRICTIONS_EFFECTIVE_PERMISSIONS);
        assertThat(effectivePermissions)
                .extracting(EffectivePermissionDto::getHttpMethod)
                .containsOnly(GET);
    }

    @Test
    public void appPermissionMustExistBeforeRolePermission() {
        testRole1.setApplicationId(restrictedApp.getId());
        UUID createdRoleId = entityIsCreated(testRole1);
        createEntity(relationshipsHelpers.constructRolePermission(createdRoleId, GET, RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT, false))
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(42203);
    }

    @Test
    public void snapshotUserWithInternalAppIgnoreRoles() {
        setDefaultUserAndApplicationPathParams();
        effectivePermissions = getEffectivePermissionsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, pathParams);

        testRole1.setApplicationId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        entityIsCreated(testRole1);
        List<EffectivePermissionDto> effectivePermissionsAfterRoleIsCreated = getEffectivePermissionsByUserForApp(
                DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, pathParams
        );
        assertThat(effectivePermissionsAfterRoleIsCreated).isEqualTo(effectivePermissions);
    }

    private Response getAllEffectivePermissionsWithParams(Map<String, String> params) {
        return getEntities(EFFECTIVE_PERMISSIONS_PATH,
                buildQueryParamMapForPaging(null, null, null, null, null, params));
    }

    private List<EffectivePermissionDto> collectEffectivePermissionsWithParams(Map<String, String> params) {
        return stream(getAllEffectivePermissionsWithParams(params)
                .then()
                .statusCode(SC_OK)
                .extract().response().as(EffectivePermissionDto[].class)).collect(toList());
    }

    private List<EffectivePermissionDto> getEffectivePermissionsByUserForApp(UUID userId, UUID applicationVersionId, Map<String, String> params) {
        return getEntitiesAsTypeByUserForApp(
                userId,
                applicationVersionId,
                EFFECTIVE_PERMISSIONS_PATH,
                EffectivePermissionDto.class,
                buildQueryParamMapForPaging(null, null, null, null, null, params)
        );
    }

    private void setDefaultUserAndApplicationPathParams(){
        pathParams.put(USER_ID, DEFAULT_SNAPSHOT_USER_ID.toString());
        pathParams.put(APP_VERSION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID.toString());
    }
}
