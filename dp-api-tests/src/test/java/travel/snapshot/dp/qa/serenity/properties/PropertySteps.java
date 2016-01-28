package travel.snapshot.dp.qa.serenity.properties;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.Property;
import travel.snapshot.dp.qa.model.PropertyUser;
import travel.snapshot.dp.qa.model.User;
import travel.snapshot.dp.qa.model.Address;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author martin.konkol(at)snapshot.travel Created by Martin Konkol on 9/23/2015.
 */
public class PropertySteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTIES = "properties";
    private static final String SERENITY_SESSION__CREATED_PROPERTY = "created_property";
    private static final String SERENITY_SESSION__PROPERTY_ID = "property_id";

    private static final String BASE_PATH__PROPERTIES = "/identity/properties";

    public PropertySteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI)).basePath(BASE_PATH__PROPERTIES);
    }

    // --- steps ---

    @Step
    public void followingPropertiesExist(List<Property> properties) {
        properties.forEach(t -> {
            // remove duplicates
            Property existingProperty = getPropertyByCodeInternal(t.getPropertyCode());
            if (existingProperty != null) {
                deleteProperty(existingProperty.getPropertyId());
            }

            // introduce new records
            t.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
            Response createResponse = createProperty(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property cannot be created: " + createResponse.asString());
            }
        });
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTIES).to(properties);
    }

    @Step
    public void getPropertyByID(String id) {
        Response resp = getProperty(id, null);

        // store to session
        setSessionResponse(resp);
    }

    @Step
    public void getPropertyByCode(String code) {
        Property customerFromList = getPropertyByCodeInternal(code);
        Response resp = getProperty(customerFromList.getPropertyId(), null);

        // store to session
        setSessionResponse(resp);
    }

    @Step
    public void bodyContainsPropertyWith(String atributeName, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, is(parseRawType(value)));
    }

    @Step
    public void getPropertyByCodeUsingEtag(String code) {
        Property propertyFromList = getPropertyByCodeInternal(code);
        if (propertyFromList == null) {
            fail("No matching property with code: [" + code + "] found.");
        }

        // we first need to get current ETag of a property
        Response responseWithETag = getProperty(propertyFromList.getPropertyId(), null);

        // try to get the property with current ETag
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader(HEADER_ETAG));

        // store to session
        setSessionResponse(resp);
    }

    @Step
    public void getPropertyWithCodeUsingEtagAfterUpdate(String code) {
        Property propertyFromList = getPropertyByCodeInternal(code);
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
    public void listOfPropertiesExistsWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);

        // store to session
        setSessionResponse(response);
    }

    @Step
    public void followingPropertyIsCreated(Property property) {
        property.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
        Serenity.setSessionVariable(SERENITY_SESSION__CREATED_PROPERTY).to(property);
        Property existingProperty = getPropertyByCodeInternal(property.getPropertyCode());
        if (existingProperty != null) {
            deleteProperty(existingProperty.getPropertyId());
        }
        Response response = createProperty(property);
        setSessionResponse(response);
    }

    @Step
    public void followingPropertyIsCreatedWithAddress(Property property, Address address) {
    	property.setAddress(address);
        Serenity.setSessionVariable(SERENITY_SESSION__CREATED_PROPERTY).to(property);
        Property existingProperty = getPropertyByCodeInternal(property.getPropertyCode());
        if (existingProperty != null) {
            deleteProperty(existingProperty.getPropertyId());
        }
        Response response = createProperty(property);
        setSessionResponse(response);
    }

    @Step
    public void comparePropertyOnHeaderWithStored(String headerName) {
        Property originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY);
        Response response = getSessionResponse();
        String propertyLocation = response.header(headerName).replaceFirst(BASE_PATH__PROPERTIES, "");
        given().spec(spec).get(propertyLocation).then()
                .body("salesforce_id", is(originalProperty.getSalesforceId()))
                .body("property_name", is(originalProperty.getPropertyName()))
                .body("property_code", is(originalProperty.getPropertyCode()))
                .body("email", is(originalProperty.getEmail()));

    }

    @Step
    public void deletePropertyWithCode(String code) {
        Property p = getPropertyByCodeInternal(code);
        if (p == null) {
            return;
        }
        String propertyId = p.getPropertyId();

        Response resp = deleteProperty(propertyId);

        //store to session
        setSessionResponse(resp);
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTY_ID).to(propertyId);
    }

    @Step
    public void propertyIdInSessionDoesntExist() {
        String propertyId = Serenity.sessionVariableCalled(SERENITY_SESSION__PROPERTY_ID);

        Response response = getProperty(propertyId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void deletePropertyById(String propertyId) {
        Response resp = deleteProperty(propertyId);

        //store to session
        setSessionResponse(resp);
    }

    // --- internal impl. ---

    /**
     * POST - new property object
     *
     * @param t property
     * @return server response
     */
    private Response createProperty(Property t) {
        return given().spec(spec)
                .body(t)
                .when().post();

    }

    /**
     * GET - property object by ID
     *
     * @param id   ID of the property object
     * @param etag ETag version stamp
     * @return server response
     */
    private Response getProperty(String id, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (etag != null && !etag.isEmpty()) {
            requestSpecification = requestSpecification.header(HEADER_IF_NONE_MATCH, etag);
        }
        return requestSpecification.when().get("/{id}", id);
    }

    /**
     * POST - update property object
     *
     * @param id     ID of the property
     * @param fields updated property field values
     * @param etag   ETag version stamp
     * @return server response
     */
    private Response updateProperty(String id, Map<String, Object> fields, String etag) {
        RequestSpecification requestSpecification = given().spec(spec);
        if (etag != null && !etag.isEmpty()) {
            requestSpecification = requestSpecification.header(HEADER_IF_MATCH, etag);
        }
        return requestSpecification.body(fields).when().post("/{id}", id);

    }

    /**
     * DELETE - remove property object
     *
     * @param id ID of the property
     * @return server response
     */
    private Response deleteProperty(String id) {
        return given().spec(spec)
                .when().delete("/{id}", id);
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
    public Property getPropertyByCodeInternal(String code) {
        Property[] properties = getEntities("1", "0", "property_code==" + code, null, null).as(Property[].class);
        return Arrays.asList(properties).stream().findFirst().orElse(null);
    }

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

    public void removeAllUsersFromPropertiesWithCodes(List<String> propertyCodes) {
        propertyCodes.forEach(c -> {
            Property property = getPropertyByCodeInternal(c);
            Response customerUsersResponse = getSecondLevelEntities(property.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null);
            PropertyUser[] propertyUsers = customerUsersResponse.as(PropertyUser[].class);
            for (PropertyUser pu : propertyUsers) {
                Response deleteResponse = deleteSecondLevelEntity(property.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, pu.getUserId());
                if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                    fail("User cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    public void relationExistsBetweenUserAndProperty(User user, String propertyCode) {
        Property p = getPropertyByCodeInternal(propertyCode);

        PropertyUser existingPropertyUser = getUserForProperty(p.getPropertyId(), user.getUserName());
        if (existingPropertyUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(p.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, user.getUserId());
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertyUser cannot be deleted - status: " + deleteResponse.getStatusCode() + ", " + deleteResponse.asString());
            }
        }
        Response createResponse = addUserToProperty(user.getUserId(), p.getPropertyId());
        if (createResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("PropertyUser cannot be created - status: " + createResponse.getStatusCode() + ", " + createResponse.asString());
        }
    }

    private Response addUserToProperty(String userId, String propertyId) {
        Map<String, Object> propertyUser = new HashMap<>();
        propertyUser.put("user_id", userId);

        return given().spec(spec)
                .body(propertyUser)
                .when().post("/{propertyId}/users", propertyId);
    }

    private PropertyUser getUserForProperty(String propertyId, String code) {
        Response customerUserResponse = getSecondLevelEntities(propertyId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + code, null, null);
        return Arrays.asList(customerUserResponse.as(PropertyUser[].class)).stream().findFirst().orElse(null);
    }

    private Customer getCustomerForProperty(String propertyId, String customerCode){
        Response customerResponse = getSecondLevelEntities(propertyId, SECOND_LEVEL_OBJECT_CUSTOMERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "code==" + customerCode, null, null);
        return Arrays.asList(customerResponse.as(Customer[].class)).stream().findFirst().orElse(null);
    }

    public void customerDoesNotExistForProperty(String customerCode, String propertyCode){
        Property p = getPropertyByCodeInternal(propertyCode);
        Customer cust = getCustomerForProperty(p.getPropertyId(), customerCode);
        assertNull("Customer should not be link with property", cust);
    }

    public void userIsAddedToProperty(User u, String propertyCode) {
        Property p = getPropertyByCodeInternal(propertyCode);
        Response response = addUserToProperty(u.getUserId(), p.getPropertyId());
        setSessionResponse(response);
    }

    public void userIsDeletedFromProperty(User u, String propertyCode) {
        Property p = getPropertyByCodeInternal(propertyCode);
        Response deleteResponse = deleteSecondLevelEntity(p.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, u.getUserId());
        setSessionResponse(deleteResponse);
    }

    public void userDoesntExistForProperty(User u, String propertyCode) {
        Property p = getPropertyByCodeInternal(propertyCode);
        PropertyUser userForProperty = getUserForProperty(p.getPropertyId(), u.getUserName());
        assertNull("User should not be present in property", userForProperty);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        PropertyUser[] propertiesUsers = response.as(PropertyUser[].class);
        int i = 0;
        for (PropertyUser pu : propertiesUsers) {
            assertEquals("Propertyuser on index=" + i + " is not expected", usernames.get(i), pu.getUserName());
            i++;
        }
    }

    public void listOfUsersIsGotWith(String propertyCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        Property p = getPropertyByCodeInternal(propertyCode);
        Response response = getSecondLevelEntities(p.getPropertyId(), SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void bodyContainsEntityWith(String attributeName, String attributeValue) {
        Response response = getSessionResponse();
        response.then().body(attributeName, is(Integer.valueOf(attributeValue)));
    }

    @Step
    public void activatePropertyWithCode(String code) {
        Property property = getPropertyByCodeInternal(code);
        Response response = activateProperty(property.getPropertyId());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    @Step
    public void inactivatePropertyWithCode(String code) {
        Property property = getPropertyByCodeInternal(code);
        Response response = inactivateProperty(property.getPropertyId());
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
    }

    public Response activateProperty(String id) {
        return given().spec(spec).basePath(BASE_PATH__PROPERTIES)
                .when().post("/{id}/active", id);
    }

    public Response inactivateProperty(String id) {
        return given().spec(spec).basePath(BASE_PATH__PROPERTIES)
                .when().post("/{id}/inactive", id);
    }

    @Step
    public void isActiveSetTo(boolean activeFlag, String code) {
        Property property = getPropertyByCodeInternal(code);

        if (activeFlag) {
            assertNotNull("Property should be returned", property);
            assertEquals("Property should have code=" + code, code, property.getPropertyCode());
            assertEquals("is_active parameter should be set to 0", Integer.valueOf(1), property.getIsActive());
        } else {
            assertNotNull("Property should be returned", property);
            assertEquals("is_active parameter should be set to 0", Integer.valueOf(0), property.getIsActive());
        }
    }

    public void inactivateNotExistingProperty(String id) {
        Response response = inactivateProperty(id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void activateNotExistingProperty(String id) {
        Response response = inactivateProperty(id);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    public void listOfCustomersIsGotWith(String propertyCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        Property p = getPropertyByCodeInternal(propertyCode);
        Response response = getSecondLevelEntities(p.getPropertyId(), SECOND_LEVEL_OBJECT_CUSTOMERS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void allCustomersAreCustomersOfProperty(String propertyCode) {
    	String propertyID = getPropertyByCodeInternal(propertyCode).getPropertyId();
    	Response response = getSessionResponse();
    	Customer[] customers = response.as(Customer[].class);
    	for (Customer c : customers) {
			given().baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI)).basePath("identity/customers/").get(c.getCustomerId()+"/properties").
			then().body("property_id",hasItem(propertyID));
		}
    }

    // TODO reuse existing code

//    @Step
//    public void fileIsUsedForCreation(String filename) {
//        Customer customer = getCustomerFromFile(this.getClass().getResourceAsStream(filename));
//        Serenity.setSessionVariable(SESSION_CREATED_PROPERTY).to(customer);
//        Response response = createCustomer(customer);
//        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
//    }
//
//    private Customer getCustomerFromFile(InputStream inputStream) {
//        Customer customer = from(inputStream).getObject("", Customer.class);
//        return customer;
//    }
//
//
//    @Step
//    public void customerWithIdIsGot(String customerId) {
//        Response resp = getCustomer(customerId, null);
//        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);//store to session
//    }
//
//    @Step
//    public void updateCustomerWithCode(String code, Customer updatedCustomer) {
//        Customer original = getCustomerByCode(code);
//        Response tempResponse = getCustomer(original.getCustomerId(), null);
//
//        Map<String, Object> customer = new HashMap<>();
//        if (updatedCustomer.getEmail() != null && !"".equals(updatedCustomer.getEmail())) {
//            customer.put("email", updatedCustomer.getEmail());
//        }
//        if (updatedCustomer.getCompanyName() != null && !"".equals(updatedCustomer.getCompanyName())) {
//            customer.put("company_name", updatedCustomer.getCompanyName());
//        }
//        if (updatedCustomer.getVatId() != null && !"".equals(updatedCustomer.getVatId())) {
//            customer.put("vat_id", updatedCustomer.getVatId());
//        }
//        if (updatedCustomer.getPhone() != null && !"".equals(updatedCustomer.getPhone())) {
//            customer.put("phone", updatedCustomer.getPhone());
//        }
//        if (updatedCustomer.getSalesforceId() != null && !"".equals(updatedCustomer.getSalesforceId())) {
//            customer.put("salesforce_id", updatedCustomer.getSalesforceId());
//        }
//        if (updatedCustomer.getWebsite() != null && !"".equals(updatedCustomer.getWebsite())) {
//            customer.put("website", updatedCustomer.getWebsite());
//        }
//
//        Response response = updateCustomer(original.getCustomerId(), customer, tempResponse.getHeader(HEADER_ETAG));
//        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
//    }
//
//    public void updateCustomerWithCodeIfUpdatedBefore(String code, Customer updatedCustomer) {
//        Customer original = getCustomerByCode(code);
//        Response tempResponse = getCustomer(original.getCustomerId(), null);
//
//
//        Map<String, Object> mapForUpdate = new HashMap<>();
//        mapForUpdate.put("vat_id", "CZ99999999");
//
//        Response updateResponse = updateCustomer(original.getCustomerId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));
//
//        if (updateResponse.getStatusCode() != 204) {
//            fail("Customer cannot be updated: " + updateResponse.asString());
//        }
//
//
//        Map<String, Object> customer = new HashMap<>();
//        if (updatedCustomer.getEmail() != null && !"".equals(updatedCustomer.getEmail())) {
//            customer.put("email", updatedCustomer.getEmail());
//        }
//        if (updatedCustomer.getCompanyName() != null && !"".equals(updatedCustomer.getCompanyName())) {
//            customer.put("company_name", updatedCustomer.getCompanyName());
//        }
//        if (updatedCustomer.getVatId() != null && !"".equals(updatedCustomer.getVatId())) {
//            customer.put("vat_id", updatedCustomer.getVatId());
//        }
//        if (updatedCustomer.getPhone() != null && !"".equals(updatedCustomer.getPhone())) {
//            customer.put("phone", updatedCustomer.getPhone());
//        }
//
//        Response response = updateCustomer(original.getCustomerId(), customer, tempResponse.getHeader(HEADER_ETAG));
//        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
//    }
//
//    public void customerWithCodeHasData(String code, Customer data) {
//        Customer customerByCode = getCustomerByCode(code);
//
//        if (data.getEmail() != null && !"".equals(data.getEmail())) {
//            assertEquals(data.getEmail(), customerByCode.getEmail());
//        }
//        if (data.getCompanyName() != null && !"".equals(data.getCompanyName())) {
//            assertEquals(data.getCompanyName(), customerByCode.getCode());
//        }
//        if (data.getVatId() != null && !"".equals(data.getVatId())) {
//            assertEquals(data.getVatId(), customerByCode.getVatId());
//        }
//        if (data.getPhone() != null && !"".equals(data.getPhone())) {
//            assertEquals(data.getPhone(), customerByCode.getPhone());
//        }
//        if (data.getSalesforceId() != null && !"".equals(data.getSalesforceId())) {
//            assertEquals(data.getSalesforceId(), customerByCode.getSalesforceId());
//        }
//        if (data.getWebsite() != null && !"".equals(data.getWebsite())) {
//            assertEquals(data.getWebsite(), customerByCode.getWebsite());
//        }
//    }

}