package travel.snapshot.dp.qa.junit.tests.notification;


import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.helpers.RoleHelpers.getRoleBaseType;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for Roles. Only user-customer roles covered here.
 */
public class RoleNotificationsTests extends CommonTest{
    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/role_notification_tests.yaml"));
    private Map<String, Object> receivedCreateNotification;
    private Map<String, Object> receivedUpdateNotification;
    private Map<String, Object> receivedDeleteNotification;

    @Before
    public void setUp() {
        super.setUp();
        roleHelpers.setRolesPathCustomer();
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsHelpers.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void roleCRUDNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createRoleNotificationTest");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "updateRoleNotificationTest");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "deleteRoleNotificationTest");
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);

        UUID roleId = entityIsCreated(testRole1);
        receivedCreateNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedCreateNotification);

        RoleUpdateDto roleUpdate = getRoleBaseType().getDtoClassType().newInstance();
        roleUpdate.setName("Updated Role Name");
        entityIsUpdated(ROLES_PATH, roleId, roleUpdate);
        receivedUpdateNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedUpdateNotification);

        entityIsDeleted(ROLES_PATH, roleId);
        receivedDeleteNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedDeleteNotification);
    }

    @Test
    public void CreateUpdateRoleByCustomerUserNotificationTest() throws Exception{
        UUID createdRoleId = entityIsCreated(testRole1);
        UUID createdUserId = entityIsCreated(testUser1);
        jmsHelpers.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);

        createEntityByUserForApplication(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,  testRole2).then().statusCode(SC_CREATED);
        Map<String, Object> receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(getSingleTestData(notificationTestsData, "createRoleByCustomerUserNotificationTest"), receivedNotification);

        RoleUpdateDto RoleUpdateDto = new RoleUpdateDto();
        RoleUpdateDto.setName("Updated Role Name");
        updateEntityByUserForApp(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, ROLES_PATH, createdRoleId, RoleUpdateDto).then().statusCode(SC_OK);
        receivedNotification = jmsHelpers.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(getSingleTestData(notificationTestsData, "updateRoleByCustomerUserNotificationTest"), receivedNotification);
    }
}
