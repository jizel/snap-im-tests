package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.ALL_ENDPOINTS;
import static travel.snapshot.dp.qa.junit.utils.DpEndpoints.READ_WRITE_ENDPOINTS;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Collections;
import java.util.Map;


class AllEndpointsParameterizedTests extends CommonTest {

    private static final String EXAMPLES = "/csv/all_endpoints/";
    private static final String EMPTY_PARAM_VALUE = "";


    @ParameterizedTest
    @CsvFileSource(resources = EXAMPLES + "checkingErrorCodesForGettingEntities.csv")
    @Tag(SLOW_TEST)
    @Jira("DPIM-72")
    void checkingErrorCodesForGettingEntities(
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

    @ParameterizedTest
    @ValueSource(strings = {"limit", "cursor", "filter", "sort", "sortDesc"})
    void emptyGetParamsAreIgnored(String param) {
        Map<String, String> queryParams = Collections.singletonMap(param, EMPTY_PARAM_VALUE);
        READ_WRITE_ENDPOINTS.forEach(endpoint -> getEntities(endpoint, queryParams)
                .then()
                .statusCode(SC_OK));

    }

}
