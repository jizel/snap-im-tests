package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static travel.snapshot.dp.api.type.HttpMethod.DELETE;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

/**
 * Endpoint restrictions for customers
 */
@RunWith(SerenityRunner.class)
public class CustomerRestrictionsTests extends CommonRestrictionTest {

    @Test
    public void getCustomerRestrictionTest(){
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, null, null, null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT, GET);
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.customerWithIdIsGotByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, GET);
        customerHelpers.customerWithIdIsGotByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    public void getCustomerSecondLevelEndpointsRestrictionTest(){
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_CUSTOMER_PROPERTIES_ENDPOINT, GET);
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_CUSTOMER_USERS_ENDPOINT, GET);
        customerHelpers.listOfUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudCustomerRestrictionTest() throws Exception{
//        Create
        commonHelpers.createEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT, POST);
        commonHelpers.createEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1);
        responseCodeIs(SC_CREATED);
//        Update
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Updated Customer Name");
        customerHelpers.updateCustomerByUserForApp(testCustomer1.getId(), DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), customerUpdate);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, POST);
        customerHelpers.updateCustomerByUserForApp(testCustomer1.getId(), DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), customerUpdate);
        responseCodeIs(SC_NO_CONTENT);
//        Delete
        customerHelpers.deleteCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, DELETE);
        customerHelpers.deleteCustomerByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testCustomer1.getId());
        responseCodeIs(SC_NO_CONTENT);
    }

}

