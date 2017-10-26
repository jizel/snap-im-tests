package travel.snapshot.dp.qa.jms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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

}
