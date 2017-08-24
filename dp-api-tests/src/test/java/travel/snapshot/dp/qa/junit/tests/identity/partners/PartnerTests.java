package travel.snapshot.dp.qa.junit.tests.identity.partners;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_COMMERCIAL_SUBSCRIPTION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.Map;
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
    public void invalidUpdatePartner() throws Exception {
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        commonHelpers.updateEntity(PARTNERS_PATH, createdPartner1.getId(), invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        commonHelpers.updateEntity(PARTNERS_PATH, createdPartner1.getId(), invalidUpdate);
        responseIsUnprocessableEntity();

        commonHelpers.updateEntity(PARTNERS_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
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

    @Test
    @Jira("DPIM-31")
    public void treatCommercialSubscriptionsAsRelationshipPartnerUser() throws IOException {
        // make user belong to the correct partner
        UUID userId = createdUser.getId();
        UserPartnerRelationshipDto relation = relationshipsHelpers.constructUserPartnerRelationshipDto(userId, DEFAULT_SNAPSHOT_PARTNER_ID, true);
        commonHelpers.entityIsCreated(USER_PARTNER_RELATIONSHIPS_PATH, relation);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserCustomerRelationshipDto userCustomerRelation = relationshipsHelpers.constructUserCustomerRelationshipDto(userId, DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true);
        commonHelpers.entityIsCreated(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelation);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserPropertyRelationshipDto userPropertyRelation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, userPropertyRelation);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
        CommercialSubscriptionUpdateDto update = new CommercialSubscriptionUpdateDto();
        update.setIsActive(false);
        commonHelpers.entityIsUpdated(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID, update);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    @Jira("DPIM-31")
    public void treatCommercialSubscriptionsAsRelationshipCustomerUser() throws IOException {
        UUID userId = commonHelpers.entityIsCreated(USERS_PATH, testUser2);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserPropertyRelationshipDto userPropertyRelation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        commonHelpers.entityIsCreated(USER_PROPERTY_RELATIONSHIPS_PATH, userPropertyRelation);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
        CommercialSubscriptionUpdateDto update = new CommercialSubscriptionUpdateDto();
        update.setIsActive(false);
        commonHelpers.entityIsUpdated(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID, update);
        commonHelpers.getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
    }
}
