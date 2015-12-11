package travel.snapshot.qa.integration.jms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Effectively sends JMS message to topic / queue and close resources.
 */
public class JMSMessageSender {

    private final JMSConnectionHelper jmsHelper;

    /**
     * @param jmsHelper connection helper
     * @throws IllegalArgumentException if {@code jmsHelper} is a null object
     */
    public JMSMessageSender(final JMSConnectionHelper jmsHelper) {

        if (jmsHelper == null) {
            throw new IllegalArgumentException("JMSConnectionHelper is a null object.");
        }

        this.jmsHelper = jmsHelper;
    }

    /**
     * Sends {@code messageObject} to the {@code destination}.
     *
     * After sending is done, underlying connection and session are closed.
     *
     * @param messageObject message object to send to destination
     * @param destination   destination to send {@code messageObject} to
     */
    public <T extends Serializable> void send(final T messageObject, final String destination) {

        final Connection connection = jmsHelper.buildConnection();

        final Session session = jmsHelper.buildSession(connection);

        final Destination jmsDestination = jmsHelper.buildDestination(session, destination);

        final Message message = new JMSMessageBuilder().buildMessage(messageObject, session);

        final JMSProducerHelper producerHelper = new JMSProducerHelper();

        final MessageProducer producer = producerHelper.buildProducer(session, jmsDestination);

        producerHelper.send(message, producer);

        producerHelper.close(producer);
        jmsHelper.close(session);
        jmsHelper.close(connection);
    }

}
