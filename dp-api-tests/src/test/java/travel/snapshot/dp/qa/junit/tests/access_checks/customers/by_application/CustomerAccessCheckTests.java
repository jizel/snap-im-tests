package travel.snapshot.dp.qa.junit.tests.access_checks.customers.by_application;


import net.serenitybdd.junit.runners.SerenityRunner;
import org.codehaus.jackson.map.annotate.JsonTypeResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.List;
import java.util.Map;

import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.*;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class CustomerAccessCheckTests extends CommonTest {

    //    Load this test class specific test data
    private static Map<String, Map<String, List<String>>> testClassDataFromYamlTables = loadYamlTables(String.format(YAML_DATA_PATH, "customer_tests.yaml"));


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        CustomerDto createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
        CustomerDto createdCustomer2 = customerHelpers.customerIsCreated(testCustomer2);
        ApplicationDto createdApp1 = applicationHelpers.applicationIsCreated(testApplication1);
        ApplicationDto createdApp2 = applicationHelpers.applicationIsCreated(testApplication2);
        applicationHelpers.grantAllPermissions(createdApp1.getId());
        applicationHelpers.grantAllPermissions(createdApp2.getId());
        UserDto createdUser = userHelpers.userIsCreated(testUser1);
        // Version with subscription
        commercialSubscriptionHelpers.commercialSubscriptionIsCreated(createdCustomer1.getId(), DEFAULT_PROPERTY_ID, createdApp1.getId());
        testAppVersion1.setApplicationId(createdApp1.getId());
        ApplicationVersionDto createdAppVersion1 = applicationVersionHelpers.applicationVersionIsCreated(testAppVersion1);
        // Version without subscription
        testAppVersion2.setApplicationId(createdApp2.getId());
        ApplicationVersionDto createdAppVersion2 = applicationVersionHelpers.applicationVersionIsCreated(testAppVersion2);
        // Non-commercial version
        testAppVersion3.setApplicationId(createdApp2.getId());
        testAppVersion3.setIsNonCommercial(true);
        propertyHelpers.relationExistsBetweenUserAndProperty(createdUser.getId(), DEFAULT_PROPERTY_ID, true);
    }

    @After
    public void cleanUp() {
    }

    @Test
    public void thereIsActiveCommercialSubscriptionLinkingToTheApplicationVersion() throws Throwable {
    }

}
