package travel.snapshot.dp.qa.junit.tests.common;

import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;

import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;

/**
 * Commons for Restriction tests.
 */
public class CommonRestrictionTest extends CommonTest {

    protected static final String GET_METHOD = "GET";
    protected static final String POST_METHOD = "POST";
    protected static final String DELETE_METHOD = "DELETE";

    //    Help methods

    protected ApplicationVersionDto createTestApplicationVersionForApp(String applicationId){
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
