package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_NAME;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ENTITIES_TO_DELETE;

import net.serenitybdd.core.Serenity;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Non destructive smoke tests.
 *
 * Test classes extending this class use keycloak authorization and clean everything they made so they can be
 * performed on various environments without deleting whole DB.
 */
@RunWith(SerenityRunner.class)
public class CommonSmokeTest extends CommonTest {


    @Before
    public void setUp() throws Exception {
        // To clean after normal tests we first remove all test entities
        dbStepDefs.defaultEntitiesAreDeleted();
        dbStepDefs.defaultEntitiesAreCreated();
        Map<String, Object> testClient1 = entitiesLoader.getClients().get("client1");
        keycloakHelpers.createClient(testClient1);
        String clientId = (String) testClient1.get("clientId");
        String clientSecret = (String) testClient1.get("secret");
        authorizationHelpers.getToken(DEFAULT_SNAPSHOT_USER_NAME, DEFAULT_PASSWORD, clientId, clientSecret);
        Map<String, ArrayList<String>> thingsToDelete = new HashMap<>();
        Serenity.setSessionVariable(ENTITIES_TO_DELETE).to(thingsToDelete);
    }

    @After
    public void cleanUp() throws Throwable {
        dbStepDefs.removeCreatedEntities(Serenity.sessionVariableCalled(ENTITIES_TO_DELETE));
        dbStepDefs.defaultEntitiesAreDeleted();
    }
}
