package travel.snapshot.dp.qa.junit.tests.common;

import net.serenitybdd.core.Serenity;
import org.junit.After;
import org.junit.Before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_NAME;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.ENTITIES_TO_DELETE;

/**
 * Created by ofayans on 7/27/17.
 */
public class CommonSmokeTest extends CommonTest {
    protected final Map<String, Object> testClient1 = entitiesLoader.getClients().get("client1");

    @Before
    public void setUp() throws Throwable {
        // To clean after normal tests we first remove all test entities
        dbStepDefs.defaultEntitiesAreDeleted();
        dbStepDefs.defaultEntitiesAreCreated();
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

