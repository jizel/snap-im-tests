package travel.snapshot.dp.qa.steps.identity.customers;

import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static travel.snapshot.dp.qa.helpers.CustomerUtils.createCustomerDto;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_CUSTOMER_TYPE;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_SALESFORCE_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.REQUESTOR_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.TARGET_ID;

import com.jayway.restassured.response.Response;
import cucumber.api.DataTable;
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
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.CustomerUtils;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.review.ReviewMultipropertyCustomerSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

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

    @Steps
    private ApplicationVersionsSteps applicationVersionSteps;


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

    @Given("^Relation between property(?: with code)? \"([^\"]*)\" and customer(?: with id)? \"([^\"]*)\" exists(?: with type \"([^\"]*)\")?(?: from \"([^\"]*)\" to \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?$")
    public void Relation_between_property_with_code_and_customer_with_code_exists_with_type_from_to(String propertyCode,
                                                                                                    String customerName, String type, String validFrom, String validTo, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String customerId = customerSteps.resolveCustomerId(customerName);
        customerSteps.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(propertyId, customerId, type, validFrom, validTo, isActive);
    }

    @Given("^Relation between user \"([^\"]*)\" and customer(?: with id)? \"([^\"]*)\" exists(?: with isPrimary \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?$")
    public void Relation_between_user_with_username_and_customer_with_id_exists_with_isPrimary(String username,
                                                                                                 String customerName, String isPrimaryString, String isActiveString) throws Throwable {
        Boolean isPrimary = ((isPrimaryString==null) ? false : Boolean.valueOf(isPrimaryString));
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String userId = usersSteps.resolveUserId(username);
        String customerId = customerSteps.resolveCustomerId(customerName);
        customerSteps.relationExistsBetweenUserAndCustomer(userId, customerId, isPrimary, isActive);
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
    public void customer_is_created(DataTable customersTable) throws Throwable {
//        Cucumber Transformer cannot parse SalesforceId. Using dataTable and parsing values instead.
        Map<String, Object> customerMap = customersTable.asMaps(String.class, Object.class).get(0);
        CustomerCreateDto customer = createCustomerDto(customerMap);
        customerSteps.followingCustomerIsCreatedWithRandomAddress(customer);
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

    @When("^List of customers is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void List_of_customers_is_got_with_limit_and_cursor_and_filter_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.listOfCustomersIsGotByUserForAppVersionWith(userId, applicationVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^Customer(?: with id)? \"([^\"]*)\" is updated with data(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void customerWithIdIsUpdatedWithData(String customerName, String username, String applicationVersionName, List<CustomerUpdateDto> customersData) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        String customerId = customerSteps.resolveCustomerId(customerName);
        customerSteps.updateCustomerByUserForApp(customerId, userId, applicationVersionId, customersData.get(0));
    }

    @When("^Property(?: with code)? \"([^\"]*)\" is added to customer with id \"([^\"]*)\"(?: with type \"([^\"]*)\")?(?: from \"([^\"]*)\" to \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?(?: with error \"([^\"]*)\")?$")
    public void Property_with_code_is_added_to_customer_with_code_with_type_from_to(String propertyCode,
                                                                                    String customerId, @Transform(NullEmptyStringConverter.class) String type,
                                                                                    @Transform(NullEmptyStringConverter.class) String dateFrom,
                                                                                    @Transform(NullEmptyStringConverter.class) String dateTo,
                                                                                    String isActiveString, String userName, String applicationVersionName, Boolean error) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String userId = usersSteps.resolveUserId(userName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        String propertyId = (property == null) ? NON_EXISTENT_ID : property.getId();
        if (error){
            customerSteps.addPropertyToCustomerByUserForAppInvalid(userId, applicationVersionId, propertyId, customerId, type, dateFrom, dateTo, isActive);
        }
        else {
            customerSteps.addPropertyToCustomerByUserForApp(userId, applicationVersionId, propertyId, customerId, type, dateFrom, dateTo, isActive);
        }

    }

    @When("^Property with code \"([^\"]*)\" is added to customer(?: with id)? \"([^\"]*)\" with type \"([^\"]*)\"(?: from \"([^\"]*)\")?(?: to \"([^\"]*)\")(?: by user \"([^\"]*)\")?(?: with is_active \"([^\"]*)\")$")
    public void propertyWithCodeIsAddedToCustomerWithIdWithTypeFromToByUser(String propertyCode, String customerName, @Transform(NullEmptyStringConverter.class) String type,
                                                                            @Transform(NullEmptyStringConverter.class) String dateFrom,
                                                                            @Transform(NullEmptyStringConverter.class) String dateTo,
                                                                            String username, String isActiveString) throws Throwable {
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String userId = usersSteps.resolveUserId(username) ;
        String customerId = customerSteps.resolveCustomerId(customerName);
        Response response = customerSteps.addPropertyToCustomerByUser(userId, propertyId, customerId, type, dateFrom, dateTo, isActive);
        customerSteps.setSessionResponse(response);
    }

    @When("^Property with code \"([^\"]*)\" (?:from|for) customer(?: with id)? \"([^\"]*)\" is (?:got|requested)(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void propertyWithCodeFromCustomerWithIdIsGotByUser(String propertyCode, String customerName, String username, String applicationVersionName) throws Throwable {
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String userId = usersSteps.resolveUserId(username) ;
        String customerId = customerSteps.resolveCustomerId(customerName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.getPropertyForCustomerByUserForApp(userId, applicationVersionId, propertyId, customerId);
    }

    @When("^Nonexistent customerPropety id is got for customer with id \"([^\"]*)\"$")
    public void Nonexistent_customerPropety_id_is_got_for_customer_with_code(String customerId) throws Throwable {
        customerSteps.getCustomerPropertyWithRelationshipId(customerId, NON_EXISTENT_ID);
    }

    @When("^List of customerProperties is got for customer(?: with id)? \"([^\"]*)\" with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customerProperties_is_got_for_customer_with_id_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerName, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        String customerId = customerSteps.resolveCustomerId(customerName);
        customerSteps.listOfCustomerPropertiesIsGotWith(customerId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all customer properties is got for customer with id \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllCustomerPropertiesIsGotForCustomerWithIdByUser(String customerId, String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username) ;
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.listOfCustomerPropertiesIsGotByUserForApp(userId, applicationVersionId, customerId, null, null, null, null, null);
    }

    @When("^User \"([^\"]*)\" is added to customer with id \"([^\"]*)\"(?: with isPrimary \"([^\"]*)\")?(?: and|with is_active \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userIsAddedToCustomerWithIdWithIsPrimaryByUser(String username, String customerId,
                                                               String isPrimaryString, String isActiveString, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> userIds = usersSteps.getUsersIds(performerName, username);
        Boolean isActive = ((isActiveString==null) ? true : Boolean.valueOf(isActiveString));
        Boolean isPrimary = ((isPrimaryString==null) ? false : Boolean.valueOf(isPrimaryString));
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        Response response = customerSteps.addUserToCustomerByUserForApp(userIds.get(REQUESTOR_ID), applicationVersionId, userIds.get(TARGET_ID), customerId, isPrimary, isActive);
        customerSteps.setSessionResponse(response);
    }

    @When("^User \"([^\"]*)\" is removed from customer with id \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void userIsRemovedFromCustomerWithIdByUser(String username, String customerId, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> userIds = usersSteps.getUsersIds(performerName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.userIsDeletedFromCustomerByUserForApp(userIds.get(REQUESTOR_ID), applicationVersionId, userIds.get(TARGET_ID), customerId);
    }

    @When("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" is updated with isPrimary \"([^\"]*)\"(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void relationBetweenUserAndCustomerWithIdIsUpdatedWithIsPrimaryByUser(String username, String customerId, Boolean isPrimary, String performerName, String applicationVersionName) throws Throwable {
        Map<String, String> userIds = usersSteps.getUsersIds(performerName, username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        UserCustomerRelationshipDto userCustomerRelationship = new UserCustomerRelationshipDto();
        userCustomerRelationship.setIsPrimary(isPrimary);
        customerSteps.updateUserCustomerRelationshipByUserForApp(userIds.get(REQUESTOR_ID), applicationVersionId, userIds.get(TARGET_ID), customerId, userCustomerRelationship);
    }

    @When("^Property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" is updating field \"([^\"]*)\" to value \"([^\"]*)\"$")
    public void Property_with_code_for_customer_with_code_with_type_is_updating_field_to_value(String propertyCode,
                                                                                               String customerId, String fieldName, String value) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsUpdateForCustomer(property, customerId, fieldName, value);
    }

    @When("^Property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" is updated(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")? with$")
    public void propertyWithCodeForCustomerWithIdFieldIsUpdatedToValueString(String propertyCode,String customerId,
                                                                             String username, String applicationVersionName, List<CustomerPropertyRelationshipUpdateDto> relationshipUpdates) throws Throwable {
        String propertyId = propertySteps.resolvePropertyId(propertyCode);
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.updateCustomerPropertyRelationshipByUserForApp(userId, applicationVersionId, propertyId, customerId, relationshipUpdates.get(0));
    }

    @When("^Property with code \"([^\"]*)\" for customer with id \"([^\"]*)\" is updating field \"([^\"]*)\" to value \"([^\"]*)\" with invalid etag$")
    public void Property_with_code_for_customer_with_code_with_type_is_updating_field_to_value_with_invalid_etag(
            String propertyCode, String customerId, String fieldName, String value) throws Throwable {
        PropertyDto property = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsUpdateForCustomerWithInvalidEtag(property, customerId, fieldName, value);
    }

    @When("^Customer with id \"([^\"]*)\", update address with following data$")
    public void customerWithIdUpdateAddressWithFollowingData(String customerId, List<AddressUpdateDto> addresses) throws Throwable {
        customerSteps.updateCustomerAddress(customerId, addresses.get(0));
    }

    @When("^Relation between user \"([^\"]*)\" and customer(?: with id)? \"([^\"]*)\" is deleted$")
    public void relationBetweenUserWithUsernameAndCustomerIsDeleted(String username, String customerId) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        customerSteps.relationBetweenUserAndCustomerIsDeleted(userId, customerId);
    }

    @When("^Customer with customerId \"([^\"]*)\" is got$")
    public void customerWithCustomerIdIsGot(String customerId) throws Throwable {
        customerSteps.customerWithIdIsGot(customerId);
    }

    @When("^Customer(?: with id)? \"([^\"]*)\" is deleted(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void customerWithCustomerIdIsDeletedByUserForApp(String customerName, String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String customerId = customerSteps.resolveCustomerId(customerName);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.deleteCustomerByUserForApp(userId, applicationVersionId, customerId);
    }

    @When("^Customer with id \"([^\"]*)\" is updated with outdated etag$")
    public void customerWithIdIsUpdatedWithOutdatedEtag(String customerId) throws Throwable {
        customerSteps.customerWithIdIsUpdatedWithOutdatedEtag(customerId);
    }

    @When("^List of property sets for customer \"([^\"]*)\" is got(?: by user \"([^\"]*)\")? with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_for_customer_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerId, String username, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerPropertySetsIsGotByUserForApp(usersSteps.resolveUserId(username), DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                customerId, limit, cursor, filter, sort, sortDesc);
    }

    @When("^List of all property sets for customer with id \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllPropertySetsForCustomerWithIdIsRequestedByUser(String customerId, String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.listOfCustomerPropertySetsIsGotByUserForApp(userId, applicationVersionId, customerId, null, null, null, null, null);
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


    @When("^List of all users for customer with id \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void listOfAllUsersForCustomerWithIdIsRequestedByUser(String customerId, String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.listOfUsersIsGotByUserForApp(userId, applicationVersionId, customerId, null, null, null, null, null);
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

    @Then("^(?:User with id \"([^\"]*)\" )?(?:c|C)hecks? updated customer(?: with id)? \"([^\"]*)\" has data$")
    public void Updated_customer_with_code_has_data(String userId, String customerName, List<CustomerDto> customers) throws Throwable {
        String customerId = customerSteps.resolveCustomerId(customerName);
        userId = (userId == null) ? DEFAULT_SNAPSHOT_USER_ID : userId;
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
        customerSteps.fieldNameHasValueForPropertyForCustomer(fieldName, value, p.getId(), customerId);
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

    @When("^Customer with customerId \"([^\"]*)\" is requested(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void customerWithCustomerIdIsGotByUserWithId(String customerId, String username, String applicationVersionName) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        String applicationVersionId = applicationVersionSteps.resolveApplicationVersionId(applicationVersionName);
        customerSteps.customerWithIdIsGotByUserForApplication(userId, applicationVersionId, customerId);
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
        String userId = usersSteps.resolveUserId(username);

        CustomerUserRelationshipDto existingCustomerUser = customerSteps.getUserForCustomer(customerId, userId);
        assertThat(existingCustomerUser, is(notNullValue()));
        assertThat(existingCustomerUser.getIsPrimary(), is(true));
    }

    @And("^Relation between user \"([^\"]*)\" and customer with id \"([^\"]*)\" is not primary$")
    public void relationBetweenUserAndCustomerWithIdIsNotPrimary(String username, String customerId) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        CustomerUserRelationshipDto existingCustomerUser = customerSteps.getUserForCustomer(customerId, userId);
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
        customer.setType(CustomerType.valueOf(DEFAULT_CUSTOMER_TYPE));
        customer.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
        customerSteps.followingCustomerIsCreated(customer);
    }

    @When("^Relation between customer \"([^\"]*)\" and user \"([^\"]*)\" is requested by user \"([^\"]*)\"$")
    public void relationBetweenCustomerAndUserWithUsernameIsRequestedByUserWithUsername(String customerId, String targetUserName, String requestorUserName) throws Throwable {
        Map<String, String> userIdsMap = usersSteps.getUsersIds(requestorUserName, targetUserName);
        customerSteps.getCustomerUserRelationByUser(userIdsMap.get(REQUESTOR_ID), customerId, userIdsMap.get(TARGET_ID));
    }

    @When("^Relation between user \"([^\"]*)\" and customer(?: with id)? \"([^\"]*)\" is (de|in)?activated$")
    public void relationBetweenUserAndCustomerWithIdIsActivated(String userName, String customerId, String negation) throws Throwable {
        Boolean isActive = true;
        if (negation != null) {
            isActive = false;
        }
        String userId = usersSteps.resolveUserId(userName);
        UserCustomerRelationshipUpdateDto relation = new UserCustomerRelationshipUpdateDto();
        relation.setIsActive(isActive);
        customerSteps.updateUserCustomerRelationship(userId, customerId, relation);
    }
}
