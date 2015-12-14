package travel.snapshot.qa.integration.jms;

import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Builds connection, session, destination.
 */
public class JMSConnectionHelper {

    private static final Logger logger = Logger.getLogger(JMSConnectionHelper.class.getName());

    private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";

    private String brokerURL = DEFAULT_BROKER_URL;

    // translates to java.jmx.Session.AUTO_ACKNOWLEDGE
    private int sessionAcknowledge = 1;

    private boolean toQueue = false;

    private boolean transacted = false;

    /**
     * Builds JMS connection. The decision whether the connection will be done to topic or queue is made according to
     * the {@link JMSConnectionHelper#toQueue(boolean)} method. When not called, it will be sent to topic. After
     * connection is created, it is started afterwards.
     *
     * @return JMS connection
     */
    public Connection buildConnection() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

        Connection connection = null;

        try {
            if (toQueue) {
                connection = connectionFactory.createQueueConnection();
            } else {
                connection = connectionFactory.createTopicConnection();
            }

            connection.start();
        } catch (JMSException ex) {
            throw new RuntimeException(String.format("Unable to build JMS connection to %s", brokerURL), ex.getCause());
        }

        return connection;
    }

    /**
     * @param connection connection to build a session for
     * @return built session
     * @throws IllegalArgumentException if {@code connection} is a null object
     */
    public Session buildSession(final Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection is a null object.");
        }

        Session session = null;

        try {
            session = connection.createSession(transacted, sessionAcknowledge);
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to build a session.", ex.getCause());
        }

        return session;
    }

    /**
     * @param session     session to build a destination for
     * @param destination destination to build a JMS destination for
     * @return JMS destination for given {@code session} and {@code destination}
     * @throws IllegalArgumentException if {@code destination} is a null object or an empty String
     */
    public Destination buildDestination(final Session session, final String destination) {

        if (destination == null || destination.isEmpty()) {
            throw new IllegalArgumentException("Destination string is not set or empty.");
        }

        Destination jmsDestination = null;

        try {
            if (toQueue) {
                jmsDestination = session.createQueue(destination);
            } else {
                jmsDestination = session.createTopic(destination);
            }
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to build a destination to send a message to.", ex.getCause());
        }

        return jmsDestination;
    }

    /**
     * Closes a session.
     *
     * @param session session to close
     * @throws IllegalArgumentException if {@code session} is a null object
     */
    public void close(final Session session) {

        if (session == null) {
            throw new IllegalArgumentException("Session to close is a null object.");
        }

        try {
            session.close();
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to close a session.", ex.getCause());
        }
    }

    /**
     * Closes a connection.
     *
     * @param connection connection to close
     * @throws IllegalArgumentException if {@code connection} to close is a null object.
     */
    public void close(final Connection connection) {

        if (connection == null) {
            throw new IllegalArgumentException("Connection to close is a null object.");
        }

        try {
            connection.close();
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to close a connection.", ex.getCause());
        }
    }

    // setters

    /**
     * @param brokerURL URL of ActiveMQ broker, if null or empty, default URL of {@code tcp://localhost:61616} is used.
     * @return this
     */
    public JMSConnectionHelper brokerUrl(String brokerURL) {

        if (brokerURL == null || brokerURL.isEmpty()) {
            logger.warning(String.format("Supplied broker URL is not valid, default broker URL %s will be used",
                    DEFAULT_BROKER_URL));

            this.brokerURL = DEFAULT_BROKER_URL;
            return this;
        }

        this.brokerURL = brokerURL;

        return this;
    }

    public JMSConnectionHelper toQueue(boolean toQueue) {
        this.toQueue = toQueue;
        return this;
    }

    /**
     * @param sessionAcknowledge maps to acknowledge constants in javax.jms.Session.
     * @return this
     * @throws IllegalArgumentException if {@code sessionAcknowledge} is lower then 0 or higher then 3.
     */
    public JMSConnectionHelper sessionAcknowledge(int sessionAcknowledge) {

        if (sessionAcknowledge < 0 || sessionAcknowledge > 3) {
            throw new IllegalArgumentException("JMS session acknowledge has to be 0,1,2 or 3");
        }

        this.sessionAcknowledge = sessionAcknowledge;
        return this;
    }

    public JMSConnectionHelper transacted(boolean transacted) {
        this.transacted = transacted;
        return this;
    }

}
