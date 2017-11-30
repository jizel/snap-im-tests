package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import static java.util.Arrays.stream;
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
import org.junit.jupiter.params.provider.MethodSource;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Stream;


class AllEndpointsParameterizedTests extends CommonTest {

    private static final String EXAMPLES = "/csv/all_endpoints/";
    private static final String EMPTY_ARGS = "";


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
    @MethodSource("getQueryParamsMethods")
    void emptyGetParamsAreIgnored(Method method) {
        READ_WRITE_ENDPOINTS.forEach(endpoint -> getEntities(endpoint, getParamsFromMethod(method))
                .then()
                .statusCode(SC_OK));

    }

    /**
     * Gets all QueryParams (filter, cursor, sort...) as QueryParamsBuilder methods. Can be used for invoking the
     * methods on the builder object (separately) to test all the queryParams (separately)
     */
    private static Stream<Method> getQueryParamsMethods() {
        try {
            final Method buildMethod = QueryParams.QueryParamsBuilder.class.getDeclaredMethod("build", null);
            final Method additionalParamsMethod = QueryParams.QueryParamsBuilder.class.getDeclaredMethod("additionalParams", Map.class);

            return stream(QueryParams.QueryParamsBuilder.class.getDeclaredMethods())
                    .filter(method -> !method.equals(buildMethod))
                    .filter(method -> !method.equals(additionalParamsMethod));
        } catch (Exception e) {
            throw new RuntimeException(e.getCause());
        }
    }

    private Map<String, String> getParamsFromMethod(Method method){
        try {
            QueryParams.QueryParamsBuilder queryParamsBuilder = QueryParams.builder();
            method.invoke(queryParamsBuilder, EMPTY_ARGS);

            return queryParamsBuilder.build();
        } catch (Exception e) {
            throw new RuntimeException("Exception thrown when invoking method " + method, e.getCause());
        }
    }

}
