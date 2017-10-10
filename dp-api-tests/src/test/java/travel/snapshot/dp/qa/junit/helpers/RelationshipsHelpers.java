package travel.snapshot.dp.qa.junit.helpers;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;

import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.RoleAssignmentCreateDto;
import travel.snapshot.dp.api.identity.model.RolePermissionCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipCreateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
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
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;
import travel.snapshot.dp.qa.junit.tests.identity.roles.RoleAssignmentTests;

import java.time.LocalDate;
import java.util.List;
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

    public List<UserCustomerRelationshipDto> getUserCustomerRelationsForUserWithAuth(UUID userId) {
        Map<String, String> queryParams = buildQueryParamMapForPaging(null, null, String.format("user_id==%s", userId), null, null, null);
        return authorizationHelpers.getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams);
    }

    //    Help methods for relationship objects construction.

    //    This approach is used instead of yaml entities because relationships contain a lot of dependencies to other
    // entities which we want to test. It is also easier to maintain than dependencies across yaml files.

    public UserCustomerRelationshipCreateDto constructUserCustomerRelationshipDto(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipCreateDto userCustomerRelationship = new UserCustomerRelationshipCreateDto();
        userCustomerRelationship.setCustomerId(customerId);
        userCustomerRelationship.setUserId(userId);
        userCustomerRelationship.setIsActive(isActive);
        userCustomerRelationship.setIsPrimary(isPrimary);
        return userCustomerRelationship;
    }

    public UserCustomerRelationshipUpdateDto constructUserCustomerRelationshipUpdate(Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate = new UserCustomerRelationshipUpdateDto();
        userCustomerRelationshipUpdate.setIsPrimary(isPrimary);
        userCustomerRelationshipUpdate.setIsActive(isActive);
        return userCustomerRelationshipUpdate;
    }

    public UserPartnerRelationshipCreateDto constructUserPartnerRelationshipDto(UUID userId, UUID partnerId, Boolean isActive) {
        UserPartnerRelationshipCreateDto userPartnerRelationship = new UserPartnerRelationshipCreateDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPartnerId(partnerId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    public UserPartnerRelationshipUpdateDto constructUserPartnerRelationshipUpdateDto(Boolean isActive) {
        UserPartnerRelationshipUpdateDto userPartnerRelationship = new UserPartnerRelationshipUpdateDto();
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }


    public UserPropertyRelationshipCreateDto constructUserPropertyRelationshipDto(UUID userId, UUID propertyId, Boolean isActive) {
        UserPropertyRelationshipCreateDto userPartnerRelationship = new UserPropertyRelationshipCreateDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPropertyId(propertyId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    public UserPropertyRelationshipUpdateDto constructUserPropertyRelationshipUpdateDto(Boolean isActive) {
        UserPropertyRelationshipUpdateDto userPropertyRelationship = new UserPropertyRelationshipUpdateDto();
        userPropertyRelationship.setIsActive(isActive);
        return userPropertyRelationship;
    }

    public UserPropertySetRelationshipCreateDto constructUserPropertySetRelationshipDto(UUID userId, UUID propertySetId, Boolean isActive) {
        UserPropertySetRelationshipCreateDto userPropertySetRelationship = new UserPropertySetRelationshipCreateDto();
        userPropertySetRelationship.setUserId(userId);
        userPropertySetRelationship.setPropertySetId(propertySetId);
        userPropertySetRelationship.setIsActive(isActive);
        return userPropertySetRelationship;
    }

    public UserPropertySetRelationshipUpdateDto constructUserPropertySetRelationshipUpdateDto(Boolean isActive) {
        UserPropertySetRelationshipUpdateDto userPropertySetRelationship = new UserPropertySetRelationshipUpdateDto();
        userPropertySetRelationship.setIsActive(isActive);
        return userPropertySetRelationship;
    }

    public CustomerPropertyRelationshipCreateDto constructCustomerPropertyRelationshipDto(UUID customerId, UUID propertyId, Boolean isActive,
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

    public CustomerPropertyRelationshipUpdateDto constructCustomerPropertyRelationshipUpdate(Boolean isActive,
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

    public PropertySetPropertyRelationshipCreateDto constructPropertySetPropertyRelationship(UUID propertySetId, UUID propertyId, Boolean isActive) {
        PropertySetPropertyRelationshipCreateDto propertySetPropertyRelationship = new PropertySetPropertyRelationshipCreateDto();
        propertySetPropertyRelationship.setPropertyId(propertyId);
        propertySetPropertyRelationship.setPropertySetId(propertySetId);
        propertySetPropertyRelationship.setIsActive(isActive);
        return propertySetPropertyRelationship;
    }

    public PropertySetPropertyRelationshipUpdateDto constructPropertySetPropertyRelationshipUpdate(Boolean isActive) {
        PropertySetPropertyRelationshipUpdateDto propertySetPropertyRelationshipUpdate = new PropertySetPropertyRelationshipUpdateDto();
        propertySetPropertyRelationshipUpdate.setIsActive(isActive);
        return propertySetPropertyRelationshipUpdate;
    }

    public UserGroupUserRelationshipCreateDto constructUserGroupUserRelationship(UUID userGroupId, UUID userId, Boolean isActive) {
        UserGroupUserRelationshipCreateDto userGroupUserRelationship = new UserGroupUserRelationshipCreateDto();
        userGroupUserRelationship.setUserGroupId(userGroupId);
        userGroupUserRelationship.setUserId(userId);
        userGroupUserRelationship.setIsActive(isActive);
        return userGroupUserRelationship;
    }

    public UserGroupUserRelationshipUpdateDto constructUserGroupUserRelationshipUpdate(Boolean isActive) {
        UserGroupUserRelationshipUpdateDto userGroupUserRelationshipUpdate = new UserGroupUserRelationshipUpdateDto();
        userGroupUserRelationshipUpdate.setIsActive(isActive);
        return userGroupUserRelationshipUpdate;
    }

    public UserGroupPropertyRelationshipCreateDto constructUserGroupPropertyRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipCreateDto userGroupPropertyRelationship = new UserGroupPropertyRelationshipCreateDto();
        userGroupPropertyRelationship.setPropertyId(propertyId);
        userGroupPropertyRelationship.setUserGroupId(userGroupId);
        userGroupPropertyRelationship.setIsActive(isActive);
        return userGroupPropertyRelationship;
    }

    public UserGroupPropertyRelationshipUpdateDto constructUserGroupPropertyRelationshipUpdate(Boolean isActive) {
        UserGroupPropertyRelationshipUpdateDto userGroupPropertyRelationshipUpdate = new UserGroupPropertyRelationshipUpdateDto();
        userGroupPropertyRelationshipUpdate.setIsActive(isActive);
        return userGroupPropertyRelationshipUpdate;
    }

    public UserGroupPropertySetRelationshipCreateDto constructUserGroupPropertySetRelationship(UUID userGroupId, UUID propertySetId, Boolean isActive) {
        UserGroupPropertySetRelationshipCreateDto userGroupPropertySetRelationship = new UserGroupPropertySetRelationshipCreateDto();
        userGroupPropertySetRelationship.setPropertySetId(propertySetId);
        userGroupPropertySetRelationship.setUserGroupId(userGroupId);
        userGroupPropertySetRelationship.setIsActive(isActive);
        return userGroupPropertySetRelationship;
    }

    public UserGroupPropertySetRelationshipUpdateDto constructUserGroupPropertySetRelationshipUpdate(Boolean isActive) {
        UserGroupPropertySetRelationshipUpdateDto userGroupPropertySetRelationshipUpdate = new UserGroupPropertySetRelationshipUpdateDto();
        userGroupPropertySetRelationshipUpdate.setIsActive(isActive);
        return userGroupPropertySetRelationshipUpdate;
    }

    public RoleAssignmentCreateDto constructRoleAssignment(UUID roleId, UUID userId) {
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
