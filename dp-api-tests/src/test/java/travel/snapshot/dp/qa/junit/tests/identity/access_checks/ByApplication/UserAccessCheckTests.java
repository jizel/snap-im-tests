package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;

import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;
import java.util.UUID;

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
        fieldName = Optional.of("user_name");
        pattern = Optional.of("johnSmith");
    }
}
