package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType.CHAIN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class PropertiesAccessChecksByUserTests extends CommonAccessCheckByUserTest {
    UUID userId1;
    UUID userId2;
    UUID propertyId;
    UUID propertySetId;
    UUID propsetPropertyRelationId;
    UUID propertyUserRelationId;
    UUID customerPropertyRelationId;
    Map<String, UUID> inaccessibles = new HashMap<>();

    @BeforeEach
    public void setUp() {
        super.setUp();
        requestorId = entityIsCreated(testUser1);

    }

    @Test
    void propertyCRUD() {
        createEntityByUserForApplication(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, testProperty1)
                .then().statusCode(SC_CREATED);
        propertyId = getSessionResponse().as(PropertyDto.class).getId();
        getEntitySucceeds(requestorId, PROPERTIES_PATH, propertyId);
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", requestorId)).build();
        UUID relationId = getEntitiesAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, params).get(0).getId();
        updateEntity(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTIES_PATH, propertyId);
    }

    @Test
    void accessToPropertyRelations() {
        userId2 = entityIsCreated(testUser2);
        propertyId = entityIsCreated(testProperty1);
        UUID relationId = entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, false));
        propertySetId = entityIsCreated(testPropertySet1);
        propsetPropertyRelationId = entityIsCreated(constructPropertySetPropertyRelationship(propertySetId, propertyId, true));
        propertyUserRelationId = entityIsCreated(constructUserPropertyRelationshipDto(userId2, propertyId, true));
        customerPropertyRelationId = entityIsCreated(constructCustomerPropertyRelationshipDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, propertyId, true, CHAIN, validFrom, validTo));
        /*
        We end up with the following topology
        P1--PS1
        | \
        |  \
        U1  U2
         */
        inaccessibles.put(PROPERTIES_PATH, propertyId);
        inaccessibles.put(PROPERTY_SETS_PATH, propertySetId);
        inaccessibles.put(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, propsetPropertyRelationId);
        inaccessibles.put(USER_PROPERTY_RELATIONSHIPS_PATH, propertyUserRelationId);
        inaccessibles.put(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, customerPropertyRelationId);
        inaccessibles.forEach((endpoint, id) -> {
            getEntityFails(requestorId, endpoint, id);
        });
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        inaccessibles.forEach((endpoint, id) -> {
            getEntitySucceeds(requestorId, endpoint, id);
        });
        entityIsUpdated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, propsetPropertyRelationId, INACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, propsetPropertyRelationId);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, propertyUserRelationId, INACTIVATE_RELATION);
        getEntitySucceeds(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, propertyUserRelationId);
    }

    @Test
    void accessThroughUserGroup() {
        UUID groupId = entityIsCreated(testUserGroup1);
        UUID userGroupRelationId = entityIsCreated(constructUserGroupUserRelationship(groupId, requestorId, false));
        UUID groupPropertyRelationId = entityIsCreated(constructUserGroupPropertyRelationship(groupId, DEFAULT_PROPERTY_ID, false));
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupRelationId, INACTIVATE_RELATION);
        entityIsUpdated(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, groupPropertyRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupRelationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
    }

    @Test
    void accessThroughPropertySet() {
        UUID psId = entityIsCreated(testPropertySet1);
        UUID userPsRelationId = entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, psId, false));
        UUID psPropertyRelationId = entityIsCreated(constructPropertySetPropertyRelationship(psId, DEFAULT_PROPERTY_ID, false));
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, userPsRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, userPsRelationId, INACTIVATE_RELATION);
        entityIsUpdated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, psPropertyRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, userPsRelationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
    }

    @Test
    void accessThroughGroupAndPropertySet() {
        UUID groupId = entityIsCreated(testUserGroup1);
        UUID psId = entityIsCreated(testPropertySet1);
        entityIsCreated(constructUserGroupUserRelationship(groupId, requestorId, true));
        UUID groupPropertySetRelationId = entityIsCreated(constructUserGroupPropertySetRelationship(groupId, psId, false));
        entityIsCreated(constructPropertySetPropertyRelationship(psId, DEFAULT_PROPERTY_ID, true));
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        entityIsUpdated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, groupPropertySetRelationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
    }

    @Test
    void propertyFilteringWithAccessChecks() {
        testProperty1.setName("Default Property Nothing");
        createEntityByUserForApplication(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, testProperty1);
        Map<String, String> params = QueryParams.builder().filter("name=='Default Property*'").build();
        getEntitiesByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTIES_PATH, params);
        assertThat(parseResponseAsListOfObjects(PropertyDto.class)).hasSize(1);
    }

    @Test
    void propertyCustomerAccessCheckByUser() {
        /*
         C1--P1  P2----C2
          \  |  /
           \ | /
            \|/
             R
         Where:
         R - requestor
         P2-C2 - target relation
         */
        // Setup
        UUID relationId1 = entityIsCreated(constructCustomerPropertyRelationshipDto(DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID, true, CHAIN, validFrom, validTo));
        UUID customerId = entityIsCreated(testCustomer1);
        propertyId = entityIsCreated(testProperty1);
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, DEFAULT_PROPERTY_ID, true));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, true));

        // Test
        createEntityFails(requestorId, constructCustomerPropertyRelationshipDto(customerId, propertyId, true, CHAIN, validFrom, validTo));
        UUID relationId2 = entityIsCreated(constructCustomerPropertyRelationshipDto(customerId, propertyId, true, CHAIN, validFrom, validTo));
        updateEntityFails(requestorId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId2, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId2, SC_NOT_FOUND);
        updateEntitySucceeds(requestorId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId1);
    }

    @Test
    void propertyPropertySetAccessCheckByUser() {
        /*
        PS1--P1  P2----PS2
          \  |  /
           \ | /
            \|/
             R
         Where:
         R - requestor
         P2-PS2 - target relation
         */
        // Setup
        propertyId = entityIsCreated(testProperty1);
        UUID psId1 = entityIsCreated(testPropertySet1);
        UUID psId2 = entityIsCreated(testPropertySet2);
        UUID relationId1 = entityIsCreated(constructPropertySetPropertyRelationship(psId1, DEFAULT_PROPERTY_ID, true));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, DEFAULT_PROPERTY_ID, true));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, psId1, true));

        // Test
        createEntityFails(requestorId, constructPropertySetPropertyRelationship(psId2, propertyId, true));
        UUID relationId2 = entityIsCreated(constructPropertySetPropertyRelationship(psId2, propertyId, true));
        // Implicit access  to PS through the single property
        getEntitySucceeds(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId2);
        // Now destroy implicit access by adding extra property to ps
        UUID newPropertyId = entityIsCreated(testProperty2);
        entityIsCreated(constructPropertySetPropertyRelationship(psId2, newPropertyId, true));
        getEntityFails(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId2);

        updateEntityFails(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId2, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId2, SC_NOT_FOUND);
        updateEntitySucceeds(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId1);
    }

    @Test
    void propertyUserAccessCheckByUser() {
        /*
           C1     C2
           /\    / \
         U1-P1  P2-U2
          \ |  /
           \| /
            R
         Where:
         R - requestor
         P2-U3 - target relation
         */
        // Setup

        UUID customerId = entityIsCreated(testCustomer1);
        propertyId = entityIsCreated(testProperty1);
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, DEFAULT_PROPERTY_ID, true));
        entityIsCreated(constructUserPropertyRelationshipDto(requestorId, propertyId, true));
        userId1 = entityIsCreated(testUser2);
        testUser3.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        userId2 = entityIsCreated(testUser3);

        // Test
        createEntityFails(requestorId, constructUserPropertyRelationshipDto(userId2, propertyId, true));
        createEntitySucceeds(requestorId, constructUserPropertyRelationshipDto(userId1, DEFAULT_PROPERTY_ID, true));
        UUID relationId1 = getSessionResponse().as(UserPropertyRelationshipDto.class).getId();
        UUID relationId2 = entityIsCreated(constructUserPropertyRelationshipDto(userId2, propertyId, true));
        updateEntityFails(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId2, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId2, SC_NOT_FOUND);
        updateEntitySucceeds(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, USER_PROPERTY_RELATIONSHIPS_PATH, relationId1);
    }
}
