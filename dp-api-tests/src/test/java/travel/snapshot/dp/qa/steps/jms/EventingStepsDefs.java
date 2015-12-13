package travel.snapshot.dp.qa.steps.jms;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.model.*;
import travel.snapshot.dp.qa.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.serenity.jms.JmsSteps;
import travel.snapshot.dp.qa.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.serenity.property_sets.PropertySetSteps;
import travel.snapshot.dp.qa.serenity.roles.RolesSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

/**
 * Created by sedlacek on 11/18/2015.
 */
public class EventingStepsDefs {
    private static String topicName = "MyT";

    @Steps
    private JmsSteps steps;

    @Steps
    private CustomerSteps customerSteps;

    @Steps
    private PropertySteps propertySteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private RolesSteps rolesSteps;

    @Steps
    private PropertySetSteps propertySetSteps;

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

    @Then("^Notification in session parent entity type is \"([^\"]*)\"$")
    public void Notification_in_session_parent_entity_type_is(String entityType) throws Throwable {
        steps.notificationParentEntityTypeIs(entityType);
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

    @Then("^Notification in session id stands for configuration type with identifier \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_configuration_type_with_identifier(String configurationTypeIdentifier) throws Throwable {
        steps.notificationContainsId(configurationTypeIdentifier);
    }

    @Then("^Notification in session id stands for property with code \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_property_with_code(String propertyCode) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        steps.notificationContainsId(p.getPropertyId());
    }

    @Then("^Notification in session id stands for user with username \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_user_with_username(String username) throws Throwable {
        User u = usersSteps.getUserByUsername(username);
        steps.notificationContainsId(u.getUserId());
    }

    @Then("^Notification in session id stands for role with name \"([^\"]*)\" for application id  \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_role_with_name_for_application_id(String roleName, String applicationId) throws Throwable {
        Role r = rolesSteps.getRoleByNameForApplicationInternal(roleName, applicationId);
        steps.notificationContainsId(r.getRoleId());
    }

    @Then("^Notification in session id stands for property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_role_with_name_for_customer_with_code(String propertySetName, String customerCode) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        PropertySet ps = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());
        steps.notificationContainsId(ps.getPropertySetId());
    }

    @Then("^Notification in session parent id stands for customer with code \"([^\"]*)\"$")
    public void Notification_in_session_parent_id_stands_for_customer_with_code(String customerCode) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        steps.notificationContainsParentId(c.getCustomerId());
    }

    @Given("^Role with name \"([^\"]*)\" for application id \"([^\"]*)\" is stored in session under key \"([^\"]*)\"$")
    public void Role_with_name_is_stored_in_session_under_key(String roleName, String applicationId, String sessionKey) throws Throwable {
        steps.setSessionVariable(sessionKey, rolesSteps.getRoleByNameForApplicationInternal(roleName, applicationId));
    }

    @Then("^Notification in session id stands for role in session on key \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_role_in_session_on_key(String sessionKey) throws Throwable {
        Role r = steps.getSessionVariable(sessionKey);
        steps.notificationContainsId(r.getRoleId());
    }


    @Then("^Notification in session parent id stands for user with username \"([^\"]*)\"$")
    public void Notification_in_session_parent_id_stands_for_user_with_username(String username) throws Throwable {
        User u = usersSteps.getUserByUsername(username);
        steps.notificationContainsParentId(u.getUserId());
    }

    @Then("^Notification in session parent id stands for property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\"$")
    public void Notification_in_session_parent_id_stands_for_property_set_with_name_for_customer_with_code(String propertySetName, String customerCode) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        PropertySet ps = propertySetSteps.getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());
        steps.notificationContainsParentId(ps.getPropertySetId());

    }

    @Then("^Notification in session parent id stands for property with code \"([^\"]*)\"$")
    public void Notification_in_session_parent_id_stands_for_property_with_code(String propertyCode) throws Throwable {
        Property p = propertySteps.getPropertyByCodeInternal(propertyCode);
        steps.notificationContainsParentId(p.getPropertyId());
    }

    @Given("^Property set with name \"([^\"]*)\" for customer with code \"([^\"]*)\" is stored in session under key \"([^\"]*)\"$")
    public void Property_set_with_name_for_customer_with_code_is_stored_in_session_under_key(String propertySetName, String customerCode, String sessionKey) throws Throwable {
        Customer c = customerSteps.getCustomerByCodeInternal(customerCode);
        steps.setSessionVariable(sessionKey, propertySetSteps.getPropertySetByNameForCustomer(propertySetName, c.getCustomerId()));
    }

    @Then("^Notification in session id stands for property set in session on key \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_property_set_in_session_on_key(String sessionKey) throws Throwable {
        PropertySet ps = steps.getSessionVariable(sessionKey);
        steps.notificationContainsId(ps.getPropertySetId());
    }

    @Given("^Property with code \"([^\"]*)\" is stored in session under key \"([^\"]*)\"$")
    public void Property_with_code_is_stored_in_session_under_key(String propertyCode, String sessionKey) throws Throwable {
        steps.setSessionVariable(sessionKey, propertySteps.getPropertyByCodeInternal(propertyCode));
    }

    @Then("^Notification in session id stands for property in session on key \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_property_in_session_on_key(String sessionKey) throws Throwable {
        Property p = steps.getSessionVariable(sessionKey);
        steps.notificationContainsId(p.getPropertyId());
    }

    @Given("^User with username \"([^\"]*)\" is stored in session under key \"([^\"]*)\"$")
    public void User_with_username_is_stored_in_session_under_key(String username, String sessionKey) throws Throwable {
        steps.setSessionVariable(sessionKey, usersSteps.getUserByUsername(username));
    }

    @Then("^Notification in session id stands for user in session on key \"([^\"]*)\"$")
    public void Notification_in_session_id_stands_for_user_in_session_on_key(String sessionKey) throws Throwable {
        User u = steps.getSessionVariable(sessionKey);
        steps.notificationContainsId(u.getUserId());
    }
}