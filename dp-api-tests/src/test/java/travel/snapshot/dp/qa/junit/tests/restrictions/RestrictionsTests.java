package travel.snapshot.dp.qa.junit.tests.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

/**
 * Created by zelezny on 6/30/2017.
 */
@RunWith(SerenityRunner.class)
public class RestrictionsTests extends CommonTest{

    private ApplicationDto restrictedApp;
    private ApplicationVersionDto createdAppVersion;
    private String GET_ALL_CUSTOMERS_PERMISSION_ID = "6ccb9508-d206-474a-8c7b-ba2f3dfdd639";
    private String GET_SINGLE_CUSTOMER_PERMISSION_ID = "71b59b30-83bd-4ff9-9bee-8fa0ba7fe3cc";
    private String GET_CUSTOMERS_PROPERTIES_PERMISSION_ID = "f917e55d-1b07-403b-a2de-727846385df2";
    private String GET_CUSTOMERS_USERS_PERMISSION_ID = "1b3c6f15-b600-4947-a071-2b3cb173d27c";


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        restrictedApp = applicationHelpers.applicationIsCreated(testApplication1);
        ApplicationVersionDto testAppVersion = testAppVersion = new ApplicationVersionDto();
        testAppVersion.setApplicationId(restrictedApp.getId());
        testAppVersion.setIsActive(true);
        testAppVersion.setIsNonCommercial(true);
        testAppVersion.setName("testAppVersion");
        testAppVersion.setStatus(CERTIFIED);
        testAppVersion.setApiManagerId("123");
        createdAppVersion = applicationHelpers.applicationVersionIsCreated(testAppVersion);
    }

    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void getCustomerRestrictionTest(){
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, null, null, null);
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(NOT_FOUND_CUSTOM_CODE);
        dbSteps.addApplicationPermission(restrictedApp.getId(), GET_ALL_CUSTOMERS_PERMISSION_ID);
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.customerWithIdIsGotByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(NOT_FOUND_CUSTOM_CODE);
        dbSteps.addApplicationPermission(restrictedApp.getId(), GET_SINGLE_CUSTOMER_PERMISSION_ID);
        customerHelpers.customerWithIdIsGotByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);

        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(NOT_FOUND_CUSTOM_CODE);
        dbSteps.addApplicationPermission(restrictedApp.getId(), GET_CUSTOMERS_PROPERTIES_PERMISSION_ID);
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(NOT_FOUND_CUSTOM_CODE);
        dbSteps.addApplicationPermission(restrictedApp.getId(), GET_CUSTOMERS_USERS_PERMISSION_ID);
        customerHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);
    }
}
