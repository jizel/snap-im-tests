package travel.snapshot.dp.qa.junit.tests.common;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.cucumber.serenity.authorization.AuthorizationSteps;
import travel.snapshot.dp.qa.junit.tests.Categories;

/**
 * Non destructive smoke tests.
 *
 * Test classes extending this class use keycloak authorization and clean everything they made so they can be
 * performed on various environments without deleting whole DB.
 */
@RunWith(SerenityRunner.class)
@Category(Categories.Authorization.class)
public class CommonProductionTest extends CommonTest {

    private final AuthorizationSteps authorizationSteps = new AuthorizationSteps();
    private String clientId = propertiesHelper.getProperty("client.id");
    private String clientSecret = propertiesHelper.getProperty("client.secret");
    private String userName = propertiesHelper.getProperty("user.name");
    private String userPassword = propertiesHelper.getProperty("user.password");


    @Before
    public void setUp() {
        authorizationSteps.getToken(userName, userPassword, clientId, clientSecret);
    }

    @After
    public void cleanUp() throws Throwable {
    }
}

