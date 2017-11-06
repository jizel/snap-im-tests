package travel.snapshot.dp.qa.junit.tests.identity.partners;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;

import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResourceFile;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.sendPostWithBody;

@RunWith(JUnitParamsRunner.class)
public class ParameterizedPartnerTests extends CommonTest{
    private static final String EXAMPLES = "src/test/resources/csv/partners/";

    @Test
    @FileParameters(EXAMPLES + "checkingErrorCodesForCreatingPartners.csv")
    public void checkingErrorCodesForCreatingPartners(String filepath) throws IOException {
        sendPostWithBody(PARTNERS_PATH, parseResourceFile(System.getProperty("user.dir") + filepath))
                .then()
                .statusCode(SC_UNPROCESSABLE_ENTITY)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_SEMANTIC_ERRORS));
    }
}
