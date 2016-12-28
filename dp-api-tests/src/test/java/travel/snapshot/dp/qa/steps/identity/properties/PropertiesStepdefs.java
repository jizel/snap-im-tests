package travel.snapshot.dp.qa.steps.identity.properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class PropertiesStepdefs {

    private static final String USER_ID = "userId";
    private static final String PROPERTY_ID = "propertyId";

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private PropertySteps propertySteps;
    @Steps
    private UsersSteps usersSteps;
    @Steps
    private CustomerSteps customerSteps;

    // Help methods

    public Map<String, String> getValidUserPropertyIdsFromNameAndCode(String username, String propertyCode) {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        Map<String, String> userPropertyIds = new HashMap<>();
        userPropertyIds.put(USER_ID, user.getUserId());
        userPropertyIds.put(PROPERTY_ID, property.getPropertyId());
        return userPropertyIds;
    }
//    End of help methods section

    // --- given ---

    @Given("^The following properties exist with random address and billing address for user \"([^\"]*)\"$")
    public void theFollowingPropertiesExistWithRandomAddressAndBillingAddressForUser(String userId, List<PropertyCreateDto> properties) throws Throwable {
        propertySteps.followingPropertiesExist(properties, userId);
    }

    @Given("^All users are removed for properties with codes: (.*)$")
    public void All_users_are_removed_for_properties_with_codes(List<String> propertyCodes) throws Throwable {
        propertySteps.removeAllUsersFromPropertiesWithCodes(propertyCodes);
    }

    @Given("^Relation between user with username \"([^\"]*)\" and property with code \"([^\"]*)\" exists$")
    public void Relation_between_user_with_username_and_property_with_code_exists(String username, String propertyCode) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        propertySteps.relationExistsBetweenUserAndProperty(user, propertyCode);
    }

    // --- when ---

    @When("^Property with code \"([^\"]*)\" exists with etag$")
    public void Property_with_code_exists_with_etag(String code) throws Throwable {
        propertySteps.getPropertyByCodeUsingEtag(code);
    }

    @When("^Property with code \"([^\"]*)\" exists for etag, forced new etag through update$")
    public void Property_with_code_exists_for_etag_forced_new_etag_through_update(String code) throws Throwable {
        propertySteps.getPropertyWithCodeUsingEtagAfterUpdate(code);
    }

    @When("^Nonexistent property id sent$")
    public void Nonexistent_property_id_sent() throws Throwable {
        propertySteps.getProperty("nonexistent_id");
    }

    @When("^List of properties is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_properties_exists_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                  @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                  @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                  @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                  @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfPropertiesExistsWith(limit, cursor, filter, sort, sortDesc);
    }

    @When("^The following property is created with random address and billing address for user \"([^\"]*)\"$")
    public void theFollowingPropertyIsCreatedWithRandomAddressAndBillingAddressForUser(String userId, List<PropertyCreateDto> properties) throws Throwable {
        propertySteps.followingPropertyIsCreated(properties.get(0), userId);
    }

    @When("^A property for customer \"([^\"]*)\" from country \"([^\"]*)\" region \"([^\"]*)\" code \"([^\"]*)\" email \"([^\"]*)\" is created with userId \"([^\"]*)\"$")
    public void aPropertyForCustomerFromCountryRegionCodeEmailIsCreatedWithUserId(String customerId, String country, String region, String code, String email, String userId) throws Throwable {
        AddressDto address = new AddressDto();
        PropertyCreateDto property = new PropertyCreateDto();
        address.setAddressLine1("someAddress");
        address.setCity("someCity");
        address.setZipCode("1234");
        address.setCountry(country);
        address.setRegion(region);
        property.setAnchorCustomerId(customerId);
        property.setPropertyName("someProperty");
        property.setPropertyCode(code);
        property.setEmail(email);
        property.setIsDemoProperty(true);
        property.setTimezone("GMT");
        propertySteps.followingPropertyIsCreatedWithAddress(property, address, userId);
    }

    @When("^Property with code \"([^\"]*)\" is deleted$")
    public void Property_with_code_is_deleted(String code) throws Throwable {
        propertySteps.deletePropertyWithCode(code);
    }

    @When("^Nonexistent property id is deleted$")
    public void Nonexistent_property_id_is_deleted() throws Throwable {
        propertySteps.deletePropertyById("nonexistent_id");
    }

    @When("^User with username \"([^\"]*)\" is added to property with code \"([^\"]*)\"$")
    public void User_with_username_is_added_to_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.userIsAddedToProperty(u, propertyCode);
    }

    @When("^User with username \"([^\"]*)\" is removed from property with code \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        propertySteps.userIsDeletedFromProperty(user, propertyCode);
    }

    @When("^Nonexistent user is removed from property with code \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_property_with_code(String propertyCode) throws Throwable {
        UserDto user = new UserDto();
        user.setUserId("nonexistent");
        propertySteps.userIsDeletedFromProperty(user, propertyCode);
    }

    @When("^List of users for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfUsersIsGotWith(propertyCode, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of customers for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfCustomersIsGotWith(propertyCode, limit, cursor, filter, sort, sortDesc);
    }

    // --- then ---

    @Then("^Body contains property with attribute \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyContainsEntityWith(atributeName);
    }

    @Then("^Body contains property with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute_value(String atributeName, String value) throws Throwable {
        propertySteps.bodyContainsPropertyWith(atributeName, value);
    }

    @Then("^Body does not contain property with attribute \"([^\"]*)\"$")
    public void Body_does_not_contain_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyDoesntContainEntityWith(atributeName);
    }

    @Then("^There are (\\d+) properties returned$")
    public void There_are_properties_returned(int count) throws Throwable {
        propertySteps.numberOfEntitiesInResponse(PropertyDto.class, count);
    }

    @Then("^All customers are customers of property with code \"([^\"]*)\"$")
    public void each_customer_is_a_customer_of_property_with_code(String propertyCode) throws Throwable {
        CustomerDto[] allCustomers = customerSteps.listOfCustomersIsGotWith(null, "0", null, null, null).as(CustomerDto[].class);
        propertySteps.allCustomersAreCustomersOfProperty(allCustomers, propertyCode);
    }

    // --- and ---

    @Then("^Property with same id doesn't exist$")
    public void Property_with_same_id_doesn_t_exist() throws Throwable {
        propertySteps.propertyIdInSessionDoesntExist();
    }

    @Then("^User with username \"([^\"]*)\" isn't there for property with code \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.userDoesntExistForProperty(u, propertyCode);
    }

    @Then("^There are property users with following usernames returned in order: (.*)$")
    public void There_are_property_users_with_following_usernames_returned_in_order_expected_usernames(List<String> usernames) throws Throwable {
        propertySteps.usernamesAreInResponseInOrder(usernames);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same property$")
    public void header_is_set_and_contains_the_same_property(String header) throws Throwable {
        propertySteps.comparePropertyOnHeaderWithStored(header);
    }

    @When("^Property with code \"([^\"]*)\" is activated$")
    public void property_With_Code_Is_Activated(String code) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(code);
        assertThat(property, is(notNullValue()));
        propertySteps.setPropertyIsActive(property.getPropertyId(), true);
    }

    @When("^Property with code \"([^\"]*)\" is inactivated$")
    public void property_With_Code_Is_Inactivated(String code) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(code);
        assertThat(property, is(notNullValue()));
        propertySteps.setPropertyIsActive(property.getPropertyId(), false);
    }

    @Then("^Property with code \"([^\"]*)\" is active$")
    public void Customer_with_code_is_active(String code) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(code);
        assertThat(property, is(notNullValue()));
        assertThat("Property is not active!", property.getIsActive(), is(true));
    }

    @Then("^Property with code \"([^\"]*)\" is not active$")
    public void Customer_with_code_is_not_active(String code) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(code);
        assertThat(property, is(notNullValue()));
        assertThat("Property is active but should be inactive!", property.getIsActive(), is(false));
    }

    @Then("^Property \"([^\"]*)\" is not assigned to customer \"([^\"]*)\"$")
    public void propertyIsNotAssignedToCustomer(String propertyCode, String customerId) throws Throwable {
        propertySteps.customerDoesNotExistForProperty(customerId, propertyCode);
    }

    @When("^Property set with name \"([^\"]*)\" for property with code \"([^\"]*)\" is got$")
    public void Property_set_with_name_for_property_with_code_is_got(String propertySetName, String propertyCode) {
        propertySteps.propertyPropertySetWithNameIsGot(propertySetName, propertyCode);
    }

    @When("^List of api subscriptions is got for property with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfApiSubscriptionsIsGotForPropertyWithIdAndLimitAndCursorAndFilterAndSortAndSort_desc(String propertyId,
                                                                                                          @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                          @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                          @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                          @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                          @Transform(NullEmptyStringConverter.class) String sortDesc) {
        propertySteps.listOfApiSubscriptionsIsGot(propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of property sets is got for property with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_is_got_for_property_with_id_and_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyId,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String sortDesc) {
        propertySteps.listOfPropertiesPropertySetsIsGot(propertyId, limit, cursor, filter, sort, sortDesc);
    }
    
    @When("^Property with code \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void propertyWithCodeIsRequestedByUser(String propertyCode, String userName) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(userName);
        assertThat(user, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternalByUser(user.getUserId(), propertyCode);
        assertThat(String.format("User %s doesn't see property %s or the property doesn't exist", userName), property, is(notNullValue()));

//        Sets the session response
        propertySteps.getPropertyByUser(user.getUserId(), propertyCode);
    }

    @When("^Set is active to \"([^\"]*)\" for relation between user \"([^\"]*)\" and property with code \"([^\"]*)\"$")
    public void isActiveSetToForRelationBetweenUserAndPropertyWithCode(Boolean isActive, String username, String propertyCode) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UserPropertyRelationshipUpdateDto userPropertyRelationship = new UserPropertyRelationshipUpdateDto();
        userPropertyRelationship.setIsActive(isActive);

        usersSteps.updateUserPropertyRelationship(ids.get(USER_ID), ids.get(PROPERTY_ID), userPropertyRelationship);
    }


    @And("^Check is active attribute is \"([^\"]*)\" for relation between user \"([^\"]*)\" and property with code \"([^\"]*)\"$")
    public void isActiveAttributeIsForRelationBetweenUserAndPropertyWithCode(Boolean isActive, String username, String propertyCode) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        PropertyUserRelationshipDto userPropertyRelation = propertySteps.getUserForProperty(ids.get(PROPERTY_ID), ids.get(USER_ID));
        assertThat(userPropertyRelation, is(notNullValue()));
        assertThat(userPropertyRelation.getIsActive(), is(isActive));
    }

    // TODO reuse existing code

//
//    @When("^File \"([^\"]*)\" is used for \"([^\"]*)\"$")
//    public void customer_file_is_used_for_method(String fileName, String method) throws Throwable {
//        switch (method) {
//            case "POST": {
//                propertySteps.fileIsUsedForCreation("/messages/identity/customers/" + fileName);
//                break;
//            }
//            default: break;
//        }
//
//    }
//
//    @When("^Customer with code \"([^\"]*)\" is updated with data$")
//    public void Customer_with_code_is_updated_with_data(String code, List<Customer> customers) throws Throwable {
//        propertySteps.updateCustomerWithCode(code, customers.get(0));
//    }
//
//    @Then("^Updated customer with code \"([^\"]*)\" has data$")
//    public void Updated_customer_with_code_has_data(String code, List<Customer> customers) throws Throwable {
//        propertySteps.customerWithCodeHasData(code, customers.get(0));
//    }
//
//    @When("^Customer with code \"([^\"]*)\" is updated with data if updated before$")
//    public void Customer_with_code_is_updated_with_data_if_updated_before(String code, List<Customer> customers) throws Throwable {
//        propertySteps.updateCustomerWithCodeIfUpdatedBefore(code, customers.get(0));
//    }

}
