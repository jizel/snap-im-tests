package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
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
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.buildQueryParamMapForPaging;

import org.junit.Test;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

import java.util.Map;

/**
 *  Endpoint restrictions for properties
 */
public class PropertyRestrictionTest extends CommonRestrictionTest{

    @Test
    public void getPropertyRestrictionTest(){

        commonHelpers.getEntitiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH,
                buildQueryParamMapForPaging(null, null,"name==*", null, "name", null));
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_PROPERTIES_ENDPOINT, GET);
        commonHelpers.getEntitiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH,
                buildQueryParamMapForPaging(null, null,"name==*", null, "name", null));
        responseCodeIs(SC_OK);

        commonHelpers.getEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(),PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT, GET);
        commonHelpers.getEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudPropertyRestrictionTest(){
//        Create
        commonHelpers.createEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_ALL_PROPERTIES_ENDPOINT, POST);
        commonHelpers.createEntityByUserForApplication(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1);
        responseCodeIs(SC_CREATED);
//        Update
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("Updated Name");
        commonHelpers.updateEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, testProperty1.getId(), propertyUpdate);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT, PATCH);
        commonHelpers.updateEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(),PROPERTIES_PATH, testProperty1.getId(), propertyUpdate);
        responseCodeIs(SC_OK);
//        Delete
        commonHelpers.deleteEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, testProperty1.getId());
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_SINGLE_PROPERTY_ENDPOINT, DELETE);
        commonHelpers.deleteEntityByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, testProperty1.getId());
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void getPropertySecondLevelEntitiesRestrictionTest() throws Throwable {
        Map<String, String> emptyParams = buildQueryParamMapForPaging(null, null, null, null, null, null);
//        Customers
        commonHelpers.getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, CUSTOMERS_RESOURCE, emptyParams);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_PROPERTY_CUSTOMERS_ENDPOINT, GET);
        commonHelpers.getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, CUSTOMERS_RESOURCE, emptyParams);
        responseCodeIs(SC_OK);

//        Users
        commonHelpers.getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, USERS_RESOURCE, emptyParams);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_PROPERTY_USERS_ENDPOINT, GET);
        commonHelpers.getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, USERS_RESOURCE, emptyParams);
        responseCodeIs(SC_OK);

//        Property Sets
        commonHelpers.getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, PROPERTY_SETS_RESOURCE, emptyParams);
        responseIsEndpointNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), RESTRICTIONS_PROPERTY_PROPERTY_SETS_ENDPOINT, GET);
        commonHelpers.getRelationshipsByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), PROPERTIES_PATH, DEFAULT_PROPERTY_ID, PROPERTY_SETS_RESOURCE, emptyParams);
        responseCodeIs(SC_OK);
    }
}
