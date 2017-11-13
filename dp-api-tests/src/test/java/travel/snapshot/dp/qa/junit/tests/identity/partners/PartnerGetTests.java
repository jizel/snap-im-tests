package travel.snapshot.dp.qa.junit.tests.identity.partners;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;

import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Map;

/**
 * GET, filtering, sorting etc. tests for Partner entity. 50+ partners created for testing (pagination)
 */
@Category(Categories.SlowTests.class)
public class PartnerGetTests extends CommonTest{

    private static final String EXAMPLES = "/csv/partners/";
    private static DbStepDefs dbStepDefs;

    @BeforeAll
    public static void createTestPartners() throws Exception {
//        Create 50+ test partners but only once for all tests!
        dbStepDefs = new DbStepDefs();
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
        range(0, 52).forEachOrdered(n -> {
            testPartner1.setName(String.format("partner_%d", n));
            testPartner1.setNotes(String.format("Notes %d", n));
            testPartner1.setEmail(String.format("partner-%d@snapshot.travel", n));
            testPartner1.setWebsite(String.format("http://www.partner%d.snapshot.travel", n));
            testPartner1.setId(null);
            entityIsCreated(testPartner1);
        });
    }

    @Override
    @BeforeEach
    public void setUp() {
        // Override CommonTest setup and don't delete all created partners
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "gettingListOfPartners.csv")
    public void gettingListOfPartners(String limit, String cursor, Integer returned, Integer total, String linkHeader) {
        Map<String, String> params = QueryParams.builder()
                .limit(limit)
                .cursor(cursor)
                .build();
        getEntities(PARTNERS_PATH, params);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(PartnerDto.class, returned);
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
        if (! linkHeader.equals("/null")) {
            headerContains("Link", linkHeader);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfPartners.csv")
    public void filteringListOfPartners(String limit, String cursor, Integer returned, Integer total, String filter) {
        Map<String, String> params = QueryParams.builder()
                .limit(limit)
                .cursor(cursor)
                .filter(filter)
                .build();
        getEntities(PARTNERS_PATH, params);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(PartnerDto.class, returned);
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
    }
}