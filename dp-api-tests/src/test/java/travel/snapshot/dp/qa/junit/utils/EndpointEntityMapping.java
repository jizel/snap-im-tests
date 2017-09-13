package travel.snapshot.dp.qa.junit.utils;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATION_PERMISSIONS_PATH;
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
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_ROLES_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;

import travel.snapshot.dp.api.identity.model.ApplicationCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationPermissionCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationPermissionDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionCreateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionCreateDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerRoleDto;
import travel.snapshot.dp.api.identity.model.PartnerCreateDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationCreateDto;
import travel.snapshot.dp.api.identity.model.PlatformOperationDto;
import travel.snapshot.dp.api.identity.model.PropertyCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyRoleDto;
import travel.snapshot.dp.api.identity.model.PropertySetCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.RoleCreateDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.UserCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserGroupCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.model.EntityDto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Non-modifiable static map of endpoints (paths) and their related entities (DTOs)
 */
public class EndpointEntityMapping {

    public static final Map<String, Class<? extends EntityDto>> endpointDtoMap;
    public static final Map<String, Class<?>> endpointCreateDtoMap;

    static {
        Map<String, Class<? extends EntityDto>> helpMap = new HashMap<>();
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
        helpMap.put(USER_PROPERTY_ROLES_PATH, PropertyRoleDto.class);
        helpMap.put(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipDto.class);
        helpMap.put(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipDto.class);
        helpMap.put(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, PropertySetPropertyRelationshipDto.class);
        helpMap.put(PLATFORM_OPERATIONS_PATH, PlatformOperationDto.class);
        helpMap.put(APPLICATION_PERMISSIONS_PATH, ApplicationPermissionDto.class);

        endpointDtoMap = Collections.unmodifiableMap(helpMap);
    }

    static {
        Map<String, Class<?>> helpMap = new HashMap<>();
        helpMap.put(APPLICATIONS_PATH, ApplicationCreateDto.class);
        helpMap.put(APPLICATION_VERSIONS_PATH, ApplicationVersionCreateDto.class);
        helpMap.put(CUSTOMERS_PATH, CustomerCreateDto.class);
        helpMap.put(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipCreateDto.class);
        helpMap.put(USERS_PATH, UserCreateDto.class);
        helpMap.put(CUSTOMERS_PATH, CustomerCreateDto.class);
        helpMap.put(ROLES_PATH, RoleCreateDto.class);
        helpMap.put(PROPERTIES_PATH, PropertyCreateDto.class);
        helpMap.put(PROPERTY_SETS_PATH, PropertySetCreateDto.class);
        helpMap.put(COMMERCIAL_SUBSCRIPTIONS_PATH, CommercialSubscriptionCreateDto.class);
        helpMap.put(PARTNERS_PATH, PartnerCreateDto.class);
        helpMap.put(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipCreateDto.class);
        helpMap.put(USER_PROPERTY_RELATIONSHIPS_PATH, UserPropertyRelationshipCreateDto.class);
        helpMap.put(USER_PROPERTY_SET_RELATIONSHIPS_PATH, UserPropertySetRelationshipCreateDto.class);
        helpMap.put(USER_PARTNER_RELATIONSHIPS_PATH, UserPartnerRelationshipCreateDto.class);
        helpMap.put(USER_GROUP_USER_RELATIONSHIPS_PATH, UserGroupUserRelationshipCreateDto.class);
        helpMap.put(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationshipCreateDto.class);
        helpMap.put(USER_CUSTOMER_ROLES_PATH, CustomerRoleCreateDto.class);
        helpMap.put(USER_PROPERTY_ROLES_PATH, PropertyRoleCreateDto.class);
        helpMap.put(USER_GROUPS_PATH, UserGroupCreateDto.class);
        helpMap.put(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH, UserGroupPropertyRelationshipCreateDto.class);
        helpMap.put(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH, UserGroupPropertySetRelationshipCreateDto.class);
        helpMap.put(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, PropertySetPropertyRelationshipCreateDto.class);
        helpMap.put(PLATFORM_OPERATIONS_PATH, PlatformOperationCreateDto.class);
        helpMap.put(APPLICATION_PERMISSIONS_PATH, ApplicationPermissionCreateDto.class);

        endpointCreateDtoMap = Collections.unmodifiableMap(helpMap);
    }
}
