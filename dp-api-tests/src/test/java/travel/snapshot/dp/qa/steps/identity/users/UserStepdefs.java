package travel.snapshot.dp.qa.steps.identity.users;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.SNAPSHOT;
import static travel.snapshot.dp.qa.serenity.BasicSteps.REQUESTOR_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.TARGET_ID;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.roles.RoleBaseSteps;
import travel.snapshot.dp.qa.serenity.users.UserRolesSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;
import travel.snapshot.dp.qa.steps.BasicStepDefs;

import java.util.List;
import java.util.Map;

public class UserStepdefs {

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private RoleBaseSteps roleBaseSteps;

    @Steps
    private UserRolesSteps userRolesSteps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private PropertySetSteps propertySetSteps;


    @Given("^The following users exist for customer \"([^\"]*)\" as primary \"([^\"]*)\"(?: with is_active \"([^\"]*)\")?$")
    public void theFollowingUsersExistForCustomer(String customerId, Boolean isPrimary, String isActiveString, List<UserCreateDto> users) throws Throwable {
//        Cucumber turns is_active to false when not present we want true by default in tests.
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        usersSteps.followingUsersExist(users, customerId, isPrimary, isActive);
    }

    @When("^The following users is created for customer \"([^\"]*)\" as primary \"([^\"]*)\"$")
    public void User_is_created(String customerId, Boolean isPrimary, List<UserCreateDto> users) throws Throwable {
        usersSteps.createUserWithCustomer(users.get(0), customerId, isPrimary);
    }

    @Then("^Body contains user type with \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_user_type_with_value(String name, String value) throws Throwable {
        usersSteps.bodyContainsUserWith(name, value);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same user$")
    public void header_is_set_and_contains_the_same_user(String header) throws Throwable {
        usersSteps.compareUserOnHeaderWithStored(header);
    }

    @When("^User \"([^\"]*)\" is deleted$")
    public void User_with_name_name_is_deleted(String username) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        usersSteps.deleteUser(userId);
    }

