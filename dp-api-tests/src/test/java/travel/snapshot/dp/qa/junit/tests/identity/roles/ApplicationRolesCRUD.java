package travel.snapshot.dp.qa.junit.tests.identity.roles;

import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;

import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

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

        deleteAndCheckRole(createdRole);
    }

    @Test
    public void applicationRoleCrudErrors() {
        commonHelpers.createEntity(ROLES_PATH, singletonMap("name", "invalid role")).then().statusCode(SC_UNPROCESSABLE_ENTITY);

        createdRole = commonHelpers.entityIsCreatedAs(RoleDto.class, testRole1);
        commonHelpers.updateEntity(ROLES_PATH, createdRole.getId(), singletonMap("unrecognizable_field", "anyvalue"))
                .then().statusCode(SC_UNPROCESSABLE_ENTITY);
    }

    @Test
    public void rolesFiltering() {
        roleDtos.values().forEach(commonHelpers::entityIsCreated);

        booleanFiltering();

        wildcardFiltering(roleDtos.values().size());
    }

    @Test
    public void rolesIsAliasForCustomerRoles() {
        createdRole = commonHelpers.entityIsCreatedAs(RoleDto.class, testRole1);
        CustomerRoleDto userCustomerRole = commonHelpers.getEntityAsType(USER_CUSTOMER_ROLES_PATH, CustomerRoleDto.class, createdRole.getId());

        assertThat(createdRole.getName(), is(userCustomerRole.getName()));
        assertThat(createdRole.getIsActive(), is(userCustomerRole.getIsActive()));
        assertThat(createdRole.getIsInitial(), is(userCustomerRole.getIsInitial()));
        assertThat(createdRole.getDescription(), is(userCustomerRole.getDescription()));
        assertThat(createdRole.getApplicationId(), is(userCustomerRole.getApplicationId()));
    }

    //    Help test methods


    private RoleDto createAndGetRole(RoleCreateDto roleCreateDto) {
        createdRole = commonHelpers.entityIsCreatedAs(RoleDto.class, roleCreateDto);
        compareRoles(createdRole, roleCreateDto);

        RoleDto requestedRole = commonHelpers.getEntityAsType(ROLES_PATH, RoleDto.class, createdRole.getId());
        compareRoles(requestedRole, roleCreateDto);

        return createdRole;
    }

    private void updateAndCheckRole(RoleDto role) {
        String updatedName = "Updated name";
        RoleUpdateDto roleUpdate = new RoleUpdateDto();
        roleUpdate.setName(updatedName);
        roleUpdate.setIsActive(false);
        roleUpdate.setIsInitial(true);

        commonHelpers.updateEntity(ROLES_PATH, role.getId(), roleUpdate).then().statusCode(SC_OK);
        RoleDto updatedRole = commonHelpers.getEntityAsType(ROLES_PATH, RoleDto.class, role.getId());
        assertThat(updatedRole.getName(), is(updatedName));
        assertThat(updatedRole.getIsInitial(), is(true));
        assertThat(updatedRole.getIsActive(), is(false));
    }

    private void deleteAndCheckRole(RoleDto role) {
        commonHelpers.deleteEntity(ROLES_PATH, role.getId()).then().statusCode(SC_NO_CONTENT);
        commonHelpers.getEntity(ROLES_PATH, role.getId()).then().statusCode(SC_NOT_FOUND);
    }

    private void booleanFiltering(){
        Map<String, String> params = buildQueryParamMapForPaging("5", null, "is_initial==false", "name", null, null);
        stream(commonHelpers.getEntities(ROLES_PATH, params)
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
        assertThat(commonHelpers.getEntities(ROLES_PATH, params)
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