package travel.snapshot.dp.qa.junit.tests.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

/**
 * Endpoint restrictions for customers
 */
@RunWith(SerenityRunner.class)
public class CustomerRestrictionsTests extends CommonRestrictionTest {

    private ApplicationDto restrictedApp;
    private ApplicationVersionDto createdAppVersion;

    private static final String ALL_CUSTOMERS_ENDPOINT = "/identity/customers";
    private static final String SINGLE_CUSTOMER_ENDPOINT = "/identity/customers/{customer_id}";
    private static final String CUSTOMER_PROPERTIES_ENDPOINT = "/identity/customers/{customer_id}/properties";
    private static final String CUSTOMER_USERS_ENDPOINT = "/identity/customers/{customer_id}/users";


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
        restrictedApp = applicationHelpers.applicationIsCreated(testApplication1);
        createdAppVersion = createTestApplicationVersionForApp(restrictedApp.getId());
    }

    @After
    public void cleanUp() throws Exception {
    }

    @Test
    public void getCustomerRestrictionTest(){
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, null, null, null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), ALL_CUSTOMERS_ENDPOINT, GET_METHOD);
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.customerWithIdIsGotByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_CUSTOMER_ENDPOINT, GET_METHOD);
        customerHelpers.customerWithIdIsGotByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    public void getCustomerSecondLevelEndpointsRestrictionTest(){
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), CUSTOMER_PROPERTIES_ENDPOINT, GET_METHOD);
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), CUSTOMER_USERS_ENDPOINT, GET_METHOD);
        customerHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudCustomerRestrictionTest() throws Exception{
//        Create
        customerHelpers.createCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), ALL_CUSTOMERS_ENDPOINT, POST_METHOD);
        customerHelpers.createCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1);
        responseCodeIs(SC_CREATED);
//        Update
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Updated Customer Name");
        customerHelpers.updateCustomerByUserForApp(testCustomer1.getId(), DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), customerUpdate);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_CUSTOMER_ENDPOINT, POST_METHOD);
        customerHelpers.updateCustomerByUserForApp(testCustomer1.getId(), DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), customerUpdate);
        responseCodeIs(SC_NO_CONTENT);
//        Delete
        customerHelpers.deleteCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_CUSTOMER_ENDPOINT, DELETE_METHOD);
        customerHelpers.deleteCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1.getId());
        responseCodeIs(SC_NO_CONTENT);
    }

}

