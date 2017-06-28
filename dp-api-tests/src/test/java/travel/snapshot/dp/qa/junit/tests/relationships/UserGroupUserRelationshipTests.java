package travel.snapshot.dp.qa.junit.tests.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_group_user_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserGroupUserRelationshipTests extends CommonTest {
    private UserDto createdUser1;
    private UserGroupDto createdUserGroup1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdUser1 = userHelpers.userIsCreated(testUser1);
        createdUserGroup1 = userGroupHelpers.userGroupIsCreated(testUserGroup1);
    }

    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void createUserGroupUserRelationship() {
        response = relationshipsHelpers.createUserGroupUserRelationship(createdUserGroup1.getId(), createdUser1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupUserRelationshipDto returnedRelationship = response.as(UserGroupUserRelationshipDto.class);
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroup1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupUserRelationshipDto requestedRelationship = relationshipsHelpers.getUserGroupUserRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupUserRelationshipErrors() {
        relationshipsHelpers.createUserGroupUserRelationship(NON_EXISTENT_ID, createdUser1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserGroupUserRelationship(createdUserGroup1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserGroupUserRelationship() throws Exception {
        UserGroupUserRelationshipDto userGroupUserRelationship = relationshipsHelpers.userGroupUserRelationshipIsCreated(createdUserGroup1.getId(), createdUser1.getId(), true);
        relationshipsHelpers.updateUserGroupUserRelationship(userGroupUserRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupUserRelationshipDto returnedRelationship = relationshipsHelpers.getUserGroupUserRelationship(userGroupUserRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupUserRelationship(){
        UserGroupUserRelationshipDto userGroupUserRelationship = relationshipsHelpers.userGroupUserRelationshipIsCreated(createdUserGroup1.getId(), createdUser1.getId(), true);
        relationshipsHelpers.deleteUserGroupUserRelationship(userGroupUserRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserGroupUserRelationshipDto returnedRelationship = relationshipsHelpers.getUserGroupUserRelationship(userGroupUserRelationship.getId());
        assertNull(returnedRelationship);
    }
}
