package travel.snapshot.dp.qa.junit.utils;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_VERSIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PARTNERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PLATFORM_OPERATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH;

import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetRoleDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRoleRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRoleRelationshipDto;
import travel.snapshot.dp.api.model.VersionedEntityDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Non-modifiable static map of endpoints (paths) and their related entities (DTOs)
 */
public class EndpointEntityMap {

    public static final Map<String, Class<? extends VersionedEntityDto>> endpointEntityMap = getEndpointEntityMap();

    private static Map<String, Class<? extends VersionedEntityDto>> getEndpointEntityMap() {
        Map<String, Class<? extends VersionedEntityDto>> helpMap = new HashMap<>();
        helpMap.put(APPLICATIONS_PATH, ApplicationDto.class);
        helpMap.put(APPLICATION_VERSIONS_PATH, ApplicationVersionDto.class);
        helpMap.put(CUSTOMERS_PATH, CustomerDto.class);
        helpMap.put(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipDto.class);
        helpMap.put(USERS_PATH, UserDto.class);
        helpMap.put(CUSTOMERS_PATH, CustomerDto.class);
        helpMap.put(ROLES_PATH, RoleDto.class);
        helpMap.put(PROPERTIES_PATH, PropertyDto.class);
        helpMap.put(PROPERTY_SETS_PATH, PropertySetDto.class);
        helpMap.put(COMMERCIAL_SUBSCRIPTIONS_PATH, CommercialSubscriptionDto.class);
        helpMap.put(PARTNERS_PATH, PartnerDto.class);
        helpMap.put(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class);
        helpMap.put(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipDto.class);
        helpMap.put(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipDto.class);
        helpMap.put(USER_PARTNER_RELATIONSHIPS_PATH, UserPartnerRelationshipDto.class);
        helpMap.put(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipDto.class);
        helpMap.put(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipDto.class);
        helpMap.put(USER_GROUPS_PATH, UserGroupDto.class);
        helpMap.put(USER_CUSTOMER_ROLES_PATH, CustomerRoleDto.class);
        helpMap.put(USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH, UserCustomerRoleRelationshipDto.class);
        helpMap.put(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class);
        helpMap.put(USER_GROUP_PROPERTY_ROLE_RELATIONSHIPS_PATH, UserGroupPropertyRoleRelationshipDto.class);
        helpMap.put(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class);
        helpMap.put(USER_GROUP_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH, UserGroupPropertySetRoleRelationshipDto.class);
        helpMap.put(USER_GROUP_ROLE_RELATIONSHIPS_PATH, UserGroupRoleRelationshipDto.class);
        helpMap.put(USER_PROPERTY_ROLE_RELATIONSHIPS_PATH, UserPropertyRoleRelationshipDto.class);
        helpMap.put(USER_PROPERTY_ROLES_PATH, PropertyRoleDto.class);
        helpMap.put(USER_PROPERTY_SET_ROLE_RELATIONSHIPS_PATH, UserPropertySetRoleRelationshipDto.class);
        helpMap.put(USER_PROPERTY_SET_ROLES_PATH, PropertySetRoleDto.class);
        helpMap.put(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, PropertySetPropertyRelationshipDto.class);
        helpMap.put(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class);

        return Collections.unmodifiableMap(helpMap);
    }
}
