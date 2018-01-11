package travel.snapshot.dp.qa.junit.tests.notification;


import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps.getRoleBaseType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
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
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void roleCRUDNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createRoleNotificationTest");
        Map<String, Object> expectedUpdateNotification = getSingleTestData(notificationTestsData, "updateRoleNotificationTest");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "deleteRoleNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);

        UUID roleId = entityIsCreated(testRole1);
        receivedCreateNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedCreateNotification);

        RoleUpdateDto roleUpdate = getRoleBaseType().getDtoClassType().newInstance();
        roleUpdate.setName("Updated Role Name");
        entityIsUpdated(ROLES_PATH, roleId, roleUpdate);
        receivedUpdateNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedUpdateNotification, receivedUpdateNotification);

        entityIsDeleted(ROLES_PATH, roleId);
        receivedDeleteNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedDeleteNotification);
    }
}
