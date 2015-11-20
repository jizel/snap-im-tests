package travel.snapshot.dp.qa.steps.identity.users;

import net.thucydides.core.annotations.Steps;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.CustomerUser;
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

    @Then("^\"([^\"]*)\" header is set and contains the same user$")
    public void header_is_set_and_contains_the_same_user(String header) throws Throwable {
        usersSteps.compareUserOnHeaderWithStored(header);
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

    @When("^User with username \"([^\"]*)\" is got$")
    public void User_with_username_is_got(String username) throws Throwable {
        usersSteps.userWithUsernameIsGot(username);
    }

    @When("^User with username \"([^\"]*)\" is got with etag$")
    public void User_with_username_is_got_with_etag(String username) throws Throwable {
        usersSteps.userWithUsernameIsGotWithEtag(username);
    }

    @When("^User with username \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void User_with_username_is_got_for_etag_updated_and_got_with_previous_etag(String username) throws Throwable {
        usersSteps.userWithUsernameIsGotWithEtagAfterUpdate(username);
    }

    @When("^Nonexistent user id is got$")
    public void Nonexistent_user_id_is_got() throws Throwable {
        usersSteps.userWithIdIsGot("nonexistent");
    }

    @When("^List of users is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                             @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                             @Transform(NullEmptyStringConverter.class) String filter,
                                                                                             @Transform(NullEmptyStringConverter.class) String sort,
                                                                                             @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        usersSteps.listOfUsersIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) users returned$")
    public void There_are_returned_users_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(User.class, count);
    }

    @Then("^There are (\\d+) customerUsers returned$")
    public void There_are_returned_customerUsers_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(CustomerUser.class, count);
    }

    @Then("^There are users with following usernames returned in order: (.*)$")
    public void There_are_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        usersSteps.usernamesAreInResponseInOrder(usernames);
    }
}
