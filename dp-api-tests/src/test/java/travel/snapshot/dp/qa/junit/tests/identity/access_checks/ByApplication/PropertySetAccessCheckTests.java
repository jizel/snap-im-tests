package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;

@Jira("DPIM-206")
public class PropertySetAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        accessibleEntityId = entityIsCreated(testPropertySet2);
        inaccessibleEntityId = entityIsCreated(testPropertySet3);
        entityIsCreated(constructPropertySetPropertyRelationship(accessibleEntityId, propertyId1, true));
        entityIsCreated(constructPropertySetPropertyRelationship(inaccessibleEntityId, propertyId2, true));
        PATH = PROPERTY_SETS_PATH;
        PATTERN = "'Property Set*'";
    }
}
