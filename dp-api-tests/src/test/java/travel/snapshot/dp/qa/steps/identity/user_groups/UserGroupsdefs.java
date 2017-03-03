package travel.snapshot.dp.qa.steps.identity.user_groups;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
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
        userGroupSteps.relationshipGroupPropertyExist(userGroup.getUserGroupId(), property.getPropertyId(), isActive);
    }

    @Given("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" exists(?: with isActive \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndPropertySetExistsWithIsActive(String userGroupName, String propertySetName, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String userGroupId = userGroupSteps.resolveUserGroupId(userGroupName);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        userGroupSteps.relationshipGroupPropertySetExist(userGroupId, propertySetId, isActive);
    }

    // ------------------------- WHEN ------------------------------

    @When("^The following user group is created$")
    public void userGroupWithIdIsCreated(List<UserGroupDto> userGroup) throws Throwable {
        userGroupSteps.followingUserGroupIsCreated(userGroup.get(0));
    }

    @When("^User group \"([^\"]*)\" is requested$")
    public void userGroupWithIdIsGot(String userGroupName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.getUserGroup(userGroup.getUserGroupId());
    }

    @When("^User group \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void userGroupIsRequestedByUser(String userGroupName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.getUserGroupByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID));
    }

    @When("^User group with id \"([^\"]*)\" is got with etag$")
    public void userGroupWithIdIsGotWithEtag(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdGotWithEtag(userGroupId);
    }

    @When("^User group with id \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void userGroupWithIdIsGotForEtagUpdatedAndGotWithPreviousEtag(String userGroupId) throws Throwable {
        userGroupSteps.userGroupWithIdIsGotWithEtagAfterUpdate(userGroupId);
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


    @When("^List of user groups is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void listOfUserGroupsIsGotWithLimitAndCursorAndFilterAndSortAndSort_descByUser(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                          @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                          @Transform(NullEmptyStringConverter.class) String filter,
                                                                                          @Transform(NullEmptyStringConverter.class) String sort,
                                                                                          @Transform(NullEmptyStringConverter.class) String sortDesc, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        userGroupSteps.listUserGroupsIsGotByUser(user.getUserId(), limit, cursor, filter, sort, sortDesc);
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
        userGroupSteps.listGroupRoleIsGot(usergGroup.getUserGroupId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all relationships userGroups-Roles for userGroup \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllRelationshipsUserGroupsRolesForUserGroupIsRequestedByUser(String userGroupName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.listGroupRolesIsGotByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), null, null, null, null, null);
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

    @When("^User group \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void userGroupIsDeletedByUser(String userGroupName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.deleteUserGroupByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID));
    }


    @When("^User group with id \"([^\"]*)\" is updated with following data$")
    public void userGroupWithIdIsUpdatedWithFollowingData(String userGroupId, List<UserGroupUpdateDto> userGroups) throws Throwable {
        userGroupSteps.updateUserGroup(userGroupId, userGroups.get(0));
    }
    @When("^User group \"([^\"]*)\" is updated with following data by user \"([^\"]*)\"$")
    public void userGroupIsUpdatedWithFollowingDataByUser(String userGroupName, String username, List<UserGroupUpdateDto> userGroups) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.updateUserGroupByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), userGroups.get(0));
    }


    @When("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is requested$")
    public void relationBetweenUserGroupAndPropertyIsGot(String userGroupName, String propertyCode) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));

        userGroupSteps.getUserGroupsProperty(userGroup.getUserGroupId(), property.getPropertyId());
    }

    @When("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertyIsGotByUser(String userGroupName, String propertyCode, String username) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);

        userGroupSteps.getUserGroupsPropertyByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), property.getPropertyId());
    }


    @When("^Relation between user group \"([^\"]*)\" and non existent property is requested$")
    public void relationBetweenUserGroupAndNonExistentPropertyIsRequested(String userGroupName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.getUserGroupsProperty(userGroup.getUserGroupId(), NON_EXISTENT_ID);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is got$")
    public void relationBetweenUserGroupAndPropertySetIsGot(String userGroupName, String propertySetName) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));

        userGroupSteps.getUserGroupsPropertySet(userGroup.getUserGroupId(), propertySet.getPropertySetId());
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetIsRequestedByUser(String userGroupName, String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.getUserGroupsPropertySetByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySet.getPropertySetId());

    }

    @When("^Relation between user group \"([^\"]*)\" and nonexistent property set is requested$")
    public void relationBetweenUserGroupAndNonExistentPropertySetIsRequested(String userGroupName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.getUserGroupsPropertySet(userGroup.getUserGroupId(), NON_EXISTENT_ID);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is created by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndRoleIsCreatedByUser(String userGroupName, String roleId, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);

        userGroupSteps.userGroupRoleRelationshipIsCreatedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" exists(?: with is_active \"([^\"]*)\")?$")
    public void relationBetweenUserGroupAndRoleExists(String userGroupName, String roleId, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String userGroupId= userGroupSteps.resolveUserGroupId(userGroupName);
        userGroupSteps.relationshipGroupRoleExist(userGroupId, roleId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertyIsCreatedWithIsActive(String userGroupId, @Transform(NullEmptyStringConverter.class) String propertyId, Boolean isActive) throws Throwable {
        userGroupSteps.relationshipGroupPropertyExist(userGroupId, propertyId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is created with isActive \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertyWithCodeIsCreatedWithIsActiveByUser(String userGroupName, String propertyCode, Boolean isActive, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);

        userGroupSteps.userGroupPropertyRelationshipIsCreatedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertyId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetIsCreatedWithIsActive(String userGroupName, String propertySetName, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.relationshipGroupPropertySetExist(userGroup.getUserGroupId(), propertySet.getPropertySetId(), isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set with id \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetWithIdIsCreatedWithIsActive(String userGroupName, String propertySetId, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.relationshipGroupPropertySetExist(userGroup.getUserGroupId(), propertySetId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is created with isActive \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetIsCreatedWithIsActiveByUser(String userGroupName, String propertySetName, Boolean isActive, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.userGroupPropertySetRelationshipIsCreatedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySet.getPropertySetId(), isActive);
    }

    @When("^Relation between user group \"([^\"]*)\" and property \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndPropertyIsDeleted(String userGroupId, String propertyId) throws Throwable {
        userGroupSteps.relationshipGroupPropertyIsDeleted(userGroupId, propertyId);
    }


    @When("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is deleted is deleted by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertyWithCodeIsDeletedIsDeletedByUser(String userGroupName, String propertyCode, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.userGroupPropertyRelationshipIsDeletedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), property.getPropertyId());
    }

    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndPropertySetIsDeleted(String userGroupName, String propertySetId) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));

        userGroupSteps.relationshipGroupPropertySetIsDeleted(userGroup.getUserGroupId(), propertySetId);
    }

    @When("^Relation between user group with id \"([^\"]*)\" and property set \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupWithIdAndPropertySetIsDeleted(String userGroupId, String propertySetId) throws Throwable {
        userGroupSteps.relationshipGroupPropertySetIsDeleted(userGroupId, propertySetId);
    }


    @When("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndPropertySetIsDeletedByUser(String userGroupName, String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.relationshipGroupPropertySetIsDeletedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySet.getPropertySetId());
    }

    @When("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is deleted$")
    public void relationBetweenUserGroupAndRoleIsDeleted(String userGroupId, String roleId) throws Throwable {
        userGroupSteps.deleteUserGroupRoleRelationship(userGroupId, roleId);
    }

    @When("^Relation between user group \"([^\"]*)\" and role with id \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndRoleIsDeletedByUser(String userGroupName, String roleId, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);

        userGroupSteps.deleteUserGroupRoleRelationshipByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), roleId);

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

    @When("^IsActive for relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is set to \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void isActiveForRelationBetweenUserGroupAndPropertyWithCodeIsSetTo(String userGroupName, String propertyCode, Boolean isActive, String userName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.setGroupPropertyActivityByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), property.getPropertyId(), isActive);
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

        userGroupSteps.checkGroupPropertySetExistence(userGroup.getUserGroupId(), propertySet.getPropertySetId());
    }

    @Then("^Relation between user group \"([^\"]*)\" and role with id \"([^\"]*)\" is established$")
    public void relationBetweenUserGroupAndRoleIsEstablished(String userGroupName, String roleId) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        userGroupSteps.checkUserGroupRoleRelationExistency(userGroup.getUserGroupId(), roleId, true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and role \"([^\"]*)\" is not established$")
    public void relationBetweenUserGroupAndRoleIsNotEstablished(String userGroupId, String roleId) throws Throwable {
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

        userGroupSteps.checkuserGroupPropertyRelationActivity(userGroup.getUserGroupId(), property.getPropertyId(), true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property with code \"([^\"]*)\" is not active$")
    public void relationBetweenUserGroupAndPropertyIsNotActivate(String userGroupName, String propertyCode) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.checkuserGroupPropertyRelationActivity(userGroup.getUserGroupId(), property.getPropertyId(), false);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is active$")
    public void relationBetweenUserGroupAndPropertySetIsActivate(String userGroupName, String propertySetName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        userGroupSteps.checkuserGroupPropertySetRelationActivity(userGroup.getUserGroupId(), propertySet.getPropertySetId(), true);
    }

    @Then("^Relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is not active$")
    public void relationBetweenUserGroupAndPropertySetIsNotActivate(String userGroupName, String propertySetName) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat(userGroup, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        userGroupSteps.checkuserGroupPropertySetRelationActivity(userGroup.getUserGroupId(), propertySet.getPropertySetId(), false);
    }

    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\"$")
    public void userIsAddedToUserGroupWithId(String userName, String userGroupName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.addUserToUserGroup(ids.get(USER_ID), ids.get(USER_GROUP_ID), true);
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

    @When("^Relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndUserIsRequestedByUser(String userGroupName, String userName, String performerName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        userGroupSteps.getUserGroupsUserRelationshipByUser(performer.getUserId(), ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^Relation between user group \"([^\"]*)\" and user with id \"([^\"]*)\" is created with isActive \"([^\"]*)\"$")
    public void relationBetweenUserGroupAndUserWithIdIsCreatedWithIsActive(String userGroupName, String userId, Boolean isActive) throws Throwable {
        UserGroupDto userGroup = userGroupSteps.getUserGroupByName(userGroupName);
        assertThat("UserGroup with name " + userGroupName + " not found. ", userGroup, is(not(nullValue())));

        userGroupSteps.addUserToUserGroup(userId, userGroup.getUserGroupId(), isActive);
    }


    @Given("^User \"([^\"]*)\" is added to userGroup \"([^\"]*)\" with isActive \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userIsAddedToUserGroupByUser(String userName, String userGroupName, Boolean isActive, String performerName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        userGroupSteps.addUserToUserGroupByUser(performer.getUserId(), ids.get(USER_ID), ids.get(USER_GROUP_ID), isActive);
    }

    @When("^User \"([^\"]*)\" is removed from userGroup \"([^\"]*)\"$")
    public void userIsRemovedFromUserGroup(String userName, String userGroupName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);

        userGroupSteps.relationshipGroupUserIsDeleted(ids.get(USER_GROUP_ID), ids.get(USER_ID));
    }

    @When("^User \"([^\"]*)\" is removed from userGroup \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userIsRemovedFromUserGroupByUser(String userName, String userGroupName, String performerName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        userGroupSteps.userGroupUserRelationshipIsDeletedByUser(performer.getUserId(), ids.get(USER_GROUP_ID), ids.get(USER_ID));
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

    @When("^Is Active for relation between user group \"([^\"]*)\" and user \"([^\"]*)\" is set to \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void isActiveForRelationBetweenUserGroupAndUserIsSetToByUser(String userGroupName, String userName, Boolean isActive, String performerName) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, userName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        userGroupSteps.setUserGroupUserActivityByUser(performer.getUserId(), ids.get(USER_GROUP_ID), ids.get(USER_ID), isActive);
    }


    @When("^IsActive relation between user group \"([^\"]*)\" and property set \"([^\"]*)\" is set to \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void isactiveRelationBetweenUserGroupAndPropertySetIsSetToByUser(String userGroupName, String propertySetName, Boolean isActive, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.setGroupPropertySetActivityByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySet.getPropertySetId(), isActive);
    }

    @Given("^The following user group is created by user \"([^\"]*)\"$")
    public void theFollowingUserGroupIsCreatedByUser(String userName, List<UserGroupDto> userGroups) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(userName);
        assertThat("User with username " + userName + " is null. ", user, is(not(nullValue())));

        userGroupSteps.followingUserGroupIsCreatedByUser(user.getUserId(), userGroups.get(0));
    }

    @When("^List of all properties for user group \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllPropertiesForUserGroupIsRequestedByUser(String userGroupName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.listOfUserGroupPropertiesIsGotByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), null, null, null, null, null);
    }

    @When("^List of all property sets for user group \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllPropertySetsForUserGroupIsRequestedByUser(String userGroupName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.listOfUserGroupPropertySetsIsGotByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), null, null, null, null, null);
    }

    @When("^List of all users for user group \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllUsersForUserGroupIsRequestedByUser(String userGroupName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        userGroupSteps.listOfUserGroupUsersIsGotByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), null, null, null, null, null);
    }

    @When("^Relation between user group \"([^\"]*)\", property with code \"([^\"]*)\" and role with id \"([^\"]*)\" is created by user \"([^\"]*)\"(?: with is_active \"([^\"]*)\")?$")
    public void relationBetweenUserGroupPropertyWithCodeAndRoleWithIdIsCreatedByUser(String userGroupName, String propertyCode, String roleId, String username, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);

        userGroupSteps.userGroupPropertyRoleRelationshipIsCreatedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertyId, roleId, isActive);
    }

    @When("^Relation between user group \"([^\"]*)\", property with code \"([^\"]*)\" and role with id \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupPropertyWithCodeAndRoleWithIdIsUpdatedByUser(String userGroupName, String propertyCode, String roleId, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.userGroupPropertyRoleRelationshipIsDeletedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), property.getPropertyId(), roleId);
    }

    @When("^List of all roles for user group \"([^\"]*)\" and property with code \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllRolesForUserGroupAndPropertyWithCodeIsRequestedByUser(String userGroupName, String propertyCode, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        userGroupSteps.listOfUserGroupPropertyRolesIsGotByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), property.getPropertyId());
    }

    @When("^Relation between user group \"([^\"]*)\", property set \"([^\"]*)\" and role with id \"([^\"]*)\" is created by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupPropertySetAndRoleWithIdIsCreatedByUser(String userGroupName, String propertySetName, String roleId, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        userGroupSteps.userGroupPropertySetRoleRelationshipIsCreatedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySet.getPropertySetId(), roleId);
    }

    @When("^Relation between user group \"([^\"]*)\", property set \"([^\"]*)\" and role with id \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void relationBetweenUserGroupPropertySetAndRoleWithIdIsDeletedByUser(String userGroupName, String propertySetName, String roleId, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);

        userGroupSteps.userGroupPropertySetRoleRelationshipIsDeletedByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySetId, roleId);
    }

    @When("^List of all roles for user group \"([^\"]*)\" and property set \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllRolesForUserGroupAndPropertySetIsRequestedByUser(String userGroupName, String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getNonNullIdsFromNames(userGroupName, username);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);

        userGroupSteps.listOfUserGroupPropertySetRolesIsGotByUser(ids.get(USER_ID), ids.get(USER_GROUP_ID), propertySetId);
    }

}
