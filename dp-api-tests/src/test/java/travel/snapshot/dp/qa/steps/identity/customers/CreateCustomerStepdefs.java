package travel.snapshot.dp.qa.steps.identity.customers;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;

import java.util.List;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class CreateCustomerStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CustomerSteps customerSteps;

    @Then("^Response code is \"(\\d+)\"$")
    public void response_code_is(int responseCode) throws Throwable {
        customerSteps.responseCodeIs(responseCode);
    }

    @Given("^The following customers exist with random address$")
    public void The_following_tenants_exist(List<Customer> customers) throws Throwable {
        customerSteps.followingCustomersExist(customers);
    }

    @When("^\"([^\"]*)\" is called without token using \"([^\"]*)\"$")
    public void is_called_without_token_using(String service, String method) throws Throwable {
        customerSteps.isCalledWithoutTokenUsingMethod(service, method);
    }

    @Then("^Custom code is \"(\\d+)\"$")
    public void Custom_code_is(Integer customCode) throws Throwable {
        customerSteps.customCodeIs(customCode);
    }

    @Then("^Content type is \"([^\"]*)\"$")
    public void content_type_is(String contentType) throws Throwable {
        customerSteps.contentTypeIs(contentType);
    }

    @Then("^Body is empty$")
    public void body_is_empty() throws Throwable {
        customerSteps.bodyIsEmpty();
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
            default: break;
        }

    }

    @Then("^\"([^\"]*)\" header is set and contains the same customer$")
    public void header_is_set_and_contains_the_same_customer(String headerName) throws Throwable {
        customerSteps.compareCustomerOnHeaderWithStored(headerName);
    }

    @When("^Nonexistent customer id is got$")
    public void Nonexistent_customer_id_is_got() throws Throwable {
        customerSteps.getCustomerWithId("nonexistent_id");
    }

    @When("^Nonexistent customer id is deleted$")
    public void Nonexistent_customer_id_is_deleted() throws Throwable {
        customerSteps.deleteCustomerWithId("nonexistent_id");
    }

    @And("^Customer with same id doesn't exist$")
    public void Customer_with_same_id_doesn_t_exist() throws Throwable {
        customerSteps.customerIdInSessionDoesntExist();
    }

    @When("^List of customers is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter empty and sort empty$")
    public void List_of_customers_is_got_with_limit_and_cursor_and_filter_empty_and_sort_empty(String limit, String cursor) throws Throwable {
        customerSteps.listOfCustomersIsGotWith(limit, cursor);
    }

    @Then("^There are (\\d+) customers returned$")
    public void There_are_customers_returned(int count) throws Throwable {
        customerSteps.numberOfCustomersIsInResponse(count);
    }

    @When("^Customer with code \"([^\"]*)\" is updated with data$")
    public void Customer_with_code_is_updated_with_data(String code, List<Customer> customers) throws Throwable {
        customerSteps.updateCustomerWithCode(code, customers.get(0));
    }

    @When("^Customer with code \"([^\"]*)\" is deleted$")
    public void Customer_with_code_is_deleted(String code) throws Throwable {
        customerSteps.deleteCustomerWithCode(code);
    }

    @Then("^Updated customer with code \"([^\"]*)\" has data$")
    public void Updated_customer_with_code_has_data(String code, List<Customer> customers) throws Throwable {
        // Express the Regexp above with the code you wish you had
        throw new PendingException();
    }

    @When("^Customer with code \"([^\"]*)\" is got$")
    public void Customer_with_code_is_got(String code) throws Throwable {
        customerSteps.getCustomerWithCode(code);
    }

    @When("^Customer with code \"([^\"]*)\" is activated$")
    public void Customer_with_code_is_activated(String code) throws Throwable {
        customerSteps.activateCustomerWithCode(code);
    }

    @Then("^Customer with code \"([^\"]*)\" is active$")
    public void Customer_with_code_is_active(String code) throws Throwable {
        customerSteps.isActiveSetTo(true, code);
    }

    @When("^Customer with code \"([^\"]*)\" is inactivated$")
    public void Customer_with_code_is_inactivated(String code) throws Throwable {
        customerSteps.inactivateCustomerWithCode(code);
    }

    @Then("^Customer with code \"([^\"]*)\" is not active$")
    public void Customer_with_code_is_not_active(String code) throws Throwable {
        customerSteps.isActiveSetTo(false, code);
    }

    @Then("^Body contains customer type with \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_customer_type_with_value(String atributeName, String value) throws Throwable {
        customerSteps.bodyContainsCustomerWith(atributeName, value);
    }
}