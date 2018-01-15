package travel.snapshot.dp.qa.junit.tests.identity.application_versions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Map;
import java.util.UUID;

import static java.util.stream.IntStream.range;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerContains;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.headerIs;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;

public class ApplicationVersionGetTests extends CommonTest {
    private static final String EXAMPLES = "/csv/application_versions/";

    @BeforeAll
    public static void createTestApplications() {
        cleanDbAndLoadDefaultEntities();
        range(0, 52).forEachOrdered(n -> {
            testApplication1.setId(null);
            testApplication1.setName(String.format("Test application %d", n));
            testApplication1.setDescription(String.format("Test application %d", n*2));
            testApplication1.setWebsite(String.format("https://testapp%d.com", n));
            UUID appId = entityIsCreated(testApplication1);
            testAppVersion1.setId(null);
            testAppVersion1.setApplicationId(appId);
            testAppVersion1.setName(String.format("Test application %d", n));
            testAppVersion1.setDescription(String.format("Test application %d", n*2));
            entityIsCreated(testAppVersion1);
        });
    }

    @Override
    @BeforeEach
    public void setUp() {}

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "gettingListOfApplicationVersions.csv")
    void gettingListOfApplicationVersions(String limit, String cursor, String returned, String total, String linkHeader) {
        Map<String, String> params = QueryParams.builder().limit(limit).cursor(cursor).build();
        getEntities(APPLICATION_VERSIONS_PATH, params).then().statusCode(SC_OK);
        numberOfEntitiesInResponse(ApplicationVersionDto.class, Integer.valueOf(returned));
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
        if (! linkHeader.equals("/null")) {
            headerContains("Link", linkHeader);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "filteringListOfApplicationVersions.csv")
    void filteringListOfApplicationVersions(String limit, String cursor, String returned, String total, String filter, String sort, String sort_desc) {
        Map<String, String> params = QueryParams.builder()
                .limit(limit)
                .cursor(cursor)
                .filter(filter)
                .sort(sort)
                .sortDesc(sort_desc)
                .build();
        getEntities(APPLICATION_VERSIONS_PATH, params).then().statusCode(SC_OK);
        numberOfEntitiesInResponse(ApplicationVersionDto.class, Integer.valueOf(returned));
        headerIs(TOTAL_COUNT_HEADER, String.valueOf(total));
    }
}