    @When("^User \"([^\"]*)\" is deleted with ETAG \"([^\"]*)\"$")
    public void userWithIsDeletedWithOutdatedETAG(String username, String etag) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        usersSteps.deleteUserWithEtag(userId, etag);
    }

    @Then("^User with same id doesn't exist$")
    public void User_with_same_id_does_not_exist() throws Throwable {
        usersSteps.userIdInSessionDoesNotExist();
    }

    @When("^Nonexistent user is deleted$")
    public void Nonexistent_user_is_deleted() throws Throwable {
        usersSteps.deleteUser("nonexistent");
    }

    @When("^User \"([^\"]*)\" is updated with data(?: by user \"([^\"]*)\")?$")
    public void User_with_user_name_updated_with_data(String userName, String performerName, List<UserDto> users) throws Throwable {
        String targetId = usersSteps.resolveUserId(userName);
        if (performerName == null) {
            usersSteps.updateUser(targetId, users.get(0));
        } else {
            String performerId = usersSteps.resolveUserId(performerName);
            usersSteps.updateUserByUser(performerId, targetId, users.get(0));
        }
    }

    @Then("^Updated user \"([^\"]*)\" has data$")
    public void Updated_user_with_user_name_has_data(String userName, List<UserDto> users) throws Throwable {
        usersSteps.userWithUserNameHasData(userName, users.get(0));
    }

    @When("^User \"([^\"]*)\" is updated with data if updated before$")
    public void User_with_user_name_is_updated_with_data_if_updated_before(String userName, List<UserDto> users) throws Throwable {
        usersSteps.updateUserWithUserNameIfUpdatedBefore(userName, users.get(0));
    }

    @When("^User \"([^\"]*)\" is got$")
    public void User_with_username_is_got(String username) throws Throwable {
        usersSteps.userWithUsernameIsGot(username);
    }

    @When("^User \"([^\"]*)\" is got with etag$")
    public void User_with_username_is_got_with_etag(String username) throws Throwable {
        usersSteps.userWithUsernameIsGotWithEtag(username);
    }

    @When("^Nonexistent user id is got$")
    public void Nonexistent_user_id_is_got() throws Throwable {
        usersSteps.userWithIdIsGot("nonexistent");
    }

    @When("^List of users is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"(?: by user \"([^\"]*)\")?$")
    public void List_of_users_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                             @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                             @Transform(NullEmptyStringConverter.class) String filter,
                                                                                             @Transform(NullEmptyStringConverter.class) String sort,
                                                                                             @Transform(NullEmptyStringConverter.class) String sortDesc,
                                                                                             @Transform(NullEmptyStringConverter.class) String userName) throws Throwable {
        String userId = usersSteps.resolveUserId(userName);
        if (userId != null) {
            usersSteps.listOfUsersIsGotByUser(limit, cursor, filter, sort, sortDesc, userId);
        } else {
            usersSteps.listOfUsersIsGotWith(limit, cursor, filter, sort, sortDesc);
        }
    }

    @Then("^There are (\\d+) users returned$")
    public void There_are_returned_users_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(UserDto.class, count);
    }

    @Then("^There are (\\d+) userCustomers returned$")
    public void There_are_returned_userCustomers_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(UserCustomerRelationshipDto.class, count);
    }

    @Then("^There are users with following usernames returned in order: (.*)$")
    public void There_are_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        usersSteps.usernamesAreInResponseInOrder(usernames);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is added to user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_added_to_user_with_username_with_relationship_type_and_entity_with_code(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.roleIsAddedToUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }



    @Given("^Relation between role with name \"([^\"]*)\" for application id \"([^\"]*)\" and user \"([^\"]*)\" exists with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Relation_between_role_with_name_for_application_i_and_user_with_username_exists_with_relationship_type_and_entity_with_id(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.relationExistsBetweenRoleAndUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is removed from user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_removed_from_user_with_username_with_relationship_type_and_entity_with_code(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.roleIsDeletedFromUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @Then("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is not there for user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_not_there_for_user_with_username_with_relationship_type_and_entity_with_code(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.roleDoesntExistForUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^Nonexistent role is removed from user \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Nonexistent_role_is_removed_from_user_with_username_with_relationship_type_and_entity_with_code(String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = new RoleDto();
        role.setRoleId(BasicStepDefs.NONEXISTENT_ID);
        usersSteps.roleIsDeletedFromUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^List of roles for user with username \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_roles_for_user_with_username_with_relationship_type_and_entity_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String username, String relationshipType, String entityId,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                                                @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {

        usersSteps.listOfRolesIsGotForRelationshipTypeEntityWIth(username, relationshipType, entityId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) user roles returned$")
    public void There_are_returned_user_roles_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(RoleDto.class, count);
    }

    @Then("^There are user roles with following role names returned in order: (.*)$")
    public void There_are_user_roles_with_following_role_names_returned_in_order(List<String> rolenames) throws Throwable {
        usersSteps.rolenamesAreInResponseInOrder(rolenames);
    }

    @Given("^The password of user \"([^\"]*)\" is \"([^\"]*)\"$")
    public void setting_the_password_of_user_to(String username, String password) {
        usersSteps.setUserPasswordByUsername(username, password);
    }

    @When("^User \"([^\"]*)\" is activated$")
    public void userWithCodeIsActivated(String name) throws Throwable {
        String userId = usersSteps.resolveUserId(name);
        usersSteps.setUserIsActive(userId, true);
    }

    @When("^User \"([^\"]*)\" is inactivated$")
    public void userWithIdIsInactivated(String name) throws Throwable {
        String userId = usersSteps.resolveUserId(name);
        usersSteps.setUserIsActive(userId, false);
    }

    @Then("^User \"([^\"]*)\" is active$")
    public void userWithIdIsActive(String name) throws Throwable {
        String userId = usersSteps.resolveUserId(name);
        UserDto user = usersSteps.getUserById(userId);
        assertThat("User is not active!", user.getIsActive(), is(true));
    }

    @Then("^User \"([^\"]*)\" is not active$")
    public void userWithIdIsNotActive(String name) throws Throwable {
        String userId = usersSteps.resolveUserId(name);
        UserDto user = usersSteps.getUserById(userId);
        assertThat("User is active but should be inactive.", user.getIsActive(), is(false));
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" is added$")
    public void roleWithNameForUserNameAndCustomerIdIsAddedString(String roleId, String userName, String customerId) throws Throwable {
        userRolesSteps.roleExistsBetweenUserAndCustomer(roleId, userName, customerId);
    }

    @When("^Role with id \"([^\"]*)\" for not existing user id and customer id \"([^\"]*)\" is added$")
    public void roleWithIdForNotExistingUserIdAndCustomerIdIsAdded(String roleId, String customerId) throws Throwable {
        userRolesSteps.addRoleBetweenNotExistingUserAndCustomer(roleId, "1111fd9a-a11d-11d8-8e11-111904ace123", customerId);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" is deleted$")
    public void roleWithIdForUserNameAndCustomerIdIsDeleted(String roleId, String userName, String customerId) throws Throwable {
        userRolesSteps.roleBetweenUserAndCustomerIsDeleted(roleId, userName, customerId);
    }

    @And("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" does not exist$")
    public void roleWithIdForUserNameAndCustomerIdDoesNotExist(String roleId, String userName, String customerId) throws Throwable {
        userRolesSteps.roleBetweenUserAndCustomerNotExists(roleId, userName, customerId);
    }

    @Given("^Role with name \"([^\"]*)\" for user name \"([^\"]*)\" and customer id \"([^\"]*)\" is added$")
    public void roleWithNameForUserNameAndCustomerIdIsAdded(String roleName, String userName, String customerId) throws Throwable {
        roleBaseSteps.setRolesPathCustomer();
        RoleDto role = roleBaseSteps.getRoleByName(roleName);
        userRolesSteps.roleNameExistsBetweenUserAndCustomer(role.getRoleId(), userName, customerId);
    }

    @When("^List of roles for user with username \"([^\"]*)\" and customer id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRolesForUserWithUsernameAndCustomerIdIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userName, String customerId,
                                                                                                               @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                               @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                               @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                               @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                               @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        userRolesSteps.getRolesBetweenUserAndCustomer(userName, customerId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" is added$")
    public void roleWithIdForUserNameAndPropertyCodeIsAdded(String roleId, String userName, String propCode) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.roleExistsBetweenUserAndProperty(roleId, userName, prop.getPropertyId());
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property id \"([^\"]*)\" is added$")
    public void roleWithIdForUserNameAndPropertyIdIsAdded(String roleId, String userName, String propertyId) throws Throwable {
        userRolesSteps.roleExistsBetweenUserAndProperty(roleId, userName, propertyId);
    }

    @When("^Role with id \"([^\"]*)\" for not existing user id and property code \"([^\"]*)\" is added$")
    public void roleWithIdForNotExistingPropertyIdAndCustomerIdIsAdded(String roleId, String propCode) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.addRoleBetweenNotExistingUserAndProperty(roleId, "1111fd9a-a11d-11d8-8e11-111904ace123", prop.getPropertyId());
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" is deleted$")
    public void roleWithIdForUserNameAndPropertyCodeIsDeleted(String roleId, String userName, String propCode) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.roleBetweenUserAndPropertyIsDeleted(roleId, userName, prop.getPropertyId());
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" does not exist$")
    public void roleWithIdForUserNameAndPropertyCodeDoesNotExist(String roleId, String userName, String propCode) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.roleBetweenUserAndPropertyNotExists(roleId, userName, prop.getPropertyId());
    }

    @Given("^Role with name \"([^\"]*)\" for user name \"([^\"]*)\" and property code \"([^\"]*)\" is added$")
    public void roleWithNameForUserNameAndPropertyCodeIsAdded(String roleName, String userName, String propCode) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        roleBaseSteps.setRolesPathProperty();
        RoleDto role = roleBaseSteps.getRoleByName(roleName);

        userRolesSteps.roleNameExistsBetweenUserAndProperty(role.getRoleId(), userName, prop.getPropertyId());
    }

    @When("^List of roles for user with username \"([^\"]*)\" and property code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRolesForUserWithUsernameAndPropertyCodeIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userName, String propCode,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                 @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertyDto prop = propertySteps.getPropertyByCodeInternal(propCode);
        userRolesSteps.getRolesBetweenUserAndProperty(userName, prop.getPropertyId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\" for customer \"([^\"]*)\" is added$")
    public void roleWithIdForUserNameAndPropertySetNameForCustomerIsAdded(String roleId, String userName, String propertySetName, String customerId) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        userRolesSteps.roleExistsBetweenUserAndPropertySet(roleId, userName, propertySet.getPropertySetId());
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\" for customer \"([^\"]*)\" is deleted$")
    public void roleWithIdForUserNameAndPropertySetNameForCustomerIsDeleted(String roleId, String userName, String propertySetName, String customerId) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        userRolesSteps.roleBetweenUserAndPropertySetIsDeleted(roleId, userName, propertySet.getPropertySetId());
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\" for customer \"([^\"]*)\" does not exist$")
    public void roleWithIdForUserNameAndPropertySetNameForCustomerDoesNotExist(String roleId, String userName, String propertySetName, String customerId) throws Throwable {;
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        userRolesSteps.roleBetweenUserAndPropertySetNotExists(roleId, userName, propertySet.getPropertySetId());
    }


    @Given("^Role with name \"([^\"]*)\" for user name \"([^\"]*)\" and property set name \"([^\"]*)\" for customer id \"([^\"]*)\" is added$")
    public void roleWithNameForUserNameAndPropertySetNameForCustomerCodeIsAdded(String roleName, String userName, String propertySetName, String customerId) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        roleBaseSteps.setRolesPathPropertySet();
        RoleDto role = roleBaseSteps.getRoleByName(roleName);

        userRolesSteps.roleNameExistsBetweenUserAndPropertySet(role.getRoleId(), userName, propertySet.getPropertySetId());
    }

    @When("^List of roles for user with username \"([^\"]*)\" and property set name \"([^\"]*)\" for customer id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfRolesForUserWithUsernameAndPropertySetNameForCustomerCodeIsGotWithLimitAndCursorAndFilterAndSortAndSort_desc(String userName, String propertySetName, String customerId,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                   @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        userRolesSteps.getRolesBetweenUserAndPropertySet(userName, propertySet.getPropertySetId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^Role with id \"([^\"]*)\" for not existing user id and property set name \"([^\"]*)\" for customer id \"([^\"]*)\" is added$")
    public void roleWithIdForNotExistingUserIdAndPropertySetNameForCustomerCodeIsAdded(String roleId, String propertySetName, String customerId) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, customerId);

        userRolesSteps.addRoleBetweenNotExistingUserAndPropertySet(roleId, "1111fd9a-a11d-11d8-8e11-111904ace123", propertySet.getPropertySetId());
    }

    @When("^Role with id \"([^\"]*)\" for user name \"([^\"]*)\" and property set id \"([^\"]*)\" is added$")
    public void roleWithIdForUserNameAndPropertySetIdIsAdded(String roleId, String userName, String propSerId) throws Throwable {
        userRolesSteps.roleExistsBetweenUserAndPropertySet(roleId, userName, propSerId);
    }

    @When("^Following snapshot user is created without customer$")
    public void followingSnapshotUserIsCreatedWithoutCustomer(List<UserCreateDto> users) throws Throwable {
        UserCreateDto user = users.get(0);
        user.setUserType(SNAPSHOT);
        usersSteps.createUser(user);
    }

    @When("^Property set \"([^\"]*)\" is added to user \"([^\"]*)\"$")
    public void propertySetIsAddedToUser(String propertySetName, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(user, is(notNullValue()));
        assertThat(propertySet, is(notNullValue()));

        usersSteps.addPropertySetToUser(propertySet.getPropertySetId(), user.getUserId());
    }

    @And("^There are \"([^\"]*)\" users returned$")
    public void thereAreUsersReturned(Integer userCount) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(UserDto.class, userCount);
    }

    @When("^User \"([^\"]*)\" creates user as primary \"([^\"]*)\" for customer with id \"([^\"]*)\"$")
    public void userWithUsernameCreatesUserForCustomerWithId(String userName, Boolean isPrimary, String customerId, List<UserCreateDto> users) throws Throwable {
        String performerId = usersSteps.resolveUserId(userName);
        usersSteps.createUserForCustomerByUser(performerId, customerId, users.get(0), isPrimary);
    }

    @When("^User \"([^\"]*)\" deletes user \"([^\"]*)\"$")
    public void userWithUsernameDeletesUserWithId(String performerUserName, String targetUserName) throws Throwable {
        Map<String, String> userIdMap = usersSteps.getUsersIds(performerUserName, targetUserName);
        usersSteps.deleteUserByUser(userIdMap.get(REQUESTOR_ID), userIdMap.get(TARGET_ID));
    }

    @When("^Relation between user \"([^\"]*)\" and customer \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void relationBetweenUserWithUsernameAndCustomerIsRequestedByUser(String targetUserName, String customerId, String requestorUserName) throws Throwable {
        Map<String, String> userIdMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        usersSteps.getUserCustomerRelationByUser(userIdMap.get(REQUESTOR_ID), customerId, userIdMap.get(TARGET_ID));
    }

    @When("^User \"([^\"]*)\" requests list of customer for user \"([^\"]*)\"$")
    public void userWithUsernameRequestsListOfCustomerForUserWithUsername(String requestorUserName, String targetUserName) throws Throwable {
        Map<String, String> userIdMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        usersSteps.listUserCustomersByUser(userIdMap.get(REQUESTOR_ID), userIdMap.get(TARGET_ID));
    }

    @When("^User \"([^\"]*)\" requests roles of user \"([^\"]*)\" for (customer|property|property set) \"([^\"]*)\"$")
    public void userRequestsRolesOfUserForCustomer(String requestorUserName, String targetUserName, String secondLevelName, String secondLevelId) throws Throwable {
        Map<String, String> userIdMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        usersSteps.listRolesForRelationByUser(userIdMap.get(REQUESTOR_ID), userIdMap.get(TARGET_ID), secondLevelName, secondLevelId);
    }

    @When("^User \"([^\"]*)\" assigns role \"([^\"]*)\" to relation between user \"([^\"]*)\" and (customer|property|property set) \"([^\"]*)\"$")
    public void userAssignsRoleToUserCustomerRelationBetweenUserAtCustomer(String requestorUsername, String roleName, String targetUsername, String thirdLevelName, String thirdLevelId) throws Throwable {
        Map<String, String> userIdsMap = usersSteps.getUsersIds( requestorUsername, targetUsername );
        String roleId = roleBaseSteps.resolveRoleId(roleName);
        usersSteps.userAssignsRoleToRelation(userIdsMap.get(REQUESTOR_ID), userIdsMap.get(TARGET_ID), thirdLevelName, thirdLevelId, roleId);

    }

    @When("^User \"([^\"]*)\" deletes role \"([^\"]*)\" from relation between user \"([^\"]*)\" and (customer|property|property set) \"([^\"]*)\"$")
    public void userDeletesRoleFromUserCustomerRelationBetweenUserAtCustomer(String requestorUsername, String roleName, String targetUsername, String thirdLevelName, String thirdLevelId) throws Throwable {
        Map<String, String> userIdsMap = usersSteps.getUsersIds( requestorUsername, targetUsername );
        String roleId = roleBaseSteps.resolveRoleId(roleName);
        usersSteps.userDeletesRoleFromRelation(userIdsMap.get(REQUESTOR_ID), userIdsMap.get(TARGET_ID), thirdLevelName, thirdLevelId, roleId);
    }

    @When("^User \"([^\"]*)\" creates user with:$")
    public void userCreatesSnapshotUser(String username, List<UserCreateDto> users) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        usersSteps.createUserByUser(userId, users.get(0));
    }

    @When("^User \"([^\"]*)\" requests(?: list of)? users for property \"([^\"]*)\"$")
    public void userRequestsListOfUsersForProperty(String userName, String propertyName) throws Throwable {
        String userId = usersSteps.resolveUserId(userName);
        String propertyId = propertySteps.resolvePropertyId(propertyName);
        propertySteps.listUsersForPropertyByUser(userId, propertyId);
    }

    @When("^User \"([^\"]*)\" adds user \"([^\"]*)\" to property \"([^\"]*)\"$")
    public void userAddsUserToProperty(String requestorName, String targetUserName, String propertyCode) throws Throwable {
        String requestorId = usersSteps.resolveUserId(requestorName);
        String targetUserId = usersSteps.resolveUserId(targetUserName);
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        propertySteps.addPropertyToUserByUser(requestorId, propertyId, targetUserId);
    }

    @When("^User \"([^\"]*)\" adds user \"([^\"]*)\" to customer \"([^\"]*)\"$")
    public void userAddsUserToCustomer(String requestorName, String targetUserName, String customerId) throws Throwable {
        String requestorId = usersSteps.resolveUserId(requestorName);
        String targetUserId = usersSteps.resolveUserId(targetUserName);
        customerSteps.addUserToCustomerByUser(requestorId, targetUserId, customerId, true, true);
    }

    @When("^User \"([^\"]*)\" (?:removes|deletes) user \"([^\"]*)\" from customer \"([^\"]*)\"$")
    public void userRemovesUserFromCustomer(String requestorName, String targetUserName, String customerId) throws Throwable {
        String requestorId = usersSteps.resolveUserId(requestorName);
        String targetUserId = usersSteps.resolveUserId(targetUserName);
        customerSteps.removeUserFromCustomerByUser(requestorId, customerId, targetUserId);
    }

    @When("^User \"([^\"]*)\" requests (?:list of )?users for property set \"([^\"]*)\"$")
    public void userRequestsListOfUsersForPropertySet(String requestorName, String propertySetName) throws Throwable {
        String requestorId = usersSteps.resolveUserId(requestorName);
        String propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        propertySetSteps.listOfUsersForPropertySetIsGotByUser(requestorId, propertySetId, null, null, null, null, null);
    }
}
