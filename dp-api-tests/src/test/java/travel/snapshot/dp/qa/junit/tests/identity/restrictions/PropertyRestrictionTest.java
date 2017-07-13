package travel.snapshot.dp.qa.junit.tests.identity.restrictions;

import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.tests.common.CommonRestrictionTest;

/**
 *  Endpoint restrictions for properties
 */
public class PropertyRestrictionTest extends CommonRestrictionTest{
    private ApplicationDto restrictedApp;
    private ApplicationVersionDto createdAppVersion;

    private static final String ALL_PROPERTIES_ENDPOINT = "/identity/properties";
    private static final String SINGLE_PROPERTY_ENDPOINT = "/identity/properties/{property_id}";
    private static final String PROPERTY_CUSTOMERS_ENDPOINT = "/identity/properties/{property_id}/customers";
    private static final String PROPERTY_USERS_ENDPOINT = "/identity/properties/{property_id}/users";
    private static final String PROPERTY_PROPERTY_SETS_ENDPOINT = "/identity/properties/{property_id}/property_sets";

    @Test
    public void getPropertyRestrictionTest(){
        propertyHelpers.getListOfPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, "name==*", null, "name");
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), ALL_PROPERTIES_ENDPOINT, GET_METHOD);
        propertyHelpers.getListOfPropertiesByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), null, null, "name==*", null, "name");
        responseCodeIs(SC_OK);

        propertyHelpers.getPropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_PROPERTY_ENDPOINT, GET_METHOD);
        propertyHelpers.getPropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID);
        responseCodeIs(SC_OK);
    }

    @Test
    public void crudPropertyRestrictionTest(){
//        Create
        propertyHelpers.createPropertyByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), ALL_PROPERTIES_ENDPOINT, POST_METHOD);
        propertyHelpers.createPropertyByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1);
        responseCodeIs(SC_CREATED);
//        Update
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setName("Updated Name");
        propertyHelpers.updatePropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1.getId(), propertyUpdate);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_PROPERTY_ENDPOINT, POST_METHOD);
        propertyHelpers.updatePropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1.getId(), propertyUpdate);
        responseCodeIs(SC_NO_CONTENT);
//        Delete
        propertyHelpers.deletePropertyByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1.getId());
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), SINGLE_PROPERTY_ENDPOINT, DELETE_METHOD);
        propertyHelpers.deletePropertyByUserForApp(BasicSteps.DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), testProperty1.getId());
        responseCodeIs(SC_NO_CONTENT);
    }

    @Test
    public void getPropertySecondLevelEntitiesRestrictionTest(){
//        Customers
        propertyHelpers.listOfCustomersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID, null, null, null, null, null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), PROPERTY_CUSTOMERS_ENDPOINT, GET_METHOD);
        propertyHelpers.listOfCustomersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);

//        Users
        propertyHelpers.listOfPropertyUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID, null, null, null, null, null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), PROPERTY_USERS_ENDPOINT, GET_METHOD);
        propertyHelpers.listOfPropertyUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);

//        Property Sets
        propertyHelpers.listOfPropertiesPropertySetsIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID, null, null, null, null, null);
        responseIsNotFound();
        dbSteps.addApplicationPermission(restrictedApp.getId(), PROPERTY_PROPERTY_SETS_ENDPOINT, GET_METHOD);
        propertyHelpers.listOfPropertiesPropertySetsIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, createdAppVersion.getId(), DEFAULT_PROPERTY_ID, null, null, null, null, null);
        responseCodeIs(SC_OK);
    }
}
