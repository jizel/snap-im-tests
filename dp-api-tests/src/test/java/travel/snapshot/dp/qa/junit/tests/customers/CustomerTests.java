package travel.snapshot.dp.qa.junit.tests.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
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
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.cucumber.helpers.AddressUtils;
import travel.snapshot.dp.qa.junit.helpers.UrlParamsWithResponse;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;

import java.util.List;
import java.util.Map;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class CustomerTests extends CommonTest {

    //    Load this test class specific test data
    private static Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "customer_tests.yaml"));
    private static Map<String, Map<String, List<String>>> testClassDataFromYamlTables = loadYamlTables(String.format(YAML_DATA_PATH, "customer_tests.yaml"));
    private static EntityNonNullMap<String, CustomerCreateDto> customerDtos = entitiesLoader.getCustomerDtos();


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() {
    }

    @Test
    public void createAllCustomersTest() {
        customerDtos.values().forEach(customer -> {
            customerHelpers.followingCustomerIsCreated(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has

    }

    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingYaml() throws Throwable {
        List<Map<String, String>> listOfCustomerComSubsExamples = selectExamplesForTest(testClassData, "checkErrorCodesForGettingListOfCustomerCommercialSubscriptions");
        CustomerDto createdCustomer = customerHelpers.customerIsCreated(testCustomer1);
        for (Map<String, String> listOfCustomerComSubsExample : listOfCustomerComSubsExamples) {
            UrlParamsWithResponse exampleParams = createUrlParams(listOfCustomerComSubsExample);
            String limit = exampleParams.getLimit();
            String cursor = exampleParams.getCursor();
            String filter = exampleParams.getFilter();
            String sort = exampleParams.getSort();
            String sortDesc = exampleParams.getSortDesc();
            String responseCode = exampleParams.getResponseCode();
            String customCode = exampleParams.getCustomCode();
            customerHelpers.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
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
        CustomerDto createdCustomer = customerHelpers.customerIsCreated(testCustomer1);
        listOfCustomerComSubsExamples.forEach( example -> {
            UrlParamsWithResponse exampleParams = createUrlParams(example);
            customerHelpers.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(),
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

    @Test
    public void validateCustomerHasValidVatId() throws Throwable {
        List<Map<String, String>> listOfExamples = selectExamplesForTestFromTable(testClassDataFromYamlTables, "validateValidVatId");
        listOfExamples.forEach( example -> {
            AddressDto address = AddressUtils.createRandomAddress(5, 5, 6, example.get("country"), null);
            testCustomer1.setAddress(address);
            String vatId = example.get("vatId");
            testCustomer1.setVatId(vatId);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("vat_id", vatId);
        });
    }

    @Test
    public void validateCustomerHasInvalidVatId() throws Throwable {
        List<Map<String, String>> listOfExamples = selectExamplesForTestFromTable(testClassDataFromYamlTables, "validateInvalidVatId");
        listOfExamples.forEach( example -> {
            AddressDto address = AddressUtils.createRandomAddress(5, 5, 6, example.get("country"), null);
            testCustomer1.setAddress(address);
            String vatId = example.get("vatId");
            testCustomer1.setVatId(vatId);
            responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        });
    }

    @Test
    public void validateCustomerRegionsBelongToCorrectCountry() throws Throwable {
        List<Map<String, String>> listOfExamples = selectExamplesForTestFromTable(testClassDataFromYamlTables, "validateCustomerRegionsBelongToCorrectCountry");
        listOfExamples.forEach( example -> {
            AddressDto address = AddressUtils.createRandomAddress(5, 5, 6, example.get("country"), example.get("region"));
            testCustomer1.setAddress(address);
            String vatId = example.get("vatId");
            testCustomer1.setVatId(vatId);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("vat_id", vatId);
        });
    }
}
