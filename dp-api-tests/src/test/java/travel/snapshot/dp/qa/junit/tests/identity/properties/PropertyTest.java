package travel.snapshot.dp.qa.junit.tests.identity.properties;

import static java.util.Collections.singletonMap;
import static java.util.UUID.randomUUID;
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

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
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

public class PropertyTest extends CommonTest {

    private UUID createdPropertyId;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void propertyCRUD() throws Exception {
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        bodyContainsEntityWith(PROPERTY_CODE, "property1code");
        bodyContainsEntityWith("name", "Property 1");
        bodyContainsEntityWith("website", "http://www.snapshot.travel");
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("UpdatedName");
        propertyUpdate.setEmail("updated@snapshot.travel");
        propertyUpdate.setIsDemo(false);
        propertyUpdate.setTtiId(123456);
        propertyUpdate.setTimezone("Europe/London");
        Response updateResponse = commonHelpers.updateEntity(PROPERTIES_PATH, createdPropertyId, propertyUpdate);
        responseCodeIs(SC_OK);
        PropertyDto updateResponseProperty = updateResponse.as(PropertyDto.class);
        PropertyDto requestedProperty = commonHelpers.getEntityAsType(PROPERTIES_PATH, PropertyDto.class, createdPropertyId);
        assertThat("Update response body differs from the same user requested by GET ", updateResponseProperty, is(requestedProperty));
        commonHelpers.deleteEntity(PROPERTIES_PATH, createdPropertyId);
        responseCodeIs(SC_NO_CONTENT);
        entityDoesNotExist(PROPERTIES_PATH, createdPropertyId);
    }

    @Test
    public void invalidUpdateProperty() throws Exception {
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        Map<String, String> invalidUpdate = singletonMap("invalid_key", "whatever");
        commonHelpers.updateEntity(PROPERTIES_PATH, createdPropertyId, invalidUpdate);
        responseIsUnprocessableEntity();

        invalidUpdate = singletonMap("email", "invalid_value");
        commonHelpers.updateEntity(PROPERTIES_PATH, createdPropertyId, invalidUpdate);
        responseIsUnprocessableEntity();

        commonHelpers.updateEntity(PROPERTIES_PATH, randomUUID(), invalidUpdate);
        responseIsEntityNotFound();
    }

    @Test
    public void propertyActivateDeactivate() {
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        PropertyUpdateDto update = new PropertyUpdateDto();
        update.setIsActive(false);
        commonHelpers.entityIsUpdated(PROPERTIES_PATH, createdPropertyId, update);
        bodyContainsEntityWith(IS_ACTIVE, "false");
        update.setIsActive(true);
        commonHelpers.entityIsUpdated(PROPERTIES_PATH, createdPropertyId, update);
        bodyContainsEntityWith(IS_ACTIVE, "true");
    }


    @Test
    public void createPropertyWithCapitalIdLetters() throws Exception {
        String id = "000e833e-50b8-4854-a233-289f00bc4a09";
        commonHelpers.useFileForSendDataTo("/messages/identity/properties/property_capital_letters.json", "POST", PROPERTIES_PATH, "identity");
        bodyContainsEntityWith(PROPERTY_ID, id);
    }

    @Test
    public void timezoneParameterIsMandatory() {
        testProperty1.setTimezone(null);
        commonHelpers.createEntity(PROPERTIES_PATH,testProperty1);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void sendPostRequestWithEmptyBodyToAllPropertyUrls() {
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        sendBlankPost(String.format("%s/%s", PROPERTIES_PATH, createdPropertyId), "identity");
        responseIsUnprocessableEntity();

        sendBlankPost(String.format("%s/%s/users", PROPERTIES_PATH, createdPropertyId), "identity");
        responseIsUnprocessableEntity();

        UUID userId = commonHelpers.entityIsCreated(testUser1);
        UserPropertyRelationshipCreateDto relation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, createdPropertyId, true);
        commonHelpers.entityIsCreated(relation);

        sendBlankPost(String.format("%s/%s/users/%s", PROPERTIES_PATH, createdPropertyId, userId), "identity");
        responseIsUnprocessableEntity();
    }

    @Test
    public void creatingDuplicatePropertyReturnsCorrectError() {
        commonHelpers.entityIsCreated(testProperty1);
        commonHelpers.createEntity(PROPERTIES_PATH, testProperty1);
        responseIsConflictId();

        testProperty1.setId(null);
        commonHelpers.createEntity(PROPERTIES_PATH, testProperty1);
        responseIsConflictField();
    }

    @Test
    public void propertyCannotBeDeletedIfItHasRelationshipWithExistingCustomer() {
        for (CustomerPropertyRelationshipType type : CustomerPropertyRelationshipType.values()) {
            createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
            CustomerPropertyRelationshipCreateDto relation = relationshipsHelpers.constructCustomerPropertyRelationshipDto(
                    DEFAULT_SNAPSHOT_CUSTOMER_ID,
                    createdPropertyId,
             true,
                    type,
                    LocalDate.parse(VALID_FROM_VALUE),
                    LocalDate.parse(VALID_TO_VALUE)
            );
            UUID relationId = commonHelpers.entityIsCreated(relation);
            commonHelpers.deleteEntity(PROPERTIES_PATH, createdPropertyId);
            responseIsEntityReferenced();
            commonHelpers.entityIsDeleted(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationId);
            commonHelpers.entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
        }
    }

    @Test
    public void propertyCannotBeDeletedWhenItBelongsToAnyPropertySet() {
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        UUID psId = commonHelpers.entityIsCreated(testPropertySet1);
        PropertySetPropertyRelationshipCreateDto relation = relationshipsHelpers.constructPropertySetPropertyRelationship(psId, createdPropertyId, true);
        UUID relationId = commonHelpers.entityIsCreated(relation);
        commonHelpers.deleteEntity(PROPERTIES_PATH, createdPropertyId);
        responseIsEntityReferenced();
        commonHelpers.deleteEntity(PROPERTY_SETS_PATH, psId);
        responseIsEntityReferenced();
        commonHelpers.entityIsDeleted(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relationId);
        commonHelpers.entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
        commonHelpers.entityIsDeleted(PROPERTY_SETS_PATH, psId);
    }

    @Test
    public void propertyCannotBeDeletedWhenItHasARelationshipWithSomeUser() {
        createdPropertyId = commonHelpers.entityIsCreated(testProperty1);
        UUID userId = commonHelpers.entityIsCreated(testUser1);
        UserPropertyRelationshipCreateDto relation = relationshipsHelpers.constructUserPropertyRelationshipDto(userId, createdPropertyId, true);
        UUID relationId = commonHelpers.entityIsCreated(relation);
        commonHelpers.deleteEntity(PROPERTIES_PATH, createdPropertyId);
        responseIsEntityReferenced();
        commonHelpers.deleteEntity(USERS_PATH, userId);
        responseIsEntityReferenced();
        commonHelpers.entityIsDeleted(USER_PROPERTY_RELATIONSHIPS_PATH, relationId);
        commonHelpers.entityIsDeleted(PROPERTIES_PATH, createdPropertyId);
    }
}
