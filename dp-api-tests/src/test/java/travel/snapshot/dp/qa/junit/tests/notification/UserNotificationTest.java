package travel.snapshot.dp.qa.junit.tests.notification;

import static travel.snapshot.dp.qa.cucumber.helpers.RoleType.CUSTOMER;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for User entity
 */
@RunWith(SerenityRunner.class)
public class UserNotificationTest extends CommonTest {
    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/user_notification_tests.yaml"));
    private UserDto createdUser1;
    private UserCreateDto user2;
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdUser1 = userHelpers.userIsCreated(testUser1);
        user2 = entitiesLoader.getUserDtos().get("user2");
    }

    @After
    public void cleanUp() throws Exception {
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
        userHelpers.updateUser(createdUser1.getId(), userUpdate);
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
        RoleDto testRole = roleHelpers.roleIsCreated(entitiesLoader.getCustomerRoleDtos().get("customerRole1"), CUSTOMER);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        userHelpers.userAssignsRoleToRelationWithApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                createdUser1.getId(),
                "customer",
                DEFAULT_SNAPSHOT_CUSTOMER_ID,
                testRole.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
        userHelpers.roleBetweenUserAndEntityIsDeleted("customers", testRole.getId(), createdUser1.getId(), BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID, null);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedDeleteNotification, receivedNotification);
    }

}
