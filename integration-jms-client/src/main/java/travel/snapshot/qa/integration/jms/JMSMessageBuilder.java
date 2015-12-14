package travel.snapshot.qa.integration.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Builds a JMS message.
 */
public class JMSMessageBuilder {

    public <T extends Serializable> Message buildMessage(final T object, final Session session) {

        Message message = null;

        try {
            message = session.createObjectMessage(object);
        } catch (JMSException ex) {
            throw new RuntimeException("Unable to build object message.", ex.getCause());
        }

        return message;
    }
}
