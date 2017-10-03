package travel.snapshot.dp.qa.junit.tests.identity.access_checks;

import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_SALESFORCE_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;

import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.qa.junit.tests.common.CommonTest;

import java.util.UUID;

/**
 * API tests for attributes hiding for external applications
 *
 * Based on DPIM-35
 */
public class AttributesHidingTests extends CommonTest {

    private UUID externalApplicationVersionId;
    private UUID externalApplicationId;
    private UUID createdUserId;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        externalApplicationId = commonHelpers.entityIsCreated(testApplication3);
        externalApplicationVersionId = commonHelpers.entityIsCreated(testAppVersion3);
        createdUserId = commonHelpers.entityIsCreated(testUser1);
        commonHelpers.entityIsCreated(relationshipsHelpers.constructUserPropertyRelationshipDto(
                createdUserId, DEFAULT_PROPERTY_ID, true));
//        Populate applicationPermission table with all permissions for the new external app so it can "see" all endpoints
        dbSteps.populateApplicationPermissionsTableForApplication(externalApplicationId);
        commonHelpers.entityIsCreated(constructCommercialSubscriptionDto(externalApplicationId, DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID));
    }

    @Test
    public void hideCustomerAttributesTest() {
        CustomerDto createdCustomer = commonHelpers.entityIsCreatedAs(CustomerDto.class, testCustomer1);
        assertNotNull(createdCustomer.getSalesforceId());
        assertNotNull(createdCustomer.getIsDemo());

        setUserCustomerRights(createdUserId, createdCustomer.getId());
        CustomerDto customerReturnedForExternalApp = commonHelpers.getEntityAsTypeByUserForApp(
                createdUserId, externalApplicationVersionId, CUSTOMERS_PATH, CustomerDto.class, createdCustomer.getId()
        );
        assertNull(customerReturnedForExternalApp.getSalesforceId());
        assertNull(customerReturnedForExternalApp.getIsDemo());
    }

    @Test
    public void hidePropertyAttributesTest() {
        testProperty1.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
        testProperty1.setTtiId(12345);
        PropertyDto createdProperty = commonHelpers.entityIsCreatedAs(PropertyDto.class, testProperty1);
        assertNotNull(createdProperty.getSalesforceId());
        assertNotNull(createdProperty.getIsDemo());
        assertNotNull(createdProperty.getTtiId());

        setUserPropertyRights(createdProperty.getId());
        PropertyDto propertyReturnedForExternalApp = commonHelpers.getEntityAsTypeByUserForApp(
                createdUserId, externalApplicationVersionId, PROPERTIES_PATH, PropertyDto.class, createdProperty.getId());
        assertNull(propertyReturnedForExternalApp.getSalesforceId());
        assertNull(propertyReturnedForExternalApp.getIsDemo());
        assertNull(propertyReturnedForExternalApp.getTtiId());
    }

    @Test
    public void hideApplicationAttributesTest() {
        ApplicationDto createdApplication = commonHelpers.entityIsCreatedAs(ApplicationDto.class, testApplication1);
        assertNotNull(createdApplication.getPartnerId());

        commonHelpers.entityIsCreated(constructCommercialSubscriptionDto(createdApplication.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, DEFAULT_PROPERTY_ID));
        ApplicationDto appReturnedForExternalApp = commonHelpers.getEntityAsTypeByUserForApp(
                createdUserId, externalApplicationVersionId, APPLICATIONS_PATH, ApplicationDto.class, createdApplication.getId());
        assertNull(appReturnedForExternalApp.getPartnerId());
    }


    /**
     * Help method for tests in this class
     *
     * Every user can now have only one user-customer relationship (multi-tenancy was temporarily restricted). So if we want to
     * create new user-customer relationship we have to delete the old one first.
     * Also if external app is to be used, commercialSubscription for the new customer needs to be created with this app
     * or the user/app version pair will not see any data.
     */
    private void setUserCustomerRights(UUID userId, UUID customerId) {
        userHelpers.deleteExistingUserCustomerRelationship(userId);
        commonHelpers.entityIsCreated(relationshipsHelpers.constructUserCustomerRelationshipDto(
                userId, customerId, true, true));
        commonHelpers.entityIsCreated(constructCommercialSubscriptionDto(externalApplicationId, customerId, DEFAULT_PROPERTY_ID));
    }

    private void setUserPropertyRights(UUID propertyId) {
        commonHelpers.entityIsCreated(constructCommercialSubscriptionDto(externalApplicationId, DEFAULT_SNAPSHOT_CUSTOMER_ID, propertyId));
        commonHelpers.entityIsCreated(relationshipsHelpers.constructUserPropertyRelationshipDto(
                createdUserId, propertyId, true));
    }
}
