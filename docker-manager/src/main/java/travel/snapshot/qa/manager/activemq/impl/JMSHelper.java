package travel.snapshot.qa.manager.activemq.impl;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManagerException;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.api.configuration.Validate;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Builds connection, session, destination and closes these resources.
 */
public class JMSHelper {

    private static final Logger logger = LoggerFactory.getLogger(JMSHelper.class);

    private static final String DEFAULT_BROKER_ADDRESS = "127.0.0.1";

    private static final int DEFAULT_BROKER_PORT = 61616;

    private String brokerAddress = DEFAULT_BROKER_ADDRESS;

    private int brokerPort = DEFAULT_BROKER_PORT;

    private JMSMessageContext messageContext;

    public JMSHelper() {
        messageContext = new JMSMessageContext.Builder().build();
    }

    /**
     * Sets JMS message context with configuration by which resources to send a message by are configured properly
     *
     * @param messageContext message context to use
     * @return this
     */
    public JMSHelper withContext(final JMSMessageContext messageContext) {
        this.messageContext = messageContext;
        return this;
    }

    public JMSMessageContext getMessageContext() {
        return messageContext;
    }

    /**
     * Builds JMS connection. The decision whether the connection will be done to topic or queue is made according to
     * the {@link JMSMessageContext#isToQueue()} method. When not set to true by its builder, it will be sent to topic.
     * After connection is created, it is started afterwards.
     *
     * @return JMS connection
     */
    public Connection buildConnection(final ActiveMQManagerConfiguration configuration) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(getBrokerURL());

        final String username = configuration.getUsername();
        final String password = configuration.getPassword();

        Connection connection;

        try {
            if (messageContext.isToQueue()) {
                connection = connectionFactory.createQueueConnection(username, password);
            } else {
                connection = connectionFactory.createTopicConnection(username, password);
            }

            connection.start();
        } catch (JMSException ex) {
            throw new ActiveMQManagerException(String.format("Unable to build JMS connection to %s", brokerAddress), ex.getCause());
        }

        return connection;
    }

    /**
     * Builds session for given connection.
     *
     * @param connection connection to build a session for
     * @return built session
     * @throws IllegalArgumentException if {@code connection} is a null object
     * @throws ActiveMQManagerException if it is not possible to build a session
     */
    public Session buildSession(final Connection connection) {
        Validate.notNull(connection, "Connection is a null object.");

        Session session;

        try {
            session = connection.createSession(messageContext.isTransacted(), messageContext.getSessionAcknowledge());
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to build a session.", ex.getCause());
        }

        return session;
    }

    /**
     * Builds destination for given session.
     *
     * @param session     session to build a destination for
     * @param destination destination to build a JMS destination for
     * @return JMS destination for given {@code session} and {@code destination}
     * @throws IllegalArgumentException if {@code destination} is a null object or an empty String
     * @throws ActiveMQManagerException if it is not possible to build a destination
     */
    public Destination buildDestination(final Session session, final String destination) {
        Validate.notNullOrEmpty(destination, "Destination string is not set or empty.");

        Destination jmsDestination;

        try {
            if (messageContext.isToQueue()) {
                jmsDestination = session.createQueue(destination);
            } else {
                jmsDestination = session.createTopic(destination);
            }
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to build a destination to send a message to.", ex.getCause());
        }

        return jmsDestination;
    }

    /**
     * Builds an object message from serializable object.
     *
     * @param object  serializable object to be sent in a message
     * @param session session to use for message sending
     * @param <T>     type of message to be sent
     * @return created object message
     * @throws ActiveMQManagerException if it is not possible to build a message
     */
    public <T extends Serializable> Message buildMessage(final T object, final Session session) {

        Message message;

        try {
            message = session.createObjectMessage(object);
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to build object message.", ex.getCause());
        }

        return message;
    }

    /**
     * Closes a session.
     *
     * @param session session to close
     * @throws ActiveMQManagerException if it is not possible to close the session
     */
    public void close(final Session session) {

        if (session == null) {
            logger.debug("Session to close is a null object.");
            return;
        }

        try {
            session.close();
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to close a session.", ex.getCause());
        }
    }

    /**
     * Closes a connection.
     *
     * @param connection connection to close
     * @throws ActiveMQManagerConfiguration if it is not possible to close the connection
     */
    public void close(final Connection connection) {

        if (connection == null) {
            logger.debug("Connection to close is a null object.");
            return;
        }

        try {
            connection.close();
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to close a connection.", ex.getCause());
        }
    }

    /**
     * @param brokerAddress broker address, without port
     * @return this
     * @throws IllegalArgumentException if {@code brokerAddress} is a null object or an empty String
     */
    public JMSHelper brokerAddress(String brokerAddress) {
        Validate.notNull(brokerAddress, "Broker address must not be a null object or an empty String.");
        this.brokerAddress = brokerAddress;
        return this;
    }

    /**
     * @param brokerPort broker port
     * @return this
     */
    public JMSHelper brokerPort(int brokerPort) {
        this.brokerPort = brokerPort;
        return this;
    }

    /**
     * @return broker URL in form tcp://address:port
     */
    public String getBrokerURL() {
        return String.format("tcp://%s:%s", brokerAddress, brokerPort);
    }
}
