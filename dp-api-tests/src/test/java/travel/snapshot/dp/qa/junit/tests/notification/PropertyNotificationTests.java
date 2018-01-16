package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadExamplesYaml;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.selectExamplesForTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for Property entity
 */
public class PropertyNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    private Map<String, List<Map<String, String>>> testClassData = loadExamplesYaml(String.format(YAML_DATA_PATH, "notifications/property_notification_tests.yaml"));
    private UUID createdCustomerId;
    private UUID createdPropertyId;
    private UUID createdUserId;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() {
        super.setUp();
        createdPropertyId = entityIsCreated(testProperty1);
        createdCustomerId = entityIsCreated(testCustomer1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsHelpers.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPropertyNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(testProperty2);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

//    DP-1728
    @Test
    public void createPropertyByCustomerUserNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertyByCustomerUserNotificationTest");
        createdUserId = entityIsCreated(testUser1);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        createEntityByUserForApplication(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,  testProperty2);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertyNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertyNotificationTest");
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("Update Property Name");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsUpdated(PROPERTIES_PATH, testProperty1.getId(), propertyUpdate);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deletePropertyNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deletePropertyNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

    @Test
    public void addRemovePropertyUserNotificationTest() throws Throwable {
//        Prepare data - a way to employ selectExamplesForTest (list of maps) to have multiple notification objects for one test
        List<Map<String, String>> expectedNotifications = selectExamplesForTest(testClassData, "addRemovePropertyUserNotificationTest");
        Map<String, Object> expectedCreateNotification = new LinkedHashMap<>();
        Map<String, Object> expectedDeleteNotification = new LinkedHashMap<>();
        expectedCreateNotification.putAll(expectedNotifications.get(0));
        expectedDeleteNotification.putAll(expectedNotifications.get(1));
        createdUserId = entityIsCreated(testUser1);
//        Subscribe and test
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserPropertyRelationshipDto(createdUserId, createdPropertyId, true));
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsDeleted(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void updateRemovePropertyCustomerNotificationTest() throws Throwable {
//        Prepare data - a way to employ selectExamplesForTest (list of maps) to have multiple notification objects for one test
        List<Map<String, String>> expectedNotifications = selectExamplesForTest(testClassData, "updateRemovePropertyCustomerNotificationTest");
        Map<String, Object> expectedUpdateNotification = new LinkedHashMap<>();
        Map<String, Object> expectedDeleteNotification = new LinkedHashMap<>();
        expectedUpdateNotification.putAll(expectedNotifications.get(0));
        expectedDeleteNotification.putAll(expectedNotifications.get(1));
        CustomerPropertyRelationshipUpdateDto customerPropertyUpdate = new CustomerPropertyRelationshipUpdateDto();
        customerPropertyUpdate.setType(OWNER);
        UUID relationId = entityIsCreated(constructCustomerPropertyRelationshipDto(createdCustomerId, createdPropertyId, true, CHAIN, validFrom, validTo));
//        Subscribe and test
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsUpdated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId, customerPropertyUpdate);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
