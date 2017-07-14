package travel.snapshot.dp.qa.junit.tests.identity.customers;


import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Example of JUnit tests using Parameters. It can be used for multiline data driven testing where every line of data
 * is reported as separate test but it cannot be used with Serenity Runner. Also it needs data set per test.
 */


@RunWith(JUnitParamsRunner.class)
public class ParametersCustomer extends CommonTest {

    private CustomerDto createdCustomer = null;


    @Before
    public void setUp() throws Throwable {
        super.setUp();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdCustomer = customerHelpers.customerIsCreated(testCustomer1);
    }


//    Value of a string given to any annotation must be known at compile time. Hence String.format or concatenation cannot be used here and we must always give a "full path' to the test file.
    @FileParameters("src/test/resources/csv/customers/getCustomerCommSubscriptionErrorCodesTestExamples.csv")
    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingParams(String limit,
                                                                                          String cursor,
                                                                                          String filter,
                                                                                          String sort,
                                                                                          String sortDesc,
                                                                                          String responseCode,
                                                                                          String customCode) throws Throwable {
        customerHelpers.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
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
        customerHelpers.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
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



