package travel.snapshot.dp.qa.serenity;

import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_ADDRESS_ID;
import static travel.snapshot.dp.qa.serenity.BasicSteps.DEFAULT_SNAPSHOT_ETAG;

import travel.snapshot.dp.api.identity.model.ApiSubscriptionDto;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.DbHelper;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class DbUtilsSteps {

    static final String DELETE_CUSTOMER_PROPERTY = "delete  from Customer_Property";
    static final String DELETE_CUSTOMER_USER = "delete  from Customer_User";
    static final String DELETE_USER_PROPERTY = "delete  from User_Property";
    static final String DELETE_USER_PROPERTYSET = "delete  from User_PropertySet";
    static final String DELETE_PROPERTY_PROPERTYSET = "delete  from Property_PropertySet";
    static final String DELETE_PROPERTY = "delete  from Property";
    static final String UPDATE_PROPERTY_SET = "update Property_Set set parent_id = null where parent_id is not null";
    static final String DELETE_PROPERTY_SET = "delete  from Property_Set";
    static final String UPDATE_CUSTOMER = "update Customer set parent_id = null where parent_id is not null";
    static final String DELETE_CUSTOMER = "delete  from Customer";
    static final String DELETE_USER = "delete  from User";
    static final String DELETE_ADDRESS = "delete  from Address";
    static final String DELETE_ROLE = "delete  from Role";
    static final String DELETE_USER_GROUP_ROLE = "delete  from User_Group_Role";
    static final String DELETE_USER_PROPERTY_SET_ROLE = "delete from User_Property_Set_Role";
    static final String DELETE_USER_PROPERTY_ROLE = "delete from User_Property_Role";
    static final String DELETE_API_SUBSCRIPTIONS = "delete  from Api_Subscription";
    static final String DELETE_APPLICATION = "delete from Application";
    static final String DELETE_APPLICATION_VERSIONS = "delete from Application_Version";
    static final String DELETE_USER_GROUPS = "delete from User_Group";
    static final String DELETE_USER_GROUPS_PROPERTY_ROLES = "delete from User_Group_Property_Role";
    static final String DELETE_USER_GROUPS_PROPERTIES = "delete from User_Group_Property";
    static final String DELETE_USER_GROUPS_PROPERTYSET_ROLES = "delete from User_Group_Property_Set_Role";
    static final String DELETE_USER_GROUPS_PROPERTYSET = "delete from User_Group_Property_Set";
    static final String DELETE_USER_GROUPS_USER = "delete from User_Group_User";
    static final String DELETE_CUSTOMER_PROPERTY_BY_CUSTOMER_ID_PROPERTY_ID = "delete from Customer_Property where customer_id = ? and property_id = ?";
    static final String DELETE_COMMERCIAL_SUBSCRIPTION = "delete from Commercial_Subscription";
    static final String DELETE_CUSTOMER_HIERARCHY_PATH = "delete from Customer_Hierarchy_Path";
    static final String DELETE_PROPERTY_SET_HIERARCHY_PATH = "delete from PropertySet_Hierarchy_Path";
    static final String DELETE_USER_PROPERTY_BY_USER_ID_PROPERTY_ID = "delete  from User_Property where user_id = ? and property_id = ?";
    static final String DELETE_TTI_CROSSREFERENCES = "delete  from crossreferences";
    static final String DELETE_PARTNER = "delete from Partner";
    static final String DELETE_PARTNER_USER = "delete from Partner_User";
    static final String DELETE_USER_CUSTOMER_ROLE = "delete from User_Customer_Role";

    static final String CREATE_DB_USER = "INSERT INTO User (id, user_type, user_name, first_name, last_name, email, timezone, culture, is_active, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_PARTNER = "INSERT INTO Partner (id, name, email, notes, website, vat_id, is_active, version) VALUES (?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_APPLICATION = "INSERT INTO Application (id, application_name, description, website, partner_id, is_internal, is_active, version) VALUES (?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_APPLICATION_VERSION = "INSERT INTO Application_Version (id, application_id, api_manager_id, version_name, status, release_date, description, is_active, is_non_commercial, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_CUSTOMER = "INSERT INTO Customer (id, is_active, salesforce_id, company_name, phone, email, website, vat_id, is_demo_customer, notes, address_id, timezone, code, version) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'defaultCode1', '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_ADDRESS = "INSERT INTO Address (id, address_line1, address_line2, city, zip_code, country) VALUES (?, ?, ?, ?, ?, ?);";
    static final String CREATE_DB_PROPERTY = "INSERT INTO Property (id, is_active, salesforce_id, name, email, website, is_demo_property, address_id, timezone, property_code, description, anchor_customer_id, version, tti_id, hospitality_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "', '123', '5544788');";
    static final String CREATE_DB_COMMERCIAL_SUBSCRIPTION = "INSERT INTO Commercial_Subscription (id, customer_id, property_id, application_id, is_active,  version) VALUES (?, ?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";
    static final String CREATE_DB_API_SUBSCRIPTION = "INSERT INTO Api_Subscription (id, commercial_subscription_id, app_version_id, is_active,  version) VALUES (?, ?, ?, ?, '" + DEFAULT_SNAPSHOT_ETAG + "');";

    private DbHelper dbHelper = new DbHelper();

    public void deleteAllPropertyCustomersFromDb(String customerId, String propertyId) {
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY_BY_CUSTOMER_ID_PROPERTY_ID, customerId, propertyId);
    }

    public void deletePropertyUserFromDb(String userId, String propertyId) {
        dbHelper.identityDb().update(DELETE_USER_PROPERTY_BY_USER_ID_PROPERTY_ID, userId, propertyId);
    }

    public void createDBUser(UserDto user) {
        dbHelper.identityDb().update(CREATE_DB_USER, user.getId(), user.getUserType().toString(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getTimezone(), user.getCulture(), user.getIsActive());
    }

    public void createDBPartner(PartnerDto partner) {
        dbHelper.identityDb().update(CREATE_DB_PARTNER, partner.getId(), partner.getName(), partner.getEmail(), partner.getNotes(), partner.getWebsite(), partner.getVatId(), partner.getIsActive());
    }

    public void createDBApplication(ApplicationDto application) {
        dbHelper.identityDb().update(CREATE_DB_APPLICATION, application.getId(), application.getApplicationName(), application.getDescription(), application.getWebsite(), application.getPartnerId(), application.getIsInternal(), application.getIsActive());
    }

    public void createDBApplicationVersion(ApplicationVersionDto applicationVersion) {
        dbHelper.identityDb().update(CREATE_DB_APPLICATION_VERSION, applicationVersion.getId(), applicationVersion.getApplicationId(), applicationVersion.getApiManagerId(), applicationVersion.getVersionName(), applicationVersion.getStatus().toString(), applicationVersion.getReleaseDate(), applicationVersion.getDescription(), applicationVersion.getIsActive(), applicationVersion.getIsNonCommercial());
    }

    public void createDBCustomer(CustomerCreateDto customer) {
        dbHelper.identityDb().update(CREATE_DB_ADDRESS, DEFAULT_ADDRESS_ID, "address line 1", "address line 2", "city", "12345", "CZ");
        dbHelper.identityDb().update(CREATE_DB_CUSTOMER, customer.getId(), customer.getIsActive(), customer.getSalesforceId(), customer.getCompanyName(), customer.getPhone(), customer.getEmail(), customer.getWebsite(), customer.getVatId(), customer.getIsDemoCustomer(), customer.getNotes(), DEFAULT_ADDRESS_ID, customer.getTimezone());
    }

    public void createDBProperty(PropertyDto property) {
        dbHelper.identityDb().update(CREATE_DB_PROPERTY, property.getId(), property.getIsActive(), property.getSalesforceId(), property.getName(), property.getEmail(), property.getWebsite(), property.getIsDemoProperty(), DEFAULT_ADDRESS_ID, property.getTimezone(), property.getPropertyCode(), property.getDescription(), property.getAnchorCustomerId());
    }

    public void createDbCommercialSubscription(CommercialSubscriptionDto commercialSubscription) {
        dbHelper.identityDb().update(CREATE_DB_COMMERCIAL_SUBSCRIPTION, commercialSubscription.getId(), commercialSubscription.getCustomerId(), commercialSubscription.getPropertyId(), commercialSubscription.getApplicationId(), commercialSubscription.getIsActive());
    }

    public void createDBApiSubscription(ApiSubscriptionDto apiSubscription) {
        dbHelper.identityDb().update(CREATE_DB_API_SUBSCRIPTION, apiSubscription.getId(), apiSubscription.getCommercialSubscriptionId(), apiSubscription.getApplicationVersionId(), apiSubscription.getIsActive());
    }

    public void cleanDatabase() {
        dbHelper.identityDb().update(DELETE_CUSTOMER_HIERARCHY_PATH);
        dbHelper.identityDb().update(DELETE_PROPERTY_SET_HIERARCHY_PATH);
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY);
        dbHelper.identityDb().update(DELETE_USER_CUSTOMER_ROLE);
        dbHelper.identityDb().update(DELETE_CUSTOMER_USER);
        dbHelper.identityDb().update(DELETE_PARTNER_USER);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY_ROLE);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY_SET_ROLE);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY);
        dbHelper.identityDb().update(DELETE_USER_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_API_SUBSCRIPTIONS);
        dbHelper.identityDb().update(DELETE_COMMERCIAL_SUBSCRIPTION);
        dbHelper.identityDb().update(DELETE_PROPERTY_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTY_ROLES);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTIES);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTYSET_ROLES);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_USER);
        dbHelper.identityDb().update(DELETE_PROPERTY);
        dbHelper.identityDb().update(UPDATE_PROPERTY_SET);
        dbHelper.identityDb().update(DELETE_PROPERTY_SET);
        dbHelper.identityDb().update(DELETE_USER_GROUP_ROLE);
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
        dbHelper.ttiDb().update(DELETE_TTI_CROSSREFERENCES);
        dbHelper.identityDb().update(DELETE_PARTNER);
    }



}
