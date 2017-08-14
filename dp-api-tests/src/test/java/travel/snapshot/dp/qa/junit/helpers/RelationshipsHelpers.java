package travel.snapshot.dp.qa.junit.helpers;

import static java.util.Arrays.stream;
import static org.apache.http.HttpStatus.SC_CREATED;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLE_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_CUSTOMER_ROLE_RELATIONSHIPS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_GROUP_USER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PARTNER_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_SET_RELATIONSHIPS_PATH;

import com.jayway.restassured.response.Response;
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
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * CRUD methods for relationship endpoints
 */
public class RelationshipsHelpers extends BasicSteps {

    private final CommonHelpers commonHelpers = new CommonHelpers();
    private final AuthorizationHelpers authorizationHelpers = new AuthorizationHelpers();

    //    User Customer Relationships

    public Response createUserCustomerRelationship(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIPS_PATH);
        UserCustomerRelationshipDto userCustomerRelationship = constructUserCustomerRelationshipDto(userId, customerId, isActive, isPrimary);
        return createEntity(userCustomerRelationship);
    }

    public UUID userCustomerRelationIsCreatedWithAuth(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        createUserCustomerRelationshipWithAuth(userId, customerId, isActive, isPrimary);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(UserCustomerRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeleTables(CUSTOMER_USERS, relationId);
        return relationId;
    }

    public void createUserCustomerRelationshipWithAuth(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipDto userCustomerRelationship = constructUserCustomerRelationshipDto(userId, customerId, isActive, isPrimary);
        authorizationHelpers.createEntity(USER_CUSTOMER_RELATIONSHIPS_PATH, userCustomerRelationship);
    }

    public UserCustomerRelationshipDto userCustomerRelationshipIsCreated(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        Response response = createUserCustomerRelationship(userId, customerId, isActive, isPrimary);
        responseCodeIs(SC_CREATED);
        return response.as(UserCustomerRelationshipDto.class);
    }

    public Response updateUserCustomerRelationship(UUID relationshipId, Boolean isActive, Boolean isPrimary) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIPS_PATH);
        UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate = new UserCustomerRelationshipUpdateDto();
        userCustomerRelationshipUpdate.setIsPrimary(isPrimary);
        if (isActive != null) userCustomerRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, userCustomerRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserCustomerRelationship(UUID relationshipId) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public List<UserCustomerRelationshipDto> getUserCustomerRelationsForUserWithAuth(UUID userId) throws Throwable {
        Map<String, String> queryParams = buildQueryParamMapForPaging(null, null, String.format("user_id==%s", userId), null, null, null);
        return authorizationHelpers.getEntities(USER_CUSTOMER_RELATIONSHIPS_PATH, UserCustomerRelationshipDto.class, queryParams);
    }

    public UserCustomerRelationshipDto getUserCustomerRelationship(UUID relationshipId) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserCustomerRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserCustomerRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }

    //    User Partner Relationships

    public Response createUserPartnerRelationship(UUID userId, UUID partnerId, Boolean isActive) {
        spec.basePath(USER_PARTNER_RELATIONSHIPS_PATH);
        UserPartnerRelationshipDto UserPartnerRelationship = constructUserPartnerRelationshipDto(userId, partnerId, isActive);
        return createEntity(UserPartnerRelationship);
    }

    public UserPartnerRelationshipDto userPartnerRelationshipIsCreated(UUID userId, UUID partnerId, Boolean isActive) {
        Response response = createUserPartnerRelationship(userId, partnerId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(UserPartnerRelationshipDto.class);
    }

    public Response updateUserPartnerRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(USER_PARTNER_RELATIONSHIPS_PATH);
        UserPartnerRelationshipUpdateDto userPartnerRelationshipUpdate = new UserPartnerRelationshipUpdateDto();
        userPartnerRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, userPartnerRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserPartnerRelationship(UUID relationshipId) {
        spec.basePath(USER_PARTNER_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public UserPartnerRelationshipDto getUserPartnerRelationship(UUID relationshipId) {
        spec.basePath(USER_PARTNER_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserPartnerRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserPartnerRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
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
        commonHelpers.updateRegistryOfDeleTables(USER_PROPERTIES, relationId);
        return relationId;
    }

    public Response createUserPropertyRelationship(UUID userId, UUID propertyId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_RELATIONSHIPS_PATH);
        UserPropertyRelationshipDto UserPropertyRelationship = constructUserPropertyRelationshipDto(userId, propertyId, isActive);
        return createEntity(UserPropertyRelationship);
    }

    public UserPropertyRelationshipDto userPropertyRelationshipIsCreated(UUID userId, UUID propertyId, Boolean isActive) {
        Response response = createUserPropertyRelationship(userId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(UserPropertyRelationshipDto.class);
    }

    public Response updateUserPropertyRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_RELATIONSHIPS_PATH);
        UserPropertyRelationshipUpdateDto UserPropertyRelationshipUpdate = new UserPropertyRelationshipUpdateDto();
        UserPropertyRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserPropertyRelationship(UUID relationshipId) {
        spec.basePath(USER_PROPERTY_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public UserPropertyRelationshipDto getUserPropertyRelationship(UUID relationshipId) {
        spec.basePath(USER_PROPERTY_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserPropertyRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserPropertyRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
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
        commonHelpers.updateRegistryOfDeleTables(USER_PROPERTYSETS, relationId);
        return relationId;
    }

    public Response createUserPropertySetRelationship(UUID userId, UUID propertyId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIPS_PATH);
        UserPropertySetRelationshipDto UserPropertySetRelationship = constructUserPropertySetRelationshipDto(userId, propertyId, isActive);
        return createEntity(UserPropertySetRelationship);
    }

    public UserPropertySetRelationshipDto userPropertySetRelationshipIsCreated(UUID userId, UUID propertyId, Boolean isActive) {
        Response response = createUserPropertySetRelationship(userId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(UserPropertySetRelationshipDto.class);
    }

    public Response updateUserPropertySetRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIPS_PATH);
        UserPropertySetRelationshipUpdateDto UserPropertySetRelationshipUpdate = new UserPropertySetRelationshipUpdateDto();
        UserPropertySetRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserPropertySetRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserPropertySetRelationship(UUID relationshipId) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public UserPropertySetRelationshipDto getUserPropertySetRelationship(UUID relationshipId) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserPropertySetRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserPropertySetRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }

    //    Customer Property Relationships

    public Response createCustomerPropertyRelationship(UUID customerId, UUID propertyId, Boolean isActive,
                                                       CustomerPropertyRelationshipType type,
                                                       LocalDate validFrom,
                                                       LocalDate validTo) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH);
        CustomerPropertyRelationshipDto CustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(customerId, propertyId, isActive, type, validFrom, validTo);
        return createEntity(CustomerPropertyRelationship);
    }

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
        commonHelpers.updateRegistryOfDeleTables(CUSTOMER_PROPERTIES, relationId);
        return relationId;
    }


    public CustomerPropertyRelationshipDto customerPropertyRelationshipIsCreated(UUID customerId, UUID propertyId,
                                                                                 Boolean isActive,
                                                                                 CustomerPropertyRelationshipType type,
                                                                                 LocalDate validFrom,
                                                                                 LocalDate validTo) {
        Response response = createCustomerPropertyRelationship(customerId, propertyId, isActive, type, validFrom, validTo);
        responseCodeIs(SC_CREATED);
        return response.as(CustomerPropertyRelationshipDto.class);
    }

    public Response updateCustomerPropertyRelationship(UUID relationshipId, Boolean isActive, CustomerPropertyRelationshipType type,
                                                       LocalDate validFrom,
                                                       LocalDate validTo) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH);
        CustomerPropertyRelationshipUpdateDto customerPropertyRelationshipUpdate = new CustomerPropertyRelationshipUpdateDto();
        customerPropertyRelationshipUpdate.setIsActive(isActive);
        customerPropertyRelationshipUpdate.setType(type);
        customerPropertyRelationshipUpdate.setValidFrom(validFrom);
        customerPropertyRelationshipUpdate.setValidTo(validTo);

        return updateEntity(relationshipId, customerPropertyRelationshipUpdate, getEntityEtag(relationshipId));
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

    public Response deleteCustomerPropertyRelationship(UUID relationshipId) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public CustomerPropertyRelationshipDto getCustomerPropertyRelationship(UUID relationshipId) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        CustomerPropertyRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(CustomerPropertyRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }

    //    Property Set Property Relationships

    public UUID propertySetPropertyIsCreatedWithAuth(UUID propertySetId, UUID propertyId, Boolean isActive) {
        createPropertySetPropertyWithAuth(propertySetId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        UUID relationId = getSessionResponse().as(PropertySetPropertyRelationshipDto.class).getId();
        commonHelpers.updateRegistryOfDeleTables(PROPERTYSET_PROPERTIES, relationId);
        return relationId;
    }

    public void createPropertySetPropertyWithAuth(UUID propertySetId, UUID propertyId, Boolean isActive) {
        PropertySetPropertyRelationshipDto relation = new PropertySetPropertyRelationshipDto();
        relation.setPropertySetId(propertySetId);
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);
        authorizationHelpers.createEntity(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, relation);
    }

    public Response createPropertySetPropertyRelationship(UUID propertySetId, UUID propertyId, Boolean isActive) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH);
        PropertySetPropertyRelationshipDto propertySetPropertyRelationship = constructPropertySetPropertyRelationship(propertySetId, propertyId, isActive);
        return createEntity(propertySetPropertyRelationship);
    }

    public PropertySetPropertyRelationshipDto propertySetPropertyRelationshipIsCreated(UUID propertySetId, UUID propertyId, Boolean isActive) {
        Response response = createPropertySetPropertyRelationship(propertySetId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(PropertySetPropertyRelationshipDto.class);
    }

    public Response updatePropertySetPropertyRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH);
        PropertySetPropertyRelationshipUpdateDto PropertySetPropertyRelationshipUpdate = new PropertySetPropertyRelationshipUpdateDto();
        PropertySetPropertyRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, PropertySetPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deletePropertySetPropertyRelationship(UUID relationshipId) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public PropertySetPropertyRelationshipDto getPropertySetPropertyRelationship(UUID relationshipId) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        PropertySetPropertyRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(PropertySetPropertyRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }

    //    User Group Property Relationships

    public Response createUserGroupPropertyRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH);
        UserGroupPropertyRelationshipDto UserGroupPropertyRelationship = constructUserGroupPropertyRelationship(userGroupId, propertyId, isActive);
        return createEntity(UserGroupPropertyRelationship);
    }

    public UserGroupPropertyRelationshipDto userGroupPropertyRelationshipIsCreated(UUID userGroupId, UUID propertyId, Boolean isActive) {
        Response response = createUserGroupPropertyRelationship(userGroupId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(UserGroupPropertyRelationshipDto.class);
    }

    public Response updateUserGroupPropertyRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH);
        UserGroupPropertyRelationshipUpdateDto UserGroupPropertyRelationshipUpdate = new UserGroupPropertyRelationshipUpdateDto();
        UserGroupPropertyRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserGroupPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserGroupPropertyRelationship(UUID relationshipId) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public UserGroupPropertyRelationshipDto getUserGroupPropertyRelationship(UUID relationshipId) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserGroupPropertyRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserGroupPropertyRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }

    //    User Group Property Set Relationships

    public Response createUserGroupPropertySetRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH);
        UserGroupPropertySetRelationshipDto UserGroupPropertySetRelationship = constructUserGroupPropertySetRelationship(userGroupId, propertyId, isActive);
        return createEntity(UserGroupPropertySetRelationship);
    }

    public UserGroupPropertySetRelationshipDto userGroupPropertySetRelationshipIsCreated(UUID userGroupId, UUID propertyId, Boolean isActive) {
        Response response = createUserGroupPropertySetRelationship(userGroupId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(UserGroupPropertySetRelationshipDto.class);
    }

    public Response updateUserGroupPropertySetRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH);
        UserGroupPropertySetRelationshipUpdateDto UserGroupPropertySetRelationshipUpdate = new UserGroupPropertySetRelationshipUpdateDto();
        UserGroupPropertySetRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserGroupPropertySetRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserGroupPropertySetRelationship(UUID relationshipId) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public UserGroupPropertySetRelationshipDto getUserGroupPropertySetRelationship(UUID relationshipId) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserGroupPropertySetRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserGroupPropertySetRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }

    //    User Group User Relationships

    public Response createUserGroupUserRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIPS_PATH);
        UserGroupUserRelationshipDto UserGroupUserRelationship = constructUserGroupUserRelationship(userGroupId, propertyId, isActive);
        return createEntity(UserGroupUserRelationship);
    }

    public UserGroupUserRelationshipDto userGroupUserRelationshipIsCreated(UUID userGroupId, UUID propertyId, Boolean isActive) {
        Response response = createUserGroupUserRelationship(userGroupId, propertyId, isActive);
        responseCodeIs(SC_CREATED);
        return response.as(UserGroupUserRelationshipDto.class);
    }

    public Response updateUserGroupUserRelationship(UUID relationshipId, Boolean isActive) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIPS_PATH);
        UserGroupUserRelationshipUpdateDto UserGroupUserRelationshipUpdate = new UserGroupUserRelationshipUpdateDto();
        UserGroupUserRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserGroupUserRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserGroupUserRelationship(UUID relationshipId) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIPS_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    public UserGroupUserRelationshipDto getUserGroupUserRelationship(UUID relationshipId) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIPS_PATH);
        String filter = String.format("id==%s", relationshipId);
        UserGroupUserRelationshipDto[] relationshipDtos = getEntities(null, null, null, filter, null, null, null).as(UserGroupUserRelationshipDto[].class);
        return stream(relationshipDtos).findFirst().orElse(null);
    }


    //    Help methods
    private UserCustomerRelationshipDto constructUserCustomerRelationshipDto(UUID userId, UUID customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipDto userCustomerRelationship = new UserCustomerRelationshipDto();
        userCustomerRelationship.setCustomerId(customerId);
        userCustomerRelationship.setUserId(userId);
        userCustomerRelationship.setIsActive(isActive);
        userCustomerRelationship.setIsPrimary(isPrimary);
        return userCustomerRelationship;
    }

    private UserPartnerRelationshipDto constructUserPartnerRelationshipDto(UUID userId, UUID partnerId, Boolean isActive) {
        UserPartnerRelationshipDto userPartnerRelationship = new UserPartnerRelationshipDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPartnerId(partnerId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    private UserPropertyRelationshipDto constructUserPropertyRelationshipDto(UUID userId, UUID propertyId, Boolean isActive) {
        UserPropertyRelationshipDto userPartnerRelationship = new UserPropertyRelationshipDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPropertyId(propertyId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    private UserPropertySetRelationshipDto constructUserPropertySetRelationshipDto(UUID userId, UUID propertySetId, Boolean isActive) {
        UserPropertySetRelationshipDto userPropertySetRelationship = new UserPropertySetRelationshipDto();
        userPropertySetRelationship.setUserId(userId);
        userPropertySetRelationship.setPropertySetId(propertySetId);
        userPropertySetRelationship.setIsActive(isActive);
        return userPropertySetRelationship;
    }

    private CustomerPropertyRelationshipDto constructCustomerPropertyRelationshipDto(UUID customerId, UUID propertyId, Boolean isActive,
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

    private PropertySetPropertyRelationshipDto constructPropertySetPropertyRelationship(UUID propertySetId, UUID propertyId, Boolean isActive) {
        PropertySetPropertyRelationshipDto propertySetPropertyRelationship = new PropertySetPropertyRelationshipDto();
        propertySetPropertyRelationship.setPropertyId(propertyId);
        propertySetPropertyRelationship.setPropertySetId(propertySetId);
        propertySetPropertyRelationship.setIsActive(isActive);
        return propertySetPropertyRelationship;
    }

    private UserGroupUserRelationshipDto constructUserGroupUserRelationship(UUID userGroupId, UUID userId, Boolean isActive) {
        UserGroupUserRelationshipDto userGroupUserRelationship = new UserGroupUserRelationshipDto();
        userGroupUserRelationship.setUserGroupId(userGroupId);
        userGroupUserRelationship.setUserId(userId);
        userGroupUserRelationship.setIsActive(isActive);
        return userGroupUserRelationship;
    }

    private UserGroupPropertyRelationshipDto constructUserGroupPropertyRelationship(UUID userGroupId, UUID propertyId, Boolean isActive) {
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = new UserGroupPropertyRelationshipDto();
        userGroupPropertyRelationship.setPropertyId(propertyId);
        userGroupPropertyRelationship.setUserGroupId(userGroupId);
        userGroupPropertyRelationship.setIsActive(isActive);
        return userGroupPropertyRelationship;
    }

    private UserGroupPropertySetRelationshipDto constructUserGroupPropertySetRelationship(UUID userGroupId, UUID propertySetId, Boolean isActive) {
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = new UserGroupPropertySetRelationshipDto();
        userGroupPropertySetRelationship.setPropertySetId(propertySetId);
        userGroupPropertySetRelationship.setUserGroupId(userGroupId);
        userGroupPropertySetRelationship.setIsActive(isActive);
        return userGroupPropertySetRelationship;
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
        commonHelpers.updateRegistryOfDeleTables(USER_CUSTOMER_ROLE_RELATIONSHIPS_RESOURCE, relationId);
        return relationId;
    }

}
