package travel.snapshot.dp.qa.junit.tests.identity.partners;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_COMMERCIAL_SUBSCRIPTION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.numberOfEntitiesInResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Tests for IM Partner entity
 */
public class PartnerTests extends CommonTest{
    private UUID createdPartner1Id;
    private UUID createdUserId;

    @Before
    public void setUp() {
        super.setUp();
        createdPartner1Id = entityIsCreated(testPartner1);
        testUser1.setType(UserUpdateDto.UserType.PARTNER);
        testUser1.setUserCustomerRelationship(null);
        createdUserId = entityIsCreated(testUser1);
    }

    @Test
    public void updatePartner() throws Exception {
        PartnerUpdateDto partnerUpdate = new PartnerUpdateDto();
        partnerUpdate.setName("Updated name");
        partnerUpdate.setEmail("updated@snapshot.travel");
        partnerUpdate.setWebsite("http://www.updated.partner.com");
        partnerUpdate.setIsActive(false);
        Response updateResponse = updateEntity(PARTNERS_PATH, createdPartner1Id, partnerUpdate);
        responseCodeIs(SC_OK);
        PartnerDto updateResponsePartner = updateResponse.as(PartnerDto.class);
        PartnerDto requestedPartner = getEntityAsType(PARTNERS_PATH, PartnerDto.class, createdPartner1Id);
        assertThat("Update response body differs from the same partner requested by GET ", updateResponsePartner, is(requestedPartner));
    }

    @Test
    public void invalidUpdatePartner() throws Exception {
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        updateEntity(PARTNERS_PATH, createdPartner1Id, invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        updateEntity(PARTNERS_PATH, createdPartner1Id, invalidUpdate);
        responseIsUnprocessableEntity();

        updateEntity(PARTNERS_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
    }

    @Test
    public void testDP2194() throws Throwable {
        UUID requestorId = createdUserId;
        partnerHelpers.createPartnerUserRelationship(createdPartner1Id, requestorId);
        partnerHelpers.getUsersForPartnerByUserForApp(createdPartner1Id, requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
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
        UUID userId = createdUserId;
        UserPartnerRelationshipCreateDto relation = relationshipsHelpers.constructUserPartnerRelationshipDto(userId, DEFAULT_SNAPSHOT_PARTNER_ID, true);
        entityIsCreated(relation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserCustomerRelationshipCreateDto userCustomerRelation = relationshipsHelpers.constructUserCustomerRelationshipDto(userId, DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true);
        entityIsCreated(userCustomerRelation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserPropertyRelationshipCreateDto userPropertyRelation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        entityIsCreated(userPropertyRelation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
        CommercialSubscriptionUpdateDto update = new CommercialSubscriptionUpdateDto();
        update.setIsActive(false);
        entityIsUpdated(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID, update);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    @Jira("DPIM-31")
    public void treatCommercialSubscriptionsAsRelationshipCustomerUser() throws IOException {
        UUID userId = entityIsCreated(testUser2);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserPropertyRelationshipCreateDto userPropertyRelation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        entityIsCreated(userPropertyRelation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
        CommercialSubscriptionUpdateDto update = new CommercialSubscriptionUpdateDto();
        update.setIsActive(false);
        entityIsUpdated(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID, update);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
    }
}
