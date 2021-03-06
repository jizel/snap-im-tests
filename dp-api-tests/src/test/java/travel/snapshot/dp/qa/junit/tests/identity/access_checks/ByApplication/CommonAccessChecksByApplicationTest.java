package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByPatternByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityEtag;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtagByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class CommonAccessChecksByApplicationTest extends CommonTest {
    String PATH;
    UUID userId;
    UUID appVersionId;
    UUID propertyId1;
    UUID propertyId2;
    UUID accessibleEntityId;
    UUID inaccessibleEntityId;
    Map<String, Object> update;
    Optional<String> pattern;
    Optional<String> fieldName;
    String description = "description";
    Integer expectedCode;
    int returnedEntities = 1;
    UUID userPropertyRelationId1;
    UUID userPropertyRelationId2;
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
        userPropertyRelationId1 = entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId1, true));
        userPropertyRelationId2 = entityIsCreated(constructUserPropertyRelationshipDto(userId, propertyId2, true));
        expectedCode = SC_CONFLICT;
        update = new HashMap<>();
    }

    @Test
    void filteringEntitiesWithAccessChecks() {
        assertThat(getEntitiesByPatternByUserForApp(userId, appVersionId, PATH, fieldName, pattern)).hasSize(returnedEntities);
    }

    @Test
    void getEntityWithAndWithoutAccess() {
        getEntityByUserForApplication(userId, appVersionId, PATH, accessibleEntityId)
                .then()
                .statusCode(SC_OK);
        getEntityByUserForApplication(userId, appVersionId, PATH, inaccessibleEntityId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat().body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
    }

    @Test
    void applicationWithAndWithoutAccessUpdatesEntity() {
        String etag = getEntityEtag(PATH, inaccessibleEntityId);
        updateEntityWithEtagByUserForApp(userId, appVersionId, PATH, inaccessibleEntityId, update, etag)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        deleteEntityByUserForApp(userId, appVersionId, PATH, inaccessibleEntityId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .assertThat()
                .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        updateEntityByUserForApp(userId, appVersionId, PATH, accessibleEntityId, update)
                .then()
                .statusCode(SC_OK);
        deleteEntityByUserForApp(userId, appVersionId, PATH, accessibleEntityId)
                .then()
                .statusCode(expectedCode);
    }
}