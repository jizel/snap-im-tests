package travel.snapshot.dp.qa.junit.helpers;

import static org.junit.Assert.*;

import com.jayway.restassured.path.json.JsonPath;
import lombok.NoArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import java.nio.charset.Charset;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * Basic steps for handling JMS notifications
 */

@NoArgsConstructor
public class JmsHelpers extends BasicSteps {

    private static final String SESSION_NOTIFICATION = "notification";
    private static final int JMS_TIMEOUT = 5000;
    private Connection connection = null;
    private Session session = null;
    private MessageConsumer consumer = null;


    private void start() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                propertiesHelper.getProperty("broker.user"),
                propertiesHelper.getProperty("broker.password"),
                propertiesHelper.getProperty("broker.url"));
        connection = connectionFactory.createConnection();
        connection.setClientID("testClientId");
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private void close() throws Exception {
        session.close();
        connection.close();
    }

    public Map<String, Object> receiveMessage(String destinationName, String subscriptionName) throws Exception {
        start();
        Topic topic = new ActiveMQTopic(destinationName);
        consumer = session.createDurableSubscriber(topic, subscriptionName);
        BytesMessage message = (BytesMessage) consumer.receive(JMS_TIMEOUT);
        close();

        assertNotNull("Message was not received after " + JMS_TIMEOUT + "ms", message);

        byte[] byteArr = new byte[(int) message.getBodyLength()];
        message.readBytes(byteArr);
        String m = new String(byteArr, Charset.forName("utf-8"));
        Map<String, Object> mapNotification = JsonPath.from(m).getJsonObject("");
        setSessionVariable(SESSION_NOTIFICATION, mapNotification);
        return mapNotification;
    }

    public void unsubscribe(String destinationName, String subscriptionName) throws Exception {
        start();
        session.unsubscribe(subscriptionName);
        close();
    }

    public void subscribe(String destinationName, String subscriptionName) throws Exception {
        start();
        Topic topic = new ActiveMQTopic(destinationName);
        consumer = session.createDurableSubscriber(topic, subscriptionName);
        close();
    }

}
