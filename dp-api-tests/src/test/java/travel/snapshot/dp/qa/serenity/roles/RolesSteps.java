package travel.snapshot.dp.qa.serenity.roles;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Role;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class RolesSteps extends BasicSteps {


    private static final String SESSION_ROLE_ID = "role_id";

    public RolesSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath("/identity/roles");
    }

    @Step
    public void followingRolesExist(List<Role> roles) {
        roles.forEach(r -> {
            Role existingRole = getRoleByNameForApplication(r.getRoleName(), r.getApplicationId());
            if (existingRole != null) {
                deleteRole(existingRole.getRoleId());
            }
            Response createResponse = createRole(r);
            if (createResponse.getStatusCode() != 201) {
                fail("Role cannot be created");
            }
        });

    }

    @Step
    public void followingRoleIsCreated(Role role) {
        Role existingRole = getRoleByNameForApplication(role.getRoleName(), role.getApplicationId());
        if (existingRole != null) {
            deleteRole(existingRole.getRoleId());
        }
        Response response = createRole(role);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    private Response createRole(Role r) {
        return given().spec(spec)
                .body(r)
                .when().post();

    }

    private Response updateRole(String id, Map<String, Object> role, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (etag != null && !"".equals(etag)) {
            requestSpecification = requestSpecification.header("If-Match", etag);
        }
        return requestSpecification.body(role).when().post("/{id}", id);

    }

    private Response deleteRole(String id) {
        return given().spec(spec)
                .when().delete("/{id}", id);
    }

    private Response getRole(String id, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (etag != null && !"".equals(etag)) {
            requestSpecification = requestSpecification.header("If-None-Match", etag);
        }
        return requestSpecification.when().get("/{id}", id);
    }

    /**
     * getting roles over rest api, if limit and cursor is null or empty, it's not added to query string
     *
     * @param limit
     * @param cursor
     * @param filter
     *@param sort
     * @param sortDesc @return
     */
    private Response getRoles(String limit, String cursor, String filter, String sort, String sortDesc) {
        RequestSpecification requestSpecification = given().spec(spec);

        if (cursor != null) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null) {
            requestSpecification.parameter("limit", limit);
        }
        if (filter != null) {
            requestSpecification.parameter("filter", filter);
        }
        if (sort != null) {
            requestSpecification.parameter("sort", sort);
        }
        if (sortDesc != null) {
            requestSpecification.parameter("sort_desc", sortDesc);
        }
        return requestSpecification.when().get();
    }

    private Role getRoleByNameForApplication(String name, String applicationId) {
        Role[] roles = getRoles("100", "0", null, null, null).as(Role[].class);
        return Arrays.asList(roles).stream().filter(p -> name.equals(p.getRoleName()) && applicationId.equals(p.getApplicationId())).findFirst().orElse(null);
    }


    @Step
    public void getRoleWithId(String roleId) {
        Response resp = getRole(roleId, null);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void getRoleWithNameForApplicationId(String name, String applicationId) {
        //TODO implement actual customer search
        Role roleByName = getRoleByNameForApplication(name, applicationId);

        Response resp = getRole(roleByName.getRoleId(), null);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void deleteRoleWithId(String roleId) {
        Response resp = deleteRole(roleId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void deleteRoleWithNameForApplication(String name, String applicationId) {
        String roleId = getRoleByNameForApplication(name, applicationId).getRoleId();
        Response resp = deleteRole(roleId);//delete role
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
        Serenity.setSessionVariable(SESSION_ROLE_ID).to(roleId);//store to session
    }

    @Step
    public void roleIdInSessionDoesntExist() {
        String roleId = Serenity.sessionVariableCalled(SESSION_ROLE_ID);

        Response response = getRole(roleId, null);
        response.then().statusCode(404);
    }

    @Step
    public void listOfRolesIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getRoles(limit, cursor, filter, sort, sortDesc);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void numberOfRolesIsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Role[] roles = response.as(Role[].class);
        assertEquals("There should be " + count + " roles got", count, roles.length);
    }

    @Step
    public void updateRoleWithNameForApplicationId(String name, String applicationId, Role updatedRole) {
        Role original = getRoleByNameForApplication(name, applicationId);
        Response tempResponse = getRole(original.getRoleId(), null);

        Map<String, Object> role = new HashMap<>();
        if (updatedRole.getRoleDescription() != null && !"".equals(updatedRole.getRoleDescription())) {
            role.put("role_description", updatedRole.getRoleDescription());
        }
        if (updatedRole.getRoleName() != null && !"".equals(updatedRole.getRoleName())) {
            if (!name.equals(updatedRole)) { //update only if changed
                role.put("role_name", updatedRole.getRoleName());
            }
        }


        Response response = updateRole(original.getRoleId(), role, tempResponse.getHeader("ETag"));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void bodyDoesNotContainRoleWith(String atributeName) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, nullValue());
    }

    @Step
    public void bodyContainsRoleWith(String atributeName) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, notNullValue());
    }

    @Step
    public void bodyContainsRoleWith(String atributeName, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, is(value));
    }

    @Step
    public void getRoleWithNameForApplicationIdUsingEtag(String name, String applicationId) {
        //TODO implement actual customer search
        Role roleFromList = getRoleByNameForApplication(name, applicationId);

        Response tempResponse = getRole(roleFromList.getRoleId(), null);

        Response resp = getRole(roleFromList.getRoleId(), tempResponse.getHeader("ETag"));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void getRoleWithNameForApplicationIdUsingEtagAfterUpdate(String name, String applicationId) {
        Role roleFromList = getRoleByNameForApplication(name, applicationId);

        Response tempResponse = getRole(roleFromList.getRoleId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("role_description", "updated because of etag");

        Response updateResponse = updateRole(roleFromList.getRoleId(), mapForUpdate, tempResponse.getHeader("ETag"));

        if (updateResponse.getStatusCode() != 204) {
            fail("Role cannot be updated: " + updateResponse.asString());
        }

        Response resp = getRole(roleFromList.getRoleId(), tempResponse.getHeader("ETag"));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    public void updateRoleWithNameForApplicationIdIfUpdatedBefore(String name, String applicationId, Role updatedRole) {
        Role original = getRoleByNameForApplication(name, applicationId);
        Response tempResponse = getRole(original.getRoleId(), null);


        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("role_description", "changed description");

        Response updateResponse = updateRole(original.getRoleId(), mapForUpdate, tempResponse.getHeader("ETag"));

        if (updateResponse.getStatusCode() != 204) {
            fail("Role cannot be updated: " + updateResponse.asString());
        }

        Map<String, Object> role = new HashMap<>();
        if (updatedRole.getRoleDescription() != null && !"".equals(updatedRole.getRoleDescription())) {
            role.put("role_description", updatedRole.getRoleDescription());
        }

        Response response = updateRole(original.getRoleId(), role, tempResponse.getHeader("ETag"));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    public void roleWithNameForApplicationIdHasData(String name, String applicationId, Role data) {
        Role roleByName = getRoleByNameForApplication(name, applicationId);

        if (data.getRoleDescription() != null && !"".equals(data.getRoleDescription())) {
            assertEquals(data.getRoleDescription(), roleByName.getRoleDescription());
        }
        if (data.getRoleName() != null && !"".equals(data.getRoleName())) {
            assertEquals(data.getRoleName(), roleByName.getRoleName());
        }
    }

    @Step
    public void deleteRoles(List<Role> roles) {
        roles.forEach(r -> {
            Role existingRole = getRoleByNameForApplication(r.getRoleName(), r.getApplicationId());
            if (existingRole != null) {
                deleteRole(existingRole.getRoleId());
            }
        });
    }

    public void roleNamesAreInResponseInOrder(List<String> names) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Role[] roles = response.as(Role[].class);
        int i = 0;
        for(Role r: roles) {
            assertEquals("Role on index=" + i + " is not expected", names.get(i), r.getRoleName());
            i++;
        }
    }
}
