package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import static java.util.Optional.empty;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;

import org.junit.jupiter.api.BeforeEach;

import java.util.UUID;

public class UserGroupPropertyAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID ugId = entityIsCreated(testUserGroup1);
        entityIsCreated(constructUserGroupUserRelationship(ugId, userId, true));
        accessibleEntityId = entityIsCreated(constructUserGroupPropertyRelationship(ugId, propertyId1, true));
        inaccessibleEntityId = entityIsCreated(constructUserGroupPropertyRelationship(ugId, propertyId2, true));
        PATH = USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
        pattern = empty();
        fieldName = empty();
        expectedCode = SC_NO_CONTENT;
        update = INACTIVATE_UPDATE;
    }
}
