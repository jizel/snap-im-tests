package travel.snapshot.dp.qa.serenity.roles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;


public class RoleBaseSteps extends BasicSteps {

    private static final String SESSION_ROLE_ID = "role_id";
    private static final String SESSION_CREATED_ROLE = "created_role";
    public static final String USER_CUSTOMER_ROLES_PATH = "/identity/user_customer_roles";
    public static final String USER_PROPERTY_SET_ROLES_PATH = "/identity/user_property_set_roles";
    public static final String USER_PROPERTY_ROLES_PATH = "/identity/user_property_roles";
    private String roleBasePath = "";


    public RoleBaseSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    public void setRolesPathCustomer() {
        spec.basePath(USER_CUSTOMER_ROLES_PATH);
        roleBasePath = USER_CUSTOMER_ROLES_PATH;
    }

    public void setRolesPathProperty() {
        spec.basePath(USER_PROPERTY_ROLES_PATH);
        roleBasePath = USER_PROPERTY_ROLES_PATH;
    }

    public void setRolesPathPropertySet() {
        spec.basePath(USER_PROPERTY_SET_ROLES_PATH);
        roleBasePath = USER_PROPERTY_SET_ROLES_PATH;
    }

    public String getBasePath() {
        return roleBasePath;
    }

    @Step
    public void followingRolesExist(List<RoleDto> roles) {
        roles.forEach(role -> {
            Response createResponse = createEntity(role);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Role cannot be created! Status:" + createResponse.getStatusCode() + " " + createResponse.body().asString());
            }
        });
    }

    @Step
    public void followingRoleIsCreated(RoleDto role) {
        setSessionVariable(SESSION_CREATED_ROLE, role);
        Response response = createEntity(role);
        setSessionResponse(response);
    }

    @Step
    public void updateRole(String roleId, RoleUpdateDto roleUpdate, String etag) {
        try {
            String updatedRoleString = retrieveData(roleUpdate).toString();
            Response response = updateEntity(roleId, updatedRoleString, etag);
            setSessionResponse(response);
        }catch(JsonProcessingException jsonException){
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    @Step
    public Response getRole(String id, String etag) {
        return getEntity(id, etag);
    }

    public RoleDto getRoleByNameForApplicationInternal(String name, String applicationId) {
        String filter = String.format("name=='%s' and application_id=='%s'", name, applicationId);
        RoleDto[] roles = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(RoleDto[].class);
        return stream(roles).findFirst().orElse(null);
    }

    public RoleDto getRoleByNameForApplicationInternalUsingCustomerRole(String name, String applicationId) {
        setRolesPathCustomer();
        return getRoleByNameForApplicationInternal(name, applicationId);
    }

    public RoleDto getRoleByName(String name) {
        String filter = String.format("name=='%s'", name);
        RoleDto[] roles = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(RoleDto[].class);
        return stream(roles).findFirst().orElse(null);
    }


    @Step
    public Response getRoleWithId(String roleId) {
        Response resp = getRole(roleId, null);
        setSessionResponse(resp);
        return resp;
    }

    @Step
    public void getRoleWithNameForApplicationId(String name, String applicationId) {
        //TODO implement actual customer search
        RoleDto roleByName = getRoleByNameForApplicationInternal(name, applicationId);

        Response resp = getRole(roleByName.getRoleId(), null);
        setSessionResponse(resp);
    }

    @Step
    public Response deleteRole(String roleId) {
        Response response = deleteEntityWithEtag(roleId);
        setSessionResponse(response);
        setSessionVariable(SESSION_ROLE_ID, roleId);
        return response;
    }

    @Step
    public void roleIdInSessionDoesntExist() {
        String roleId = Serenity.sessionVariableCalled(SESSION_ROLE_ID);

        Response response = getRole(roleId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void listOfRolesIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
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

    public void compareRoleOnHeaderWithStored(String headerName) throws Exception {
        RoleDto originalRole = Serenity.sessionVariableCalled(SESSION_CREATED_ROLE);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String roleLocation = response.header(headerName).replaceFirst(getBasePath(), "");
        given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).get(roleLocation).then()
                .body("application_id", is(originalRole.getApplicationId()))
                .body("description", is(originalRole.getDescription()))
                .body("name", is(originalRole.getRoleName()));
    }

    public String resolveRoleId(String roleName) {
        String roleId;
        if (isUUID(roleName)) {
            roleId = roleName;
        } else {
            RoleDto role = getRoleByName(roleName);
            assertThat(String.format("Role with name \"%s\" does not exist", roleName), role, is(notNullValue()));
            roleId = role.getRoleId();
        }
        return roleId;
    }
}
