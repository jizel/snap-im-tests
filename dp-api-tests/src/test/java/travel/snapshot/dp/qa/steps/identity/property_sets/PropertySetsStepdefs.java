package travel.snapshot.dp.qa.steps.identity.property_sets;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.List;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class PropertySetsStepdefs {

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private PropertySteps propertySteps;

    @Given("^The following property sets exist for customer with id \"([^\"]*)\" and user \"([^\"]*)\"$")
    public void theFollowingPropertySetsExistForCustomerWithCodeAndUser(String customerId, String userId, List<PropertySetCreateDto> propertySets) throws Throwable {
        propertySetSteps.followingPropertySetsExist(propertySets, customerId, userId);
    }

    @Given("^All property sets are deleted for customers with codes: (.*)$")
    public void All_property_sets_are_deleted_for_customers_c_t_c_t(List<String> customerCodes) throws Throwable {
        List<CustomerDto> customers = customerSteps.getCustomersForCodes(customerCodes);
        propertySetSteps.deleteAllPropertySetsForCustomer(customers);
    }

    @Given("^All users are removed for property_sets for customer with code \"([^\"]*)\" with names: (.*)$")
    public void All_users_are_removed_for_property_sets(String customerCode, List<String> names) throws Throwable {
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.removeAllUsersForPropertySetsForCustomer(names, c);
    }

    @Given("^Relation between user with username \"([^\"]*)\" and property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" exists$")
    public void Relation_between_user_with_username_and_property_set_with_name_for_customer_with_code_exists(String username, String propertySetName, String customerCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.relationExistsBetweenUserAndPropertySetForCustomer(u, propertySetName, c);
    }

    @Given("^All properties are removed from property_sets for customer with code \"([^\"]*)\" with names: (.*)$")
    public void All_properties_are_removed_from_property_sets_for_customer_with_code_with_names_ps__name_ps__name(String customerCode, List<String> propertySetNames) throws Throwable {
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.removeAllPropertiesFromPropertySetsForCustomer(propertySetNames, c);
    }

    @Given("^Relation between property with code \"([^\"]*)\" and property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" exists$")
    public void Relation_between_property_with_code_and_property_set_with_name_for_customer_with_code_exists(String propertyCode, String propertySetName, String customerCode) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.relationExistsBetweenPropertyAndPropertySetForCustomer(p, propertySetName, c);
    }

    @When("^The following property set is created for customer with id \"([^\"]*)\" and user \"([^\"]*)\"$")
    public void theFollowingPropertySetIsCreatedForCustomerWithIdAndUser(String customerId, String userId, List<PropertySetCreateDto> propertySets) throws Throwable {
        propertySetSteps.followingPropertySetIsCreated(propertySets.get(0), customerId, userId);
    }

    @When("^List of property sets is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                     @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                     @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                     @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                     @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySetSteps.listOfPropertySetsIsGotWith(limit, cursor, filter, sort, sortDesc);

    }

    @When("^Property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" is deleted$")
    public void Property_set_with_name_for_customer_with_code_is_deleted(String propertySetName, String customerCode) throws Throwable {
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.propertySetWithNameForCustomerIsDeleted(c, propertySetName);
    }

    @When("^Nonexistent property set id is got$")
    public void Nonexistent_property_set_id_is_got() throws Throwable {
        propertySetSteps.propertysetWithIdIsGot("nonexistent");
    }

    @When("^Nonexistent property set id is deleted$")
    public void Nonexistent_property_set_id_is_deleted() throws Throwable {
        propertySetSteps.deletePropertySetWithId("nonexistent");
    }

    @When("^User with username \"([^\"]*)\" is added to property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void User_with_username_is_added_to_property_set_with_name_for_customer_with_code(String username, String propertySetName, String customerCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.userIsAddedToPropertySetForCustomer(u, propertySetName, c);
    }

    @When("^User with username \"([^\"]*)\" is removed from property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_property_set_with_name_for_customer_with_code(String username, String propertySetName, String customerCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.userIsRemovedFromPropertySetForCustomer(u, propertySetName, c);
    }

    @When("^Nonexistent user is removed from property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_property_set_with_name_for_customer_with_code(String propertySetName, String customerCode) throws Throwable {
        UserDto user = new UserDto();
        user.setUserId("nonexistent");
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.userIsRemovedFromPropertySetForCustomer(user, propertySetName, c);
    }

    @When("^List of properties for property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_properties_for_property_set_with_name_for_customer_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertySetName, String customerCode,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.listOfPropertiesIsGotWith(propertySetName, c, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of users for property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_property_set_with_name_for_customer_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertySetName, String customerCode,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                               @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.listOfUsersIsGotWith(propertySetName, c, limit, cursor, filter, sort, sortDesc);
    }

    @When("^Nonexistent property is removed from property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Nonexistent_property_is_removed_from_property_set_with_name_for_customer_with_code(String propertySetName, String customerCode) throws Throwable {
        PropertyDto p = new PropertyDto();
        p.setPropertyId("nonexistent");
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.propertyIsRemovedFromPropertySetForCustomer(p, propertySetName, c);
    }

    @When("^Property with code \"([^\"]*)\" is added to property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Property_with_code_is_added_to_property_set_with_name_for_customer_with_code(String propertyCode, String propertySetName, String customerCode) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.propertyIsAddedToPropertySetForCustomer(p, propertySetName, c);
    }

    @When("^Property with code \"([^\"]*)\" is removed from property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Property_with_code_is_removed_from_property_set_with_name_for_customer_with_code(String propertyCode, String propertySetName, String customerCode) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.propertyIsRemovedFromPropertySetForCustomer(p, propertySetName, c);
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

    @Then("^User with username \"([^\"]*)\" isn't there for property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_property_set_with_name_for_customer_with_code(String username, String propertySetName, String customerCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.userDoesntExistForPropertySetForCustomer(u, propertySetName, c);
    }

    @Then("^Property with code \"([^\"]*)\" isn't there for property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Property_with_code_isn_t_there_for_property_set_with_name_for_customer_with_code(String propertyCode, String propertySetName, String customerCode) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        CustomerDto c = customerSteps.getCustomerByCodeInternal(customerCode);
        propertySetSteps.propertiesDoesntExistForPropertySetForCustomer(p, propertySetName, c);
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

    @Then("^There are (\\d+) property set properties  returned$")
    public void There_are_returned_property_set_properties_returned(int count) throws Throwable {
        propertySetSteps.numberOfEntitiesInResponse(PropertySetPropertyRelationshipDto.class, count);
    }

    @When("^Property set with name \"([^\"]*)\" for customer \"([^\"]*)\" is updated with following data$")
    public void propertySetWithNameIsUpdatedWithFollowingData(String propName, String customerId, List<PropertySetUpdateDto> propSet) throws Throwable {
        propertySetSteps.updatePropertySet(propName, customerId, propSet.get(0));
    }

    @Then("^Updated property set with name \"([^\"]*)\" for customer \"([^\"]*)\" has following data$")
    public void updatedPropertySetWithNameForCustomerWithCodeHasFollowingData(String propName, String customerId, List<PropertySetUpdateDto> propSet) throws Throwable {
        propertySetSteps.comparePropertySets(propName, customerId, propSet.get(0));
    }
}
