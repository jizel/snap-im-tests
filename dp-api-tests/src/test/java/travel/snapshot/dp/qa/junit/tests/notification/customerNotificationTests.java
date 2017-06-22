package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Customer entity
 */

@RunWith(SerenityRunner.class)
public class customerNotificationTests extends CommonTest {

    private static final String TOPIC_NAME = "Notifications.crud";
    private static final String SUBSCRIPTION_NAME = "Test";
    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/customer_notification_tests.yaml"));

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() throws Exception{
        jmsSteps.unsubscribe(TOPIC_NAME, SUBSCRIPTION_NAME);
    }

    @Test
    public void createCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createCustomerNotificationTest");
        jmsSteps.subscribe(TOPIC_NAME, SUBSCRIPTION_NAME);
        customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(TOPIC_NAME, SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateCustomerNotificationTest() throws Exception{
//        Prepare data
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateCustomerNotificationTest");
        CustomerDto testCustomer = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Update Customer Name");
//        Get update notification and verify
        jmsSteps.subscribe(TOPIC_NAME, SUBSCRIPTION_NAME);
        customerHelpers.customerIsUpdated(testCustomer.getId(), customerUpdate);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(TOPIC_NAME, SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteCustomerNotificationTest");
        CustomerDto testCustomer = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        jmsSteps.subscribe(TOPIC_NAME, SUBSCRIPTION_NAME);
        customerHelpers.customerIsDeleted(testCustomer.getId());
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(TOPIC_NAME, SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

    @Test
    public void addPropertyToCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "addPropertyToCustomerNotificationTest");
        CustomerDto testCustomer = customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer1"));
        PropertyDto testProperty = propertyHelpers.propertyIsCreated(entitiesLoader.getPropertyDtos().get("property1"));
        jmsSteps.subscribe(TOPIC_NAME, SUBSCRIPTION_NAME);
        customerSteps.addPropertyToCustomer(testProperty.getId(), testCustomer.getId(), null, null, null, true);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(TOPIC_NAME, SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }


}
