package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
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

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsTypeByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipPartialDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class PropertySetAccessCheckByUserTests extends CommonAccessCheckByUserTest {

    private UUID requestorId;
    private UUID psId1;
    private UUID psId2;
    private UUID defaultRelationId;

    @Before
    public void setUp() {
        super.setUp();
        requestorId = entityIsCreated(testUser1);
        psId1 = entityIsCreated(testPropertySet1);
        psId2 = entityIsCreated(testPropertySet2);
        defaultRelationId = entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, psId1, true));
    }

    @Test
    public void directAccessToPS() {
        getEntitySucceeds(requestorId, PROPERTY_SETS_PATH, psId1);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, psId2);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, emptyQueryParams())).hasSize(1);
        Map<String, String> params = QueryParams.builder().filter("name=='*Property Set*'").build();
        assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, params)).hasSize(1);
        entityIsCreated(constructUserPropertySetRelationshipDto(requestorId, psId2, true));
        assertThat(getEntitiesAsTypeByUserForApp(requestorId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, PROPERTY_SETS_PATH, PropertySetDto.class, params)).hasSize(2);
        deleteEntityFails(requestorId, PROPERTY_SETS_PATH, psId1, SC_CONFLICT);
        deleteEntityFails(requestorId, PROPERTY_SETS_PATH, psId2, SC_CONFLICT);
    }

    @Test
    public void propertySetPropertyAccess() {
        getEntityFails(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        // Implicit access to property through PS
        UUID relationId1 = entityIsCreated(constructPropertySetPropertyRelationship(psId1, DEFAULT_PROPERTY_ID, true));
        getEntitySucceeds(requestorId, PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        getEntitySucceeds(requestorId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId1);
    }

    @Test
    public void propertySetUserAccess() {
        UUID userId1 = entityIsCreated(testUser2);
        UUID relationId1 = entityIsCreated(constructUserPropertySetRelationshipDto(userId1, psId1, true));
        UUID customerId = entityIsCreated(testCustomer1);
        testUser3.setUserCustomerRelationship(constructUserCustomerRelationshipPartialDto(customerId, true, true));
        UUID userId2 = entityIsCreated(testUser3);
        createEntityFails(requestorId, constructUserPropertySetRelationshipDto(userId2, psId1, true));
        UUID relationId2 = entityIsCreated(constructUserPropertySetRelationshipDto(userId2, psId1, true));
        getEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
        getEntityFails(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId2);
        assertThat(getEntitiesAsTypeByUserForApp(requestorId,
                                                 DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID,
                                                 USER_PROPERTY_SET_RELATIONSHIPS_PATH,
                                                 UserPropertySetRelationshipDto.class,
                                                 emptyQueryParams())).hasSize(2);
        deleteEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId1);
        deleteEntitySucceeds(requestorId, USER_PROPERTY_SET_RELATIONSHIPS_PATH, defaultRelationId);
        deleteEntityFails(requestorId, PROPERTY_SETS_PATH, psId1, SC_NOT_FOUND);
    }

    @Test
    public void inaccessibleSecondLevelEndpoints() {
        // Setup
        String psPath = String.format("%s/%s", PROPERTY_SETS_PATH, psId2.toString());
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
    }

    @Test
    public void accessibleSecondLevelEndpoints() {
        entityIsCreated(constructPropertySetPropertyRelationship(psId1, DEFAULT_PROPERTY_ID, true));
        String psPath = String.format("%s/%s", PROPERTY_SETS_PATH, psId1.toString());
        String psUsers = String.format("%s%s", psPath, USERS_PATH);
        String psProperties = String.format("%s%s", psPath, PROPERTIES_PATH);
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

    @Jira("DPIM-238")
    @Test
    public void propertySetHierarchyAccessCheck() {
        PropertySetUpdateDto update = new PropertySetUpdateDto();

        update.setParentId(psId1);
        entityIsUpdated(PROPERTY_SETS_PATH, psId2, update);
        getEntitySucceeds(requestorId, PROPERTY_SETS_PATH, psId2);

        update.setParentId(null);
        entityIsUpdated(PROPERTY_SETS_PATH, psId2, update);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, psId2);

        update.setParentId(psId2);
        entityIsUpdated(PROPERTY_SETS_PATH, psId1, update);
        getEntityFails(requestorId, PROPERTY_SETS_PATH, psId2);

    }

}
