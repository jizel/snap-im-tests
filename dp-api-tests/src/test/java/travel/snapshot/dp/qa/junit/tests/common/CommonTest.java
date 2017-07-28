package travel.snapshot.dp.qa.junit.tests.common;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;

import net.serenitybdd.junit.runners.SerenityRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.runner.RunWith;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;
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
import travel.snapshot.dp.qa.junit.helpers.ApplicationHelpers;
import travel.snapshot.dp.qa.junit.helpers.ApplicationVersionHelpers;
import travel.snapshot.dp.qa.junit.helpers.AuthorizationHelpers;
import travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers;
import travel.snapshot.dp.qa.junit.helpers.ConfigurationHelpers;
import travel.snapshot.dp.qa.junit.helpers.CustomerHelpers;
import travel.snapshot.dp.qa.junit.helpers.KeycloakHelpers;
import travel.snapshot.dp.qa.junit.helpers.PartnerHelpers;
import travel.snapshot.dp.qa.junit.helpers.PropertyHelpers;
import travel.snapshot.dp.qa.junit.helpers.PropertySetHelpers;
import travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers;
import travel.snapshot.dp.qa.junit.helpers.RoleHelpers;
import travel.snapshot.dp.qa.junit.helpers.UserGroupHelpers;
import travel.snapshot.dp.qa.junit.helpers.UserHelpers;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.junit.utils.EntityNonNullMap;
import travel.snapshot.dp.qa.junit.utils.issueKeeperJiraCredentials.JiraCredentialsClient;


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

@RunWith(SerenityRunner.class)
public abstract class CommonTest {
    public static final String YAML_DATA_PATH = "src/test/resources/yaml/%s";
    public static final String NOTIFICATION_CRUD_TOPIC = "Notifications.crud";
    public static final String JMS_SUBSCRIPTION_NAME = "Test";

    protected static EntitiesLoader entitiesLoader;

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


    //    Custom codes
    protected static final int SEMANTIC_ERRORS_CUSTOM_CODE = 42201;
    protected static final int NON_EXISTING_REFERENCE_CUSTOM_CODE = 42202;
    protected static final int NOT_FOUND_CUSTOM_CODE = 40401;
    protected static final int CONFLICT_CUSTOM_CODE = 40902;
    protected static final int INSUFFICIENT_PERMISSIONS_CUSTOM_CODE = 40301;

    //    Basic test entities
    protected static EntityNonNullMap<String, CustomerCreateDto> customerDtos;
    protected static EntityNonNullMap<String, UserCreateDto> userDtos;
    protected static EntityNonNullMap<String, PropertyDto> propertyDtos;
    protected static EntityNonNullMap<String, PropertySetDto> propertySetDtos;
    protected static EntityNonNullMap<String, PartnerDto> partnerDtos;
    protected static EntityNonNullMap<String, ApplicationDto> applicationDtos;
    protected static EntityNonNullMap<String, ApplicationVersionDto> applicationVersionDtos;
    protected static UserCreateDto testUser1;
    protected static UserCreateDto testUser2;
    protected static UserCreateDto testUser3;
    protected static UserCreateDto testSnapshotUser1;
    protected static CustomerCreateDto testCustomer1;
    protected static CustomerCreateDto testCustomer2;
    protected static CustomerCreateDto testCustomer3;
    protected static CustomerCreateDto testCustomer4;
    protected static PropertyDto testProperty1;
    protected static PropertyDto testProperty2;
    protected static PropertySetDto testPropertySet1;
    protected static CustomerRoleDto testCustomerRole1;
    protected static PartnerDto testPartner1;
    protected static PartnerDto testPartner2;
    protected static PartnerDto testPartner3;
    protected static UserGroupDto testUserGroup1;
    protected static ApplicationDto testApplication1;
    protected static ApplicationDto testApplication2;
    protected static ApplicationDto testApplication3;
    protected static ApplicationDto testApplication4;
    protected static ApplicationVersionDto testAppVersion1;
    protected static ApplicationVersionDto testAppVersion2;
    protected static ApplicationVersionDto testAppVersion3;


    /**
     * Loading default entities before each test class so any changes made in class do not interfere with other test classes.
     *
     * JUnit BeforeClass was chosen over overriding default CommonTest constructor to enable using BeforeClass in subclasses
     * without errors (it is performed before super constructor is called)
     *
     * Execution time of this method is roughly 300ms on standard SnapShot laptop so it's not recommended to run it before
     * each test method. Tests within one test class should clean after themselves.
     * Also it might be sometimes wanted to change default attributes for whole class.
     */
    @BeforeClass
    public static void loadDefaultTestEntities() {
        //   Get EntitiesLoader instance containing all test entity data
        entitiesLoader = EntitiesLoader.getInstance();

        customerDtos = entitiesLoader.getCustomerDtos();
        userDtos = entitiesLoader.getUserDtos();
        propertyDtos = entitiesLoader.getPropertyDtos();
        propertySetDtos = entitiesLoader.getPropertySetDtos();
        partnerDtos = entitiesLoader.getPartnerDtos();
        applicationDtos =  entitiesLoader.getApplicationDtos();
        applicationVersionDtos =  entitiesLoader.getApplicationVersionDtos();

        testUser1 = userDtos.get("user1");
        testUser2 = userDtos.get("user2");
        testUser3 = userDtos.get("user3");
        testSnapshotUser1 = entitiesLoader.getSnapshotUserDtos().get("snapshotUser1");
        testCustomer1 = customerDtos.get("customer1");
        testCustomer2 = customerDtos.get("customer2");
        testCustomer3 = customerDtos.get("customer3");
        testCustomer4 = customerDtos.get("customer4");
        testProperty1 = propertyDtos.get("property1");
        testProperty2 = propertyDtos.get("property2");
        testPropertySet1 = propertySetDtos.get("propertySet1");
        testCustomerRole1 = entitiesLoader.getCustomerRoleDtos().get("customerRole1");
        testPartner1 = partnerDtos.get("partner1");
        testPartner2 = partnerDtos.get("partner2");
        testPartner3 = partnerDtos.get("partner3");
        testUserGroup1 = entitiesLoader.getUserGroupDtos().get("user_group1");
        testApplication1 = applicationDtos.get("application1");
        testApplication2 = applicationDtos.get("application2");
        testApplication3 = applicationDtos.get("application3");
        testApplication4 = applicationDtos.get("application4");
        testAppVersion1 = applicationVersionDtos.get("app_version1");
        testAppVersion2 = applicationVersionDtos.get("app_version2");
        testAppVersion3 = applicationVersionDtos.get("app_version3");
    }

    @Rule
    public IKeeperJUnitConnector issueKeeper = new IKeeperJUnitConnector(
            new JiraCredentialsClient("https://conhos.atlassian.net")
    );

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
