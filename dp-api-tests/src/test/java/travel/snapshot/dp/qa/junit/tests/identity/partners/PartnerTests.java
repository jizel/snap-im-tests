package travel.snapshot.dp.qa.junit.tests.identity.partners;

import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Created by ofayans on 7/11/17.
 */
public class PartnerTests extends CommonTest{
    private PartnerDto createdPartner1 = null;
    private UserDto createdUser = null;

    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdPartner1 = partnerHelpers.partnerIsCreated(testPartner1);
        testUser1.setType(UserUpdateDto.UserType.PARTNER);
        testUser1.setUserCustomerRelationship(null);
        createdUser = userHelpers.userIsCreated(testUser1);
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
