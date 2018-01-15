package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.parseResponseAsListOfObjects;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class PropertySetsAccessChecksByUserTests extends CommonAccessCheckByUserTest {
    UUID userId1;
    UUID userId2;
    UUID propertySetId;
    UUID targetPropertySetId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        requestorId = entityIsCreated(testUser1);
        targetPropertySetId = entityIsCreated(testPropertySet1);
    }

    @Test
    void propertySetCRUD() {
        propertySetId = createEntitySucceeds(requestorId, testPropertySet2)
                .then().extract().response()
                .as(PropertySetDto.class).getId();
        getEntitySucceeds(requestorId, PROPERTY_SETS_PATH, propertySetId);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", requestorId)).build();
        UUID relationId = getEntitiesAsType(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class, params).get(0).getId();
        updateEntity(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, propertySetId);
    }

    @Test
    void accessThroughUserGroup() {
        UUID groupId = entityIsCreated(testUserGroup1);
        UUID userGroupRelationId = entityIsCreated(constructUserGroupUserRelationship(groupId, requestorId, false));
        UUID groupPropertySetRelationId = entityIsCreated(constructUserGroupPropertySetRelationship(groupId, targetPropertySetId, false));
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupRelationId, INACTIVATE_RELATION);
        entityIsUpdated(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, groupPropertySetRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        entityIsUpdated(USER_GROUP_USER_RELATIONSHIPS_PATH, userGroupRelationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
    }

    @Test
    void accessThroughProperty() {
        UUID userPropertyRelationId = entityIsCreated(constructUserPropertyRelationshipDto(requestorId, DEFAULT_PROPERTY_ID, false));
        UUID psPropertyRelationId = entityIsCreated(constructPropertySetPropertyRelationship(targetPropertySetId, DEFAULT_PROPERTY_ID, false));
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, userPropertyRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, userPropertyRelationId, INACTIVATE_RELATION);
        entityIsUpdated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, psPropertyRelationId, ACTIVATE_RELATION);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, userPropertyRelationId, ACTIVATE_RELATION);
        getEntitySucceeds(requestorId, PROPERTY_SETS_PATH, targetPropertySetId);
    }


    @Test
    void propertySetFilteringWithAccessChecks() {
        testPropertySet2.setName("Test Property Set Nothing");
        propertySetId = entityIsCreated(testPropertySet2);
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, targetPropertySetId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, propertySetId, false));
        Map<String, String> params = QueryParams.builder().filter("name=='Test Property Set*'").build();
        getEntitiesByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, params);
        assertThat(parseResponseAsListOfObjects(PropertySetDto.class)).hasSize(1);
    }

    @Test
    void propertySetUserAccessCheckByUser() {
        /*
           C1     C2
           /\    / \
          /  \
         U1-PS1 PS2-U2
          \ |  /
           \| /
            R
         Where:
         R - requestor
         PS2-U2 - target relation
         */
        // Setup

        // default ps to which requestor has access is called targetPropertySetId.
        // Do not let yourself be confused, real target property set in this test is called propertySetId
        UUID customerId = entityIsCreated(testCustomer1);
        testPropertySet2.setCustomerId(customerId);
        propertySetId = entityIsCreated(testPropertySet2);
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, targetPropertySetId, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, propertySetId, true));
        userId1 = entityIsCreated(testUser2);
        testUser3.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        userId2 = entityIsCreated(testUser3);

        // Test
        createEntityFails(requestorId, constructUserPropertySetRelationshipDto(userId2, propertySetId, true));
        UUID relationId1 = createEntitySucceeds(requestorId, constructUserPropertySetRelationshipDto(userId1, targetPropertySetId, true))
                .then().extract().response()
                .as(UserPropertySetRelationshipDto.class).getId();
        UUID relationId2 = entityIsCreated(constructUserPropertySetRelationshipDto(userId2, propertySetId, true));
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                USER_PROPERTY_SET_RELATIONSHIPS_PATH,
                UserPropertySetRelationshipDto.class,
                emptyQueryParams())).hasSize(3);
        updateEntityFails(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId2, INACTIVATE_RELATION);
        deleteEntityFails(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId2, SC_NOT_FOUND);
        updateEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1, INACTIVATE_RELATION);
        deleteEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
    }

    @Test
    public void accessToSecondLevelEndpoints() {
        // Setup
        entityIsCreated(constructPropertySetPropertyRelationship(targetPropertySetId, DEFAULT_PROPERTY_ID, true));
        String psPath = String.format("%s/%s", PROPERTY_SETS_PATH, targetPropertySetId.toString());
        String psUsers = String.format("%s%s", psPath, USERS_PATH);
        String psProperties = String.format("%s%s", psPath, PROPERTIES_PATH);
        List<String> endpoints = Arrays.asList(psUsers, psProperties);
        endpoints.forEach(endpoint-> {
            getEntitiesByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, emptyQueryParams())
                    .then()
                    .statusCode(SC_NOT_FOUND)
                    .assertThat()
                    .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        });
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, targetPropertySetId, true));
        Map<String, Class> endpointEntityMap = new HashMap<>();
        endpointEntityMap.put(psUsers, PropertySetUserRelationshipPartialDto.class);
        endpointEntityMap.put(psProperties, PropertySetPropertyRelationshipPartialDto.class);
        endpointEntityMap.forEach((path, clazz) -> {
            assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                    DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                    path,
                    clazz,
                    emptyQueryParams())).hasSize(1);
        });
    }

    @Test
    public void propertySetAccessThroughParent() {

        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, targetPropertySetId, true));
        propertySetId = entityIsCreated(testPropertySet2);
        PropertySetUpdateDto update = new PropertySetUpdateDto();

        update.setParentId(targetPropertySetId);
        entityIsUpdated(PROPERTY_SETS_PATH, propertySetId, update);
        getEntitySucceeds(requestorId, PROPERTY_SETS_PATH, propertySetId);
    }
}
