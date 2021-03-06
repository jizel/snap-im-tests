package travel.snapshot.dp.qa.junit.tests.common;

import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_UNPROCESSABLE_ENTITY;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.RESPONSE_CODE;

import com.jayway.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import qa.tools.ikeeper.client.JiraClient;
import qa.tools.ikeeper.test.IKeeperJUnitConnector;
import travel.snapshot.dp.api.identity.model.ApplicationCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleCreateDto;
import travel.snapshot.dp.api.identity.model.PartnerCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetCreateDto;
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupCreateDto;
import travel.snapshot.dp.qa.junit.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.junit.helpers.BasicSteps;
import travel.snapshot.dp.qa.junit.helpers.DbHelpers;
import travel.snapshot.dp.qa.junit.helpers.ConfigurationHelpers;
import travel.snapshot.dp.qa.junit.helpers.JmsHelpers;
import travel.snapshot.dp.qa.junit.helpers.AuthorizationHelpers;
import travel.snapshot.dp.qa.junit.helpers.CommercialSubscriptionHelpers;
import travel.snapshot.dp.qa.junit.helpers.CommonHelpers;
import travel.snapshot.dp.qa.junit.helpers.CustomerHelpers;
import travel.snapshot.dp.qa.junit.helpers.KeycloakHelpers;
import travel.snapshot.dp.qa.junit.helpers.PlatformOperationHelpers;
import travel.snapshot.dp.qa.junit.helpers.RelationshipsHelpers;
import travel.snapshot.dp.qa.junit.helpers.RoleHelpers;
import travel.snapshot.dp.qa.junit.helpers.UserHelpers;
import travel.snapshot.dp.qa.junit.loaders.EntitiesLoader;
import travel.snapshot.dp.qa.junit.utils.NonNullMapDecorator;

