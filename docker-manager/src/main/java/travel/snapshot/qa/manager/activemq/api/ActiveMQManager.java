package travel.snapshot.qa.manager.activemq.api;

import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.JMSMessageContext;
import travel.snapshot.qa.manager.api.ServiceManager;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * ActiveMQ operations interface. Gathers methods which simplifies and enables you to interact with ActiveMQ.
 */
public interface ActiveMQManager extends ServiceManager {

    ActiveMQManagerConfiguration getConfiguration();

    /**
     * Builds JMS connection. The decision whether this connection will be done to queue or topic is determined by
     * {@link JMSMessageContext} which can be set by {@link ActiveMQManager#withMessageContext(JMSMessageContext)}.
     *
     * @return built JMS connection
     */
    Connection buildConnection();

    /**
     * Builds a session for given connection.
     *
     * @param connection connection to build a session for
     * @return built JMS Session
     */
    Session buildSession(Connection connection);

    /**
     * Builds a destination for given session. The decision whether this destination will be built for queue or topic is
     * determined by {@link JMSMessageContext} which can be set by {@link ActiveMQManager#withMessageContext(JMSMessageContext)}.
     */
    Destination buildDestination(Session session, String destination);

    /**
     * Builds a {@link javax.jms.Message} for given serializable message object.
     *
     * @param messageObject serializable message to build
     * @param session       session to build a message for
     * @param <T>           type of serializable object to build a message of
     * @return built JMS message
     */
    <T extends Serializable> Message buildMessage(T messageObject, Session session);

    MessageProducer buildProducer(Session session, Destination destination);

    /**
     * Sends given {@link javax.jms.Message} by specified {@link javax.jms.MessageProducer}.
     *
     * @param message         JMS message to send
     * @param messageProducer message producer to send a message by
     */
    void send(Message message, MessageProducer messageProducer);

    /**
     * Sends serializable message object to given destination.
     *
     * All connection, session, destination and message producer are constructed, used and destroyed in the scope of
     * this message.
     *
     * @param messageObject message object to send
     * @param destination   destination to send a {@code messageObject}.
     * @param <T>           type of serializable message object to send
     */
    <T extends Serializable> void send(T messageObject, String destination);

    /**
     * Sets message context used for other method invocations in this helper. The same message context is cached between
     * method invocations until new message context is set by this method. When this method is not called, default
     * message context is used.
     *
     * @param messageContext message context to use
     */
    void withMessageContext(JMSMessageContext messageContext);

    /**
     * Closes session.
     *
     * @param session session to close
     */
    void closeSession(Session session);

    /**
     * Closes connection.
     *
     * @param connection connection to close
     */
    void closeConnection(Connection connection);

    /**
     * Closes message producer.
     *
     * @param messageProducer message producer to close
     */
    void closeMessageProducer(MessageProducer messageProducer);
}
