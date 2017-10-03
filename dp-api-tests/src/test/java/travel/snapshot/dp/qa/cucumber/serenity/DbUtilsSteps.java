package travel.snapshot.dp.qa.cucumber.serenity;

import static java.util.UUID.randomUUID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.ADDRESS_LINE1_PATTERN;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_ADDRESS_ID;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_CUSTOMER_TYPE;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_ENCRYPTED_PASSWORD;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_ETAG;

import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.cucumber.helpers.DbHelper;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class that manages direct DB access operations.
 *
 * Should be renamed to something more meaningful once All Cucumber relicts are gone.
 */
public class DbUtilsSteps {

    static final String DELETE_SINGLE_USER = "delete from public.User where id = '%s';";
    static final String DELETE_SINGLE_PROPERTY = "delete from Property where id = '%s';";
    static final String DELETE_SINGLE_PROPERTYSET = "delete from Propertyset where id = '%s';";
    static final String DELETE_SINGLE_PROPERTYSET_PROPERTY = "delete from Propertyset_Property where id = '%s';";
    static final String DELETE_SINGLE_PROPERTYSET_HIERARCHY = "delete from PropertysetHierarchyPath where parent_id = '%s';";
    static final String DELETE_SINGLE_CUSTOMER = "delete from Customer where id = '%s';";
    static final String DELETE_SINGLE_COMMERCIAL_SUBSCRIPTION = "delete from CommercialSubscription where id = '%s';";
    static final String REVOKE_APP_PERMISSIONS = "delete from applicationpermission where application_id = '%s';";
    static final String DELETE_SINGLE_APPLICATION = "delete from Application where id = '%s';";
    static final String DELETE_SINGLE_APPLICATION_VERSION = "delete from ApplicationVersion where id = '%s';";
    static final String DELETE_SINGLE_PARTNER = "delete from Partner where id = '%s';";
    static final String DELETE_SINGLE_CUSTOMER_HIERARCHY = "delete from customerhierarchypath where parent_id = '%s';";
    static final String DELETE_SINGLE_ADDRESS = "delete from Address where line1 = '%s';";
    static final String DELETE_DB_ADDRESS = "delete from Address where id = '%s';";
    static final String DELETE_SINGLE_CUSTOMER_PROPERTY = "delete from customer_property where id = '%s';";
    static final String DELETE_SINGLE_USER_CUSTOMER = "delete from user_customer where id = '%s';";
    static final String DELETE_SINGLE_USER_PROPERTY = "delete from user_property where id = '%s';";
    static final String DELETE_SINGLE_USER_PROPERTYSET = "delete from user_propertyset where id = '%s';";
    static final String DELETE_SINGLE_ROLE = "delete from Role where id = '%s';";
    static final String DELETE_SINGLE_ROLE_ASSIGNMENT = "delete from roleassignment where id = '%s';";
    static final String DELETE_SINGLE_ROLE_PERMISSION = "delete from rolepermission where id = '%s';";


