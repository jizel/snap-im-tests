package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructPropertySetPropertyRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupPropertySetRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserGroupUserRelationship;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertySetRelationshipDto;

@Jira("DPIM-206")
public class UserGroupPropertySetAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        UUID ugId = entityIsCreated(testUserGroup1);
        entityIsCreated(constructUserGroupUserRelationship(ugId, userId, true));
        UUID ps1Id = entityIsCreated(testPropertySet1);
        UUID ps2Id = entityIsCreated(testPropertySet2);
        entityIsCreated(constructPropertySetPropertyRelationship(ps1Id, propertyId1, true));
        entityIsCreated(constructPropertySetPropertyRelationship(ps2Id, propertyId2, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(userId, ps1Id, true));
        entityIsCreated(constructUserPropertySetRelationshipDto(userId, ps2Id, true));
        accessibleEntityId = entityIsCreated(constructUserGroupPropertySetRelationship(ugId, ps1Id, true)) ;
        inaccessibleEntityId = entityIsCreated(constructUserGroupPropertySetRelationship(ugId, ps2Id, true)) ;
        PATH = USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
        PATTERN = null;
        FIELD_NAME = null;
        EXPECTED_CODE = SC_NO_CONTENT;
        update = INACTIVATE_UPDATE;
    }
}
