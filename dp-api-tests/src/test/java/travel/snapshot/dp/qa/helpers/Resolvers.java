package travel.snapshot.dp.qa.helpers;

import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;

public class Resolvers {


    @Steps
    private PropertySteps propertySteps;

    @Steps
    private PropertySetSteps propertySetSteps;

    @Steps
    private CustomerSteps customerSteps;

    public String resolveEntityName(String type, String name) {
        switch (type) {
            case "customer":     return customerSteps.resolveCustomerId(name);
            case "property":     return propertySteps.resolvePropertyId(name);
            case "property set": return propertySetSteps.resolvePropertySetId(name);
        }
        return null;
    }
}