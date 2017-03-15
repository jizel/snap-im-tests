package travel.snapshot.dp.qa.steps.identity.roles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

import cucumber.api.DataTable;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.roles.RoleBaseSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RolesStepdefs {

    @Steps
    private RoleBaseSteps roleBaseSteps;

    @Given("^Switch for user customer role tests$")
    public void switchForUserCustomerRoleTests() throws Throwable {
        roleBaseSteps.setRolesPathCustomer();
    }

    @Given("^Switch for user property role tests$")
    public void switchForUserPropertyRoleTests() throws Throwable {
        roleBaseSteps.setRolesPathProperty();
    }

    @Given("^Switch for user property set role tests$")
    public void switchForUserPropertySetRoleTests() throws Throwable {
        roleBaseSteps.setRolesPathPropertySet();
    }

    @Given("^The following roles exist$")
    public void The_following_roles_exist(DataTable rolesTable) throws Throwable {
        List<Map<String, Object>> roleAttributes = rolesTable.asMaps(String.class, Object.class);
        roleBaseSteps.followingRolesExist(roleAttributes);
    }

    @When("^Role is created$")
    public void Role_is_created(DataTable rolesTable) throws Throwable {
        List<Map<String, Object>> roleAttributes = rolesTable.asMaps(String.class, Object.class);
//        Cannot just use get(0) because of the the way list of maps is represented. Mergining all maps to one here:
        Map<String, Object> roleMap = new HashMap<>();
        for(Map<String, Object> singleAttributeMap : roleAttributes){
            roleMap.putAll(singleAttributeMap);
        }
        roleBaseSteps.createRoleWithType(roleMap);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same role$")
    public void header_is_set_and_contains_the_same_role(String header) throws Throwable {
        roleBaseSteps.compareRoleOnHeaderWithStored(header);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is deleted$")
    public void role_with_name_for_application_id_is_deleted(String roleName, String applicationId) throws Throwable {
//        TODO: Remove applicationId from this step, refactor all usages
        RoleDto role = roleBaseSteps.getRoleByName(roleName);
        assertThat(role, is(notNullValue()));
        roleBaseSteps.deleteRole(role.getId());
    }

    @When("^Nonexistent role id is deleted$")
    public void Nonexistent_role_id_is_deleted() throws Throwable {
        roleBaseSteps.deleteRole("nonexistent");
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is updated with data$")
    public void role_with_name_for_application_id_is_updated_with_data(String roleName, String applicationId, List<RoleUpdateDto> roles) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByName(roleName);
        assertThat(role,is(notNullValue()));
        String etag = roleBaseSteps.getEntityEtag(role.getId());
        roleBaseSteps.updateRole(role.getId(), roles.get(0), etag);
    }


    @When("^Role with name \"([^\"]*)\" is updated with data if updated before$")
    public void Role_with_name_for_application_id_is_updated_with_data_if_updated_before(String roleName, List<RoleUpdateDto> roles) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByName(roleName);
        assertThat(role,is(notNullValue()));
        String originalEtag = roleBaseSteps.getEntityEtag(role.getId());
        RoleUpdateDto firstUpdate = new RoleUpdateDto();
        firstUpdate.setDescription("first update");
        roleBaseSteps.updateRole(role.getId(), firstUpdate, originalEtag);
        assertThat("Update was not successfull", roleBaseSteps.getSessionResponse().getStatusCode(), is(HttpStatus.SC_NO_CONTENT));

        roleBaseSteps.updateRole(role.getId(), roles.get(0), originalEtag);
    }

    @Then("^Role with same id doesn't exist for application id \"([^\"]*)\"$")
    public void Role_with_same_id_doesn_t_exist_for_application_id(String applicationId) throws Throwable {
        roleBaseSteps.roleIdInSessionDoesntExist();
    }

    @Then("^Updated role with name \"([^\"]*)\" has data$")
    public void Updated_role_with_name_for_application_id_has_data(String roleName, List<RoleDto> roles) throws Throwable {
        RoleDto requestedRole = roleBaseSteps.getRoleByName(roleName);
        assertThat("Role does not exists", requestedRole, is(notNullValue()));
        assertThat("Roles have different application ids", requestedRole.getId(), is(roles.get(0).getId()));
        assertThat("Roles have different names", requestedRole.getRoleName(), is(roles.get(0).getRoleName()));
        assertThat("Roles have different application descriptions", requestedRole.getDescription(), is(roles.get(0).getDescription()));
    }

    @Then("^There are (\\d+) roles returned$")
    public void There_are_roles_returned(int count) throws Throwable {
        roleBaseSteps.numberOfEntitiesInResponse(RoleDto.class, count);
    }

    @When("^Nonexistent role id got$")
    public void Nonexistent_role_id_got() throws Throwable {
        roleBaseSteps.getRoleWithId("nonexistent");
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is got$")
    public void Role_with_name_for_application_id_is_got(String roleName, String applicationId) throws Throwable {
        roleBaseSteps.getRoleWithNameForApplicationId(roleName, applicationId);
    }

    @Given("^The following roles don't exist$")
    public void The_following_roles_don_t_exist(List<RoleDto> roles) throws Throwable {
        roleBaseSteps.deleteRoles(roles);
    }

    @When("^List of roles is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_roles_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                             @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                             @Transform(NullEmptyStringConverter.class) String filter,
                                                                                             @Transform(NullEmptyStringConverter.class) String sort,
                                                                                             @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        roleBaseSteps.listOfRolesIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are roles with following names returned in order: (.*)")
    public void There_are_customers_with_following_codes_returned_in_order(List<String> names) throws Throwable {
        roleBaseSteps.roleNamesAreInResponseInOrder(names);
    }
}
