package travel.snapshot.dp.qa.serenity.roles;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RoleBaseSteps extends BasicSteps {

    private static final String SESSION_ROLE_ID = "role_id";
    private static final String ROLES_PATH = "/identity/roles";
    private static final String SESSION_CREATED_ROLE = "created_role";
    public static final String USER_CUSTOMER_ROLES_PATH = "/identity/user_customer_roles";
    public static final String USER_PROPERTY_SET_ROLES_PATH = "/identity/user_property_set_roles";
    public static final String USER_PROPERTY_ROLES_PATH = "/identity/user_property_roles";


    public RoleBaseSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    public void setRolesPathCustomer(){
        spec.basePath(USER_CUSTOMER_ROLES_PATH);
    }
    public void setRolesPathProperty(){
        spec.basePath(USER_PROPERTY_ROLES_PATH);
    }
    public void setRolesPathPropertySet(){
        spec.basePath(USER_PROPERTY_SET_ROLES_PATH);
    }

    public String getBasePath() {
        return "";
    }

    @Step
    public void followingRolesExist(List<RoleDto> roles) {
        roles.forEach(r -> {
            RoleDto existingRole = getRoleByNameForApplicationInternal(r.getRoleName(), r.getApplicationId());
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
    public void followingRoleIsCreated(RoleDto role) {
        RoleDto existingRole = getRoleByNameForApplicationInternal(role.getRoleName(), role.getApplicationId());

        setSessionVariable(SESSION_CREATED_ROLE, role);

        if (existingRole != null) {
            deleteRole(existingRole.getRoleId());
        }

        Response response = createRole(role);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }


    private Response createRole(RoleDto r) {
        return given().spec(spec).body(r).when().post();
    }

    private Response updateRole(String id, Map<String, Object> role, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);

        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }

        return requestSpecification.body(role).when().post("/{id}", id);

    }


    private Response deleteRole(String id) {
        return given().spec(spec).when().delete("/{id}", id);
    }

    private Response getRole(String id, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);

        if (!StringUtils.isBlank(etag)) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }

        return requestSpecification.when().get("/{id}", id);
    }

    public RoleDto getRoleByNameForApplicationInternal(String name, String applicationId) {
        String filter = String.format("name=='%s' and application_id=='%s'", name, applicationId);
        RoleDto[] roles = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(RoleDto[].class);
        return Arrays.asList(roles).stream().findFirst().orElse(null);
    }

    public RoleDto getRoleByName(String name) {
        String filter = String.format("name=='%s'", name);
        RoleDto[] roles = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(RoleDto[].class);
        return Arrays.asList(roles).stream().findFirst().orElse(null);
    }


    @Step
    public RoleDto getRoleWithId(String roleId) {
        Response resp = getRole(roleId, null);
        setSessionResponse(resp);
        return resp.as(RoleDto.class);
    }

    @Step
    public void getRoleWithNameForApplicationId(String name, String applicationId) {
        //TODO implement actual customer search
        RoleDto roleByName = getRoleByNameForApplicationInternal(name, applicationId);

        Response resp = getRole(roleByName.getRoleId(), null);
        setSessionResponse(resp);
    }

    @Step
    public void deleteRoleWithId(String roleId) {
        Response resp = deleteRole(roleId);
        setSessionResponse(resp);
    }

    @Step
    public void deleteRoleWithNameForApplication(String name, String applicationId) {
        RoleDto r = getRoleByNameForApplicationInternal(name, applicationId);

        if (r == null) {
            return;
        }

        String roleId = r.getRoleId();
        Response resp = deleteRole(roleId);

        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
        Serenity.setSessionVariable(SESSION_ROLE_ID).to(roleId);
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
        setSessionResponse(response);
    }

    @Step
    public void updateRoleWithNameForApplicationId(String name, String applicationId, RoleDto updatedRole) {
        RoleDto original = getRoleByNameForApplicationInternal(name, applicationId);
        Response tempResponse = getRole(original.getRoleId(), null);

        Map<String, Object> role = new HashMap<>();
        if (StringUtils.isNotBlank(updatedRole.getDescription())) {
            role.put("role_description", updatedRole.getDescription());
        }

        if (StringUtils.isNotBlank(updatedRole.getRoleName())) {
            if (!name.equals(updatedRole.getRoleName())) {
                role.put("name", updatedRole.getRoleName());
            }
        }

        if (StringUtils.isNotBlank(updatedRole.getApplicationId())) {
            role.put("application_id", updatedRole.getApplicationId());
        }


        Response response = updateRole(original.getRoleId(), role, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void getRoleWithNameForApplicationIdUsingEtag(String name, String applicationId) {
        //TODO implement actual customer search
        RoleDto roleFromList = getRoleByNameForApplicationInternal(name, applicationId);

        Response tempResponse = getRole(roleFromList.getRoleId(), null);

        Response resp = getRole(roleFromList.getRoleId(), tempResponse.getHeader(HEADER_ETAG));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }

    @Step
    public void getRoleWithNameForApplicationIdUsingEtagAfterUpdate(String name, String applicationId) {
        RoleDto roleFromList = getRoleByNameForApplicationInternal(name, applicationId);

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

    public void updateRoleWithNameForApplicationIdIfUpdatedBefore(String name, String applicationId, RoleDto updatedRole) {
        RoleDto original = getRoleByNameForApplicationInternal(name, applicationId);
        Response tempResponse = getRole(original.getRoleId(), null);


        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("role_description", "changed description");

        Response updateResponse = updateRole(original.getRoleId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Role cannot be updated: " + updateResponse.asString());
        }

        Map<String, Object> role = new HashMap<>();
        if (updatedRole.getDescription() != null && !"".equals(updatedRole.getDescription())) {
            role.put("role_description", updatedRole.getDescription());
        }

        Response response = updateRole(original.getRoleId(), role, tempResponse.getHeader(HEADER_ETAG));
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void roleWithNameForApplicationIdHasData(String name, String applicationId, RoleDto data) {
        RoleDto roleByName = getRoleByNameForApplicationInternal(name, applicationId);

        if (StringUtils.isNotBlank(data.getDescription())) {
            assertEquals(data.getDescription(), roleByName.getDescription());
        }
        if (StringUtils.isNotBlank(data.getRoleName())) {
            assertEquals(data.getRoleName(), roleByName.getRoleName());
        }
    }

    @Step
    public void deleteRoles(List<RoleDto> roles) {
        roles.forEach(r -> {
            RoleDto existingRole = getRoleByNameForApplicationInternal(r.getRoleName(), r.getApplicationId());
            if (existingRole != null) {
                deleteRole(existingRole.getRoleId());
            }
        });
    }

    public void roleNamesAreInResponseInOrder(List<String> names) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        RoleDto[] roles = response.as(RoleDto[].class);
        int i = 0;
        for (RoleDto r : roles) {
            assertEquals("Role on index=" + i + " is not expected", names.get(i), r.getRoleName());
            i++;
        }
    }

    public void compareRoleOnHeaderWithStored(String headerName, String header) throws Exception {
        RoleDto originalRole = Serenity.sessionVariableCalled(SESSION_CREATED_ROLE);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String roleLocation = response.header(headerName).replaceFirst(header, "");
        given().spec(spec).get(roleLocation).then()
                .body("application_id", is(originalRole.getApplicationId()))
                .body("role_description", is(originalRole.getDescription()))
                .body("name", is(originalRole.getRoleName()));
    }
}
