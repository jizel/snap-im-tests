package travel.snapshot.dp.qa.steps;

import net.thucydides.core.annotations.Steps;

import cucumber.api.java.en.Given;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
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

    @Given("^All customerProperties are deleted from DB for customer id \"([^\"]*)\" and property code \"([^\"]*)\"$")
    public void All_customerProperties_are_deleted_from_DB_for_customer_code_and_property_code(String customerId, String propertyCode) throws Throwable {
        CustomerDto c = customerSteps.getCustomerById(customerId);
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        if (c != null && p != null) {
            dbSteps.deleteAllPropertyCustomersFromDb(c.getCustomerId(), p.getPropertyId());
        }

    }

    @Given("^Database is cleaned$")
    public void databaseIsCleaned() throws Throwable {
        dbSteps.cleanDatabase();
    }
}
