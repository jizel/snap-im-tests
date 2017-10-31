package travel.snapshot.dp.qa.junit.tests.identity.commercial_subscriptions;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntities;

@RunWith(JUnitParamsRunner.class)
public class ParameterizedCommercialSubscriptionTests extends CommonTest{
    private static final String EXAMPLES = "src/test/resources/csv/commercial_subscriptions/";


    @FileParameters(EXAMPLES + "errorCodesForGettingListOfApplicationsCommercialSubscriptions.csv")
    @Test
    public void errorCodesForGettingListOfApplicationsCommercialSubscriptions(
            String limit,
            String cursor,
            String filter,
            String sort,
            String sortDesc
    ) {
        filter = filter + " and application_id==" + String.valueOf(DEFAULT_SNAPSHOT_APPLICATION_ID);
        Map<String, String> params = buildQueryParamMapForPaging(limit, cursor, filter, sort, sortDesc, null);
        getEntities(COMMERCIAL_SUBSCRIPTIONS_PATH, params)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_BAD_PARAMS));
    }

}
