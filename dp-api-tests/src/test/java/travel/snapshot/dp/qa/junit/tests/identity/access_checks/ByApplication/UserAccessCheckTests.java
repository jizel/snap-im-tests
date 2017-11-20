package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;
import travel.snapshot.dp.qa.junit.tests.common.CommonAccessChecksByApplicationTest;

import java.util.UUID;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

public class UserAccessCheckTests extends CommonAccessChecksByApplicationTest {
    private static UUID userId1;


    @BeforeEach
    public void setUp() {
        super.setUp();
        update.put("first_name", "James");
        userId1 = entityIsCreated(testUser2);
        accessibleEntityId = userId;
        inaccessibleEntityId = userId1;
        PATH = USERS_PATH;
        PATTERN = null;
    }
}
