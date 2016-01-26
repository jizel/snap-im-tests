package travel.snapshot.qa.manager.activemq.impl;

import org.jboss.arquillian.core.spi.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;

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

    private static final Logger logger = LoggerFactory.getLogger(JMSMessageSender.class);

    private final JMSHelper jmsHelper;

    private final ActiveMQManagerConfiguration configuration;

    /**
     * @param jmsHelper connection helper
     * @throws IllegalArgumentException if {@code jmsHelper} is a null object
     */
    public JMSMessageSender(final JMSHelper jmsHelper, final ActiveMQManagerConfiguration configuration) {
        Validate.notNull(jmsHelper, "JMSHelper must not be a null object.");
        Validate.notNull(configuration, "ActiveMQManagerConfiguration must not be a null object.");
        this.jmsHelper = jmsHelper;
        this.configuration = configuration;
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

        final JMSProducerHelper producerHelper = new JMSProducerHelper();

        MessageProducer producer = null;

        Session session = null;

        Connection connection = null;

        try {
            connection = jmsHelper.buildConnection(configuration);

            session = jmsHelper.buildSession(connection);

            final Destination jmsDestination = jmsHelper.buildDestination(session, destination);

            final Message message = jmsHelper.buildMessage(messageObject, session);

            producer = producerHelper.buildProducer(session, jmsDestination);

            producerHelper.send(message, producer);
        } finally {
            try {
                producerHelper.close(producer);
                jmsHelper.close(session);
                jmsHelper.close(connection);
            } catch (Exception ex) {
                logger.warn("Unable to close resources: {}", ex.getMessage());
            }
        }
    }

}
