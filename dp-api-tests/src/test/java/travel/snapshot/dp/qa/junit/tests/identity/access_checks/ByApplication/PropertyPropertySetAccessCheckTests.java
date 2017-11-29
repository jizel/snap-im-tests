package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import static java.util.Optional.empty;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.INACTIVATE_RELATION;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public class PropertyPropertySetAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID psId = entityIsCreated(testPropertySet1);
        entityIsCreated(constructUserPropertySetRelationshipDto(userId, psId, true));
        accessibleEntityId = entityIsCreated(constructPropertySetPropertyRelationship(psId, propertyId1, true)) ;
        inaccessibleEntityId = entityIsCreated(constructPropertySetPropertyRelationship(psId, propertyId2, true)) ;
        PATH = PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
        pattern = empty();
        fieldName = empty();
        expectedCode = SC_NO_CONTENT;
        update = INACTIVATE_RELATION;
    }
}
