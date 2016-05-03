package travel.snapshot.qa.docker;

import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.manager.jboss.docker.JBossDomainDockerManager;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.Management;

@Category(DockerTest.class)
public class JBossDomainDockerTestCase {

    private static final Logger logger = LoggerFactory.getLogger(JBossDomainDockerTestCase.class);

    private static final String JBOSS_DOMAIN_CONTAINER_ID = "wildfly_domain";

    private static JBossDomainDockerManager jbossDomainDockerManager;

    @BeforeClass
    public static void setup() {

        final Management management = new Management.Builder().setManagementAddress(System.getProperty("docker.host", "127.0.0.1")).build();

        final JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
                .setManagement(management)
                .remote()
                .domain()
                .build();

        final JBossDomainManager jbossDomainManager = new JBossDomainManager(configuration);

        jbossDomainDockerManager = new JBossDomainDockerManager(jbossDomainManager);

        jbossDomainDockerManager.getDockerManager().startManager();

        logger.info("Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        jbossDomainDockerManager.getDockerManager().stopManager();

        logger.info("Docker manager has stopped.");
    }

    @Test
    public void jbossDomainContainerLifecycleTest() {

        final Cube startedJBossDomainContainer = jbossDomainDockerManager.start(JBOSS_DOMAIN_CONTAINER_ID);
        logger.info("JBoss domain Docker container has started");

        Assert.assertTrue("JBoss domain container is not running!", jbossDomainDockerManager.serviceRunning());
        Assert.assertNotNull("JBoss domain container is a null object!", startedJBossDomainContainer);

        final Cube givenJBossStandaloneContainer = jbossDomainDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedJBossDomainContainer, givenJBossStandaloneContainer);
        Assert.assertEquals("Docker manager started container with different container ID", JBOSS_DOMAIN_CONTAINER_ID, startedJBossDomainContainer.getId());

        jbossDomainDockerManager.stop(startedJBossDomainContainer);
        logger.info("JBoss Docker domain container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", jbossDomainDockerManager.serviceRunning());
    }
}
