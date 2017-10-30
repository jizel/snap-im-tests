package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLE_ASSIGNMENTS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructRoleAssignment;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;
import java.util.UUID;

/**
 * JMS notification tests for User entity
 */
@RunWith(SerenityRunner.class)
public class UserNotificationTest extends CommonTest {
    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/user_notification_tests.yaml"));
    private UUID createdUserId;
    private Map<String, Object> receivedNotification;

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

    @Test
    public void createUserNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createUserNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsCreated(testUser2);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateUserNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateUserNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("Updated Username");
        entityIsUpdated(USERS_PATH, createdUserId, userUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteUserNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteUserNotificationTest");
//        Customer type user cannot be deleted because it references customer and vice versa
        UUID snapshotUserId = entityIsCreated(testSnapshotUser1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        entityIsDeleted(USERS_PATH, snapshotUserId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

//    DP-2180
    @Ignore
    @Test
    public void roleAssignmentNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "addRoleToUserNotificationTest");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "deleteRoleFromUserNotificationTest");
        UUID roleId = entityIsCreated(testRole1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UUID assignmentId = entityIsCreated(constructRoleAssignment(roleId, createdUserId));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        entityIsDeleted(ROLE_ASSIGNMENTS_PATH, assignmentId);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }
}
