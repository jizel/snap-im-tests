package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.type.HttpMethod.DELETE;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.api.type.HttpMethod.PATCH;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers.constructCommercialSubscriptionDto;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.emptyQueryParams;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsCreated;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.entityIsDeleted;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers.constructUserCustomerRelationshipDto;

import org.junit.Test;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

import java.util.UUID;

/**
 * Endpoint restrictions for customers
 */
public class CustomerRestrictionsTests extends CommonRestrictionTest {

    @Test
    public void getCustomerRestrictionTest(){
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(createdUserWithRelationshipsId, createdAppVersion.getId(), null, null, null, null, null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT, GET);
        customerHelpers.listOfCustomersIsGotByUserForAppVersionWith(createdUserWithRelationshipsId, createdAppVersion.getId(), null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.customerWithIdIsGotByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, GET);
        customerHelpers.customerWithIdIsGotByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    public void getCustomerSecondLevelEndpointsRestrictionTest(){
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_CUSTOMER_PROPERTIES_ENDPOINT, GET);
        customerHelpers.listOfCustomerPropertiesIsGotByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);

        customerHelpers.listOfUsersIsGotByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_CUSTOMER_USERS_ENDPOINT, GET);
        customerHelpers.listOfUsersIsGotByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), DEFAULT_SNAPSHOT_CUSTOMER_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudCustomerRestrictionTest() throws Exception{
//        Create
        createEntityByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), testCustomer1);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_CUSTOMERS_ENDPOINT, POST);
        createEntityByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), testCustomer1)
                .then().statusCode(SC_CREATED);
//        Update
        setApplicationAccessRightsForNewCustomer();
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setName("Updated Customer Name");
        updateEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), CUSTOMERS_PATH, testCustomer1.getId(), customerUpdate);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, PATCH);
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, GET);
        updateEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), CUSTOMERS_PATH, testCustomer1.getId(), customerUpdate)
                .then().statusCode(SC_OK);
//        Delete
        deleteEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), CUSTOMERS_PATH, testCustomer1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_CUSTOMER_ENDPOINT, DELETE);
        deleteEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), CUSTOMERS_PATH, testCustomer1.getId())
                .then().statusCode(SC_CONFLICT);
    }

    private void setApplicationAccessRightsForNewCustomer(){
        UUID existingRelationshipId = getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, emptyQueryParams()).get(0).getId();
        entityIsDeleted(USER_CUSTOMER_RELATIONSHIPS_PATH, existingRelationshipId);
        entityIsCreated(constructUserCustomerRelationshipDto(createdUserWithRelationshipsId, testCustomer1.getId(), true, true));
        entityIsCreated(constructCommercialSubscriptionDto(restrictedApp.getId(), testCustomer1.getId(), DEFAULT_PROPERTY_ID));
    }
}

