package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_property_set_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPropertySetRelationshipTests extends CommonTest {
    private UUID createdPropertySetId;
    private UUID createdUserId;
    private UserPropertySetRelationshipCreateDto testUserPropertySetRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdPropertySetId = commonHelpers.entityIsCreated(testPropertySet1);
        createdUserId = commonHelpers.entityIsCreated(testUser1);
        testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(createdUserId, createdPropertySetId, true);
    }

    @Test
    public void createUserPropertySetRelationship() {
        Response response = commonHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPropertySetRelationshipCreateDto returnedRelationship = response.as(UserPropertySetRelationshipDto.class);
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySetId));
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPropertySetRelationshipCreateDto requestedRelationship = commonHelpers.getEntityAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPropertySetRelationshipErrors() {
        testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(NON_EXISTENT_ID, createdPropertySetId, true);
        commonHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserPropertySetRelationship = relationshipsHelpers.constructUserPropertySetRelationshipDto(createdUserId, NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, testUserPropertySetRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserPropertySetRelationship() throws Exception {
        UserPropertySetRelationshipCreateDto UserPropertySetRelationship = commonHelpers.entityIsCreatedAs(UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        UserPropertySetRelationshipUpdateDto update = relationshipsHelpers.constructUserPropertySetRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserPropertySetRelationshipCreateDto returnedRelationship = commonHelpers.getEntityAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, UserPropertySetRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPropertySetRelationship(){
        UserPropertySetRelationshipCreateDto UserPropertySetRelationship = commonHelpers.entityIsCreatedAs(UserPropertySetRelationshipDto.class, testUserPropertySetRelationship);
        commonHelpers.deleteEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
