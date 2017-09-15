package travel.snapshot.dp.qa.junit.tests.identity.customers;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.AddressUtils.createRandomAddress;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Example of JUnit tests using Parameters. It can be used for multiline data driven testing where every line of data
 * is reported as separate test but it cannot be used with Serenity Runner. Also it needs data set per test.
 */


@RunWith(JUnitParamsRunner.class)
@Category(Categories.SlowTests.class)
public class ParametersCustomer extends CommonTest {

    private static final String CUSTOMER_EXAMPLES = "src/test/resources/csv/customers/";
    private static final String COMM_SUBSCRIPTIONS_ERROR_EXAMPLES = CUSTOMER_EXAMPLES + "getCustomerCommSubscriptionErrorCodesTestExamples.csv";
    private static final String CORRECT_REGION_EXAMPLES = CUSTOMER_EXAMPLES + "validateCustomerRegionsBelongToCorrectCountry.csv";
    private static final String INVALID_REGIONS_EXAMPLES = CUSTOMER_EXAMPLES + "invalidRegions.csv";
    private static final String INVALID_VAT_ID_EXAMPLES = CUSTOMER_EXAMPLES + "validateInvalidVatId.csv";
    private static final String VALID_VAT_ID_EXAMPLES = CUSTOMER_EXAMPLES + "validateValidVatId.csv";
    private static final String FILTERING_CUSTOMERS_EXAMPLES = CUSTOMER_EXAMPLES + "filteringCustomers.csv";


    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }


//    TODO: This test should be rather defined in CustomerCommercialSubscriptionTests class but since it requires JUnit params
    @FileParameters(COMM_SUBSCRIPTIONS_ERROR_EXAMPLES)
    @Test
    public void checkErrorCodesForGettingListOfCustomerCommercialSubscriptionsUsingParams(String limit,
                                                                                          String cursor,
                                                                                          String filter,
                                                                                          String sort,
                                                                                          String sortDesc,
                                                                                          String responseCode,
                                                                                          String customCode) throws Throwable {
        UUID createdCustomerId = commonHelpers.entityIsCreated(testCustomer1);
        commonHelpers.getRelationships(CUSTOMERS_PATH, createdCustomerId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE,
                buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    @FileParameters(CORRECT_REGION_EXAMPLES)
    @Test
    public void validateCustomerRegionsBelongToCorrectCountry(String country,
                                                              String region,
                                                              String vatId) {
        AddressDto address = createRandomAddress(5, 5, 6, country, region);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId(vatId);
        customerHelpers.createRandomCustomer(testCustomer1);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("vat_id", vatId);
    }

    @FileParameters(INVALID_VAT_ID_EXAMPLES)
    @Test
    public void validateCustomerHasInvalidVatId(String country, String vatId) throws Throwable {
        AddressDto address = createRandomAddress(5, 5, 6, country, null);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId(vatId);
        customerHelpers.createRandomCustomer(testCustomer1);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
    }

    @FileParameters(VALID_VAT_ID_EXAMPLES)
    @Test
    public void validateCustomerHasValidVatId(String country, String vatId) throws Throwable {
        AddressDto address = createRandomAddress(5, 5, 6, country, null);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId(vatId);
        customerHelpers.createRandomCustomer(testCustomer1);
        responseCodeIs(SC_CREATED);
    }

    @FileParameters(INVALID_REGIONS_EXAMPLES)
    @Test
    public void checkErrorCodesForRegions(String country, String region) throws Exception {
        AddressDto address = createRandomAddress(10, 10, 5,country, region);
        testCustomer1.setAddress(address);
        commonHelpers.createEntity(CUSTOMERS_PATH, testCustomer1);
        responseIsReferenceDoesNotExist();
        bodyContainsEntityWith("message", String.format("Reference does not exist. The entity Region with ID %s cannot be found.", region));
    }
}



