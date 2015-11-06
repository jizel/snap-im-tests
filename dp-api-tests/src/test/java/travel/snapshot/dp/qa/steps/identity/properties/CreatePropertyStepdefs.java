package travel.snapshot.dp.qa.steps.identity.properties;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;

import java.util.List;
import travel.snapshot.dp.qa.model.Property;

/**
 * Created by sedlacek on 9/18/2015.
 */
public class CreatePropertyStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private PropertySteps propertySteps;
    
    // --- given ---
    
    @Given("^The following properties exist with random address and billing address$")
    public void The_following_properties_exist(List<Property> properties) throws Throwable {
        propertySteps.followingPropertiesExist(properties);
    }
    
    // --- when ---
    
    @When("^Property with code \"([^\"]*)\" exists$")
    public void Property_with_code_exists(String code) throws Throwable {
        propertySteps.getPropertyByCode(code);
    }
    
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
        propertySteps.getPropertyByID("nonexistent_id");
    }
    
    @When("^List of properties exists with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter empty and sort empty$")
    public void List_of_properties_exists_with_limit_and_cursor_and_filter_empty_and_sort_empty(String limit, String cursor) throws Throwable {
        propertySteps.listOfPropertiesExistsWith(limit, cursor);
    }
    
    @When("^Property is created with random address and billing address$")
    public void property_is_created(List<Property> properties) throws Throwable {
        propertySteps.followingPropertyIsCreated(properties.get(0));
    }
    
    @When("^Property with code \"([^\"]*)\" is deleted$")
    public void Property_with_code_is_deleted(String code) throws Throwable {
        propertySteps.deletePropertyWithCode(code);
    }
    
    @When("^Nonexistent property id is deleted$")
    public void Nonexistent_property_id_is_deleted() throws Throwable {
        propertySteps.deletePropertyById("nonexistent_id");
    }

    
    // --- then ---
    
    @Then("^Body contains property with attribute \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyContainsPropertyWith(atributeName);
    }
    
    @Then("^Body contains property with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_property_with_attribute_value(String atributeName, String value) throws Throwable {
        propertySteps.bodyContainsPropertyWith(atributeName, value);
    }
    
    @Then("^Body does not contain property with attribute \"([^\"]*)\"$")
    public void Body_does_not_contain_property_with_attribute(String atributeName) throws Throwable {
        propertySteps.bodyDoesNotContainPropertyWith(atributeName);
    }
    
    @Then("^There are (\\d+) properties returned$")
    public void There_are_properties_returned(int count) throws Throwable {
        propertySteps.numberOfPropertiesIsInResponse(count);
    }
    
    // --- and ---
    
    @And("^Property with same id doesn't exist$")
    public void Property_with_same_id_doesn_t_exist() throws Throwable {
        propertySteps.propertyIdInSessionDoesntExist();
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
//    @When("^Customer with code \"([^\"]*)\" is activated$")
//    public void Customer_with_code_is_activated(String code) throws Throwable {
//        propertySteps.activateCustomerWithCode(code);
//    }
//
//    @Then("^Customer with code \"([^\"]*)\" is active$")
//    public void Customer_with_code_is_active(String code) throws Throwable {
//        propertySteps.isActiveSetTo(true, code);
//    }
//
//    @When("^Customer with code \"([^\"]*)\" is inactivated$")
//    public void Customer_with_code_is_inactivated(String code) throws Throwable {
//        propertySteps.inactivateCustomerWithCode(code);
//    }
//
//    @Then("^Customer with code \"([^\"]*)\" is not active$")
//    public void Customer_with_code_is_not_active(String code) throws Throwable {
//        propertySteps.isActiveSetTo(false, code);
//    }
//
//    @When("^Customer with code \"([^\"]*)\" is updated with data if updated before$")
//    public void Customer_with_code_is_updated_with_data_if_updated_before(String code, List<Customer> customers) throws Throwable {
//        propertySteps.updateCustomerWithCodeIfUpdatedBefore(code, customers.get(0));
//    }
    
}
