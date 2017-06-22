package travel.snapshot.dp.qa.cucumber.serenity.users;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertThat;

import com.jayway.restassured.response.Response;
import static org.hamcrest.Matchers.is;

/**
 * Created by benka on 05-May-16.
 */
public class UserRolesSteps extends UsersSteps {


    public void roleExistsBetweenUserAndCustomer(String roleId, String userId, String customerId, Boolean isActive) {
        Response response = createRoleBetweenUserAndCustomer(roleId, userId, customerId, isActive);
        assertThat("Failed to assign role: " + response.body().toString(), response.statusCode(), is(SC_CREATED));
    }

    public Response createRoleBetweenUserAndCustomer(String roleId, String userId, String customerId, Boolean isActive) {
        return createRoleBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userId, customerId, isActive);
    }

    public void roleBetweenUserAndCustomerIsDeleted(String roleId, String userId, String customerId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userId, customerId, nonExistent);
    }

    public void roleBetweenUserAndCustomerNotExists(String roleId, String userName, String customerId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId);
    }

    public void roleNameExistsBetweenUserAndCustomer(String roleId, String userName, String customerId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, roleId, userName, customerId, isActive);
    }

    public void getRolesBetweenUserAndCustomer(String userName, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_CUSTOMERS, userName, customerId, limit, cursor, filter, sort, sortDesc);
    }

//------------------------------------------------------------------------------------------------------------

    public void addRoleBetweenUserAndProperty(String roleId, String userId, String propId, Boolean isActive) {
        createRoleBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userId, propId, isActive);
    }

    public void roleExistsBetweenUserAndProperty(String roleId, String userName, String propId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propId, isActive);
    }

    public void roleBetweenUserAndPropertyIsDeleted(String roleId, String userId, String propertyId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userId, propertyId, nonExistent);
    }

    public void roleBetweenUserAndPropertyNotExists(String roleId, String userName, String propertyId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId);
    }

    public void roleNameExistsBetweenUserAndProperty(String roleId, String userName, String propertyId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, roleId, userName, propertyId, isActive);
    }

    public void getRolesBetweenUserAndProperty(String userName, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTIES, userName, propertyId, limit, cursor, filter, sort, sortDesc);
    }


//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndPropertySet(String roleId, String userName, String propertySetId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId, isActive);
    }

    public void addRoleToUserPropertySet(String roleId, String userName, String propertySetId, Boolean isActive) {
        createRoleBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId, isActive);
    }

    public void roleBetweenUserAndPropertySetIsDeleted(String roleId, String userId, String propertySetId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userId, propertySetId, nonExistent);
    }

    public void roleBetweenUserAndPropertySetNotExists(String roleId, String userName, String propertySetId) {
        roleBetweenUserAndEntityNotExists(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId);
    }

    public void roleNameExistsBetweenUserAndPropertySet(String roleId, String userId, String propertySetId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userId, propertySetId, isActive);
    }

    public void getRolesBetweenUserAndPropertySet(String userName, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, userName, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void addRoleBetweenNotExistingUserAndPropertySet(String roleId, String userName, String propertySetId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(SECOND_LEVEL_OBJECT_PROPERTY_SETS, roleId, userName, propertySetId, isActive);
    }
}
