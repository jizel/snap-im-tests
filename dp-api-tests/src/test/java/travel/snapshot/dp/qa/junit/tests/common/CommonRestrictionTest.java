package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;

import org.junit.Before;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;

import java.util.UUID;

/**
 * Commons for Restriction tests.
 */
public abstract class CommonRestrictionTest extends CommonTest {

    protected static final String RESTRICTIONS_ALL_USERS_ENDPOINT = "/identity/users";
    protected static final String RESTRICTIONS_SINGLE_USER_ENDPOINT = "/identity/users/{user_id}";
    protected static final String RESTRICTIONS_USER_CUSTOMER_ENDPOINT = "/identity/users/{user_id}/customers/{customer_id}";
    protected static final String RESTRICTIONS_USER_PROPERTIES_ENDPOINT = "/identity/users/{user_id}/properties";
    protected static final String RESTRICTIONS_USER_PROPERTIES_ROLES_ENDPOINT = "/identity/users/{user_id}/properties/{property_id}/roles";
    protected static final String RESTRICTIONS_USER_PROPERTY_SETS_ROLES_ENDPOINT = "/identity/users/{user_id}/property_sets/{property_set_id}/roles";
    protected static final String RESTRICTIONS_USER_CUSTOMERS_ROLES_ENDPOINT = "/identity/users/{user_id}/customers/{customer_id}/roles";

    protected static final String RESTRICTIONS_ALL_PROPERTIES_ENDPOINT = "/identity/properties";
    protected static final String RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT = "/identity/properties/{property_id}";
    protected static final String RESTRICTIONS_PROPERTY_CUSTOMERS_ENDPOINT = "/identity/properties/{property_id}/customers";
    protected static final String RESTRICTIONS_PROPERTY_USERS_ENDPOINT = "/identity/properties/{property_id}/users";
    protected static final String RESTRICTIONS_PROPERTY_PROPERTY_SETS_ENDPOINT = "/identity/properties/{property_id}/property_sets";

    protected static final String RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT = "/identity/customers";
    protected static final String RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT = "/identity/customers/{customer_id}";
    protected static final String RESTRICTIONS_CUSTOMER_PROPERTIES_ENDPOINT = "/identity/customers/{customer_id}/properties";
    protected static final String RESTRICTIONS_CUSTOMER_USERS_ENDPOINT = "/identity/customers/{customer_id}/users";

    public static final String RESTRICTIONS_APPLICATIONS_ENDPOINT = "/identity" + APPLICATIONS_PATH;
    public static final String RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT = "/identity" + APPLICATIONS_PATH + "/{application_id}";

    protected ApplicationDto restrictedApp;
    protected ApplicationVersionDto createdAppVersion;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        restrictedApp = commonHelpers.entityIsCreatedAs(ApplicationDto.class, testApplication1);
        createdAppVersion = createTestApplicationVersionForApp(restrictedApp.getId());
    }

    //    Help methods
    private ApplicationVersionDto createTestApplicationVersionForApp(UUID applicationId){
        ApplicationVersionCreateDto testAppVersion = new ApplicationVersionCreateDto();
        testAppVersion.setApplicationId(applicationId);
        testAppVersion.setIsActive(true);
        testAppVersion.setIsNonCommercial(true);
        testAppVersion.setName("testAppVersion");
        testAppVersion.setStatus(CERTIFIED);
        testAppVersion.setApiManagerId("123");
        return commonHelpers.entityIsCreatedAs(ApplicationVersionDto.class, testAppVersion);
    }

}