    static final String DELETE_CUSTOMER_PROPERTY = "delete  from Customer_Property";
    static final String DELETE_CUSTOMER_USER = "delete  from User_Customer";
    static final String DELETE_USER_PROPERTY = "delete  from User_Property";
    static final String DELETE_USER_PROPERTYSET = "delete  from User_PropertySet";
    static final String DELETE_PROPERTY_PROPERTYSET = "delete  from PropertySet_Property";
    static final String DELETE_PROPERTY = "delete  from Property";
    static final String UPDATE_PROPERTY_SET = "update PropertySet set parent_id = null where parent_id is not null";
    static final String DELETE_PROPERTY_SET = "delete  from PropertySet";
    static final String UPDATE_CUSTOMER = "update Customer set parent_id = null where parent_id is not null";
    static final String DELETE_CUSTOMER = "delete  from Customer";
    static final String DELETE_USER = "delete from public.user";
    static final String DELETE_ADDRESS = "delete  from Address";
    static final String DELETE_ROLE = "delete  from Role";
    static final String DELETE_USER_PROPERTY_ROLE = "delete from User_Property_Role";
    static final String DELETE_APPLICATION = "delete from Application";
    static final String DELETE_APPLICATION_VERSIONS = "delete from ApplicationVersion";
    static final String DELETE_USER_GROUPS = "delete from UserGroup";
    static final String DELETE_USER_GROUPS_PROPERTIES = "delete from UserGroup_Property";
    static final String DELETE_USER_GROUPS_PROPERTYSET = "delete from UserGroup_PropertySet";
    static final String DELETE_USER_GROUPS_USER = "delete from UserGroup_User";
    static final String DELETE_CUSTOMER_PROPERTY_BY_CUSTOMER_ID_PROPERTY_ID = "delete from Customer_Property where customer_id = ? and property_id = ?";
    static final String DELETE_COMMERCIAL_SUBSCRIPTION = "delete from CommercialSubscription";
    static final String DELETE_CUSTOMER_HIERARCHY_PATH = "delete from CustomerHierarchyPath";
    static final String DELETE_PROPERTY_SET_HIERARCHY_PATH = "delete from PropertySetHierarchyPath";
    static final String DELETE_USER_PROPERTY_BY_USER_ID_PROPERTY_ID = "delete  from User_Property where user_id = ? and property_id = ?";
    static final String DELETE_TTI_CROSSREFERENCES = "delete  from crossreferences";
    static final String DELETE_PARTNER = "delete from Partner";
    static final String DELETE_PARTNER_USER = "delete from User_Partner";
    static final String DELETE_ROLE_PERMISSION = "delete from Rolepermission";
    static final String DELETE_ROLE_ASSIGNMENT = "delete from Roleassignment";
    static final String CREATE_DB_USER = "INSERT INTO public.user (id, type, username, password, first_name, last_name, email, timezone, language_code, is_active, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_PARTNER = "INSERT INTO Partner (id, name, email, notes, website, vat_id, is_active, version) VALUES (?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_APPLICATION = "INSERT INTO Application (id, name, description, website, partner_id, is_internal, is_active, version) VALUES (?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_APPLICATION_VERSION = "INSERT INTO ApplicationVersion (id, application_id, api_manager_id, name, status, release_date, description, is_active, is_non_commercial, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_CUSTOMER = "INSERT INTO Customer (id, is_active, salesforce_id, name, phone, email, website, vat_id, is_demo, notes, address_id, timezone, type, code, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_CUSTOMER_TYPE + "', 'defaultCode1', '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_ADDRESS = "INSERT INTO Address (id, line1, line2, city, zip_code, country_code) VALUES (?, ?, ?, ?, ?, ?);";
    static final String CREATE_DB_PROPERTY = "INSERT INTO Property (id, is_active, salesforce_id, name, email, website, is_demo, address_id, timezone, code, description, customer_id, version, tti_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "', '123');";
    static final String CREATE_DB_COMMERCIAL_SUBSCRIPTION = "INSERT INTO CommercialSubscription (id, customer_id, property_id, application_id, is_active,  version) VALUES (?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_CUSTOMER_HIERARCHY_PATH = "INSERT INTO CustomerHierarchyPath (parent_id, child_id) values (?, ?) ;";
    static final String DELETE_APPLICATION_PERMISSION = "DELETE FROM ApplicationPermission;";
    static final String POPULATE_APPLICATION_PERMISSION = "INSERT INTO ApplicationPermission (id, application_id, platform_operation_id) SELECT uuid_generate_v4(), ?, id FROM platformoperation;";
    static final String ADD_APPLICATION_PERMISSION = "INSERT INTO ApplicationPermission (id, application_id, platform_operation_id) VALUES (?, ?, ?);";
    static final String SELECT_PERMISSION_ID = "SELECT id FROM platformoperation where uri_template = ? AND http_method = ?;";
    static final String SET_USER_PASSWORD = "UPDATE public.user SET password = ? where id = ?;";

    static final String TTI_SCHEMA_NAME = "tti";
    static final String IDENTITY_SCHEMA_NAME = "identity";

    private DbHelper dbHelper = new DbHelper();

    public void setUserPassword(UUID userId, String passwordHash) {
        dbHelper.identityDb().update(SET_USER_PASSWORD, passwordHash, userId);
    }

    public void deleteAllPropertyCustomersFromDb(UUID customerId, UUID propertyId) {
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY_BY_CUSTOMER_ID_PROPERTY_ID, customerId, propertyId);
    }

    public void deletePropertyUserFromDb(UUID userId, UUID propertyId) {
        dbHelper.identityDb().update(DELETE_USER_PROPERTY_BY_USER_ID_PROPERTY_ID, userId, propertyId);
    }

    public void createDBUser(UserCreateDto user) {
        dbHelper.identityDb().update(CREATE_DB_USER, (user.getId()), user.getType().toString(), user.getUsername(), DEFAULT_ENCRYPTED_PASSWORD, user.getFirstName(), user.getLastName(), user.getEmail(), user.getTimezone(), user.getLanguageCode(), user.getIsActive());
    }

    public void deleteDbUser(UUID userId) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_USER, userId));
    }

    public void deleteDbAddress(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_DB_ADDRESS, id));
    }

    public void revokeAppPermissions(UUID applicationId) {
        dbHelper.identityDb().update(String.format(REVOKE_APP_PERMISSIONS, applicationId));
    }

    public void deleteAppVersion(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_APPLICATION_VERSION, id));
    }

    public void deleteCommercialSubscription(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_COMMERCIAL_SUBSCRIPTION, id));
    }

    public void deleteProperty(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_PROPERTY, id));
    }

    public void deletePropertySet(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_PROPERTYSET_HIERARCHY, id));
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_PROPERTYSET, id));
    }

    public void deleteCustomer(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_CUSTOMER_HIERARCHY, id));
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_CUSTOMER, id));
    }

    public void deletePropertySetProperty(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_PROPERTYSET_PROPERTY, id));
    }

    public void deleteUserProperty(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_USER_PROPERTY, id));
    }

    public void deleteUserPropertySet(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_USER_PROPERTYSET, id));
    }

    public void deleteUser(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_USER, id));
    }

    public void deleteRole(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_ROLE, id));
    }

    public void deleteRoleAssignment(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_ROLE_ASSIGNMENT, id));
    }

    public void deleteRolePermission(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_ROLE_PERMISSION, id));
    }

    public void deleteAddress() {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_ADDRESS, ADDRESS_LINE1_PATTERN));
    }

    public void deleteApplication(UUID id) {
        dbHelper.identityDb().update(String.format(REVOKE_APP_PERMISSIONS, id));
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_APPLICATION, id));
    }

    public void deletePartner(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_PARTNER, id));
    }

    public void createDBPartner(PartnerDto partner) {
        dbHelper.identityDb().update(CREATE_DB_PARTNER, (partner.getId()), partner.getName(), partner.getEmail(), partner.getNotes(), partner.getWebsite(), partner.getVatId(), partner.getIsActive());
    }

    public void createDBApplication(ApplicationDto application) {
        dbHelper.identityDb().update(CREATE_DB_APPLICATION, (application.getId()), application.getName(), application.getDescription(), application.getWebsite(), (application.getPartnerId()), application.getIsInternal(), application.getIsActive());
    }

    public void createDBApplicationVersion(ApplicationVersionDto applicationVersion) {
        dbHelper.identityDb().update(CREATE_DB_APPLICATION_VERSION, (applicationVersion.getId()), (applicationVersion.getApplicationId()), applicationVersion.getApiManagerId(), applicationVersion.getName(), applicationVersion.getStatus().toString(), applicationVersion.getReleaseDate(), applicationVersion.getDescription(), applicationVersion.getIsActive(), applicationVersion.getIsNonCommercial());
    }

    public void createDBCustomer(CustomerCreateDto customer) {
        dbHelper.identityDb().update(CREATE_DB_ADDRESS, (DEFAULT_ADDRESS_ID), "address line 1", "address line 2", "city", "12345", "CZ");
        dbHelper.identityDb().update(CREATE_DB_CUSTOMER, (customer.getId()), customer.getIsActive(), customer.getSalesforceId().toString(), customer.getName(), customer.getPhone(), customer.getEmail(), customer.getWebsite(), customer.getVatId(), customer.getIsDemo(), customer.getNotes(), (DEFAULT_ADDRESS_ID), customer.getTimezone());
    }

    public void populateCustomerHierarchyPath(UUID customerId) {
        dbHelper.identityDb().update(CREATE_CUSTOMER_HIERARCHY_PATH, customerId, customerId);
    }

    public void createDBProperty(PropertyDto property) {
        dbHelper.identityDb().update(CREATE_DB_PROPERTY, (property.getId()), property.getIsActive(), property.getSalesforceId().toString(), property.getName(), property.getEmail(), property.getWebsite(), property.getIsDemo(), (DEFAULT_ADDRESS_ID), property.getTimezone(), property.getCode(), property.getDescription(), (property.getCustomerId()));
    }

    public void createDbCommercialSubscription(CommercialSubscriptionDto commercialSubscription) {
        dbHelper.identityDb().update(CREATE_DB_COMMERCIAL_SUBSCRIPTION, (commercialSubscription.getId()), (commercialSubscription.getCustomerId()), (commercialSubscription.getPropertyId()), (commercialSubscription.getApplicationId()), commercialSubscription.getIsActive());
    }

    public List<Map<String, Object>> selectColumnFromTableWhere(String column, String tableName, String conditionColumn, String conditionValue, String schemaName) {
        String sqlSelect = "SELECT " + column + " FROM " + tableName + " WHERE " + conditionColumn + " = ?";
//        Only tti and identity schemes are used in tests now. New schemas constants can be added if needed. Identity should always be default.
        if (schemaName != null && schemaName.equals(TTI_SCHEMA_NAME)) {
            return dbHelper.ttiDb().queryForList(sqlSelect, conditionValue);
        } else {
            return dbHelper.identityDb().queryForList(sqlSelect, conditionValue);
        }
    }

    public void cleanDatabase() {
        dbHelper.identityDb().update(DELETE_ROLE_PERMISSION);
        dbHelper.identityDb().update(DELETE_ROLE_ASSIGNMENT);
        dbHelper.identityDb().update(DELETE_APPLICATION_PERMISSION);
        dbHelper.identityDb().update(DELETE_CUSTOMER_HIERARCHY_PATH);
        dbHelper.identityDb().update(DELETE_PROPERTY_SET_HIERARCHY_PATH);
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY);
        dbHelper.identityDb().update(DELETE_CUSTOMER_USER);
        dbHelper.identityDb().update(DELETE_PARTNER_USER);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY_ROLE);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY);
        dbHelper.identityDb().update(DELETE_USER_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_COMMERCIAL_SUBSCRIPTION);
        dbHelper.identityDb().update(DELETE_PROPERTY_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTIES);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_USER);
        dbHelper.identityDb().update(DELETE_PROPERTY);
        dbHelper.identityDb().update(UPDATE_PROPERTY_SET);
        dbHelper.identityDb().update(DELETE_PROPERTY_SET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS);
        dbHelper.identityDb().update(DELETE_PROPERTY);
        dbHelper.identityDb().update(UPDATE_CUSTOMER);
        dbHelper.identityDb().update(DELETE_CUSTOMER);
        dbHelper.identityDb().update(DELETE_USER);
        dbHelper.identityDb().update(DELETE_ADDRESS);
        dbHelper.identityDb().update(DELETE_ROLE);
        dbHelper.identityDb().update(DELETE_APPLICATION_VERSIONS);
        dbHelper.identityDb().update(DELETE_APPLICATION);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY);
        dbHelper.identityDb().update(DELETE_PARTNER);
    }

    public void cleanTtiDatabase() {
        dbHelper.ttiDb().update(DELETE_TTI_CROSSREFERENCES);
    }


    public void populateApplicationPermissionsTableForApplication(UUID applicationId) {
        dbHelper.identityDb().update(POPULATE_APPLICATION_PERMISSION, applicationId);
    }

    public void addApplicationPermission(UUID applicationId, String endpoint, HttpMethod method) {
        UUID permissionId = dbHelper.identityDb().queryForObject(SELECT_PERMISSION_ID, UUID.class, endpoint, method.toString());
        dbHelper.identityDb().update(ADD_APPLICATION_PERMISSION, randomUUID(), applicationId, permissionId);
    }

    public void deleteCustomerProperty(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_CUSTOMER_PROPERTY, id));
    }

    public void deleteCustomerUser(UUID id) {
        dbHelper.identityDb().update(String.format(DELETE_SINGLE_USER_CUSTOMER, id));
    }
}
