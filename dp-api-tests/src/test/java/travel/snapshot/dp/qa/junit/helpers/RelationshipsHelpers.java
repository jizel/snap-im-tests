package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLE_RELATIONSHIPS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;

import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRoleRelationshipDto;
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

    public UUID userCustomerRelationIsCreatedWithAuth(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        createUserCustomerRelationshipWithAuth(userId, customerId, isActive, isPrimary);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(UserCustomerRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(CUSTOMER_USERS, relationId);
        return relationId;
    }

    public void createUserCustomerRelationshipWithAuth(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipDto userCustomerRelationship = constructUserCustomerRelationshipDto(userId, customerId, isActive, isPrimary);
        authorizationHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship);
    }

    public List<UserCustomerRelationshipDto> getUserCustomerRelationsForUserWithAuth(UUID userId) throws Throwable {
        Map<String, String> queryParams = buildQueryParamMapForPaging(null, null, String.format("user_id==%s", userId), null, null, null);
        return authorizationHelpers.getEntities(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams);
    }

    //    User Property Relationships

    public void createUserPropertyRelationWithAuth(UUID userId, UUID propertyId, Boolean isActive) {
        UserPropertyRelationshipDto relation = new UserPropertyRelationshipDto();
        relation.setUserId(userId);
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);
        authorizationHelpers.createEntity(USER_PROPERTY_RELATIONSHIPS_PATH, relation);
    }

    public UUID userPropertyRelationIsCreatedWithAuth(UUID userId, UUID propertyId, Boolean isActive) {
        createUserPropertyRelationWithAuth(userId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(UserPropertyRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(USER_PROPERTIES, relationId);
        return relationId;
    }

    //    User Property Set Relationships

    public void createUserPropertySetRelationWithAuth(UUID userId, UUID propertySetId, Boolean isActive) {
        UserPropertySetRelationshipDto relation = new UserPropertySetRelationshipDto();
        relation.setUserId(userId);
        relation.setPropertySetId(propertySetId);
        relation.setIsActive(isActive);
        authorizationHelpers.createEntity(USER_PROPERTY_SET_RELATIONSHIPS_PATH, relation);
    }

    public UUID userPropertySetRelationIsCreatedWithAuth(UUID userId, UUID propertySetId, Boolean isActive) {
        createUserPropertySetRelationWithAuth(userId, propertySetId, isActive);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(UserPropertySetRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(USER_PROPERTYSETS, relationId);
        return relationId;
    }

    //    Customer Property Relationships

    public void createCustomerPropertyRelationshipWithAuth(UUID customerId, UUID propertyId, Boolean isActive,
                                                           CustomerPropertyRelationshipType type,
                                                           LocalDate validFrom,
                                                           LocalDate validTo) {
        CustomerPropertyRelationshipDto CustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(customerId, propertyId, isActive, type, validFrom, validTo);
        authorizationHelpers.createEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, CustomerPropertyRelationship).as(CustomerPropertyRelationshipDto.class);
    }

    public UUID customerPropertyRelationIsCreatedWithAuth(UUID customerId, UUID propertyId, Boolean isActive,
                                                          CustomerPropertyRelationshipType type,
                                                          LocalDate validFrom,
                                                          LocalDate validTo) {
        createCustomerPropertyRelationshipWithAuth(customerId, propertyId, isActive, type, validFrom, validTo);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(CustomerPropertyRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(CUSTOMER_PROPERTIES, relationId);
        return relationId;
    }

    public void updateCustomerPropertyRelationshipWithAuth(UUID relationshipId, Boolean isActive, CustomerPropertyRelationshipType type,
                                                           LocalDate validFrom,
                                                           LocalDate validTo) {
        CustomerPropertyRelationshipUpdateDto customerPropertyRelationshipUpdate = new CustomerPropertyRelationshipUpdateDto();
        customerPropertyRelationshipUpdate.setIsActive(isActive);
        customerPropertyRelationshipUpdate.setType(type);
        customerPropertyRelationshipUpdate.setValidFrom(validFrom);
        customerPropertyRelationshipUpdate.setValidTo(validTo);
        authorizationHelpers.updateEntity(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, relationshipId, customerPropertyRelationshipUpdate);
    }

    //    Property Set Property Relationships

    public UUID propertySetPropertyIsCreatedWithAuth(UUID propertySetId, UUID propertyId, Boolean isActive) {
        createPropertySetPropertyWithAuth(propertySetId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(PropertySetPropertyRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(PROPERTYSET_PROPERTIES, relationId);
        return relationId;
    }

    public void createPropertySetPropertyWithAuth(UUID propertySetId, UUID propertyId, Boolean isActive) {
        PropertySetPropertyRelationshipDto relation = new PropertySetPropertyRelationshipDto();
        relation.setPropertySetId(propertySetId);
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);
        authorizationHelpers.createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation);
    }

    // roles

    public void createUserCustomerRoleRelationWithAuth(UUID userCustomerRelationId, UUID roleId) {
        // Uses the new endpoint /identity/user_customer_role_relationships
        UserCustomerRoleRelationshipDto relation = new UserCustomerRoleRelationshipDto();
        relation.setUserCustomerId(userCustomerRelationId);
        relation.setRoleId(roleId);
        authorizationHelpers.createEntity(USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH, relation);
    }

    public UUID userCustomerRoleRelationIsCreatedWithAuth(UUID userCustomerRelationId, UUID roleId) {
        // Uses the new endpoint /identity/user_customer_role_relationships
        createUserCustomerRoleRelationWithAuth(userCustomerRelationId, roleId);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(UserCustomerRoleRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeletables(USER_CUSTOMER_ROLE_RELATIONSHIPS_RESOURCE, relationId);
        return relationId;
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
