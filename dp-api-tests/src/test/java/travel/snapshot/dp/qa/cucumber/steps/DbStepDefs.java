package travel.snapshot.dp.qa.cucumber.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static travel.snapshot.dp.api.identity.model.ApplicationVersionStatus.CERTIFIED;
import static travel.snapshot.dp.api.identity.model.UserUpdateDto.UserType.SNAPSHOT;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_COMMERCIAL_SUBSCRIPTION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_PROPERTY_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_CUSTOMER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_PARTNER_VAT_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_SALESFORCE_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_TIMEZONE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_USER_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.SNAPSHOT_WEBSITE;

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
import travel.snapshot.dp.qa.cucumber.serenity.customers.CustomerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.properties.PropertySteps;

import java.util.Map;
import java.util.UUID;

public class DbStepDefs {

    @Steps
    private DbUtilsSteps dbSteps = new DbUtilsSteps();
    @Steps
    private CustomerSteps customerSteps;
    @Steps
    private PropertySteps propertySteps;

    @Given("^All customerProperties are deleted from DB for customer id \"([^\"]*)\" and property code \"([^\"]*)\"$")
    public void All_customerProperties_are_deleted_from_DB_for_customer_code_and_property_code(String customerId, String propertyCode) throws Throwable {
        CustomerDto c = customerSteps.getCustomerById(customerId);
        PropertyDto p = propertySteps.getPropertyByCodeInternal(propertyCode);
        if (c != null && p != null) {
            dbSteps.deleteAllPropertyCustomersFromDb(c.getId(), p.getId());
        }

    }

    @Given("^Database is cleaned and default entities are created$")
    public void databaseIsCleanedAndEntitiesAreCreated() throws Throwable {
        dbSteps.cleanDatabase();
        defaultSnapshotUserIsCreated();
        defaultPartnerIsCreated();
        defaultSnapshotApplicationIsCreated();
        defaultSnapshotApplicationVersionIsCreated();
        defaultCustomerIsCreated();
        defaultPropertyIsCreated();
        defaultCommercialSubscriptionIsCreated();
        applicationPermissionPopulated();
    }

    @Given("^Database is cleaned$")
    public void databaseIsCleaned() throws Throwable {
        dbSteps.cleanDatabase();
    }

    @Given("^Default Snapshot user is created$")
    public void defaultSnapshotUserIsCreated() throws Throwable {
        UserCreateDto defaultSnapshotUser = new UserCreateDto();
        defaultSnapshotUser.setId(DEFAULT_SNAPSHOT_USER_ID);
        defaultSnapshotUser.setType(SNAPSHOT);
        defaultSnapshotUser.setUsername("defaultSnapshotUser");
        defaultSnapshotUser.setFirstName("Default");
        defaultSnapshotUser.setLastName("SnapshotUser");
        defaultSnapshotUser.setEmail("defaultSnapshotUser1@snapshot.travel");
        defaultSnapshotUser.setTimezone("Europe/Prague");
        defaultSnapshotUser.setLanguageCode("cs-CZ");
        defaultSnapshotUser.setIsActive(true);

        dbSteps.createDBUser(defaultSnapshotUser);
    }

    @Given("^Default partner is created$")
    public void defaultPartnerIsCreated() throws Throwable {
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
    public void defaultSnapshotApplicationIsCreated() throws Throwable {
        ApplicationDto defaultApp = new ApplicationDto();
        defaultApp.setName("Test");
        defaultApp.setDescription("Test");
        defaultApp.setPartnerId(DEFAULT_SNAPSHOT_PARTNER_ID);
        defaultApp.setIsInternal(true);
        defaultApp.setId(DEFAULT_SNAPSHOT_APPLICATION_ID);
        defaultApp.setWebsite(SNAPSHOT_WEBSITE);
        dbSteps.createDBApplication(defaultApp);
    }

    @Given("^Default application version is created$")
    public void defaultSnapshotApplicationVersionIsCreated() throws Throwable {
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
    public void defaultCustomerIsCreated() throws Throwable {
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
        dbSteps.populateCustomerHierarchyPath(UUID.fromString(customer.getId()));
    }

    @Given("^Default property is created$")
    public void defaultPropertyIsCreated() throws Throwable {
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
    public void defaultCommercialSubscriptionIsCreated() throws Throwable {
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
        Map<String,Object> selectResult = dbSteps.selectColumnFromTableWhere(columnName, tableName, conditionColumnName, conditionColumnValue, schema).get(0);
        assertThat(selectResult.get(columnName).toString(), is(columnValue));

    }

    @And("^Application permission table is populated$")
    public void applicationPermissionPopulated() throws Throwable {
        dbSteps.populateApplicationPermissionsTable();
    }
}
