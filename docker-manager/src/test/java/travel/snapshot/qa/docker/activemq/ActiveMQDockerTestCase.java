package travel.snapshot.qa.docker.activemq;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
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
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;
import travel.snapshot.qa.manager.activemq.impl.JMSMessageContext;
import travel.snapshot.qa.manager.activemq.impl.docker.ActiveMQDockerManager;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

@Category(DockerTest.class)
public class ActiveMQDockerTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQDockerTestCase.class);

    private static ActiveMQDockerManager activeMQDockerManager;

    private static final JMSMessageContext queueMessageContext = new JMSMessageContext.Builder().toQueue(true).build();

    private static final JMSMessageContext topicMessageContext = new JMSMessageContext.Builder().toQueue(false).build();

    private static Cube startedActiveMQContainer;

    @BeforeClass
    public static void setup() throws Exception {

        final ActiveMQManager activeMQManager = new ActiveMQManagerImpl(new ActiveMQManagerConfiguration.Builder().build());

        activeMQDockerManager = new ActiveMQDockerManager(activeMQManager);
        activeMQDockerManager.getDockerManager().startManager();

        logger.info("ActiveMQ Docker manager has started.");

        startedActiveMQContainer = activeMQDockerManager.start(DEFAULT_ACTIVEMQ_CONTAINER_ID);
        logger.info("ActiveMQ Docker container has started");
    }

    @AfterClass
    public static void teardown() {
        activeMQDockerManager.stop(startedActiveMQContainer);
        logger.info("Docker ActiveMQ container has stopped.");

        Assert.assertFalse("Container has stopped but service is still running.", activeMQDockerManager.serviceRunning());

        activeMQDockerManager.getDockerManager().stopManager();

        logger.info("ActiveMQ Docker manager has stopped.");
    }

    @Test
    public void dockerContainerTest() throws Exception {

        Assert.assertTrue("Docker ActiveMQ container is not running!", activeMQDockerManager.serviceRunning());
        Assert.assertNotNull("Docker ActiveMQ container is a null object!", startedActiveMQContainer);

        final Cube activeMQContainer = activeMQDockerManager.getDockerContainer();

        Assert.assertEquals("Started and saved Docker containers are not equal!", startedActiveMQContainer, activeMQContainer);
        Assert.assertEquals("Docker manager started container with different container ID", DEFAULT_ACTIVEMQ_CONTAINER_ID, startedActiveMQContainer.getId());

        final Connection connection = activeMQDockerManager.getServiceManager().buildConnection();
        Assert.assertNotNull("JMS connection is null!", connection);
        activeMQDockerManager.getServiceManager().closeConnection(connection);

        testQueueMessaging(activeMQDockerManager.getServiceManager());
        testTopicMessaging(activeMQDockerManager.getServiceManager());
    }

    private void testTopicMessaging(ActiveMQManager activeMQManager) throws JMSException {
        activeMQManager.withMessageContext(topicMessageContext);

        Connection topicConnection = activeMQManager.buildConnection();

        Session session = activeMQManager.buildSession(topicConnection);

        Destination destination = activeMQManager.buildDestination(session, "testingTopicDestination");

        MessageProducer messageProducer = activeMQManager.buildProducer(session, destination);

        TextMessage textMessage = session.createTextMessage("test message");

        MessageConsumer messageConsumer = activeMQManager.buildConsumer(session, destination);

        activeMQManager.send(textMessage, messageProducer);

        Message receivedMessage = messageConsumer.receive(5000);

        Assert.assertThat(receivedMessage, is(instanceOf(TextMessage.class)));

        Assert.assertEquals("test message", ((TextMessage) receivedMessage).getText());

        Assert.assertNotNull(receivedMessage);

        activeMQManager.closeMessageConsumer(messageConsumer);
        activeMQManager.closeMessageProducer(messageProducer);
        activeMQManager.closeSession(session);
        activeMQManager.closeConnection(topicConnection);
    }

    private void testQueueMessaging(ActiveMQManager activeMQManager) throws JMSException {
        activeMQManager.withMessageContext(queueMessageContext);

        Connection queueConnection = activeMQManager.buildConnection();

        Session session = activeMQManager.buildSession(queueConnection);

        Destination destination = activeMQManager.buildDestination(session, "testingQueueDestination");

        MessageProducer messageProducer = activeMQManager.buildProducer(session, destination);

        TextMessage textMessage = session.createTextMessage("test queue message");

        MessageConsumer messageConsumer = activeMQManager.buildConsumer(session, destination);

        activeMQManager.send(textMessage, messageProducer);

        Message receivedMessage = messageConsumer.receive(5000);

        Assert.assertThat(receivedMessage, is(instanceOf(TextMessage.class)));

        Assert.assertEquals("test queue message", ((TextMessage) receivedMessage).getText());

        Assert.assertNotNull(receivedMessage);

        activeMQManager.closeMessageConsumer(messageConsumer);
        activeMQManager.closeMessageProducer(messageProducer);
        activeMQManager.closeSession(session);
        activeMQManager.closeConnection(queueConnection);
    }
}