import java.time.LocalDate;
import java.util.Collections;
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

    protected static EntitiesLoader entitiesLoader;

    //    Steps
    protected DbHelpers dbHelpers = new DbHelpers();
    protected final JmsHelpers jmsHelpers = new JmsHelpers();
    protected final DbHelpers dbSteps = new DbHelpers();

    private final BasicSteps basicSteps = new BasicSteps();

    //    Helpers
    protected final CustomerHelpers customerHelpers = new CustomerHelpers();
    protected final UserHelpers userHelpers = new UserHelpers();
    protected final RoleHelpers roleHelpers = new RoleHelpers();
    protected final RelationshipsHelpers relationshipsHelpers = new RelationshipsHelpers();
    protected final CommercialSubscriptionHelpers commercialSubscriptionHelpers = new CommercialSubscriptionHelpers();
    protected final ConfigurationHelpers configurationHelpers = new ConfigurationHelpers();
    protected final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();
    protected final KeycloakHelpers keycloakHelpers = new KeycloakHelpers();
    protected final CommonHelpers commonHelpers = new CommonHelpers();
    protected final PlatformOperationHelpers platformOperationHelpers = new PlatformOperationHelpers();
    protected static final PropertiesHelper propertiesHelper = new PropertiesHelper();

    //    Custom codes
    public static final int CC_INSUFFICIENT_PERMISSIONS = 40301;
    public static final int CC_ENDPOINT_NOT_FOUND = 40401;
    public static final int CC_ENTITY_NOT_FOUND = 40402;
    public static final int CC_CONFLICT_CODE = 40901;
    public static final int CC_CONFLICT_ID = 40902;
    public static final int CC_CONFLICT_VALUES = 40907;
    public static final int CC_CIRCULAR_DEPENDENCY = 40911;
    public static final int CC_ENTITY_REFERENCED = 40915;
    public static final int CC_BAD_PARAMS = 40002;
    public static final int CC_MISSING_PARAMS = 40003;
    public static final int CC_SEMANTIC_ERRORS = 42201;
    public static final int CC_NON_EXISTING_REFERENCE = 42202;
    public static final int CC_NO_MATCHING_ENTITY = 42203;
    public static final int CC_INVALID_ETAG = 41202;
    public static final int CC_MISSING_ETAG = 41201;

    //    Basic test entities
    protected static NonNullMapDecorator<String, CustomerCreateDto> customerDtos;
    protected static NonNullMapDecorator<String, UserCreateDto> userDtos;
    protected static NonNullMapDecorator<String, PropertyCreateDto> propertyDtos;
    protected static NonNullMapDecorator<String, PropertySetCreateDto> propertySetDtos;
    protected static NonNullMapDecorator<String, PartnerCreateDto> partnerDtos;
    protected static NonNullMapDecorator<String, ApplicationCreateDto> applicationDtos;
    protected static NonNullMapDecorator<String, ApplicationVersionCreateDto> applicationVersionDtos;
    protected static NonNullMapDecorator<String, RoleCreateDto> roleDtos;
    protected static UserCreateDto testUser1;
    protected static UserCreateDto testUser2;
    protected static UserCreateDto testUser3;
    protected static UserCreateDto testSnapshotUser1;
    protected static UserCreateDto testPartnerUser1;
    protected static CustomerCreateDto testCustomer1;
    protected static CustomerCreateDto testCustomer2;
    protected static CustomerCreateDto testCustomer3;
    protected static CustomerCreateDto testCustomer4;
    protected static PropertyCreateDto testProperty1;
    protected static PropertyCreateDto testProperty2;
    protected static PropertyCreateDto testProperty3;
    protected static PropertySetCreateDto testPropertySet1;
    protected static PropertySetCreateDto testPropertySet2;
    protected static PropertySetCreateDto testPropertySet3;
    protected static RoleCreateDto testRole1;
    protected static RoleCreateDto testRole2;
    protected static RoleCreateDto testRole3;
    protected static CustomerRoleCreateDto testCustomerRole1;
    protected static PropertyRoleCreateDto testPropertyRole1;
    protected static PartnerCreateDto testPartner1;
    protected static PartnerCreateDto testPartner2;
    protected static PartnerCreateDto testPartner3;
    protected static UserGroupCreateDto testUserGroup1;
    protected static UserGroupCreateDto testUserGroup2;
    protected static UserGroupCreateDto testUserGroup3;
    protected static ApplicationCreateDto testApplication1;
    protected static ApplicationCreateDto testApplication2;
    protected static ApplicationCreateDto testApplication3;
    protected static ApplicationCreateDto testApplication4;
    protected static ApplicationVersionCreateDto testAppVersion1;
    protected static ApplicationVersionCreateDto testAppVersion2;
    protected static ApplicationVersionCreateDto testAppVersion3;
    public static LocalDate validFrom = LocalDate.now();
    public static LocalDate validTo = LocalDate.now().plusYears(1).plusMonths(2).plusDays(3);
    public static String NOTIFICATION_CRUD_TOPIC = null;

    // shared variables
    public static final String YAML_DATA_PATH = "src/test/resources/yaml/%s";
    public static final String JMS_SUBSCRIPTION_NAME = "Test";
    public static final String TOTAL_COUNT_HEADER = "X-Total-Count";

    @Rule
    public IKeeperJUnitConnector issueKeeper = new IKeeperJUnitConnector(
            getClient()
    );

    private JiraClient getClient(){
        String jiraUsername = propertiesHelper.getProperty("jira.username");
        String jiraPassword = propertiesHelper.getProperty("jira.password");
        JiraClient jiraClient  = new JiraClient("https://conhos.atlassian.net");
        jiraClient.authenticate(jiraUsername,jiraPassword);

        return jiraClient;
    }

    @Before
    @BeforeEach
    public void setUp() {
        dbHelpers.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
    }

    @After
    public void cleanUp() throws Throwable {
    }


    /**
     * Loading default entities before each test class so any changes made in class do not interfere with other test classes.
     *
     * Execution time of this method is roughly 300ms on standard SnapShot laptop
     */
    protected static void loadDefaultTestEntities() {
        //   Get EntitiesLoader instance containing all test entity data
        entitiesLoader = EntitiesLoader.getInstance();

        customerDtos = entitiesLoader.getCustomerDtos();
        userDtos = entitiesLoader.getUserDtos();
        propertyDtos = entitiesLoader.getPropertyDtos();
        propertySetDtos = entitiesLoader.getPropertySetDtos();
        partnerDtos = entitiesLoader.getPartnerDtos();
        applicationDtos =  entitiesLoader.getApplicationDtos();
        applicationVersionDtos =  entitiesLoader.getApplicationVersionDtos();
        roleDtos =  entitiesLoader.getRoleDtos();

        testUser1 = userDtos.get("user1");
        testUser2 = userDtos.get("user2");
        testUser3 = userDtos.get("user3");
        testSnapshotUser1 = entitiesLoader.getSnapshotUserDtos().get("snapshotUser1");
        testPartnerUser1 = entitiesLoader.getPartnerUserDtos().get("partnerUser1");
        testCustomer1 = customerDtos.get("customer1");
        testCustomer2 = customerDtos.get("customer2");
        testCustomer3 = customerDtos.get("customer3");
        testCustomer4 = customerDtos.get("customer4");
        testProperty1 = propertyDtos.get("property1");
        testProperty2 = propertyDtos.get("property2");
        testProperty3 = propertyDtos.get("property3");
        testPropertySet1 = propertySetDtos.get("propertySet1");
        testPropertySet2 = propertySetDtos.get("propertySet2");
        testPropertySet3 = propertySetDtos.get("propertySet3");
        testRole1 = roleDtos.get("role1");
        testRole2 = roleDtos.get("role2");
        testRole3 = roleDtos.get("role3");
        testCustomerRole1 = entitiesLoader.getCustomerRoleDtos().get("customerRole1");
        testPropertyRole1 = entitiesLoader.getPropertyRoleDtos().get("propertyRole1");
        testPartner1 = partnerDtos.get("partner1");
        testPartner2 = partnerDtos.get("partner2");
        testPartner3 = partnerDtos.get("partner3");
        testUserGroup1 = entitiesLoader.getUserGroupDtos().get("user_group1");
        testUserGroup2 = entitiesLoader.getUserGroupDtos().get("user_group2");
        testUserGroup3 = entitiesLoader.getUserGroupDtos().get("user_group3");
        testApplication1 = applicationDtos.get("application1");
        testApplication2 = applicationDtos.get("application2");
        testApplication3 = applicationDtos.get("application3");
        testApplication4 = applicationDtos.get("application4");
        testAppVersion1 = applicationVersionDtos.get("app_version1");
        testAppVersion2 = applicationVersionDtos.get("app_version2");
        testAppVersion3 = applicationVersionDtos.get("app_version3");
        NOTIFICATION_CRUD_TOPIC = propertiesHelper.getProperty("notificationCrud.topic");
    }


    //    Help methods

    protected void verifyResponseAndCustomCode(String responseCode, String customCode) {
        responseCodeIs(Integer.valueOf(responseCode));
        customCodeIs(Integer.valueOf(customCode));
    }

    protected void verifyResponseAndCustomCode(Integer responseCode, Integer customCode) {
        responseCodeIs(responseCode);
        customCodeIs(customCode);
    }

    public static String transformNull(Object value) {
        String stringVal = String.valueOf(value);
        if ("/null".equals(stringVal)) {
            return null;
        }
        return stringVal;
    }

    protected static void responseIsEndpointNotFound() {
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(CC_ENDPOINT_NOT_FOUND);
    }

    protected static void responseIsEntityNotFound() {
        responseCodeIs(SC_NOT_FOUND);
        customCodeIs(CC_ENTITY_NOT_FOUND);
    }

    protected static void responseIsUnprocessableEntity() {
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_SEMANTIC_ERRORS);
    }

    protected static void responseIsReferenceDoesNotExist() {
        responseCodeIs(SC_UNPROCESSABLE_ENTITY);
        customCodeIs(CC_NON_EXISTING_REFERENCE);
    }

    protected static void responseIsConflictId() {
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CONFLICT_ID);
    }

    protected static void responseIsConflictField() {
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CONFLICT_CODE);
    }

    protected static void responseIsConflictValues() {
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_CONFLICT_VALUES);
    }

    protected static void responseIsEntityReferenced() {
        responseCodeIs(SC_CONFLICT);
        customCodeIs(CC_ENTITY_REFERENCED);
    }

    public static Map<String, String> filterParam(String filterValue){
        return Collections.singletonMap("filter", filterValue);
    }

    public static void cleanDbAndLoadDefaultEntities(){
        DbHelpers dbHelpers = new DbHelpers();
        dbHelpers.databaseIsCleanedAndEntitiesAreCreated();
        loadDefaultTestEntities();
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

    protected String getAttributeValue(String attributeName) {
        return basicSteps.getAttributeValue(attributeName);
    }

    protected static void assertStatusCodes(Response response, Integer returnCode, Integer customCode) {
        response.then()
                .statusCode(returnCode)
                .assertThat()
                .body(RESPONSE_CODE, is(customCode));
    }

}
