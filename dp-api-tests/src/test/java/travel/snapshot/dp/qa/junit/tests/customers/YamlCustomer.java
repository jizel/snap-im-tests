package travel.snapshot.dp.qa.junit.tests.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.qa.junit.helpers.UrlParamsWithResponse.createUrlParams;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadYamlTables;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTest;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTestFromTable;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.junit.helpers.UrlParamsWithResponse;
import travel.snapshot.dp.qa.junit.tests.common.Common;

import java.util.List;
import java.util.Map;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class YamlCustomer extends Common {

    //    Load this test class specific test data
    private static Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "customer_tests.yaml"));
    private static Map<String, Map<String, List<String>>> testClassDataFromYamlTables = loadYamlTables(String.format(YAML_DATA_PATH, "customer_tests.yaml"));


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() {
    }

    @Test
    public void createAllCustomersTest() {
        entitiesLoader.getCustomerDtos().values().forEach(customer -> {
            customerSteps.followingCustomerIsCreated(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has

    }

    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingYaml() throws Throwable {
        List<Map<String, String>> listOfCustomerComSubsExamples = selectExamplesForTest(testClassData, "checkErrorCodesForGettingListOfCustomerCommercialSubscriptions");
        CustomerDto createdCustomer = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        for (Map<String, String> listOfCustomerComSubsExample : listOfCustomerComSubsExamples) {
            UrlParamsWithResponse exampleParams = createUrlParams(listOfCustomerComSubsExample);
            String limit = exampleParams.getLimit();
            String cursor = exampleParams.getCursor();
            String filter = exampleParams.getFilter();
            String sort = exampleParams.getSort();
            String sortDesc = exampleParams.getSortDesc();
            String responseCode = exampleParams.getResponseCode();
            String customCode = exampleParams.getCustomCode();
            customerSteps.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
            responseCodeIs(Integer.valueOf(responseCode));
            customCodeIs(Integer.valueOf(customCode));
        }
    }

    /**
     *New approach example - using YAML "tables" to load test data.
     */
    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingYamlTables() throws Throwable {
        List<Map<String, String>> listOfCustomerComSubsExamples = selectExamplesForTestFromTable(testClassDataFromYamlTables, "checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingTable");
        CustomerDto createdCustomer = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));

        listOfCustomerComSubsExamples.forEach( example -> {
            UrlParamsWithResponse exampleParams = createUrlParams(example);
            customerSteps.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(),
                    exampleParams.getLimit(),
                    exampleParams.getCursor(),
                    exampleParams.getFilter(),
                    exampleParams.getSort(),
                    exampleParams.getSortDesc()
                    );
            responseCodeIs(Integer.valueOf(exampleParams.getResponseCode()));
            customCodeIs(Integer.valueOf(exampleParams.getCustomCode()));
        });
    }
}
