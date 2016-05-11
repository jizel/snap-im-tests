package travel.snapshot.dp.qa.steps.identity.user_groups;

import net.thucydides.core.annotations.Steps;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.user_groups.UserGroupsSteps;

/**
 * Created by vlcek on 5/9/2016.
 */
public class UserGroupsdefs {

    @Steps
    private UserGroupsSteps userGroupSteps;

    // ------------------------- GIVEN ------------------------------

    @Given("^The following user groups exist$")
    public void The_following_user_groups_exist(List<UserGroupDto> userGroups) throws Throwable {
        userGroupSteps.followingUserGroupsExist(userGroups);
    }

    // ------------------------- WHEN ------------------------------

    @When("^The following user group is created$")
    public void userGroupWithIdIsCreated(List<UserGroupDto> userGroup) throws Throwable {
        userGroupSteps.followingUserGroupIsCreated(userGroup.get(0));
    }

    @When("^User group with id \"([^\"]*)\" is got$")
    public void userGroupWithIdIsGot(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdGot(userGroupId);

    }

    @When("^User group with id \"([^\"]*)\" is got with etag$")
    public void userGroupWithIdIsGotWithEtag(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdGotWithEtag(userGroupId);
    }

    @When("^User group with id \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void userGroupWithIdIsGotForEtagUpdatedAndGotWithPreviousEtag(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdIsGotWithEtagAfterUpdate(userGroupId);
    }

    @When("^List of user groups is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfUserGroupsIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        userGroupSteps.listUserGroupsIsGot(limit, cursor, filter, sort, sortDesc);
    }

    @When("^User group with id \"([^\"]*)\" is activated$")
    public void userGroupWithIdIsActivated(String userGroupId) throws Throwable {
        userGroupSteps.setUserGroupActiveField(userGroupId, true);
    }

    @When("^User group with id \"([^\"]*)\" is deactivated$")
    public void userGroupWithIdIsDeactivated(String userGroupId) throws Throwable {
        userGroupSteps.setUserGroupActiveField(userGroupId, false);
    }

    @When("^User group with id \"([^\"]*)\" is deleted$")
    public void userGroupWithIdIsDeleted(String userGroupId) throws Throwable {
        userGroupSteps.deleteUserGroup(userGroupId);
    }

    @When("^User group with id \"([^\"]*)\" is updated with following data$")
    public void userGroupWithIdIsUpdatedWithFollowingData(String userGroupId, List<UserGroupUpdateDto> userGroups) throws Throwable {
        userGroupSteps.updateUserGroup(userGroupId, userGroups.get(0));
    }

    // ------------------------- THEN ------------------------------

    @Then("^There are \"([^\"]*)\" user groups returned$")
    public void thereAreUserGroupsReturned(Integer userGroupsCount) throws Throwable {
        userGroupSteps.numberOfEntitiesInResponse(UserGroupDto.class, userGroupsCount);
    }

    @Then("^There are user groups with following description returned in order: \"([^\"]*)\"$")
    public void thereAreUserGroupsWithFollowingDescriptionReturnedInOrder(List<String> order) throws Throwable {
        userGroupSteps.responseSortId(order);
    }

    @Then("^User group with id \"([^\"]*)\" is active$")
    public void userGroupWithIdIsActive(String userGroupId) throws Throwable {
        userGroupSteps.checkUserGroupActiveField(userGroupId, true);
    }

    @Then("^User group with id \"([^\"]*)\" is not active$")
    public void userGroupWithIdIsNotActive(String userGroupId) throws Throwable {
        userGroupSteps.checkUserGroupActiveField(userGroupId, false);
    }


    @Then("^User group with id \"([^\"]*)\" is no more exists$")
    public void userGroupWithIdIsNoMoreExists(String userGroupId) throws Throwable {
        userGroupSteps.checkUserGroupExistency(userGroupId, false);
    }

    @Then("^User group with id \"([^\"]*)\" contains following data$")
    public void userGroupWithIdContainsFollowingData(String userGroupId, List<UserGroupUpdateDto> userGroups) throws Throwable {
        userGroupSteps.checkUserGroupData(userGroupId, userGroups.get(0));
    }
}
