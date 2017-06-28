package travel.snapshot.dp.qa.junit.tests.relationships;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Integration tests for /identity/user_property_relationships endpoint
 */
@RunWith(SerenityRunner.class)
public class PropertySetPropertRelationshipTests extends CommonTest {
    private PropertyDto createdProperty1;
    private PropertySetDto createdPropertySet1;
    private Response response;

    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        createdProperty1 = propertyHelpers.propertyIsCreated(testProperty1);
        createdPropertySet1 = propertySetHelpers.propertySetIsCreated(testPropertySet1);
    }

    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void createPropertySetPropertyRelationship() {
        response = relationshipsHelpers.createPropertySetPropertyRelationship(createdPropertySet1.getId(), createdProperty1.getId(), true);
        responseCodeIs(SC_CREATED);
        bodyContainsEntityWith("id");
        PropertySetPropertyRelationshipDto returnedRelationship = response.as(PropertySetPropertyRelationshipDto.class);
        assertThat(returnedRelationship.getPropertyId(), is(createdProperty1.getId()));
        assertThat(returnedRelationship.getPropertySetId(), is(createdPropertySet1.getId()));
        assertThat(returnedRelationship.getIsActive(), is(true));
        PropertySetPropertyRelationshipDto requestedRelationship = relationshipsHelpers.getPropertySetPropertyRelationship(returnedRelationship.getId());
        assertThat("Returned relationship is different from sent ", requestedRelationship, is(returnedRelationship));
    }

    @Test
    public void createPropertySetPropertyRelationshipErrors() {
        relationshipsHelpers.createPropertySetPropertyRelationship(NON_EXISTENT_ID, createdProperty1.getId(), true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
        relationshipsHelpers.createPropertySetPropertyRelationship(createdPropertySet1.getId(), NON_EXISTENT_ID, true);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(NON_EXISTING_REFERENCE_CUSTOM_CODE);
    }

    @Test
    public void updatePropertySetPropertyRelationship() throws Exception {
        PropertySetPropertyRelationshipDto PropertySetPropertyRelationship = relationshipsHelpers.propertySetPropertyRelationshipIsCreated(createdPropertySet1.getId(), createdProperty1.getId(), true);
        relationshipsHelpers.updatePropertySetPropertyRelationship(PropertySetPropertyRelationship.getId(), false);
        responseCodeIs(SC_NO_CONTENT);
        PropertySetPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getPropertySetPropertyRelationship(PropertySetPropertyRelationship.getId());
        assertThat(returnedRelationship.getIsActive(), is(false));
    }

    @Test
    public void deletePropertySetPropertyRelationship(){
        PropertySetPropertyRelationshipDto PropertySetPropertyRelationship = relationshipsHelpers.propertySetPropertyRelationshipIsCreated(createdPropertySet1.getId(), createdProperty1.getId(), true);
        relationshipsHelpers.deletePropertySetPropertyRelationship(PropertySetPropertyRelationship.getId());
        responseCodeIs(SC_NO_CONTENT);
        PropertySetPropertyRelationshipDto returnedRelationship = relationshipsHelpers.getPropertySetPropertyRelationship(PropertySetPropertyRelationship.getId());
        assertNull(returnedRelationship);
    }
}
