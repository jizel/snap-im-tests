package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.configuration.model.ValueType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyConfigurationNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.configuration.model.ConfigurationRecordDto;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Configuration module
 */
public class ConfigurationRecordNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/configuration_notification_tests.yaml"));
    private ConfigurationTypeDto testConfigurationType1;
    private ConfigurationRecordDto testConfigurationRecord1;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() {
        super.setUp();
        testConfigurationType1 = new ConfigurationTypeDto();
        testConfigurationType1.setIdentifier("NotificationTestConfType");
        testConfigurationType1.setDescription("Notification Test Configuration Type Description");
        configurationHelpers.followingConfigurationTypeIsCreated(testConfigurationType1);
        testConfigurationRecord1 = new ConfigurationRecordDto();
        testConfigurationRecord1.setKey("notification_test_key");
        testConfigurationRecord1.setType(STRING);
        testConfigurationRecord1.setValue("String value");
    }

    @After
    public void cleanUp()throws Throwable {
        super.cleanUp();
        jmsHelpers.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createConfigurationRecordNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createConfigurationRecordNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationHelpers.followingConfigurationIsCreated(testConfigurationRecord1, testConfigurationType1.getIdentifier());
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateConfigurationRecordNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateConfigurationRecordNotificationTest");
        configurationHelpers.followingConfigurationIsCreated(testConfigurationRecord1, testConfigurationType1.getIdentifier());
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationHelpers.updateConfigurationValue(testConfigurationType1.getIdentifier(), testConfigurationRecord1.getKey(), "Updated value", "string");
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteConfigurationRecordNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteConfigurationRecordNotificationTest");
        configurationHelpers.followingConfigurationIsCreated(testConfigurationRecord1, testConfigurationType1.getIdentifier());
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationHelpers.tryDeleteConfiguration(testConfigurationRecord1.getKey(), testConfigurationType1.getIdentifier());
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }
}