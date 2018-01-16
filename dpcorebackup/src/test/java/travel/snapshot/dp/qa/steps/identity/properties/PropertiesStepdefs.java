package travel.snapshot.dp.qa.cucumber.steps.identity.properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.bodyContainsEntityWith;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.cucumber.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.cucumber.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    @Steps
    private ApplicationVersionsSteps applicationVersionSteps;

    // Help methods

    public Map<String, UUID> getValidUserPropertyIdsFromNameAndCode(String username, String propertyCode) {
        UUID userId = usersSteps.resolveUserId(username);
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        Map<String, UUID> userPropertyIds = new HashMap<>();
        userPropertyIds.put(USER_ID, userId);
        userPropertyIds.put(PROPERTY_ID, propertyId);
        return userPropertyIds;
    }
//    End of help methods section

    // --- given ---

    @Given("^The following properties exist with random address and billing address(?: for user \"([^\"]*)\")?$")
    public void theFollowingPropertiesExistWithRandomAddressAndBillingAddressForUser(String userName, List<PropertyDto> properties) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        propertySteps.followingPropertiesExist(properties, userId);
    }

    @Given("^Relation between user \"([^\"]*)\" and property(?: with code)? \"([^\"]*)\" exists(?: with is_active \"([^\"]*)\")?$")
    public void Relation_between_user_with_username_and_property_with_code_exists(String username, String propertyCode, String isActiveString) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        Boolean isActive = ((isActiveString == null) ? true : Boolean.valueOf(isActiveString));
        propertySteps.relationExistsBetweenUserAndProperty(ids.get(USER_ID), ids.get(PROPERTY_ID), isActive);
    }

    @Given("^Relation between user \"([^\"]*)\" and default property exists$")
    public void Relation_between_user_and_default_property_exists(String username) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        propertySteps.relationExistsBetweenUserAndProperty(userId, DEFAULT_PROPERTY_ID, true);
    }
    // --- when ---

    @When("^User \"([^\"]*)\" is added to property with code \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userIsAddedToPropertyWithCodeByUser(String username, String propertyCode, String performerName, String isActiveString, String applicationVersionName) throws Throwable {
        Boolean isActive = ((isActiveString == null) ? true : Boolean.valueOf(isActiveString));
        UUID performerId = ((performerName == null) ? DEFAULT_SNAPSHOT_USER_ID : usersSteps.resolveUserId(performerName));
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.addUserToPropertyByUserForApp(performerId, applicationVersionId, ids.get(USER_ID), ids.get(PROPERTY_ID), isActive);
    }

    @When("^Nonexistent property is got$")
    public void Nonexistent_property_id_sent() throws Throwable {
        propertySteps.getProperty(NON_EXISTENT_ID);
    }


    @When("^List of properties is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfPropertiesIsGotWithLimitAndCursorAndFilterAndSortAndSort_descByUser(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                          @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                          @Transform(NullEmptyStringConverter.class) String filter,
                                                                                          @Transform(NullEmptyStringConverter.class) String sort,
                                                                                          @Transform(NullEmptyStringConverter.class) String sortDesc,
                                                                                          String username, String applicationVersionName) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.getEntitiesByUserForApp(userId, applicationVersionId, null, limit, cursor, filter, sort, sortDesc, null);
    }

    @Given("^The following property is created with random address and billing address(?: for user \"([^\"]*)\")?$")
    public void theFollowingPropertyIsCreatedWithRandomAddressAndBillingAddressForUser(String userName, List<PropertyDto> properties) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        propertySteps.followingPropertyIsCreatedByUser(userId, properties.get(0));
    }

    @When("^The user \"([^\"]*)\" creates the following property$")
    public void theUserCreatesTheFollowingProperty(String userName, List<PropertyDto> properties) throws Throwable {
        UUID userId = usersSteps.resolveUserId(userName);
        propertySteps.followingPropertyIsCreatedByUser(userId, properties.get(0));
    }

    @When("^A property for customer \"([^\"]*)\" from country \"([^\"]*)\" region \"([^\"]*)\" code \"([^\"]*)\" email \"([^\"]*)\" is created by user \"([^\"]*)\"$")
    public void aPropertyForCustomerFromCountryRegionCodeEmailIsCreatedWithUserId(UUID customerId, String country, String region, String code, String email, String userName) throws Throwable {
        AddressDto address = new AddressDto();
        PropertyDto property = new PropertyDto();
        address.setLine1("someAddress");
        address.setCity("someCity");
        address.setZipCode("1234");
        address.setCountryCode(country);
        address.setRegionCode(region);
        property.setCustomerId(customerId);
        property.setName("someProperty");
        property.setCode(code);
        property.setEmail(email);
        property.setIsDemo(true);
        property.setTimezone("GMT");
        propertySteps.followingPropertyIsCreatedWithAddressByUser(property, address, usersSteps.resolveUserId(userName));
    }

    @When("^Property with code \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void propertyWithCodeIsDeletedByUser(String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.deletePropertyByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID));
    }

    @When("^Nonexistent property id is deleted$")
    public void Nonexistent_property_id_is_deleted() throws Throwable {
        propertySteps.deleteProperty(NON_EXISTENT_ID);
    }

    @When("^User \"([^\"]*)\" is removed from property with code \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userWithUsernameIsRemovedFromPropertyWithCodeByUser(String username, String propertyCode, String performerName, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID performerId = usersSteps.resolveUserId(performerName);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.userIsDeletedFromPropertyByUserForApp(performerId, applicationVersionId, ids.get(USER_ID), ids.get(PROPERTY_ID));
    }

    @When("^Nonexistent user is removed from property with code \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_property_with_code(String propertyCode) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        propertySteps.userIsDeletedFromProperty(NON_EXISTENT_ID, propertyId);
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
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        propertySteps.listOfUsersIsGotWith(propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all users for property with code \"([^\"]*)\" is got(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllUsersForPropertyWithCodeIsGotByUser(String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.listOfPropertyUsersIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), null, null, null, null, null);
    }

    @When("^List of customers for property with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_for_property_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String propertyCode,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                        @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        propertySteps.listOfCustomersIsGot(propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all customers for property with code \"([^\"]*)\" is got(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllCustomersForPropertyWithCodeIsGotByUser(String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.listOfCustomersIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), null, null, null, null, null);
    }

    // --- then ---

    @Then("^Body contains property with attribute \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute(String atributeName) throws Throwable {
        bodyContainsEntityWith(atributeName);
    }

    @Then("^Body contains property with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute_value(String atributeName, String value) throws Throwable {
        propertySteps.bodyContainsEntityWith(atributeName, value);
    }

    @Then("^Body does not contain property with attribute \"([^\"]*)\"$")
    public void Body_does_not_contain_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyDoesntContainEntityWith(atributeName);
    }

    @Then("^There are (\\d+) properties returned$")
    public void There_are_properties_returned(int count) throws Throwable {
        numberOfEntitiesInResponse(PropertyDto.class, count);
    }

    @Then("^All customers are customers of property with code \"([^\"]*)\"$")
    public void each_customer_is_a_customer_of_property_with_code(String propertyCode) throws Throwable {
        UUID propertyID = propertySteps.resolvePropertyId(propertyCode);
        CustomerDto[] allCustomers = customerSteps.listOfCustomersIsGotWith(null, null, null, null, null).as(CustomerDto[].class);
        for (CustomerDto customer : allCustomers) {
            if(!customer.getId().equals("11111111-0000-4000-a000-555555555555")) {
//                We don't want to check default customer's properties
                customerSteps.listOfCustomerPropertiesIsGotWith(customer.getId(), null, null, null, null, null).then().body("property_id", hasItem(propertyID));
            }
        }
    }

    // --- and ---

    @Then("^User \"([^\"]*)\" isn't there for property with code \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_property_with_code(String username, String propertyCode) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        propertySteps.userDoesntExistForProperty(userId, propertyCode);
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
        UUID propertyId = propertySteps.resolvePropertyId(code);
        propertySteps.setPropertyIsActive(propertyId, true);
    }

    @When("^Property with code \"([^\"]*)\" is inactivated$")
    public void property_With_Code_Is_Inactivated(String code) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(code);
        propertySteps.setPropertyIsActive(propertyId, false);
    }

    @Then("^Property with code \"([^\"]*)\" is active$")
    public void Property_with_code_is_active(String code) throws Throwable {
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

    @When("^Property set with name \"([^\"]*)\" for property with code \"([^\"]*)\" is got$")
    public void Property_set_with_name_for_property_with_code_is_got(String propertySetName, String propertyCode) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        propertySteps.propertyPropertySetIsGot(propertyId, propertySetId);
    }

    @When("^Property set with name \"([^\"]*)\" for property with code \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void PropertySetWithNameForPropertyWithCodeIsGotByUser(String propertySetName, String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.propertyPropertySetIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), propertySetId);
    }

    @When("^List of property sets is got for property with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_is_got_for_property_with_id_and_limit_and_cursor_and_filter_and_sort_and_sort_desc(UUID propertyId,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                         @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        propertySteps.listOfPropertiesPropertySetsIsGot(propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all property sets is got for property with code \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllPropertySetsIsGotForPropertyWithIdByUser(String propertyCode, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.listOfPropertiesPropertySetsIsGotByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), null, null, null, null, null);
    }

    @When("^Property with code \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void propertyWithCodeIsRequestedByUser(String propertyCode, String username, String applicationVersionName) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
//        Sets the session response
        propertySteps.getPropertyByUserForApp(userId, applicationVersionId, propertyId);
    }

    @When("^Set is active to \"([^\"]*)\" for relation between user \"([^\"]*)\" and property with code \"([^\"]*)\"$")
    public void isActiveSetToForRelationBetweenUserAndPropertyWithCode(Boolean isActive, String username, String propertyCode) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UserPropertyRelationshipUpdateDto userPropertyRelationship = new UserPropertyRelationshipUpdateDto();
        userPropertyRelationship.setIsActive(isActive);

        usersSteps.updateUserPropertyRelationship(ids.get(USER_ID), ids.get(PROPERTY_ID), userPropertyRelationship);
    }


    @And("^Check is active attribute is \"([^\"]*)\" for relation between user \"([^\"]*)\" and property with code \"([^\"]*)\"$")
    public void isActiveAttributeIsForRelationBetweenUserAndPropertyWithCode(Boolean isActive, String username, String propertyCode) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UserPropertyRelationshipDto userPropertyRelation = propertySteps.getUserForProperty(ids.get(PROPERTY_ID), ids.get(USER_ID));
        assertThat(userPropertyRelation, is(notNullValue()));
        assertThat(userPropertyRelation.getIsActive(), is(isActive));
    }

