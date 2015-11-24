package travel.snapshot.dp.qa.serenity.roles;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Role;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class RolesSteps extends BasicSteps {


    private static final String SESSION_ROLE_ID = "role_id";
    private static final String ROLES_PATH = "/identity/roles";
    private static final String SESSION_CREATED_ROLE = "created_role";

    public RolesSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(ROLES_PATH);
    }

    @Step
    public void followingRolesExist(List<Role> roles) {
        roles.forEach(r -> {
            Role existingRole = getRoleByNameForApplication(r.getRoleName(), r.getApplicationId());
            if (existingRole != null) {
                deleteRole(existingRole.getRoleId());
            }
            Response createResponse = createRole(r);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Role cannot be created");
            }
        });

    }

    @Step
    public void followingRoleIsCreated(Role role) {
        Role existingRole = getRoleByNameForApplication(role.getRoleName(), role.getApplicationId());
        setSessionVariable(SESSION_CREATED_ROLE, role);
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
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(role).when().post("/{id}", id);

    }

    private Response deleteRole(String id) {
        return given().spec(spec)
                .when().delete("/{id}", id);
    }

    private Response getRole(String id, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }
        return requestSpecification.when().get("/{id}", id);
    }

    private Role getRoleByNameForApplication(String name, String applicationId) {
        String filter = String.format("role_name=='%s' and application_id=='%s'", name, applicationId);
        Role[] roles = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(Role[].class);
        return Arrays.asList(roles).stream().findFirst().orElse(null);
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
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void listOfRolesIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void updateRoleWithNameForApplicationId(String name, String applicationId, Role updatedRole) {
        Role original = getRoleByNameForApplication(name, applicationId);
        Response tempResponse = getRole(original.getRoleId(), null);

        Map<String, Object> role = new HashMap<>();
        if (StringUtils.isNotBlank(updatedRole.getRoleDescription())) {
            role.put("role_description", updatedRole.getRoleDescription());
        }
        if (StringUtils.isNotBlank(updatedRole.getRoleName())) {
            if (!name.equals(updatedRole.getRoleName())) { //update only if changed
                role.put("role_name", updatedRole.getRoleName());
            }
        }

        Response response = updateRole(original.getRoleId(), role, tempResponse.getHeader(HEADER_ETAG));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void getRoleWithNameForApplicationIdUsingEtag(String name, String applicationId) {
        //TODO implement actual customer search
        Role roleFromList = getRoleByNameForApplication(name, applicationId);

        Response tempResponse = getRole(roleFromList.getRoleId(), null);

        Response resp = getRole(roleFromList.getRoleId(), tempResponse.getHeader(HEADER_ETAG));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    @Step
    public void getRoleWithNameForApplicationIdUsingEtagAfterUpdate(String name, String applicationId) {
        Role roleFromList = getRoleByNameForApplication(name, applicationId);

        Response tempResponse = getRole(roleFromList.getRoleId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("role_description", "updated because of etag");

        Response updateResponse = updateRole(roleFromList.getRoleId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Role cannot be updated: " + updateResponse.asString());
        }

        Response resp = getRole(roleFromList.getRoleId(), tempResponse.getHeader(HEADER_ETAG));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
    }

    public void updateRoleWithNameForApplicationIdIfUpdatedBefore(String name, String applicationId, Role updatedRole) {
        Role original = getRoleByNameForApplication(name, applicationId);
        Response tempResponse = getRole(original.getRoleId(), null);


        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("role_description", "changed description");

        Response updateResponse = updateRole(original.getRoleId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Role cannot be updated: " + updateResponse.asString());
        }

        Map<String, Object> role = new HashMap<>();
        if (updatedRole.getRoleDescription() != null && !"".equals(updatedRole.getRoleDescription())) {
            role.put("role_description", updatedRole.getRoleDescription());
        }

        Response response = updateRole(original.getRoleId(), role, tempResponse.getHeader(HEADER_ETAG));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    public void roleWithNameForApplicationIdHasData(String name, String applicationId, Role data) {
        Role roleByName = getRoleByNameForApplication(name, applicationId);

        if (StringUtils.isNotBlank(data.getRoleDescription())) {
            assertEquals(data.getRoleDescription(), roleByName.getRoleDescription());
        }
        if (StringUtils.isNotBlank(data.getRoleName())) {
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
        for (Role r : roles) {
            assertEquals("Role on index=" + i + " is not expected", names.get(i), r.getRoleName());
            i++;
        }
    }

    public void compareRoleOnHeaderWithStored(String headerName) {
        Role originalRole = Serenity.sessionVariableCalled(SESSION_CREATED_ROLE);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String roleLocation = response.header(headerName).replaceFirst(ROLES_PATH, "");
        given().spec(spec).get(roleLocation).then()
                .body("application_id", is(originalRole.getApplicationId()))
                .body("role_description", is(originalRole.getRoleDescription()))
                .body("role_name", is(originalRole.getRoleName()));
    }
}
