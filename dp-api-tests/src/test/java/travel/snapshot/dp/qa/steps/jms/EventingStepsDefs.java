package travel.snapshot.dp.qa.steps.jms;

import net.thucydides.core.annotations.Steps;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.jms.JmsSteps;

/**
 * Created by sedlacek on 11/18/2015.
 */
public class EventingStepsDefs {
    private static String topicName = "MyT";

    @Steps
    private JmsSteps steps;

    @Steps
    private CustomerSteps customerSteps;

    /*@When("^Send an ETL message and receiving verify it$")
    public void send_an_ETL_message_and_receiving_verify_it() throws Throwable {
        //steps.initializeComponents(topicName);
        steps.etlMessageIsSent();
        steps.etlMessageIsReceived();
        steps.closeResources();
    }


    @When("^ETL message is sent$")
    public void etl_message_is_sent() throws Throwable {
        //steps.initializeComponents(topicName);
        steps.etlMessageIsSent();
        steps.closeResources();
    }

    @Then("^ETL DurableSubscriber should receive the message and validate it$")
    public void etl_DurableSubscriber_should_receive_the_message_and_validate_it() throws Throwable {
        //steps.initializeComponents(topicName);
        steps.etlMessageIsReceived();
        steps.closeResources();
    }*/


    @Given("^Subscription with name \"([^\"]*)\" for topic \"([^\"]*)\" does not exist$")
    public void Subscription_with_name_for_topic_does_not_exist(String subscriptionName, String topic) throws Throwable {
        steps.unsubscribe(topic, subscriptionName);
    }

    @Given("^Subscription with name \"([^\"]*)\" for topic \"([^\"]*)\" is created$")
    public void Subscription_with_name_for_topic_is_created(String subscriptionName, String topic) throws Throwable {
        steps.subscribe(topic, subscriptionName);

    }

    @Then("^Subscription with name \"([^\"]*)\" for topic \"([^\"]*)\" is unsubscribed$")
    public void Subscription_with_name_for_topic_is_unsubscribed(String subscriptionName, String topic) throws Throwable {
        steps.unsubscribe(topic, subscriptionName);
    }

    @Then("^Message is received with subscription \"([^\"]*)\" from topic \"([^\"]*)\" and stored in session$")
    public void Message_is_received_with_subscription_from_topic_and_stored_in_session(String subscriptionName, String topic) throws Throwable {
        steps.receiveMessage(topic, subscriptionName);
    }

    @Then("^Notification in session entity_type is \"([^\"]*)\"$")
    public void Notification_in_session_entity_type_is(String entityType) throws Throwable {
        steps.notificationEntityTypeIs(entityType);
    }

    @Then("^Notification in session operation is \"([^\"]*)\"$")
    public void Notification_in_session_operation_is(String operation) throws Throwable {
        steps.notificationOperationIs(operation);
    }

    @Then("^Notification in session id stands for customer with code \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_customer_with_code(String customerCode) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        steps.notificationContainsId(c.getCustomerId());
    }

    @Given("^Customer with code \"([^\"]*)\" is stored in session under key \"([^\"]*)\"$")
    public void Customer_with_code_is_stored_in_session_under_key(String customerCode, String sessionKey) throws Throwable {
        steps.setSessionVariable(sessionKey, customerSteps.getCustomerByCodeInternal(customerCode));
    }

    @Then("^Notification in session id stands for customer in session on key \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_customer_in_session_on_key(String sessionKey) throws Throwable {
        Customer c = steps.getSessionVariable(sessionKey);
        steps.notificationContainsId(c.getCustomerId());
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
