package travel.snapshot.dp.qa.junit.helpers;

import com.jayway.restassured.response.Response;
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.RoleRelationshipDto;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.qa.junit.helpers.RoleType.CUSTOMER;

/**
 * Helper class for roles. Obsolete dependency on RoleBaseSteps will be removed when obsolete role types are removed completely from IM.
 */
public class RoleHelpers extends BasicSteps {

    private static RoleType roleBaseType;
    private static UserHelpers userHelpers = new UserHelpers();

    public RoleHelpers() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    public static RoleCreateDto constructRole(UUID applicationId, String name) {
        RoleCreateDto role = new RoleCreateDto();
        role.setApplicationId(applicationId);
        role.setIsActive(true);
        role.setName(name);
        role.setIsInitial(true);
        return role;
    }

    public void setRolesPathCustomer() {
        spec.basePath(USER_CUSTOMER_ROLES_PATH);
        roleBaseType = CUSTOMER;
    }

    public static RoleType getRoleBaseType() {
        return roleBaseType;
    }

    /**
     * Remove when old role endpoints are removed from IM
     */
    @Deprecated
    public Response assignRoleToUserCustomerRelationshipOld(UUID userId, UUID customerId, RoleRelationshipDto roleRelationship){
        return userHelpers.createThirdLevelEntity(userId, CUSTOMERS_RESOURCE, customerId, ROLES_RESOURCE, roleRelationship)
                .then()
                .statusCode(SC_CREATED)
                .extract()
                .response();
    }
}
