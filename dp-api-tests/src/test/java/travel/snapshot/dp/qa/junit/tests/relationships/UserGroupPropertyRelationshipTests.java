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
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_group_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserGroupPropertyRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private UserGroupDto createdUserGroup1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdUserGroup1 = userGroupHelpers.userGroupIsCreated(testUserGroup1);
    }

    @Test
    public void createUserGroupPropertyRelationship() {
        response = relationshipsHelpers.createUserGroupPropertyRelationship(createdUserGroup1.getId(), createdProperty1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupPropertyRelationshipDto returnedRelationship = response.as(UserGroupPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroup1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupPropertyRelationshipDto requestedRelationship = relationshipsHelpers.getUserGroupPropertyRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupPropertyRelationshipErrors() {
        relationshipsHelpers.createUserGroupPropertyRelationship(NON_EXISTENT_ID, createdProperty1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserGroupPropertyRelationship(createdUserGroup1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserGroupPropertyRelationship() throws Exception {
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = relationshipsHelpers.userGroupPropertyRelationshipIsCreated(createdUserGroup1.getId(), createdProperty1.getId(), true);
        relationshipsHelpers.updateUserGroupPropertyRelationship(userGroupPropertyRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getUserGroupPropertyRelationship(userGroupPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupPropertyRelationship(){
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = relationshipsHelpers.userGroupPropertyRelationshipIsCreated(createdUserGroup1.getId(), createdProperty1.getId(), true);
        relationshipsHelpers.deleteUserGroupPropertyRelationship(userGroupPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getUserGroupPropertyRelationship(userGroupPropertyRelationship.getId());
        assertNull(returnedRelationship);
    }
}
