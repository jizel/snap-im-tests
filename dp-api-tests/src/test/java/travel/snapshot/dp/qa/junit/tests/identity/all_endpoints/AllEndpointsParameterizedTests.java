package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.Categories;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ALL_ENDPOINTS;

@RunWith(JUnitParamsRunner.class)
public class AllEndpointsParameterizedTests extends CommonTest {

    private static final String EXAMPLES = "src/test/resources/csv/all_endpoints/";

    @FileParameters(EXAMPLES + "checkingErrorCodesForGettingEntities.csv")
    @Test
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
        commonHelpers.getEntities(endpoint, params);
        responseCodeIs(Integer.valueOf(SC_BAD_REQUEST));
        customCodeIs(Integer.valueOf(CC_BAD_PARAMS));
    }
}
