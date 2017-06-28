package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.DATA_OWNER;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;
import java.util.Map;

/**
 * JMS notification tests for entity relationship endpoints
 */
@RunWith(SerenityRunner.class)
public class RelationshipsNotificationTests extends CommonTest{

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/relationships_notification_tests.yaml"));
    private Map<String, Object> receivedNotification;
    private UserDto createdUser1;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @After
    public void cleanUp() throws Exception {
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }


    @Test
    public void userCustomerRelationshipNotificationTest() throws Exception {
        CustomerDto customer = customerHelpers.customerIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserCustomerRelationshipDto userCustomerRelationship = relationshipsHelpers.userCustomerRelationshipIsCreated(createdUser1.getId(), customer.getId(), true, true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserCustomerRelationship(userCustomerRelationship.getId(), false, false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserCustomerRelationship(userCustomerRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPartnerRelationshipNotificationTest() throws Exception {
        PartnerDto partner = partnerHelpers.partnerIsCreated(testPartner1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPartnerRelationshipDto userPartnerRelationship = relationshipsHelpers.userPartnerRelationshipIsCreated(createdUser1.getId(), partner.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserPartnerRelationship(userPartnerRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserPartnerRelationship(userPartnerRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPropertyRelationshipNotificationTest() throws Exception {
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPropertyRelationshipDto userPropertyRelationship = relationshipsHelpers.userPropertyRelationshipIsCreated(createdUser1.getId(), property.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserPropertyRelationship(userPropertyRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserPropertyRelationship(userPropertyRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPropertySetRelationshipNotificationTest() throws Exception {
        PropertySetDto propertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPropertySetRelationshipDto userPropertySetRelationship = relationshipsHelpers.userPropertySetRelationshipIsCreated(createdUser1.getId(), propertySet.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserPropertySetRelationship(userPropertySetRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserPropertySetRelationship(userPropertySetRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void customerPropertyRelationshipNotificationTest() throws Exception {
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        CustomerDto customer = customerHelpers.customerIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        CustomerPropertyRelationshipDto customerPropertyRelationship = relationshipsHelpers.customerPropertyRelationshipIsCreated(
                customer.getId(), property.getId(), true, DATA_OWNER, LocalDate.now(), LocalDate.now().plusMonths(6));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateCustomerPropertyRelationship(customerPropertyRelationship.getId(), false,
                OWNER, LocalDate.now().minusDays(5), LocalDate.now());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteCustomerPropertyRelationship(customerPropertyRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void PropertySetPropertyRelationshipNotificationTest() throws Exception {
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        PropertySetDto propertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        PropertySetPropertyRelationshipDto propertySetPropertyRelationship = relationshipsHelpers.propertySetPropertyRelationshipIsCreated(propertySet.getId(), property.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updatePropertySetPropertyRelationship(propertySetPropertyRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deletePropertySetPropertyRelationship(propertySetPropertyRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupPropertyRelationshipNotificationTest() throws Exception {
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        UserGroupDto userGroup = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = relationshipsHelpers.userGroupPropertyRelationshipIsCreated(userGroup.getId(), property.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserGroupPropertyRelationship(userGroupPropertyRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserGroupPropertyRelationship(userGroupPropertyRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupPropertySetRelationshipNotificationTest() throws Exception {
        PropertySetDto propertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        UserGroupDto userGroup = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = relationshipsHelpers.userGroupPropertySetRelationshipIsCreated(userGroup.getId(), propertySet.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserGroupPropertySetRelationship(userGroupPropertySetRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserGroupPropertySetRelationship(userGroupPropertySetRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupUserRelationshipNotificationTest() throws Exception {
        UserGroupDto userGroup = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupUserRelationshipDto userGroupUserRelationship = relationshipsHelpers.userGroupUserRelationshipIsCreated(userGroup.getId(), createdUser1.getId(), true);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        relationshipsHelpers.updateUserGroupUserRelationship(userGroupUserRelationship.getId(), false);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        relationshipsHelpers.deleteUserGroupUserRelationship(userGroupUserRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
