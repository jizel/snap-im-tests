package travel.snapshot.dp.qa.junit.helpers;

import static java.lang.String.format;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.qa.junit.helpers.CommonHelpers.getEntitiesAsType;

import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.RoleAssignmentCreateDto;
import travel.snapshot.dp.api.identity.model.RolePermissionCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.api.type.HttpMethod;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.utils.QueryParams;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

/**
 * Helpers for relationship endpoints
 */
public class RelationshipsHelpers extends BasicSteps {

    public RelationshipsHelpers() {
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    private final PlatformOperationHelpers platformOperationHelpers = new PlatformOperationHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    //    User Customer Relationships

    public UserCustomerRelationshipDto getDefaultUserCustomerRelationForUserWithAuth(UUID userId) {
        Map<String, String> queryParams = QueryParams.builder().filter(format("user_id==%s", userId)).build();
        return authorizationHelpers.getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams).get(0);
    }

    public static UserCustomerRelationshipDto getDefaultUserCustomerRelationForUser(UUID userId) {
        Map<String, String> queryParams = QueryParams.builder().filter(format("user_id==%s", userId)).build();
        return getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams).get(0);
    }

    //    Help methods for relationship objects construction.

    //    This approach is used instead of yaml entities because relationships contain a lot of dependencies to other
    // entities which we want to test. It is also easier to maintain than dependencies across yaml files.

    public static UserCustomerRelationshipCreateDto constructUserCustomerRelationshipDto(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipCreateDto userCustomerRelationship = new UserCustomerRelationshipCreateDto();
        userCustomerRelationship.setCustomerId(customerId);
        userCustomerRelationship.setUserId(userId);
        userCustomerRelationship.setIsActive(isActive);
        userCustomerRelationship.setIsPrimary(isPrimary);
        return userCustomerRelationship;
    }

    public static UserCustomerRelationshipPartialDto constructUserCustomerRelationshipPartialDto(UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipPartialDto userCustomerRelationship = new UserCustomerRelationshipPartialDto();
        userCustomerRelationship.setCustomerId(customerId);
        userCustomerRelationship.setIsActive(isActive);
        userCustomerRelationship.setIsPrimary(isPrimary);
        return userCustomerRelationship;
    }

    public static UserCustomerRelationshipUpdateDto constructUserCustomerRelationshipUpdate(Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate = new UserCustomerRelationshipUpdateDto();
        userCustomerRelationshipUpdate.setIsPrimary(isPrimary);
        userCustomerRelationshipUpdate.setIsActive(isActive);
        return userCustomerRelationshipUpdate;
    }

    public static UserPartnerRelationshipCreateDto constructUserPartnerRelationshipDto(UUID userId, UUID partnerId, Boolean isActive) {
        UserPartnerRelationshipCreateDto userPartnerRelationship = new UserPartnerRelationshipCreateDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPartnerId(partnerId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    public static UserPartnerRelationshipUpdateDto constructUserPartnerRelationshipUpdateDto(Boolean isActive) {
        UserPartnerRelationshipUpdateDto userPartnerRelationship = new UserPartnerRelationshipUpdateDto();
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }


    public static UserPropertyRelationshipCreateDto constructUserPropertyRelationshipDto(UUID userId, UUID propertyId, Boolean isActive) {
        UserPropertyRelationshipCreateDto userPartnerRelationship = new UserPropertyRelationshipCreateDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPropertyId(propertyId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    public static UserPropertyRelationshipUpdateDto constructUserPropertyRelationshipUpdateDto(Boolean isActive) {
        UserPropertyRelationshipUpdateDto userPropertyRelationship = new UserPropertyRelationshipUpdateDto();
        userPropertyRelationship.setIsActive(isActive);
        return userPropertyRelationship;
    }

    public static UserPropertySetRelationshipCreateDto constructUserPropertySetRelationshipDto(UUID userId, UUID propertySetId, Boolean isActive) {
        UserPropertySetRelationshipCreateDto userPropertySetRelationship = new UserPropertySetRelationshipCreateDto();
        userPropertySetRelationship.setUserId(userId);
        userPropertySetRelationship.setPropertySetId(propertySetId);
        userPropertySetRelationship.setIsActive(isActive);
        return userPropertySetRelationship;
    }

    public static UserPropertySetRelationshipUpdateDto constructUserPropertySetRelationshipUpdateDto(Boolean isActive) {
        UserPropertySetRelationshipUpdateDto userPropertySetRelationship = new UserPropertySetRelationshipUpdateDto();
        userPropertySetRelationship.setIsActive(isActive);
        return userPropertySetRelationship;
    }

    public static CustomerPropertyRelationshipCreateDto constructCustomerPropertyRelationshipDto(UUID customerId, UUID propertyId, Boolean isActive,
                                                                                          CustomerPropertyRelationshipType type,
                                                                                          LocalDate validFrom,
                                                                                          LocalDate validTo) {
        CustomerPropertyRelationshipCreateDto customerPropertyRelationship = new CustomerPropertyRelationshipCreateDto();
        customerPropertyRelationship.setCustomerId(customerId);
        customerPropertyRelationship.setPropertyId(propertyId);
        customerPropertyRelationship.setIsActive(isActive);
        customerPropertyRelationship.setType(type);
        customerPropertyRelationship.setValidFrom(validFrom);
        customerPropertyRelationship.setValidTo(validTo);
        return customerPropertyRelationship;
    }

    public static CustomerPropertyRelationshipUpdateDto constructCustomerPropertyRelationshipUpdate(Boolean isActive,
                                                                                             CustomerPropertyRelationshipType type,
                                                                                             LocalDate validFrom,
                                                                                             LocalDate validTo) {
        CustomerPropertyRelationshipUpdateDto customerPropertyRelationshipUpdate = new CustomerPropertyRelationshipUpdateDto();
        customerPropertyRelationshipUpdate.setIsActive(isActive);
        customerPropertyRelationshipUpdate.setType(type);
        customerPropertyRelationshipUpdate.setValidFrom(validFrom);
        customerPropertyRelationshipUpdate.setValidTo(validTo);

        return customerPropertyRelationshipUpdate;
    }

    public static PropertySetPropertyRelationshipCreateDto constructPropertySetPropertyRelationship(UUID propertySetId, UUID propertyId, Boolean isActive) {
        PropertySetPropertyRelationshipCreateDto propertySetPropertyRelationship = new PropertySetPropertyRelationshipCreateDto();
        propertySetPropertyRelationship.setPropertyId(propertyId);
        propertySetPropertyRelationship.setPropertySetId(propertySetId);
        propertySetPropertyRelationship.setIsActive(isActive);
        return propertySetPropertyRelationship;
    }

    public static PropertySetPropertyRelationshipUpdateDto constructPropertySetPropertyRelationshipUpdate(Boolean isActive) {
        PropertySetPropertyRelationshipUpdateDto propertySetPropertyRelationshipUpdate = new PropertySetPropertyRelationshipUpdateDto();
        propertySetPropertyRelationshipUpdate.setIsActive(isActive);
        return propertySetPropertyRelationshipUpdate;
    }

    public static UserGroupUserRelationshipCreateDto constructUserGroupUserRelationship(UUID userGroupId, UUID userId, Boolean isActive) {
        UserGroupUserRelationshipCreateDto userGroupUserRelationship = new UserGroupUserRelationshipCreateDto();
        userGroupUserRelationship.setUserGroupId(userGroupId);
        userGroupUserRelationship.setUserId(userId);
        userGroupUserRelationship.setIsActive(isActive);
        return userGroupUserRelationship;
    }

    public static UserGroupUserRelationshipUpdateDto constructUserGroupUserRelationshipUpdate(Boolean isActive) {
        UserGroupUserRelationshipUpdateDto userGroupUserRelationshipUpdate = new UserGroupUserRelationshipUpdateDto();
        userGroupUserRelationshipUpdate.setIsActive(isActive);
        return userGroupUserRelationshipUpdate;
    }

    public static UserGroupPropertyRelationshipCreateDto constructUserGroupPropertyRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipCreateDto userGroupPropertyRelationship = new UserGroupPropertyRelationshipCreateDto();
        userGroupPropertyRelationship.setPropertyId(propertyId);
        userGroupPropertyRelationship.setUserGroupId(userGroupId);
        userGroupPropertyRelationship.setIsActive(isActive);
        return userGroupPropertyRelationship;
    }

    public static UserGroupPropertyRelationshipUpdateDto constructUserGroupPropertyRelationshipUpdate(Boolean isActive) {
        UserGroupPropertyRelationshipUpdateDto userGroupPropertyRelationshipUpdate = new UserGroupPropertyRelationshipUpdateDto();
        userGroupPropertyRelationshipUpdate.setIsActive(isActive);
        return userGroupPropertyRelationshipUpdate;
    }

    public static UserGroupPropertySetRelationshipCreateDto constructUserGroupPropertySetRelationship(UUID userGroupId, UUID propertySetId, Boolean isActive) {
        UserGroupPropertySetRelationshipCreateDto userGroupPropertySetRelationship = new UserGroupPropertySetRelationshipCreateDto();
        userGroupPropertySetRelationship.setPropertySetId(propertySetId);
        userGroupPropertySetRelationship.setUserGroupId(userGroupId);
        userGroupPropertySetRelationship.setIsActive(isActive);
        return userGroupPropertySetRelationship;
    }

    public static UserGroupPropertySetRelationshipUpdateDto constructUserGroupPropertySetRelationshipUpdate(Boolean isActive) {
        UserGroupPropertySetRelationshipUpdateDto userGroupPropertySetRelationshipUpdate = new UserGroupPropertySetRelationshipUpdateDto();
        userGroupPropertySetRelationshipUpdate.setIsActive(isActive);
        return userGroupPropertySetRelationshipUpdate;
    }

    public static RoleAssignmentCreateDto constructRoleAssignment(UUID roleId, UUID userId) {
        RoleAssignmentCreateDto assignment = new RoleAssignmentCreateDto();
        assignment.setRoleId(roleId);
        assignment.setUserId(userId);
        return assignment;
    }

    public RolePermissionCreateDto constructRolePermission(UUID roleId, HttpMethod method, String uriTemplate, Boolean withAuth) {
        UUID platformOperationId = platformOperationHelpers.getPlatformOperationId(method, uriTemplate, withAuth);
        RolePermissionCreateDto rolePermission = new RolePermissionCreateDto();
        rolePermission.setRoleId(roleId);
        rolePermission.setPlatformOperationId(platformOperationId);
        return rolePermission;
    }
}
