package travel.snapshot.dp.qa.junit.tests.identity.partners;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Tests for IM Partner entity
 */
public class PartnerTests extends CommonTest{
    private PartnerDto createdPartner1 = null;
    private UserDto createdUser = null;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdPartner1 = partnerHelpers.partnerIsCreated(testPartner1);
        testUser1.setType(UserUpdateDto.UserType.PARTNER);
        testUser1.setUserCustomerRelationship(null);
        createdUser = userHelpers.userIsCreated(testUser1);
    }

    @Test
    public void updatePartner() throws Exception {
        PartnerUpdateDto partnerUpdate = new PartnerUpdateDto();
        partnerUpdate.setName("Updated name");
        partnerUpdate.setEmail("updated@snapshot.travel");
        partnerUpdate.setWebsite("http://www.updated.partner.com");
        partnerUpdate.setIsActive(false);
        Response updateResponse = commonHelpers.updateEntity(PARTNERS_PATH, createdPartner1.getId(), partnerUpdate);
        responseCodeIs(SC_OK);
        PartnerDto updateResponsePartner = updateResponse.as(PartnerDto.class);
        PartnerDto requestedPartner = commonHelpers.getEntityAsType(PARTNERS_PATH, PartnerDto.class, createdPartner1.getId());
        assertThat("Update response body differs from the same partner requested by GET ", updateResponsePartner, is(requestedPartner));
    }

    @Test
    public void testDP2194() throws Throwable {
        UUID requestorId = createdUser.getId();
        partnerHelpers.createPartnerUserRelationship(createdPartner1.getId(), requestorId);
        partnerHelpers.getUsersForPartnerByUserForApp(createdPartner1.getId(), requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(PartnerUserRelationshipPartialDto.class, 1);
        userHelpers.getPartnersForUserByUserForApp(requestorId, requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        responseCodeIs(SC_OK);
        numberOfEntitiesInResponse(UserPartnerRelationshipPartialDto.class, 1);
    }
}
