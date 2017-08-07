package travel.snapshot.dp.qa.cucumber.serenity.users;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;

import com.jayway.restassured.response.Response;

/**
 * Created by benka on 05-May-16.
 */
public class UserRolesSteps extends UsersSteps {


    public void roleExistsBetweenUserAndCustomer(String roleId, String userId, String customerId, Boolean isActive) {
        Response response = createRoleBetweenUserAndCustomer(roleId, userId, customerId, isActive);
        assertThat("Failed to assign role: " + response.body().toString(), response.statusCode(), is(SC_CREATED));
    }

    public Response createRoleBetweenUserAndCustomer(String roleId, String userId, String customerId, Boolean isActive) {
        return createRoleBetweenUserAndEntity(CUSTOMERS_RESOURCE, roleId, userId, customerId, isActive);
    }

    public void roleBetweenUserAndCustomerIsDeleted(String roleId, String userId, String customerId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(CUSTOMERS_RESOURCE, roleId, userId, customerId, nonExistent);
    }

    public void roleBetweenUserAndCustomerNotExists(String roleId, String userName, String customerId) {
        roleBetweenUserAndEntityNotExists(CUSTOMERS_RESOURCE, roleId, userName, customerId);
    }

    public void roleNameExistsBetweenUserAndCustomer(String roleId, String userId, String customerId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(CUSTOMERS_RESOURCE, roleId, userId, customerId, isActive);
    }

    public void getRolesBetweenUserAndCustomer(String userName, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(CUSTOMERS_RESOURCE, userName, customerId, limit, cursor, filter, sort, sortDesc);
    }

//------------------------------------------------------------------------------------------------------------

    public void addRoleBetweenUserAndProperty(String roleId, String userId, String propId, Boolean isActive) {
        createRoleBetweenUserAndEntity(PROPERTIES_RESOURCE, roleId, userId, propId, isActive);
    }

    public void roleExistsBetweenUserAndProperty(String roleId, String userId, String propId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(PROPERTIES_RESOURCE, roleId, userId, propId, isActive);
    }

    public void roleBetweenUserAndPropertyIsDeleted(String roleId, String userId, String propertyId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(PROPERTIES_RESOURCE, roleId, userId, propertyId, nonExistent);
    }

    public void roleBetweenUserAndPropertyNotExists(String roleId, String userName, String propertyId) {
        roleBetweenUserAndEntityNotExists(PROPERTIES_RESOURCE, roleId, userName, propertyId);
    }

    public void roleNameExistsBetweenUserAndProperty(String roleId, String userId, String propertyId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(PROPERTIES_RESOURCE, roleId, userId, propertyId, isActive);
    }

    public void getRolesBetweenUserAndProperty(String userId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(PROPERTIES_RESOURCE, userId, propertyId, limit, cursor, filter, sort, sortDesc);
    }


//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndPropertySet(String roleId, String userName, String propertySetId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userName, propertySetId, isActive);
    }

    public void addRoleToUserPropertySet(String roleId, String userName, String propertySetId, Boolean isActive) {
        createRoleBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userName, propertySetId, isActive);
    }

    public void roleBetweenUserAndPropertySetIsDeleted(String roleId, String userId, String propertySetId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, nonExistent);
    }

    public void roleBetweenUserAndPropertySetNotExists(String roleId, String userName, String propertySetId) {
        roleBetweenUserAndEntityNotExists(PROPERTY_SETS_RESOURCE, roleId, userName, propertySetId);
    }

    public void roleNameExistsBetweenUserAndPropertySet(String roleId, String userId, String propertySetId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, isActive);
    }

    public void getRolesBetweenUserAndPropertySet(String userId, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, userId, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void addRoleBetweenNotExistingUserAndPropertySet(String roleId, String userName, String propertySetId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userName, propertySetId, isActive);
    }
}
