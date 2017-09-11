package travel.snapshot.dp.qa.junit.helpers;

import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;

import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserGroupUserRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helpers for relationship endpoints
 */
public class RelationshipsHelpers extends BasicSteps {

    public RelationshipsHelpers() {
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
    }

    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    //    User Customer Relationships

    public List<UserCustomerRelationshipDto> getUserCustomerRelationsForUserWithAuth(UUID userId) {
        Map<String, String> queryParams = buildQueryParamMapForPaging(null, null, String.format("user_id==%s", userId), null, null, null);
        return authorizationHelpers.getEntitiesAsType(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams);
    }

    //    Help methods for relationship objects construction.

    //    This approach is used instead of yaml entities because relationships contain a lot of dependencies to other
    // entities which we want to test. It is also easier to maintain than dependencies across yaml files.

    public UserCustomerRelationshipDto constructUserCustomerRelationshipDto(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipDto userCustomerRelationship = new UserCustomerRelationshipDto();
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

    public UserPartnerRelationshipDto constructUserPartnerRelationshipDto(UUID userId, UUID partnerId, Boolean isActive) {
        UserPartnerRelationshipDto userPartnerRelationship = new UserPartnerRelationshipDto();
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


    public UserPropertyRelationshipDto constructUserPropertyRelationshipDto(UUID userId, UUID propertyId, Boolean isActive) {
        UserPropertyRelationshipDto userPartnerRelationship = new UserPropertyRelationshipDto();
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

    public UserPropertySetRelationshipDto constructUserPropertySetRelationshipDto(UUID userId, UUID propertySetId, Boolean isActive) {
        UserPropertySetRelationshipDto userPropertySetRelationship = new UserPropertySetRelationshipDto();
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

    public CustomerPropertyRelationshipDto constructCustomerPropertyRelationshipDto(UUID customerId, UUID propertyId, Boolean isActive,
                                                                                    CustomerPropertyRelationshipType type,
                                                                                    LocalDate validFrom,
                                                                                    LocalDate validTo) {
        CustomerPropertyRelationshipDto customerPropertyRelationship = new CustomerPropertyRelationshipDto();
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

    public PropertySetPropertyRelationshipDto constructPropertySetPropertyRelationship(UUID propertySetId, UUID propertyId, Boolean isActive) {
        PropertySetPropertyRelationshipDto propertySetPropertyRelationship = new PropertySetPropertyRelationshipDto();
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

    public UserGroupUserRelationshipDto constructUserGroupUserRelationship(UUID userGroupId, UUID userId, Boolean isActive) {
        UserGroupUserRelationshipDto userGroupUserRelationship = new UserGroupUserRelationshipDto();
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

    public UserGroupPropertyRelationshipDto constructUserGroupPropertyRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = new UserGroupPropertyRelationshipDto();
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

    public UserGroupPropertySetRelationshipDto constructUserGroupPropertySetRelationship(UUID userGroupId, UUID propertySetId, Boolean isActive) {
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = new UserGroupPropertySetRelationshipDto();
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
}
