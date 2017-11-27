package travel.snapshot.dp.qa.junit.tests.identity.access_checks.ByApplication;

import org.junit.jupiter.api.BeforeEach;

import static java.util.Optional.empty;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;

public class UserPropertyAccessCheckTests extends CommonAccessChecksByApplicationTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        accessibleEntityId = userPropertyRelationId1;
        inaccessibleEntityId = userPropertyRelationId2;
        PATH = USER_PROPERTY_RELATIONSHIPS_PATH;
        pattern = empty();
        fieldName = empty();
        expectedCode = SC_FORBIDDEN;
        update = INACTIVATE_UPDATE;
    }
}
