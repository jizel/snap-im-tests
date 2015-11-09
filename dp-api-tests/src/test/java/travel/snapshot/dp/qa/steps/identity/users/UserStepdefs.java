package travel.snapshot.dp.qa.steps.identity.users;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;

import java.util.List;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.model.Role;
import travel.snapshot.dp.qa.model.User;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

public class UserStepdefs {

    @Steps
    private UsersSteps usersSteps;

    @Given("^The following users exist$")
    public void The_following_roles_exist(List<User> users) throws Throwable {
        usersSteps.followingUsersExist(users);
    }

    @When("^User is created$")
    public void User_is_created(List<User> users) throws Throwable {
        usersSteps.followingUserIsCreated(users.get(0));
    }

    @Then("^Body contains user type with \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_user_type_with_value(String name, String value) throws Throwable {
        usersSteps.bodyContainsUserWith(name, value);
    }

    @Then("^Location header is set and contains the same user$")
    public void Header_is_set_and_contains_the_same_user() throws Throwable {
        usersSteps.responseContainsLocationHeader();
    }

    @When("^User with userName \"([^\"]*)\" is deleted$")
    public void User_with_name_name_is_deleted(String username) throws Throwable {
        usersSteps.deleteUserWithUserName(username);
    }

    @Then("^User with same id doesn't exist$")
    public void User_with_same_id_does_not_exist() throws Throwable {
        usersSteps.userIdInSessionDoesNotExist();
    }

    @When("^Nonexistent user is deleted$")
    public void Nonexistent_user_is_deleted() throws Throwable {
        usersSteps.deleteUserWithId("nonexistent");
    }

    @When("^User with userName \"([^\"]*)\" is updated with data$")
    public void User_with_user_name_updated_with_data(String userName, List<User> users) throws Throwable {
        usersSteps.updateUserWithUserName(userName, users.get(0));
    }

    @Then("^Updated user with userName \"([^\"]*)\" has data$")
    public void Updated_user_with_user_name_has_data(String userName, List<User> users) throws Throwable {
        usersSteps.userWithUserNameHasData(userName, users.get(0));
    }

    @When("^User with userName \"([^\"]*)\" is updated with data if updated before$")
    public void User_with_user_name_is_updated_with_data_if_updated_before(String userName, List<User> users) throws Throwable {
        usersSteps.updateUserWithUserNameIfUpdatedBefore(userName, users.get(0));
    }
}
