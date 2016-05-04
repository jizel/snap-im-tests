package travel.snapshot.qa.docker.activemq;

import static travel.snapshot.qa.manager.activemq.impl.docker.ActiveMQService.DEFAULT_ACTIVEMQ_CONTAINER_ID;

import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.manager.activemq.impl.docker.ActiveMQDockerManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;

import javax.jms.Connection;

@Category(DockerTest.class)
public class ActiveMQDockerTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQDockerTestCase.class);

    private static ActiveMQDockerManager activeMQDockerManager;

    @BeforeClass
    public static void setup() throws Exception {

        final ActiveMQManager activeMQManager = new ActiveMQManagerImpl(new ActiveMQManagerConfiguration.Builder().build());

        activeMQDockerManager = new ActiveMQDockerManager(activeMQManager);
        activeMQDockerManager.getDockerManager().startManager();

        logger.info("ActiveMQ Docker manager has started.");
    }

    @AfterClass
    public static void teardown() {
        activeMQDockerManager.getDockerManager().stopManager();

        logger.info("ActiveMQ Docker manager has stopped.");
    }

    @Test
    public void dockerContainerTest() {

        final Cube startedActiveMQContainer = activeMQDockerManager.start(DEFAULT_ACTIVEMQ_CONTAINER_ID);
        logger.info("ActiveMQ Docker container has started");

        Assert.assertTrue("Docker ActiveMQ container is not running!", activeMQDockerManager.serviceRunning());
        Assert.assertNotNull("Docker ActiveMQ container is a null object!", startedActiveMQContainer);

        final Cube activeMQContainer = activeMQDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedActiveMQContainer, activeMQContainer);
        Assert.assertEquals("Docker manager started container with different container ID", DEFAULT_ACTIVEMQ_CONTAINER_ID, startedActiveMQContainer.getId());

        final Connection connection = activeMQDockerManager.getServiceManager().buildConnection();
        Assert.assertNotNull("JMS connection is null!", connection);

        activeMQDockerManager.getServiceManager().closeConnection(connection);

        activeMQDockerManager.stop(startedActiveMQContainer);
        logger.info("Docker ActiveMQ container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", activeMQDockerManager.serviceRunning());
    }
}
