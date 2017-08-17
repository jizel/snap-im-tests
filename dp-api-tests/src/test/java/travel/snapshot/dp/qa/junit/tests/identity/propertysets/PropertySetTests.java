package travel.snapshot.dp.qa.junit.tests.identity.propertysets;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.io.IOException;
import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;

@RunWith(SerenityRunner.class)
public class PropertySetTests extends CommonTest {

    @Test
    public void propertySetTypeIsMandatory() {
        testPropertySet1.setType(null);
        commonHelpers.createEntity(PROPERTY_SETS_PATH, testPropertySet1);
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    @Test
    public void parentChildLoopTest() throws IOException {
        UUID ps1Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        testPropertySet2.setParentId(ps1Id);
        UUID ps2Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet2);
        testPropertySet3.setParentId(ps2Id);
        UUID ps3Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet3);
        PropertySetUpdateDto update = new PropertySetUpdateDto();
        update.setParentId(ps3Id);
        commonHelpers.updateEntityWithEtag(PROPERTY_SETS_PATH, ps1Id, update);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CIRCULAR_DEPENDENCY);
        bodyContainsEntityWith("message", "Circular dependency in PropertySet hierarchy.");
    }

    @Test
    public void parentPropertySetCannotBeDeletedUntilAllChildPropertySetsAreDeleted() throws IOException {
        UUID ps1Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        testPropertySet2.setParentId(ps1Id);
        UUID ps2Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet2);
        testPropertySet3.setParentId(ps2Id);
        UUID ps3Id = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet3);
        commonHelpers.deleteEntityWithEtag(PROPERTY_SETS_PATH, ps1Id);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
        commonHelpers.deleteEntityWithEtag(PROPERTY_SETS_PATH, ps2Id);
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
        commonHelpers.entityIsDeletedWithEtag(PROPERTY_SETS_PATH, ps3Id);
        commonHelpers.entityIsDeletedWithEtag(PROPERTY_SETS_PATH, ps2Id);
        commonHelpers.entityIsDeletedWithEtag(PROPERTY_SETS_PATH, ps1Id);
    }

    // PropertySet-Users

    @Test
    public void updatingPropertySetUserRelationship() throws IOException {
        UUID psId = commonHelpers.entityIsCreated(PROPERTY_SETS_PATH, testPropertySet1);
        UUID userId = commonHelpers.entityIsCreated(USERS_PATH, testUser1);
        UserPropertySetRelationshipDto relation = relationshipsHelpers.constructUserPropertySetRelationshipDto(userId, psId, true);
        UUID relationId = commonHelpers.entityIsCreated(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relation);
        UserPropertySetRelationshipUpdateDto update = new UserPropertySetRelationshipUpdateDto();
        update.setIsActive(false);
        commonHelpers.updateEntityWithEtag(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "false");
        update.setIsActive(true);
        commonHelpers.updateEntityWithEtag(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId, update);
        responseCodeIs(SC_NO_CONTENT);
        commonHelpers.getEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relationId);
        bodyContainsEntityWith("is_active", "true");
    }


}