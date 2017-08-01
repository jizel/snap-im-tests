package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_NAME;

import org.junit.After;
import org.junit.Before;

import java.util.Map;


/*
  A generic class for tests that use authorization workflow.
  This class invokes keycloak-specific steps at setUp and cleanUp to prepare keycloak clients
  corresponding to the context application versions.

 */
public abstract class CommonAuthorizationTest extends CommonTest {

    protected final Map<String, Object> testClient1 = entitiesLoader.getClients().get("client1");
    protected final Map<String, Object> testClient2 = entitiesLoader.getClients().get("client2");
    protected final Map<String, Object> testClient3 = entitiesLoader.getClients().get("client3");

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        keycloakHelpers.createClient(testClient1);
        String clientId = (String) testClient1.get("clientId");
        String clientSecret = (String) testClient1.get("secret");
        authorizationHelpers.getToken(DEFAULT_SNAPSHOT_USER_NAME, DEFAULT_PASSWORD, clientId, clientSecret);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        keycloakHelpers.deleteClient((String) testClient1.get("id"));
    }

    //    Help methods
}
