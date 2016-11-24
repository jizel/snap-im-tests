package travel.snapshot.dp.qa.steps.identity.users;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
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

    @Given("^The following users exist for customer \"([^\"]*)\" as primary \"([^\"]*)\"$")
    public void theFollowingUsersExistForCustomer(String customerId, Boolean isPrimary, List<UserCreateDto> users) throws Throwable {
        usersSteps.followingUsersExist(users, customerId, isPrimary);
    }

    @When("^The following users is created for customer \"([^\"]*)\" as primary \"([^\"]*)\"$")
    public void User_is_created(String customerId, Boolean isPrimary, List<UserCreateDto> users) throws Throwable {
        usersSteps.followingUserIsCreated(users.get(0), customerId, isPrimary);
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
    public void User_with_user_name_updated_with_data(String userName, List<UserDto> users) throws Throwable {
        usersSteps.updateUserWithUserName(userName, users.get(0));
    }

    @Then("^Updated user with userName \"([^\"]*)\" has data$")
    public void Updated_user_with_user_name_has_data(String userName, List<UserDto> users) throws Throwable {
        usersSteps.userWithUserNameHasData(userName, users.get(0));
    }

    @When("^User with userName \"([^\"]*)\" is updated with data if updated before$")
    public void User_with_user_name_is_updated_with_data_if_updated_before(String userName, List<UserDto> users) throws Throwable {
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
        usersSteps.numberOfEntitiesInResponse(UserDto.class, count);
    }

    @Then("^There are (\\d+) customerUsers returned$")
    public void There_are_returned_customerUsers_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(CustomerUserRelationshipDto.class, count);
    }

    @Then("^There are users with following usernames returned in order: (.*)$")
    public void There_are_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        usersSteps.usernamesAreInResponseInOrder(usernames);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is added to user with username \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_added_to_user_with_username_with_relationship_type_and_entity_with_code(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.roleIsAddedToUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }



    @Given("^Relation between role with name \"([^\"]*)\" for application id \"([^\"]*)\" and user with username \"([^\"]*)\" exists with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Relation_between_role_with_name_for_application_i_and_user_with_username_exists_with_relationship_type_and_entity_with_id(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.relationExistsBetweenRoleAndUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is removed from user with username \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_removed_from_user_with_username_with_relationship_type_and_entity_with_code(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.roleIsDeletedFromUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @Then("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is not there for user with username \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
    public void Role_with_name_for_application_id_is_not_there_for_user_with_username_with_relationship_type_and_entity_with_code(String roleName, String applicationId, String username, String relationshipType, String entityId) throws Throwable {
        RoleDto role = roleBaseSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        usersSteps.roleDoesntExistForUserWithRelationshipTypeEntity(role, username, relationshipType, entityId);
    }

    @When("^Nonexistent role is removed from user with username \"([^\"]*)\" with relationship_type \"([^\"]*)\" and entity with id \"([^\"]*)\"$")
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

    @When("^Activating user with username \"([^\"]*)\"$")
    public void activatingUserWithUsername(String username) throws Throwable {
        usersSteps.activateUser(username);
    }

    @When("^Deactivating user with username \"([^\"]*)\"$")
    public void deactivatingUserWithUsername(String username) throws Throwable {
        usersSteps.inactivateUser(username);
    }

    @When("^User with not existing id \"([^\"]*)\" is inactivated$")
    public void userWithNotExistingIdIsInactivated(String id) throws Throwable {
        usersSteps.inactivateNotExistingUser(id);
    }

    @When("^User with not existing id \"([^\"]*)\" is activated$")
    public void userWithNotExistingIdIsActivated(String id) throws Throwable {
        usersSteps.activateNotExistingUser(id);
    }

    @When("^User with id \"([^\"]*)\" is activated$")
    public void userWithCodeIsActivated(String name) throws Throwable {
        usersSteps.activateUserWithName(name);
    }

    @When("^User with id \"([^\"]*)\" is inactivated$")
    public void userWithIdIsInactivated(String name) throws Throwable {
        usersSteps.inactivateUserWithName(name);
    }

    @Then("^User with id \"([^\"]*)\" is active$")
    public void userWithIdIsActive(String name) throws Throwable {
        usersSteps.isActiveSetTo(true, name);
    }

    @Then("^User with id \"([^\"]*)\" is not active$")
    public void userWithIdIsNotActive(String name) throws Throwable {
        usersSteps.isActiveSetTo(false, name);
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
}
