package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ALL_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;

import org.junit.experimental.categories.Category;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;


public class AllEndpointsParameterizedTests extends CommonTest {

    private static final String EXAMPLES = "/csv/all_endpoints/";


    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "checkingErrorCodesForGettingEntities.csv")
    @Category(Categories.SlowTests.class)
    @Jira("DPIM-72")
    public void checkingErrorCodesForGettingEntities(
            String endpointIndex,
            String limit,
            String cursor,
            String filter,
            String sort,
            String sortDesc
    ) {
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null);
        String endpoint = ALL_ENDPOINTS.get(Integer.valueOf(endpointIndex));
        getEntities(endpoint, params);
        responseCodeIs(SC_BAD_REQUEST);
        customCodeIs(CC_BAD_PARAMS);
    }
}
