package travel.snapshot.dp.qa.cucumber.serenity.properties;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.apache.http.HttpStatus.SC_NO_CONTENT;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMER_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USER_PROPERTY_RELATIONSHIPS_PATH;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.UserPartnerRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertyRelationshipDto;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.qa.cucumber.helpers.AddressUtils;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author martin.konkol(at)snapshot.travel Created by Martin Konkol on 9/23/2015.
 */
public class PropertySteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTIES = "properties";
    private static final String SERENITY_SESSION__CREATED_PROPERTY = "created_property";
    private static final String SERENITY_SESSION__PROPERTY_ID = "property_id";
    private static final String BASE_PATH__PROPERTIES = "/properties";
    private static final String USER_ID_KEY = "user_id";
    private static final String PROPERTY_SET_ID_KEY = "property_set_id";

    public static final String DEFAULT_PROPERTY_EMAIL = "property1@snapshot.travel";
    public static final Boolean DEFAULT_PROPERTY_IS_DEMO = true;
    public static final String DEFAULT_PROPERTY_TIMEZONE = "Europe/Prague";

    public PropertySteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI)).basePath(BASE_PATH__PROPERTIES);
    }

    public void followingPropertiesExist(List<PropertyDto> properties, UUID userId) throws IOException {
        properties.forEach(property -> {
            property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
            if (property.getSalesforceId() == null) {
                property.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
            }
            entityIsCreatedByUser(userId, property);
        });
    }

    @Step
    public void getProperty(UUID id) {
        getEntity(id);
    }

    @Step
    public void getPropertyByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId) {
        Response resp = getEntityByUserForApplication(userId, applicationVersionId, propertyId);
        setSessionResponse(resp);
    }

    @Step
    public void followingPropertyIsCreatedByUser(UUID userId, PropertyDto property) throws IOException {
        property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
        entityIsCreatedByUser(userId, property);
    }

    @Step
    public void createDefaultMinimalPropertyWithAddressByUser(String propertyName, UUID userId, UUID customerId, AddressDto address) {
        PropertyDto property = buildDefaultMinimalProperty(propertyName, customerId);
        followingPropertyIsCreatedWithAddressByUser(property, address, userId);
    }

    @Step
    public void followingPropertyIsCreatedWithAddressByUser(PropertyDto property, AddressDto address, UUID userId) {
        property.setAddress(address);
        Response response = createPropertyByUser(userId, property);
        setSessionResponse(response);
    }

    @Step
    public void comparePropertyOnHeaderWithStored(String headerName) {
        PropertyDto originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY);
        Response response = getSessionResponse();
        String propertyLocation = response.header(headerName).replaceFirst(BASE_PATH__PROPERTIES, "");
        given().spec(spec).get(propertyLocation).then()
                .body("salesforce_id", is(originalProperty.getSalesforceId()))
                .body("name", is(originalProperty.getName()))
                .body("property_code", is(originalProperty.getCode()))
                .body("email", is(originalProperty.getEmail()));

    }


    /**
     * POST - new property object
     *
     * @param property property
     * @return server response
     */
    @Step
    public Response createPropertyByUser(UUID userId, PropertyDto property) {
        Response response = null;
        try {
            JSONObject jsonProperty = retrieveData(property);
            response = createEntityByUser(userId, jsonProperty.toString());
        } catch (JsonProcessingException e) {
            fail("Error while creating JSON object from propertyDto: " + e.toString());
        }
        return response;
    }

    @Step
    public void updateProperty(UUID propertyId, PropertyUpdateDto updatedProperty) {
        updatePropertyByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, updatedProperty);
    }

    @Step
    public void updatePropertyByUser(UUID userId, UUID propertyId, PropertyUpdateDto updatedProperty) {
        updatePropertyByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, updatedProperty);
    }

    @Step
    public void updatePropertyByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, PropertyUpdateDto updatedProperty) {
        try {
            String updatedPropertyString = retrieveData(updatedProperty).toString();
            assertThat("Empty property update", updatedPropertyString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));
            String etag = getEntityEtag(propertyId);

            updateEntityByUserForApplication(userId, applicationVersionId, propertyId, updatedPropertyString, etag);
        } catch (JsonProcessingException jsonException) {
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    /**
     * DELETE - remove property object
     *
     * @param id ID of the property
     * @return server response
     */
    @Step
    public Response deleteProperty(UUID id) {
        deleteEntityWithEtag(id);
        return getSessionResponse();
    }

    @Step
    public Response deletePropertyByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId) {
        setSessionVariable(SERENITY_SESSION__PROPERTY_ID, propertyId);
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, propertyId);
        return getSessionResponse();
    }

    /**
     * GET - single property filtered by code from a list of properties
     *
     * @param code property code
     * @return Requested property or {@code null} if no such property exists in the list
     */
    @Step
    public PropertyDto getPropertyByCodeInternal(String code) {
        PropertyDto[] properties = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "property_code=='" + code + "'", null, null, null).as(PropertyDto[].class);
        return stream(properties).findFirst().orElse(null);
    }


    public void relationExistsBetweenUserAndProperty(UUID userId, UUID propertyId, Boolean isActive) {
        addUserToProperty(userId, propertyId, isActive);
        Response response = getSessionResponse();
        if (response.getStatusCode() != SC_CREATED) {
            fail("Relation between user and property cannot be created - status: " + response.getStatusCode() + ", " + response.asString());
        }
    }

    public void addUserToProperty(UUID userId, UUID propertyId, Boolean isActive) {
        addUserToPropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertyId, isActive);
    }

    @Step
    public void addUserToPropertyByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID propertyId, Boolean isActive) {
        if (isActive == null) {
            isActive = true;
        }
        UserPropertyRelationshipDto relation = new UserPropertyRelationshipDto();
        relation.setUserId(userId);
        relation.setIsActive(isActive);

        createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, propertyId, USERS_PATH, relation);
    }

    @Step
    public UserPropertyRelationshipDto getUserForProperty(UUID propertyId, UUID userId) throws IOException {
        return stream(getEntities(USER_PROPERTY_RELATIONSHIPS_PATH, null, null, String.format("property_id==%s&user_id==%s", propertyId, userId), null, null, null).as(UserPropertyRelationshipDto[].class)).findFirst().orElse(null);
    }

    public void userIsDeletedFromProperty(UUID userId, UUID propertyId) throws Throwable {
        userIsDeletedFromPropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertyId);

    }

    public void userIsDeletedFromPropertyByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID propertyId) throws Throwable {
        UserPropertyRelationshipDto[] relations = getEntitiesByUserForApp(performerId, applicationVersionId, USER_PROPERTY_RELATIONSHIPS_PATH, null, null, String.format("property_id==%s&user_id==%s", propertyId, userId), null, null, null).as(UserPropertyRelationshipDto[].class);
        UUID relationId = stream(relations).findFirst().orElse(null).getId();
        deleteEntityWithEtagByUserForApp(performerId, applicationVersionId, relationId);
        responseCodeIs(SC_NO_CONTENT);
    }

    public void propertySetIsDeletedFromPropertyByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, propertyId, PROPERTY_SETS_RESOURCE, propertySetId, null);
        setSessionResponse(deleteResponse);
    }

    public void userDoesntExistForProperty(UUID userId, String propertyCode) throws IOException {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        UserPropertyRelationshipDto userForProperty = getUserForProperty(p.getId(), userId);
        assertNull("User should not be present in property", userForProperty);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        UserPartnerRelationshipPartialDto[] propertiesUsers = response.as(UserPartnerRelationshipPartialDto[].class);
        int i = 0;
        for (UserPartnerRelationshipPartialDto pu : propertiesUsers) {
            //            userName is not part of new class - UserPartnerRelationshipPartialDto, needs to be obtained via different endpoint
            //            assertEquals("Propertyuser on index=" + i + " is not expected", usernames.get(i), pu.getUserName());
            i++;
        }
    }

    @Step
    public void listOfUsersIsGotWith(UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) throws Throwable {
        listOfPropertyUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public void listOfPropertyUsersIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) throws Throwable {
        getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertyId,
                USERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
    }

    @Step
    public void setPropertyIsActive(UUID propertyId, Boolean isActive) {
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setIsActive(isActive);
        updateProperty(propertyId, propertyUpdate);
    }

    @Step
    public void updatePropertyAddress(UUID propertyId, AddressDto addressUpdate) {
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setAddress(addressUpdate);
        updateProperty(propertyId, propertyUpdate);
    }

    @Step
    public void updatePropertyPropertySetRelationship(UUID propertyId, UUID propertySetId, PropertySetPropertyRelationshipUpdateDto relationshipUpdate) throws Throwable {
        updatePropertyPropertySetRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, propertySetId, relationshipUpdate);
    }

    @Step
    public void updatePropertyPropertySetRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID propertySetId, PropertySetPropertyRelationshipUpdateDto relationshipUpdate) throws Throwable {

        PropertySetPropertyRelationshipDto[] relations = getEntities(PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, null, null, String.format("property_id==%s&property_set_id==%s", propertyId, propertySetId), null, null, null).as(PropertySetPropertyRelationshipDto[].class);
        UUID relationId = stream(relations).findFirst().orElse(null).getId();
        String etag = getEntityEtag(relationId);
        try {
            JSONObject jsonUpdate = retrieveData(relationshipUpdate);
            updateEntityByUserForApplication(userId, applicationVersionId, relationId, jsonUpdate, etag);
        } catch (JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " + e);
        }
    }

    @Step
    public void updatePropertyCustomerRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID customerId, CustomerPropertyRelationshipUpdateDto relationshipUpdate) throws IOException {
        CustomerPropertyRelationshipDto[] relations = getEntitiesByUserForApp(userId, applicationVersionId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, null, null, String.format("customer_id==%s&property_id==%s", customerId, propertyId), null, null, null).as(CustomerPropertyRelationshipDto[].class);
        assertThat("Property-Customer relationship does not exists", relations, is(notNullValue()));
        UUID relationId = stream(relations).findFirst().orElse(null).getId();
        String etag = getEntityEtag(relationId);
        try {
            JSONObject jsonUpdate = retrieveData(relationshipUpdate);
            Response response = updateEntityByUserForApplication(userId, applicationVersionId, relationId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch (JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " + e);
        }
    }

    @Step
    public void deletePropertyCustomerRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID customerId) throws Throwable {
        CustomerPropertyRelationshipDto[] propertyCustomerRelations = getPropertyCustomerRelationships(customerId, propertyId);
        assertThat(propertyCustomerRelations, is(notNullValue()));
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, stream(propertyCustomerRelations).findFirst().orElse(null).getId());
    }

    @Step
    public void deletePropertyUserRelationshipByUserForApp(UUID performerId, UUID applicationVersionId, UUID propertyId, UUID userId) throws Throwable {
        UserPropertyRelationshipDto relation = stream(getEntities(USER_PROPERTY_RELATIONSHIPS_PATH, null, null, String.format("user_id==%s&property_id==%s", userId, propertyId), null, null, null).as(UserPropertyRelationshipDto[].class)).
                findFirst().orElse(null);
        UUID relationId = relation.getId();
        deleteEntityWithEtagByUserForApp(performerId, applicationVersionId, relationId);
    }

    @Step
    public void propertyPropertySetIsGot(UUID propertyId, UUID propertySetId) throws Throwable {
        propertyPropertySetIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, propertySetId);
    }

    @Step
    public void propertyPropertySetIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID propertySetId) throws Throwable {
        getEntitiesByUserForApp(userId, applicationVersionId, PROPERTY_SET_PROPERTY_RELATIONSHIPS_PATH, null, null, String.format("property_id==%s&property_set_id==%s", propertyId, propertySetId), null, null,   null);
    }

    @Step
    public void listOfPropertiesPropertySetsIsGot(UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) throws Throwable {
        listOfPropertiesPropertySetsIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public void listOfPropertiesPropertySetsIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) throws Throwable {
        getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertyId,
                PROPERTY_SETS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
    }

    @Step
    public void listOfCustomersIsGot(UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) throws Throwable {
        Map<String, String> queryParameters = buildQueryParamMapForPaging(limit, cursor, String.format("property_id==%s&%s", propertyId, filter), sort, sortDesc, null);
        getEntities(CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, null, null, null, null, null,   queryParameters);
    }

    @Step
    public void listOfCustomersIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, String limit, String cursor, String filter, String sort, String sortDesc) throws Throwable {
        getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertyId,
                CUSTOMERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
    }

    public CustomerPropertyRelationshipDto[] getPropertyCustomerRelationships(UUID customerId, UUID propertyId) throws Throwable {
        return getPropertyCustomerRelationshipsByUser(DEFAULT_SNAPSHOT_USER_ID, customerId, propertyId);
    }

    public CustomerPropertyRelationshipDto[] getPropertyCustomerRelationshipsByUser(UUID userId, UUID customerId, UUID propertyId) throws Throwable {
        return getPropertyCustomerRelationshipsByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customerId, propertyId);
    }

    public CustomerPropertyRelationshipDto[] getPropertyCustomerRelationshipsByUserForApp(UUID userId, UUID appVersionId, UUID customerId, UUID propertyId) throws Throwable {

        Map<String, String> queryParams = buildQueryParamMapForPaging(null, null, String.format("customer_id==%s&property_id==%s", customerId, propertyId), null, null, null);
        return getEntitiesByUserForApp(userId, appVersionId, CUSTOMER_PROPERTY_RELATIONSHIPS_PATH, null, null, null, null, null,  queryParams)
                .as(CustomerPropertyRelationshipDto[].class);
    }


