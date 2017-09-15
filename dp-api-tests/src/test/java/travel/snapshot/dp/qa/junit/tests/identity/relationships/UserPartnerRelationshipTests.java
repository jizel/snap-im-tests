package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_partner_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPartnerRelationshipTests extends CommonTest{
    private UUID createdPartnerId;
    private UUID createdUserId;
    private UserPartnerRelationshipCreateDto testUserPartnerRelationship;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdPartnerId = commonHelpers.entityIsCreated(testPartner1);
        createdUserId = commonHelpers.entityIsCreated(testUser1);
        testUserPartnerRelationship = relationshipsHelpers.constructUserPartnerRelationshipDto(createdUserId, createdPartnerId, true);
    }

    @Test
    public void createUserPartnerRelationship() {
        Response response = commonHelpers.createEntity(USER_PARTNER_RELATIONSHIPS_PATH, testUserPartnerRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPartnerRelationshipCreateDto returnedRelationship = response.as(UserPartnerRelationshipDto.class);
        assertThat(returnedRelationship.getPartnerId(), is(createdPartnerId));
        assertThat(returnedRelationship.getUserId(), is(createdUserId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPartnerRelationshipCreateDto requestedRelationship = commonHelpers.getEntityAsType(USER_PARTNER_RELATIONSHIPS_PATH, UserPartnerRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ",requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPartnerRelationshipErrors() {
        testUserPartnerRelationship = relationshipsHelpers.constructUserPartnerRelationshipDto(NON_EXISTENT_ID, createdPartnerId, true);
        commonHelpers.createEntity(USER_PARTNER_RELATIONSHIPS_PATH, testUserPartnerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
        testUserPartnerRelationship = relationshipsHelpers.constructUserPartnerRelationshipDto(createdUserId, NON_EXISTENT_ID, true);
        commonHelpers.createEntity(USER_PARTNER_RELATIONSHIPS_PATH, testUserPartnerRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updateUserPartnerRelationship() throws Exception {
        UserPartnerRelationshipCreateDto userPartnerRelationship = commonHelpers.entityIsCreatedAs(UserPartnerRelationshipDto.class, testUserPartnerRelationship);
        UserPartnerRelationshipUpdateDto update = relationshipsHelpers.constructUserPartnerRelationshipUpdateDto(false);
        commonHelpers.updateEntityPost(USER_PARTNER_RELATIONSHIPS_PATH, userPartnerRelationship.getId(), update);
        responseCodeIs(SC_NO_CONTENT);
        UserPartnerRelationshipCreateDto requestedRelationship = commonHelpers.getEntityAsType(USER_PARTNER_RELATIONSHIPS_PATH, UserPartnerRelationshipDto.class, userPartnerRelationship.getId());
        assertThat(requestedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPartnerRelationship(){
        UserPartnerRelationshipCreateDto userPartnerRelationship = commonHelpers.entityIsCreatedAs(UserPartnerRelationshipDto.class, testUserPartnerRelationship);
        commonHelpers.deleteEntity(USER_PARTNER_RELATIONSHIPS_PATH, userPartnerRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_PARTNER_RELATIONSHIPS_PATH, userPartnerRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
