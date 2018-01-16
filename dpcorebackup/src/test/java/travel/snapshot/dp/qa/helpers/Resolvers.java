package travel.snapshot.dp.qa.helpers;

import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.serenity.applications.ApplicationsSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.UUID;

public class Resolvers {


    @Steps
    private PropertySteps propertySteps;

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private ApplicationsSteps applicationsSteps;

    @Step
    public UUID resolveEntityName(String type, String name) {
        switch (type) {
            case "customer":     return customerSteps.resolveCustomerId(name);
            case "property":     return propertySteps.resolvePropertyId(name);
            case "property set": return propertySetSteps.resolvePropertySetId(name);
            case "user":         return usersSteps.resolveUserId(name);
            case "application":  return applicationsSteps.resolveApplicationId(name);
        }
        return null;
    }
}