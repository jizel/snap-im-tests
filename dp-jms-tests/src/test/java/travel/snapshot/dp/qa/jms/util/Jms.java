package travel.snapshot.dp.qa.jms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class Jms {

    private final JmsTemplate jmsTeplate;

    @Autowired
    public Jms(JmsTemplate jmsTeplate) {
        this.jmsTeplate = jmsTeplate;
    }

    public Jms send(String destination, String message) {
        jmsTeplate.convertAndSend(destination, message);

        return this;
    }

}
