package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTest;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JMS notification tests for Property entity
 */
@RunWith(SerenityRunner.class)
public class PropertyNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    private CustomerDto createdCustomer;
    Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdCustomer = customerHelpers.customerIsCreated(testCustomer1);
        propertyHelpers.propertyIsCreated(testProperty1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPropertyNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.propertyIsCreated(testProperty2);
        Map<String, Object> receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

//    DP-1728
    @Test
    public void createPropertyByCustomerUserNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyByCustomerUserNotificationTest");
        UserDto customerUser = userHelpers.userWithCustomerIsCreated(entitiesLoader.getUserDtos().get("user1"), createdCustomer.getId());
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.propertyIsCreatedByUser(customerUser.getId(), testProperty2);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertyNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertyNotificationTest");
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("Update Property Name");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.updateProperty(testProperty1.getId(), propertyUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deletePropertyNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deletePropertyNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.deleteProperty(testProperty1.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

    @Test
    public void addRemovePropertyUserNotificationTest() throws Exception{
//        Prepare data - a way to employ selectExamplesForTest (list of maps) to have multiple notification objects for one test
        List<Map<String, String>> expectedNotifications = selectExamplesForTest(testClassData, "addRemovePropertyUserNotificationTest");
        Map<String, Object> expectedCreateNotification = new LinkedHashMap<>();
        Map<String, Object> expectedDeleteNotification = new LinkedHashMap<>();
        expectedCreateNotification.putAll(expectedNotifications.get(0));
        expectedDeleteNotification.putAll(expectedNotifications.get(1));
        UserDto testUser = userHelpers.userIsCreated(entitiesLoader.getSnapshotUserDtos().get("snapshotUser1"));
//        Subscribe and test
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.addUserToProperty(testUser.getId(), testProperty1.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        propertyHelpers.userIsDeletedFromProperty(testUser.getId(), testProperty1.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void updateRemovePropertyCustomerNotificationTest() throws Exception{
//        Prepare data - a way to employ selectExamplesForTest (list of maps) to have multiple notification objects for one test
        List<Map<String, String>> expectedNotifications = selectExamplesForTest(testClassData, "updateRemovePropertyCustomerNotificationTest");
        Map<String, Object> expectedUpdateNotification = new LinkedHashMap<>();
        Map<String, Object> expectedDeleteNotification = new LinkedHashMap<>();
        expectedUpdateNotification.putAll(expectedNotifications.get(0));
        expectedDeleteNotification.putAll(expectedNotifications.get(1));
        CustomerPropertyRelationshipPartialUpdateDto customerPropertyUpdate = new CustomerPropertyRelationshipPartialUpdateDto();
        customerPropertyUpdate.setType(OWNER);
        customerHelpers.relationExistsBetweenPropertyAndCustomerWithTypeFromTo(testProperty1.getId(), createdCustomer.getId(), null, null, null, true);
//        Subscribe and test
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertyHelpers.updatePropertyCustomerRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                testProperty1.getId(), createdCustomer.getId(), customerPropertyUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        propertyHelpers.deletePropertyCustomerRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                testProperty1.getId(), createdCustomer.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
