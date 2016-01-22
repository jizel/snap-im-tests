package travel.snapshot.qa.manager.activemq.impl;

import org.jboss.arquillian.core.spi.Validate;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class ActiveMQManagerImpl implements ActiveMQManager {

    private final ActiveMQManagerConfiguration configuration;

    private final JMSHelper jmsHelper;

    public ActiveMQManagerImpl() {
        this(new ActiveMQManagerConfiguration.Builder().build());
    }

    public ActiveMQManagerImpl(final ActiveMQManagerConfiguration configuration) {
        Validate.notNull(configuration, "Configuration must not be a null object.");
        this.configuration = configuration;

        jmsHelper = new JMSHelper()
                .brokerAddress(configuration.getBrokerAddress())
                .brokerPort(configuration.getBrokerPort());
    }

    @Override
    public ActiveMQManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Connection buildConnection() {
        return jmsHelper.buildConnection(configuration);
    }

    @Override
    public Session buildSession(Connection connection) {
        return jmsHelper.buildSession(connection);
    }

    @Override
    public Destination buildDestination(Session session, String destination) {
        return jmsHelper.buildDestination(session, destination);
    }

    @Override
    public <T extends Serializable> Message buildMessage(T messageObject, Session session) {
        return jmsHelper.buildMessage(messageObject, session);
    }

    @Override
    public MessageProducer buildProducer(Session session, Destination destination) {
        return new JMSProducerHelper().buildProducer(session, destination);
    }

    @Override
    public void send(Message message, MessageProducer messageProducer) {
        new JMSProducerHelper().send(message, messageProducer);
    }

    @Override
    public <T extends Serializable> void send(final T messageObject, final String destination) {
        new JMSMessageSender(jmsHelper, configuration).send(messageObject, destination);
    }

    @Override
    public void withMessageContext(JMSMessageContext messageContext) {
        jmsHelper.withContext(messageContext);
    }

    @Override
    public void closeSession(Session session) {
        jmsHelper.close(session);
    }

    @Override
    public void closeConnection(Connection connection) {
        jmsHelper.close(connection);
    }

    @Override
    public void closeMessageProducer(MessageProducer messageProducer) {
        new JMSProducerHelper().close(messageProducer);
    }
}
