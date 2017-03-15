package travel.snapshot.dp.qa.steps.identity.user_groups;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.roles.RoleBaseSteps;
import travel.snapshot.dp.qa.serenity.user_groups.UserGroupsSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vlcek on 5/9/2016.
 */
public class UserGroupsdefs {

    private static final String USER_ID = "userId";
    private static final String USER_GROUP_ID = "userGroupId";

    @Steps
    private UserGroupsSteps userGroupSteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private RoleBaseSteps roleSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionSteps;

    //    Help methods
    private Map<String, String> getNonNullIdsFromNames(String userGroupName, String userName) {
        String userId = usersSteps.resolveUserId(userName);
        String userGroupId = userGroupSteps.resolveUserGroupId(userGroupName);

        Map<String, String> userGroupUserIds = new HashMap<>();
        userGroupUserIds.put(USER_ID, userId);
        userGroupUserIds.put(USER_GROUP_ID, userGroupId);

        return userGroupUserIds;
    }
//    End of help method section

    // ------------------------- GIVEN ------------------------------

    @Given("^The following user groups exist$")
    public void The_following_user_groups_exist(List<UserGroupDto> userGroups) throws Throwable {
        userGroupSteps.followingUserGroupsExist(userGroups);
    }

    @Given("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" exists(?: with isActive \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertyExistsWithIsActive(String userGroupName, String propertyCode, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        userGroupSteps.relationshipGroupPropertyExist(userGroup.getId(), property.getId(), isActive);
    }

    @Given("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" exists(?: with isActive \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertySetExistsWithIsActive(String userGroupName, String propertySetName, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String userGroupId = userGroupSteps.resolveUserGroupId(userGroupName);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        userGroupSteps.relationshipGroupPropertySetExist(userGroupId, propertySetId, isActive);
    }

    // ------------------------- WHEN ------------------------------

    @When("^The following user group is created(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userGroupWithIdIsCreated(String username, String applicationVersionName, List<UserGroupDto> userGroup) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.followingUserGroupIsCreatedByUserForApp(userId, applicationVersionId, userGroup.get(0));
    }

    @When("^User group \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userGroupIsRequestedByUser(String userGroupName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.getUserGroupByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID));
    }


    @When("^User group with non existent UserGroupId is got$")
    public void userGroupWithNonExistentUserGroupIdIsGot() throws Throwable {
        userGroupSteps.getUserGroup(NON_EXISTENT_ID);
    }


    @When("^List of user groups is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfUserGroupsIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        userGroupSteps.listUserGroupsIsGot(limit, cursor, filter, sort, sortDesc);
    }


    @When("^List of user groups is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfUserGroupsIsGotWithLimitAndCursorAndFilterAndSortAndSort_descByUser(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                          @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                          @Transform(NullEmptyStringConverter.class) String filter,
                                                                                          @Transform(NullEmptyStringConverter.class) String sort,
                                                                                          @Transform(NullEmptyStringConverter.class) String sortDesc,
                                                                                          String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.listUserGroupsIsGotByUserForApp(userId, applicationVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of relationships userGroups-Roles for userGroup \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRelationshipsUserGroupsRolesIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userGroupName,
                                                                                                      @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                      @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                      @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                      @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                      @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        UserGroupDto usergGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(usergGroup, is(notNullValue()));
        userGroupSteps.listGroupRoleIsGot(usergGroup.getId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all relationships userGroups-Roles for userGroup \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllRelationshipsUserGroupsRolesForUserGroupIsRequestedByUser(String userGroupName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.listGroupRolesIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), null, null, null, null, null);
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

    @When("^User group \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userGroupIsDeletedByUser(String userGroupName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.deleteUserGroupByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID));
    }


    @When("^User group with id \"([^\"]*)\" is updated with following data$")
    public void userGroupWithIdIsUpdatedWithFollowingData(String userGroupId, List<UserGroupUpdateDto> userGroups) throws Throwable {
        userGroupSteps.updateUserGroup(userGroupId, userGroups.get(0));
    }
    @When("^User group \"([^\"]*)\" is updated with following data(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userGroupIsUpdatedWithFollowingDataByUser(String userGroupName, String username, String applicationVersionName, List<UserGroupUpdateDto> userGroups) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.updateUserGroupByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), userGroups.get(0));
    }


    @When("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is requested$")
    public void relationBetweenUserGroupAndPropertyIsGot(String userGroupName, String propertyCode) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));

        userGroupSteps.getUserGroupsProperty(userGroup.getId(), property.getId());
    }

    @When("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertyIsGotByUser(String userGroupName, String propertyCode, String username, String applicationVersionName) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.getUserGroupsPropertyByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), property.getId());
    }


    @When("^Relation between user group \"([^\"]*)\" and non existent property is requested$")
    public void relationBetweenUserGroupAndNonExistentPropertyIsRequested(String userGroupName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.getUserGroupsProperty(userGroup.getId(), NON_EXISTENT_ID);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is got$")
    public void relationBetweenUserGroupAndPropertySetIsGot(String userGroupName, String propertySetName) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));

        userGroupSteps.getUserGroupsPropertySet(userGroup.getId(), propertySet.getId());
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertySetIsRequestedByUser(String userGroupName, String propertySetName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.getUserGroupsPropertySetByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetId);

    }

    @When("^Relation between user group \"([^\"]*)\" and nonexistent property set is requested$")
    public void relationBetweenUserGroupAndNonExistentPropertySetIsRequested(String userGroupName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.getUserGroupsPropertySet(userGroup.getId(), NON_EXISTENT_ID);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is created(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndRoleIsCreatedByUser(String userGroupName, String roleId, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupRoleRelationshipIsCreatedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" exists(?: with is_active \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndRoleExists(String userGroupName, String roleId, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String userGroupId= userGroupSteps.resolveUserGroupId(userGroupName);
        userGroupSteps.relationshipGroupRoleExist(userGroupId, roleId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property(?: with code)? \"([^\"]*)\" is created(?: with isActive \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertyWithCodeIsCreatedWithIsActiveByUser(String userGroupName, String propertyCode, Boolean isActive, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertyRelationshipIsCreatedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertyId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetIsCreatedWithIsActive(String userGroupName, String propertySetName, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.relationshipGroupPropertySetExist(userGroup.getId(), propertySet.getId(), isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set(?: with id)? \"([^\"]*)\" is created(?: with isActive \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertySetIsCreatedWithIsActiveByUser(String userGroupName, String propertySetName, Boolean isActive, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetid = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertySetRelationshipIsCreatedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetid, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property(?: with code)? \"([^\"]*)\" is deleted is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertyWithCodeIsDeletedIsDeletedByUser(String userGroupName, String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertyRelationshipIsDeletedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertyId);
    }

    @When("^Relation between user group(?: with id)? \"([^\"]*)\" and property set \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertySetIsDeletedByUser(String userGroupName, String propertySetName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.relationshipGroupPropertySetIsDeletedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndRoleIsDeleted(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.deleteUserGroupRoleRelationship(userGroupId, roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role with id \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndRoleIsDeletedByUser(String userGroupName, String roleId, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.deleteUserGroupRoleRelationshipByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), roleId);

    }
    
    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is (de|in)?activated$")
    public void relationBetweenUserGroupAndPropertyIsActivated(String userGroupName, String propertyCode, String negation) throws Throwable {
        String groupId = userGroupSteps.resolveUserGroupId(userGroupName);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        Boolean activity = true;
        if (negation != null) {
            activity = false;
        }
        userGroupSteps.setGroupPropertyActivity(groupId, propertyId, activity);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is (in|de)?activated$")
    public void relationBetweenUserGroupAndPropertySetIsActivated(String userGroupName, String propertySetName, String negation) throws Throwable {
        String groupId = userGroupSteps.resolveUserGroupId(userGroupName);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        Boolean activity = (negation == null);
        userGroupSteps.setGroupPropertySetActivity(groupId, propertySetId, activity);
    }

    @When("^IsActive for relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is set to \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void isActiveForRelationBetweenUserGroupAndPropertyWithCodeIsSetTo(String userGroupName, String propertyCode, Boolean isActive, String userName, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.setGroupPropertyActivityByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertyId, isActive);
    }


    // ------------------------- THEN ------------------------------

    @Then("^There are \"([^\"]*)\" user groups returned$")
    public void thereAreUserGroupsReturned(Integer userGroupsCount) throws Throwable {
        userGroupSteps.numberOfEntitiesInResponse(UserGroupDto.class, userGroupsCount);
    }

    @Then("^There are user groups with following description returned in order: \"([^\"]*)\"$")
    public void thereAreUserGroupsWithFollowingDescriptionReturnedInOrder(List<String> order) throws Throwable {
        userGroupSteps.responseSortByDescription(order);
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

    @Then("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is no more exists$")
    public void relationBetweenUserGroupAndPropertyIsNoMoreExists(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.checkGroupPropertyExistence(userGroupId, propertyId);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" no more exists$")
    public void relationBetweenUserGroupAndPropertySetIsNoMoreExists(String userGroupName, String propertySetName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.checkGroupPropertySetExistence(userGroup.getId(), propertySet.getId());
    }

    @Then("^Relation between user group \"([^\"]*)\" and role with id \"([^\"]*)\" is established$")
    public void relationBetweenUserGroupAndRoleIsEstablished(String userGroupName, String roleId) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.checkUserGroupRoleRelationExistency(userGroup.getId(), roleId, true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is not established$")
    public void relationBetweenUserGroupAndRoleIsNotEstablished(String userGroupName, String roleId) throws Throwable {
        String userGroupId = userGroupSteps.resolveUserGroupId(userGroupName);
        userGroupSteps.checkUserGroupRoleRelationExistency(userGroupId, roleId, false);
    }

    @Then("^There are \"([^\"]*)\" relationships returned$")
    public void thereAreRelationshipsReturned(int numberOfRoles) throws Throwable {
        userGroupSteps.numberOfEntitiesInResponse(RoleDto.class, numberOfRoles);
    }

    @Then("^There are relationships start with following IDs returned in order: \"([^\"]*)\"$")
    public void thereAreRelationshipsStartWithFollowingIDsReturnedInOrder(List<String> order) throws Throwable {
        userGroupSteps.responseSortById(order);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is active$")
    public void relationBetweenUserGroupAndPropertyIsActivate(String userGroupName, String propertyCode) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.checkuserGroupPropertyRelationActivity(userGroup.getId(), property.getId(), true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is not active$")
    public void relationBetweenUserGroupAndPropertyIsNotActivate(String userGroupName, String propertyCode) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.checkuserGroupPropertyRelationActivity(userGroup.getId(), property.getId(), false);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is (not )?active$")
    public void relationBetweenUserGroupAndPropertySetIsNotActivate(String userGroupName, String propertySetName, String negation) throws Throwable {
        String userGroupId = userGroupSteps.resolveUserGroupId(userGroupName);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        Boolean isActive = (negation==null);
        userGroupSteps.checkuserGroupPropertySetRelationActivity(userGroupId, propertySetId, isActive);
    }

    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\"(?: with is_active \"([^\"]*)\")?$")
    public void userIsAddedToUserGroupWithId(String userName, String userGroupName, String isActiveString) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        userGroupSteps.addUserToUserGroup(ids.get(USER_ID), ids.get(USER_GROUP_ID), isActive);
    }

    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\" as isActive \"([^\"]*)\"$")
    public void userIsAddedToUserGroupWithIdAsIsActive(String userName, String userGroupName, Boolean isActive) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.addUserToUserGroup(ids.get(USER_ID), ids.get(USER_GROUP_ID), isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is got$")
    public void relationBetweenUserGroupAndUserIsGot(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.getUserGroupsUser(ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndUserIsRequestedByUser(String userGroupName, String userName, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        String performerId = usersSteps.resolveUserId(performerName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.getUserGroupsUserRelationshipByUserForApp(performerId, applicationVersionId, ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^Relation between user group \"([^\"]*)\" and user with id \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndUserWithIdIsCreatedWithIsActive(String userGroupName, String userId, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat("UserGroup with name " + userGroupName + " not found. ", userGroup, is(not(nullValue())));

        userGroupSteps.addUserToUserGroup(userId, userGroup.getId(), isActive);
    }


    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\" with isActive \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userIsAddedToUserGroupByUser(String userName, String userGroupName, Boolean isActive, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        String performerId = usersSteps.resolveUserId(performerName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.addUserToUserGroupByUserForApp(performerId, applicationVersionId, ids.get(USER_ID), ids.get(USER_GROUP_ID), isActive);
    }

    @When("^User \"([^\"]*)\" is removed from userGroup \"([^\"]*)\"$")
    public void userIsRemovedFromUserGroup(String userName, String userGroupName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.relationshipGroupUserIsDeleted(ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^User \"([^\"]*)\" is removed from userGroup \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userIsRemovedFromUserGroupByUser(String userName, String userGroupName, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        String performerId = usersSteps.resolveUserId(performerName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupUserRelationshipIsDeletedByUserForApp(performerId, applicationVersionId, ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is activated$")
    public void relationBetweenUserGroupAndUserIsActivated(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.setUserGroupUserActivity(ids.get(USER_GROUP_ID), ids.get(USER_ID), true);
    }

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is deactivated$")
    public void relationBetweenUserGroupAndUserIsDeActivated(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.setUserGroupUserActivity(ids.get(USER_GROUP_ID), ids.get(USER_ID), false);
    }

    @And("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is active$")
    public void relationBetweenUserGroupAndUserIsActive(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        Boolean isActive = userGroupSteps.getUserGroupUserRelationIsActive(ids.get(USER_GROUP_ID), ids.get(USER_ID));
        assertThat("User is not active", isActive, is(true));
    }

    @And("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is not active$")
    public void relationBetweenUserGroupAndUserIsNotActive(String userGroupName, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        Boolean isActive = userGroupSteps.getUserGroupUserRelationIsActive(ids.get(USER_GROUP_ID), ids.get(USER_ID));
        assertThat("User is not active", isActive, is(false));
    }

    @When("^Is Active for relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is set to \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void isActiveForRelationBetweenUserGroupAndUserIsSetToByUser(String userGroupName, String userName, Boolean isActive, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        String performerId = usersSteps.resolveUserId(performerName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.setUserGroupUserActivityByUserForApp(performerId, applicationVersionId, ids.get(USER_GROUP_ID), ids.get(USER_ID), isActive);
    }


    @When("^IsActive relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is set to \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void isactiveRelationBetweenUserGroupAndPropertySetIsSetToByUser(String userGroupName, String propertySetName, Boolean isActive, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.setGroupPropertySetActivityByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetId, isActive);
    }

    @Given("^Relation between user \"([^\"]*)\" and group \"([^\"]*)\" is (in|de)?activated$")
    public void relationBetweenUserAndGroupIsActivated(String userName, String userGroupName, String negation) throws Throwable {
        String userId = usersSteps.resolveUserId(userName);
        String groupId = userGroupSteps.resolveUserGroupId(userGroupName);
        Boolean isActive = (negation == null);
        userGroupSteps.setUserGroupUserActivity(groupId, userId, isActive);
        Response response = userGroupSteps.getSessionResponse();
        assertThat(String.format("Failed to change the is_active flag: %s", response.asString()), response.statusCode() == HttpStatus.SC_NO_CONTENT);
    }

    @When("^List of all properties for user group \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllPropertiesForUserGroupIsRequestedByUser(String userGroupName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.listOfUserGroupPropertiesIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), null, null, null, null, null);
    }

    @When("^List of all property sets for user group \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllPropertySetsForUserGroupIsRequestedByUser(String userGroupName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.listOfUserGroupPropertySetsIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), null, null, null, null, null);
    }

    @When("^List of all users for user group \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllUsersForUserGroupIsRequestedByUser(String userGroupName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        userGroupSteps.listOfUserGroupUsersIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), null, null, null, null, null);
    }

    @When("^Relation between user group \"([^\"]*)\", property with code \"([^\"]*)\" and role with id \"([^\"]*)\" is created(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?$")
    public void relationBetweenUserGroupPropertyWithCodeAndRoleWithIdIsCreatedByUser(String userGroupName, String propertyCode, String roleId, String username, String applicationVersionName, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertyRoleRelationshipIsCreatedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertyId, roleId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\", property with code \"([^\"]*)\" and role with id \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupPropertyWithCodeAndRoleWithIdIsUpdatedByUser(String userGroupName, String propertyCode, String roleId, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertyRoleRelationshipIsDeletedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertyId, roleId);
    }

    @When("^List of all roles for user group \"([^\"]*)\" and property with code \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllRolesForUserGroupAndPropertyWithCodeIsRequestedByUser(String userGroupName, String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.listOfUserGroupPropertyRolesIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertyId);
    }

    @When("^Relation between user group \"([^\"]*)\", property set \"([^\"]*)\" and role with id \"([^\"]*)\" is created(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupPropertySetAndRoleWithIdIsCreatedByUser(String userGroupName, String propertySetName, String roleId, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertySetRoleRelationshipIsCreatedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetId, roleId);
    }

    @When("^Relation between user group \"([^\"]*)\", property set \"([^\"]*)\" and role with id \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserGroupPropertySetAndRoleWithIdIsDeletedByUser(String userGroupName, String propertySetName, String roleId, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.userGroupPropertySetRoleRelationshipIsDeletedByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetId, roleId);
    }

    @When("^List of all roles for user group \"([^\"]*)\" and property set \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllRolesForUserGroupAndPropertySetIsRequestedByUser(String userGroupName, String propertySetName, String username, String applicationVersionName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        userGroupSteps.listOfUserGroupPropertySetRolesIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(USER_GROUP_ID), propertySetId);
    }
}
