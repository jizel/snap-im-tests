package travel.snapshot.dp.qa.steps;

import net.thucydides.core.annotations.Steps;

import cucumber.api.java.en.Given;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Property;
import travel.snapshot.dp.qa.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;

public class DbStepDefs {

    @Steps
    private DbUtilsSteps dbSteps;
    @Steps
    private CustomerSteps customerSteps;
    @Steps
    private PropertySteps propertySteps;

    @Given("^All customerProperties are deleted from DB for customer code \"([^\"]*)\" and property code \"([^\"]*)\"$")
    public void All_customerProperties_are_deleted_from_DB_for_customer_code_and_property_code(String customerCode, String propertyCode) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        dbSteps.deleteAllPropertyCustomersFromDb(c.getCustomerId(), p.getPropertyId());
    }
}
