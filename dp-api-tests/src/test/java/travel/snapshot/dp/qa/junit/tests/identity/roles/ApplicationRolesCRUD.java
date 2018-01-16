package travel.snapshot.dp.qa.junit.tests.identity.roles;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_PRECONDITION_FAILED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_ETAG;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtag;

import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.junit.helpers.CommonHelpers;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * CRUD tests for /identity/roles endpoint
 */
@Jira("DPIM-127")
public class ApplicationRolesCRUD extends CommonTest {

    private RoleDto createdRole;

    @Test
    public void applicationRoleCrud() {
        createdRole = createAndGetRole(testRole1);

        updateAndCheckRole(createdRole);

        entityIsDeleted(ROLES_PATH, createdRole.getId());
    }

    @Test
    public void applicationRoleCrudErrors() {
        testRole1.setApplicationId(null);
        createEntity(ROLES_PATH, testRole1)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY);
        testRole1.setApplicationId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        testRole1.setName(null);
        createEntity(ROLES_PATH, testRole1)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY);
        testRole1.setName("Some name");
        createdRole = entityIsCreatedAs(RoleDto.class, testRole1);
        RoleUpdateDto updateDto = new RoleUpdateDto();
        updateDto.setDescription("New desc");
        updateEntityWithEtag(ROLES_PATH, createdRole.getId(), updateDto, DEFAULT_SNAPSHOT_ETAG)
                .then()
                .statusCode(SC_PRECONDITION_FAILED)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_INVALID_ETAG));
        updateEntity(ROLES_PATH, createdRole.getId(), singletonMap("unrecognizable_field", "anyvalue"))
                .then().statusCode(SC_UNPROCESSABLE_ENTITY);
    }

    @Jira({"DP-1661", "DPIM-198"})
    @Test
    public void roleNameIsUniqueWithinApplication() {
        entityIsCreated(testRole1);
        createEntity(testRole1)
                .then()
                .statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_CONFLICT_VALUES))
                .body("message", is("The tuple (application_id, name) must be unique."));
        UUID applicationId = entityIsCreated(testApplication1);
        testRole1.setApplicationId(applicationId);
        entityIsCreated(testRole1);
    }

    @Test
    public void rolesFiltering() {
        roleDtos.values().forEach(CommonHelpers::entityIsCreated);

        booleanFiltering();

        wildcardFiltering(roleDtos.values().size());
    }

    @Test
    public void rolesIsAliasForCustomerRoles() {
        createdRole = entityIsCreatedAs(RoleDto.class, testRole1);
        CustomerRoleDto userCustomerRole = getEntityAsType(USER_CUSTOMER_ROLES_PATH, CustomerRoleDto.class, createdRole.getId());

        assertThat(createdRole.getName(), is(userCustomerRole.getName()));
        assertThat(createdRole.getIsActive(), is(userCustomerRole.getIsActive()));
        assertThat(createdRole.getIsInitial(), is(userCustomerRole.getIsInitial()));
        assertThat(createdRole.getDescription(), is(userCustomerRole.getDescription()));
        assertThat(createdRole.getApplicationId(), is(userCustomerRole.getApplicationId()));
    }

    @Test
    public void appIdCannotBeChangedAfterRoleIsCreated() {
        createdRole = entityIsCreatedAs(RoleDto.class, testRole1);
        UUID anotherCreatedApplication = entityIsCreated(testApplication2);
        testRole1.setApplicationId(anotherCreatedApplication);
        updateEntity(ROLES_PATH, createdRole.getId(), testRole1)
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY);
    }

    //    Help test methods


    private RoleDto createAndGetRole(RoleCreateDto roleCreateDto) {
        createdRole = entityIsCreatedAs(RoleDto.class, roleCreateDto);
        compareRoles(createdRole, roleCreateDto);

        RoleDto requestedRole = getEntityAsType(ROLES_PATH, RoleDto.class, createdRole.getId());
        compareRoles(requestedRole, roleCreateDto);

        return createdRole;
    }

    private void updateAndCheckRole(RoleDto role) {
        String updatedName = "Updated name";
        RoleUpdateDto roleUpdate = new RoleUpdateDto();
        roleUpdate.setName(updatedName);
        roleUpdate.setIsActive(false);
        roleUpdate.setIsInitial(true);

        updateEntity(ROLES_PATH, role.getId(), roleUpdate).then().statusCode(SC_OK);
        RoleDto updatedRole = getEntityAsType(ROLES_PATH, RoleDto.class, role.getId());
        assertThat(updatedRole.getName(), is(updatedName));
        assertThat(updatedRole.getIsInitial(), is(true));
        assertThat(updatedRole.getIsActive(), is(false));
    }


    private void booleanFiltering(){
        Map<String, String> params = buildQueryParamMapForPaging("5", null, "is_initial==false", "name", null, null);
        stream(getEntities(ROLES_PATH, params)
                .then().statusCode(SC_OK)
                .extract().response()
                .as(RoleDto[].class))
                .forEach(roleDto -> {
                    assertThat(roleDto.getIsInitial(), is(false));
                    assertNotNull(roleDto.getId());
                    assertNotNull(roleDto.getVersion());
                });
    }

    private void wildcardFiltering(int expectedCount){
        Map<String, String> params = buildQueryParamMapForPaging("5", null, "name==*Role*", null, "is_active", null);
        assertThat(getEntities(ROLES_PATH, params)
                .then().statusCode(SC_OK)
                .extract().response().as(RoleDto[].class)
                .length, is(expectedCount));
    }

    private void compareRoles(RoleDto actualRole, RoleCreateDto expectedRole) {
        assertThat(actualRole.getName(), is(expectedRole.getName()));
        assertThat(actualRole.getApplicationId(), is(expectedRole.getApplicationId()));
        assertThat(actualRole.getDescription(), is(expectedRole.getDescription()));
        assertThat(actualRole.getIsActive(), is(expectedRole.getIsActive()));
        assertThat(actualRole.getIsInitial(), is(expectedRole.getIsInitial()));
    }
}
