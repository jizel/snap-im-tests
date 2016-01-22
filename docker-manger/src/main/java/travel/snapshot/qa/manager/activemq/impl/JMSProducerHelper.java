package travel.snapshot.qa.manager.activemq.impl;

import org.jboss.arquillian.core.spi.Validate;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManagerException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JMSProducerHelper {

    /**
     * Builts a message producer from given {@code session} and {@code destination}.
     *
     * @param session     session to build a message producer for
     * @param destination destination to build a message producer for
     * @return message producer which sends message to given {@code destination}
     * @throws IllegalArgumentException if {@code session} or {@code destination} is a null object
     */
    public MessageProducer buildProducer(final Session session, final Destination destination) {
        Validate.notNull(session, "Session must not be a null object.");
        Validate.notNull(destination, "Destination must not be a null object.");

        MessageProducer producer;

        try {
            producer = session.createProducer(destination);
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to create a message producer.", ex.getCause());
        }

        return producer;
    }

    /**
     * Sends given message by specified producer.
     *
     * @param message  message to send by {@code producer}
     * @param producer producer by which {@code message} will be sent
     * @throws IllegalArgumentException if {@code producer} or {@code message} is a null object
     */
    public void send(final Message message, final MessageProducer producer) {
        Validate.notNull(message, "Message to send by producer must not be a null object.");
        Validate.notNull(message, "Producer to send a message by must not be a null object.");

        try {
            producer.send(message);
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to send a message. ", ex.getCause());
        }
    }

    /**
     * Closes a producer.
     *
     * @param producer producer to close
     * @throws IllegalArgumentException if {@code producer} is a null object
     */
    public void close(final MessageProducer producer) {
        Validate.notNull(producer, "Message producer to close is a null object");

        try {
            producer.close();
        } catch (JMSException ex) {
            throw new ActiveMQManagerException("Unable to close JMS producer.", ex.getCause());
        }
    }
}
