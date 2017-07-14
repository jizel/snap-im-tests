package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.qa.junit.helpers.UrlParamsWithResponse.createUrlParams;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadYamlTables;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTest;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTestFromTable;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
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

    @Test
    public void createAllCustomersTest() {
        customerDtos.values().forEach(customer -> {
            customerHelpers.followingCustomerIsCreated(customer);
            responseCodeIs(SC_CREATED);
            bodyContainsEntityWith("name");
        });
//            TODO: Use existing/make new matchers for DTOs. Assert that createdCustomer has all the attributes the customer has
    }
}
