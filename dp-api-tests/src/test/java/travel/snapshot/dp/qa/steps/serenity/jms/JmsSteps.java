package travel.snapshot.dp.qa.steps.serenity.jms;

import javax.validation.constraints.AssertTrue;

import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.jms.JmsTestUtilities;

/**
 * Created by sedlacek on 11/18/2015.
 */
public class JmsSteps extends BasicSteps {

    public void serverIsConnected() {
        JmsTestUtilities jms = new JmsTestUtilities();
        jms.createQueue();
    }

    public void messageIsRecieved(String queueName) {
        //code for creating jms connection, session, receiving here
        TextMessage text = session.receive(queueName);
        //store recieved message to session
        setSessionVariable("message", text);
    }

    public void messageContains(String text) {
        //get from session to validate it
        TextMessage message = getSessionVariable("message");

        //assert something
        AssertTrue(message.contains(text));
    }

    public void messagehasSize(int size) {
        //get from session again to validate in a different way
        TextMessage message = getSessionVariable("message");

        //assert something
        assertEquals(size, message.size());
    }
}
