package travel.snapshot.dp.qa.serenity.properties;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import travel.snapshot.dp.qa.model.Property;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import java.util.Collections;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author martin.konkol(at)snapshot.travel
 * Created by Martin Konkol on 9/23/2015.
 */
public class PropertySteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTIES = "properties";
    private static final String SERENITY_SESSION__CREATED_PROPERTY = "created_property";
    private static final String SERENITY_SESSION__PROPERTY_ID = "property_id";
    
    private static final String BASE_PATH__PROPERTIES = "/identity/properties";

    public PropertySteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
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
            t.setBillingAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
            Response createResponse = createProperty(t);
            if (createResponse.getStatusCode() != 201) {
                fail("Property cannot be created: " + createResponse.asString());
            }
        });
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTIES).to(properties);
    }
    
    @Step
    public void getPropertyByID(String id) {
        Response resp = getProperty(id, null);
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }
    
    @Step
    public void getPropertyByCode(String code) {
        Property customerFromList = getPropertyByCodeInternal(code);
        Response resp = getProperty(customerFromList.getPropertyId(), null);
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }
    
    @Step
    public void bodyContainsPropertyWith(String atributeName) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, notNullValue());
    }
    
    @Step
    public void bodyContainsPropertyWith(String atributeName, String value) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, is(parseRawType(value)));
    }
    
    @Step
    public void bodyDoesNotContainPropertyWith(String atributeName) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        response.then().body(atributeName, nullValue());
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
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader("ETag"));
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
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
            Collections.singletonMap("vat_id", "CZ99999999"),
            responseWithETag.getHeader("ETag"));
        if (updateResponse.getStatusCode() != 204) {
            fail("Property cannot be updated: " + updateResponse.asString());
        }

        // get with old ETag
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader("ETag"));
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }
    
    @Step
    public void listOfPropertiesExistsWith(String limit, String cursor) {
        Response response = getProperties(limit, cursor);
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    @Step
    public void numberOfPropertiesIsInResponse(int count) {
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        Property[] properties = response.as(Property[].class);
        assertEquals("There should be " + count + " properties existing", count, properties.length);
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
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }
    
    @Step
    public void comparePropertyOnHeaderWithStored(String headerName) {
        Property originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String propertyLocation = response.header(headerName);
        given().spec(spec).get(propertyLocation).then()
                .body("salesforce_id", is(originalProperty.getSalesforceId()))
                .body("property_name", is(originalProperty.getPropertyName()))
                .body("property_code", is(originalProperty.getPropertyCode()))
                .body("email", is(originalProperty.getEmail()))
                .body("vat_id", is(originalProperty.getVatId()));

    }
    
    @Step
    public void deletePropertyWithCode(String code) {
        String propertyId = getPropertyByCodeInternal(code).getPropertyId();
        Response resp = deleteProperty(propertyId);
        
        //store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTY_ID).to(propertyId);
    }
    
    @Step
    public void propertyIdInSessionDoesntExist() {
        String propertyId = Serenity.sessionVariableCalled(SERENITY_SESSION__PROPERTY_ID);

        Response response = getProperty(propertyId, null);
        response.then().statusCode(404);
    }
    
    @Step
    public void deletePropertyById(String propertyId) {
        Response resp = deleteProperty(propertyId);
        
        //store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
    }
    
    // --- internal impl. ---
    
    /**
     * POST - new property object
     * 
     * @param t property
     * @return server response
     */
    private Response createProperty(Property t) {
        return given().spec(spec).basePath(BASE_PATH__PROPERTIES)
                .body(t)
                .when().post();

    }
    
    /**
     * GET - property object by ID
     * 
     * @param id ID of the property object
     * @param etag ETag version stamp
     * @return server response
     */
    private Response getProperty(String id, String etag) {
        RequestSpecification requestSpecification = given().spec(spec).basePath(BASE_PATH__PROPERTIES);
        if (etag != null && !etag.isEmpty()) {
            requestSpecification = requestSpecification.header("If-None-Match", etag);
        }
        return requestSpecification.when().get("/{id}", id);
    }
    
    /**
     * POST - update property object
     * 
     * @param id ID of the property
     * @param fields updated property field values
     * @param etag ETag version stamp
     * @return server response
     */
    private Response updateProperty(String id, Map<String, Object> fields, String etag) {
        RequestSpecification requestSpecification = given().spec(spec).basePath(BASE_PATH__PROPERTIES);
        if (etag != null && !etag.isEmpty()) {
            requestSpecification = requestSpecification.header("If-Match", etag);
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
        return given().spec(spec).basePath(BASE_PATH__PROPERTIES)
                .when().delete("/{id}", id);
    }
    
    /**
     * GET - list of property objects
     *
     * @param limit maximum amount of properties
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
    private Property getPropertyByCodeInternal(String code) {
        Property[] properties = getProperties("100", "0").as(Property[].class);
        return Arrays.asList(properties).stream().filter(p -> code.equals(p.getPropertyCode())).findFirst().orElse(null);
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
//    private Response activateCustomer(String id) {
//        return given().spec(spec).basePath("/identity/customers")
//                .when().post("/{id}/active", id);
//    }
//
//    private Response inactivateCustomer(String id) {
//        return given().spec(spec).basePath("/identity/customers")
//                .when().post("/{id}/inactive", id);
//    }
//
//    @Step
//    public void getCustomerWithId(String customerId) {
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
//        Response response = updateCustomer(original.getCustomerId(), customer, tempResponse.getHeader("ETag"));
//        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
//    }
//
//    @Step
//    public void activateCustomerWithCode(String code) {
//        Customer customer = getCustomerByCode(code);
//        Response response = activateCustomer(customer.getCustomerId());
//        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);//store to session
//    }
//
//    @Step
//    public void isActiveSetTo(boolean activeFlag, String code) {
//        Customer customer = getCustomerByCode(code);
//        fail("How to check it was changed?");
//    }
//
//    @Step
//    public void inactivateCustomerWithCode(String code) {
//        Customer customer = getCustomerByCode(code);
//        Response response = inactivateCustomer(customer.getCustomerId());
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
//        Response updateResponse = updateCustomer(original.getCustomerId(), mapForUpdate, tempResponse.getHeader("ETag"));
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
//        Response response = updateCustomer(original.getCustomerId(), customer, tempResponse.getHeader("ETag"));
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
