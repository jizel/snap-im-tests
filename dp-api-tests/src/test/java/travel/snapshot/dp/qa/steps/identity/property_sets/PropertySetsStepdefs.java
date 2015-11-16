package travel.snapshot.dp.qa.steps.identity.property_sets;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.PendingException;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Property;
import travel.snapshot.dp.qa.model.PropertySet;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class PropertySetsStepdefs {

    @Steps
    private PropertySetSteps steps;

    @Steps
    private CustomerSteps customerSteps;

    @Given("^The following property sets exist for customer with code \"([^\"]*)\"$")
    public void The_following_property_set_exist_for_customer_with_code(String customerCode, List<PropertySet> propertySets) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        steps.followingPropertySetsExistForCustomer(propertySets, c);
    }

    @Given("^All property sets are deleted for customers with codes: (.*)$")
    public void All_property_sets_are_deleted_for_customers_c_t_c_t(List<String> customerCodes) throws Throwable {
        List<Customer> customers = customerSteps.getCustomersForCodes(customerCodes);
        steps.deleteAllPropertySetsForCustomer(customers);
    }

    @When("^List of property sets is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_property_sets_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(@Transform(NullEmptyStringConverter.class) String limit,
                                                                                                     @Transform(NullEmptyStringConverter.class) String cursor,
                                                                                                     @Transform(NullEmptyStringConverter.class) String filter,
                                                                                                     @Transform(NullEmptyStringConverter.class) String sort,
                                                                                                     @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        steps.listOfPropertySetsIsGotWith(limit, cursor, filter, sort, sortDesc);

    }

    @Then("^There are property sets with following names returned in order: (.*)$")
    public void There_are_property_sets_with_following_names_returned_in_order(List<String> names) throws Throwable {
        steps.propertySetNamesAreInResponseInOrder(names);
    }

    @When("^Property set is created for customer with code \"([^\"]*)\"$")
    public void Property_set_is_created_for_customer_with_code(String customerCode, List<PropertySet> propertySets) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        steps.followingPropertySetIsCreatedForCustomer(c, propertySets.get(0));
    }

    @When("^Property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" is deleted$")
    public void Property_set_with_name_for_customer_with_code_is_deleted(String propertySetName, String customerCode) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        steps.propertySetWithNameForCustomerIsDeleted(c, propertySetName);
    }

    @Then("^Property set with same id doesn't exist$")
    public void Property_set_with_same_id_doesn_t_exist() throws Throwable {
        steps.propertySetIdInSessionDoesntExist();
    }

    @When("^Nonexistent property set id is deleted$")
    public void Nonexistent_property_set_id_is_deleted() throws Throwable {
        steps.deletePropertySetWithId("nonexistent");
    }

    @Then("^There are (\\d+) property sets returned$")
    public void There_are_returned_property_sets_returned(int count) throws Throwable {
        steps.numberOfEntitiesInResponse(PropertySet.class, count);
    }
}
