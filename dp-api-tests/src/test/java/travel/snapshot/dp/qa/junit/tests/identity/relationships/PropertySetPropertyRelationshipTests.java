package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.NON_EXISTENT_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityWithEtag;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationshipUpdate;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Integration tests for /identity/user_property_relationships endpoint
 */
public class PropertySetPropertyRelationshipTests extends CommonTest {
    private UUID createdPropertyId;
    private UUID createdPropertySetId;
    private PropertySetPropertyRelationshipCreateDto testPropertySetPropertyRelationship;

    @Before
    public void setUp() {
        super.setUp();
        createdPropertyId = entityIsCreated(testProperty1);
        createdPropertySetId = entityIsCreated(testPropertySet1);
        testPropertySetPropertyRelationship = constructPropertySetPropertyRelationship(
                createdPropertySetId, createdPropertyId, true);
    }

    @Test
    public void createPropertySetPropertyRelationship() {
        Response response = createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        PropertySetPropertyRelationshipCreateDto returnedRelationship = response.as(PropertySetPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdPropertyId));
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySetId));
        assertThat(returnedRelationship.getIsActive(), is(true));
        PropertySetPropertyRelationshipCreateDto requestedRelationship = getEntityAsType(
                PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, PropertySetPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createPropertySetPropertyRelationshipErrors() {
        testPropertySetPropertyRelationship = constructPropertySetPropertyRelationship(
                NON_EXISTENT_ID, createdPropertyId, true);
        createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        testPropertySetPropertyRelationship = constructPropertySetPropertyRelationship(
                createdPropertySetId, NON_EXISTENT_ID, true);
        createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updatePropertySetPropertyRelationship() throws Exception {
        PropertySetPropertyRelationshipDto createdRelationship = entityIsCreatedAs(PropertySetPropertyRelationshipDto.class, testPropertySetPropertyRelationship);
        PropertySetPropertyRelationshipUpdateDto propertySetPropertyRelationshipUpdate = constructPropertySetPropertyRelationshipUpdate(false);

        updateEntityWithEtag(
                PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), propertySetPropertyRelationshipUpdate, createdRelationship.getVersion().toString()
        ).then().statusCode(SC_OK);
        PropertySetPropertyRelationshipCreateDto returnedRelationship = getEntityAsType(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
                PropertySetPropertyRelationshipDto.class, createdRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deletePropertySetPropertyRelationship() {
        PropertySetPropertyRelationshipCreateDto createdRelationship = entityIsCreatedAs(PropertySetPropertyRelationshipDto.class, testPropertySetPropertyRelationship);
        deleteEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        getEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
