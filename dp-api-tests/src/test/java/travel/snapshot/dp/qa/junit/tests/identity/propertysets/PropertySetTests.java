package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.PropertySetType.GEOLOCATION;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;

import com.jayway.restassured.response.Response;
import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetType;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.UUID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;

@RunWith(SerenityRunner.class)
public class PropertySetTests extends CommonTest {

    private PropertySetDto createdPropertySet;

    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void propertySetCRUDTest() {
        UUID createdCustomerId = commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer2);
        UUID psId = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        PropertySetDto propertySet = commonHelpers.getEntityAsType(PROPERTY_SETS_PATH, PropertySetDto.class, psId);
        assert(propertySet.getType().equals(testPropertySet1.getType()));
        assert(propertySet.getCustomerId().equals(DEFAULT_SNAPSHOT_CUSTOMER_ID));
        assert(propertySet.getDescription().equals(testPropertySet1.getDescription()));
        PropertySetUpdateDto update = new PropertySetUpdateDto();
        update.setType(PropertySetType.GEOLOCATION);
        update.setDescription("Some new desc");
        update.setCustomerId(createdCustomerId);
        commonHelpers.entityIsUpdated(PROPERTY_SETS_PATH, psId, update);
        PropertySetDto updatedPropertySet = commonHelpers.getEntityAsType(PROPERTY_SETS_PATH, PropertySetDto.class, psId);
        assert(updatedPropertySet.getType().equals(PropertySetType.GEOLOCATION));
        assert(updatedPropertySet.getCustomerId().equals(createdCustomerId));
        assert(updatedPropertySet.getDescription().equals("Some new desc"));
        commonHelpers.entityIsDeleted(PROPERTY_SETS_PATH, psId);
    }

    @Test
    public void parentChildLoopTest() {
        UUID ps1Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        testPropertySet2.setParentId(ps1Id);
        UUID ps2Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet2);
        testPropertySet3.setParentId(ps2Id);
        UUID ps3Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet3);
        PropertySetUpdateDto update = new PropertySetUpdateDto();
        update.setParentId(ps3Id);
        commonHelpers.updateEntity(PROPERTY_SETS_PATH, ps1Id, update);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CIRCULAR_DEPENDENCY);
        bodyContainsEntityWith("message", "Circular dependency in PropertySet hierarchy.");
    }

    @Test
    public void parentPropertySetCannotBeDeletedUntilAllChildPropertySetsAreDeleted() {
        UUID ps1Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        testPropertySet2.setParentId(ps1Id);
        UUID ps2Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet2);
        testPropertySet3.setParentId(ps2Id);
        UUID ps3Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet3);
        commonHelpers.deleteEntity(PROPERTY_SETS_PATH, ps1Id);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
        commonHelpers.deleteEntity(PROPERTY_SETS_PATH, ps2Id);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
        commonHelpers.entityIsDeleted(PROPERTY_SETS_PATH, ps3Id);
        commonHelpers.entityIsDeleted(PROPERTY_SETS_PATH, ps2Id);
        commonHelpers.entityIsDeleted(PROPERTY_SETS_PATH, ps1Id);
    }

    // PropertySet-Users

    @Test
    public void updatingPropertySetUserRelationship() {
        UUID psId = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        UUID userId = commonHelpers.entityIsCreated(USERS_PATH, testUser1);
        UserPropertySetRelationshipDto relation = relationshipsHelpers.constructUserPropertySetRelationshipDto(userId, psId, true);
        UUID relationId = commonHelpers.entityIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relation);
        UserPropertySetRelationshipUpdateDto update = new UserPropertySetRelationshipUpdateDto();
        update.setIsActive(false);
        commonHelpers.updateEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        responseCodeIs(SC_OK);
        commonHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        update.setIsActive(true);
        commonHelpers.updateEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        responseCodeIs(SC_OK);
        commonHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "true");
        commonHelpers.entityIsDeleted(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
    }
}
