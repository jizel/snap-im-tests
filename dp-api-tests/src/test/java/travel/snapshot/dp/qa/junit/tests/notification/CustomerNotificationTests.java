package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for Customer entity
 */

@RunWith(SerenityRunner.class)
public class CustomerNotificationTests extends CommonTest {

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/customer_notification_tests.yaml"));
    private UUID createdCustomerId;

    @Before
    public void setUp() {
        super.setUp();
        createdCustomerId = entityIsCreated(testCustomer1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createCustomerNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(testCustomer2);
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
        entityIsUpdated(CUSTOMERS_PATH, createdCustomerId, customerUpdate);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deleteCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deleteCustomerNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsDeleted(CUSTOMERS_PATH, createdCustomerId);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

    @Test
    public void addPropertyToCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "addPropertyToCustomerNotificationTest");
        UUID testPropertyId = entityIsCreated(testProperty1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerHelpers.addPropertyToCustomer(testPropertyId, createdCustomerId, null, null, null, true);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertyForCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertyForCustomerNotificationTest");
        UUID testPropertyId = entityIsCreated(testProperty1);
        customerHelpers.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(testPropertyId, createdCustomerId, null, null, null, true);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerHelpers.propertyIsUpdatedForCustomerWith(testPropertyId, createdCustomerId, "valid_from", "2015-01-01");
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void addAndRemoveCustomerUser() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "addAndRemoveCustomerUser");
        UUID testUserId = entityIsCreated(testSnapshotUser1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        customerHelpers.addUserToCustomer(testUserId, createdCustomerId, true, true);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }


}
