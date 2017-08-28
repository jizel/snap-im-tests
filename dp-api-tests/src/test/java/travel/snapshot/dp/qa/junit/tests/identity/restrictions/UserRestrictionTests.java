package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

/**
 *  Endpoint restrictions for customers
 */
public class UserRestrictionTests extends CommonRestrictionTest{
    private UserDto createdUser1;

    private UserPropertyRelationshipDto testUserPropertyRelationship;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @Test
    public void getUserRestrictionTest(){
        userHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), "5", "0", "user_name==defaultSnapshotUser", "user_name", null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_USERS_ENDPOINT, GET_METHOD);
        userHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), "5", "0", "user_name==defaultSnapshotUser", "user_name", null);
        responseCodeIs(SC_OK);

        userHelpers.getUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseIsEndpointNotFound();
        userHelpers.getUserByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_USER_ENDPOINT, GET_METHOD);
        userHelpers.getUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseCodeIs(SC_OK);
        userHelpers.getUserByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId());
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudUserRestrictionTest(){
//        Create
        userHelpers.createUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_USERS_ENDPOINT, POST_METHOD);
        userHelpers.createUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2);
        responseCodeIs(SC_CREATED);
//        Update
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("UpdatedUsername");
        userHelpers.updateUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId(), userUpdate);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_USER_ENDPOINT, POST_METHOD);
        userHelpers.updateUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId(), userUpdate);
        responseCodeIs(SC_NO_CONTENT);
//        Delete
        userHelpers.deleteUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_USER_ENDPOINT, DELETE_METHOD);
        userHelpers.deleteUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId());
//        User is referenced so it cannot be removed - correct response
        responseCodeIs(SC_CONFLICT);
    }

    @Test
    public void getUserSecondLevelEntitiesRestrictionTest(){
        userHelpers.getUserCustomerRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, createdUser1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_CUSTOMER_ENDPOINT, GET_METHOD);
        userHelpers.getUserCustomerRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, createdUser1.getId());
        responseCodeIs(SC_OK);

        userHelpers.getAllUserPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_PROPERTIES_ENDPOINT, GET_METHOD);
        userHelpers.getAllUserPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUser1.getId());
        responseCodeIs(SC_OK);
    }

    @Test
    public void getUserRolesRestrictionTest() throws Exception{
//        Property roles
        PropertyDto createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, relationshipsHelpers.constructUserPropertyRelationshipDto(createdUser1.getId(), testProperty1.getId(), true));
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), PROPERTIES_RESOURCE, createdProperty1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_PROPERTIES_ROLES_ENDPOINT, GET_METHOD);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), PROPERTIES_RESOURCE, testProperty1.getId());
        responseCodeIs(SC_OK);

//        Customer roles
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), CUSTOMERS_RESOURCE, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_CUSTOMERS_ROLES_ENDPOINT, GET_METHOD);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), CUSTOMERS_RESOURCE, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);

//        Property Set roles
        PropertySetDto createdPropertySet = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        propertySetHelpers.addUserToPropertySet(createdUser1.getId(), createdPropertySet.getId(), true);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), PROPERTY_SETS_RESOURCE, createdPropertySet.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_PROPERTY_SETS_ROLES_ENDPOINT, GET_METHOD);
        userHelpers.listRolesForRelationByUserForApp(createdUser1.getId(), createdAppVersion.getId(), createdUser1.getId(), PROPERTY_SETS_RESOURCE, createdPropertySet.getId());
        responseCodeIs(SC_OK);
    }
}
