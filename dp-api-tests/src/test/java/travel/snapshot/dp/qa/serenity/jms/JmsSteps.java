package travel.snapshot.dp.qa.serenity.jms;

import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.jayway.restassured.path.json.JsonPath;
import lombok.NoArgsConstructor;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

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
public class JmsSteps extends BasicSteps {

    public static final String SESSION_NOTIFICATION = "notification";
    public static final int JMS_TIMEOUT = 5000;
    private ConnectionFactory connectionFactory = null;
    private Connection connection = null;
    private Session session = null;
    private MessageConsumer consumer = null;
    private Gson gson = new Gson();


    public void start() throws Exception {
        connectionFactory = new ActiveMQConnectionFactory(PropertiesHelper.getProperty("broker.url"));
        connection = connectionFactory.createConnection();
        connection.setClientID("testClientId");
        connection.start();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public void close() throws Exception {
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
        Topic topic = new ActiveMQTopic(destinationName);
        consumer = session.createDurableSubscriber(topic, subscriptionName);
        consumer.close();
        session.unsubscribe(subscriptionName);
        close();
    }

    public void subscribe(String destinationName, String subscriptionName) throws Exception {
        start();
        Topic topic = new ActiveMQTopic(destinationName);
        consumer = session.createDurableSubscriber(topic, subscriptionName);
        connection.start();
        consumer.close();
        close();
    }

    public void notificationEntityTypeIs(String entityType) {
        Map<String, Object> notification = getSessionVariable(SESSION_NOTIFICATION);
        String value = (String) notification.get("entity_type");
        assertEquals(entityType, value);
    }

    public void notificationOperationIs(String operation) {
        Map<String, Object> notification = getSessionVariable(SESSION_NOTIFICATION);
        String value = (String) notification.get("operation");
        assertEquals(operation, value);
    }

    public void notificationContainsId(String entityId) {
        Map<String, Object> notification = getSessionVariable(SESSION_NOTIFICATION);
        String value = (String) notification.get("entity_id");
        assertEquals(entityId, value);
    }

    public void notificationParentEntityTypeIs(String entityType) {
        Map<String, Object> notification = getSessionVariable(SESSION_NOTIFICATION);
        String value = (String) notification.get("parent_entity_type");
        assertEquals(entityType, value);
    }

    public void notificationContainsParentId(String entityId) {
        Map<String, Object> notification = getSessionVariable(SESSION_NOTIFICATION);
        String value = (String) notification.get("parent_id");
        assertEquals(entityId, value);
    }

    public String getNotificationValue(String key) {
        Map<String, Object> notification = getSessionVariable(SESSION_NOTIFICATION);
        return (String) notification.get(key);
    }
}
