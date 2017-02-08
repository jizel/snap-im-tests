package travel.snapshot.dp.qa.steps.identity.properties;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

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
    private PropertySetSteps propertySetSteps;
    @Steps
    private UsersSteps usersSteps;
    @Steps
    private CustomerSteps customerSteps;
    @Steps
    private BasicSteps basicSteps;

    // Help methods

    public Map<String, String> getValidUserPropertyIdsFromNameAndCode(String username, String propertyCode) {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat("User " + username + " is null", user, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat("Property with code " + propertyCode + " is null", property, is(notNullValue()));

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


    @Given("^The following properties exist with random address and billing address$")
    public void theFollowingPropertiesExistWithRandomAddressAndBillingAddress(List<PropertyCreateDto> properties) throws Throwable {
        propertySteps.followingPropertiesExist(properties, usersSteps.DEFAULT_SNAPSHOT_USER_ID);
    }

    @Given("^All users are removed for properties with codes: (.*)$")
    public void All_users_are_removed_for_properties_with_codes(List<String> propertyCodes) throws Throwable {
        propertySteps.removeAllUsersFromPropertiesWithCodes(propertyCodes);
    }

    @Given("^Relation between user with username \"([^\"]*)\" and property with code \"([^\"]*)\" exists$")
    public void Relation_between_user_with_username_and_property_with_code_exists(String username, String propertyCode) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.relationExistsBetweenUserAndProperty(ids.get(USER_ID), ids.get(PROPERTY_ID));
    }

    // --- when ---

    @When("^User \"([^\"]*)\" is added to property with code \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userIsAddedToPropertyWithCodeByUser(String username, String propertyCode, String performerName) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        Response response = propertySteps.addUserToPropertyByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), performer.getUserId());
        basicSteps.setSessionResponse(response);
    }

    @When("^Property with code \"([^\"]*)\" is requested$")
    public void Property_with_code_exists_with_etag(String code) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(code);
        assertThat(property, is(notNullValue()));
        propertySteps.getProperty(property.getPropertyId());
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
        propertySteps.getListOfPropertiesWith(limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of properties is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void listOfPropertiesIsGotWithLimitAndCursorAndFilterAndSortAndSort_descByUser(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                          @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                          @Transform(NullEmptyStringConverter.class) String filter,
                                                                                          @Transform(NullEmptyStringConverter.class) String sort,
                                                                                          @Transform(NullEmptyStringConverter.class) String sortDesc,
                                                                                          String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        propertySteps.getListOfPropertiesByUserWith(user.getUserId(), limit, cursor, filter, sort, sortDesc);
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
        PropertyDto property = propertySteps.getPropertyByCodeInternal(code);
        assertThat(property, is(notNullValue()));

        propertySteps.deleteProperty(property.getPropertyId());
    }

    @When("^Property with code \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void propertyWithCodeIsDeletedByUser(String propertyCode, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.deletePropertyByUser(ids.get(USER_ID), ids.get(PROPERTY_ID));
    }

    @When("^Nonexistent property id is deleted$")
    public void Nonexistent_property_id_is_deleted() throws Throwable {
        propertySteps.deleteProperty("nonexistent_id");
    }

    @When("^User with username \"([^\"]*)\" is added to property with code \"([^\"]*)\"$")
    public void User_with_username_is_added_to_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto u = usersSteps.getUserByUsername(username);
        propertySteps.userIsAddedToProperty(u, propertyCode);
    }

    @When("^User with username \"([^\"]*)\" is removed from property with code \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_property_with_code(String username, String propertyCode) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        propertySteps.userIsDeletedFromProperty(user.getUserId(), propertyCode);
    }

    @When("^User with username \"([^\"]*)\" is removed from property with code \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userWithUsernameIsRemovedFromPropertyWithCodeByUser(String username, String propertyCode, String performerName) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        propertySteps.userIsDeletedFromPropertyByUser(performer.getUserId(), ids.get(USER_ID), ids.get(PROPERTY_ID));
    }

    @When("^Nonexistent user is removed from property with code \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_property_with_code(String propertyCode) throws Throwable {
        propertySteps.userIsDeletedFromProperty("nonexistent", propertyCode);
    }

    @When("^I query list of users for nonexistent property$")
    public void i_query_list_of_users_for_nonexistent_property() throws Throwable {
        propertySteps.listOfUsersIsGotWith(BasicSteps.NON_EXISTENT_ID, null, null, null, null, null);
}

    @When("^List of users for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                    @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        propertySteps.listOfUsersIsGotWith(property.getPropertyId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all users for property with code \"([^\"]*)\" is got by user \"([^\"]*)\"$")
    public void listOfAllUsersForPropertyWithCodeIsGotByUser(String propertyCode, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.listOfUsersIsGotByUserWith(ids.get(USER_ID), ids.get(PROPERTY_ID), null, null, null, null, null);
    }

    @When("^List of customers for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        propertySteps.listOfCustomersIsGot(property.getPropertyId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all customers for property with code \"([^\"]*)\" is got by user \"([^\"]*)\"$")
    public void listOfAllCustomersForPropertyWithCodeIsGotByUser(String propertyCode, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.listOfCustomersIsGotByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), null, null, null, null, null);
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
        CustomerDto[] allCustomers = customerSteps.listOfCustomersIsGotWith(null, null, null, null, null).as(CustomerDto[].class);
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
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        propertySteps.propertyPropertySetIsGot(property.getPropertyId(), propertySet.getPropertySetId());
    }

    @When("^Property set with name \"([^\"]*)\" for property with code \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void PropertySetWithNameForPropertyWithCodeIsGotByUser(String propertySetName, String propertyCode, String username) {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        propertySteps.propertyPropertySetIsGotByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), propertySet.getPropertySetId());
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

    @When("^List of all property sets is got for property with code \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void listOfAllPropertySetsIsGotForPropertyWithIdByUser(String propertyCode, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.listOfPropertiesPropertySetsIsGotByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), null, null, null, null, null);
    }

    @When("^Property with code \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void propertyWithCodeIsRequestedByUser(String propertyCode, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

//        Sets the session response
        propertySteps.getPropertyByUser(user.getUserId(), property.getPropertyId());
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

    @When("^Add ttiId to booking.com id \"([^\"]*)\" mapping to property with code \"([^\"]*)\"$")
    public void addTtiIdAndBookingComIdMappingToPropertyWithCode(Integer bookingComId, String propertyCode) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        TtiCrossreferenceDto ttiCrossreference = new TtiCrossreferenceDto();
        ttiCrossreference.setCode(bookingComId);
        propertySteps.assignTtiToProperty(property.getPropertyId(), ttiCrossreference);
    }

    @When("^Add ttiId to booking.com id \"([^\"]*)\" mapping to property with code \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void addTtiIdToBookingComIdMappingToPropertyWithCodeByUser(Integer bookingComId, String propertyCode, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        TtiCrossreferenceDto ttiCrossreference = new TtiCrossreferenceDto();
        ttiCrossreference.setCode(bookingComId);

        propertySteps.assignTtiToPropertyByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), ttiCrossreference);
    }

    @When("^Add ttiId to booking.com id mapping to property with code \"([^\"]*)\" without booking.com code$")
    public void addTtiIdToBookingComIdMappingToPropertyWithCodeWithoutCode(String propertyCode) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));

        TtiCrossreferenceDto ttiCrossreference = new TtiCrossreferenceDto();
        propertySteps.assignTtiToProperty(property.getPropertyId(), ttiCrossreference);
    }

    @Given("^Property \"([^\"]*)\" is created with address for user \"([^\"]*)\" and customer with id \"([^\"]*)\"$")
    public void propertyIsCreatedWithAddress(String propertyName, String username, String customerId, List<AddressDto> addresses) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is((notNullValue())));

        propertySteps.createDefaultMinimalPropertyWithAddress(propertyName, user.getUserId(), customerId, addresses.get(0));
    }

    @When("^Property with code \"([^\"]*)\" is updated with data$")
    public void propertyIsUpdatedWithData(String propertyCode, List<PropertyUpdateDto> propertyUpdates) throws Throwable {
        PropertyDto originalProperty = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(originalProperty, is(notNullValue()));

        propertySteps.updateProperty(originalProperty.getPropertyId(), propertyUpdates.get(0));
    }

    @When("^Property with code \"([^\"]*)\" is updated with data by user \"([^\"]*)\"$")
    public void propertyWithCodeIsUpdatedWithDataByUser(String propertyCode, String username, List<PropertyUpdateDto> propertyUpdates) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.updatePropertyByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), propertyUpdates.get(0));
    }

    @When("^Property \"([^\"]*)\" is requested$")
    public void propertyIsRequested(String propertyName) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByName(propertyName);
        assertThat(property, is(notNullValue()));
