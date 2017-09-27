package travel.snapshot.dp.qa.junit.tests.identity.permissions;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.EFFECTIVE_PERMISSIONS_PATH;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;

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

    private Map<String, String> paramsMap;
    private List<EffectivePermissionDto> effectivePermissions;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_EFFECTIVE_PERMISSIONS, GET);
        paramsMap = new HashMap<>();
        paramsMap.put(USER_ID, DEFAULT_SNAPSHOT_USER_ID.toString());
        paramsMap.put(APP_VERSION_ID, createdAppVersion.getId().toString());
    }

    @Test
    public void getEffectivePermissionsBasedOnAppVersion() {
        effectivePermissions = collectEffectivePermissionsWithParams(paramsMap);
        assertThat(effectivePermissions.size(), is(1));

        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_USERS_ENDPOINT, POST);
        effectivePermissions = collectEffectivePermissionsWithParams(paramsMap);
        assertThat(effectivePermissions.size(), is(2));
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
    @Jira("DPIM-127")
    public void userPermissionsAreReturnedWhenAppRoleIsDefined() {
        UUID createdUserId = commonHelpers.entityIsCreated(testUser1);
        UUID createdRoleId = commonHelpers.entityIsCreated(testRole1);
        commonHelpers.entityIsCreated(
                relationshipsHelpers.constructRolePermission(createdRoleId, GET, RESTRICTIONS_EFFECTIVE_PERMISSIONS)
        );
        commonHelpers.entityIsCreated(
                relationshipsHelpers.constructRoleAssignment(createdRoleId, createdUserId)
        );
        paramsMap.put(USER_ID, createdUserId.toString());
        roleHelpers.setRoleIsActive(createdRoleId, true);

        Response response = commonHelpers.getEntitiesByUserForApp(createdUserId, createdAppVersion.getId(), EFFECTIVE_PERMISSIONS_PATH,
                buildQueryParamMapForPaging(null, null, null, null, null, paramsMap))
                .then()
                .statusCode(SC_OK)
                .extract().response();
        effectivePermissions = stream(response.as(EffectivePermissionDto[].class)).collect(toList());

        assertThat(effectivePermissions.size(), is(1));
        assertThat(effectivePermissions.get(0).getHttpMethod(), is(GET));
        assertThat(effectivePermissions.get(0).getUriTemplate(), is(RESTRICTIONS_EFFECTIVE_PERMISSIONS));
    }


    private Response getAllEffectivePermissionsWithParams(Map<String, String> params) {
        return commonHelpers.getEntities(EFFECTIVE_PERMISSIONS_PATH,
                buildQueryParamMapForPaging(null, null, null, null, null, params));
    }

    private List<EffectivePermissionDto> collectEffectivePermissionsWithParams(Map<String, String> params) {
        return stream(getAllEffectivePermissionsWithParams(params)
                .then()
                .statusCode(SC_OK)
                .extract().response().as(EffectivePermissionDto[].class)).collect(toList());
    }
}
