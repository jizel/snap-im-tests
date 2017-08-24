package travel.snapshot.dp.qa.cucumber.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.SNAPSHOT;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.*;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.*;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.qa.cucumber.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;
import travel.snapshot.dp.qa.junit.helpers.CommonHelpers;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class DbStepDefs {

    @Steps
    private DbUtilsSteps dbSteps = new DbUtilsSteps();
    @Steps
    private CustomerSteps customerSteps;
    @Steps
    private PropertySteps propertySteps;

    protected static final CommonHelpers commonHelpers = new CommonHelpers();


    //    @Steps annotation does not work with JUnit
    private ApplicationsSteps applicationsSteps = new ApplicationsSteps();

    @Given("^All customerProperties are deleted from DB for customer id \"([^\"]*)\" and property code \"([^\"]*)\"$")
    public void All_customerProperties_are_deleted_from_DB_for_customer_code_and_property_code(UUID customerId, String propertyCode) throws Throwable {
        CustomerDto c = customerSteps.getCustomerById(customerId);
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        if (c != null && p != null) {
            dbSteps.deleteAllPropertyCustomersFromDb(c.getId(), p.getId());
        }

    }

    @Given("^Database is cleaned and default entities are created$")
    public void databaseIsCleanedAndEntitiesAreCreated() {
        dbSteps.cleanDatabase();
        defaultEntitiesAreCreated();
    }

    @Given("^Default entities are created$")
    public void defaultEntitiesAreCreated() {
        defaultSnapshotUserIsCreated();
        defaultPartnerIsCreated();
        defaultSnapshotApplicationIsCreated();
        defaultSnapshotApplicationVersionIsCreated();
        defaultCustomerIsCreated();
        defaultPropertyIsCreated();
        defaultCommercialSubscriptionIsCreated();
        applicationPermissionPopulated(DEFAULT_SNAPSHOT_APPLICATION_ID.toString());
    }

    @Given("^Default entities are deleted$")
    public void defaultEntitiesAreDeleted() {
        dbSteps.deleteAppVersion(DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        dbSteps.revokeAppPermissions(DEFAULT_SNAPSHOT_APPLICATION_ID);
        dbSteps.deleteCommercialSubscription(DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        dbSteps.deleteProperty(DEFAULT_PROPERTY_ID);
        dbSteps.deleteCustomer(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        dbSteps.deleteApplication(DEFAULT_SNAPSHOT_APPLICATION_ID);
        dbSteps.deletePartner(DEFAULT_SNAPSHOT_PARTNER_ID);
        dbSteps.deleteDbUser(DEFAULT_SNAPSHOT_USER_ID);
        dbSteps.deleteDbAddress(DEFAULT_ADDRESS_ID);

    }

    @Given("^Database is cleaned$")
    public void databaseIsCleaned() throws Throwable {
        dbSteps.cleanDatabase();
    }

    @Given("^Default Snapshot user is created$")
    public void defaultSnapshotUserIsCreated() {
        UserCreateDto defaultSnapshotUser = new UserCreateDto();
        defaultSnapshotUser.setId(DEFAULT_SNAPSHOT_USER_ID);
        defaultSnapshotUser.setType(SNAPSHOT);
        defaultSnapshotUser.setUsername(DEFAULT_SNAPSHOT_USER_NAME);
        defaultSnapshotUser.setFirstName("Default");
        defaultSnapshotUser.setLastName("SnapshotUser");
        defaultSnapshotUser.setEmail("defaultSnapshotUser1@snapshot.travel");
        defaultSnapshotUser.setTimezone("Europe/Prague");
        defaultSnapshotUser.setLanguageCode("cs-CZ");
        defaultSnapshotUser.setIsActive(true);

        dbSteps.createDBUser(defaultSnapshotUser);
    }

    @Given("^Default partner is created$")
    public void defaultPartnerIsCreated() {
        PartnerDto defaultPartner = new PartnerDto();
        defaultPartner.setId(DEFAULT_SNAPSHOT_PARTNER_ID);
        defaultPartner.setName("Somepartner");
        defaultPartner.setWebsite(SNAPSHOT_WEBSITE);
        defaultPartner.setIsActive(true);
        defaultPartner.setEmail("somemail@snapshot.travel");
        defaultPartner.setVatId(DEFAULT_SNAPSHOT_PARTNER_VAT_ID);
        dbSteps.createDBPartner(defaultPartner);
    }

    @Given("^Default application is created$")
    public void defaultSnapshotApplicationIsCreated() {
        ApplicationDto defaultApp = new ApplicationDto();
        defaultApp.setName("Default Snapshot Test App");
        defaultApp.setDescription("Default Snapshot Test App created in test background");
        defaultApp.setPartnerId(DEFAULT_SNAPSHOT_PARTNER_ID);
        defaultApp.setIsInternal(true);
        defaultApp.setId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        defaultApp.setWebsite(SNAPSHOT_WEBSITE);
        dbSteps.createDBApplication(defaultApp);
    }

    @Given("^Default application version is created$")
    public void defaultSnapshotApplicationVersionIsCreated() {
        ApplicationVersionDto defaultAppVersion = new ApplicationVersionDto();
        defaultAppVersion.setName("DefaultVersion");
        defaultAppVersion.setDescription("Default test app version");
        defaultAppVersion.setIsActive(true);
        defaultAppVersion.setApplicationId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        defaultAppVersion.setId(DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID);
        defaultAppVersion.setApiManagerId("123");
        defaultAppVersion.setStatus(CERTIFIED);
        defaultAppVersion.setIsNonCommercial(true);
        dbSteps.createDBApplicationVersion(defaultAppVersion);
    }

    @Given("^Default customer is created$")
    public void defaultCustomerIsCreated() {
        CustomerCreateDto customer = new CustomerCreateDto();
        customer.setId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        customer.setName("DefaultCustomer");
        customer.setIsActive(true);
        customer.setTimezone(DEFAULT_SNAPSHOT_TIMEZONE);
        customer.setIsDemo(true);
        customer.setEmail("defaultCustomer@snapshot.travel");
        customer.setNotes("Default customer created directly in DB to set in default commercial subscription");
        customer.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
        customer.setPhone("+420123456789");
        customer.setVatId("DEF0000001");
        customer.setWebsite("https://www.defaultCustomerForTests.com");
        dbSteps.createDBCustomer(customer);
        dbSteps.populateCustomerHierarchyPath(customer.getId());
    }

    @Given("^Default property is created$")
    public void defaultPropertyIsCreated() {
        PropertyDto property = new PropertyDto();
        property.setId(DEFAULT_PROPERTY_ID);
        property.setEmail("defaultProperty@snapshot.travel");
        property.setCode("defaultPropertyCode");
        property.setTimezone(DEFAULT_SNAPSHOT_TIMEZONE);
        property.setIsActive(true);
        property.setWebsite("https://www.defaultPropertyForTests.com");
        property.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        property.setDescription("Default property for default commercial subscription");
        property.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
        property.setIsDemo(true);
        property.setName("Default Property Name");
        dbSteps.createDBProperty(property);
    }

    @Given("^Default commercial subscription is created$")
    public void defaultCommercialSubscriptionIsCreated() {
        CommercialSubscriptionDto commercialSubscription = new CommercialSubscriptionDto();
        commercialSubscription.setIsActive(true);
        commercialSubscription.setId(DEFAULT_COMMERCIAL_SUBSCRIPTION_ID);
        commercialSubscription.setApplicationId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        commercialSubscription.setCustomerId(DEFAULT_SNAPSHOT_CUSTOMER_ID);
        commercialSubscription.setPropertyId(DEFAULT_PROPERTY_ID);
        dbSteps.createDbCommercialSubscription(commercialSubscription);
    }

    @And("^Column \"([^\"]*)\" has value \"([^\"]*)\" in table \"([^\"]*)\" where column \"([^\"]*)\" has value \"([^\"]*)\"(?: in \"([^\"]*)\" schema)?$")
    public void columnHasValueInTableForColumnWithValue(String columnName, String columnValue, String tableName, String conditionColumnName, String conditionColumnValue, String schema) throws Throwable {
        Map<String, Object> selectResult = dbSteps.selectColumnFromTableWhere(columnName, tableName, conditionColumnName, conditionColumnValue, schema).get(0);
        assertThat(selectResult.get(columnName).toString(), is(columnValue));

    }

    @And("^Application permission table is populated(?: for application \"([^\"]*)\")?$")
    public void applicationPermissionPopulated(String applicationName) {
        UUID applicationId = applicationsSteps.resolveApplicationId(applicationName);
        dbSteps.populateApplicationPermissionsTableForApplication(applicationId);
    }

    public void removeCreatedEntities(Map<String, ArrayList<UUID>> registry) {

        ArrayList<UUID> roleIds = commonHelpers.getArrayFromMap(ROLES_RESOURCE, registry);
        ArrayList<UUID> userGroupIds = commonHelpers.getArrayFromMap(USER_GROUPS_RESOURCE, registry);
        ArrayList<UUID> userPropertySetIds = commonHelpers.getArrayFromMap(USER_PROPERTYSETS, registry);
        ArrayList<UUID> userPropertyIds = commonHelpers.getArrayFromMap(USER_PROPERTIES, registry);
        ArrayList<UUID> propertySetPropertyIds = commonHelpers.getArrayFromMap(PROPERTYSET_PROPERTIES, registry);
        ArrayList<UUID> propertyIds = commonHelpers.getArrayFromMap(PROPERTIES_RESOURCE, registry);
        ArrayList<UUID> propertySetIds = commonHelpers.getArrayFromMap(PROPERTY_SETS_RESOURCE, registry);
        ArrayList<UUID> customerPropertyIds = commonHelpers.getArrayFromMap(CUSTOMER_PROPERTIES, registry);
        ArrayList<UUID> customerUserIds = commonHelpers.getArrayFromMap(CUSTOMER_USERS, registry);
        ArrayList<UUID> customerIds = commonHelpers.getArrayFromMap(CUSTOMERS_RESOURCE, registry);
        ArrayList<UUID> userIds = commonHelpers.getArrayFromMap(USERS_RESOURCE, registry);
        roleIds.forEach(roleId -> {
            dbSteps.deleteRole(roleId);
        });
        userGroupIds.forEach(userGroupId -> {
            dbSteps.deleteUserPropertySet(userGroupId);
        });
        userPropertySetIds.forEach(userPropertySetId -> {
            dbSteps.deleteUserPropertySet(userPropertySetId);
        });
        userPropertyIds.forEach(userPropertyId -> {
            dbSteps.deleteUserProperty(userPropertyId);
        });
        propertySetPropertyIds.forEach(propertySetPropertyId -> {
            dbSteps.deletePropertySetProperty(propertySetPropertyId);
        });
        propertySetIds.forEach(propertySetId -> {
            dbSteps.deletePropertySet(propertySetId);
        });
        propertyIds.forEach(propertyId -> {
            dbSteps.deleteProperty(propertyId);
        });
        customerPropertyIds.forEach(customerPropertyId -> {
            dbSteps.deleteCustomerProperty(customerPropertyId);
        });
        customerUserIds.forEach(customerUserId -> {
            dbSteps.deleteCustomerUser(customerUserId);
        });
        customerIds.forEach(customerId -> {
            dbSteps.deleteCustomer(customerId);
        });
        userIds.forEach(userId -> {
            dbSteps.deleteUser(userId);
        });
        dbSteps.deleteAddress();
    }
}
