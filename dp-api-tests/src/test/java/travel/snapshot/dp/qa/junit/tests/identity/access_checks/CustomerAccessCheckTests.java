package travel.snapshot.dp.qa.junit.tests.identity.access_checks;


import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;

import org.junit.Before;
import org.junit.Test;
import qa.tools.ikeeper.annotation.Jira;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * Access check tests for customers
 */
public class CustomerAccessCheckTests extends CommonTest {

    //    Load this test class specific test data
    private UUID versionWithSubscriptionId;
    private UUID versionWithoutSubscriptionId;
    private UUID nonCommercialVersionId;


    @Before
    public void setUp() {
        super.setUp();
        versionWithSubscriptionId = testAppVersion1.getId();
        versionWithoutSubscriptionId = testAppVersion2.getId();
        nonCommercialVersionId = testAppVersion3.getId();
        // Create two customers
        entityIsCreated(testCustomer1);
        entityIsCreated(testCustomer2);
        // Create two applications
        entityIsCreated(testApplication1);
        entityIsCreated(testApplication2);
        dbSteps.populateApplicationPermissionsTableForApplication(testApplication1.getId());
        dbSteps.populateApplicationPermissionsTableForApplication(testApplication2.getId());
        // Make user belong to the correct customer
        UserCustomerRelationshipPartialDto userCustRelation = testUser1.getUserCustomerRelationship();
        userCustRelation.setCustomerId(testCustomer1.getId());
        testUser1.setUserCustomerRelationship(userCustRelation);
        // create user
        entityIsCreated(testUser1);
        // Create subscription for app 1
        commercialSubscriptionHelpers.commercialSubscriptionIsCreated(testCustomer1.getId(), DEFAULT_PROPERTY_ID, testApplication1.getId());
        // Create app version with subscription
        testAppVersion1.setApplicationId(testApplication1.getId());
        entityIsCreated(testAppVersion1);
        // Version without subscription
        testAppVersion2.setApplicationId(testApplication2.getId());
        entityIsCreated(testAppVersion2);
        // Non-commercial version
        testAppVersion3.setApplicationId(testApplication2.getId());
        testAppVersion3.setIsNonCommercial(true);
        entityIsCreated(testAppVersion3);
        // Grant the user access to test property
        UserPropertyRelationshipCreateDto userPropertyRelationship = relationshipsHelpers.constructUserPropertyRelationshipDto(testUser1.getId(), DEFAULT_PROPERTY_ID, true);
        entityIsCreated(userPropertyRelationship);
    }

    @Test
    @Jira("DPIM-50")
    public void thereIsActiveCommercialSubscriptionLinkingToTheApplicationVersion() throws Throwable {
        getEntityByUserForApplication(testUser1.getId(), versionWithSubscriptionId, CUSTOMERS_PATH, testCustomer1.getId());
        responseCodeIs(SC_OK);
        getEntityByUserForApplication(testUser1.getId(), nonCommercialVersionId, CUSTOMERS_PATH, testCustomer1.getId());
        responseCodeIs(SC_OK);
        getEntityByUserForApplication(testUser1.getId(), versionWithSubscriptionId, CUSTOMERS_PATH, testCustomer2.getId());
        responseCodeIs(SC_NOT_FOUND);
        getEntityByUserForApplication(testUser1.getId(), versionWithoutSubscriptionId, CUSTOMERS_PATH, testCustomer1.getId());
        responseCodeIs(SC_FORBIDDEN);
    }

    @Test
    public void thereIsActiveCommercialSubscriptionWithParentCustomerEntity() throws Throwable {
        testCustomer3.setParentId(testCustomer1.getId());
        entityIsCreated(testCustomer3);
        testCustomer4.setParentId(testCustomer3.getId());
        entityIsCreated(testCustomer4);
        getEntityByUserForApplication(testUser1.getId(), versionWithSubscriptionId, CUSTOMERS_PATH, testCustomer4.getId());
        responseCodeIs(SC_OK);
        getEntityByUserForApplication(testUser1.getId(), nonCommercialVersionId, CUSTOMERS_PATH, testCustomer4.getId());
        responseCodeIs(SC_OK);
        getEntityByUserForApplication(testUser1.getId(), versionWithoutSubscriptionId, CUSTOMERS_PATH, testCustomer4.getId());
        responseCodeIs(SC_FORBIDDEN);
    }
}
