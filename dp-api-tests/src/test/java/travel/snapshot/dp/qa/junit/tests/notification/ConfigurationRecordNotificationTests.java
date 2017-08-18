package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.configuration.model.ValueType.STRING;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyConfigurationNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.configuration.model.ConfigurationRecordDto;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Configuration module
 */
@RunWith(SerenityRunner.class)
public class ConfigurationRecordNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/configuration_notification_tests.yaml"));
    private ConfigurationTypeDto testConfigurationType1;
    private ConfigurationRecordDto testConfigurationRecord1;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        testConfigurationType1 = new ConfigurationTypeDto();
        testConfigurationType1.setIdentifier("NotificationTestConfType");
        testConfigurationType1.setDescription("Notification Test Configuration Type Description");
        configurationSteps.followingConfigurationTypeIsCreated(testConfigurationType1);
        testConfigurationRecord1 = new ConfigurationRecordDto();
        testConfigurationRecord1.setKey("notification_test_key");
        testConfigurationRecord1.setType(STRING);
        testConfigurationRecord1.setValue("String value");
    }

    @After
    public void cleanUp()throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createConfigurationRecordNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createConfigurationRecordNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.followingConfigurationIsCreated(testConfigurationRecord1, testConfigurationType1.getIdentifier());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateConfigurationRecordNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateConfigurationRecordNotificationTest");
        configurationSteps.followingConfigurationIsCreated(testConfigurationRecord1, testConfigurationType1.getIdentifier());
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.updateConfigurationValue(testConfigurationType1.getIdentifier(), testConfigurationRecord1.getKey(), "Updated value", "string");
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteConfigurationRecordNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteConfigurationRecordNotificationTest");
        configurationSteps.followingConfigurationIsCreated(testConfigurationRecord1, testConfigurationType1.getIdentifier());
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.tryDeleteConfiguration(testConfigurationRecord1.getKey(), testConfigurationType1.getIdentifier());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }
}