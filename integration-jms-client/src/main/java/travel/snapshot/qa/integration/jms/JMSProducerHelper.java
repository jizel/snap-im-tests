package travel.snapshot.qa.integration.jms;

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

        if (session == null) {
            throw new IllegalArgumentException("Session is a null object.");
        }

        if (destination == null) {
            throw new IllegalArgumentException("Destination is a null object");
        }

        MessageProducer producer = null;

        try {
            producer = session.createProducer(destination);
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to create a message producer.", ex.getCause());
        }

        return producer;
    }

    /**
     * @param message  message to send by {@code producer}
     * @param producer producer by which {@code message} will be sent
     * @throws IllegalArgumentException if {@code producer} or {@code message} is a null object
     */
    public void send(final Message message, final MessageProducer producer) {

        if (message == null) {
            throw new IllegalArgumentException("Message to send by producer is a null object");
        }

        if (producer == null) {
            throw new IllegalArgumentException("Producer to send a message by is a null object.");
        }

        try {
            producer.send(message);
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to send a message. ", ex.getCause());
        }
    }

    /**
     * Closes a producer.
     *
     * @param producer producer to close
     * @throws IllegalArgumentException if {@code producer} is a null object
     */
    public void close(final MessageProducer producer) {

        if (producer == null) {
            throw new IllegalArgumentException("Message producer to close is a null object");
        }

        try {
            producer.close();
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to close JMS producer.", ex.getCause());
        }
    }
}
