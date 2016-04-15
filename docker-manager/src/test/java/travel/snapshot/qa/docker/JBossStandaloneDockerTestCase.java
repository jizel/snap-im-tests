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
import travel.snapshot.qa.docker.manager.impl.JBossStandaloneDockerManager;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.Management;

@Category(DockerTest.class)
public class JBossStandaloneDockerTestCase {

    private static final Logger logger = LoggerFactory.getLogger(JBossStandaloneDockerTestCase.class);

    private static final String JBOSS_STANDALONE_CONTAINER_ID = "wildfly";

    private static JBossStandaloneDockerManager jbossStandaloneDockerManager;

    @BeforeClass
    public static void setup() {

        final Management management = new Management.Builder().setManagementAddress(System.getProperty("docker.host", "127.0.0.1")).build();

        final JBossManagerConfiguration configuration = new JBossManagerConfiguration.Builder()
                .setManagement(management)
                .remote()
                .build();

        final JBossStandaloneManager jbossStandaloneManager = new JBossStandaloneManager(configuration);

        jbossStandaloneDockerManager = new JBossStandaloneDockerManager(jbossStandaloneManager);

        jbossStandaloneDockerManager.getDockerManager().startManager();

        logger.info("Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        jbossStandaloneDockerManager.getDockerManager().stopManager();

        logger.info("Docker manager has stopped.");
    }

    @Test
    public void jbossStandaloneContainerLifecycleTest() {

        final Cube startedJBossStandaloneContainer = jbossStandaloneDockerManager.start(JBOSS_STANDALONE_CONTAINER_ID);
        logger.info("JBoss standalone Docker container has started");

        Assert.assertTrue("JBoss standalone container is not running!", jbossStandaloneDockerManager.serviceRunning());
        Assert.assertNotNull("JBoss standalone container is a null object!", startedJBossStandaloneContainer);

        final Cube givenJBossStandaloneContainer = jbossStandaloneDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedJBossStandaloneContainer, givenJBossStandaloneContainer);
        Assert.assertEquals("Docker manager started container with different container ID", JBOSS_STANDALONE_CONTAINER_ID, startedJBossStandaloneContainer.getId());

        jbossStandaloneDockerManager.stop(startedJBossStandaloneContainer);
        logger.info("JBoss Docker standalone container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", jbossStandaloneDockerManager.serviceRunning());
    }
}
