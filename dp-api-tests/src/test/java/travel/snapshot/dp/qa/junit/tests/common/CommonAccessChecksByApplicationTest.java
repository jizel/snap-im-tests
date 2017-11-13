package travel.snapshot.dp.qa.junit.tests.common;

import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;

import java.util.Map;
import java.util.UUID;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

public class CommonAccessChecksByApplicationTest extends CommonTest {
    public static UUID userId = null;
    public static UUID appVersionId = null;
    public static UUID propertyId1 = null;
    public static UUID propertyId2 = null;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // Create 2 properties and an application that will have commercial subscription with only 1 property and a default customer
        // Also create a user, connected to both properties
        propertyId1 = entityIsCreated(testProperty1);
        propertyId2 = entityIsCreated(testProperty2);
        UUID appId = entityIsCreated(testApplication1);
        dbSteps.populateApplicationPermissionsTableForApplication(appId);
        testAppVersion1.setApplicationId(appId);
        appVersionId = entityIsCreated(testAppVersion1);
        commercialSubscriptionIsCreated(DEFAULT_SNAPSHOT_CUSTOMER_ID, propertyId1, appId);
        userId = entityIsCreated(testUser1);
        entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId1, true));
        entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId2, true));
    }

    public static void activateUserCustomerRelation(UUID userId, UUID customerId) {
        UserCustomerRelationshipUpdateDto update = new UserCustomerRelationshipUpdateDto();
        update.setIsActive(true);
        Map<String, String> params = buildQueryParamMapForPaging(null, null, String.format("user_id==%s and customer_id==%s", userId, customerId), null, null, null);
        UUID relationId = getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, params).get(0).getId();
        entityIsUpdated(USER_CUSTOMER_RELATIONSHIPS_PATH, relationId, update);
    }

    public static void activateUserPropertyRelation(UUID userId, UUID propertyId) {
        UserPropertyRelationshipUpdateDto update = new UserPropertyRelationshipUpdateDto();
        update.setIsActive(true);
        Map<String, String> params = buildQueryParamMapForPaging(null, null, String.format("user_id==%s and property_id==%s", userId, propertyId), null, null, null);
        UUID relationId = getEntitiesAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, params).get(0).getId();
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, relationId, update);
    }
}
