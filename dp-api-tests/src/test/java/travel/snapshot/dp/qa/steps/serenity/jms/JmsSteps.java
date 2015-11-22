package travel.snapshot.dp.qa.steps.serenity.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.validation.constraints.AssertTrue;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;

import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.jms.JmsTestUtilities;

/**
 * Created by sedlacek on 11/18/2015.
 */
public class JmsSteps extends BasicSteps {

    private static String JMS_SERVER_URL = "tcp://127.0.0.1:61616";
    private static String topicName = "myQ";
    private static String[] msgContent = {"create", "update","delete", "STOP"};

	public void serverIsConnected() throws JMSException {
    	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_SERVER_URL);
        JmsTestUtilities jms = new JmsTestUtilities(connectionFactory);
        jms.createQueue(topicName);
    }

    public void messageIsRecieved(String queueName) throws JMSException, InterruptedException {
    	BasicConfigurator.configure();
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_SERVER_URL);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createTopic(queueName);
		MessageProducer messageProducer = session.createProducer(destination);
		MessageConsumer consumer = session.createConsumer(destination);
		TextMessage message = session.createTextMessage();

		for(int i=0; i<3; i++){
			message.setText(msgContent[i]);
			messageProducer.send(message);
			System.out.println("Sent message '" + i + " " + msgContent[i]);
			Thread.sleep(100); 
		
		//MessageConsumer consumer = session.createConsumer(destination);
			//while(true) {
				Message message1 = consumer.receive();
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					System.out.println("Received message '" + textMessage.getText() + "'");
					//setSessionVariable("message", textMessage);
					if(textMessage.getText() != null && textMessage.getText().contains("STOP")) {
						break;
					//}
				}
			}
		}
		
		consumer.close();
		session.close();
		connection.close();
    }
    

	public static void messageSent(String queueName) throws JMSException, InterruptedException {
		BasicConfigurator.configure();

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_SERVER_URL);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// Creating session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Getting the queue
		Destination destination = session.createTopic(queueName);
		MessageProducer messageProducer = session.createProducer(destination);
		for(int i=0; i<4; i++){
		TextMessage message = session.createTextMessage();
		message.setText(msgContent[i]);
		messageProducer.send(message);
		System.out.println("Sent message '" + i + " " + msgContent[i]);
		Thread.sleep(4000); 
		}
		messageProducer.close();
		connection.close();
    }
	
}
    
   
//    public void messageContains(String text) {
//        //get from session to validate it
//        TextMessage message = getSessionVariable("message");
//
//        //assert something
//        AssertTrue(message.contains(text));
//    }
//
//    public void messagehasSize(int size) {
//        //get from session again to validate in a different way
//        TextMessage message = getSessionVariable("message");
//
//        //assert something
//        assertEquals(size, message.size());
