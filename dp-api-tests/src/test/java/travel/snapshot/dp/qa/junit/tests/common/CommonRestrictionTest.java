package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;

import org.junit.Before;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;

import java.util.UUID;

/**
 * Commons for Restriction tests.
 */
public abstract class CommonRestrictionTest extends CommonTest {

    public static final String RESTRICTIONS_ALL_USERS_ENDPOINT = "/identity/users";
    public static final String RESTRICTIONS_SINGLE_USER_ENDPOINT = "/identity/users/{user_id}";
    public static final String RESTRICTIONS_USER_CUSTOMER_ENDPOINT = "/identity/users/{user_id}/customers/{customer_id}";
    public static final String RESTRICTIONS_USER_PROPERTIES_ENDPOINT = "/identity/users/{user_id}/properties";
    public static final String RESTRICTIONS_USER_PROPERTIES_ROLES_ENDPOINT = "/identity/users/{user_id}/properties/{property_id}/roles";
    public static final String RESTRICTIONS_USER_PROPERTY_SETS_ROLES_ENDPOINT = "/identity/users/{user_id}/property_sets/{property_set_id}/roles";
    public static final String RESTRICTIONS_USER_CUSTOMERS_ROLES_ENDPOINT = "/identity/users/{user_id}/customers/{customer_id}/roles";

    public static final String RESTRICTIONS_ALL_PROPERTIES_ENDPOINT = "/identity/properties";
    public static final String RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT = "/identity/properties/{property_id}";
    public static final String RESTRICTIONS_PROPERTY_CUSTOMERS_ENDPOINT = "/identity/properties/{property_id}/customers";
    public static final String RESTRICTIONS_PROPERTY_USERS_ENDPOINT = "/identity/properties/{property_id}/users";
    public static final String RESTRICTIONS_PROPERTY_PROPERTY_SETS_ENDPOINT = "/identity/properties/{property_id}/property_sets";

    public static final String RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT = "/identity/customers";
    public static final String RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT = "/identity/customers/{customer_id}";
    public static final String RESTRICTIONS_CUSTOMER_PROPERTIES_ENDPOINT = "/identity/customers/{customer_id}/properties";
    public static final String RESTRICTIONS_CUSTOMER_USERS_ENDPOINT = "/identity/customers/{customer_id}/users";

    public static final String RESTRICTIONS_APPLICATIONS_ENDPOINT = "/identity" + APPLICATIONS_PATH;
    public static final String RESTRICTIONS_SINGLE_APPLICATION_ENDPOINT = "/identity" + APPLICATIONS_PATH + "/{application_id}";


    protected static final String GET_METHOD = "GET";
    protected static final String POST_METHOD = "POST";
    protected static final String DELETE_METHOD = "DELETE";
    protected ApplicationDto restrictedApp;
    protected ApplicationVersionDto createdAppVersion;


    @Before
    public void setUp() throws Exception {
        super.setUp();
        restrictedApp = applicationHelpers.applicationIsCreated(testApplication1);
        createdAppVersion = createTestApplicationVersionForApp(restrictedApp.getId());
    }

    //    Help methods
    private ApplicationVersionDto createTestApplicationVersionForApp(UUID applicationId){
        ApplicationVersionDto testAppVersion = new ApplicationVersionDto();
        testAppVersion.setApplicationId(applicationId);
        testAppVersion.setIsActive(true);
        testAppVersion.setIsNonCommercial(true);
        testAppVersion.setName("testAppVersion");
        testAppVersion.setStatus(CERTIFIED);
        testAppVersion.setApiManagerId("123");
        return applicationVersionHelpers.applicationVersionIsCreated(testAppVersion);
    }

}
