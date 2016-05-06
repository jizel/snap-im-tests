package travel.snapshot.dp.qa.serenity.users;

/**
 * Created by benka on 05-May-16.
 */
public class UserRolesSteps extends UsersSteps {


    public void roleExistsBetweenUserAndCustomer(String roleId, String userName, String customerId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void addRoleBetweenNotExistingUserAndCustomer(String roleId, String s, String customerId) {
        roleExistsBetweenNotExistingUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, s, customerId);
    }

    public void roleBetweenUserAndCustomerIsDeleted(String roleId, String userName, String customerId) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void roleBetweenUserAndCustomerNotExists(String roleId, String userName, String customerId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void roleNameExistsBetweenUserAndCustomer(String roleId, String userName, String customerId) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void getRolesBetweenUserAndCustomer(String userName, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, userName, customerId, limit, cursor, filter, sort, sortDesc);
    }

//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndProperty(String roleId, String userName, String propId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propId);
    }

    public void addRoleBetweenNotExistingUserAndProperty(String roleId, String s, String customerId) {
        roleExistsBetweenNotExistingUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, s, customerId);
    }

    public void roleBetweenUserAndPropertyIsDeleted(String roleId, String userName, String propertyId) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId);
    }

    public void roleBetweenUserAndPropertyNotExists(String roleId, String userName, String propertyId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId);
    }

    public void roleNameExistsBetweenUserAndProperty(String roleId, String userName, String propertyId) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId);
    }

    public void getRolesBetweenUserAndProperty(String userName, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, userName, propertyId, limit, cursor, filter, sort, sortDesc);
    }


//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndPropertySet(String roleId, String userName, String propertySetId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void roleBetweenUserAndPropertySetIsDeleted(String roleId, String userName, String propertySetId) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void roleBetweenUserAndPropertySetNotExists(String roleId, String userName, String propertySetId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void roleNameExistsBetweenUserAndPropertySet(String roleId, String userName, String propertySetId) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void getRolesBetweenUserAndPropertySet(String userName, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, userName, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void addRoleBetweenNotExistingUserAndPropertySet(String roleId, String userName, String propertySetId) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }
}
