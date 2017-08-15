package travel.snapshot.dp.qa.junit.tests.identity.access_checks;


import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Access check tests for customers
 */

@RunWith(SerenityRunner.class)
public class CustomerAccessCheckTests extends CommonTest {

    //    Load this test class specific test data
    private UUID userId = testUser1.getId();
    private UUID versionWithSubscriptionId = testAppVersion1.getId();
    private UUID versionWithoutSubscriptionId = testAppVersion2.getId();
    private UUID nonCommercialVersionId = testAppVersion3.getId();
    private UUID app1Id = testApplication1.getId();
    private UUID app2Id = testApplication2.getId();
    private UUID customer1Id = testCustomer1.getId();
    private UUID customer2Id = testCustomer2.getId();
    private UUID customer3Id = testCustomer3.getId();
    private UUID customer4Id = testCustomer4.getId();


    @Before
    public void setUp() throws Throwable {
        super.setUp();
        // Create two customers
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer1);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer2);
        // Create two applications
        commonHelpers.entityIsCreated(APPLICATIONS_PATH, testApplication1);
        commonHelpers.entityIsCreated(APPLICATIONS_PATH, testApplication2);
        dbSteps.populateApplicationPermissionsTableForApplication(app1Id);
        dbSteps.populateApplicationPermissionsTableForApplication(app2Id);
        // Make user belong to the correct customer
        UserCustomerRelationshipPartialDto userCustRelation = testUser1.getUserCustomerRelationship();
        userCustRelation.setCustomerId(customer1Id);
        testUser1.setUserCustomerRelationship(userCustRelation);
        // create user
        commonHelpers.entityIsCreated(USERS_PATH, testUser1);
        // Create subscription for app 1
        commercialSubscriptionHelpers.commercialSubscriptionIsCreated(customer1Id, DEFAULT_PROPERTY_ID, app1Id);
        // Create app version with subscription
        testAppVersion1.setApplicationId(app1Id);
        commonHelpers.entityIsCreated(APPLICATION_VERSIONS_PATH, testAppVersion1);
        // Version without subscription
        testAppVersion2.setApplicationId(app2Id);
        commonHelpers.entityIsCreated(APPLICATION_VERSIONS_PATH, testAppVersion2);
        // Non-commercial version
        testAppVersion3.setApplicationId(app2Id);
        testAppVersion3.setIsNonCommercial(true);
        commonHelpers.entityIsCreated(APPLICATION_VERSIONS_PATH, testAppVersion3);
        // Grant the user access to test property
        relationshipsHelpers.userPropertyRelationshipIsCreated(userId, DEFAULT_PROPERTY_ID, true);
    }

    @Test
    public void thereIsActiveCommercialSubscriptionLinkingToTheApplicationVersion() throws Throwable {
        commonHelpers.getEntityByUserForApplication(userId, versionWithSubscriptionId, CUSTOMERS_PATH, customer1Id);
        responseCodeIs(SC_OK);
        commonHelpers.getEntityByUserForApplication(userId, nonCommercialVersionId, CUSTOMERS_PATH, customer1Id);
        responseCodeIs(SC_OK);
        commonHelpers.getEntityByUserForApplication(userId, versionWithSubscriptionId, CUSTOMERS_PATH, customer2Id);
        responseCodeIs(SC_NOT_FOUND);
        commonHelpers.getEntityByUserForApplication(userId, versionWithoutSubscriptionId, CUSTOMERS_PATH, customer1Id);
        responseCodeIs(SC_FORBIDDEN);
    }

    @Test
    public void thereIsActiveCommercialSubscriptionWithParentCustomerEntity() throws Throwable {
        testCustomer3.setParentId(customer1Id);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer3);
        testCustomer4.setParentId(customer3Id);
        commonHelpers.entityIsCreated(CUSTOMERS_PATH, testCustomer4);
        commonHelpers.getEntityByUserForApplication(userId, versionWithSubscriptionId, CUSTOMERS_PATH, customer4Id);
        responseCodeIs(SC_OK);
        commonHelpers.getEntityByUserForApplication(userId, nonCommercialVersionId, CUSTOMERS_PATH, customer4Id);
        responseCodeIs(SC_OK);
        commonHelpers.getEntityByUserForApplication(userId, versionWithoutSubscriptionId, CUSTOMERS_PATH, customer4Id);
        responseCodeIs(SC_FORBIDDEN);
    }
}
