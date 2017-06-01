package travel.snapshot.dp.qa.easyTests.tests.customers;


import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.easyTests.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.easyTests.loaders.YamlLoader;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.steps.DbStepDefs;

import java.util.Map;

/**
 * Example of JUnit tests using Parameters. It can be used for multiline data driven testing where every line of data
 * is reported as separate test but it cannot be used with Serenity Runner. Also it needs data set per test.
 */


@RunWith(JUnitParamsRunner.class)
public class ParametersCustomer extends BasicSteps {

    private static CustomerHelpers helpers = new CustomerHelpers();
    private static CustomerSteps customerSteps = new CustomerSteps();
    private static DbStepDefs dbStepDefs = new DbStepDefs();
    private static String YAML_DATA_PATH = "src/test/resources/yaml/%s";
    private static final String BASE_PATH_CUSTOMERS = "/identity/customers";
    Map<String, Object> data = YamlLoader.loadData(String.format(YAML_DATA_PATH, "customers.yaml"));
    private static final EntitiesLoader entitiesLoader = EntitiesLoader.getInstance();
    private CustomerDto createdCustomer = null;

    public ParametersCustomer() throws Throwable {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMERS);
    }


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        helpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
    }

    @After
    public void cleanUp() {
        // If Converters are used once in the class, they stay registered and then mess up test using jsonObjects - those should not use converters (or should use specialized ones).
    }

    @Test
    @Parameters({
        "/null, -1,/null,/null,/null,400,40002",
        ",-1,/null,/null,/null,400,40002",
        "/null,text,/null,/null,/null,400,40002",
        ",text,/null,/null,/null,400,40002",
        "-1,,/null,/null,/null,400,40002",
        "-1,/null,/null,/null,/null,400,40002",
        "201,/null,/null,/null,/null,400,40002",
        "21474836470,/null,/null,/null,/null,400,40002",
        "text,,/null,/null,/null,400,40002",
        "text,/null,/null,/null,/null,400,40002",
        "10,-1,/null,/null,/null,400,40002",
        "text,0,/null,/null,/null,400,40002",
        "10,text,/null,/null,/null,400,40002",
        "10,0,/null,commercial_subscription_id,commercial_subscription_id,400,40002",
        "10,0,/null,/null,nonexistent,400,40002",
        "10,0,/null,nonexistent,/null,400,40002",
        "10,0,code==,/null,/null,400,40002"
    })
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingParams(String limit,
                                                                                          String cursor,
                                                                                          String filter,
                                                                                          String sort,
                                                                                          String sortDesc,
                                                                                          String responseCode,
                                                                                          String customCode) throws Throwable {
        customerSteps.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    @Test
    @Parameters(method = "paramValues")
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingParams2(String limit,
                                                                                          String cursor,
                                                                                          String filter,
                                                                                          String sort,
                                                                                          String sortDesc,
                                                                                          String responseCode,
                                                                                          String customCode) throws Throwable {
        customerSteps.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }


    //help
    private Object[] paramValues(){
        return new Object[]{
                "/null, -1,/null,/null,/null,400,40002",
                ",-1,/null,/null,/null,400,40002",
                "/null,text,/null,/null,/null,400,40002",
                ",text,/null,/null,/null,400,40002",
                "-1,,/null,/null,/null,400,40002",
                "-1,/null,/null,/null,/null,400,40002",
                "201,/null,/null,/null,/null,400,40002",
                "21474836470,/null,/null,/null,/null,400,40002",
                "text,,/null,/null,/null,400,40002",
                "text,/null,/null,/null,/null,400,40002",
                "10,-1,/null,/null,/null,400,40002",
                "text,0,/null,/null,/null,400,40002",
                "10,text,/null,/null,/null,400,40002",
                "10,0,/null,commercial_subscription_id,commercial_subscription_id,400,40002",
                "10,0,/null,/null,nonexistent,400,40002",
                "10,0,/null,nonexistent,/null,400,40002",
                "10,0,code==,/null,/null,400,40002"
        };
    }
}



