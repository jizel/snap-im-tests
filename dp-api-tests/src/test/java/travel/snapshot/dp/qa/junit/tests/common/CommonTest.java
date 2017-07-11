package travel.snapshot.dp.qa.junit.tests.common;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import org.junit.After;
import org.junit.Before;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.cucumber.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.configuration.ConfigurationSteps;
import travel.snapshot.dp.qa.cucumber.serenity.jms.JmsSteps;
import travel.snapshot.dp.qa.cucumber.steps.DbStepDefs;
import travel.snapshot.dp.qa.junit.helpers.*;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;

import java.util.Map;


/**
 * Commons for all test classes. Every new test class should extend this class and use it's protected variables such as:
 * - instantiated *Helper classes that contains test steps for entities (they extend *Steps classes)
 * - instantiated Dtos from entities yaml files
 * - constants
 *
 * There are also new help methods defined (public) when found useful in more than one test class.
 * Also common @Before and @After Junit steps are defined here. They can be overridden
 *
 * This class is declared abstract and there should never be a good reason to instantiate it.
 */
public abstract class CommonTest {
    public static final String YAML_DATA_PATH = "src/test/resources/yaml/%s";
    public static final String NOTIFICATION_CRUD_TOPIC = "Notifications.crud";
    public static final String JMS_SUBSCRIPTION_NAME = "Test";

    //    Get EntitiesLoader instance containing all test entity data
    protected static final EntitiesLoader entitiesLoader = EntitiesLoader.getInstance();

    //    Steps
    protected static DbStepDefs dbStepDefs = new DbStepDefs();
    protected static final JmsSteps jmsSteps = new JmsSteps();
    protected static final ConfigurationSteps configurationSteps = new ConfigurationSteps();
    protected static final DbUtilsSteps dbSteps = new DbUtilsSteps();

    //    Helpers
    protected static final CustomerHelpers customerHelpers = new CustomerHelpers();
    protected static final PropertyHelpers propertyHelpers = new PropertyHelpers();
    protected static final PropertySetHelpers propertySetHelpers = new PropertySetHelpers();
    protected static final UserHelpers userHelpers = new UserHelpers();
    protected static final RoleHelpers roleHelpers = new RoleHelpers();
    protected static final PartnerHelpers partnerHelpers = new PartnerHelpers();
    protected static final RelationshipsHelpers relationshipsHelpers = new RelationshipsHelpers();
    protected static final UserGroupHelpers userGroupHelpers = new UserGroupHelpers();
    protected static final ApplicationHelpers applicationHelpers = new ApplicationHelpers();
    protected static final ApplicationVersionHelpers applicationVersionHelpers = new ApplicationVersionHelpers();
    protected static final CommercialSubscriptionHelpers commercialSubscriptionHelpers = new CommercialSubscriptionHelpers();
    protected static final ConfigurationHelpers configurationHelpers = new ConfigurationHelpers();
    protected static final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();
    protected static final KeycloakHelpers keycloakHelpers = new KeycloakHelpers();

    //    Basic test entities
    protected static final UserCreateDto testUser1 = entitiesLoader.getUserDtos().get("user1");
    protected static final UserCreateDto testUser2 = entitiesLoader.getUserDtos().get("user2");
    protected static final UserCreateDto testUser3 = entitiesLoader.getUserDtos().get("user3");
    protected static final UserCreateDto testSnapshotUser1 = entitiesLoader.getSnapshotUserDtos().get("snapshotUser1");
    protected static final CustomerCreateDto testCustomer1 = entitiesLoader.getCustomerDtos().get("customer1");
    protected static final CustomerCreateDto testCustomer2 = entitiesLoader.getCustomerDtos().get("customer2");
    protected static final CustomerCreateDto testCustomer3 = entitiesLoader.getCustomerDtos().get("customer3");
    protected static final CustomerCreateDto testCustomer4 = entitiesLoader.getCustomerDtos().get("customer4");
    protected static final PropertyDto testProperty1 = entitiesLoader.getPropertyDtos().get("property1");
    protected static final PropertyDto testProperty2 = entitiesLoader.getPropertyDtos().get("property2");
    protected static final PropertySetDto testPropertySet1 = entitiesLoader.getPropertySetDtos().get("propertySet1");
    protected static final CustomerRoleDto testCustomerRole1 = entitiesLoader.getCustomerRoleDtos().get("customerRole1");
    protected static final PartnerDto testPartner1 = entitiesLoader.getPartnerDtos().get("partner1");
    protected static final PartnerDto testPartner2 = entitiesLoader.getPartnerDtos().get("partner2");
    protected static final PartnerDto testPartner3 = entitiesLoader.getPartnerDtos().get("partner3");
    protected static final UserGroupDto testUserGroup1 = entitiesLoader.getUserGroupDtos().get("user_group1");
    protected static final ApplicationDto testApplication1 = entitiesLoader.getApplicationDtos().get("application1");
    protected static final ApplicationDto testApplication2 = entitiesLoader.getApplicationDtos().get("application2");
    protected static final ApplicationDto testApplication3 = entitiesLoader.getApplicationDtos().get("application3");
    protected static final ApplicationDto testApplication4 = entitiesLoader.getApplicationDtos().get("application4");
    protected static final ApplicationVersionDto testAppVersion1 = entitiesLoader.getApplicationVersionDtos().get("app_version1");
    protected static final ApplicationVersionDto testAppVersion2 = entitiesLoader.getApplicationVersionDtos().get("app_version2");
    protected static final ApplicationVersionDto testAppVersion3 = entitiesLoader.getApplicationVersionDtos().get("app_version3");


    //    Custom codes
    protected static final int SEMANTIC_ERRORS_CUSTOM_CODE = 42201;
    protected static final int NON_EXISTING_REFERENCE_CUSTOM_CODE = 42202;
    protected static final int NOT_FOUND_CUSTOM_CODE = 40401;
    protected static final int CONFLICT_CUSTOM_CODE = 40902;
    protected static final int INSUFFICIENT_PERMISSIONS_CUSTOM_CODE = 40301;


    @Before
    public void setUp() throws Throwable {
        dbStepDefs.databaseIsCleanedAndEntitiesAreCreated();
    }

    @After
    public void cleanUp() throws Throwable {
    }

    //    Help methods

    protected void verifyResponseAndCustomCode(String responseCode, String customCode) {
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    public static String transformNull(String value) {
        if ("/null".equals(value)) {
            return null;
        }
        return value;
    }

    protected void responseIsNotFound() {
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(NOT_FOUND_CUSTOM_CODE);
    }


    /**
     * Facade help methods for BasicSteps static methods.
     *
     * All the tests don't have to static import the methods but can use this directly.
     */
    protected static void responseCodeIs(int responseCode) {
        BasicSteps.responseCodeIs(responseCode);
    }

    protected static void customCodeIs(int responseCode) {
        BasicSteps.customCodeIs(responseCode);
    }

    protected static void bodyContainsEntityWith(String attributeName) {
        BasicSteps.bodyContainsEntityWith(attributeName);
    }

    protected static void bodyContainsEntityWith(String attributeName, Integer attributeValue) {
        BasicSteps.bodyContainsEntityWith(attributeName, attributeValue);
    }

    protected static void bodyContainsEntityWith(String attributeName, String attributeValue) {
        BasicSteps.bodyContainsEntityWith(attributeName, attributeValue);
    }

    protected static void bodyIsEmpty() {
        BasicSteps.bodyIsEmpty();
    }

    protected static void contentTypeIs(String contentType) {
        BasicSteps.contentTypeIs(contentType);
    }

}
