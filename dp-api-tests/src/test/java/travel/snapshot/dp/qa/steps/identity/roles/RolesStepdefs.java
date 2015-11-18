package travel.snapshot.dp.qa.steps.identity.roles;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.Role;
import travel.snapshot.dp.qa.serenity.roles.RolesSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class RolesStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private RolesSteps rolesSteps;


    @Given("^The following roles exist$")
    public void The_following_roles_exist(List<Role> roles) throws Throwable {
        rolesSteps.followingRolesExist(roles);
    }

    @When("^Role is created$")
    public void Role_is_created(List<Role> roles) throws Throwable {
        rolesSteps.followingRoleIsCreated(roles.get(0));
    }

    @Then("^\"([^\"]*)\" header is set and contains the same role$")
    public void header_is_set_and_contains_the_same_role(String header) throws Throwable {

    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is deleted$")
    public void role_with_name_for_application_id_is_deleted(String roleName, String applicationId) throws Throwable {
        rolesSteps.deleteRoleWithNameForApplication(roleName, applicationId);
    }

    @When("^Nonexistent role id is deleted$")
    public void Nonexistent_role_id_is_deleted() throws Throwable {
        rolesSteps.deleteRoleWithId("nonexistent");
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is updated with data$")
    public void role_with_name_for_application_id_is_updated_with_data(String roleName, String applicationId, List<Role> roles) throws Throwable {
        rolesSteps.updateRoleWithNameForApplicationId(roleName, applicationId, roles.get(0));
    }


    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is updated with data if updated before$")
    public void Role_with_name_for_application_id_is_updated_with_data_if_updated_before(String name, String applicationId, List<Role> roles) throws Throwable {
        rolesSteps.updateRoleWithNameForApplicationIdIfUpdatedBefore(name, applicationId, roles.get(0));
    }

    @Then("^Role with same id doesn't exist for application id \"([^\"]*)\"$")
    public void Role_with_same_id_doesn_t_exist_for_application_id(String applicationId) throws Throwable {
        rolesSteps.roleIdInSessionDoesntExist();
    }

    @Then("^Updated role with name \"([^\"]*)\" for application id \"([^\"]*)\" has data$")
    public void Updated_role_with_name_for_application_id_has_data(String roleName, String applicationId, List<Role> roles) throws Throwable {
        rolesSteps.roleWithNameForApplicationIdHasData(roleName, applicationId, roles.get(0));
    }

    @Then("^There are (\\d+) roles returned$")
    public void There_are_roles_returned(int count) throws Throwable {
        rolesSteps.numberOfEntitiesInResponse(Role.class, count);
    }

    @When("^Nonexistent role id got$")
    public void Nonexistent_role_id_got() throws Throwable {
        rolesSteps.getRoleWithId("nonexistent");
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is got$")
    public void Role_with_name_for_application_id_is_got(String roleName, String applicationId) throws Throwable {
        rolesSteps.getRoleWithNameForApplicationId(roleName, applicationId);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is got with etag$")
    public void Role_with_name_for_application_id_is_got_with_etag(String roleName, String applicationId) throws Throwable {
        rolesSteps.getRoleWithNameForApplicationIdUsingEtag(roleName, applicationId);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is got for etag, forced new etag through update$")
    public void Role_with_name_for_application_id_is_got_for_etag_forced_new_etag_through_update(String roleName, String applicationId) throws Throwable {
        rolesSteps.getRoleWithNameForApplicationIdUsingEtagAfterUpdate(roleName, applicationId);
    }

    @Given("^The following roles don't exist$")
    public void The_following_roles_don_t_exist(List<Role> roles) throws Throwable {
        rolesSteps.deleteRoles(roles);
    }

    @When("^List of roles is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_roles_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                             @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                             @Transform(NullEmptyStringConverter.class) String filter,
                                                                                             @Transform(NullEmptyStringConverter.class) String sort,
                                                                                             @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        rolesSteps.listOfRolesIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are roles with following names returned in order: (.*)")
    public void There_are_customers_with_following_codes_returned_in_order(List<String> names) throws Throwable {
        rolesSteps.roleNamesAreInResponseInOrder(names);
    }
}
