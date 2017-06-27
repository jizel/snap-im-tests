package travel.snapshot.dp.qa.junit.helpers;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.jayway.restassured.response.Response;
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
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.time.LocalDate;

/**
 * Created by zelezny on 6/27/2017.
 */
public class RelationshipsHelpers extends BasicSteps{

    private static final String USER_CUSTOMER_RELATIONSHIP_PATH = "api/identity/user_customer_relationships";
    private static final String USER_PARTNER_RELATIONSHIP_PATH = "api/identity/user_partner_relationships";
    private static final String USER_PROPERTY_RELATIONSHIP_PATH = "api/identity/user_property_relationships";
    private static final String USER_PROPERTY_SET_RELATIONSHIP_PATH = "api/identity/user_property_set_relationships";
    private static final String CUSTOMER_PROPERTY_RELATIONSHIP_PATH = "api/identity/customer_property_relationships";
    private static final String PROPERTY_SET_PROPERTY_RELATIONSHIP_PATH = "api/identity/property_set_property_relationships";
    private static final String USER_GROUP_PROPERTY_RELATIONSHIP_PATH = "api/identity/user_group_property_relationships";
    private static final String USER_GROUP_PROPERTY_SET_RELATIONSHIP_PATH = "api/identity/user_group_property_set_relationships";
    private static final String USER_GROUP_USER_RELATIONSHIP_PATH = "api/identity/user_group_user_relationships";


    //    User Customer Relationships

