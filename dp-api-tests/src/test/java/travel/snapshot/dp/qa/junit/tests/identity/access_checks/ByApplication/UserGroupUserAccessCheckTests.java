package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

public class UserGroupUserAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID ugId = entityIsCreated(testUserGroup1);
        UUID otherUserId = entityIsCreated(testUser2);
        entityIsCreated(constructUserPropertyRelationshipDto(otherUserId, propertyId2, true));
        accessibleEntityId = entityIsCreated(constructUserGroupUserRelationship(ugId, userId, true));
        inaccessibleEntityId = entityIsCreated(constructUserGroupUserRelationship(ugId, otherUserId, true));
        PATH = USER_GROUP_USER_RELATIONSHIPS_PATH;
        PATTERN = null;
        FIELD_NAME = null;
        EXPECTED_CODE = SC_NOT_FOUND;
        update = INACTIVATE_UPDATE;
    }
}
