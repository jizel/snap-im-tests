package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import qa.tools.ikeeper.annotation.Jira;

import java.util.Optional;

@Jira("DPIM-206")
@Disabled
public class PropertySetAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        update.put(description, "New description");
        accessibleEntityId = entityIsCreated(testPropertySet2);
        inaccessibleEntityId = entityIsCreated(testPropertySet3);
        entityIsCreated(constructPropertySetPropertyRelationship(accessibleEntityId, propertyId1, true));
        entityIsCreated(constructPropertySetPropertyRelationship(inaccessibleEntityId, propertyId2, true));
        PATH = PROPERTY_SETS_PATH;
        pattern = Optional.of("'Property Set 2'");
        fieldName = Optional.of("name");
    }
}