//  Tti functionality temporarily removed. Keeping this code in case it comes back
//    public Response assignTtiToProperty(UUID propertyId, TtiCrossreferenceDto ttiCrossreference) {
//      return assignTtiToPropertyByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId,ttiCrossreference);
//    }
//
//    public Response assignTtiToPropertyByUser(UUID userId, UUID propertyId, TtiCrossreferenceDto ttiCrossreference) {
//        Response response = createSecondLevelRelationshipByUser(userId, propertyId, SECOND_LEVEL_OBJECT_TTI, ttiCrossreference);
//        setSessionResponse(response);
//        return response;
//    }

    public PropertyDto buildDefaultMinimalProperty(String propertyName, UUID customerId){
        PropertyDto property = new PropertyDto();
        property.setName(propertyName);
        property.setCustomerId(customerId);
        property.setEmail(DEFAULT_PROPERTY_EMAIL);
        property.setTimezone(DEFAULT_PROPERTY_TIMEZONE);
        property.setIsDemo(DEFAULT_PROPERTY_IS_DEMO);
        return property;
    }

//    Internal help methods

    /**
     * Parses provided string for predefined prefixes, returning a Java type where applicable.
     *
     * @param value original string value
     * @return Java type, if a matching prefix is found
     */
    private Object parseRawType(String value) {
        int position = value.indexOf(':');
        if (position > -1) {
            String prefix = value.substring(0, position);
            String postfix = value.substring(position + 1);

            switch (prefix) {
                case "java.lang.Boolean": {
                    return Boolean.valueOf(postfix);
                }

                default: {
                    return value;
                }
            }
        }

        return value;
    }

    public UUID resolvePropertyId(String propertyCode) {
        UUID propertyId;
        if (isUUID(propertyCode)) {
            propertyId = UUID.fromString(propertyCode);
        } else {
            PropertyDto property = getPropertyByCodeInternal(propertyCode);
            assertThat(String.format("Property with code \"%s\" does not exist", propertyCode), property, is(notNullValue()));
            propertyId = property.getId();
        }
        return propertyId;
    }

    public void listUsersForPropertyByUserForApp(UUID userId, UUID appId, UUID propertyId) throws Throwable {
        Map<String, String> queryParams = buildQueryParamMapForPaging(null, null, String.format("property_id==%s", propertyId), null, null, null);
        getEntitiesByUserForApp(userId, appId, USER_PROPERTY_RELATIONSHIPS_PATH, null, null, null, null, null, queryParams);
    }

    public void addPropertyToUserByUserForApp(UUID requestorId, UUID applicationVersionId, UUID propertyId, UUID targetUserId) {
        UserPropertyRelationshipDto relation = new UserPropertyRelationshipDto();
        relation.setUserId(targetUserId);
        relation.setPropertyId(propertyId);
        createEntityByUserForApplication(requestorId, applicationVersionId, relation);
    }
}