//    @When("^Add ttiId to booking.com id \"([^\"]*)\" mapping to property with code \"([^\"]*)\"$")
//    public void addTtiIdAndBookingComIdMappingToPropertyWithCode(Integer bookingComId, String propertyCode) throws Throwable {
//        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
//        TtiCrossreferenceDto ttiCrossreference = new TtiCrossreferenceDto();
//        ttiCrossreference.setCode(bookingComId);
//        propertySteps.assignTtiToProperty(propertyId, ttiCrossreference);
//    }

//    @When("^Add ttiId to booking.com id \"([^\"]*)\" mapping to property with code \"([^\"]*)\" by user \"([^\"]*)\"$")
//    public void addTtiIdToBookingComIdMappingToPropertyWithCodeByUser(Integer bookingComId, String propertyCode, String username) throws Throwable {
//        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
//        TtiCrossreferenceDto ttiCrossreference = new TtiCrossreferenceDto();
//        ttiCrossreference.setCode(bookingComId);
//
//        propertySteps.assignTtiToPropertyByUser(ids.get(USER_ID), ids.get(PROPERTY_ID), ttiCrossreference);
//    }
//
//    @When("^Add ttiId to booking.com id mapping to property with code \"([^\"]*)\" without booking.com code$")
//    public void addTtiIdToBookingComIdMappingToPropertyWithCodeWithoutCode(String propertyCode) throws Throwable {
//        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
//
//        TtiCrossreferenceDto ttiCrossreference = new TtiCrossreferenceDto();
//        propertySteps.assignTtiToProperty(propertyId, ttiCrossreference);
//    }

    @Given("^Property \"([^\"]*)\" is created with address for user \"([^\"]*)\" and customer with id \"([^\"]*)\"$")
    public void propertyIsCreatedWithAddress(String propertyName, String username, UUID customerId, List<AddressDto> addresses) throws Throwable {
        UUID userId = usersSteps.resolveUserId(username);
        propertySteps.createDefaultMinimalPropertyWithAddressByUser(propertyName, userId, customerId, addresses.get(0));
    }

    @When("^Property with code \"([^\"]*)\" is updated with data(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void propertyWithCodeIsUpdatedWithDataByUser(String propertyCode, String username, String applicationVersionName, List<PropertyUpdateDto> propertyUpdates) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.updatePropertyByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), propertyUpdates.get(0));
    }

    @When("^Property \"([^\"]*)\" is requested$")
    public void propertyIsRequested(String propertyName) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyName);
