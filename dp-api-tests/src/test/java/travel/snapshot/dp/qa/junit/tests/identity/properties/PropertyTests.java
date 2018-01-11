package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.IS_ACTIVE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_CODE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_FROM_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.VALID_TO_VALUE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.sendBlankPost;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.ACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructCustomerPropertyRelationshipDto;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

import com.jayway.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Basic tests for IM Property entity
 */

class PropertyTests extends CommonTest {

    private UUID createdPropertyId;

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    void propertyCRUD() throws Exception {
        createdPropertyId = entityIsCreated(testProperty1);
        bodyContainsEntityWith(PROPERTY_CODE, "property1code");
        bodyContainsEntityWith("name", "Property 1");
        bodyContainsEntityWith("website", "http://www.snapshot.travel");
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("UpdatedName");
        propertyUpdate.setEmail("updated@snapshot.travel");
        propertyUpdate.setIsDemo(false);
        propertyUpdate.setTtiId(123456);
        propertyUpdate.setTimezone("Europe/London");
        Response updateResponse = updateEntity(PROPERTIES_PATH, createdPropertyId, propertyUpdate);
        responseCodeIs(SC_OK);
        PropertyDto updateResponseProperty = updateResponse.as(PropertyDto.class);
        PropertyDto requestedProperty = getEntityAsType(PROPERTIES_PATH, PropertyDto.class, createdPropertyId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseProperty, is(requestedProperty));
        deleteEntity(PROPERTIES_PATH, createdPropertyId);
        responseCodeIs(SC_NO_CONTENT);
        getEntity(PROPERTIES_PATH, createdPropertyId).then().statusCode(SC_NOT_FOUND);
    }

    @Test
    void invalidUpdateProperty() throws Exception {
        createdPropertyId = entityIsCreated(testProperty1);
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        updateEntity(PROPERTIES_PATH, createdPropertyId, invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        updateEntity(PROPERTIES_PATH, createdPropertyId, invalidUpdate);
        responseIsUnprocessableEntity();

        updateEntity(PROPERTIES_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
    }

    @Test
    void propertyActivateDeactivate() {
        createdPropertyId = entityIsCreated(testProperty1);
        entityIsUpdated(PROPERTIES_PATH, createdPropertyId, INACTIVATE_RELATION);
        bodyContainsEntityWith(IS_ACTIVE, "false");
        entityIsUpdated(PROPERTIES_PATH, createdPropertyId, ACTIVATE_RELATION);
        bodyContainsEntityWith(IS_ACTIVE, "true");
    }


    @Test
    void createPropertyWithCapitalIdLetters() throws Exception {
        String id = "000e833e-50b8-4854-a233-289f00bc4a09";
        commonHelpers.useFileForSendDataTo("/messages/identity/properties/property_capital_letters.json", "POST", PROPERTIES_PATH, "identity");
        bodyContainsEntityWith(PROPERTY_ID, id);
    }

    @Test
    void timezoneParameterIsMandatory() {
        testProperty1.setTimezone(null);
        createEntity(PROPERTIES_PATH,testProperty1);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    void sendPostRequestWithEmptyBodyToAllPropertyUrls() {
        createdPropertyId = entityIsCreated(testProperty1);
        sendBlankPost(String.format("%s/%s", PROPERTIES_PATH, createdPropertyId), "identity");
        responseIsUnprocessableEntity();

        sendBlankPost(String.format("%s/%s/users", PROPERTIES_PATH, createdPropertyId), "identity");
        responseIsUnprocessableEntity();

        UUID userId = entityIsCreated(testUser1);
        UserPropertyRelationshipCreateDto relation = constructUserPropertyRelationshipDto(userId, createdPropertyId, true);
        entityIsCreated(relation);

        sendBlankPost(String.format("%s/%s/users/%s", PROPERTIES_PATH, createdPropertyId, userId), "identity");
        responseIsUnprocessableEntity();
    }

    @Test
    void creatingDuplicatePropertyReturnsCorrectError() {
        entityIsCreated(testProperty1);
        createEntity(PROPERTIES_PATH, testProperty1);
        responseIsConflictId();

        testProperty1.setId(null);
        createEntity(PROPERTIES_PATH, testProperty1);
        responseIsConflictField();
    }

    @Test
    void propertyCannotBeDeletedIfItHasRelationshipWithExistingCustomer() {
        for (CustomerPropertyRelationshipType type : CustomerPropertyRelationshipType.values()) {
            createdPropertyId = entityIsCreated(testProperty1);
            CustomerPropertyRelationshipCreateDto relation = constructCustomerPropertyRelationshipDto(
                    DEFAULT_SNAPSHOT_CUSTOMER_ID,
                    createdPropertyId,
             true,
                    type,
                    LocalDate.parse(VALID_FROM_VALUE),
                    LocalDate.parse(VALID_TO_VALUE)
            );
            UUID relationId = entityIsCreated(relation);
            deleteEntity(PROPERTIES_PATH, createdPropertyId);
            responseIsEntityReferenced();
            entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
            entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
        }
    }

    @Test
    void propertyCannotBeDeletedWhenItBelongsToAnyPropertySet() {
        createdPropertyId = entityIsCreated(testProperty1);
        UUID psId = entityIsCreated(testPropertySet1);
        PropertySetPropertyRelationshipCreateDto relation = constructPropertySetPropertyRelationship(psId, createdPropertyId, true);
        UUID relationId = entityIsCreated(relation);
        deleteEntity(PROPERTIES_PATH, createdPropertyId);
        responseIsEntityReferenced();
        deleteEntity(PROPERTY_SETS_PATH, psId);
        responseIsEntityReferenced();
        entityIsDeleted(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
        entityIsDeleted(PROPERTY_SETS_PATH, psId);
    }

    @Test
    void propertyCannotBeDeletedWhenItHasARelationshipWithSomeUser() {
        createdPropertyId = entityIsCreated(testProperty1);
        UUID userId = entityIsCreated(testUser1);
        UserPropertyRelationshipCreateDto relation = constructUserPropertyRelationshipDto(userId, createdPropertyId, true);
        UUID relationId = entityIsCreated(relation);
        deleteEntity(PROPERTIES_PATH, createdPropertyId);
        responseIsEntityReferenced();
        deleteEntity(USERS_PATH, userId);
        responseIsEntityReferenced();
        entityIsDeleted(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
    }
}
