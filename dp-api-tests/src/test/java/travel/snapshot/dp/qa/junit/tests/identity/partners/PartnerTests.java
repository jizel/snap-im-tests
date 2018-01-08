package travel.snapshot.dp.qa.junit.tests.identity.partners;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_COMMERCIAL_SUBSCRIPTION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_EMAIL;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_NAME;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPartnerRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserType;
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
        testUser1.setType(UserType.PARTNER);
        testUser1.setUserCustomerRelationship(null);
        createdUserId = entityIsCreated(testUser1);
    }

    @Test
    public void partnerCRUD() throws Exception {
        PartnerUpdateDto partnerUpdate = new PartnerUpdateDto();
        partnerUpdate.setName("Updated name");
        partnerUpdate.setEmail("updated@snapshot.travel");
        partnerUpdate.setWebsite("http://www.updated.partner.com");
        partnerUpdate.setIsActive(false);
        updateEntity(PARTNERS_PATH, createdPartner1Id, partnerUpdate).then().statusCode(SC_OK);
        Response updateResponse = updateEntity(PARTNERS_PATH, createdPartner1Id, ACTIVATE_RELATION);
        updateResponse.then().statusCode(SC_OK);
        PartnerDto updateResponsePartner = updateResponse.as(PartnerDto.class);
        assertThat(getEntityAsType(PARTNERS_PATH, PartnerDto.class, createdPartner1Id)).isEqualToComparingFieldByField(updateResponsePartner);
        entityIsDeleted(PARTNERS_PATH, createdPartner1Id);
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
    @Jira("DP-2194")
    public void partnerUserShouldSeeHisRelation() throws Throwable {
        entityIsCreated(constructUserPartnerRelationshipDto(createdUserId, createdPartner1Id, true));
        assertThat(getEntitiesAsTypeByUserForApp(createdUserId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, USER_PARTNER_RELATIONSHIPS_PATH, UserPartnerRelationshipDto.class, emptyQueryParams())).hasSize(1);
    }

    @Test
    @Jira("DPIM-31")
    public void treatCommercialSubscriptionsAsRelationshipPartnerUser() throws IOException {
        // make user belong to the correct partner
        UUID userId = createdUserId;
        UserPartnerRelationshipCreateDto relation = constructUserPartnerRelationshipDto(userId, DEFAULT_SNAPSHOT_PARTNER_ID, true);
        entityIsCreated(relation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserCustomerRelationshipCreateDto userCustomerRelation = constructUserCustomerRelationshipDto(userId, DEFAULT_SNAPSHOT_CUSTOMER_ID, true, true);
        entityIsCreated(userCustomerRelation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserPropertyRelationshipCreateDto userPropertyRelation = constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        entityIsCreated(userPropertyRelation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
        entityIsUpdated(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID, INACTIVATE_RELATION);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    @Jira("DPIM-31")
    public void treatCommercialSubscriptionsAsRelationshipCustomerUser() throws IOException {
        UUID userId = entityIsCreated(testUser2);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_NOT_FOUND);
        UserPropertyRelationshipCreateDto userPropertyRelation = constructUserPropertyRelationshipDto(userId, DEFAULT_PROPERTY_ID, true);
        entityIsCreated(userPropertyRelation);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
        entityIsUpdated(COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID, INACTIVATE_RELATION);
        getEntityByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, COMMERCIAL_SUBSCRIPTIONS_PATH, DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    @Jira("DPIM-174")
    public void partnerNameIsUnique() {
        testPartner1.setName(DEFAULT_SNAPSHOT_PARTNER_NAME);
        testPartner1.setId(null);
        assertStatusCodes(createEntity(testPartner1), SC_CONFLICT, CC_CONFLICT_CODE);
    }

    @Test
    @Jira("DPIM-186")
    public void partnerEmailIsUnique() {
        testPartner1.setEmail(DEFAULT_SNAPSHOT_PARTNER_EMAIL);
        testPartner1.setId(null);
        createEntity(testPartner1)
                .then()
                .statusCode(SC_CONFLICT)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_CONFLICT_CODE));
    }
}
