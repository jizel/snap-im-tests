package travel.snapshot.dp.qa.junit.tests.identity.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_property_relationships endpoint
 */
public class PropertySetPropertyRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private PropertySetDto createdPropertySet1;
    private PropertySetPropertyRelationshipDto testPropertySetPropertyRelationship;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdPropertySet1 = propertySetHelpers.propertySetIsCreated(testPropertySet1);
        testPropertySetPropertyRelationship = relationshipsHelpers.constructPropertySetPropertyRelationship(
                createdPropertySet1.getId(), createdProperty1.getId(), true);
    }

    @Test
    public void createPropertySetPropertyRelationship() {
        Response response = commonHelpers.createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        PropertySetPropertyRelationshipDto returnedRelationship = response.as(PropertySetPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySet1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        PropertySetPropertyRelationshipDto requestedRelationship = commonHelpers.getEntityAsType(
                PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, PropertySetPropertyRelationshipDto.class, returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createPropertySetPropertyRelationshipErrors() {
        testPropertySetPropertyRelationship = relationshipsHelpers.constructPropertySetPropertyRelationship(
                NON_EXISTENT_ID, createdProperty1.getId(), true);
        commonHelpers.createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);

        testPropertySetPropertyRelationship = relationshipsHelpers.constructPropertySetPropertyRelationship(
                createdPropertySet1.getId(), NON_EXISTENT_ID, true);
        commonHelpers.createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, testPropertySetPropertyRelationship);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    @Test
    public void updatePropertySetPropertyRelationship() throws Exception {
        PropertySetPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
                PropertySetPropertyRelationshipDto.class, testPropertySetPropertyRelationship);
        PropertySetPropertyRelationshipUpdateDto propertySetPropertyRelationshipUpdate = relationshipsHelpers
                .constructPropertySetPropertyRelationshipUpdate(false);

        commonHelpers.updateEntityWithEtag(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), propertySetPropertyRelationshipUpdate, createdRelationship.getVersion().toString());
        responseCodeIs(SC_NO_CONTENT);
        PropertySetPropertyRelationshipDto returnedRelationship = commonHelpers.getEntityAsType(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
                PropertySetPropertyRelationshipDto.class, createdRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deletePropertySetPropertyRelationship() {
        PropertySetPropertyRelationshipDto createdRelationship = commonHelpers.entityWithTypeIsCreated(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH,
                PropertySetPropertyRelationshipDto.class, testPropertySetPropertyRelationship);
        commonHelpers.deleteEntityWithEtag(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId(), createdRelationship.getVersion().toString());
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, createdRelationship.getId());
        responseCodeIs(SC_NOT_FOUND);
    }
}
