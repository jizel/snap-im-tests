package travel.snapshot.dp.qa.serenity.properties;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
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
import travel.snapshot.dp.api.identity.model.PropertyCustomerRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.TtiCrossreferenceDto;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.DbUtilsSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

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

    public void followingPropertiesExist(List<PropertyDto> properties, String userId) {
        properties.forEach(property -> {
            property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
            if(property.getSalesforceId() == null){
                property.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
            }
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
    public void getPropertyByUserForApp(String userId, String applicationVersionId, String propertyId) {
        Response resp = getEntityByUserForApplication(userId, applicationVersionId, propertyId);
        setSessionResponse(resp);
    }

    @Step
    public void bodyContainsPropertyWith(String attributeName, String value) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(parseRawType(value)));
    }

    @Step
    public void getListOfPropertiesWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);

        // store to session
        setSessionResponse(response);
    }

    @Step
    public void getListOfPropertiesByUserForApp(String userId, String applicationVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, applicationVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void followingPropertyIsCreated(PropertyDto property, String userId) {
        property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
        Response response = createProperty(userId, property);
        setSessionResponse(response);
    }

    @Step
    public void createDefaultMinimalProperty(String propertyName, String userId, String customerId) {
        PropertyDto property = buildDefaultMinimalProperty(propertyName, customerId);
        followingPropertyIsCreated(property, userId);
    }

    @Step
    public void createDefaultMinimalPropertyWithAddress(String propertyName, String userId, String customerId, AddressDto address) {
        PropertyDto property = buildDefaultMinimalProperty(propertyName, customerId);
        followingPropertyIsCreatedWithAddress(property, address, userId);
    }

    @Step
    public void followingPropertyIsCreatedWithAddress(PropertyDto property, AddressDto address, String userId) {
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
                .body("name", is(originalProperty.getName()))
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
        Response response = null;
        try{
            JSONObject jsonProperty = retrieveData(property);
            response = createEntityByUser(userId, jsonProperty.toString());
        }catch (JsonProcessingException e){
            fail("Error while creating JSON object from propertyDto: " + e.toString());
        }
        return response;
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
        return getEntity(propertyId);
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
        updatePropertyByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, updatedProperty);
    }

    @Step
    public void updatePropertyByUserForApp(String userId, String applicationVersionId, String propertyId, PropertyUpdateDto updatedProperty){
        try {
            String updatedPropertyString = retrieveData(updatedProperty).toString();
            assertThat("Empty property update", updatedPropertyString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));

            String etag = getEntityEtag(propertyId);
            Response response = updateEntityByUserForApplication(userId, applicationVersionId, propertyId, updatedPropertyString, etag);
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
    public Response deletePropertyByUserForApp(String userId, String applicationVersionId, String propertyId) {
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

    public void removeAllUsersFromPropertiesWithCodes(List<String> propertyCodes) {
        propertyCodes.forEach(c -> {
            PropertyDto property = getPropertyByCodeInternal(c);
            Response customerUsersResponse = getSecondLevelEntities(property.getId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
            PartnerUserRelationshipDto[] propertyUsers = customerUsersResponse.as(PartnerUserRelationshipDto[].class);
            for (PartnerUserRelationshipDto pu : propertyUsers) {
                Response deleteResponse = deleteSecondLevelEntity(property.getId(), SECOND_LEVEL_OBJECT_USERS, pu.getUserId(), null);
                if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                    fail("User cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    public void relationExistsBetweenUserAndProperty(String userId, String propertyId, Boolean isActive) {
        addUserToProperty(userId, propertyId, isActive);
        Response response = getSessionResponse();
        if (response.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("Relation between user and property cannot be created - status: " + response.getStatusCode() + ", " + response.asString());
        }
    }

    public void addUserToProperty(String userId, String propertyId, Boolean isActive) {
        addUserToPropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertyId, isActive);
    }

    @Step
    public void addUserToPropertyByUserForApp(String performerId, String applicationVersionId, String userId, String propertyId, Boolean isActive) {
        if (isActive == null) {
            isActive = true;
        }
        PropertyUserRelationshipDto relation = new PropertyUserRelationshipDto();
        relation.setUserId(userId);
        relation.setIsActive(isActive);

        Response resp = createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_USERS, relation);
        setSessionResponse(resp);
    }

    @Step
    public PropertyUserRelationshipDto getUserForProperty(String propertyId, String userId) {
        Response customerUserResponse = getSecondLevelEntity(propertyId, SECOND_LEVEL_OBJECT_USERS, userId);
        if (customerUserResponse.statusCode() == HttpStatus.SC_OK) {
            return customerUserResponse.as(PropertyUserRelationshipDto.class);
        } else {
            return null;
        }
    }

    @Step
    public CustomerDto getCustomerForProperty(String propertyId, String customerId) {
        Response customerResponse = getSecondLevelEntity(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, customerId);
        return customerResponse.as(CustomerDto.class);
    }

    public void customerDoesNotExistForProperty(String customerId, String propertyCode) {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        CustomerDto cust = getCustomerForProperty(p.getId(), customerId);
        assertNull("Customer should not be link with property", cust);
    }

    public void userIsAddedToProperty(String userId, String propertyCode, Boolean isActive) {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        addUserToProperty(userId, p.getId(), isActive);
    }

    public void userIsDeletedFromProperty(String userId, String propertyId) {
      userIsDeletedFromPropertyByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertyId);
    }

    public void userIsDeletedFromPropertyByUserForApp(String performerId, String applicationVersionId, String userId, String propertyId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    public void propertySetIsDeletedFromPropertyByUserForApp(String userId, String applicationVersionId, String propertyId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, null);
        setSessionResponse(deleteResponse);
    }

    public void userDoesntExistForProperty(String userId, String propertyCode) {
        PropertyDto p = getPropertyByCodeInternal(propertyCode);
        PropertyUserRelationshipDto userForProperty = getUserForProperty(p.getId(), userId);
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
        return listOfPropertyUsersIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfPropertyUsersIsGotByUserForApp(String userId, String applicationVersionId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
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
        updatePropertyPropertySetRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, propertySetId, relationshipUpdate);
    }

    @Step
    public void updatePropertyPropertySetRelationshipByUserForApp(String userId, String applicationVersionId, String propertyId, String propertySetId, PropertySetPropertyRelationshipUpdateDto relationshipUpdate) {
        String etag = getSecondLevelEntityEtag(propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId);
        try {
            JSONObject jsonUpdate = retrieveData(relationshipUpdate);
            Response response = updateSecondLevelEntityByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }
    }

    @Step
    public void updatePropertyCustomerRelationshipByUserForApp(String userId, String applicationVersionId, String propertyId, String customerId, CustomerPropertyRelationshipUpdateDto relationshipUpdate) {
        PropertyCustomerRelationshipDto propertyCustomerRelation = getPropertyCustomerRelationship(customerId, propertyId);
        assertThat(propertyCustomerRelation, is(notNullValue()));
        String etag = getSecondLevelEntityEtag(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, propertyCustomerRelation.getId());
        try {
            JSONObject jsonUpdate = retrieveData(relationshipUpdate);
            Response response = updateSecondLevelEntityByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, propertyCustomerRelation.getId(), jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }
    }

    @Step
    public void deletePropertyCustomerRelationshipByUserForApp(String userId, String applicationVersionId, String propertyId, String customerId) {
        PropertyCustomerRelationshipDto propertyCustomerRelation = getPropertyCustomerRelationship(customerId, propertyId);
        assertThat(propertyCustomerRelation, is(notNullValue()));
        Response response = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, propertyCustomerRelation.getId(), null);
        setSessionResponse(response);
    }

    @Step
    public void deletePropertyUserRelationshipByUserForApp(String performerId, String applicationVersionId, String propertyId, String userId){
        Response response = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(response);
    }

    @Step
    public Response propertyPropertySetIsGot(String propertyId, String propertySetId) {
        return propertyPropertySetIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, propertySetId);
    }

    @Step
    public Response propertyPropertySetIsGotByUserForApp(String userId, String applicationVersionId, String propertyId, String propertySetId) {
        Response propertyPropertySet = getSecondLevelEntityByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, propertySetId);
        setSessionResponse(propertyPropertySet);
        return propertyPropertySet;
    }

    @Step
    public Response listOfPropertiesPropertySetsIsGot(String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfPropertiesPropertySetsIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfPropertiesPropertySetsIsGotByUserForApp(String userId, String applicationVersionId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void listOfCustomersIsGot(String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomersIsGotByUserForApp(String userId, String applicationVersionId, String propertyId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public PropertyCustomerRelationshipDto getPropertyCustomerRelationship(String customerId, String propertyId) {
        return getPropertyCustomerRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, customerId, propertyId);
    }

    public PropertyCustomerRelationshipDto getPropertyCustomerRelationshipByUser(String userId, String customerId, String propertyId) {
        String filter = String.format("customer_id==%s", customerId);
        PropertyCustomerRelationshipDto[] relations = getSecondLevelEntitiesByUser(userId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, null, null, filter, null, null, null).as(PropertyCustomerRelationshipDto[].class);
        return stream(relations).findFirst().orElse(null);
    }

    @Step
    public void requestPropertyCustomerRelationshipByUserForApp(String userId, String applicationVersionId, String propertyId, String customerId) {
        PropertyCustomerRelationshipDto propertyCustomerRelation = getPropertyCustomerRelationship(customerId, propertyId);
        assertThat(propertyCustomerRelation, is(notNullValue()));
        setSessionResponse(getSecondLevelEntityByUserForApp(userId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, propertyCustomerRelation.getId()));
    }

    public void allCustomersAreCustomersOfProperty(CustomerDto[] allCustomers, String propertyCode) {
        String propertyID = getPropertyByCodeInternal(propertyCode).getId();
        // This is slightly better but still does not work - listOfCustomersIsGotWith does not return list of customers but list of relations and needs to be changed
        for (CustomerDto c : allCustomers) {
            given().baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI)).basePath("identity/customers/").get(c.getId() + "/properties").
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
        Response response = createSecondLevelRelationshipByUser(userId, propertyId, SECOND_LEVEL_OBJECT_TTI, ttiCrossreference);
        setSessionResponse(response);
        return response;
    }

    public PropertyDto buildDefaultMinimalProperty(String propertyName, String customerId){
        PropertyDto property = new PropertyDto();
        property.setName(propertyName);
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

    public String resolvePropertyId(String propertyCode) {
        String propertyId;
        if (isUUID(propertyCode)) {
            propertyId = propertyCode;
        } else {
            PropertyDto property = getPropertyByCodeInternal(propertyCode);
            assertThat(String.format("Property with code \"%s\" does not exist", propertyCode), property, is(notNullValue()));
            propertyId = property.getId();
        }
        return propertyId;
    }

    public void listUsersForPropertyByUser(String userId, String propertyId) {
        listUsersForPropertyByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId);
    }

    public void listUsersForPropertyByUserForApp(String userId, String appId, String propertyId) {
        setSessionResponse(getSecondLevelEntitiesByUserForApp(userId, appId, propertyId, SECOND_LEVEL_OBJECT_USERS, null, null, null, null, null, null));
    }

    public void addPropertyToUserByUserForApp(String requestorId, String applicationVersionId, String propertyId, String targetUserId) {
        PropertyUserRelationshipDto relation = new PropertyUserRelationshipDto();
        relation.setUserId(targetUserId);
        setSessionResponse(createSecondLevelRelationshipByUserForApplication(requestorId, applicationVersionId, propertyId, SECOND_LEVEL_OBJECT_USERS, relation));
    }
}
