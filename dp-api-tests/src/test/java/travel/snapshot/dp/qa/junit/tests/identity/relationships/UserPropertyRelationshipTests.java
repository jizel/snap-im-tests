package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPropertyRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private UserDto createdUser1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @Test
    public void createUserPropertyRelationship() {
        response = relationshipsHelpers.createUserPropertyRelationship(createdUser1.getId(), createdProperty1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertyRelationshipDto returnedRelationship = response.as(UserPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertyRelationshipDto requestedRelationship = relationshipsHelpers.getUserPropertyRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertyRelationshipErrors() {
        relationshipsHelpers.createUserPropertyRelationship(NON_EXISTENT_ID, createdProperty1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserPropertyRelationship(createdUser1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserPropertyRelationship() throws Exception {
        UserPropertyRelationshipDto userPropertyRelationship = relationshipsHelpers.userPropertyRelationshipIsCreated(createdUser1.getId(), createdProperty1.getId(), true);
        relationshipsHelpers.updateUserPropertyRelationship(userPropertyRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getUserPropertyRelationship(userPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertyRelationship(){
        UserPropertyRelationshipDto userPropertyRelationship = relationshipsHelpers.userPropertyRelationshipIsCreated(createdUser1.getId(), createdProperty1.getId(), true);
        relationshipsHelpers.deleteUserPropertyRelationship(userPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getUserPropertyRelationship(userPropertyRelationship.getId());
        assertNull(returnedRelationship);
    }
}
