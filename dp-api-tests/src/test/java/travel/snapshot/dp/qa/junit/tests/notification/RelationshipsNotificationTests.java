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
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
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
    public void setUp() throws Exception {
        super.setUp();
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }


    @Test
    public void userCustomerRelationshipNotificationTest() throws Exception {
        CustomerDto customer = customerHelpers.customerIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userCustomerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserCustomerRelationshipDto testUserCustomerRelationship = relationshipsHelpers.constructUserCustomerRelationshipDto(createdUser1.getId(), customer.getId(), true, true);
        UserCustomerRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, testUserCustomerRelationship);
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
        PartnerDto partner = partnerHelpers.partnerIsCreated(testPartner1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPartnerRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPartnerRelationshipDto testUserPartnerRelationship = relationshipsHelpers.constructUserPartnerRelationshipDto(createdUser1.getId(), partner.getId(), true);
        UserPartnerRelationshipDto createdUserPartnerRelationship = commonHelpers.entityWithTypeIsCreated(USER_PARTNER_RELATIONSHIPS_PATH, UserPartnerRelationshipDto.class, testUserPartnerRelationship);
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
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPropertyRelationshipDto testUserPropertyRelationship = relationshipsHelpers.constructUserPropertyRelationshipDto(createdUser1.getId(), property.getId(), true);
        UserPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, testUserPropertyRelationship);
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
        PropertySetDto propertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserPropertySetRelationshipDto testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(createdUser1.getId(), propertySet.getId(), true);
        UserPropertySetRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
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
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        CustomerDto customer = customerHelpers.customerIsCreated(testCustomer1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "customerPropertyRelationshipNotificationDelete");
        CustomerPropertyRelationshipDto testCustomerPropertyRelationship = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                customer.getId(), property.getId(), true, DATA_OWNER, LocalDate.now(), LocalDate.now().plusMonths(6));

        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        CustomerPropertyRelationshipDto customerPropertyRelationship = commonHelpers.entityWithTypeIsCreated(
                CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipDto.class, testCustomerPropertyRelationship);
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
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        PropertySetDto propertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "propertySetPropertyRelationshipNotificationDelete");
        PropertySetPropertyRelationshipDto testPropertySetPropertyRelationship = relationshipsHelpers.constructPropertySetPropertyRelationship(
                propertySet.getId(), property.getId(), true);
        PropertySetPropertyRelationshipUpdateDto propertySetPropertyRelationshipUpdate = relationshipsHelpers
                .constructPropertySetPropertyRelationshipUpdate(false);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        PropertySetPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
                PropertySetPropertyRelationshipDto.class, testPropertySetPropertyRelationship);
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
        PropertyDto property = propertyHelpers.propertyIsCreated(testProperty1);
        UserGroupDto userGroup = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertyRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupPropertyRelationshipDto testUserGroupPropertyRelationship = relationshipsHelpers.constructUserGroupPropertyRelationship(userGroup.getId(), property.getId(), true);
        UserGroupPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class, testUserGroupPropertyRelationship);
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
        PropertySetDto propertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        UserGroupDto userGroup = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupPropertySetRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserGroupPropertySetRelationshipDto testUserGroupPropertySetRelationship = relationshipsHelpers.constructUserGroupPropertySetRelationship(userGroup.getId(), propertySet.getId(), true);
        UserGroupPropertySetRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class, testUserGroupPropertySetRelationship);
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
        UserGroupDto userGroup = userGroupHelpers.userGroupIsCreated(testUserGroup1);
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationCreate");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationUpdate");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "userGroupUserRelationshipNotificationDelete");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
//        UserGroupUserRelationshipDto userGroupUserRelationship = relationshipsHelpers.userGroupUserRelationshipIsCreated(userGroup.getId(), createdUser1.getId(), true);
        UserGroupUserRelationshipDto userGroupUserRelationship = relationshipsHelpers.constructUserGroupUserRelationship(userGroup.getId(), createdUser1.getId(), true);
        UserGroupUserRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipDto.class, userGroupUserRelationship);
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
