package travel.snapshot.dp.qa.junit.tests.notification;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for Customer entity
 */

public class CustomerNotificationTests extends CommonTest {

    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/customer_notification_tests.yaml"));
    private UUID createdCustomerId;

    @Before
    public void setUp() {
        super.setUp();
        createdCustomerId = entityIsCreated(testCustomer1);
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsHelpers.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createCustomerNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(testCustomer2);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateCustomerNotificationTest() throws Exception{
//        Prepare data
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updateCustomerNotificationTest");
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Update Customer Name");
//        Get update notification and verify
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsUpdated(CUSTOMERS_PATH, createdCustomerId, customerUpdate);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void deleteCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "deleteCustomerNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsDeleted(CUSTOMERS_PATH, createdCustomerId);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void CRUDCustomerByCustomerUserNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createCustomerByCustomerUserNotificationTest");
        UUID createdUserId = entityIsCreated(testUser1);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        createEntityByUserForApplication(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,  testCustomer2).then().statusCode(SC_CREATED);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);

        CustomerUpdateDto customerUpdateDto = new CustomerUpdateDto();
        customerUpdateDto.setName("Updated Customer Name");
        updateEntityByUserForApp(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, CUSTOMERS_PATH, DEFAULT_SNAPSHOT_CUSTOMER_ID, customerUpdateDto).then().statusCode(SC_OK);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(getSingleTestData(notificationTestsData, "updateCustomerByCustomerUserNotificationTest"), receivedNotification);
    }

    //        -------------------< Second level entities >-----------------

    @Test
    public void addPropertyToCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "addPropertyToCustomerNotificationTest");
        UUID testPropertyId = entityIsCreated(testProperty1);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(constructCustomerPropertyRelationshipDto(createdCustomerId, testPropertyId, true, CHAIN, validFrom, validTo));
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updatePropertyForCustomerNotificationTest() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "updatePropertyForCustomerNotificationTest");
        UUID testPropertyId = entityIsCreated(testProperty1);
        UUID relationId = entityIsCreated(constructCustomerPropertyRelationshipDto(createdCustomerId, testPropertyId, true, CHAIN, validFrom, validTo));
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        CustomerPropertyRelationshipUpdateDto update = new CustomerPropertyRelationshipUpdateDto();
        update.setValidFrom(LocalDate.of(2015, 1, 1));
        entityIsUpdated(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId, update);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }

    @Test
    public void addAndRemoveCustomerUser() throws Exception{
        Map<String, Object> expectedNotification = getSingleTestData(notificationTestsData, "addAndRemoveCustomerUser");
        UUID testUserId = entityIsCreated(testUser1);
        userHelpers.deleteUserCustomerRelationshipIfExists(testUserId);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(constructUserCustomerRelationshipDto(testUserId, createdCustomerId, true, true));
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedNotification, receivedNotification);
    }


}
