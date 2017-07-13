package travel.snapshot.dp.qa.junit.tests.identity.relationships;

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
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_property_set_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPropertySetRelationshipTests extends CommonTest {
    private PropertySetDto createdPropertySet1;
    private UserDto createdUser1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPropertySet1 = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @Test
    public void createUserPropertySetRelationship() {
        response = relationshipsHelpers.createUserPropertySetRelationship(createdUser1.getId(), createdPropertySet1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertySetRelationshipDto returnedRelationship = response.as(UserPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySet1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertySetRelationshipDto requestedRelationship = relationshipsHelpers.getUserPropertySetRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertySetRelationshipErrors() {
        relationshipsHelpers.createUserPropertySetRelationship(NON_EXISTENT_ID, createdPropertySet1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserPropertySetRelationship(createdUser1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserPropertySetRelationship() throws Exception {
        UserPropertySetRelationshipDto userPropertySetRelationship = relationshipsHelpers.userPropertySetRelationshipIsCreated(createdUser1.getId(), createdPropertySet1.getId(), true);
        relationshipsHelpers.updateUserPropertySetRelationship(userPropertySetRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertySetRelationshipDto returnedRelationship = relationshipsHelpers.getUserPropertySetRelationship(userPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertySetRelationship(){
        UserPropertySetRelationshipDto userPropertySetRelationship = relationshipsHelpers.userPropertySetRelationshipIsCreated(createdUser1.getId(), createdPropertySet1.getId(), true);
        relationshipsHelpers.deleteUserPropertySetRelationship(userPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserPropertySetRelationshipDto returnedRelationship = relationshipsHelpers.getUserPropertySetRelationship(userPropertySetRelationship.getId());
        assertNull(returnedRelationship);
    }
}
