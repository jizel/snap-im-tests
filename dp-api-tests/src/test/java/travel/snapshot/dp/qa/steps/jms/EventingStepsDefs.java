package travel.snapshot.dp.qa.steps.jms;

import net.thucydides.core.annotations.Steps;
import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.User;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.jms.JmsTestUtilities;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;
import travel.snapshot.dp.qa.steps.serenity.jms.JmsSteps;

/**
 * Created by sedlacek on 11/18/2015.
 */
public class EventingStepsDefs {
	private static String topicName = "MyT";

    @Steps
    private JmsSteps steps;

    @Steps
    private UsersSteps usersSteps;
    
    @When("^Send an ETL message and receiving verify it$")
    public void send_an_ETL_message_and_receiving_verify_it() throws Throwable {
        steps.initializeComponents(topicName);
        steps.etlMessageIsSent();
        steps.etlMessageIsReceived();
        steps.closeResources();
          }
    
    

    @When("^ETL message is sent$")
    public void etl_message_is_sent() throws Throwable {
    	  steps.initializeComponents(topicName);
    	  steps.etlMessageIsSent();
    	  steps.closeResources();
    	}

    @Then("^ETL DurableSubscriber should receive the message and validate it$")
    public void etl_DurableSubscriber_should_receive_the_message_and_validate_it() throws Throwable {
    	steps.initializeComponents(topicName);
    	steps.etlMessageIsReceived();
        steps.closeResources();
    	}


    //@And("^publisher sends a message and the subscriber consumes it$")
    //public void when_message_is_sent() throws Throwable {
    //	steps.messageSent("MyT");
    //}
    
//    @Given("^the test is connected to the JMS <server>$")
//    public void the_test_is_connected_to_the_JMS_server() throws Throwable {
//        steps.serverIsConnected();
//    }
//
//    @Then("^message was recieved from queue \"([^\"]*)\"$")
//    public void message_was_recieved_from_queue(String queueName) throws Throwable {
//        steps.messageIsRecieved(queueName);
//    }


//    @Then("^message contains text \"([^\"]*)\"$")
//    public void message_contains_text(String text) throws Throwable {
//        steps.messageContains(text);
//    }
//
//    @Then("^message has size (\\d+)$")
//    public void message_has_size(int size) throws Throwable {
//        steps.messagehasSize(size);
//    }
}
