package travel.snapshot.dp.qa.steps.identity.customers;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Property;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class CustomerStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Given("^The following customers exist with random address$")
    public void The_following_tenants_exist(List<Customer> customers) throws Throwable {
        customerSteps.followingCustomersExist(customers);
    }

    @Given("^The following customers with codes don't exist$")
    public void The_following_customers_dont_exist(List<String> customerCodes) throws Throwable {
        customerSteps.followingCustomersDontExist(customerCodes);
    }

    @Given("^Relation between property with code \"([^\"]*)\" and customer with code \"([^\"]*)\" exists with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void Relation_between_property_with_code_and_customer_with_code_exists_with_type_from_to(String propertyCode, String customerCode, String type, String validFrom, String validTo) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(p, customerCode, type, validFrom, validTo);

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
        customerSteps.getCustomerWithId("nonexistent_id");
    }

    @When("^Nonexistent customer id is deleted$")
    public void Nonexistent_customer_id_is_deleted() throws Throwable {
        customerSteps.deleteCustomerWithId("nonexistent_id");
    }

    @When("^List of customers is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customers_is_got_with_limit_and_cursor_and_filter_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                        @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                        @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                        @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                        @Transform(NullEmptyStringConverter.class) String sortDesc ) throws Throwable {
        customerSteps.listOfCustomersIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @When("^Customer with code \"([^\"]*)\" is updated with data$")
    public void Customer_with_code_is_updated_with_data(String code, List<Customer> customers) throws Throwable {
        customerSteps.updateCustomerWithCode(code, customers.get(0));
    }

    @When("^Customer with code \"([^\"]*)\" is deleted$")
    public void Customer_with_code_is_deleted(String code) throws Throwable {
        customerSteps.deleteCustomerWithCode(code);
    }

    @When("^Customer with code \"([^\"]*)\" is got$")
    public void Customer_with_code_is_got(String code) throws Throwable {
        customerSteps.getCustomerWithCode(code);
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
        customerSteps.getCustomerWithCodeUsingEtag(code);
    }

    @When("^Customer with code \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void Customer_with_code_is_got_for_etag_updated_and_got_with_previous_etag(String code) throws Throwable {
        customerSteps.getCustomerWithCodeUsingEtagAfterUpdate(code);
    }

    @When("^Customer with code \"([^\"]*)\" is updated with data if updated before$")
    public void Customer_with_code_is_updated_with_data_if_updated_before(String code, List<Customer> customers) throws Throwable {
        customerSteps.updateCustomerWithCodeIfUpdatedBefore(code, customers.get(0));
    }

    @When("^Property with code \"([^\"]*)\" is added to customer with code \"([^\"]*)\" with type \"([^\"]*)\" from \"([^\"]*)\" to \"([^\"]*)\"$")
    public void Property_with_code_is_added_to_customer_with_code_with_type_from_to(String propertyCode, String customerCode, String type, String dateFrom, String dateTo) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsAddedToCustomerWithTypeFromTo(p, customerCode, type, dateFrom, dateTo);
    }

    @When("^Property with code \"([^\"]*)\" from customer with code \"([^\"]*)\" is got with type \"([^\"]*)\"$")
    public void Property_with_code_from_customer_with_code_is_got_with_type(String propertyCode, String customerCode, String type) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        customerSteps.propertyIsgotForCustomerWithType(p, customerCode, type);
    }

    @When("^Nonexistent customerPropety id is got for customer with code \"([^\"]*)\"$")
    public void Nonexistent_customerPropety_id_is_got_for_customer_with_code(String custmerCode) throws Throwable {
        customerSteps.getCustomerPropertyWithId(custmerCode, "nonexistent");
    }

    @When("^List of customerProperties is got for customer with code \"([^\"]*)\" with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_customerProperties_is_got_for_customer_with_code_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(String customerCode,
                                                                                                                                 @Transform(NullEmptyStringConverter.class) String limit,
                                                                                                                                 @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                                                 @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                                                 @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                                                 @Transform(NullEmptyStringConverter.class) String sortDesc ) throws Throwable {
        customerSteps.listOfCustomerPropertiesIsGotWith(customerCode,
                limit, cursor, filter, sort, sortDesc);
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
        customerSteps.numberOfCustomersIsInResponse(count);
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

    @Then("^Body contains customer type with \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_customer_type_with_value(String atributeName, String value) throws Throwable {
        customerSteps.bodyContainsEntityWithAtribute(atributeName, value);
    }

    @Then("^There are customers with following codes returned in order: (.*)")
    public void There_are_customers_with_following_codes_returned_in_order(List<String> codes) throws Throwable {
        customerSteps.codesAreInResponseInOrder(codes);
    }

    @Then("^Body contains customerProperty type with \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_customerProperty_type_with_value(String parameterName, String value) throws Throwable {
        customerSteps.bodyContainsEntityWithAtribute(parameterName, value);
    }

    @Then("^There are (\\d+) customerProperties returned$")
    public void There_are_returned_customerProperties_returned(int count) throws Throwable {
        customerSteps.numberOfCustomerPropertiesIsInResponse(count);
    }
}