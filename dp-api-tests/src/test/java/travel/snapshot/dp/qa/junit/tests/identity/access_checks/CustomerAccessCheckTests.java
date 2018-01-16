package travel.snapshot.dp.qa.junit.tests.identity.access_checks;


import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.commercialSubscriptionIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserPropertyRelationshipDto;

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
        // Create three applications
        UUID app1Id = entityIsCreated(testApplication1);
        UUID app2Id = entityIsCreated(testApplication2);
        UUID app3Id = entityIsCreated(testApplication3);
        dbSteps.populateApplicationPermissionsTableForApplication(app1Id);
        dbSteps.populateApplicationPermissionsTableForApplication(app2Id);
        dbSteps.populateApplicationPermissionsTableForApplication(app3Id);
        // Make user belong to the correct customer
        UserCustomerRelationshipPartialDto userCustRelation = testUser1.getUserCustomerRelationship();
        userCustRelation.setCustomerId(testCustomer1.getId());
        testUser1.setUserCustomerRelationship(userCustRelation);
        // create user
        entityIsCreated(testUser1);
        // Create subscription for app 1
        commercialSubscriptionIsCreated(testCustomer1.getId(), DEFAULT_PROPERTY_ID, testApplication1.getId());
        // Create app version with subscription
        testAppVersion1.setApplicationId(testApplication1.getId());
        entityIsCreated(testAppVersion1);
        // Version without subscription
        testAppVersion2.setApplicationId(testApplication2.getId());
        entityIsCreated(testAppVersion2);
        // Non-commercial version
        testAppVersion3.setApplicationId(testApplication3.getId());
        testAppVersion3.setIsNonCommercial(true);
        entityIsCreated(testAppVersion3);
        // Grant the user access to test property
        UserPropertyRelationshipCreateDto userPropertyRelationship = constructUserPropertyRelationshipDto(testUser1.getId(), DEFAULT_PROPERTY_ID, true);
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
