package travel.snapshot.dp.qa.easyTests.tests.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.qa.easyTests.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.easyTests.loaders.YamlLoader.selectExamplesForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.qa.easyTests.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.easyTests.tests.common.Common;

import java.util.List;
import java.util.Map;


/**
 * Sample customer tests using YAML data
 */


public class YamlCustomer extends Common {

    //    Get EntitiesLoader instance containing all test entity data
    private static final EntitiesLoader entitiesLoader = EntitiesLoader.getInstance();
    //    Load this test class specific test data
    private static Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "customer_tests.yaml"));


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() {
    }

    @Test
    public void createAllCustomersTest(){
        for(CustomerCreateDto customer: entitiesLoader.getCustomerDtos().values()) {
            customerSteps.followingCustomerIsCreated(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        }
    }

    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingYaml() throws Throwable {
        List<Map<String, String>> listOfCustomerComSubsExamples = selectExamplesForTest(testClassData, "checkErrorCodesForGettingListOfCustomerCommercialSubscriptions");
        CustomerCreateDto createdCustomer = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        for(Map<String, String> listOfCustomerComSubsExample : listOfCustomerComSubsExamples) {
//            TODO: move this into a function (or create new object urlParamsWithResponseValues or something?) There is gonna be a lot similar assignemts
            String limit = listOfCustomerComSubsExample.get("limit");
            String cursor = listOfCustomerComSubsExample.get("cursor");
            String filter = listOfCustomerComSubsExample.get("filter");
            String sort = listOfCustomerComSubsExample.get("sort");
            String sortDesc = listOfCustomerComSubsExample.get("sort_desc");
            String responseCode = listOfCustomerComSubsExample.get("response_code");
            String customCode = listOfCustomerComSubsExample.get("custom_code");
            customerSteps.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
            responseCodeIs(Integer.valueOf(responseCode));
            customCodeIs(Integer.valueOf(customCode));
        }
    }
}