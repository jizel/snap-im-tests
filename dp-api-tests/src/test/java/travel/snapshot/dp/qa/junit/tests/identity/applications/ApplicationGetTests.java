package travel.snapshot.dp.qa.junit.tests.identity.applications;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Map;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;

public class ApplicationGetTests extends CommonTest {
    private static final String EXAMPLES = "/csv/applications/";

    @BeforeAll
    public static void createTestApplications() {
        cleanDbAndLoadDefaultEntities();
        range(0, 52).forEachOrdered(n -> {
            testApplication1.setId(null);
            testApplication1.setName(String.format("Test application %d", n));
            testApplication1.setDescription(String.format("Test application %d", n*2));
            testApplication1.setWebsite(String.format("https://testapp%d.com", n));
            entityIsCreated(testApplication1);
        });
    }

    @Override
    @BeforeEach
    public void setUp() {}

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "gettingListOfApplications.csv")
    void gettingListOfApplications(String limit, String cursor, String returned, String total, String linkHeader) {
        Map<String, String> params = QueryParams.builder().limit(limit).cursor(cursor).build();
        getEntities(APPLICATIONS_PATH, params).then().statusCode(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, Integer.valueOf(returned));
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
        if (! linkHeader.equals("/null")) {
            headerContains("Link", linkHeader);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfApplications.csv")
    void filteringListOfApplications(String limit, String cursor, String returned, String total, String filter, String sort, String sort_desc) {
        Map<String, String> params = QueryParams.builder()
                .limit(limit)
                .cursor(cursor)
                .filter(filter)
                .sort(sort)
                .sortDesc(sort_desc)
                .build();
        getEntities(APPLICATIONS_PATH, params).then().statusCode(SC_OK);
        numberOfEntitiesInResponse(ApplicationDto.class, Integer.valueOf(returned));
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
    }
}
