package travel.snapshot.dp.qa.junit.tests.notification;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.DATA_OWNER;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.OWNER;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for entity relationship endpoints
 */
@RunWith(SerenityRunner.class)
public class RelationshipsNotificationTests extends CommonTest {

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/relationships_notification_tests.yaml"));
    private Map<String, Object> receivedNotification;
    private UUID createdUserId;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdUserId = commonHelpers.entityIsCreated(testUser1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }


    @Test
    public void userCustomerRelationshipNotificationTest() throws Exception {
        UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserCustomerRelationshipCreateDto testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUserId, customerId, true, true);
        UserCustomerRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(UserCustomerRelationshipDto.class, testUserCustomerRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserCustomerRelationshipUpdateDto update = relationshipsHelpers.constructUserCustomerRelationshipUpdate(false, false);
        commonHelpers.updateEntityPost(USER_CUSTOMER_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPartnerRelationshipNotificationTest() throws Exception {
        UUID partnerId = commonHelpers.entityIsCreated(testPartner1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPartnerRelationshipCreateDto testUserPartnerRelationship = relationshipsHelpers.constructUserPartnerRelationshipDto(createdUserId, partnerId, true);
        UserPartnerRelationshipCreateDto createdUserPartnerRelationship = commonHelpers.entityIsCreatedAs(UserPartnerRelationshipDto.class, testUserPartnerRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserPartnerRelationshipUpdateDto update = relationshipsHelpers.constructUserPartnerRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PARTNER_RELATIONSHIPS_PATH, createdUserPartnerRelationship.getId(), update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_PARTNER_RELATIONSHIPS_PATH, createdUserPartnerRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = commonHelpers.entityIsCreated(testProperty1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPropertyRelationshipCreateDto testUserPropertyRelationship = relationshipsHelpers.constructUserPropertyRelationshipDto(createdUserId, propertyId, true);
        UserPropertyRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(UserPropertyRelationshipDto.class, testUserPropertyRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserPropertyRelationshipUpdateDto update = relationshipsHelpers.constructUserPropertyRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPropertySetRelationshipNotificationTest() throws Exception {
        UUID propertySetId = commonHelpers.entityIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPropertySetRelationshipCreateDto testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(createdUserId, propertySetId, true);
        UserPropertySetRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserPropertySetRelationshipUpdateDto update = relationshipsHelpers.constructUserPropertySetRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PROPERTY_SET_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void customerPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = commonHelpers.entityIsCreated(testProperty1);
        UUID customerId = commonHelpers.entityIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationDelete");
        CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                customerId, propertyId, true, DATA_OWNER, LocalDate.now(), LocalDate.now().plusMonths(6));

        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        CustomerPropertyRelationshipCreateDto customerPropertyRelationship = commonHelpers.entityIsCreatedAs(CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);

        CustomerPropertyRelationshipUpdateDto update = relationshipsHelpers.constructCustomerPropertyRelationshipUpdate(false,
                OWNER, LocalDate.now().minusDays(5), LocalDate.now());
        commonHelpers.updateEntityPost(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, customerPropertyRelationship.getId(), update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, customerPropertyRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void PropertySetPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = commonHelpers.entityIsCreated(testProperty1);
        UUID propertySetId = commonHelpers.entityIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationDelete");
        PropertySetPropertyRelationshipCreateDto testPropertySetPropertyRelationship = relationshipsHelpers.constructPropertySetPropertyRelationship(
                propertySetId, propertyId, true);
        PropertySetPropertyRelationshipUpdateDto propertySetPropertyRelationshipUpdate = relationshipsHelpers
                .constructPropertySetPropertyRelationshipUpdate(false);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        PropertySetPropertyRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(PropertySetPropertyRelationshipDto.class, testPropertySetPropertyRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        commonHelpers.updateEntityPost(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), propertySetPropertyRelationshipUpdate);
        responseCodeIs(SC_NO_CONTENT);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = commonHelpers.entityIsCreated(testProperty1);
        UUID userGroupId = commonHelpers.entityIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupPropertyRelationshipCreateDto testUserGroupPropertyRelationship = relationshipsHelpers.constructUserGroupPropertyRelationship(userGroupId, propertyId, true);
        UserGroupPropertyRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(UserGroupPropertyRelationshipDto.class, testUserGroupPropertyRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserGroupPropertyRelationshipUpdateDto update = relationshipsHelpers.constructUserGroupPropertyRelationshipUpdate(false);
        commonHelpers.updateEntityPost(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupPropertySetRelationshipNotificationTest() throws Exception {
        UUID propertySetId = commonHelpers.entityIsCreated(testPropertySet1);
        UUID userGroupId = commonHelpers.entityIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupPropertySetRelationshipCreateDto testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(userGroupId, propertySetId, true);
        UserGroupPropertySetRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(UserGroupPropertySetRelationshipDto.class, testUserGroupPropertySetRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        commonHelpers.updateEntityPost(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH,
                createdRelationship.getId(),
                relationshipsHelpers.constructUserGroupPropertySetRelationshipUpdate(false));

        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupUserRelationshipNotificationTest() throws Exception {
        UUID userGroupId = commonHelpers.entityIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupUserRelationshipCreateDto userGroupUserRelationship = relationshipsHelpers.constructUserGroupUserRelationship(userGroupId, createdUserId, true);
        UserGroupUserRelationshipCreateDto createdRelationship = commonHelpers.entityIsCreatedAs(UserGroupUserRelationshipDto.class, userGroupUserRelationship);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        commonHelpers.updateEntityPost(USER_GROUP_USER_RELATIONSHIPS_PATH,
                createdRelationship.getId(),
                relationshipsHelpers.constructUserGroupUserRelationshipUpdate(false));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        commonHelpers.deleteEntity(USER_GROUP_USER_RELATIONSHIPS_PATH, createdRelationship.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
