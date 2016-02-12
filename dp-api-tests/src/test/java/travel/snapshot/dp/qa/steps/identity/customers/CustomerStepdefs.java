package travel.snapshot.dp.qa.steps.identity.customers;

import cucumber.api.PendingException;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.*;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;
import travel.snapshot.dp.qa.steps.BasicStepDefs;
import travel.snapshot.dp.qa.steps.review.ReviewMultipropertyCustomerSteps;

import java.util.List;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class CustomerStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private ReviewMultipropertyCustomerSteps reviewMultipropertyCustomerSteps;

    @Given("^Set access token from session for customer steps defs$")
    public void setAccessTokenFromSessionForCustomerStepsDefs() throws Throwable {
        customerSteps.setAccessToken();
        propertySteps.setAccessToken();
        usersSteps.setAccessToken();
        reviewMultipropertyCustomerSteps.setAccessToken();
    }


    @Given("^The following customers exist with random address$")
    public void The_following_tenants_exist(List<Customer> customers) throws Throwable {
        customerSteps.followingCustomersExist(customers);
    }

    @When("^A customer from country \"([^\"]*)\" region \"([^\"]*)\" code \"([^\"]*)\" email \"([^\"]*)\" is created$")
    public void customer_from_country_region_code_email_is_created(String country, String region, String code,
                                                                   String email) {
        Address address = new Address();
        Customer customer = new Customer();
        address.setAddressLine1("someAddress");
        address.setCity("someCity");
        address.setZipCode("1234");
        address.setCountry(country);
        address.setRegion(region);
        customer.setCompanyName("someCompany");
        customer.setCode(code);
        customer.setEmail(email);
        customer.setIsDemoCustomer(true);
        customer.setTimezone("GMT");
        customerSteps.followingCustomerIsCreatedWithAddress(customer, address);
    }

    @When("^A customer from country \"([^\"]*)\" code \"([^\"]*)\" email \"([^\"]*)\" vatId \"([^\"]*)\" company nqma \"([^\"]*)\"is created$")
    public void a_customer_from_country_with_code_and_email_is_created_with_proper_vatID(String country, String code,
                                                                                         String email, String vatId, String companyName) {
        Address address = new Address();
        Customer customer = new Customer();
        address.setAddressLine1("someAddress");
        address.setCity("someCity");
        address.setZipCode("1234");
        address.setCountry(country);
        customer.setCompanyName(companyName);
        customer.setCode(code);
        customer.setEmail(email);
        customer.setIsDemoCustomer(true);
        customer.setTimezone("GMT");
        customer.setVatId(vatId);
        customerSteps.followingCustomerIsCreatedWithAddress(customer, address);
    }

    @Given("^The following customers with codes don't exist$")
    public void The_following_customers_dont_exist(List<String> customerCodes) throws Throwable {
        customerSteps.followingCustomersDontExist(customerCodes);
    }

    @Given("^Relation between property with code \"([^\"]*)\" and customer with code \"([^\"]*)\" exists with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void Relation_between_property_with_code_and_customer_with_code_exists_with_type_from_to(String propertyCode,
                                                                                                    String customerCode, String type, String validFrom, String validTo) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(p, customerCode, type, validFrom, validTo);
    }

    @Given("^Relation between user with username \"([^\"]*)\" and customer with code \"([^\"]*)\" exists with isPrimary \"([^\"]*)\"$")
    public void Relation_between_user_with_username_and_customer_with_code_exists_with_isPrimary(String username,
                                                                                                 String customerCode, String isPrimary) throws Throwable {
        User user = usersSteps.getUserByUsername(username);
        customerSteps.relationExistsBetweenUserAndCustomerWithPrimary(user, customerCode, isPrimary);
    }

    @When("^Customer is created with random address$")
    public void customer_is_created(List<Customer> customers) throws Throwable {
        customerSteps.followingCustomerIsCreated(customers.get(0));
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

    @When("^Nonexistent customer id is got$")
    public void Nonexistent_customer_id_is_got() throws Throwable {
        customerSteps.customerWithIdIsGot("nonexistent_id");
    }

    @When("^Nonexistent customer id is deleted$")
    public void Nonexistent_customer_id_is_deleted() throws Throwable {
        customerSteps.deleteCustomerWithId("nonexistent_id");
    }

    @When("^List of customers is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_is_got_with_limit_and_cursor_and_filter_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomersIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @When("^Customer with code \"([^\"]*)\" is updated with data$")
    public void Customer_with_code_is_updated_with_data(String code, List<Customer> customers) throws Throwable {
        customerSteps.updateCustomerWithCode(code, customers.get(0));
    }

    @When("^Customer with code \"([^\"]*)\" is deleted$")
    public void Customer_with_code_is_deleted(String code) throws Throwable {
        customerSteps.customerWithCodeIsDeleted(code);
    }

    @When("^Customer with code \"([^\"]*)\" is got$")
    public void Customer_with_code_is_got(String code) throws Throwable {
        customerSteps.customerWithCodeIsGot(code);
    }

    @When("^Customer with code \"([^\"]*)\" is activated$")
    public void Customer_with_code_is_activated(String code) throws Throwable {
        customerSteps.activateCustomerWithCode(code);
    }

    @When("^Customer with code \"([^\"]*)\" is inactivated$")
    public void Customer_with_code_is_inactivated(String code) throws Throwable {
        customerSteps.inactivateCustomerWithCode(code);
    }

    @When("^Customer with code \"([^\"]*)\" is got with etag$")
    public void Customer_with_code_is_got_with_etag(String code) throws Throwable {
        customerSteps.customerWithCodeIsGotWithEtag(code);
    }

    @When("^Customer with code \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void Customer_with_code_is_got_for_etag_updated_and_got_with_previous_etag(String code) throws Throwable {
        customerSteps.customerWithCodeIsGotWithEtagAfterUpdate(code);
    }

    @When("^Customer with code \"([^\"]*)\" is updated with data if updated before$")
    public void Customer_with_code_is_updated_with_data_if_updated_before(String code, List<Customer> customers)
            throws Throwable {
        customerSteps.updateCustomerWithCodeIfUpdatedBefore(code, customers.get(0));
    }

    @When("^Property with code \"([^\"]*)\" is added to customer with code \"([^\"]*)\" with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void Property_with_code_is_added_to_customer_with_code_with_type_from_to(String propertyCode,
                                                                                    String customerCode, @Transform(NullEmptyStringConverter.class) String type,
                                                                                    @Transform(NullEmptyStringConverter.class) String dateFrom,
                                                                                    @Transform(NullEmptyStringConverter.class) String dateTo) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        if (p == null) {
            customerSteps.propertyIsAddedToCustomerWithTypeFromTo(BasicStepDefs.NONEXISTENT_ID, customerCode, type,
                    dateFrom, dateTo);
        } else {
            customerSteps.propertyIsAddedToCustomerWithTypeFromTo(p.getPropertyId(), customerCode, type, dateFrom,
                    dateTo);
        }

    }

    @When("^Property with code \"([^\"]*)\" from customer with code \"([^\"]*)\" is got with type \"([^\"]*)\"$")
    public void Property_with_code_from_customer_with_code_is_got_with_type(String propertyCode, String customerCode,
                                                                            String type) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithType(p, customerCode, type);
    }

    @When("^Nonexistent customerPropety id is got for customer with code \"([^\"]*)\"$")
    public void Nonexistent_customerPropety_id_is_got_for_customer_with_code(String custmerCode) throws Throwable {
        customerSteps.getCustomerPropertyWithId(custmerCode, "nonexistent");
    }

    @When("^List of customerProperties is got for customer with code \"([^\"]*)\" with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customerProperties_is_got_for_customer_with_code_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerCode, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerPropertiesIsGotWith(customerCode, limit, cursor, filter, sort, sortDesc);
    }

    @When("^User with username \"([^\"]*)\" is added to customer with code \"([^\"]*)\" with isPrimary \"([^\"]*)\"$")
    public void User_with_username_is_added_to_customer_with_code_with_isPrimary(String username, String customerCode,
                                                                                 String isPrimary) throws Throwable {
        User u = usersSteps.getUserByUsername(username);
        customerSteps.userIsAddedToCustomerWithIsPrimary(u, customerCode, isPrimary);
    }

    @When("^User with username \"([^\"]*)\" is removed from customer with code \"([^\"]*)\"$")
    public void User_with_username_is_removed_from_customer_with_code(String username, String customerCode)
            throws Throwable {
        User u = usersSteps.getUserByUsername(username);
        customerSteps.userIsDeletedFromCustomer(u, customerCode);
    }

    @Then("^Customer with same id doesn't exist$")
    public void Customer_with_same_id_doesn_t_exist() throws Throwable {
        customerSteps.customerIdInSessionDoesntExist();
    }

    @Then("^\"([^\"]*)\" header is set and contains the same customer$")
    public void header_is_set_and_contains_the_same_customer(String headerName) throws Throwable {
        customerSteps.compareCustomerOnHeaderWithStored(headerName);
    }

    @Then("^There are (\\d+) customers returned$")
    public void There_are_customers_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(Customer.class, count);
    }

    @Then("^Updated customer with code \"([^\"]*)\" has data$")
    public void Updated_customer_with_code_has_data(String code, List<Customer> customers) throws Throwable {
        customerSteps.customerWithCodeHasData(code, customers.get(0));
    }

    @Then("^Customer with code \"([^\"]*)\" is active$")
    public void Customer_with_code_is_active(String code) throws Throwable {
        customerSteps.isActiveSetTo(true, code);
    }

    @Then("^Customer with code \"([^\"]*)\" is not active$")
    public void Customer_with_code_is_not_active(String code) throws Throwable {
        customerSteps.isActiveSetTo(false, code);
    }

    @Then("^There are customers with following codes returned in order: (.*)")
    public void There_are_customers_with_following_codes_returned_in_order(List<String> codes) throws Throwable {
        customerSteps.codesAreInResponseInOrder(codes);
    }

    @Then("^There are (\\d+) customerProperties returned$")
    public void There_are_returned_customerProperties_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(CustomerProperty.class, count);
    }

    @Then("^User with username \"([^\"]*)\" isn't there for customer with code \"([^\"]*)\"$")
    public void User_with_username_isn_t_there_for_customer_with_code(String username, String customerCode)
            throws Throwable {
        User u = usersSteps.getUserByUsername(username);
        customerSteps.userDoesntExistForCustomer(u, customerCode);
    }

    @When("^Nonexistent user is removed from customer with code \"([^\"]*)\"$")
    public void Nonexistent_user_is_removed_from_customer_with_code(String customerId) throws Throwable {
        User user = new User();
        user.setUserId("nonexistent");
        customerSteps.userIsDeletedFromCustomer(user, customerId);
    }

    @Then("^\"([^\"]*)\" header is set and contains the same customerProperty$")
    public void header_is_set_and_contains_the_same_customerProperty(String header) throws Throwable {
        customerSteps.compareCustomerPropertyOnHeaderWithStored(header);
    }

    @When("^Property with code \"([^\"]*)\" for customer with code \"([^\"]*)\" with type \"([^\"]*)\" is updating field \"([^\"]*)\" to value \"([^\"]*)\"$")
    public void Property_with_code_for_customer_with_code_with_type_is_updating_field_to_value(String propertyCode,
                                                                                               String customerCode, String type, String fieldName, String value) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsUpdateForCustomerWithType(p, customerCode, type, fieldName, value);
    }

    @When("^Property with code \"([^\"]*)\" for customer with code \"([^\"]*)\" with type \"([^\"]*)\" is updating field \"([^\"]*)\" to value \"([^\"]*)\" with invalid etag$")
    public void Property_with_code_for_customer_with_code_with_type_is_updating_field_to_value_with_invalid_etag(
            String propertyCode, String customerCode, String type, String fieldName, String value) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsUpdateForCustomerWithTypeWithInvalidEtag(p, customerCode, type, fieldName, value);
    }

    @When("^Property with code \"([^\"]*)\" from customer with code \"([^\"]*)\" is got with type \"([^\"]*)\" with etag$")
    public void Property_with_code_from_customer_with_code_is_got_with_type_with_etag(String propertyCode,
                                                                                      String customerCode, String type) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithTypeWithEtag(p, customerCode, type);
    }

    @When("^Property with code \"([^\"]*)\" from customer with code \"([^\"]*)\" is got with type \"([^\"]*)\" for etag, updated and got with previous etag$")
    public void Property_with_code_from_customer_with_code_is_got_with_type_for_etag_updated_and_got_with_previous_etag(
            String propertyCode, String customerCode, String type) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithTypeWithEtagAfterUpdate(p, customerCode, type);
    }

    @Then("^There are (\\d+) customer property sets returned$")
    public void There_are_returned_customer_property_sets_returned(int count) throws Throwable {
        customerSteps.numberOfEntitiesInResponse(PropertySetArray.class, count);

    }

    @When("^List of property sets for customer \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_for_customer_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerCode, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfCustomerPropertySetsIsGotWith(customerCode, limit, cursor, filter, sort, sortDesc);
    }

    @Given("^All users are removed for customers with codes: (.*)$")
    public void All_users_are_removed_for_customers_with_codes_default(List<String> codes) throws Throwable {
        customerSteps.removeAllUsersFromCustomers(codes);
    }

    @When("^List of users for customer with code \"([^\"]*)\" is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_users_for_customer_with_code_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String customerCode, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        customerSteps.listOfUsersIsGotWith(customerCode, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are customer users with following usernames returned in order: (.*)$")
    public void There_are_customer_users_with_following_usernames_returned_in_order_expected_usernames(
            List<String> usernames) throws Throwable {
        customerSteps.usernamesAreInResponseInOrder(usernames);
    }

    @Given("^Customer with code \"([^\"]*)\" does not exist$")
    public void Customer_with_code_does_not_exist(String customerCode) throws Throwable {
        customerSteps.customerWithCodeIsDeleted(customerCode);
    }

    @Then("^Field \"([^\"]*)\" has value \"([^\"]*)\" for property with code \"([^\"]*)\" for customer with code \"([^\"]*)\" with type \"([^\"]*)\"$")
    public void Field_has_value_for_property_with_code_for_customer_with_code_with_type(String fieldName, String value,
                                                                                        String propertyCode, String customerCode, String type) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.fieldNameHasValueForPropertyForCustomerAndType(fieldName, value, p.getPropertyId(), customerCode,
                type);
    }

    @Then("^All customers are active$")
    public void all_customers_are_active() throws Throwable {
        customerSteps.allCustomersAreActive();
    }


}
