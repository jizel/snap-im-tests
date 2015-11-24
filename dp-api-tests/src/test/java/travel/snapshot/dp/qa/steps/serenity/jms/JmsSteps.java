package travel.snapshot.dp.qa.steps.serenity.jms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.activemq.*;
import org.apache.log4j.BasicConfigurator;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.jms.JmsTestUtilities;

/**
 * Created by sedlacek on 11/18/2015.
 */
public class JmsSteps extends BasicSteps {
	
	private Connection connection;
	private Session session;
	private ConnectionFactory connectionFactory;
	private Destination destination;
	private MessageProducer messageProducer;
	private MessageConsumer consumer;
	private Topic topic;
	private TopicSubscriber subscriber;
	private TextMessage message;
	private Gson gson = new Gson();

    private static String JMS_SERVER_URL = "tcp://127.0.0.1:61616";
    private static String topicName = "myQ";
    private static String[] msgContent = {"create", "update","delete", "STOP"};
    private static String[] entitType = {"Property", "PropertySet","Customer", "User", "Configuration"};
    private static String[] operation = {"Create", "Update","Delete"};
    private static String entityId = "6D4068FA-0448-4794-BA14-88FB098A3259";
    private static String timestampPattern	= "\\d\\d\\d\\d-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\d\\+\\d\\d:\\d\\d";
    private static String datePattern	= "\\d\\d\\d\\d-\\d\\d\\-\\d\\d";
    
	public void serverIsConnected() throws JMSException {
    	ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_SERVER_URL);
        JmsTestUtilities jms = new JmsTestUtilities(connectionFactory);
        jms.createQueue(topicName);
    }

    public void etlMessageIsSent() throws JMSException, InterruptedException {
    	
			HashMap<Object, Object> testMessage = new HashMap<Object,Object>();
			testMessage.put("id", entityId);
			testMessage.put("entity-type", entitType[0]);
			testMessage.put("operation", operation[0]);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			String formattedDate = sdf.format(new Date());
			testMessage.put("timestamp", formattedDate);
			String jsonMessage = gson.toJson(testMessage);
			System.out.println("jsonMessage " + jsonMessage);
			message.setText(jsonMessage);
			messageProducer.send(message);
			Thread.sleep(100); 
    }
    
    public void etlMessageIsReceived() throws JMSException, InterruptedException {
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			String returnedMessage = textMessage.getText();
			HashMap<Object,Object> returnedMap = gson.fromJson(returnedMessage, HashMap.class);
			if(returnedMap.get("id") == null) {
				System.out.println("ID property is null");
			}
			if(returnedMap.get("entity-type") != null) {
				for(int j = 0; j < entitType.length; j++) {
					if(returnedMap.get("entity-type").equals(entitType[j])) {
						System.out.println("Entity-type is " + entitType[j]);
					}
				}
			}
			else {
				System.out.println("Entity-type is null");
			}
			if(returnedMap.get("operation") != null) {
				for(int j = 0; j < operation.length; j++) {
					if(returnedMap.get("operation").equals(operation[j])) {
						System.out.println("Operation is " + operation[j]);
					}
				}
			}
			else {
				System.out.println("Operation is null");
			}
			Pattern pattern = Pattern.compile(timestampPattern);
			Matcher matcher = pattern.matcher(returnedMap.get("timestamp").toString());
			if (!matcher.matches()) {
				System.out.println("Timestamp is not the correct format");
			}
			//System.out.println("Received message '" + textMessage.getText() + "'");
			//setSessionVariable("message", textMessage);
			if(textMessage.getText() != null && textMessage.getText().contains("STOP")) {
			//}
			}
	}
    }

	public static void messageSent(String topicName) throws JMSException, InterruptedException {
		BasicConfigurator.configure();

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(JMS_SERVER_URL);
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// Creating session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		// Getting the queue
		Destination destination = session.createTopic(topicName);
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
	
	public void initializeComponents(String topicName)  throws JMSException, InterruptedException {
		connectionFactory = new ActiveMQConnectionFactory(JMS_SERVER_URL);
		connection = connectionFactory.createConnection();
		connection.setClientID("John");
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		destination = session.createTopic(topicName);
		messageProducer = session.createProducer(destination);
		consumer = session.createConsumer(destination);
		topic = session.createTopic(topicName);
		subscriber = session.createDurableSubscriber(topic, "John");
		message = session.createTextMessage();
	}
	
	public void crudMessageIsSend() throws JMSException, InterruptedException {
			
			HashMap<Object,Object> testMessage = new HashMap<Object,Object>();
			Pair datePair = new Pair();
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			datePair.setStart_date(dateFormatter.format(new Date()));
			datePair.setEnd_date(dateFormatter.format(new Date()));
			ArrayList<Pair> dates = new ArrayList<Pair>();
			dates.add(datePair);
			dates.add(datePair);
			testMessage.put("message_code", "Name");
			testMessage.put("affected_data_ranges", dates);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
			String formattedDate = sdf.format(new Date());
			
			testMessage.put("timestamp", formattedDate);
			ArrayList<String> affectedProperties = new ArrayList<String>();
			affectedProperties.add("6D4068FA-0448-4794-BA14-88FB098A3259");
			testMessage.put("affected_properties", affectedProperties);
			
			
			String jsonMessage = gson.toJson(testMessage);
			System.out.println("jsonMessage from crud" + jsonMessage);
			message.setText(jsonMessage);
			messageProducer.send(message);
		}
		//MessageConsumer consumer = session.createConsumer(destination);
		//while(true) {
	
	public void crudMessageIsReceived() throws JMSException, InterruptedException {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				String returnedMessage = textMessage.getText();
				
				HashMap<Object, Object> returnedMap = gson.fromJson(returnedMessage, new TypeToken<HashMap<Object, Object>>() {}.getType());
				if(returnedMap.get("affected_properties") == null) {
					System.out.println("Affected_properties is null");
				}
				if(returnedMap.get("message_code") == null) {
					System.out.println("Message_code is null");
				}
				
				Pattern pattern = Pattern.compile(timestampPattern);
				Matcher matcher = pattern.matcher(returnedMap.get("timestamp").toString());
				if (!matcher.matches()) {
					System.out.println("Timestamp is not the correct format");
				}
				if(returnedMap.get("affected_data_ranges") == null) {
					System.out.println("Affected_data_ranges is null");
				}
			}
	}
	
	public void closeResources() throws JMSException, InterruptedException {
		consumer.close();
		session.close();
		connection.close();
	}
	class Pair {
		private String start_date;
		private String end_date;
		public String getStart_date() {
			return start_date;
		}
		public void setStart_date(String start_date) {
			this.start_date = start_date;
		}
		public String getEnd_date() {
			return end_date;
		}
		public void setEnd_date(String end_date) {
			this.end_date = end_date;
		}
		
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
