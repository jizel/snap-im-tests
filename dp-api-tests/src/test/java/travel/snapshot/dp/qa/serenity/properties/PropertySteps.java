package travel.snapshot.dp.qa.serenity.properties;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.AddressUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PartnerUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyCreateDto;
import travel.snapshot.dp.api.identity.model.PropertyCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.TtiCrossreferenceDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author martin.konkol(at)snapshot.travel Created by Martin Konkol on 9/23/2015.
 */
public class PropertySteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTIES = "properties";
    private static final String SERENITY_SESSION__CREATED_PROPERTY = "created_property";
    private static final String SERENITY_SESSION__PROPERTY_ID = "property_id";
    private static final String BASE_PATH__PROPERTIES = "/identity/properties";
    private static final String USER_ID_KEY = "user_id";
    private static final String PROPERTY_SET_ID_KEY = "property_set_id";

    public static final String DEFAULT_PROPERTY_EMAIL = "property1@snapshot.travel";
    public static final Boolean DEFAULT_PROPERTY_IS_DEMO = true;
    public static final String DEFAULT_PROPERTY_TIMEZONE = "Europe/Prague";

    @Steps
    private DbUtilsSteps dbSteps;

    @Steps
    private UsersSteps usersSteps;

    public PropertySteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI)).basePath(BASE_PATH__PROPERTIES);
    }

    public void followingPropertiesExist(List<PropertyCreateDto> properties, String userName) {
        String userId = usersSteps.resolveUserId(userName);
        properties.forEach(property -> {
            property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));

            Response createResponse = createProperty(userId, property);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property cannot be created! Status:" + createResponse.getStatusCode() + " " + createResponse.body().asString());
            }
        });
    }

    @Step
    public void getProperty(String id) {
        Response resp = getProperty(id, null);
        setSessionResponse(resp);
    }

    @Step
    public void getPropertyByUser(String userId, String propertyId) {
        Response resp = getEntityByUser(userId, propertyId);
        setSessionResponse(resp);
    }

    @Step
    public void bodyContainsPropertyWith(String atributeName, String value) {
        Response response = getSessionResponse();
        response.then().body(atributeName, is(parseRawType(value)));
    }

    @Step
    public void getPropertyWithCodeUsingEtagAfterUpdate(String code) {
        PropertyDto propertyFromList = getPropertyByCodeInternal(code);
        if (propertyFromList == null) {
            fail("No matching property with code: [" + code + "] found.");
        }

        // we first need to get current ETag of a property
        Response responseWithETag = getProperty(propertyFromList.getPropertyId(), null);

        // force new ETag on server side
        Response updateResponse = updateProperty(
                propertyFromList.getPropertyId(),
                Collections.singletonMap("website", "http://changed.it"),
                responseWithETag.getHeader(HEADER_ETAG));
        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Property cannot be updated: " + updateResponse.asString());
        }

        // get with old ETag
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader(HEADER_ETAG));

        // store to session
        setSessionResponse(resp);
    }

    @Step
    public void getListOfPropertiesWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);

        // store to session
        setSessionResponse(response);
    }

    @Step
    public void getListOfPropertiesByUserWith(String userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUser(userId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void followingPropertyIsCreated(PropertyCreateDto property, String userId) {
        property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
        Response response = createProperty(userId, property);
        setSessionResponse(response);
    }

    @Step
    public void createDefaultMinimalProperty(String propertyName, String userId, String customerId) {
        PropertyCreateDto property = buildDefaultMinimalProperty(propertyName, customerId);
        followingPropertyIsCreated(property, userId);
    }

    @Step
    public void createDefaultMinimalPropertyWithAddress(String propertyName, String userId, String customerId, AddressDto address) {
        PropertyCreateDto property = buildDefaultMinimalProperty(propertyName, customerId);
        followingPropertyIsCreatedWithAddress(property, address, userId);
    }

    @Step
    public void followingPropertyIsCreatedWithAddress(PropertyCreateDto property, AddressDto address, String userId) {
        property.setAddress(address);

        Response response = createProperty(userId, property);
        setSessionResponse(response);
    }

    @Step
    public void comparePropertyOnHeaderWithStored(String headerName) {
        PropertyDto originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY);
        Response response = getSessionResponse();
        String propertyLocation = response.header(headerName).replaceFirst(BASE_PATH__PROPERTIES, "");
        given().spec(spec).get(propertyLocation).then()
                .body("salesforce_id", is(originalProperty.getSalesforceId()))
                .body("name", is(originalProperty.getPropertyName()))
                .body("property_code", is(originalProperty.getPropertyCode()))
                .body("email", is(originalProperty.getEmail()));

    }

    @Step
    public void propertyIdInSessionDoesntExist() {
        String propertyId = Serenity.sessionVariableCalled(SERENITY_SESSION__PROPERTY_ID);

        Response response = getProperty(propertyId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }


    /**
     * POST - new property object
     *
     * @param property property
     * @return server response
     */
    @Step
    public Response createProperty(String userId, PropertyDto property) {
        return createEntityByUser(userId, property);
    }

    /**
     * GET - property object by ID
     *
     * @param propertyId ID of the property object
     * @param etag ETag version stamp
     * @return server response
     */
    @Step
    public Response getProperty(String propertyId, String etag) {
        return getEntity(propertyId, etag);
    }

    /**
     * POST - update property object
     *
     * @param id     ID of the property
     * @param fields updated property field values
     * @param etag   ETag version stamp
     * @return server response
     */
    @Step
    public Response updateProperty(String id, Map<String, Object> fields, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (etag != null && !etag.isEmpty()) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(fields).when().post("/{id}", id);
    }

    @Step
    public void updateProperty(String propertyId, PropertyUpdateDto updatedProperty){
        updatePropertyByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, updatedProperty);
    }

    @Step
    public void updatePropertyByUser(String userId, String propertyId, PropertyUpdateDto updatedProperty){
        try {
            String updatedPropertyString = retrieveData(updatedProperty).toString();
            assertThat("Empty property update", updatedPropertyString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));

            Response response = updateEntityWithEtagByUser(userId, propertyId, updatedPropertyString);
            setSessionResponse(response);
        }catch(JsonProcessingException jsonException){
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
    public Response deleteProperty(String id) {
        deleteEntityWithEtag(id);
        return getSessionResponse();
    }

    @Step
    public Response deletePropertyByUser(String userId, String propertyId) {
        deleteEntityWithEtagByUser(userId, propertyId);
        return getSessionResponse();
    }


    /**
     * GET - list of property objects
     *
     * @param limit  maximum amount of properties
     * @param cursor offset in the available list of properties
     * @return server response
     */
    private Response getProperties(String limit, String cursor) {
        RequestSpecification requestSpecification = given().spec(spec)
                .basePath(BASE_PATH__PROPERTIES);

        if (cursor != null && !"".equals(cursor)) {
            requestSpecification.parameter("cursor", cursor);
        }
        if (limit != null && !"".equals(limit)) {
            requestSpecification.parameter("limit", limit);
        }
        return requestSpecification.when().get();
    }

    /**
     * GET - single property filtered by code from a list of properties
     *
     * @param code property code
     * @return Requested property or {@code null} if no such property exists in the list
     */
    @Step
    public PropertyDto getPropertyByCodeInternal(String code) {
        PropertyDto[] properties = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "property_code==" + code, null, null, null).as(PropertyDto[].class);
        return stream(properties).findFirst().orElse(null);
    }

    @Step
    public PropertyDto getPropertyByCodeInternalByUser(String userId, String code) {
        PropertyDto[] properties = getEntitiesByUser(userId, null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "property_code==" + code, null, null, null).as(PropertyDto[].class);
        return stream(properties).findFirst().orElse(null);
    }

    @Step
    public PropertyDto getPropertyByName(String propertyName) {
        PropertyDto[] properties = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, String.format("name=='%s'", propertyName), null, null, null).as(PropertyDto[].class);
        return stream(properties).findFirst().orElse(null);
    }


    public void removeAllUsersFromPropertiesWithCodes(List<String> propertyCodes) {
        propertyCodes.forEach(c -> {
            PropertyDto property = getPropertyByCodeInternal(c);
            Response customerUsersResponse = getSecondLevelEntities(property.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
            PartnerUserRelationshipDto[] propertyUsers = customerUsersResponse.as(PartnerUserRelationshipDto[].class);
            for (PartnerUserRelationshipDto pu : propertyUsers) {
                Response deleteResponse = deleteSecondLevelEntity(property.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, pu.getUserId(), null);
                if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                    fail("User cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    public void relationExistsBetweenUserAndProperty(String userId, String propertyId) {
        PropertyUserRelationshipDto existingPropertyUser = getUserForProperty(propertyId, DEFAULT_SNAPSHOT_USER_ID);
        if (existingPropertyUser != null) {
            // Delete second level entities does not work for properties/users because the endpoint is not implemented. Using DB delete instead.
            dbSteps.deletePropertyUserFromDb(userId, propertyId);
        }
        Response createResponse = addUserToProperty(userId, propertyId);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("PropertyUser cannot be created - status: " + createResponse.getStatusCode() + ", " + createResponse.asString());
        }
    }

    public Response addUserToProperty(String userId, String propertyId) {
//        Needs to be done as default snapshot user or this method wouldn't be able to add any (nonsnapshot) user that is not already added
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID)
                .body(singletonMap(USER_ID_KEY, userId))
                .when().post("/{propertyId}/users", propertyId);
    }

    @Step
    public Response addUserToPropertyByUser(String userId, String propertyId, String performerId) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, performerId)
                .body(singletonMap(USER_ID_KEY, userId))
                .when().post("/{propertyId}/users", propertyId);
    }

    @Step
    public PropertyUserRelationshipDto getUserForProperty(String propertyId, String userId) {
        Response customerUserResponse = getSecondLevelEntitiesByUser(userId, propertyId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_id==" + userId, null, null, null);
        return stream(customerUserResponse.as(PropertyUserRelationshipDto[].class)).findFirst().orElse(null);
    }

    @Step
    public CustomerDto getCustomerForProperty(String propertyId, String customerId) {
        Response customerResponse = getSecondLevelEntities(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + customerId, null, null, null);
        return stream(customerResponse.as(CustomerDto[].class)).findFirst().orElse(null);
    }

    public void customerDoesNotExistForProperty(String customerId, String propertyCode) {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        CustomerDto cust = getCustomerForProperty(p.getPropertyId(), customerId);
        assertNull("Customer should not be link with property", cust);
    }

    public void userIsAddedToProperty(String userId, String propertyCode) {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        Response response = addUserToProperty(userId, p.getPropertyId());
        setSessionResponse(response);
    }

    public void userIsDeletedFromProperty(String userId, String propertyId) {
      userIsDeletedFromPropertyByUser(DEFAULT_SNAPSHOT_USER_ID, userId, propertyId);
    }

    public void userIsDeletedFromPropertyByUser(String performerId, String userId, String propertyId) {
        Response deleteResponse = deleteSecondLevelEntityByUser(performerId, propertyId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    public void propertySetIsDeletedFromPropertyByUser(String userId, String propertyId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUser(userId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(deleteResponse);
    }

    public void userDoesntExistForProperty(String userId, String propertyCode) {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        PropertyUserRelationshipDto userForProperty = getUserForProperty(p.getPropertyId(), userId);
        assertNull("User should not be present in property", userForProperty);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        PartnerUserRelationshipDto[] propertiesUsers = response.as(PartnerUserRelationshipDto[].class);
        int i = 0;
        for (PartnerUserRelationshipDto pu : propertiesUsers) {
//            userName is not part of new class - PartnerUserRelationshipDto, needs to be obtained via different endpoint
//            assertEquals("Propertyuser on index=" + i + " is not expected", usernames.get(i), pu.getUserName());
            i++;
        }
    }

    @Step
    public Response listOfUsersIsGotWith(String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfUsersIsGotByUserWith(DEFAULT_SNAPSHOT_USER_ID, propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfUsersIsGotByUserWith(String userId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, propertyId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return  response;
    }

    @Step
    public void bodyContainsEntityWith(String attributeName, String attributeValue) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(Integer.valueOf(attributeValue)));
    }

    @Step
    public void setPropertyIsActive(String propertyId, Boolean isActive) {
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setIsActive(isActive);
        updateProperty(propertyId, propertyUpdate);
    }

    @Step
    public void updatePropertyAddress(String propertyId, AddressUpdateDto addressUpdate) {
        PropertyUpdateDto propertyUpdate = new PropertyUpdateDto();
        propertyUpdate.setAddress(addressUpdate);
        updateProperty(propertyId, propertyUpdate);
    }

    @Step
    public void updatePropertyPropertySetRelationship(String propertyId, String propertySetId, PropertySetPropertyRelationshipUpdateDto relationshipUpdate) {
        updatePropertyPropertySetRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, propertySetId, relationshipUpdate);
    }

    @Step
    public void updatePropertyPropertySetRelationshipByUser(String userId, String propertyId, String propertySetId, PropertySetPropertyRelationshipUpdateDto relationshipUpdate) {
        String etag = getSecondLevelEntityEtag(propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId);
        try {
            JSONObject jsonUpdate = retrieveData(relationshipUpdate);
            Response response = updateSecondLevelEntityByUser(userId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }
    }

    @Step
    public void updatePropertyCustomerRelationshipByUser(String userId, String propertyId, String customerId, CustomerPropertyRelationshipUpdateDto relationshipUpdate) {
        PropertyCustomerRelationshipDto customerPropertyRelationship = getPropertyCustomerRelationship(propertyId, customerId);
        assertThat(customerPropertyRelationship, is(notNullValue()));
        String etag = getSecondLevelEntityEtag(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, customerPropertyRelationship.getRelationshipId());
        try {
            JSONObject jsonUpdate = retrieveData(relationshipUpdate);
            Response response = updateSecondLevelEntityByUser(userId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, customerPropertyRelationship.getRelationshipId(), jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }
    }

    @Step
    public void deletePropertyCustomerRelationshipByUser(String userId, String propertyId, String customerId) {
        PropertyCustomerRelationshipDto customerPropertyRelationship = getPropertyCustomerRelationship(propertyId, customerId);
        assertThat(customerPropertyRelationship, is(notNullValue()));

        Response response = deleteSecondLevelEntityByUser(userId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, customerPropertyRelationship.getRelationshipId(), null);
        setSessionResponse(response);
    }

    @Step
    public Response propertyPropertySetIsGot(String propertyId, String propertySetId) {
        return propertyPropertySetIsGotByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, propertySetId);
    }

    @Step
    public Response propertyPropertySetIsGotByUser(String userId, String propertyId, String propertySetId) {
        Response propertyPropertySet = getSecondLevelEntityByUser(userId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(propertyPropertySet);
        return propertyPropertySet;
    }

    @Step
    public Response listOfPropertiesPropertySetsIsGot(String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfPropertiesPropertySetsIsGotByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfPropertiesPropertySetsIsGotByUser(String userId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void listOfCustomersIsGot(String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomersIsGotByUser(String userId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public PropertyCustomerRelationshipDto getPropertyCustomerRelationship(String propertyId, String customerId) {
        return getPropertyCustomerRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, customerId);
    }

    @Step
    public PropertyCustomerRelationshipDto getPropertyCustomerRelationshipByUser(String userId, String propertyId, String customerId) {
        String filter = String.format("customer_id==%s", customerId);
        PropertyCustomerRelationshipDto[] customerProperties = getSecondLevelEntitiesByUser(userId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertyCustomerRelationshipDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    public void allCustomersAreCustomersOfProperty(CustomerDto[] allCustomers, String propertyCode) {
        String propertyID = getPropertyByCodeInternal(propertyCode).getPropertyId();
        // This is slightly better but still does not work - listOfCustomersIsGotWith does not return list of customers but list of relations and needs to be changed
        for (CustomerDto c : allCustomers) {
            given().baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI)).basePath("identity/customers/").get(c.getCustomerId() + "/properties").
                    then().body("property_id", hasItem(propertyID));
        }
    }

    public void listOfApiSubscriptionsIsGot(String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response resp = getSecondLevelEntities(propertyId, SECOND_LEVEL_OBJECT_API_SUBSCRIPTION, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(resp);
    }


    public Response assignTtiToProperty(String propertyId, TtiCrossreferenceDto ttiCrossreference) {
      return assignTtiToPropertyByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId,ttiCrossreference);
    }

    public Response assignTtiToPropertyByUser(String userId, String propertyId, TtiCrossreferenceDto ttiCrossreference) {
        Response response = given().spec(spec).header(HEADER_XAUTH_USER_ID, userId).body(ttiCrossreference).when().post(propertyId + "/tti");
        setSessionResponse(response);
        return response;
    }

    public PropertyCreateDto buildDefaultMinimalProperty(String propertyName, String customerId){
        PropertyCreateDto property = new PropertyCreateDto();
        property.setPropertyName(propertyName);
        property.setAnchorCustomerId(customerId);
        property.setEmail(DEFAULT_PROPERTY_EMAIL);
        property.setTimezone(DEFAULT_PROPERTY_TIMEZONE);
        property.setIsDemoProperty(DEFAULT_PROPERTY_IS_DEMO);
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

}