//        Sets session response
        propertySteps.getProperty(property.getPropertyId());
    }

    @When("^Property \"([^\"]*)\" is updated with address$")
    public void propertyIsUpdatedWithAddressForUserAndCustomerWithId(String propertyName, List<AddressUpdateDto> addresses) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByName(propertyName);
        assertThat(property, is((notNullValue())));

        propertySteps.updatePropertyAddress(property.getPropertyId(), addresses.get(0));
    }

    @When("^Relation between property with code \"([^\"]*)\" and property set \"([^\"]*)\" is updated by user \"([^\"]*)\" with$")
    public void relationBetweenPropertyWithCodeAndPropertySetWithNameIsUpdatedByUser(String propertyCode, String propertySetName, String username, List<PropertySetPropertyRelationshipUpdateDto> relationshitpUpdates) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        propertySteps.updatePropertyPropertySetRelationshipByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), propertySet.getPropertySetId(), relationshitpUpdates.get(0));
    }

    @When("^Relation between property with code \"([^\"]*)\" and property set \"([^\"]*)\" is updated with empty body$")
    public void relationBetweenPropertyWithCodeAndPropertySetWithNameIsUpdatedWithEmptyBody(String propertyCode, String propertySetName) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property,is(notNullValue()));
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        PropertySetPropertyRelationshipUpdateDto relationshipUpdate = new PropertySetPropertyRelationshipUpdateDto();
        propertySteps.updatePropertyPropertySetRelationship(property.getPropertyId(), propertySet.getPropertySetId(), relationshipUpdate);
    }

    @When("^Relation between property with code \"([^\"]*)\" and property set \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void relationBetweenPropertyWithCodeAndPropertySetWithNameIsDeletedByUser(String propertyCode, String propertySetName, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        PropertySetDto propertySet = propertySetSteps.getPropertySetByName(propertySetName);
        assertThat(propertySet, is(notNullValue()));

        propertySteps.propertySetIsDeletedFromPropertyByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), propertySet.getPropertySetId());
    }


    @When("^Property customer relationship for property with code \"([^\"]*)\" and customer with id \"([^\"]*)\" is updated by user \"([^\"]*)\" with$")
    public void propertyCustomerRelationshipForPropertyWithCodeAndCustomerWithIdIsUpdatedByUserWith(String propertyCode, String customerId, String username,
                                                                                                    List<CustomerPropertyRelationshipUpdateDto> relationshipUpdates) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.updatePropertyCustomerRelationshipByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), customerId, relationshipUpdates.get(0));
    }

    @When("^Property customer relationship for property with code \"([^\"]*)\" and customer with id \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void propertyCustomerRelationshipForPropertyWithCodeAndCustomerWithIdIsDeletedByUser(String propertyCode, String customerId, String username) throws Throwable {
        Map<String, String> ids =  getValidUserPropertyIdsFromNameAndCode(username, propertyCode);

        propertySteps.deletePropertyCustomerRelationshipByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), customerId);
    }
}