//        Sets session response
        propertySteps.getProperty(propertyId);
    }

    @When("^Property \"([^\"]*)\" is updated with address$")
    public void propertyIsUpdatedWithAddressForUserAndCustomerWithId(String propertyName, List<AddressDto> addresses) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyName);

        propertySteps.updatePropertyAddress(propertyId, addresses.get(0));
    }

    @When("^Relation between property with code \"([^\"]*)\" and property set \"([^\"]*)\" is updated(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")? with$")
    public void relationBetweenPropertyWithCodeAndPropertySetWithNameIsUpdatedByUser(String propertyCode, String propertySetName, String username, String applicationVersionName, List<PropertySetPropertyRelationshipUpdateDto> relationshitpUpdates) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.updatePropertyPropertySetRelationshipByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), propertySetId, relationshitpUpdates.get(0));
    }

    @When("^Relation between property with code \"([^\"]*)\" and property set \"([^\"]*)\" is updated with empty body$")
    public void relationBetweenPropertyWithCodeAndPropertySetWithNameIsUpdatedWithEmptyBody(String propertyCode, String propertySetName) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        PropertySetPropertyRelationshipUpdateDto relationshipUpdate = new PropertySetPropertyRelationshipUpdateDto();
        propertySteps.updatePropertyPropertySetRelationship(propertyId, propertySetId, relationshipUpdate);
    }

    @When("^Relation between property(?: with code)? \"([^\"]*)\" and property set \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenPropertyWithCodeAndPropertySetWithNameIsDeletedByUser(String propertyCode, String propertySetName, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID propertySetId = propertySetSteps.resolvePropertySetId(propertySetName);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.propertySetIsDeletedFromPropertyByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), propertySetId);
    }


    @When("^Property customer relationship for property with code \"([^\"]*)\" and customer with id \"([^\"]*)\" is updated(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")? with$")
    public void propertyCustomerRelationshipForPropertyWithCodeAndCustomerWithIdIsUpdatedByUserWith(String propertyCode, UUID customerId, String username, String applicationVersionName,
                                                                                                    List<CustomerPropertyRelationshipUpdateDto> relationshipUpdates) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.updatePropertyCustomerRelationshipByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), customerId, relationshipUpdates.get(0));
    }

    @When("^Property customer relationship for property with code \"([^\"]*)\" and customer with id \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void propertyCustomerRelationshipForPropertyWithCodeAndCustomerWithIdIsDeletedByUser(String propertyCode, UUID customerId, String username, String applicationVersionName) throws Throwable {
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);

        propertySteps.deletePropertyCustomerRelationshipByUserForApp(ids.get(USER_ID), applicationVersionId, ids.get(PROPERTY_ID), customerId);
    }

    @When("^Relation between user \"([^\"]*)\" and property(?: with code)? \"([^\"]*)\" is (in|de)?activated(?: by user \"([^\"]*)\")?$")
    public void relationBetweenUserAndPropertyWithCodeIsActivated(String userName, String propertyCode, String negation, String performerName) throws Throwable {
        Boolean isActive = true;
        if (negation != null) {
            isActive = false;
        }
        UUID performerId = ((performerName == null) ? DEFAULT_SNAPSHOT_USER_ID : usersSteps.resolveUserId(performerName));
        UUID userId = usersSteps.resolveUserId(userName);
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UserPropertyRelationshipDto relation = propertySteps.getUserForProperty(propertyId, userId);
        relation.setIsActive(isActive);
        usersSteps.updateUserPropertyRelationshipByUser(performerId, userId, propertyId, relation);
    }

    @Given("^Relation between property(?: with code)? \"([^\"]*)\" and user \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application \"([^\"]*)\")?$")
    public void relationBetweenPropertyWithCodeAndUserIsDeleted(String propertyCode, String username, String performerName, String applicationVersionName) throws Throwable {
        UUID performerId = ((performerName == null) ? DEFAULT_SNAPSHOT_USER_ID : usersSteps.resolveUserId(performerName));
        Map<String, UUID> ids = getValidUserPropertyIdsFromNameAndCode(username, propertyCode);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.deletePropertyUserRelationshipByUserForApp(performerId, applicationVersionId, ids.get(PROPERTY_ID), ids.get(USER_ID));
    }

    @When("^Relation between property(?: with code)? \"([^\"]*)\" and customer with id \"([^\"]*)\" is (in|de)?activated$")
    public void relationBetweenPropertyWithCodeAndCustomerWithIdIsActivated(String propertyCode, UUID customerId, String negation) throws Throwable {
        Boolean isActive = true;
        if (negation != null) {
            isActive = false;
        }
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        CustomerPropertyRelationshipPartialUpdateDto relation = new CustomerPropertyRelationshipPartialDto();
        relation.setIsActive(isActive);
        customerSteps.updateCustomerPropertyRelationship(propertyId, customerId, relation);
        Response response = customerSteps.getSessionResponse();
        assert (response.statusCode() == HttpStatus.SC_NO_CONTENT);
    }

    @When("^Relation between property(?: with code)? \"([^\"]*)\" and customer with id \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenPropertyAndCustomerWithIdIsRequestedByUser(String propertyCode, UUID customerId, String userName, String applicationVersionName) throws Throwable {
        UUID propertyId = propertySteps.resolvePropertyId(propertyCode);
        UUID userId = usersSteps.resolveUserId(userName);
        UUID applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        propertySteps.getPropertyCustomerRelationshipsByUserForApp(userId, applicationVersionId, propertyId, customerId);
    }
}