package travel.snapshot.dp.qa.easyTests.tests.customers;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import travel.snapshot.dp.qa.easyTests.tests.common.Common;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.easyTests.converters.CustomerConverter;
import travel.snapshot.dp.qa.easyTests.loaders.YamlLoader;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import static travel.snapshot.dp.qa.easyTests.converters.helpers.ConverterHelper.selectExamplesForTest;

import java.util.Map;
import java.util.List;


/**
 * Created by zelezny on 5/16/2017.
 */


public class YamlCustomer extends Common {

    private static Helpers helpers = new Helpers();
    private static CustomerSteps customerSteps = new CustomerSteps();
    private static final String BASE_PATH_CUSTOMERS = "/identity/customers";
    private static CustomerCreateDto customer = null;
    private CustomerDto createdCustomer = null;
    static Map<String, List<Map<String, String>>> testData = YamlLoader.loadExamplesYaml(String.format(YAML_DATA_PATH, "customer_tests.yaml"));

    public YamlCustomer() throws Throwable {
        super();
        Map<String, Object> data = YamlLoader.loadData(String.format(YAML_DATA_PATH, "customers.yaml"));
        customer = CustomerConverter.convert(data.get("customer1"));
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMERS);
    }

    @Before
    public static void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        helpers.customerIsCreated(customer);
    }

    @After
    public void cleanUp() {
        // If Converters are used once in the class, they stay registered and then mess up test using jsonObjects - those should not use converters (or should use specialized ones).
    }

    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingYaml() throws Throwable {
        List<Map<String, String>> examples = selectExamplesForTest(testData, "checkErrorCodesForGettingListOfCustomerCommercialSubscriptions");
        for (Map<String, String> example : examples) {
            String limit = example.get("limit");
            String cursor = example.get("cursor");
            String filter = example.get("filter");
            String sort = example.get("sort");
            String sortDesc = example.get("sort_desc");
            String responseCode = example.get("response_code");
            String customCode = example.get("custom_code");
            customerSteps.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
            responseCodeIs(Integer.valueOf(responseCode));
            customCodeIs(Integer.valueOf(customCode));
        }
    }
}
