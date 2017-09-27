package travel.snapshot.dp.qa.junit.tests.identity.customers;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * GET, filtering, sorting etc. tests for Customer entity. 50+ customers created for testing (pagination)
 */
@RunWith(JUnitParamsRunner.class)
@Category(Categories.SlowTests.class)
public class CustomerGetTests extends CommonTest{

    private static final String FILTERING_CUSTOMERS_EXAMPLES = "src/test/resources/csv/customers/filteringCustomers.csv";

    @BeforeClass
    public static void createTestCustomers() throws Exception {
//        Create 50+ test customers but only once for all tests!
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
            range(0, 52).forEachOrdered(n -> {
                testCustomer1.setName(String.format("customer_%d", n));
                testCustomer1.setEmail(String.format("customer-%d@snapshot.travel", n));
                testCustomer1.setWebsite(String.format("http://www.customer%d.snapshot.travel", n));
                testCustomer1.setId(null);
                commonHelpers.entityIsCreated(testCustomer1);
            });
    }

    @Override
    @Before
    public void setUp() {
        // Override CommonTest setup and don't delete all created customers
    }

    @FileParameters(FILTERING_CUSTOMERS_EXAMPLES)
    @Test
    public void filteringCustomers(String limit, String cursor, Integer returned, Integer total, String filter, String sort, String sortDesc, String linkHeader) {
        commonHelpers.getEntities(CUSTOMERS_PATH, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(CustomerDto.class, returned);
        headerIs("X-Total-Count", String.valueOf(total));
        headerIs("Link", transformNull(linkHeader));
    }
}
