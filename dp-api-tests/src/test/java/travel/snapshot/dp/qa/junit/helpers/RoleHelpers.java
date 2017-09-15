package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.RoleCreateBaseDto;
import travel.snapshot.dp.qa.cucumber.helpers.RoleType;
import travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps;

/**
 * Helper class for roles. Obsolete dependency on RoleBaseSteps will be removed when obsolete role types are removed completely from IM.
 */
public class RoleHelpers extends RoleBaseSteps {

    @Deprecated
    public RoleBaseDto roleIsCreated(RoleCreateBaseDto role, RoleType roleType) {
        setRolesPath(roleType);
        Response response = createRole(role);
        assertEquals(String.format("Failed to create role: %s", response.toString()), response.getStatusCode(), SC_CREATED);
        return response.as(roleType.getDtoClassType());
    }
}
