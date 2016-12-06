package travel.snapshot.dp.qa.serenity;

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
    static final String DELETE_API_SUBSCRIPTIONS = "delete  from Api_Subscription";
    static final String DELETE_APPLICATION = "delete from Application";
    static final String DELETE_APPLICATION_VERSIONS = "delete from Version";
    static final String DELETE_USER_GROUPS = "delete from User_Group";
    static final String DELETE_USER_GROUPS_PROPERTIES = "delete from User_Group_Property";
    static final String DELETE_USER_GROUPS_PROPERTYSET = "delete from User_Group_Property_Set";
    static final String DELETE_CUSTOMER_PROPERTY_BY_CUSTOMER_ID_PROPERTY_ID = "delete from Customer_Property where customer_id = ? and property_id = ?";
    static final String DELETE_COMMERCIAL_SUBSCRIPTION = "delete from Commercial_Subscription";
    static final String DELETE_CUSTOMER_HIERARCHY_PATH = "delete from Customer_Hierarchy_Path";
    static final String DELETE_PROPERTY_SET_HIERARCHY_PATH = "delete from PropertySet_Hierarchy_Path";
    static final String DELETE_USER_PROPERTY_BY_USER_ID_PROPERTY_ID = "delete  from User_Property where user_id = ? and property_id = ?";
    private DbHelper dbHelper = new DbHelper();

    public void deleteAllPropertyCustomersFromDb(String customerId, String propertyId) {
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY_BY_CUSTOMER_ID_PROPERTY_ID, customerId, propertyId);
    }

    public void deletePropertyUserFromDb(String userId, String propertyId) {
        dbHelper.identityDb().update(DELETE_USER_PROPERTY_BY_USER_ID_PROPERTY_ID, userId, propertyId);
    }

    public void cleanDatabase() {
        dbHelper.identityDb().update(DELETE_CUSTOMER_HIERARCHY_PATH);
        dbHelper.identityDb().update(DELETE_PROPERTY_SET_HIERARCHY_PATH);
        dbHelper.identityDb().update(DELETE_CUSTOMER_PROPERTY);
        dbHelper.identityDb().update(DELETE_CUSTOMER_USER);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY);
        dbHelper.identityDb().update(DELETE_USER_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_COMMERCIAL_SUBSCRIPTION);
        dbHelper.identityDb().update(DELETE_PROPERTY_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTIES);
        dbHelper.identityDb().update(DELETE_USER_GROUPS_PROPERTYSET);
        dbHelper.identityDb().update(DELETE_PROPERTY);
        dbHelper.identityDb().update(DELETE_PROPERTY_SET);
        dbHelper.identityDb().update(UPDATE_PROPERTY_SET);
        dbHelper.identityDb().update(DELETE_USER_GROUPS);
        dbHelper.identityDb().update(DELETE_PROPERTY);
        dbHelper.identityDb().update(UPDATE_CUSTOMER);
        dbHelper.identityDb().update(DELETE_CUSTOMER);
        dbHelper.identityDb().update(DELETE_USER);
        dbHelper.identityDb().update(DELETE_ADDRESS);
        dbHelper.identityDb().update(DELETE_ROLE);
        dbHelper.identityDb().update(DELETE_API_SUBSCRIPTIONS);
        dbHelper.identityDb().update(DELETE_APPLICATION_VERSIONS);
        dbHelper.identityDb().update(DELETE_APPLICATION);
        dbHelper.identityDb().update(DELETE_USER_PROPERTY);
    }


}
