package travel.snapshot.qa.docker;

import org.arquillian.cube.spi.Cube;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import travel.snapshot.qa.category.DockerTest;
import travel.snapshot.qa.docker.manager.impl.ActiveMQDockerManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;

import java.util.logging.Logger;

import javax.jms.Connection;

@Category(DockerTest.class)
public class ActiveMQDockerTestCase {

    private static final Logger logger = Logger.getLogger(ActiveMQDockerTestCase.class.getName());

    private static final String ACTIVEMQ_CONTAINER_ID = "activemq";

    private static ActiveMQDockerManager activeMQDockerManager;

    @BeforeClass
    public static void setup() throws Exception {

        final ActiveMQManagerConfiguration activeMQManagerConfiguration = new ActiveMQManagerConfiguration.Builder()
                .brokerAddress("127.0.0.1")
                .brokerPort(61616)
                .build();

        final ActiveMQManager activeMQManager = new ActiveMQManagerImpl(activeMQManagerConfiguration);

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

        final Cube startedActiveMQContainer = activeMQDockerManager.start(ACTIVEMQ_CONTAINER_ID);
        logger.info("ActiveMQ Docker container has started");

        Assert.assertTrue("Docker ActiveMQ container is not running!", activeMQDockerManager.serviceRunning());
        Assert.assertNotNull("Docker ActiveMQ container is a null object!", startedActiveMQContainer);

        final Cube activeMQContainer = activeMQDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedActiveMQContainer, activeMQContainer);
        Assert.assertEquals("Docker manager started container with different container ID", ACTIVEMQ_CONTAINER_ID, startedActiveMQContainer.getId());

        final Connection connection = activeMQDockerManager.getServiceManager().buildConnection();
        Assert.assertNotNull("JMS connection is null!", connection);

        activeMQDockerManager.getServiceManager().closeConnection(connection);

        activeMQDockerManager.stop(startedActiveMQContainer);
        logger.info("Docker ActiveMQ container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", activeMQDockerManager.serviceRunning());
    }
}
