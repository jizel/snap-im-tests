package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.cucumber.helpers.RoleType.CUSTOMER;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
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
    private UserCreateDto user2;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdUserId = commonHelpers.entityIsCreated(testUser1);
        user2 = entitiesLoader.getUserDtos().get("user2");
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
        userHelpers.userIsCreated(user2);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateUserNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateUserNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("Updated Username");
        userHelpers.updateUser(createdUserId, userUpdate);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteUserNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteUserNotificationTest");
//        Customer type user cannot be deleted because it references customer and vice versa
        UserDto testSnapshotUser = userHelpers.userIsCreated(testSnapshotUser1);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        userHelpers.deleteUser(testSnapshotUser.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

//        -------------------< Second level entities >-----------------

//    DP-2180
    @Ignore
    @Test
    public void addRoleToUserNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "addRoleToUserNotificationTest");
        Map<String, Object> expectedDeleteNotification = getSingleTestData(notificationTestsData, "deleteRoleFromUserNotificationTest");
        RoleBaseDto testRole = roleHelpers.roleIsCreated(entitiesLoader.getCustomerRoleDtos().get("customerRole1"), CUSTOMER);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        userHelpers.userAssignsRoleToRelationWithApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                createdUserId,
                "customer",
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                testRole.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        userHelpers.roleBetweenUserAndEntityIsDeleted("customers", testRole.getId(), createdUserId, DEFAULT_SNAPSHOT_CUSTOMER_ID, null);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

}
