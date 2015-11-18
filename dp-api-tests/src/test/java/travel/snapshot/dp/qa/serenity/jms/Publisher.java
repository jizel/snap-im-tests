package travel.snapshot.dp.qa.serenity.jms;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;


public class Publisher {
	
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
		MessageProducer messageProducer = session.createProducer(destination);
		TextMessage message = session.createTextMessage();
		message.setText("test");
		messageProducer.send(message);
		messageProducer.close();
		connection.close();
    }
	
}
