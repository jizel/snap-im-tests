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
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipUpdate;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationshipUpdate;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipUpdate;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationshipUpdate;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationshipUpdate;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationshipUpdate;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipUpdateDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipUpdateDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipUpdateDto;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
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
    public void setUp() {
        super.setUp();
        createdUserId = entityIsCreated(testUser1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Ignore("Remove it once multi-tenancy is back again")
    @Test
    public void userCustomerRelationshipNotificationTest() throws Exception {
        UUID customerId = entityIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserCustomerRelationshipDto(createdUserId, customerId, true, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsUpdated(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, constructUserCustomerRelationshipUpdate(false, false));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPartnerRelationshipNotificationTest() throws Exception {
        UUID partnerId = entityIsCreated(testPartner1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserPartnerRelationshipDto(createdUserId, partnerId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserPartnerRelationshipUpdateDto update = constructUserPartnerRelationshipUpdateDto(false);
        entityIsUpdated(USER_PARTNER_RELATIONSHIPS_PATH, relationId, update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_PARTNER_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = entityIsCreated(testProperty1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserPropertyRelationshipDto(createdUserId, propertyId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserPropertyRelationshipUpdateDto update = constructUserPropertyRelationshipUpdateDto(false);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, relationId, update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userPropertySetRelationshipNotificationTest() throws Exception {
        UUID propertySetId = entityIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserPropertySetRelationshipDto(createdUserId, propertySetId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        UserPropertySetRelationshipUpdateDto update = constructUserPropertySetRelationshipUpdateDto(false);
        entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void customerPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = entityIsCreated(testProperty1);
        UUID customerId = entityIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationDelete");
        CustomerPropertyRelationshipCreateDto testCustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(
                customerId, propertyId, true, DATA_OWNER, LocalDate.now(), LocalDate.now().plusMonths(6));

        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructCustomerPropertyRelationshipDto(customerId, propertyId, true, DATA_OWNER, LocalDate.now(), LocalDate.now().plusMonths(6)));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);

        CustomerPropertyRelationshipUpdateDto update = constructCustomerPropertyRelationshipUpdate(false,
                OWNER, LocalDate.now().minusDays(5), LocalDate.now());
        entityIsUpdated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId, update);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void PropertySetPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = entityIsCreated(testProperty1);
        UUID propertySetId = entityIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructPropertySetPropertyRelationship(propertySetId, propertyId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsUpdated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId, constructPropertySetPropertyRelationshipUpdate(false));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupPropertyRelationshipNotificationTest() throws Exception {
        UUID propertyId = entityIsCreated(testProperty1);
        UUID userGroupId = entityIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserGroupPropertyRelationship(userGroupId, propertyId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsUpdated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, relationId, constructUserGroupPropertyRelationshipUpdate(false));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupPropertySetRelationshipNotificationTest() throws Exception {
        UUID propertySetId = entityIsCreated(testPropertySet1);
        UUID userGroupId = entityIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserGroupPropertySetRelationship(userGroupId, propertySetId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsUpdated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, constructUserGroupPropertySetRelationshipUpdate(false));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

    @Test
    public void userGroupUserRelationshipNotificationTest() throws Exception {
        UUID userGroupId = entityIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID relationId = entityIsCreated(constructUserGroupUserRelationship(userGroupId, createdUserId, true));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId, constructUserGroupUserRelationshipUpdate(false));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedNotification);
        entityIsDeleted(USER_GROUP_USER_RELATIONSHIPS_PATH, relationId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
