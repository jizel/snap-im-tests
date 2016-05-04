package travel.snapshot.qa.manager.jboss;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;

@Category(JBossTest.class)
public class JBossStandaloneDeploymentTestCase extends AbstractDeploymentTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        AbstractDeploymentTestCase.setup();
        startInternalStandalone();
    }

    @AfterClass
    public static void teardown() {
        stopInternalStandalone();
    }

    @After
    public void undeploy() {
        manager.getDeployer().undeploy(DEPLOYMENT_NAME);
    }

    @Test
    public void testDeploymentAndUndeployment() {
        String runtimeName = manager.getDeployer().deploy(archive);
        assertNotNull(runtimeName);
        // undeployment occurs in undeploy method
    }

    @Test
    public void testFileDeploymentAndUndeployment() {
        String runtimeName = manager.getDeployer().deploy(testingArchive.getAbsolutePath());
        assertNotNull(runtimeName);
        // undeployment occurs in undeploy method
    }

    @Test
    public void testAlreadyDeployedArchive() {

        expectedException.expect(ContainerDeploymentException.class);

        manager.getDeployer().deploy(archive);
        manager.getDeployer().deploy(archive);
    }
}
