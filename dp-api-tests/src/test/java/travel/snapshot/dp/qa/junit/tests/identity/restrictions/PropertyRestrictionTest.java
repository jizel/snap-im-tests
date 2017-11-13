package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static java.util.Collections.emptyMap;
import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;
import static travel.snapshot.dp.api.type.HttpMethod.DELETE;
import static travel.snapshot.dp.api.type.HttpMethod.GET;
import static travel.snapshot.dp.api.type.HttpMethod.PATCH;
import static travel.snapshot.dp.api.type.HttpMethod.POST;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.createEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.deleteEntityByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesByUserForApp;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntityByUserForApplication;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.updateEntityByUserForApp;

import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

import java.util.Map;

/**
 *  Endpoint restrictions for properties
 */
public class PropertyRestrictionTest extends CommonRestrictionTest{

    private static Map<String, String> EMPTY_PARAMS = emptyMap();

    @Test
    public void getPropertyRestrictionTest(){

        getEntitiesByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH,
                buildQueryParamMapForPaging(null, null,"name==*", null, "name", null));
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_PROPERTIES_ENDPOINT, GET);
        getEntitiesByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH,
                buildQueryParamMapForPaging(null, null,"name==*", null, "name", null));
        responseCodeIs(SC_OK);

        getEntityByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(),PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT, GET);
        getEntityByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudPropertyRestrictionTest(){
//        Create
        createEntityByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), testProperty1);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_PROPERTIES_ENDPOINT, POST);
        createEntityByUserForApplication(createdUserWithRelationshipsId, createdAppVersion.getId(), testProperty1);
        responseCodeIs(SC_CREATED);
//        Update
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("Updated Name");
        updateEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, testProperty1.getId(), propertyUpdate);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT, PATCH);
        updateEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(),PROPERTIES_PATH, testProperty1.getId(), propertyUpdate);
        responseCodeIs(SC_OK);
//        Delete
        deleteEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, testProperty1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT, DELETE);
        deleteEntityByUserForApp(createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, testProperty1.getId());
        responseCodeIs(SC_CONFLICT);
    }

    @Test
    public void getPropertySecondLevelEntitiesRestrictionTest() throws Throwable {
//        Customers
        commonHelpers.getRelationshipsByUserForApp(
                createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, CUSTOMERS_RESOURCE, EMPTY_PARAMS
        );
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_PROPERTY_CUSTOMERS_ENDPOINT, GET);
        commonHelpers.getRelationshipsByUserForApp(
                createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, CUSTOMERS_RESOURCE, EMPTY_PARAMS
        );
        responseCodeIs(SC_OK);

//        Users
        commonHelpers.getRelationshipsByUserForApp(
                createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, USERS_RESOURCE, EMPTY_PARAMS
        );
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_PROPERTY_USERS_ENDPOINT, GET);
        commonHelpers.getRelationshipsByUserForApp(
                createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, USERS_RESOURCE, EMPTY_PARAMS
        );
        responseCodeIs(SC_OK);

//        Property Sets
        commonHelpers.getRelationshipsByUserForApp(
                createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, PROPERTY_SETS_RESOURCE, EMPTY_PARAMS
        );
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_PROPERTY_PROPERTY_SETS_ENDPOINT, GET);
        commonHelpers.getRelationshipsByUserForApp(
                createdUserWithRelationshipsId, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, PROPERTY_SETS_RESOURCE, EMPTY_PARAMS
        );
        responseCodeIs(SC_OK);
    }
}
