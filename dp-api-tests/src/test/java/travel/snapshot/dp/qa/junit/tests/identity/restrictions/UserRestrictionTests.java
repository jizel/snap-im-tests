package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

/**
 *  Endpoint restrictions for customers
 */
public class UserRestrictionTests extends CommonRestrictionTest{
    private ApplicationDto restrictedApp;
    private ApplicationVersionDto createdAppVersion;
    private UserDto createdUser1;

    private static final String ALL_USERS_ENDPOINT = "/identity/users";
    private static final String SINGLE_USER_ENDPOINT = "/identity/users/{user_id}";
    private static final String USER_CUSTOMER_RELATIONSHIP_ENDPOINT = "/identity/users/{user_id}/customers/{customer_id}";
    private static final String USER_PROPERTIES_ENDPOINT = "/identity/users/{user_id}/properties";
    private static final String USER_PROPERTIES_ROLES_ENDPOINT = "/identity/users/{user_id}/properties/{property_id}/roles";
    private static final String USER_PROPERTY_SETS_ROLES_ENDPOINT = "/identity/users/{user_id}/property_sets/{property_set_id}/roles";
    private static final String USER_CUSTOMERS_ROLES_ENDPOINT = "/identity/users/{user_id}/customers/{customer_id}/roles";


    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @Test
    public void getUserRestrictionTest(){
        userHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), "5", "0", "user_name==defaultSnapshotUser", "user_name", null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), ALL_USERS_ENDPOINT, GET_METHOD);
        userHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), "5", "0", "user_name==defaultSnapshotUser", "user_name", null);
        responseCodeIs(SC_OK);

        userHelpers.getUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseIsNotFound();
        userHelpers.getUserByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_USER_ENDPOINT, GET_METHOD);
        userHelpers.getUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseCodeIs(SC_OK);
        userHelpers.getUserByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId());
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudUserRestrictionTest(){
//        Create
        userHelpers.createUserByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), ALL_USERS_ENDPOINT, POST_METHOD);
        userHelpers.createUserByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2);
        responseCodeIs(SC_CREATED);
//        Update
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("UpdatedUsername");
        userHelpers.updateUserByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId(), userUpdate);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_USER_ENDPOINT, POST_METHOD);
        userHelpers.updateUserByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId(), userUpdate);
        responseCodeIs(SC_NO_CONTENT);
//        Delete
        userHelpers.deleteUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_USER_ENDPOINT, DELETE_METHOD);
        userHelpers.deleteUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId());
//        User is referenced so it cannot be removed - correct response
        responseCodeIs(SC_CONFLICT);
    }

    @Test
    public void getUserSecondLevelEntitiesRestrictionTest(){
        userHelpers.getUserCustomerRelationByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, createdUser1.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), USER_CUSTOMER_RELATIONSHIP_ENDPOINT, GET_METHOD);
        userHelpers.getUserCustomerRelationByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, createdUser1.getId());
        responseCodeIs(SC_OK);

        userHelpers.getAllUserPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), USER_PROPERTIES_ENDPOINT , GET_METHOD);
        userHelpers.getAllUserPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseCodeIs(SC_OK);
    }

    @Test
    public void getUserRolesRestrictionTest(){
//        Property roles
        PropertyDto createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        propertyHelpers.relationExistsBetweenUserAndProperty(createdUser1.getId(), createdProperty1.getId(), true);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), SECOND_LEVEL_OBJECT_PROPERTIES, createdProperty1.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), USER_PROPERTIES_ROLES_ENDPOINT, GET_METHOD);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), SECOND_LEVEL_OBJECT_PROPERTIES, testProperty1.getId());
        responseCodeIs(SC_OK);

//        Customer roles
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), SECOND_LEVEL_OBJECT_CUSTOMERS, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), USER_CUSTOMERS_ROLES_ENDPOINT, GET_METHOD);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), SECOND_LEVEL_OBJECT_CUSTOMERS, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);

//        Property Set roles
        PropertySetDto createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        propertySetHelpers.addUserToPropertySet(createdUser1.getId(), createdPropertySet.getId(), true);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), SECOND_LEVEL_OBJECT_PROPERTY_SETS, createdPropertySet.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), USER_PROPERTY_SETS_ROLES_ENDPOINT, GET_METHOD);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), SECOND_LEVEL_OBJECT_PROPERTY_SETS, createdPropertySet.getId());
        responseCodeIs(SC_OK);
    }
}
