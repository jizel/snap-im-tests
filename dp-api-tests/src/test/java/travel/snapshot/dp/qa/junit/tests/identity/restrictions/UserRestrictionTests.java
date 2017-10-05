package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.type.HttpMethod.DELETE;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

import java.util.UUID;

/**
 *  Endpoint restrictions for users
 */
public class UserRestrictionTests extends CommonRestrictionTest{
    private UUID createdUserId;

    private UserPropertyRelationshipCreateDto testUserPropertyRelationship;


    @Before
    public void setUp() {
        super.setUp();
        createdUserId = entityIsCreated(testUser1);
    }

    @Test
    public void getUserRestrictionTest(){
        userHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), "5", "0", "user_name==defaultSnapshotUser", "user_name", null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_USERS_ENDPOINT, GET);
        userHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), "5", "0", "user_name==defaultSnapshotUser", "user_name", null);
        responseCodeIs(SC_OK);

        userHelpers.getUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUserId);
        responseIsEndpointNotFound();
        userHelpers.getUserByUserForApp(createdUserId, createdAppVersion.getId(), createdUserId);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_USER_ENDPOINT, GET);
        userHelpers.getUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUserId);
        responseCodeIs(SC_OK);
        userHelpers.getUserByUserForApp(createdUserId, createdAppVersion.getId(), createdUserId);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudUserRestrictionTest(){
//        Create
        userHelpers.createUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_USERS_ENDPOINT, POST);
        userHelpers.createUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2);
        responseCodeIs(SC_CREATED);
//        Update
        UserUpdateDto userUpdate = new UserUpdateDto();
        userUpdate.setUsername("UpdatedUsername");
        userHelpers.updateUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId(), userUpdate);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_USER_ENDPOINT, POST);
        userHelpers.updateUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId(), userUpdate);
        responseCodeIs(SC_NO_CONTENT);
//        Delete
        userHelpers.deleteUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_USER_ENDPOINT, DELETE);
        userHelpers.deleteUserByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testUser2.getId());
//        User is referenced so it cannot be removed - correct response
        responseCodeIs(SC_CONFLICT);
    }

    @Test
    public void getUserSecondLevelEntitiesRestrictionTest(){
        userHelpers.getUserCustomerRelationByUserForApp(createdUserId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, createdUserId);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_CUSTOMER_ENDPOINT, GET);
        userHelpers.getUserCustomerRelationByUserForApp(createdUserId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, createdUserId);
        responseCodeIs(SC_OK);

        userHelpers.getAllUserPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUserId);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_PROPERTIES_ENDPOINT, GET);
        userHelpers.getAllUserPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), createdUserId);
        responseCodeIs(SC_OK);
    }

    @Test
    public void getUserRolesRestrictionTest() throws Exception{
//        Property roles
        PropertyDto createdProperty1 = entityIsCreatedAs(PropertyDto.class, testProperty1);
        entityIsCreated(relationshipsHelpers.constructUserPropertyRelationshipDto(createdUserId, testProperty1.getId(), true));
        userHelpers.listRolesForRelationByUserForApp(createdUserId, createdAppVersion.getId(), createdUserId, PROPERTIES_RESOURCE, createdProperty1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_PROPERTIES_ROLES_ENDPOINT, GET);
        userHelpers.listRolesForRelationByUserForApp(createdUserId, createdAppVersion.getId(), createdUserId, PROPERTIES_RESOURCE, testProperty1.getId());
        responseCodeIs(SC_OK);

//        Customer roles
        userHelpers.listRolesForRelationByUserForApp(createdUserId, createdAppVersion.getId(), createdUserId, CUSTOMERS_RESOURCE, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_USER_CUSTOMERS_ROLES_ENDPOINT, GET);
        userHelpers.listRolesForRelationByUserForApp(createdUserId, createdAppVersion.getId(), createdUserId, CUSTOMERS_RESOURCE, DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);
    }
}
