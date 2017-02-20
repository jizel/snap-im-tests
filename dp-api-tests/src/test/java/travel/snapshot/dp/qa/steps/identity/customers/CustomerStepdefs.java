package travel.snapshot.dp.qa.steps.identity.customers;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.REQUESTOR_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.TARGET_ID;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.AddressUpdateDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.CustomerUtils;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.review.ReviewMultipropertyCustomerSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;
import travel.snapshot.dp.qa.steps.BasicStepDefs;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class CustomerStepdefs {

    public static final String DEFAULT_CUSTOMER_EMAIL = "customer1@snapshot.travel";
    public static final Boolean DEFAULT_CUSTOMER_IS_DEMO = true;
    public static final String DEFAULT_CUSTOMER_TIMEZONE = "Europe/Prague";

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private ReviewMultipropertyCustomerSteps reviewMultipropertyCustomerSteps;

    @Steps
    private BasicSteps basicSteps;

    // ---------------------------- GIVEN ------------------------------

    @Given("^All users are removed for customers with ids: (.*)$")
    public void All_users_are_removed_for_customers_with_ids_default(List<String> customerIds) throws Throwable {
        customerSteps.removeAllUsersFromCustomers(customerIds);
    }

    @Given("^Set access token from session for customer steps defs$")
    public void setAccessTokenFromSessionForCustomerStepsDefs() throws Throwable {
        customerSteps.setAccessTokenParamFromSession();
        propertySteps.setAccessTokenParamFromSession();
        usersSteps.setAccessTokenParamFromSession();
        reviewMultipropertyCustomerSteps.setAccessTokenParamFromSession();
    }

    @Given("^The following customers exist with random address$")
    public void The_following_tenants_exist(List<CustomerCreateDto> customers) throws Throwable {
        customerSteps.followingCustomersExistWithRandomAddress(customers);
    }

    @Given("^Relation between property with code \"([^\"]*)\" and customer with id \"([^\"]*)\" exists with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void Relation_between_property_with_code_and_customer_with_code_exists_with_type_from_to(String propertyCode,
                                                                                                    String customerId, String type, String validFrom, String validTo) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        customerSteps.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(property, customerId, type, validFrom, validTo);
    }

    @Given("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" exists with isPrimary \"([^\"]*)\"$")
    public void Relation_between_user_with_username_and_customer_with_id_exists_with_isPrimary(String username,
                                                                                                 String customerId, Boolean isPrimary) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        customerSteps.relationExistsBetweenUserAndCustomerWithPrimary(userId, customerId, isPrimary);
    }

    // ---------------------------- WHEN ------------------------------

    @When("^A customer with following country \"([^\"]*)\", region \"([^\"]*)\", vatId \"([^\"]*)\" is created$")
    public void aCustomerWithFollowingCountryRegionVatIdIsCreated(@Transform(NullEmptyStringConverter.class) String country,
                                                                  @Transform(NullEmptyStringConverter.class) String region,
                                                                  @Transform(NullEmptyStringConverter.class) String vatId) throws Throwable {
        AddressDto addressForCustomer = AddressUtils.createRandomAddress(10, 10, 5, country, region);
        CustomerCreateDto customer = CustomerUtils.createRandomCustomer(vatId);
        customerSteps.followingCustomerIsCreatedWithAddress(customer, addressForCustomer);
    }

    @When("^Customer is created with random address$")
    public void customer_is_created(List<CustomerCreateDto> customers) throws Throwable {
        customerSteps.followingCustomerIsCreatedWithRandomAddress(customers.get(0));
    }

    @When("^File \"([^\"]*)\" is used for \"([^\"]*)\"$")
    public void customer_file_is_used_for_method(String fileName, String method) throws Throwable {
        switch (method) {
            case "POST": {
                customerSteps.fileIsUsedForCreation("/messages/identity/customers/" + fileName);
                break;
            }
            default:
                break;
        }
    }

    @When("^List of customers is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void List_of_customers_is_got_with_limit_and_cursor_and_filter_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            String username) throws Throwable {

        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        customerSteps.listOfCustomersIsGotByUserWith(user.getUserId(), limit, cursor, filter, sort, sortDesc);
    }

    @When("^Customer with id \"([^\"]*)\" is updated with data by user \"([^\"]*)\"$")
    public void customerWithIdIsUpdatedWithData(String customerId, String username, List<CustomerUpdateDto> customersData) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        customerSteps.updateCustomerByUser(customerId, user.getUserId(), customersData.get(0));
    }

    @When("^Property with code \"([^\"]*)\" is added to customer with id \"([^\"]*)\" with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void Property_with_code_is_added_to_customer_with_code_with_type_from_to(String propertyCode,
                                                                                    String customerId, @Transform(NullEmptyStringConverter.class) String type,
                                                                                    @Transform(NullEmptyStringConverter.class) String dateFrom,
                                                                                    @Transform(NullEmptyStringConverter.class) String dateTo) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        if (property == null) {
            customerSteps.propertyIsAddedToCustomerWithTypeFromTo(BasicStepDefs.NONEXISTENT_ID, customerId, type,
                    dateFrom, dateTo);
        } else {
            customerSteps.propertyIsAddedToCustomerWithTypeFromTo(property.getPropertyId(), customerId, type, dateFrom,
                    dateTo);
        }

    }

    @When("^Property with code \"([^\"]*)\" is added to customer with id \"([^\"]*)\" with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void propertyWithCodeIsAddedToCustomerWithIdWithTypeFromToByUser(String propertyCode, String customerId, @Transform(NullEmptyStringConverter.class) String type,
                                                                            @Transform(NullEmptyStringConverter.class) String dateFrom,
                                                                            @Transform(NullEmptyStringConverter.class) String dateTo,
                                                                            String username) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        Response response = customerSteps.addPropertyToCustomerWithTypeFromToByUser(user.getUserId(), property.getPropertyId(), customerId, type, dateFrom, dateTo);
        customerSteps.setSessionResponse(response);
    }

    @When("^Property with code \"([^\"]*)\" from customer with id \"([^\"]*)\" is got with type \"([^\"]*)\"$")
    public void Property_with_code_from_customer_with_code_is_got_with_type(String propertyCode, String customerId,
                                                                            String type) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithType(property, customerId, type);
    }

    @When("^Property with code \"([^\"]*)\" from customer with id \"([^\"]*)\" is got by user \"([^\"]*)\"$")
    public void propertyWithCodeFromCustomerWithIdIsGotByUser(String propertyCode, String customerId, String username) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        customerSteps.propertyIsgotForCustomerByUser(user.getUserId(), property.getPropertyId(), customerId);
    }

    @When("^Nonexistent customerPropety id is got for customer with id \"([^\"]*)\"$")
    public void Nonexistent_customerPropety_id_is_got_for_customer_with_code(String customerId) throws Throwable {
        customerSteps.getCustomerPropertyWithRelationshipId(customerId, "nonexistent");
    }

    @When("^List of customerProperties is got for customer with id \"([^\"]*)\" with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customerProperties_is_got_for_customer_with_id_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerPropertiesIsGotWith(customerId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all customer properties is got for customer with id \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void listOfAllCustomerPropertiesIsGotForCustomerWithIdByUser(String customerId, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        customerSteps.listOfCustomerPropertiesIsGotByUser(user.getUserId(), customerId, null, null, null, null, null);


    }

    @When("^User \"([^\"]*)\" is added to customer with id \"([^\"]*)\" with isPrimary \"([^\"]*)\"$")
    public void User_with_username_is_added_to_customer_with_id_with_isPrimary(String username, String customerId,
                                                                                 Boolean isPrimary) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        customerSteps.userIsAddedToCustomerWithIsPrimary(user, customerId, isPrimary);
    }


    @When("^User \"([^\"]*)\" is added to customer with id \"([^\"]*)\" with isPrimary \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userIsAddedToCustomerWithIdWithIsPrimaryByUser(String username, String customerId,
                                                               Boolean isPrimary, String performerName) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        Response response = customerSteps.addUserToCustomerWithIsPrimaryByUser(performer.getUserId(), user.getUserId(), customerId, isPrimary);
        customerSteps.setSessionResponse(response);
    }

    @When("^User \"([^\"]*)\" is removed from customer with id \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_customer_with_id(String username, String customerId)
            throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        customerSteps.userIsDeletedFromCustomer(userId, customerId);
    }


    @When("^User \"([^\"]*)\" is removed from customer with id \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void userIsRemovedFromCustomerWithIdByUser(String username, String customerId, String performerName) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        customerSteps.userIsDeletedFromCustomerByUser(performer.getUserId(), user.getUserId(), customerId);
    }

    @When("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" is updated with isPrimary \"([^\"]*)\"$")
    public void relationBetweenUserAndCustomerWithIdIsUpdatedWithIsPrimary(String username, String customerId, Boolean isPrimary) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate = new UserCustomerRelationshipUpdateDto();
        userCustomerRelationshipUpdate.setIsPrimary(isPrimary);
        customerSteps.updateUserCustomerRelationship(user.getUserId(), customerId, userCustomerRelationshipUpdate);
    }


    @When("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" is updated with isPrimary \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void relationBetweenUserAndCustomerWithIdIsUpdatedWithIsPrimaryByUser(String username, String customerId, Boolean isPrimary, String performerName) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        UserDto performer = usersSteps.getUserByUsername(performerName);
        assertThat(performer, is(notNullValue()));

        UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate = new UserCustomerRelationshipUpdateDto();
        userCustomerRelationshipUpdate.setIsPrimary(isPrimary);
        customerSteps.updateUserCustomerRelationshipByUser(performer.getUserId(), user.getUserId(), customerId, userCustomerRelationshipUpdate);
    }

    @When("^Property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" with type \"([^\"]*)\" is updating field \"([^\"]*)\" to value \"([^\"]*)\"$")
    public void Property_with_code_for_customer_with_code_with_type_is_updating_field_to_value(String propertyCode,
                                                                                               String customerId, String type, String fieldName, String value) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsUpdateForCustomerWithType(property, customerId, type, fieldName, value);
    }

    @When("^Property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" is updated by user \"([^\"]*)\" with$")
    public void propertyWithCodeForCustomerWithIdFieldIsUpdatedToValueString(String propertyCode,String customerId,
                                                                             String username, List<CustomerPropertyRelationshipUpdateDto> relationshipUpdates) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        assertThat(property, is(notNullValue()));
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        customerSteps.updateCustomerPropertyRelationshipByUser(user.getUserId(), property.getPropertyId(), customerId, relationshipUpdates.get(0));


    }

    @When("^Property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" with type \"([^\"]*)\" is updating field \"([^\"]*)\" to value \"([^\"]*)\" with invalid etag$")
    public void Property_with_code_for_customer_with_code_with_type_is_updating_field_to_value_with_invalid_etag(
            String propertyCode, String customerId, String type, String fieldName, String value) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsUpdateForCustomerWithTypeWithInvalidEtag(property, customerId, type, fieldName, value);
    }

    @When("^Property with code \"([^\"]*)\" from customer with id \"([^\"]*)\" is got with type \"([^\"]*)\" with etag$")
    public void Property_with_code_from_customer_with_code_is_got_with_type_with_etag(String propertyCode,
                                                                                      String customerId, String type) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithTypeWithEtag(p, customerId, type);
    }

    @When("^Property with code \"([^\"]*)\" from customer with id \"([^\"]*)\" is got with type \"([^\"]*)\" for etag, updated and got with previous etag$")
    public void Property_with_code_from_customer_with_code_is_got_with_type_for_etag_updated_and_got_with_previous_etag(
            String propertyCode, String customerId, String type) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithTypeWithEtagAfterUpdate(p, customerId, type);
    }

    @When("^Customer with id \"([^\"]*)\", update address with following data$")
    public void customerWithIdUpdateAddressWithFollowingData(String customerId, List<AddressUpdateDto> addresses) throws Throwable {
        customerSteps.updateCustomerAddress(customerId, addresses.get(0));
    }

    @When("^Relation between user \"([^\"]*)\" and customer \"([^\"]*)\" is deleted$")
    public void relationBetweenUserWithUsernameAndCustomerIsDeleted(String username, String customerId) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        customerSteps.relationBetweenUserAndCustomerIsDeleted(user.getUserId(), customerId);
    }

    @When("^Customer with customerId \"([^\"]*)\" is got$")
    public void customerWithCustomerIdIsGot(String customerId) throws Throwable {
        customerSteps.customerWithIdIsGot(customerId);
    }

    @When("^Customer with customerId \"([^\"]*)\" is got with etag$")
    public void customerWithCustomerIdIsGotWithEtag(String customerId) throws Throwable {
        customerSteps.customerWithIdIsGotWithEtag(customerId);
    }

    @When("^Customer with customerId \"([^\"]*)\" is got for etag, updated and got with previous etag by user with id \"([^\"]*)\"$")
    public void customerWithCustomerIdIsGotForEtagUpdatedAndGotWithPreviousEtag(String customerId, String userId) throws Throwable {
        customerSteps.customerWithIdIsGotWithEtagAfterUpdate(customerId, userId);
    }

    @When("^Customer with customer id \"([^\"]*)\" is deleted$")
    public void customerWithCustomerIdIsDeleted(String customerId) throws Throwable {
        customerSteps.deleteCustomer(customerId);
    }

    @When("^Customer with customer id \"([^\"]*)\" is deleted by user \"([^\"]*)\"$")
    public void customerWithCustomerIdIsDeletedByUser(String customerId, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user,is(notNullValue()));

        customerSteps.deleteCustomerByUser(user.getUserId(), customerId);
    }

    @When("^Customer with id \"([^\"]*)\" is updated with outdated etag$")
    public void customerWithIdIsUpdatedWithOutdatedEtag(String customerId) throws Throwable {
        customerSteps.customerWithIdIsUpdatedWithOutdatedEtag(customerId);
    }

    @When("^List of property sets for customer \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_for_customer_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerPropertySetsIsGotWith(customerId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all property sets for customer with id \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllPropertySetsForCustomerWithIdIsRequestedByUser(String customerId, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user,is(notNullValue()));

        customerSteps.listOfCustomerPropertySetsIsGotByUser(user.getUserId(), customerId, null, null, null, null, null);
    }

    @When("^List of users for customer with id \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_customer_with_id_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfUsersIsGotWith(customerId, limit, cursor, filter, sort, sortDesc);
    }


    @When("^List of all users for customer with id \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void listOfAllUsersForCustomerWithIdIsRequestedByUser(String customerId, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user,is(notNullValue()));

        customerSteps.listOfUsersIsGotByUser(user.getUserId(), customerId, null, null, null, null, null);
    }

    @When("^Update customer with id \"([^\"]*)\", field \"([^\"]*)\", its value \"([^\"]*)\"$")
    public void updateCustomerWithIdFieldItsValue(String customerId, String updatedField, String updatedValue) throws Throwable {
        CustomerUpdateDto customer = new CustomerUpdateDto();

        Field field = ReflectionUtils.findField(CustomerDto.class, updatedField);
        field.setAccessible(true);
        field.set(customer, updatedValue);
        customerSteps.updateCustomer(customerId, customer);
    }

    @When("^Customer with id \"([^\"]*)\" is activated$")
    public void customerWithIdIsActivated(String customerId) throws Throwable {
        customerSteps.setCustomerIsActive(customerId, true);
    }

    @When("^Customer with id \"([^\"]*)\" is inactivated$")
    public void customerWithIdIsInactivated(String customerId) throws Throwable {
        customerSteps.setCustomerIsActive(customerId, false);
    }

    @When("Customers commercial subscriptions for customer id \"([^\"]*)\" is got")
    public void Customers_commercial_subscriptions_for_customer_id_is_got(String customerId) {
        customerSteps.getCommSubscriptionForCustomerId(customerId);
    }


    @When("^List of customers commercial subscriptions is got for customer with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_commercial_subscriptions_is_got_for_customer_id_with_limit_cursor_filter_sort_sortdesc(
            String applicationId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerCommSubscriptionsIsGotWith(applicationId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of api subscriptions is got for customer with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void listOfApiSubscriptionsIsGotForCustomerWithIdAndLimitAndCursorAndFilterAndSortAndSort_desc(
            String customerId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerApiSubscriptionsIsGotWith(customerId, limit, cursor, filter, sort, sortDesc);
    }

    // ---------------------------- THEN ------------------------------

    @Then("^There are (\\d+) customers returned$")
    public void There_are_customers_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(CustomerDto.class, count);
    }

    @Then("^User with id \"([^\"]*)\" checks updated customer with id \"([^\"]*)\" has data$")
    public void Updated_customer_with_code_has_data(String userId, String customerId, List<CustomerDto> customers) throws Throwable {
        customerSteps.customerWithIdHasData(customerId, userId, customers.get(0));
    }

    @Then("^There are (\\d+) customerProperties returned$")
    public void There_are_returned_customerProperties_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(CustomerPropertyRelationshipDto.class, count);
    }

    @Then("^There are (\\d+) customerUsers returned$")
    public void There_are_returned_customerUsers_returned(int count) throws Throwable {
        usersSteps.numberOfEntitiesInResponse(CustomerUserRelationshipDto.class, count);
    }

    @Then("^User \"([^\"]*)\" isn't there for customer with id \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_customer_with_code(String username, String customerId)
            throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        customerSteps.userDoesntExistForCustomer(userId, customerId);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same customerProperty$")
    public void header_is_set_and_contains_the_same_customerProperty(String header) throws Throwable {
        customerSteps.compareCustomerPropertyOnHeaderWithStored(header);
    }

    @Then("^There are (\\d+) customer property sets returned$")
    public void There_are_returned_customer_property_sets_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(PropertySetDto.class, count);

    }

    @Then("^There are customer users with following usernames returned in order: (.*)$")
    public void There_are_customer_users_with_following_usernames_returned_in_order_expected_usernames(
            List<String> usernames) throws Throwable {
        customerSteps.usernamesAreInResponseInOrder(usernames);
    }

    @Then("^Field \"([^\"]*)\" has value \"([^\"]*)\" for property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" with type \"([^\"]*)\"$")
    public void Field_has_value_for_property_with_code_for_customer_with_code_with_type(String fieldName,
                                                                                        String value,
                                                                                        String propertyCode,
                                                                                        String customerId,
                                                                                        String type) throws Throwable {
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.fieldNameHasValueForPropertyForCustomerAndType(fieldName, value, p.getPropertyId(), customerId,
                type);
    }

    @Then("There are (\\d+) customers commercial subscriptions returned")
    public void There_are_customers_commercial_subscriptions_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(CommercialSubscriptionDto.class, count);
    }

    @Then("^There are customers with following emails returned in order: \"([^\"]*)\"$")
    public void thereAreCustomersWithFollowingEmailsReturnedInOrder(List<String> emails) throws Throwable {
        customerSteps.emailsAreInResponseInOrder(emails);
    }

    @Then("^There are customers with following ids returned in order: ([^\"]*)$")
    public void thereAreCustomersWithFollowingIdsReturnedInOrder(List<String> ids) throws Throwable {
        customerSteps.idsAreInResponseInOrder(ids);
    }

    @Then("^Customer with id \"([^\"]*)\" doesn't exist$")
    public void customerWithIdDoesnTExist(String customerId) throws Throwable {
        customerSteps.customerWithIdDoesNotExist(customerId);

    }

    @When("^Customer with customerId \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void customerWithCustomerIdIsGotByUserWithId(String customerId, String username) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));
        customerSteps.customerWithIdIsGotByUser(user.getUserId(), customerId);
    }

    @When("^Customer with customerId \"([^\"]*)\" is got with etag by user with id \"([^\"]*)\"$")
    public void customerWithCustomerIdIsGotWithEtagByUserWithId(String customerId, String userId) throws Throwable {
        customerSteps.customerWithIdIsGotWithEtagByUser(customerId, userId);
    }

    @Then("^Customer with id \"([^\"]*)\" is active$")
    public void customerWithIdIsActive(String customerId) throws Throwable {
        assertThat("Customer is not active", customerSteps.getCustomerIsActive(customerId), is(true));
    }

    @Then("^Customer with id \"([^\"]*)\" is not active$")
    public void customerWithIdIsNotActive(String customerId) throws Throwable {
        assertThat("Customer is active but shouldn't be", customerSteps.getCustomerIsActive(customerId), is(false));
    }

    @And("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" is primary$")
    public void relationBetweenUserAndCustomerWithIdHasIsPrimarySetTo(String username, String customerId) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        CustomerUserRelationshipDto existingCustomerUser = customerSteps.getUserForCustomer(customerId, user.getUserId());
        assertThat(existingCustomerUser, is(notNullValue()));
        assertThat(existingCustomerUser.getIsPrimary(), is(true));
    }

    @And("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" is not primary$")
    public void relationBetweenUserAndCustomerWithIdIsNotPrimary(String username, String customerId) throws Throwable {
        UserDto user = usersSteps.getUserByUsername(username);
        assertThat(user, is(notNullValue()));

        CustomerUserRelationshipDto existingCustomerUser = customerSteps.getUserForCustomer(customerId, user.getUserId());
        assertThat(existingCustomerUser.getIsPrimary(), is(false));
    }

    @When("^Nonexistent user is removed from customer with id \"([^\"]*)\"$")
    public void nonexistentUserIsRemovedFromCustomerWithId(String customerId) throws Throwable {
        customerSteps.userIsDeletedFromCustomer(NON_EXISTENT_ID, customerId);
    }

    @When("^Customer code of customer with Id \"([^\"]*)\" is updated with \"([^\"]*)\"$")
    public void customerCodeOfCustomerWithIdIsUpdatedWith(String customerId, String customerCode) throws Throwable {
        customerSteps.invalidCustomerUpdate(customerId, singletonMap("customer_code", customerCode));
    }

    @When("^Customer is created with code$")
    public void customerIsCreatedWithCode(List<CustomerDto> customers) throws Throwable {
        customerSteps.followingCustomerIsCreatedWithRandomAddress(customers.get(0));
    }

    @Given("^Customer \"([^\"]*)\" is created with address$")
    public void customerIsCreatedWithAddress(String companyName, List<AddressDto> addresses) throws Throwable {
        CustomerCreateDto customer = new CustomerCreateDto();
        customer.setCompanyName(companyName);
        customer.setEmail(DEFAULT_CUSTOMER_EMAIL);
        customer.setIsDemoCustomer(DEFAULT_CUSTOMER_IS_DEMO);
        customer.setTimezone(DEFAULT_CUSTOMER_TIMEZONE);
        customer.setAddress(addresses.get(0));

        customerSteps.followingCustomerIsCreated(customer);
    }

    @When("^Relation between customer \"([^\"]*)\" and user \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void relationBetweenCustomerAndUserWithUsernameIsRequestedByUserWithUsername(String customerId, String targetUserName, String requestorUserName) throws Throwable {
        Map<String, String> userIdsMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        customerSteps.getCustomerUserRelationByUser(userIdsMap.get(REQUESTOR_ID), customerId, userIdsMap.get(TARGET_ID));
    }
}
