package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.helpers.AddressUtils.createRandomAddress;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * GET, filtering, sorting etc. tests for Customer entity. 50+ customers created for testing (pagination)
 */
@Tag(SLOW_TEST)
public class CustomerGetTests extends CommonTest{

    private static final String FILTERING_CUSTOMERS_EXAMPLES = "/csv/customers/filteringCustomers.csv";

    @BeforeAll
    static void createTestCustomers() throws Exception {
//        Create 50+ test customers but only once for all tests!
        DbStepDefs dbStepDefs = new DbStepDefs();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
        range(0, 52).forEachOrdered(n -> {
            testCustomer1.setName(String.format("customer_%d", n));
            testCustomer1.setEmail(String.format("customer-%d@snapshot.travel", n));
            testCustomer1.setWebsite(String.format("http://www.customer%d.snapshot.travel", n));
            testCustomer1.setId(null);
            entityIsCreated(testCustomer1);
        });
        // The following is needed to test customer filtering/sorting by address.country DPIM-116
        AddressDto address = createRandomAddress(5, 5, 6, "CZ", null);
        testCustomer1.setAddress(address);
        testCustomer1.setVatId("CZ123456789");
        testCustomer1.setName("customer_52");
        testCustomer1.setEmail("customer-52@snapshot.travel");
        testCustomer1.setWebsite("http://www.customer52.snapshot.travel");
        testCustomer1.setId(null);
    }

    @Override
    @BeforeEach
    public void setUp() {
        // Override CommonTest setup and don't delete all created customers
    }


    @ParameterizedTest
    @CsvFileSource(resources = FILTERING_CUSTOMERS_EXAMPLES)
    void filteringCustomers(String limit, String cursor, Integer returned, Integer total, String filter, String sort, String sortDesc, String linkHeader) {
        getEntities(CUSTOMERS_PATH, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CustomerDto.class, returned);
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
        if (! linkHeader.equals("/null")) {
            headerIs("Link", linkHeader);
        }
    }

}
