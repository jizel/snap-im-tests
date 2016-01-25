package travel.snapshot.qa.docker;

import org.arquillian.cube.docker.impl.docker.DockerClientExecutor;
import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.docker.manager.DockerManager;
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.api.Deployments;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.util.logging.Logger;

@Category(DockerTest.class)
public class TomcatDockerTestCase {

    private static final Logger logger = Logger.getLogger(TomcatDockerTestCase.class.getName());

    private static final String TOMCAT_CONTAINER_ID = "tomcat_tests";

    private static TomcatDockerManager tomcatDockerManager;

    @BeforeClass
    public static void setup() throws Exception {

        final TomcatManagerConfiguration tomcatManagerConfiguration = new TomcatManagerConfiguration.Builder()
                .setBindAddress("127.0.0.1")
                .remote()
                .build();

        final TomcatManager tomcatManager = new TomcatManager(tomcatManagerConfiguration);

        tomcatDockerManager = new TomcatDockerManager(tomcatManager);
        tomcatDockerManager.getDockerManager().startManager();

        logger.info("Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        tomcatDockerManager.getDockerManager().stopManager();

        logger.info("Docker manager has stopped.");
    }

    @Test
    public void tomcatContainerLifecycleTest() {

        final Cube startedTomcatContainer = tomcatDockerManager.start(TOMCAT_CONTAINER_ID);
        logger.info("Tomcat Docker container has started");

        Assert.assertTrue("Docker Tomcat container is not running!", tomcatDockerManager.serviceRunning());
        Assert.assertNotNull("Docker Tomcat container is a null object!", startedTomcatContainer);

        final Cube givenTomcatContainer = tomcatDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedTomcatContainer, givenTomcatContainer);
        Assert.assertEquals("Docker manager started container with different container ID", TOMCAT_CONTAINER_ID, startedTomcatContainer.getId());

        tomcatDockerManager.stop(startedTomcatContainer);
        logger.info("Docker Tomcat container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", tomcatDockerManager.serviceRunning());
    }

    @Test
    public void tomcatContainerInDockerTest() {

        final Cube startedTomcatContainer = tomcatDockerManager.start(TOMCAT_CONTAINER_ID);

        final TomcatManager tomcatManager = tomcatDockerManager.getServiceManager();

        Assert.assertTrue(tomcatManager.isRunning());

        Deployments deployments = tomcatManager.listDeployments();
        Assert.assertEquals(2, deployments.size());

        final DockerClientExecutor dockerClientExecutor = DockerManager.instance().getClientExecutor();

        tomcatDockerManager.stop();
    }

    @Test
    public void dockerClientExecutorTestCase() {
        Assert.assertNotNull("Client executor is a null object!", tomcatDockerManager.getDockerManager().getClientExecutor());
    }
}
