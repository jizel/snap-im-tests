package travel.snapshot.dp.qa.junit.tests.notification;


import static travel.snapshot.dp.qa.cucumber.helpers.RoleType.CUSTOMER;
import static travel.snapshot.dp.qa.cucumber.serenity.roles.RoleBaseSteps.getRoleBaseType;
import static travel.snapshot.dp.qa.junit.helpers.NotificationHelpers.verifyNotification;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.getSingleTestData;
import static travel.snapshot.dp.qa.junit.loaders.YamlLoader.loadTestData;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.RoleBaseDto;
import travel.snapshot.dp.api.identity.model.RoleUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.Map;

/**
 * JMS notification tests for Roles. Only user-customer roles covered here.
 */
@RunWith(SerenityRunner.class)
public class RoleNotificationsTests extends CommonTest{
    private static Map<String, Map<String, Object>> notificationTestsData = loadTestData(String.format(YAML_DATA_PATH, "notifications/role_notification_tests.yaml"));
    private Map<String, Object> receivedNotification;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        roleHelpers.setRolesPathCustomer();
    }

    @After
    public void cleanUp() throws Throwable {
        super.cleanUp();
        jmsSteps.unsubscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
    }

    @Test
    public void createRoleNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "createRoleNotificationTest");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        roleHelpers.roleIsCreated(testCustomerRole1, CUSTOMER);
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void updateRoleNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "updateRoleNotificationTest");
        RoleBaseDto createdRole = roleHelpers.roleIsCreated(testCustomerRole1, CUSTOMER);
        RoleUpdateDto roleUpdate = getRoleBaseType().getDtoClassType().newInstance();
        roleUpdate.setName("Updated Role Name");
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        roleHelpers.updateRole(createdRole.getId(), roleUpdate, roleHelpers.getEntityEtag(createdRole.getId()));
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }

    @Test
    public void deleteRoleNotificationTest() throws Exception {
        Map<String, Object> expectedCreateNotification = getSingleTestData(notificationTestsData, "deleteRoleNotificationTest");
        RoleBaseDto createdRole = roleHelpers.roleIsCreated(testCustomerRole1, CUSTOMER);
        jmsSteps.subscribe(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        roleHelpers.deleteRole(createdRole.getId());
        receivedNotification = jmsSteps.receiveMessage(NOTIFICATION_CRUD_TOPIC, JMS_SUBSCRIPTION_NAME);
        verifyNotification(expectedCreateNotification, receivedNotification);
    }
}
