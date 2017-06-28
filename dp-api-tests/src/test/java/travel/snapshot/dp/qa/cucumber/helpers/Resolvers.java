package travel.snapshot.dp.qa.cucumber.helpers;

import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.cucumber.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

public class Resolvers {


    @Steps
    private PropertySteps propertySteps;

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private UsersSteps usersSteps;

    public String resolveEntityName(String type, String name) {
        switch (type) {
            case "customer":     return customerSteps.resolveCustomerId(name);
            case "property":     return propertySteps.resolvePropertyId(name);
            case "property set": return propertySetSteps.resolvePropertySetId(name);
            case "user":         return usersSteps.resolveUserId(name);
        }
        return null;
    }
}