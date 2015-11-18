package travel.snapshot.dp.qa.serenity.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

public class Consumer {
	// URL of the JMS server
	private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	// Name of the queue we will receive messages from
	private static String subject = "MYtopic";

	public static void main(String[] args) throws JMSException {
		BasicConfigurator.configure();
		// Getting JMS connection from the server
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// Creating session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Getting the queue
		Destination destination = session.createTopic(subject);
		// MessageConsumer is used for consuming messages
		MessageConsumer consumer = session.createConsumer(destination);
		while(true) {
			Message message = consumer.receive();
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				System.out.println("Received message '" + textMessage.getText() + "'");
				if(textMessage.getText() != null && textMessage.getText().equals("STOP")) {
					break;
				}
			}
		}
		
		consumer.close();
		session.close();
		connection.close();
	}
}