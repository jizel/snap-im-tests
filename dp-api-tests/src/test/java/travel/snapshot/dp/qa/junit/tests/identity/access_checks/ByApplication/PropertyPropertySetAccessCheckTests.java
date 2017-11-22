package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

public class PropertyPropertySetAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID psId = entityIsCreated(testPropertySet1);
        entityIsCreated(constructUserPropertySetRelationshipDto(userId, psId, true));
        accessibleEntityId = entityIsCreated(constructPropertySetPropertyRelationship(psId, propertyId1, true)) ;
        inaccessibleEntityId = entityIsCreated(constructPropertySetPropertyRelationship(psId, propertyId2, true)) ;
        PATH = PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
        PATTERN = null;
        FIELD_NAME = null;
        EXPECTED_CODE = SC_NO_CONTENT;
        update.put("is_active", false);
    }
}
