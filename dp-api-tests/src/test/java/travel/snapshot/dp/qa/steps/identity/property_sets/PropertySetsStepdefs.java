package travel.snapshot.dp.qa.steps.identity.property_sets;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_ETAG;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class PropertySetsStepdefs {

    private static final String USER_ID = "userId";
    private static final String PROPERTY_SET_ID = "propertySetId";

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private PropertySteps propertySteps;

    // Help methods

    public Map<String, String> getValidUserPropertySetIdsFromNames(String username, String propertySetName) {
        String userId = usersSteps.resolveUserId(username);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        Map<String, String> userPropertyIds = new HashMap<>();
        userPropertyIds.put(USER_ID, userId);
        userPropertyIds.put(PROPERTY_SET_ID, propertySet.getPropertySetId());
        return userPropertyIds;
    }

//    End of help methods section


    @Given("^The following property sets exist for customer with id \"([^\"]*)\" and user \"([^\"]*)\"$")
    public void theFollowingPropertySetsExistForCustomerWithCodeAndUser(String customerId, String username, List<PropertySetDto> propertySets) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user,is(notNullValue()));
        propertySetSteps.followingPropertySetsExist(propertySets, customerId, user.getUserId());
    }

    @Given("^All property sets are deleted for customers with ids: (.*)$")
    public void All_property_sets_are_deleted_for_customers_c_t_c_t(List<String> customerIds) throws Throwable {
        List<CustomerDto> customers = customerSteps.getCustomersForIds(customerIds);
        propertySetSteps.deleteAllPropertySetsForCustomer(customers);
    }

    @Given("^All users are removed for property_sets for customer with id \"([^\"]*)\" with names: (.*)$")
    public void All_users_are_removed_for_property_sets(String customerId, List<String> names) throws Throwable {
        CustomerDto customer = customerSteps.getCustomerById(customerId);
        propertySetSteps.removeAllUsersForPropertySetsForCustomer(names, customer);
    }

    @Given("^Relation between user \"([^\"]*)\" and property set with name \"([^\"]*)\" for customer with id \"([^\"]*)\" exists$")
    public void Relation_between_user_with_username_and_property_set_with_name_for_customer_with_code_exists(String username, String propertySetName, String customerId) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        CustomerDto customer= customerSteps.getCustomerById(customerId);
        propertySetSteps.relationExistsBetweenUserAndPropertySetForCustomer(userId, propertySetName, customer);
    }

    @Given("^All properties are removed from property_sets for customer with id \"([^\"]*)\" with names: (.*)$")
    public void All_properties_are_removed_from_property_sets_for_customer_with_code_with_names_ps__name_ps__name(String customerId, List<String> propertySetNames) throws Throwable {
        CustomerDto customer= customerSteps.getCustomerById(customerId);
        propertySetSteps.removeAllPropertiesFromPropertySetsForCustomer(propertySetNames, customer);
    }

    @Given("^Relation between property with code \"([^\"]*)\" and property set with name \"([^\"]*)\" exists$")
    public void Relation_between_property_with_code_and_property_set_with_name_for_customer_with_code_exists(String propertyCode, String propertySetName) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        propertySetSteps.relationExistsBetweenPropertyAndPropertySet(property.getPropertyId(), propertySet.getPropertySetId());
    }

    @When("^The following property set is created for customer with id \"([^\"]*)\"$")
    public void theFollowingPropertySetIsCreatedForCustomerWithId(String customerId, List<PropertySetDto> propertySets) throws Throwable {
        propertySetSteps.followingPropertySetIsCreated(propertySets.get(0), customerId);
    }

    @When("^List of property sets is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                     @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                     @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                     @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                     @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySetSteps.listOfPropertySetsIsGotWith(limit, cursor, filter, sort, sortDesc);

    }


    @When("^List of property sets is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void listOfPropertySetsIsGotWithLimitAndCursorAndFilterAndSortAndSort_descByUser(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                            @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                            @Transform(NullEmptyStringConverter.class) String filter,
                                                                                            @Transform(NullEmptyStringConverter.class) String sort,
                                                                                            @Transform(NullEmptyStringConverter.class) String sortDesc, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user,is(notNullValue()));
        propertySetSteps.listOfPropertySetsIsGotByUser(user.getUserId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^Property set \"([^\"]*)\" is deleted$")
    public void Property_set_with_name_for_customer_with_code_is_deleted(String propertySetName) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        propertySetSteps.deletePropertySet(propertySet.getPropertySetId());
    }

    @When("^Property set \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void propertySetIsDeletedByUser(String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        propertySetSteps.deletePropertySetByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
    }

    @When("^Nonexistent property set id is got$")
    public void Nonexistent_property_set_id_is_got() throws Throwable {
        propertySetSteps.propertysetWithIdIsGot(NON_EXISTENT_ID);
    }

    @When("^Nonexistent property set id is deleted$")
    public void Nonexistent_property_set_id_is_deleted() throws Throwable {
        propertySetSteps.deleteEntity(NON_EXISTENT_ID, DEFAULT_SNAPSHOT_ETAG);
    }

    @When("^User \"([^\"]*)\" is added to property set with name \"([^\"]*)\"$")
    public void User_with_username_is_added_to_property_set_with_name_for_customer_with_code(String username, String propertySetName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        Response response = propertySetSteps.addUserToPropertySet(ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
        propertySetSteps.setSessionResponse(response);
    }


    @When("^User \"([^\"]*)\" is added to property set with name \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userWithUsernameIsAddedToPropertySetWithNameByUser(String username, String propertySetName, String performerName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));
        Response response = propertySetSteps.addUserToPropertySetByUser(performer.getUserId(), ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
        propertySetSteps.setSessionResponse(response);
    }

    @When("^User \"([^\"]*)\" is removed from property set \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_property_set_with_name_for_customer_with_code(String username, String propertySetName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        propertySetSteps.removeUserFromPropertySet(ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
    }

    @When("^Nonexistent user is removed from property set with name \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_property_set_with_name_for_customer_with_code(String propertySetName) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        propertySetSteps.removeUserFromPropertySet(NON_EXISTENT_ID, propertySet.getPropertySetId());
    }


    @When("^User \"([^\"]*)\" is removed from property set \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userIsRemovedFromPropertySetByUser(String username, String propertySetName, String performerName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));
        propertySetSteps.removeUserFromPropertySetByUser(performer.getUserId(), ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
    }

    @When("^List of properties for property set with name \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_properties_for_property_set_with_name_for_customer_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertySetName,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        propertySetSteps.listOfPropertiesIsGotWith(propertySet.getPropertySetId(), limit, cursor, filter, sort, sortDesc);
    }


    @When("^List of all properties for property set with name \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllPropertiesForPropertySetWithNameIsRequested(String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        propertySetSteps.listOfPropertiesForPropertySetIsGotByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), null, null, null, null, null);
    }

    @When("^List of users for property set with name \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_property_set_with_name_for_customer_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertySetName, String customerId,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        propertySetSteps.listOfUsersIsGotWith(propertySetName, limit, cursor, filter, sort, sortDesc);
    }


    @When("^List of all users for property set \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllUsersForPropertySetIsRequestedByUser(String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        propertySetSteps.listOfUsersForPropertySetIsGotByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), null, null, null, null, null);
    }

    @When("^Nonexistent property is removed from property set \"([^\"]*)\"$")
    public void Nonexistent_property_is_removed_from_property_set_with_name_for_customer_with_code(String propertySetName) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));
        propertySetSteps.removePropertyFromPropertySet(NON_EXISTENT_ID, propertySet.getPropertySetId());
    }

    @When("^Property with code \"([^\"]*)\" is added to property set \"([^\"]*)\"$")
    public void Property_with_code_is_added_to_property_set_with_name_for_customer_with_code(String propertyCode, String propertySetName) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        Response response = propertySetSteps.addPropertyToPropertySet(property.getPropertyId(), propertySet.getPropertySetId());
        propertySetSteps.setSessionResponse(response);
    }

    @When("^Property with code \"([^\"]*)\" is added to property set \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void propertyWithCodeIsAddedToPropertySetByUser(String propertyCode, String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        Response response = propertySetSteps.addPropertyToPropertySetByUser(ids.get(USER_ID), property.getPropertyId(), ids.get(PROPERTY_SET_ID));
        propertySetSteps.setSessionResponse(response);
    }

    @When("^Property with code \"([^\"]*)\" is removed from property set \"([^\"]*)\"$")
    public void Property_with_code_is_removed_from_property_set_with_name_for_customer_with_code(String propertyCode, String propertySetName) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        propertySetSteps.removePropertyFromPropertySet(property.getPropertyId(), propertySet.getPropertySetId());
    }


    @When("^Property with code \"([^\"]*)\" is removed from property set \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void propertyWithCodeIsRemovedFromPropertySetByUser(String propertyCode, String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        propertySetSteps.removePropertyFromPropertySetByUser(ids.get(USER_ID), property.getPropertyId(), ids.get(PROPERTY_SET_ID));
    }

    @Then("^There are (\\d+) property sets returned$")
    public void There_are_returned_property_sets_returned(int count) throws Throwable {
        propertySetSteps.numberOfEntitiesInResponse(PropertySetDto.class, count);
    }

    @Then("^Property set with same id doesn't exist$")
    public void Property_set_with_same_id_doesn_t_exist() throws Throwable {
        propertySetSteps.propertySetIdInSessionDoesntExist();
    }

    @Then("^There are property sets with following names returned in order: (.*)$")
    public void There_are_property_sets_with_following_names_returned_in_order(List<String> names) throws Throwable {
        propertySetSteps.propertySetNamesAreInResponseInOrder(names);
    }

    @Then("^User with \"([^\"]*)\" isn't there for property set \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_property_set_with_name_for_customer_with_code(String username, String propertySetName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        PropertySetUserRelationshipDto relationship = propertySetSteps.getUserForPropertySet(ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
        assertThat("Relation between user " + username + " and property set " + propertySetName + "should not exist!",relationship, is(nullValue()));
    }

    @Then("^Property with code \"([^\"]*)\" isn't there for property set with name \"([^\"]*)\" for customer with id \"([^\"]*)\"$")
    public void Property_with_code_isn_t_there_for_property_set_with_name_for_customer_with_code(String propertyCode, String propertySetName, String customerId) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        CustomerDto customer= customerSteps.getCustomerById(customerId);
        propertySetSteps.propertiesDoesntExistForPropertySetForCustomer(p, propertySetName, customer);
    }

    @Then("^There are properties with following names returned in order: (.*)$")
    public void There_are_properties_with_following_names_returned_in_order_expected_names(List<String> propertyNames) throws Throwable {
        propertySetSteps.propertyNamesAreInResponseInOrder(propertyNames);
    }

    @Then("^There are property set users with following usernames returned in order: (.*)$")
    public void There_are_property_set_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        propertySetSteps.usernamesAreInResponseInOrder(usernames);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same property set$")
    public void header_is_set_and_contains_the_same_property_set(String header) throws Throwable {
        propertySetSteps.comparePropertySetOnHeaderWithStored(header);
    }

    @Then("^There are (\\d+) property set properties returned$")
    public void There_are_returned_property_set_properties_returned(int count) throws Throwable {
        propertySetSteps.numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, count);
    }

    @When("^Property set \"([^\"]*)\" is updated with following data$")
    public void propertySetWithNameIsUpdatedWithFollowingData(String propertySetName, List<PropertySetUpdateDto> propSet) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat("Property set " + propertySetName + " not found!", propertySet, is(notNullValue()));
        propertySetSteps.updatePropertySet(propertySet.getPropertySetId(), propSet.get(0));
    }

    @When("^Property set \"([^\"]*)\" is updated with following data by user \"([^\"]*)\"$")
    public void propertySetWithNameIsUpdatedWithFollowingDataByUser(String propertySetName, String username, List<PropertySetUpdateDto> propertySetUpdates) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        propertySetSteps.updatePropertySetByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), propertySetUpdates.get(0));
    }

    @Then("^Updated property set \"([^\"]*)\" has following data$")
    public void updatedPropertySetWithNameForCustomerWithCodeHasFollowingData(String propertySetName, List<PropertySetUpdateDto> propSet) throws Throwable {
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat("Property set " + propertySetName + " not found!", propertySet, is(notNullValue()));
        propertySetSteps.comparePropertySets(propertySet.getPropertySetId(), propSet.get(0));
    }

    @When("^Relation between user \"([^\"]*)\" and property set \"([^\"]*)\" is activated$")
    public void relationBetweenUserAndPropertySetIsActivated(String username, String propertySetName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        UserPropertySetRelationshipUpdateDto userPropertySetRelation = new UserPropertySetRelationshipUpdateDto();
        userPropertySetRelation.setIsActive(true);

        propertySetSteps.updateUserPropertySetRelation(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), userPropertySetRelation);
    }

    @When("^Relation between user \"([^\"]*)\" and property set \"([^\"]*)\" is inactivated$")
    public void relationBetweenUserAndPropertySetIsInactivated(String username, String propertySetName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        UserPropertySetRelationshipUpdateDto userPropertySetRelation = new UserPropertySetRelationshipUpdateDto();
        userPropertySetRelation.setIsActive(false);

        propertySetSteps.updateUserPropertySetRelation(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), userPropertySetRelation);
    }

    @Given("^Check is active attribute is \"([^\"]*)\" for relation between user \"([^\"]*)\" and property set \"([^\"]*)\"$")
    public void checkIsActiveAttributeIsForRelationBetweenUserAndPropertySet(Boolean isActive, String username, String propertySetName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        PropertySetUserRelationshipDto propertySetUserRelation = propertySetSteps.getUserForPropertySet(ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
        assertThat(propertySetUserRelation, is(notNullValue()));
        assertThat(propertySetUserRelation.getIsActive(), is(isActive));
    }

    @When("^IsActive for relation between user \"([^\"]*)\" and property set \"([^\"]*)\" is set to \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void relationBetweenUserAndPropertySetIsSetTo(String username, String propertySetName, Boolean isActive, String performerName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));
        UserPropertySetRelationshipUpdateDto userPropertySetRelation = new UserPropertySetRelationshipUpdateDto();
        userPropertySetRelation.setIsActive(isActive);

        propertySetSteps.updateUserPropertySetRelationByUser(performer.getUserId(), ids.get(USER_ID), ids.get(PROPERTY_SET_ID), userPropertySetRelation);
    }

    @When("^Property set \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void propertySetIsRequestedByUser(String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);

        propertySetSteps.getPropertySetByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
    }

    @When("^Property with code \"([^\"]*)\" for property set \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void propertyWithCodeForPropertySetIsRequestedByUser(String propertyCode, String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        propertySetSteps.getPropertyForPropertySetByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), property.getPropertyId());

    }

    @When("^User \"([^\"]*)\" for property set \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void userForPropertySetIsRequestedByUser(String username, String propertySetName, String performerName) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));
        propertySetSteps.getUserForPropertySetByUser(performer.getUserId(), ids.get(USER_ID), ids.get(PROPERTY_SET_ID));
    }

    @When("^Child property sets of property set \"([^\"]*)\" are requested by user \"([^\"]*)\"$")
    public void childPropertySetsOfPropertySetAreRequestedByUser(String propertySetName, String username) throws Throwable {
        Map<String, String> ids = getValidUserPropertySetIdsFromNames(username, propertySetName);
        propertySetSteps.getChildPropertySetsByUser(ids.get(USER_ID), ids.get(PROPERTY_SET_ID), null, null, null, null, null, null);
    }
}
