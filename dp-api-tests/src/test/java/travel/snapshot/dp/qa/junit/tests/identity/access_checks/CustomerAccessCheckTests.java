package travel.snapshot.dp.qa.junit.tests.identity.access_checks;


import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;


/**
 * Sample customer tests using YAML data
 */

@RunWith(SerenityRunner.class)
public class CustomerAccessCheckTests extends CommonTest {

    //    Load this test class specific test data
    private static UserDto createdUser = null;
    private static CustomerDto createdCustomer1 = null;
    private static CustomerDto createdCustomer2 = null;
    private static ApplicationDto createdApp1 = null;
    private static ApplicationDto createdApp2 = null;
    private static ApplicationVersionDto versionWithSubscription = null;
    private static ApplicationVersionDto versionWithoutSubscription = null;
    private static ApplicationVersionDto nonCommercialVersion = null;



    @Before
    public void setUp() throws Throwable {
        super.setUp();
        createdCustomer1 = customerHelpers.customerIsCreated(testCustomer1);
        createdCustomer2 = customerHelpers.customerIsCreated(testCustomer2);
        createdApp1 = applicationHelpers.applicationIsCreated(testApplication1);
        createdApp2 = applicationHelpers.applicationIsCreated(testApplication2);
        applicationHelpers.grantAllPermissions(createdApp1.getId());
        applicationHelpers.grantAllPermissions(createdApp2.getId());
        createdUser = userHelpers.userWithCustomerIsCreated(testUser1, createdCustomer1.getId());
        // Version with subscription
        commercialSubscriptionHelpers.commercialSubscriptionIsCreated(createdCustomer1.getId(), DEFAULT_PROPERTY_ID, createdApp1.getId());
        testAppVersion1.setApplicationId(createdApp1.getId());
        versionWithSubscription = applicationVersionHelpers.applicationVersionIsCreated(testAppVersion1);
        // Version without subscription
        testAppVersion2.setApplicationId(createdApp2.getId());
        versionWithoutSubscription = applicationVersionHelpers.applicationVersionIsCreated(testAppVersion2);
        // Non-commercial version
        testAppVersion3.setApplicationId(createdApp2.getId());
        testAppVersion3.setIsNonCommercial(true);
        nonCommercialVersion = applicationVersionHelpers.applicationVersionIsCreated(testAppVersion3);
        propertyHelpers.relationExistsBetweenUserAndProperty(createdUser.getId(), DEFAULT_PROPERTY_ID, true);
    }

    @Test
    public void thereIsActiveCommercialSubscriptionLinkingToTheApplicationVersion() throws Throwable {
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), versionWithSubscription.getId(), createdCustomer1.getId());
        responseCodeIs(SC_OK);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), nonCommercialVersion.getId(), createdCustomer1.getId());
        responseCodeIs(SC_OK);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), versionWithSubscription.getId(), createdCustomer2.getId());
        responseCodeIs(SC_NOT_FOUND);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), versionWithoutSubscription.getId(), createdCustomer1.getId());
        responseCodeIs(SC_FORBIDDEN);
    }

    @Test
    public void thereIsActiveCommercialSubscriptionWithParentCustomerEntity() throws Throwable {
        testCustomer3.setParentId(createdCustomer1.getId());
        CustomerDto newCustomer1 = customerHelpers.customerIsCreated(testCustomer3);
        testCustomer4.setParentId(newCustomer1.getId());
        CustomerDto newCustomer2 = customerHelpers.customerIsCreated(testCustomer4);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), versionWithSubscription.getId(), newCustomer2.getId());
        responseCodeIs(SC_OK);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), nonCommercialVersion.getId(), newCustomer2.getId());
        responseCodeIs(SC_OK);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUser.getId(), versionWithoutSubscription.getId(), newCustomer2.getId());
        responseCodeIs(SC_FORBIDDEN);

    }

}
