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
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_partner_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class UserPartnerRelationshipTests extends CommonTest{
    private PartnerDto createdPartner1;
    private UserDto createdUser1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPartner1 = partnerHelpers.partnerIsCreated(testPartner1);
        createdUser1 = userHelpers.userIsCreated(testUser1);
    }

    @Test
    public void createUserPartnerRelationship() {
        response = relationshipsHelpers.createUserPartnerRelationship(createdUser1.getId(), createdPartner1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        UserPartnerRelationshipDto returnedRelationship = response.as(UserPartnerRelationshipDto.class);
        assertThat(returnedRelationship.getPartnerId(), is(createdPartner1.getId()));
        assertThat(returnedRelationship.getUserId(), is(createdUser1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        UserPartnerRelationshipDto requestedRelationship = relationshipsHelpers.getUserPartnerRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ",requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createUserPartnerRelationshipErrors() {
        relationshipsHelpers.createUserPartnerRelationship(NON_EXISTENT_ID, createdPartner1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createUserPartnerRelationship(createdUser1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updateUserPartnerRelationship() throws Exception {
        UserPartnerRelationshipDto userPartnerRelationship = relationshipsHelpers.userPartnerRelationshipIsCreated(createdUser1.getId(), createdPartner1.getId(), true);
        relationshipsHelpers.updateUserPartnerRelationship(userPartnerRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        UserPartnerRelationshipDto returnedRelationship = relationshipsHelpers.getUserPartnerRelationship(userPartnerRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deleteUserPartnerRelationship(){
        UserPartnerRelationshipDto userPartnerRelationship = relationshipsHelpers.userPartnerRelationshipIsCreated(createdUser1.getId(), createdPartner1.getId(), true);
        relationshipsHelpers.deleteUserPartnerRelationship(userPartnerRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        UserPartnerRelationshipDto returnedRelationship = relationshipsHelpers.getUserPartnerRelationship(userPartnerRelationship.getId());
        assertNull(returnedRelationship);
    }
}
