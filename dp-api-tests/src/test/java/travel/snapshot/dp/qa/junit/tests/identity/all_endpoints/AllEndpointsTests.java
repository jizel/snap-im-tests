package travel.snapshot.dp.qa.junit.tests.identity.all_endpoints;

import lombok.extern.java.Log;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_ETAG;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ALL_ENDPOINTS;

@Log
@RunWith(SerenityRunner.class)
public class AllEndpointsTests extends CommonTest {

    @Test
    public void getRemoveNonExistentEntity() {
        ALL_ENDPOINTS.forEach(endpoint -> {
            log.info("Endpoint is " + endpoint);
            commonHelpers.getEntity(endpoint, NON_EXISTENT_ID);
            responseIsEntityNotFound();
            commonHelpers.deleteEntityWithEtag(endpoint, NON_EXISTENT_ID, DEFAULT_SNAPSHOT_ETAG);
            responseIsEntityNotFound();
        });
    }

    @Test
    public void sendEmptyPostRequest() {
        ALL_ENDPOINTS.forEach(endpoint -> {
            log.info("Endpoint is " + endpoint);
            commonHelpers.sendBlankPost(endpoint, "identity");
            responseCodeIs(SC_UNPROCESSABLE_ENTITY);
            customCodeIs(CC_SEMANTIC_ERRORS);
        });
    }
}
