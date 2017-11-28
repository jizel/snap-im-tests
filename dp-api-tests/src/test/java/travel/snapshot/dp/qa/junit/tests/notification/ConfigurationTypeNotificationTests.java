package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyConfigurationNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.configuration.model.ConfigurationTypeDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Configuration types
 */
public class ConfigurationTypeNotificationTests extends CommonTest {

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/configuration_notification_tests.yaml"));
    private ConfigurationTypeDto testConfigurationType1;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() {
        super.setUp();
        testConfigurationType1 = new ConfigurationTypeDto();
        testConfigurationType1.setIdentifier("NotificationTestConfType");
        testConfigurationType1.setDescription("Notification Test Configuration Type Description");
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.tryDeleteConfigurationType(testConfigurationType1.getIdentifier());
    }

    @Test
    public void createConfigurationTypeNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createConfigurationTypeNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.followingConfigurationTypeIsCreated(testConfigurationType1);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateConfigurationTypeNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateConfigurationTypeNotificationTest");
        configurationSteps.followingConfigurationTypeIsCreated(testConfigurationType1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.updateConfigurationTypeDescription(testConfigurationType1.getIdentifier(), "Updated description");
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteConfigurationTypeNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteConfigurationTypeNotificationTest");
        configurationSteps.followingConfigurationTypeIsCreated(testConfigurationType1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        configurationSteps.tryDeleteConfigurationType(testConfigurationType1.getIdentifier());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyConfigurationNotification(expectedCreateNotification, receivedNotification);
    }
}
