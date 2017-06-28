package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Property Set entity
 */
@RunWith(SerenityRunner.class)
public class PropertySetNotificationTests extends CommonTest{
    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/property_set_notification_tests.yaml"));
    Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() throws Exception{
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPropertySetNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertySetNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySetHelpers.propertySetIsCreated(testPropertySet1);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertySetNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertySetNotificationTest");
        PropertySetDto createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        PropertySetUpdateDto propertySetUpdate = new PropertySetUpdateDto();
        propertySetUpdate.setName("Updated name");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySetSteps.updatePropertySet(createdPropertySet.getId(), propertySetUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deletePropertySetNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deletePropertySetNotificationTest");
        PropertySetDto createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySetSteps.deletePropertySet(createdPropertySet.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

//    -------------------< Second level entities >-----------------

    @Test
    public void addAndRemovePropertySetPropertyNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertySetPropertyNotificationTest");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "deletePropertySetPropertyNotificationTest");
        PropertySetDto createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        PropertyDto testProperty1 = propertyHelpers.propertyIsCreated(entitiesLoader.getPropertyDtos().get("property1"));
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySetSteps.addPropertyToPropertySet(testProperty1.getId(), createdPropertySet.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        propertySetSteps.removePropertyFromPropertySet(testProperty1.getId(), createdPropertySet.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void addAndRemovePropertySetUserNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPropertySetUserNotificationTest");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "deletePropertySetUserNotificationTest");
        PropertySetDto createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        UserDto createdUser = userHelpers.userIsCreated(entitiesLoader.getUserDtos().get("user1"));
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        propertySetSteps.addUserToPropertySet(createdUser.getId(), createdPropertySet.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        propertySetSteps.removeUserFromPropertySet(createdUser.getId(), createdPropertySet.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
