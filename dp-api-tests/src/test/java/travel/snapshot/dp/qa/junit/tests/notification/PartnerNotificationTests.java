package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Partner entity
 */
@RunWith(SerenityRunner.class)
public class PartnerNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/partner_notification_tests.yaml"));
    Map<String, Object> receivedNotification;
    PartnerDto testPartner1 = entitiesLoader.getPartnerDtos().get("partner1");

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() throws Exception{
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createPartnerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createPartnerNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        partnerHelpers.partnerIsCreated(testPartner1);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePartnerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePartnerNotificationTest");
        partnerHelpers.partnerIsCreated(testPartner1);
        PartnerUpdateDto partnerUpdate = new PartnerUpdateDto();
        partnerUpdate.setName("Updated partner name");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        partnerSteps.updatePartner(testPartner1.getId(), partnerUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deletePartnerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deletePartnerNotificationTest");
        partnerHelpers.partnerIsCreated(testPartner1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        partnerSteps.deletePartner(testPartner1.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }
}
