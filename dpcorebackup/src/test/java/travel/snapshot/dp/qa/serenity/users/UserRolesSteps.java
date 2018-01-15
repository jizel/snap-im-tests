package travel.snapshot.dp.qa.cucumber.serenity.users;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;

import com.jayway.restassured.response.Response;

import java.util.UUID;

/**
 * Created by benka on 05-May-16.
 */
public class UserRolesSteps extends UsersSteps {


    public void roleExistsBetweenUserAndCustomer(UUID roleId, UUID userId, UUID customerId, Boolean isActive) {
        Response response = createRoleBetweenUserAndCustomer(roleId, userId, customerId, isActive);
        assertThat("Failed to assign role: " + response.body().toString(), response.statusCode(), is(SC_CREATED));
    }

    public Response createRoleBetweenUserAndCustomer(UUID roleId, UUID userId, UUID customerId, Boolean isActive) {
        return createRoleBetweenUserAndEntity(CUSTOMERS_RESOURCE, roleId, userId, customerId, isActive);
    }

    public void roleBetweenUserAndCustomerIsDeleted(UUID roleId, UUID userId, UUID customerId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(CUSTOMERS_RESOURCE, roleId, userId, customerId, nonExistent);
    }

    public void roleBetweenUserAndCustomerNotExists(UUID roleId, String userName, UUID customerId) {
        roleBetweenUserAndEntityNotExists(CUSTOMERS_RESOURCE, roleId, userName, customerId);
    }

    public void roleNameExistsBetweenUserAndCustomer(UUID roleId, UUID userId, UUID customerId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(CUSTOMERS_RESOURCE, roleId, userId, customerId, isActive);
    }

    public void getRolesBetweenUserAndCustomer(UUID userId, UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(CUSTOMERS_RESOURCE, userId, customerId, limit, cursor, filter, sort, sortDesc);
    }

//------------------------------------------------------------------------------------------------------------

    public void addRoleBetweenUserAndProperty(UUID roleId, UUID userId, UUID propId, Boolean isActive) {
        createRoleBetweenUserAndEntity(PROPERTIES_RESOURCE, roleId, userId, propId, isActive);
    }

    public void roleExistsBetweenUserAndProperty(UUID roleId, UUID userId, UUID propId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(PROPERTIES_RESOURCE, roleId, userId, propId, isActive);
    }

    public void roleBetweenUserAndPropertyIsDeleted(UUID roleId, UUID userId, UUID propertyId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(PROPERTIES_RESOURCE, roleId, userId, propertyId, nonExistent);
    }

    public void roleBetweenUserAndPropertyNotExists(UUID roleId, String userName, UUID propertyId) {
        roleBetweenUserAndEntityNotExists(PROPERTIES_RESOURCE, roleId, userName, propertyId);
    }

    public void roleNameExistsBetweenUserAndProperty(UUID roleId, UUID userId, UUID propertyId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(PROPERTIES_RESOURCE, roleId, userId, propertyId, isActive);
    }

    public void getRolesBetweenUserAndProperty(UUID userId, UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(PROPERTIES_RESOURCE, userId, propertyId, limit, cursor, filter, sort, sortDesc);
    }


//------------------------------------------------------------------------------------------------------------

    public void roleExistsBetweenUserAndPropertySet(UUID roleId, UUID userId, UUID propertySetId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, isActive);
    }

    public void addRoleToUserPropertySet(UUID roleId, UUID userId, UUID propertySetId, Boolean isActive) {
        createRoleBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, isActive);
    }

    public void roleBetweenUserAndPropertySetIsDeleted(UUID roleId, UUID userId, UUID propertySetId, String nonExistent) {
        roleBetweenUserAndEntityIsDeleted(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, nonExistent);
    }

    public void roleBetweenUserAndPropertySetNotExists(UUID roleId, String userName, UUID propertySetId) {
        roleBetweenUserAndEntityNotExists(PROPERTY_SETS_RESOURCE, roleId, userName, propertySetId);
    }

    public void roleNameExistsBetweenUserAndPropertySet(UUID roleId, UUID userId, UUID propertySetId, Boolean isActive) {
        roleNameExistsBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, isActive);
    }

    public void getRolesBetweenUserAndPropertySet(UUID userId, UUID propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        getRolesBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, userId, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void addRoleBetweenNotExistingUserAndPropertySet(UUID roleId, UUID userId, UUID propertySetId, Boolean isActive) {
        roleExistsBetweenUserAndEntity(PROPERTY_SETS_RESOURCE, roleId, userId, propertySetId, isActive);
    }
}
