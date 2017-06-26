package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Customer entity
 */

@RunWith(SerenityRunner.class)
public class CustomerNotificationTests extends CommonTest {

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/customer_notification_tests.yaml"));
    private CustomerDto createdCustomer1;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
    }

    @After
    public void cleanUp() throws Exception{
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createCustomerNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerHelpers.customerIsCreated(entitiesLoader.getCustomerDtos().get("customer2"));
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateCustomerNotificationTest() throws Exception{
//        Prepare data
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updateCustomerNotificationTest");
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Update Customer Name");
//        Get update notification and verify
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerHelpers.customerIsUpdated(createdCustomer1.getId(), customerUpdate);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deleteCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deleteCustomerNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerHelpers.customerIsDeleted(createdCustomer1.getId());
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

    @Test
    public void addPropertyToCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "addPropertyToCustomerNotificationTest");
        PropertyDto testProperty = propertyHelpers.propertyIsCreated(entitiesLoader.getPropertyDtos().get("property1"));
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerSteps.addPropertyToCustomer(testProperty.getId(), createdCustomer1.getId(), null, null, null, true);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertyForCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertyForCustomerNotificationTest");
        PropertyDto testProperty = propertyHelpers.propertyIsCreated(entitiesLoader.getPropertyDtos().get("property1"));
        customerSteps.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(testProperty.getId(), createdCustomer1.getId(), null, null, null, true);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerSteps.propertyIsUpdatedForCustomerWith(testProperty.getId(), createdCustomer1.getId(), "valid_from", "2015-01-01");
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    @Ignore
    public void addAndRemoveCustomerUser() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "addAndRemoveCustomerUser");
        UserDto testUser = userHelpers.userIsCreated(testSnapshotUser1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerSteps.addUserToCustomer(testUser.getId(), createdCustomer1.getId(), true, true);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }


}
