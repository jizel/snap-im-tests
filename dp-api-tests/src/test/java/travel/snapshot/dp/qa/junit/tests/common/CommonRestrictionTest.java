package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.EFFECTIVE_PERMISSIONS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreatedAs;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

import org.junit.Before;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;

import java.util.UUID;

/**
 * Commons for Restriction tests.
 */
public abstract class CommonRestrictionTest extends CommonTest {

    private static String IDENTITY_PREFIX = "/identity";

    protected static final String RESTRICTIONS_ALL_USERS_ENDPOINT = IDENTITY_PREFIX + "/users";
    protected static final String RESTRICTIONS_SINGLE_USER_ENDPOINT = IDENTITY_PREFIX + "/users/{user_id}";
    protected static final String RESTRICTIONS_USER_CUSTOMER_ENDPOINT = IDENTITY_PREFIX + "/users/{user_id}/customers/{customer_id}";
    protected static final String RESTRICTIONS_USER_PROPERTIES_ENDPOINT = IDENTITY_PREFIX + "/users/{user_id}/properties";
    protected static final String RESTRICTIONS_USER_PROPERTIES_ROLES_ENDPOINT = IDENTITY_PREFIX + "/users/{user_id}/properties/{property_id}/roles";
    protected static final String RESTRICTIONS_USER_PROPERTY_SETS_ROLES_ENDPOINT = IDENTITY_PREFIX + "/users/{user_id}/property_sets/{property_set_id}/roles";
    protected static final String RESTRICTIONS_USER_CUSTOMERS_ROLES_ENDPOINT = IDENTITY_PREFIX + "/users/{user_id}/customers/{customer_id}/roles";

    protected static final String RESTRICTIONS_ALL_PROPERTIES_ENDPOINT = IDENTITY_PREFIX + "/properties";
    protected static final String RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT = IDENTITY_PREFIX + "/properties/{property_id}";
    protected static final String RESTRICTIONS_PROPERTY_CUSTOMERS_ENDPOINT = IDENTITY_PREFIX + "/properties/{property_id}/customers";
    protected static final String RESTRICTIONS_PROPERTY_USERS_ENDPOINT = IDENTITY_PREFIX + "/properties/{property_id}/users";
    protected static final String RESTRICTIONS_PROPERTY_PROPERTY_SETS_ENDPOINT = IDENTITY_PREFIX + "/properties/{property_id}/property_sets";

    protected static final String RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT = IDENTITY_PREFIX + "/customers";
    protected static final String RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT = IDENTITY_PREFIX + "/customers/{customer_id}";
    protected static final String RESTRICTIONS_CUSTOMER_PROPERTIES_ENDPOINT = IDENTITY_PREFIX + "/customers/{customer_id}/properties";
    protected static final String RESTRICTIONS_CUSTOMER_USERS_ENDPOINT = IDENTITY_PREFIX + "/customers/{customer_id}/users";

    public static final String RESTRICTIONS_APPLICATIONS_ENDPOINT = IDENTITY_PREFIX + APPLICATIONS_PATH;
    public static final String RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT = IDENTITY_PREFIX + APPLICATIONS_PATH + "/{application_id}";

    protected static final String RESTRICTIONS_EFFECTIVE_PERMISSIONS = IDENTITY_PREFIX + EFFECTIVE_PERMISSIONS_PATH;

    protected ApplicationDto restrictedApp;
    protected ApplicationVersionDto createdAppVersion;
    protected UUID createdUserWithRelationshipsId;


    @Before
    public void setUp() {
        super.setUp();
        restrictedApp = entityIsCreatedAs(ApplicationDto.class, testApplication3);
        createdUserWithRelationshipsId = entityIsCreated(testUser3);
        entityIsCreated(constructUserPropertyRelationshipDto(createdUserWithRelationshipsId, DEFAULT_PROPERTY_ID, true));
        entityIsCreated(constructCommercialSubscriptionDto(restrictedApp.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID));

        createdAppVersion = createTestApplicationVersionForApp(restrictedApp.getId());
    }

    //    Help methods
    private ApplicationVersionDto createTestApplicationVersionForApp(UUID applicationId) {
        ApplicationVersionCreateDto testAppVersion = new ApplicationVersionCreateDto();
        testAppVersion.setApplicationId(applicationId);
        testAppVersion.setIsActive(true);
        testAppVersion.setIsNonCommercial(true);
        testAppVersion.setName("testAppVersion");
        testAppVersion.setStatus(CERTIFIED);
        return entityIsCreatedAs(ApplicationVersionDto.class, testAppVersion);
    }

}
