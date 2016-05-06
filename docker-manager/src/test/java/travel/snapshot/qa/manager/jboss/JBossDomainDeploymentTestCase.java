package travel.snapshot.qa.manager.jboss;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import travel.snapshot.qa.category.JBossTest;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Category(JBossTest.class)
public class JBossDomainDeploymentTestCase extends AbstractDeploymentTestCase {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        AbstractDeploymentTestCase.setup();
        startInternalDomain();
    }

    @AfterClass
    public static void teardown() {
        stopInternalDomain();
    }

    @After
    public void undeploy() {
        domainManager.getDeployer().undeploy(DEPLOYMENT_NAME);
    }

    @Test
    public void testDeploymentAndUndeployment() {
        String runtimeName = domainManager.getDeployer().serverGroup("main-server-group").deploy(archive);
        assertNotNull(runtimeName);
        // undeployment occurs in undeploy method

        domainManager.getDeployer().undeploy(DEPLOYMENT_NAME);

        Set<String> serverGroups = Stream.of("main-server-group").collect(Collectors.toSet());
        String runtimeName2 = domainManager.getDeployer().serverGroups(serverGroups).deploy(archive);

        assertNotNull(runtimeName2);
    }

    @Test
    public void testFilePathDeploymentAndUndeployment() {
        String runtimeName = domainManager.deploy(testingArchive.getAbsolutePath());
        assertNotNull(runtimeName);
        // undeployment occurs in undeploy method
    }

    @Test
    public void testFileDeploymentAndUndeployment() {
        String runtimeName = domainManager.deploy(testingArchive);
        assertNotNull(runtimeName);
        // undeployment occurs in undeploy method
    }

    @Test
    public void testAlreadyDeployedArchive() {

        expectedException.expect(ContainerDeploymentException.class);

        domainManager.deploy(archive);
        domainManager.deploy(archive);
    }
}
