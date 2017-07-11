package travel.snapshot.dp.qa.junit.tests.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_group_property_set_relationships endpoint
 */
public class UserGroupPropertySetRelationshipTests extends CommonTest {
    private PropertySetDto createdPropertySet1;
    private UserGroupDto createdUserGroup1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPropertySet1 = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        createdUserGroup1 = userGroupHelpers.userGroupIsCreated(testUserGroup1);
    }

    @Test
    public void createUserGroupPropertySetRelationship() {
        response = relationshipsHelpers.createUserGroupPropertySetRelationship(createdUserGroup1.getId(), createdPropertySet1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserGroupPropertySetRelationshipDto returnedRelationship = response.as(UserGroupPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySet1.getId()));
        assertThat(returnedRelationship.getUserGroupId(), is(createdUserGroup1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserGroupPropertySetRelationshipDto requestedRelationship = relationshipsHelpers.getUserGroupPropertySetRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserGroupPropertySetRelationshipErrors() {
        relationshipsHelpers.createUserGroupPropertySetRelationship(NON_EXISTENT_ID, createdPropertySet1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserGroupPropertySetRelationship(createdUserGroup1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserGroupPropertySetRelationship() throws Exception {
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = relationshipsHelpers.userGroupPropertySetRelationshipIsCreated(createdUserGroup1.getId(), createdPropertySet1.getId(), true);
        relationshipsHelpers.updateUserGroupPropertySetRelationship(userGroupPropertySetRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertySetRelationshipDto returnedRelationship = relationshipsHelpers.getUserGroupPropertySetRelationship(userGroupPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserGroupPropertySetRelationship(){
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = relationshipsHelpers.userGroupPropertySetRelationshipIsCreated(createdUserGroup1.getId(), createdPropertySet1.getId(), true);
        relationshipsHelpers.deleteUserGroupPropertySetRelationship(userGroupPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserGroupPropertySetRelationshipDto returnedRelationship = relationshipsHelpers.getUserGroupPropertySetRelationship(userGroupPropertySetRelationship.getId());
        assertNull(returnedRelationship);
    }
}