    public Response createUserCustomerRelationship(String userId, String customerId, Boolean isActive, Boolean isPrimary) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIP_PATH);
        UserCustomerRelationshipDto userCustomerRelationship = constructUserCustomerRelationshipDto(userId, customerId, isActive, isPrimary);
        return createEntity(userCustomerRelationship);
    }

    public UserCustomerRelationshipDto userCustomerRelationshipIsCreated(String userId, String customerId, Boolean isActive, Boolean isPrimary) {
        Response response = createUserCustomerRelationship(userId, customerId, isActive, isPrimary);
        assertThat(String.format("Failed to create UserCustomerRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserCustomerRelationshipDto.class);
    }

    public Response updateUserCustomerRelationship(String relationshipId, Boolean isActive, Boolean isPrimary) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIP_PATH);
        UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate = new UserCustomerRelationshipUpdateDto();
        userCustomerRelationshipUpdate.setIsPrimary(isPrimary);
        if (isActive != null) userCustomerRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, userCustomerRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserCustomerRelationship(String relationshipId) {
        spec.basePath(USER_CUSTOMER_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    User Partner Relationships

    public Response createUserPartnerRelationship(String userId, String partnerId, Boolean isActive) {
        spec.basePath(USER_PARTNER_RELATIONSHIP_PATH);
        UserPartnerRelationshipDto UserPartnerRelationship = constructUserPartnerRelationshipDto(userId, partnerId, isActive);
        return createEntity(UserPartnerRelationship);
    }

    public UserPartnerRelationshipDto userPartnerRelationshipIsCreated(String userId, String partnerId, Boolean isActive) {
        Response response = createUserPartnerRelationship(userId, partnerId, isActive);
        assertThat(String.format("Failed to create UserPartnerRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserPartnerRelationshipDto.class);
    }

    public Response updateUserPartnerRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(USER_PARTNER_RELATIONSHIP_PATH);
        UserPartnerRelationshipUpdateDto userPartnerRelationshipUpdate = new UserPartnerRelationshipUpdateDto();
        userPartnerRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, userPartnerRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserPartnerRelationship(String relationshipId) {
        spec.basePath(USER_PARTNER_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    User Property Relationships

    public Response createUserPropertyRelationship(String userId, String propertyId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_RELATIONSHIP_PATH);
        UserPropertyRelationshipDto UserPropertyRelationship = constructUserPropertyRelationshipDto(userId, propertyId, isActive);
        return createEntity(UserPropertyRelationship);
    }

    public UserPropertyRelationshipDto userPropertyRelationshipIsCreated(String userId, String propertyId, Boolean isActive) {
        Response response = createUserPropertyRelationship(userId, propertyId, isActive);
        assertThat(String.format("Failed to create UserPropertyRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserPropertyRelationshipDto.class);
    }

    public Response updateUserPropertyRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_RELATIONSHIP_PATH);
        UserPropertyRelationshipUpdateDto UserPropertyRelationshipUpdate = new UserPropertyRelationshipUpdateDto();
        UserPropertyRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserPropertyRelationship(String relationshipId) {
        spec.basePath(USER_PROPERTY_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    User Property Set Relationships

    public Response createUserPropertySetRelationship(String userId, String propertyId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIP_PATH);
        UserPropertySetRelationshipDto UserPropertySetRelationship = constructUserPropertySetRelationshipDto(userId, propertyId, isActive);
        return createEntity(UserPropertySetRelationship);
    }

    public UserPropertySetRelationshipDto userPropertySetRelationshipIsCreated(String userId, String propertyId, Boolean isActive) {
        Response response = createUserPropertySetRelationship(userId, propertyId, isActive);
        assertThat(String.format("Failed to create UserPropertySetRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserPropertySetRelationshipDto.class);
    }

    public Response updateUserPropertySetRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIP_PATH);
        UserPropertySetRelationshipUpdateDto UserPropertySetRelationshipUpdate = new UserPropertySetRelationshipUpdateDto();
        UserPropertySetRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserPropertySetRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserPropertySetRelationship(String relationshipId) {
        spec.basePath(USER_PROPERTY_SET_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    Customer Property Relationships

    public Response createCustomerPropertyRelationship(String customerId, String propertyId, Boolean isActive,
                                                       CustomerPropertyRelationshipType type,
                                                       LocalDate validFrom,
                                                       LocalDate validTo) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIP_PATH);
        CustomerPropertyRelationshipDto CustomerPropertyRelationship = constructCustomerPropertyRelationshipDto(customerId, propertyId, isActive, type, validFrom, validTo);
        return createEntity(CustomerPropertyRelationship);
    }

    public CustomerPropertyRelationshipDto customerPropertyRelationshipIsCreated(String customerId, String propertyId,
                                                                                 Boolean isActive,
                                                                                 CustomerPropertyRelationshipType type,
                                                                                 LocalDate validFrom,
                                                                                 LocalDate validTo) {
        Response response = createCustomerPropertyRelationship(customerId, propertyId, isActive, type, validFrom, validTo);
        assertThat(String.format("Failed to create CustomerPropertyRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(CustomerPropertyRelationshipDto.class);
    }

    public Response updateCustomerPropertyRelationship(String relationshipId, Boolean isActive, CustomerPropertyRelationshipType type,
                                                       LocalDate validFrom,
                                                       LocalDate validTo) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIP_PATH);
        CustomerPropertyRelationshipUpdateDto customerPropertyRelationshipUpdate = new CustomerPropertyRelationshipUpdateDto();
        customerPropertyRelationshipUpdate.setIsActive(isActive);
        customerPropertyRelationshipUpdate.setType(type);
        customerPropertyRelationshipUpdate.setValidFrom(validFrom);
        customerPropertyRelationshipUpdate.setValidTo(validTo);

        return updateEntity(relationshipId, customerPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteCustomerPropertyRelationship(String relationshipId) {
        spec.basePath(CUSTOMER_PROPERTY_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    Property Set Property Relationships

    public Response createPropertySetPropertyRelationship(String propertySetId, String propertyId, Boolean isActive) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIP_PATH);
        PropertySetPropertyRelationshipDto propertySetPropertyRelationship = constructPropertySetPropertyRelationship(propertySetId, propertyId, isActive);
        return createEntity(propertySetPropertyRelationship);
    }

    public PropertySetPropertyRelationshipDto propertySetPropertyRelationshipIsCreated(String propertySetId, String propertyId, Boolean isActive) {
        Response response = createPropertySetPropertyRelationship(propertySetId, propertyId, isActive);
        assertThat(String.format("Failed to create PropertySetPropertyRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(PropertySetPropertyRelationshipDto.class);
    }

    public Response updatePropertySetPropertyRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIP_PATH);
        PropertySetPropertyRelationshipUpdateDto PropertySetPropertyRelationshipUpdate = new PropertySetPropertyRelationshipUpdateDto();
        PropertySetPropertyRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, PropertySetPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deletePropertySetPropertyRelationship(String relationshipId) {
        spec.basePath(PROPERTY_SET_PROPERTY_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    User Group Property Relationships

    public Response createUserGroupPropertyRelationship(String userGroupId, String propertyId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIP_PATH);
        UserGroupPropertyRelationshipDto UserGroupPropertyRelationship = constructUserGroupPropertyRelationship(userGroupId, propertyId, isActive);
        return createEntity(UserGroupPropertyRelationship);
    }

    public UserGroupPropertyRelationshipDto userGroupPropertyRelationshipIsCreated(String userGroupId, String propertyId, Boolean isActive) {
        Response response = createUserGroupPropertyRelationship(userGroupId, propertyId, isActive);
        assertThat(String.format("Failed to create UserGroupPropertyRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserGroupPropertyRelationshipDto.class);
    }

    public Response updateUserGroupPropertyRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIP_PATH);
        UserGroupPropertyRelationshipUpdateDto UserGroupPropertyRelationshipUpdate = new UserGroupPropertyRelationshipUpdateDto();
        UserGroupPropertyRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserGroupPropertyRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserGroupPropertyRelationship(String relationshipId) {
        spec.basePath(USER_GROUP_PROPERTY_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    User Property Set Relationships

    public Response createUserGroupPropertySetRelationship(String userGroupId, String propertyId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIP_PATH);
        UserGroupPropertySetRelationshipDto UserGroupPropertySetRelationship = constructUserGroupPropertySetRelationship(userGroupId, propertyId, isActive);
        return createEntity(UserGroupPropertySetRelationship);
    }

    public UserGroupPropertySetRelationshipDto userGroupPropertySetRelationshipIsCreated(String userGroupId, String propertyId, Boolean isActive) {
        Response response = createUserGroupPropertySetRelationship(userGroupId, propertyId, isActive);
        assertThat(String.format("Failed to create UserGroupPropertySetRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserGroupPropertySetRelationshipDto.class);
    }

    public Response updateUserGroupPropertySetRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIP_PATH);
        UserGroupPropertySetRelationshipUpdateDto UserGroupPropertySetRelationshipUpdate = new UserGroupPropertySetRelationshipUpdateDto();
        UserGroupPropertySetRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserGroupPropertySetRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserGroupPropertySetRelationship(String relationshipId) {
        spec.basePath(USER_GROUP_PROPERTY_SET_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }

    //    User Group User Relationships

    public Response createUserGroupUserRelationship(String userGroupId, String propertyId, Boolean isActive) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIP_PATH);
        UserGroupUserRelationshipDto UserGroupUserRelationship = constructUserGroupUserRelationship(userGroupId, propertyId, isActive);
        return createEntity(UserGroupUserRelationship);
    }

    public UserGroupUserRelationshipDto userGroupUserRelationshipIsCreated(String userGroupId, String propertyId, Boolean isActive) {
        Response response = createUserGroupUserRelationship(userGroupId, propertyId, isActive);
        assertThat(String.format("Failed to create UserGroupUserRelationship: %s", response.toString()), response.getStatusCode(), is(SC_CREATED));
        return response.as(UserGroupUserRelationshipDto.class);
    }

    public Response updateUserGroupUserRelationship(String relationshipId, Boolean isActive) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIP_PATH);
        UserGroupUserRelationshipUpdateDto UserGroupUserRelationshipUpdate = new UserGroupUserRelationshipUpdateDto();
        UserGroupUserRelationshipUpdate.setIsActive(isActive);
        return updateEntity(relationshipId, UserGroupUserRelationshipUpdate, getEntityEtag(relationshipId));
    }

    public Response deleteUserGroupUserRelationship(String relationshipId) {
        spec.basePath(USER_GROUP_USER_RELATIONSHIP_PATH);
        return deleteEntityWithEtag(relationshipId);
    }





    //    Help methods
    private UserCustomerRelationshipDto constructUserCustomerRelationshipDto(String userId, String customerId, Boolean isActive, Boolean isPrimary) {
        UserCustomerRelationshipDto userCustomerRelationship = new UserCustomerRelationshipDto();
        userCustomerRelationship.setCustomerId(customerId);
        userCustomerRelationship.setUserId(userId);
        userCustomerRelationship.setIsActive(isActive);
        userCustomerRelationship.setIsPrimary(isPrimary);
        return userCustomerRelationship;
    }

    private UserPartnerRelationshipDto constructUserPartnerRelationshipDto(String userId, String partnerId, Boolean isActive) {
        UserPartnerRelationshipDto userPartnerRelationship = new UserPartnerRelationshipDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPartnerId(partnerId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    private UserPropertyRelationshipDto constructUserPropertyRelationshipDto(String userId, String propertyId, Boolean isActive) {
        UserPropertyRelationshipDto userPartnerRelationship = new UserPropertyRelationshipDto();
        userPartnerRelationship.setUserId(userId);
        userPartnerRelationship.setPropertyId(propertyId);
        userPartnerRelationship.setIsActive(isActive);
        return userPartnerRelationship;
    }

    private UserPropertySetRelationshipDto constructUserPropertySetRelationshipDto(String userId, String propertySetId, Boolean isActive) {
        UserPropertySetRelationshipDto userPropertySetRelationship = new UserPropertySetRelationshipDto();
        userPropertySetRelationship.setUserId(userId);
        userPropertySetRelationship.setPropertySetId(propertySetId);
        userPropertySetRelationship.setIsActive(isActive);
        return userPropertySetRelationship;
    }

    private CustomerPropertyRelationshipDto constructCustomerPropertyRelationshipDto(String customerId, String propertyId, Boolean isActive,
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

    private PropertySetPropertyRelationshipDto constructPropertySetPropertyRelationship(String propertySetId, String propertyId, Boolean isActive){
        PropertySetPropertyRelationshipDto propertySetPropertyRelationship = new PropertySetPropertyRelationshipDto();
        propertySetPropertyRelationship.setPropertyId(propertyId);
        propertySetPropertyRelationship.setPropertySetId(propertySetId);
        propertySetPropertyRelationship.setIsActive(isActive);
        return propertySetPropertyRelationship;
    }

    private UserGroupUserRelationshipDto constructUserGroupUserRelationship(String userGroupId, String userId, Boolean isActive) {
        UserGroupUserRelationshipDto userGroupUserRelationship = new UserGroupUserRelationshipDto();
        userGroupUserRelationship.setUserGroupId(userGroupId);
        userGroupUserRelationship.setUserId(userId);
        userGroupUserRelationship.setIsActive(isActive);
        return userGroupUserRelationship;
    }

    private UserGroupPropertyRelationshipDto constructUserGroupPropertyRelationship(String userGroupId, String propertyId, Boolean isActive){
        UserGroupPropertyRelationshipDto userGroupPropertyRelationship = new UserGroupPropertyRelationshipDto();
        userGroupPropertyRelationship.setPropertyId(propertyId);
        userGroupPropertyRelationship.setUserGroupId(userGroupId);
        userGroupPropertyRelationship.setIsActive(isActive);
        return userGroupPropertyRelationship;
    }

    private UserGroupPropertySetRelationshipDto constructUserGroupPropertySetRelationship(String userGroupId, String propertySetId, Boolean isActive){
        UserGroupPropertySetRelationshipDto userGroupPropertySetRelationship = new UserGroupPropertySetRelationshipDto();
        userGroupPropertySetRelationship.setPropertySetId(propertySetId);
        userGroupPropertySetRelationship.setUserGroupId(userGroupId);
        userGroupPropertySetRelationship.setIsActive(isActive);
        return userGroupPropertySetRelationship;
    }

}
