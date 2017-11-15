package travel.snapshot.dp.qa.nonpms.jms.util;

import static org.springframework.jms.support.destination.JmsDestinationAccessor.RECEIVE_TIMEOUT_INDEFINITE_WAIT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Message;

@Component
public class Jms {

    private final JmsTemplate jmsTemplate;

    @Autowired
    public Jms(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Jms send(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);

        return this;
    }

    public Message receive(String destination) {
        return jmsTemplate.receive(destination);
    }

    public Message receive(String destination, int timeout) {
        jmsTemplate.setReceiveTimeout(timeout);
        Message returnedMessage = jmsTemplate.receive(destination);
        jmsTemplate.setReceiveTimeout(RECEIVE_TIMEOUT_INDEFINITE_WAIT);

        return returnedMessage;
    }

}
