package travel.snapshot.qa.manager.tomcat;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.UnitTest;
import travel.snapshot.qa.manager.tomcat.api.DeploymentState;
import travel.snapshot.qa.manager.tomcat.api.DeploymentStateParserException;

@Category(UnitTest.class)
public class DeploymentStateParserTestCase {

    private static final String INVALID_DEPLOYMENT_STATE = "invalid-deployment-state";

    private static final String VALID_DEPLOYMENT_STATE = "running";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void deploymentStateParserTest() throws Exception {
        DeploymentState deploymentState = DeploymentState.parse(VALID_DEPLOYMENT_STATE);

        Assert.assertEquals(DeploymentState.RUNNING, deploymentState);
    }

    @Test
    public void invalidDeploymentStateParserTest() throws Exception {
        expectedException.expect(DeploymentStateParserException.class);
        expectedException.expectMessage(String.format("Unable to parse '%s' to deployment state enumeration.",
                INVALID_DEPLOYMENT_STATE));

        DeploymentState.parse(INVALID_DEPLOYMENT_STATE);
    }
}
