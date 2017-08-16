package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.qa.cucumber.helpers.RoleType;
import travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps;

import java.util.UUID;

/**
 * Created by zelezny on 6/26/2017.
 */
public class RoleHelpers extends RoleBaseSteps {
    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    public RoleDto roleIsCreated(RoleDto role, RoleType roleType) {
        setRolesPath(roleType);
        Response response = createRole(role);
        assertEquals(String.format("Failed to create role: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(roleType.getDtoClassType());
    }

    public UUID customerRoleIsCreatedWithAuth(Object role) {
        authorizationHelpers.createEntity(USER_CUSTOMER_ROLES_PATH, role);
        responseCodeIs(SC_CREATED);
        UUID roleId = getSessionResponse().as(CustomerRoleDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(ROLES_RESOURCE, roleId);
        return roleId;
    }

    public UUID propertyRoleIsCreatedWithAuth(Object role) {
        authorizationHelpers.createEntity(USER_PROPERTY_ROLES_PATH, role);
        responseCodeIs(SC_CREATED);
        UUID roleId = getSessionResponse().as(PropertyRoleDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(ROLES_RESOURCE, roleId);
        return roleId;
    }
}
