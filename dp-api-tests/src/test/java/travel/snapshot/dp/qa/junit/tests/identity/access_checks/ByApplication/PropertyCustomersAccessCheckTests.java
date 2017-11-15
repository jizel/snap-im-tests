package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import com.jayway.restassured.response.Response;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.type.EntityVersion;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityEtag;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtagByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;


public class PropertyCustomersAccessCheckTests extends CommonTest {
    private UUID customerId;
    private UUID appId;
    private UUID appVersionId;
    private UUID propertyId1;
    private UUID propertyId2;
    private UUID userId;
    private UUID accessibleRelationId;
    private UUID inAccessibleRelationId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        customerId = entityIsCreated(testCustomer1);
        propertyId1 = entityIsCreated(testProperty1);
        propertyId2 = entityIsCreated(testProperty2);
        appId = entityIsCreated(testApplication1);
        testAppVersion1.setApplicationId(appId);
        appVersionId = entityIsCreated(testAppVersion1);
        dbSteps.populateApplicationPermissionsTableForApplication(appId);
        testUser1.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        userId = entityIsCreated(testUser1);
        entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId1, true));
        entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId2, true));
        commercialSubscriptionIsCreated(customerId, propertyId2, appId);
    }

    @Test
    public void propertyCustomerRelationshipCRUDByApplicationWithAndWithoutAccess() {
        // Create
        createRelation(propertyId2).then().statusCode(SC_CREATED);
        accessibleRelationId = getSessionResponse().as(CustomerPropertyRelationshipDto.class).getId();
        EntityVersion accessibleRelationEtag = getSessionResponse().as(CustomerPropertyRelationshipDto.class).getVersion();
        createRelation(propertyId1);
        responseIsReferenceDoesNotExist();
        inAccessibleRelationId = relationIsCreated(propertyId1);
        EntityVersion inAccessibleRelationEtag = getSessionResponse().as(CustomerPropertyRelationshipDto.class).getVersion();
        // Get
        getEntityByUserForApplication(userId, appVersionId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, inAccessibleRelationId);
        responseIsEntityNotFound();
        // Update
        CustomerPropertyRelationshipUpdateDto update = new CustomerPropertyRelationshipUpdateDto();
        update.setIsActive(false);
        updateRelation(inAccessibleRelationId, update, inAccessibleRelationEtag.toString()).then().statusCode(SC_NOT_FOUND);
        updateRelation(accessibleRelationId, update, accessibleRelationEtag.toString()).then().statusCode(SC_OK);
        // Delete
        deleteRelation(inAccessibleRelationId).then().statusCode(SC_NOT_FOUND);
        deleteRelation(accessibleRelationId).then().statusCode(SC_NO_CONTENT);
    }

    private Response createRelation(UUID propertyId) {
        return createEntityByUserForApplication(userId, appVersionId, constructCustomerPropertyRelationshipDto(
                customerId,
                propertyId,
                true,
                CHAIN,
                validFrom,
                validTo));
    }

    private UUID relationIsCreated(UUID propertyId) {
        return entityIsCreated(constructCustomerPropertyRelationshipDto(
                customerId,
                propertyId,
                true,
                CHAIN,
                validFrom,
                validTo));
    }

    private Response updateRelation(UUID relationId, CustomerPropertyRelationshipUpdateDto update, String etag) {
        return updateEntityWithEtagByUserForApp(userId, appVersionId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId, update, etag);
    }

    private Response deleteRelation(UUID relationId) {
        return deleteEntityByUserForApp(userId, appVersionId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
    }
}
