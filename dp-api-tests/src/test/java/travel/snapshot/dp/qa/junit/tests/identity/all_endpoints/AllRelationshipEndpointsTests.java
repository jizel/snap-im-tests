package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;
import static travel.snapshot.dp.qa.junit.utils.EndpointEntityMapping.endpointDtoMap;

import com.jayway.restassured.response.Response;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.common.CommonRelationshipsTest;

@Tag(SLOW_TEST)
class AllRelationshipEndpointsTests extends CommonRelationshipsTest {
    private static final String EXAMPLES = "/csv/relationships/";


    @Jira("DPIM-56")
    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "getParamsExamples.csv")
    void getAllFilteringSorting(String limit, String cursor, String filter, String sort, String sortDesc, String returned) throws Exception {
        // Create relationships for test
        constructAndCreateTestRelationshipsDtos();
        ALL_RELATIONSHIPS_ENDPOINTS.forEach(endpoint -> {
            getEntities(endpoint, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
            responseCodeIs(SC_OK);
            numberOfEntitiesInResponse(endpointDtoMap.get(endpoint), Integer.valueOf(returned));
        });
    }

    @Jira("DPIM-56")
    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "invalidGetParamsExamples.csv")
    void relationshipsFilteringTest(String limit, String cursor, String filter, String sort, String sortDesc) throws Exception {
        ALL_RELATIONSHIPS_ENDPOINTS.forEach(endpoint -> {
            Response response = getEntities(endpoint, buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null));
            responseCodeIs(SC_BAD_REQUEST);
            customCodeIs(CC_BAD_PARAMS);
        });
    }

}
