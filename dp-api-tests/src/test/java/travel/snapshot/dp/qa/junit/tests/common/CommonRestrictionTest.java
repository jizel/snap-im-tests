package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;

import org.junit.Before;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;

import java.util.UUID;

/**
 * Commons for Restriction tests.
 */
public abstract class CommonRestrictionTest extends CommonTest {

    protected static final String GET_METHOD = "GET";
    protected static final String POST_METHOD = "POST";
    protected static final String DELETE_METHOD = "DELETE";
    protected ApplicationDto restrictedApp;
    protected ApplicationVersionDto createdAppVersion;


    @Before
    public void setUp() throws Throwable {
        super.setUp();
        restrictedApp = applicationHelpers.applicationIsCreated(testApplication1);
        createdAppVersion = createTestApplicationVersionForApp(restrictedApp.getId());
    }

    //    Help methods
    private ApplicationVersionDto createTestApplicationVersionForApp(UUID applicationId){
        ApplicationVersionDto testAppVersion = testAppVersion = new ApplicationVersionDto();
        testAppVersion.setApplicationId(applicationId);
        testAppVersion.setIsActive(true);
        testAppVersion.setIsNonCommercial(true);
        testAppVersion.setName("testAppVersion");
        testAppVersion.setStatus(CERTIFIED);
        testAppVersion.setApiManagerId("123");
        return applicationVersionHelpers.applicationVersionIsCreated(testAppVersion);
    }

}
