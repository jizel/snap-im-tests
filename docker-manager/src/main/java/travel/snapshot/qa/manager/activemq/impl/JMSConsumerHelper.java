package travel.snapshot.qa.manager.activemq.impl;

import travel.snapshot.qa.manager.activemq.api.ActiveMQManagerException;
import travel.snapshot.qa.manager.api.configuration.Validate;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

public class JMSConsumerHelper {

    /**
     * Builds JMS consumer for given session and destination.
     *
     * @param session     session to build a consumer from
     * @param destination destination to create consumer for
     * @return built message consumer
     * @throws ActiveMQManagerException in case it is not possible to create message consumer
     */
    public MessageConsumer buildConsumer(Session session, Destination destination) throws ActiveMQManagerException {
        Validate.notNull(session, "Session must not be a null object.");
        Validate.notNull(destination, "Destination must not be a null object.");

        MessageConsumer consumer;

        try {
            consumer = session.createConsumer(destination);
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to create a message consumer.", ex.getCause());
        }

        return consumer;
    }

    /**
     * Closes message consumer.
     *
     * @param consumer message consumer to close
     * @throws ActiveMQManagerException in case it is not possible to close consumer
     */
    public void close(MessageConsumer consumer) throws ActiveMQManagerException {
        Validate.notNull(consumer, "Consumer to close is a null object.");

        try {
            consumer.close();
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to close JMS consumer.", ex.getCause());
        }
    }
}
