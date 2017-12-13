package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByUser;

import com.jayway.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.getSessionResponse;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

public class PropertiesAccessChecksByUserTests extends CommonTest {
    UUID userId1;
    UUID userId2;
    UUID propertyId;
    UUID propertySetId;
    UUID propsetPropertyRelationId;
    UUID propertyUserRelationId;
    Map<String, UUID> inaccessibles = new HashMap<>();

    @BeforeEach
    public void setUp() {
        super.setUp();
        userId1 = entityIsCreated(testUser1);

    }

    @Test
    void propertyCRUD() {
        createEntityByUserForApplication(userId1, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, testProperty1)
                .then().statusCode(SC_CREATED);
        propertyId = getSessionResponse().as(PropertyDto.class).getId();
        getEntity(PROPERTIES_PATH, propertyId).then().statusCode(SC_OK);
        getEntity(PROPERTIES_PATH, DEFAULT_PROPERTY_ID).then().statusCode(SC_NOT_FOUND);
        Map<String, String> params = QueryParams.builder().filter(String.format("user_id==%s", userId1)).build();
        UUID relationId = getEntitiesAsType(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class, params).get(0).getId();
        updateEntity(USER_PROPERTY_RELATIONSHIPS_PATH, relationId, INACTIVATE_RELATION);
        getEntity(PROPERTIES_PATH, propertyId).then().statusCode(SC_NOT_FOUND);
    }

    @Test
    void accessToPropertyRelations() {
        userId2 = entityIsCreated(testUser2);
        propertyId = entityIsCreated(testProperty1);
        UUID relationId = entityIsCreated(constructUserPropertyRelationshipDto(userId1, propertyId, false));
        propertySetId = entityIsCreated(testPropertySet1);
        propsetPropertyRelationId = entityIsCreated(constructPropertySetPropertyRelationship(propertySetId, propertyId, true));
        propertyUserRelationId = entityIsCreated(constructUserPropertyRelationshipDto(userId2, propertyId, true));

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
        inaccessibles.forEach((endpoint, id) -> {
            getEntity(endpoint, id)
                    .then()
                    .statusCode(SC_NOT_FOUND)
                    .assertThat()
                    .body(RESPONSE_CODE, is(CC_ENTITY_NOT_FOUND));
        });
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, relationId, ACTIVATE_RELATION);
        inaccessibles.forEach((endpoint, id) -> {
            getEntity(endpoint, id).then().statusCode(SC_OK);
        });
        entityIsUpdated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, propsetPropertyRelationId, INACTIVATE_RELATION);
        getEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, propsetPropertyRelationId).then().statusCode(SC_NOT_FOUND);
        entityIsUpdated(USER_PROPERTY_RELATIONSHIPS_PATH, propertyUserRelationId, INACTIVATE_RELATION);
        getEntity(USER_PROPERTY_RELATIONSHIPS_PATH, propertyUserRelationId).then().statusCode(SC_OK);
    }

    private Response getEntity(String endpoint, UUID id) {
        return getEntityByUserForApplication(userId1, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, endpoint, id);
    }
}
