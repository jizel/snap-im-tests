package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsUpdated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntity;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntity;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

@RunWith(SerenityRunner.class)
public class PropertySetTests extends CommonTest {

    private PropertySetDto createdPropertySet;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void propertySetCRUDTest() {
        UUID createdCustomerId = entityIsCreated(testCustomer2);
        UUID psId = entityIsCreated(testPropertySet1);
        PropertySetDto propertySet = getEntityAsType(PROPERTY_SETS_PATH, PropertySetDto.class, psId);
        assert(propertySet.getType().equals(testPropertySet1.getType()));
        assert(propertySet.getCustomerId().equals(DEFAULT_SNAPSHOT_CUSTOMER_ID));
        assert(propertySet.getDescription().equals(testPropertySet1.getDescription()));
        PropertySetUpdateDto update = new PropertySetUpdateDto();
        update.setType(PropertySetType.GEOLOCATION);
        update.setDescription("Some new desc");
        update.setCustomerId(createdCustomerId);
        entityIsUpdated(PROPERTY_SETS_PATH, psId, update);
        PropertySetDto updatedPropertySet = getEntityAsType(PROPERTY_SETS_PATH, PropertySetDto.class, psId);
        assert(updatedPropertySet.getType().equals(PropertySetType.GEOLOCATION));
        assert(updatedPropertySet.getCustomerId().equals(createdCustomerId));
        assert(updatedPropertySet.getDescription().equals("Some new desc"));
        entityIsDeleted(PROPERTY_SETS_PATH, psId);
    }

    @Test
    public void parentChildLoopTest() {
        UUID ps1Id = entityIsCreated(testPropertySet1);
        testPropertySet2.setParentId(ps1Id);
        UUID ps2Id = entityIsCreated(testPropertySet2);
        testPropertySet3.setParentId(ps2Id);
        UUID ps3Id = entityIsCreated(testPropertySet3);
        PropertySetUpdateDto update = new PropertySetUpdateDto();
        update.setParentId(ps3Id);
        updateEntity(PROPERTY_SETS_PATH, ps1Id, update);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CIRCULAR_DEPENDENCY);
        bodyContainsEntityWith("message", "Circular dependency in PropertySet hierarchy.");
    }

    @Test
    public void parentPropertySetCannotBeDeletedUntilAllChildPropertySetsAreDeleted() {
        UUID ps1Id = entityIsCreated(testPropertySet1);
        testPropertySet2.setParentId(ps1Id);
        UUID ps2Id = entityIsCreated(testPropertySet2);
        testPropertySet3.setParentId(ps2Id);
        UUID ps3Id = entityIsCreated(testPropertySet3);
        deleteEntity(PROPERTY_SETS_PATH, ps1Id);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
        deleteEntity(PROPERTY_SETS_PATH, ps2Id);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
        entityIsDeleted(PROPERTY_SETS_PATH, ps3Id);
        entityIsDeleted(PROPERTY_SETS_PATH, ps2Id);
        entityIsDeleted(PROPERTY_SETS_PATH, ps1Id);
    }

    // PropertySet-Users

    @Test
    public void updatingPropertySetUserRelationship() {
        UUID psId = entityIsCreated(testPropertySet1);
        UUID userId = entityIsCreated(testUser1);
        UserPropertySetRelationshipCreateDto relation = relationshipsHelpers.constructUserPropertySetRelationshipDto(userId, psId, true);
        UUID relationId = entityIsCreated(relation);
        UserPropertySetRelationshipUpdateDto update = new UserPropertySetRelationshipUpdateDto();
        update.setIsActive(false);
        updateEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        responseCodeIs(SC_OK);
        getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        update.setIsActive(true);
        updateEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        responseCodeIs(SC_OK);
        getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "true");
        entityIsDeleted(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
    }
}
