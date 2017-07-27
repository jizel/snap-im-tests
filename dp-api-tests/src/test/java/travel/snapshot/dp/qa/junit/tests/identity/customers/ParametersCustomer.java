package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.cucumber.helpers.AddressUtils;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Example of JUnit tests using Parameters. It can be used for multiline data driven testing where every line of data
 * is reported as separate test but it cannot be used with Serenity Runner. Also it needs data set per test.
 */


@RunWith(JUnitParamsRunner.class)
public class ParametersCustomer extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/customers/";
    private static AddressDto address;
    private static String vatId;
    private static final CustomerCreateDto customerVatTests = testCustomer1;


    @Before
    public void setUp() throws Throwable {
        super.setUp();
        address = testCustomer1.getAddress();
        vatId = testCustomer1.getVatId();
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        testCustomer1.setVatId(vatId);
        testCustomer1.setAddress(address);
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }


    //    Value of a string given to any annotation must be known at compile time. Hence String.format or concatenation cannot be used here and we must always give a "full path' to the test file.
    @FileParameters(EXAMPLES + "getCustomerCommSubscriptionErrorCodesTestExamples.csv")
    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingParams(String limit,
                                                                                          String cursor,
                                                                                          String filter,
                                                                                          String sort,
                                                                                          String sortDesc,
                                                                                          String responseCode,
                                                                                          String customCode) throws Throwable {
        CustomerDto createdCustomer = customerHelpers.customerIsCreated(testCustomer1);
        customerHelpers.listOfCustomerCommSubscriptionsIsGotWith(createdCustomer.getId(), limit, cursor, filter, sort, sortDesc);
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    @FileParameters(EXAMPLES + "validateCustomerRegionsBelongToCorrectCountry.csv")
    @Test
    @Category(Categories.SmokeTests.class)
    public void validateCustomerRegionsBelongToCorrectCountry(String country,
                                                              String region,
                                                              String vatId) {
        AddressDto address = AddressUtils.createRandomAddress(5, 5, 6, country, region);
        customerVatTests.setAddress(address);
        customerVatTests.setVatId(vatId);
        customerHelpers.createRandomCustomer(customerVatTests);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("vat_id", vatId);
    }

    @FileParameters(EXAMPLES + "validateInvalidVatId.csv")
    @Test
    @Category(Categories.SmokeTests.class)
    public void validateCustomerHasInvalidVatId(String country, String vatId) throws Throwable {
        AddressDto address = AddressUtils.createRandomAddress(5, 5, 6, country, null);
        customerVatTests.setAddress(address);
        customerVatTests.setVatId(vatId);
        customerHelpers.createRandomCustomer(customerVatTests);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
    }

    @FileParameters(EXAMPLES + "validateValidVatId.csv")
    @Test
    @Category(Categories.SmokeTests.class)
    public void validateCustomerHasValidVatId(String country, String vatId) throws Throwable {
        AddressDto address = AddressUtils.createRandomAddress(5, 5, 6, country, null);
        customerVatTests.setAddress(address);
        customerVatTests.setVatId(vatId);
        customerHelpers.createRandomCustomer(customerVatTests);
        responseCodeIs(SC_CREATED);
    }
}



