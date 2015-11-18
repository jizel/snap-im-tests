package travel.snapshot.dp.qa.serenity.customers;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.model.Customer;
import travel.snapshot.dp.qa.model.CustomerProperty;
import travel.snapshot.dp.qa.model.CustomerUser;
import travel.snapshot.dp.qa.model.Property;
import travel.snapshot.dp.qa.model.User;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class CustomerSteps extends BasicSteps {

    public static final String SESSION_CUSTOMERS = "customers";
    private static final String SESSION_CREATED_CUSTOMER = "created_customer";
    private static final String SESSION_CUSTOMER_ID = "customer_id";
    private static final String SESSION_CREATED_CUSTOMER_PROPERTY = "created_customer_property";
    public static final String BASE_PATH_CUSTOMERS = "/identity/customers";

    public CustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMERS);
    }

    @Step
    public void followingCustomersExist(List<Customer> customers) {
        customers.forEach(t -> {
            t.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
            Customer existingCustomer = getCustomerByCodeInternal(t.getCode());
            if (existingCustomer != null) {
                deleteEntity(existingCustomer.getCustomerId());
            }
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != 201) {
                fail("Customer cannot be created");
            }
        });
        Serenity.setSessionVariable(SESSION_CUSTOMERS).to(customers);
    }

    @Step
    public void fileIsUsedForCreation(String filename) {
        Customer customer = getCustomerFromFile(this.getClass().getResourceAsStream(filename));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }


    @Step
    public void followingCustomerIsCreated(Customer customer) {
        customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Customer existingCustomer = getCustomerByCodeInternal(customer.getCode());
        if (existingCustomer != null) {
            deleteEntity(existingCustomer.getCustomerId());
        }
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void compareCustomerOnHeaderWithStored(String headerName) {
        Customer originalCustomer = Serenity.sessionVariableCalled(SESSION_CREATED_CUSTOMER);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName).replaceFirst(BASE_PATH_CUSTOMERS, "");
        given().spec(spec).get(customerLocation).then()
                .body("salesforce_id", is(originalCustomer.getSalesforceId()))
                .body("company_name", is(originalCustomer.getCompanyName()))
                .body("code", is(originalCustomer.getCode()))
                .body("email", is(originalCustomer.getEmail()))
                .body("vat_id", is(originalCustomer.getVatId()));

    }

    @Step
    public void compareCustomerPropertyOnHeaderWithStored(String headerName) {
        CustomerProperty originalCustomerProperty = getSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName).replaceFirst(BASE_PATH_CUSTOMERS, "");
        given().spec(spec).get(customerLocation).then()
                .body("property_id", is(originalCustomerProperty.getPropertyId()))
                .body("property_name", is(originalCustomerProperty.getPropertyName()))
                        //.body("type", is(originalCustomerProperty.getType()))
                .body("valid_from", is(originalCustomerProperty.getValidFrom()))
                .body("valid_to", is(originalCustomerProperty.getValidTo()));

    }

    private Customer getCustomerFromFile(InputStream inputStream) {
        Customer customer = from(inputStream).getObject("", Customer.class);
        return customer;
    }

    private Response activateCustomer(String id) {
        return given().spec(spec)
                .when().post("/{id}/active", id);
    }

    private Response inactivateCustomer(String id) {
        return given().spec(spec)
                .when().post("/{id}/inactive", id);
    }

    private CustomerProperty getCustomerPropertyForCustomerWithType(String customerId, String propertyId, String type) {
        //TODO add type to query
        String filter = String.format("property_id==%s;type==%s", propertyId, type);
        CustomerProperty[] customerProperties = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(CustomerProperty[].class);
        return Arrays.asList(customerProperties).stream().findFirst().orElse(null);
    }

    private Response addPropertyToCustomerWithTypeFromTo(String propertyId, String customerId, String type, String validFrom, String validTo) {
        Map<String, Object> customerProperty = new HashMap<>();
        if (propertyId != null) {
            customerProperty.put("property_id", propertyId);
        }
        if (type != null) {
            customerProperty.put("type", type);
        }
        if (validFrom != null) {
            customerProperty.put("valid_from", validFrom);
        }
        if (validTo != null) {
            customerProperty.put("valid_to", validTo);
        }

        return given().spec(spec)
                .body(customerProperty)
                .when().post("/{customerId}/properties", customerId);
    }

    private Response addUserToCustomerWithIsPrimary(String userId, String customerId, String isPrimary) {
        Map<String, Object> customerUser = new HashMap<>();
        customerUser.put("user_id", userId);
        customerUser.put("is_primary", isPrimary);


        return given().spec(spec)
                .body(customerUser)
                .when().post("/{customerId}/users", customerId);
    }

    public Customer getCustomerByCodeInternal(String code) {
        Customer[] customers = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "code==" + code, null, null).as(Customer[].class);
        return Arrays.asList(customers).stream().findFirst().orElse(null);
    }


    @Step
    public void customerWithIdIsGot(String customerId) {
        Response response = getEntity(customerId, null);
        setSessionResponse(response);
    }

    @Step
    public void customerWithCodeIsGot(String code) {
        Customer customerFromList = getCustomerByCodeInternal(code);

        Response response = getEntity(customerFromList.getCustomerId(), null);
        setSessionResponse(response);
    }

    @Step
    public void deleteCustomerWithId(String customerId) {
        Response response = deleteEntity(customerId);
        setSessionResponse(response);
    }

    @Step
    public void customerWithCodeIsDeleted(String code) {
        Customer c = getCustomerByCodeInternal(code);
        if (c == null) {
            return;
        }
        String customerId = c.getCustomerId();
        Response response = deleteEntity(customerId);//delete customer
        setSessionResponse(response);
        Serenity.setSessionVariable(SESSION_CUSTOMER_ID).to(customerId);//store to session
    }

    @Step
    public void customerIdInSessionDoesntExist() {
        String customerId = Serenity.sessionVariableCalled(SESSION_CUSTOMER_ID);

        Response response = getEntity(customerId, null);
        response.then().statusCode(404);
    }

    @Step
    public void listOfCustomersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void updateCustomerWithCode(String code, Customer updatedCustomer) throws Throwable {
        Customer original = getCustomerByCodeInternal(code);
        Response tempResponse = getEntity(original.getCustomerId(), null);

        Map<String, Object> customerData = retrieveData(Customer.class, updatedCustomer);

        Response response = updateEntity(original.getCustomerId(), customerData, tempResponse.getHeader("ETag"));
        setSessionResponse(response);
    }

    @Step
    public void activateCustomerWithCode(String code) {
        Customer customer = getCustomerByCodeInternal(code);
        Response response = activateCustomer(customer.getCustomerId());
        setSessionResponse(response);
    }

    @Step
    public void isActiveSetTo(boolean activeFlag, String code) {
        Customer customer = getCustomerByCodeInternal(code);
        if (activeFlag) {
            assertNotNull("Customer should be returned", customer);
            assertEquals("Customer should have code=" + code, code, customer.getCode());
        } else {
            assertNull("Customer should not be returned", customer);
        }
    }

    @Step
    public void inactivateCustomerWithCode(String code) {
        Customer customer = getCustomerByCodeInternal(code);
        Response response = inactivateCustomer(customer.getCustomerId());
        setSessionResponse(response);
    }

    @Step
    public void customerWithCodeIsGotWithEtag(String code) {
        //TODO implement actual customer search
        Customer customerFromList = getCustomerByCodeInternal(code);

        Response tempResponse = getEntity(customerFromList.getCustomerId(), null);

        Response resp = getEntity(customerFromList.getCustomerId(), tempResponse.getHeader("ETag"));
        setSessionResponse(resp);
    }

    @Step
    public void customerWithCodeIsGotWithEtagAfterUpdate(String code) {
        Customer customerFromList = getCustomerByCodeInternal(code);

        Response tempResponse = getEntity(customerFromList.getCustomerId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("vat_id", "CZ99999999");

        Response updateResponse = updateEntity(customerFromList.getCustomerId(), mapForUpdate, tempResponse.getHeader("ETag"));

        if (updateResponse.getStatusCode() != 204) {
            fail("Customer cannot be updated: " + updateResponse.asString());
        }

        Response resp = getEntity(customerFromList.getCustomerId(), tempResponse.getHeader("ETag"));
        setSessionResponse(resp);
    }

    public void updateCustomerWithCodeIfUpdatedBefore(String code, Customer updatedCustomer) throws Throwable {
        Customer original = getCustomerByCodeInternal(code);

        Map<String, Object> customerData = retrieveData(Customer.class, updatedCustomer);

        Response response = updateEntity(original.getCustomerId(), customerData, "fake-etag");
        setSessionResponse(response);
    }

    public void customerWithCodeHasData(String code, Customer data) throws Throwable {
        Map<String, Object> originalData = retrieveData(Customer.class, getCustomerByCodeInternal(code));
        Map<String, Object> expectedData = retrieveData(Customer.class, data);

        expectedData.forEach((k, v) -> {
            if (v == null) {
                assertFalse("Customer JSON should not contains attributes with null values", originalData.containsKey(k));
                return;
            }
            assertTrue("Customer has no data for attribute " + k, originalData.containsKey(k));
            assertEquals(v, originalData.get(k));
        });
    }

    @Step
    public void codesAreInResponseInOrder(List<String> codes) {
        Response response = getSessionResponse();
        Customer[] customers = response.as(Customer[].class);
        int i = 0;
        for (Customer c : customers) {
            assertEquals("Customer on index=" + i + " is not expected", codes.get(i), c.getCode());
            i++;
        }
    }

    @Step
    public void propertyIsAddedToCustomerWithTypeFromTo(String propertyId, String customerCode, String type, String dateFrom, String dateTo) {
        Customer c = getCustomerByCodeInternal(customerCode);

        Response response = addPropertyToCustomerWithTypeFromTo(propertyId, c.getCustomerId(), type, dateFrom, dateTo);

        if (response.statusCode() == 201) {
            setSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY, response.as(CustomerProperty.class));
        }
        setSessionResponse(response);
    }

    @Step
    public void relationExistsBetweenPropertyAndCustomerWithTypeFromTo(Property p, String customerCode, String type, String validFrom, String validTo) {
        Customer c = getCustomerByCodeInternal(customerCode);

        CustomerProperty existingCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        if (existingCustomerProperty != null) {
            Response customerPropertyResponseWithEtag = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), null);
            String etag = customerPropertyResponseWithEtag.header("ETag");

            Map<String, Object> data = new HashMap<>();
            data.put("valid_from", validFrom);
            data.put("valid_to", validTo);

            Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, etag);
            if (updateResponse.getStatusCode() != 204) {
                fail("CustomerProperty cannot be modified");
            }
        } else {
            Response createResponse = addPropertyToCustomerWithTypeFromTo(p.getPropertyId(), c.getCustomerId(), type, validFrom, validTo);
            if (createResponse.getStatusCode() != 201) {
                fail("CustomerProperty cannot be created");
            }
        }
    }

    public void propertyIsUpdateForCustomerWithType(Property p, String customerCode, String type, String fieldName, String value) {
        Customer c = getCustomerByCodeInternal(customerCode);

        CustomerProperty existingCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        Response customerPropertyResponseWithEtag = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), null);
        String etag = customerPropertyResponseWithEtag.header("ETag");

        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, etag);
        setSessionResponse(updateResponse);
    }

    public void propertyIsUpdateForCustomerWithTypeWithInvalidEtag(Property p, String customerCode, String type, String fieldName, String value) {
        Customer c = getCustomerByCodeInternal(customerCode);

        CustomerProperty existingCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void relationExistsBetweenUserAndCustomerWithPrimary(User user, String customerCode, String isPrimary) {
        Customer c = getCustomerByCodeInternal(customerCode);

        CustomerUser existingCustomerUser = getUserForCustomer(c.getCustomerId(), user.getUserName());
        if (existingCustomerUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, user.getUserId());
            if (deleteResponse.getStatusCode() != 204) {
                fail("CustomerUser cannot be deleted");
            }
        }
        Response createResponse = addUserToCustomerWithIsPrimary(user.getUserId(), c.getCustomerId(), isPrimary);
        if (createResponse.getStatusCode() != 201) {
            fail("CustomerUser cannot be created");
        }
    }

    private CustomerUser getUserForCustomer(String customerId, String userName) {
        Response customerUserResponse = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + userName, null, null);
        return Arrays.asList(customerUserResponse.as(CustomerUser[].class)).stream().findFirst().orElse(null);
    }

    @Step
    public void propertyIsgotForCustomerWithType(Property p, String customerCode, String type) {
        Customer c = getCustomerByCodeInternal(customerCode);
        CustomerProperty tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);

        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);
        setSessionResponse(response);
    }

    @Step
    public void getCustomerPropertyWithId(String customerCode, String relationshipId) {
        Customer c = getCustomerByCodeInternal(customerCode);
        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, relationshipId, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertiesIsGotWith(String customerCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        Customer c = getCustomerByCodeInternal(customerCode);
        Response response = getSecondLevelEntities(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertySetsIsGotWith(String customerCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        Customer c = getCustomerByCodeInternal(customerCode);
        Response response = getSecondLevelEntities(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomersDontExist(List<String> customerCodes) {
        customerCodes.forEach(c -> {
            customerWithCodeIsDeleted(c);
        });
    }

    @Step
    public void userIsAddedToCustomerWithIsPrimary(User u, String customerCode, String isPrimary) {
        Customer c = getCustomerByCodeInternal(customerCode);

        Response response = addUserToCustomerWithIsPrimary(u.getUserId(), c.getCustomerId(), isPrimary);
        setSessionResponse(response);
    }

    @Step
    public void userIsDeletedFromCustomer(User u, String customerCode) {
        Customer c = getCustomerByCodeInternal(customerCode);

        Response deleteResponse = deleteSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, u.getUserId());
        setSessionResponse(deleteResponse);
    }

    @Step
    public void userDoesntExistForCustomer(User u, String customerCode) {
        Customer c = getCustomerByCodeInternal(customerCode);
        CustomerUser userForCustomer = getUserForCustomer(c.getCustomerId(), u.getUserName());
        assertNull("User should not be present in customer", userForCustomer);
    }

    public List<Customer> getCustomersForCodes(List<String> customerCodes) {
        String filter = "code=in=(" + StringUtils.join(customerCodes.iterator(), ',') + ")";
        Customer[] customers = getEntities(LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null).as(Customer[].class);
        return Arrays.asList(customers);
    }

    public void removeAllUsersFromCustomers(List<String> codes) {
        codes.forEach(c -> {
            Customer customer = getCustomerByCodeInternal(c);
            Response customerUsersResponse = getSecondLevelEntities(customer.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null);
            CustomerUser[] customerUsers = customerUsersResponse.as(CustomerUser[].class);
            for (CustomerUser cu : customerUsers) {
                Response deleteResponse = deleteSecondLevelEntity(customer.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, cu.getUserId());
                if (deleteResponse.statusCode() != 204) {
                    fail("Property set cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    public void listOfUsersIsGotWith(String customerCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        Customer c = getCustomerByCodeInternal(customerCode);
        Response response = getSecondLevelEntities(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        CustomerUser[] customerUsers = response.as(CustomerUser[].class);
        int i = 0;
        for (CustomerUser cu : customerUsers) {
            assertEquals("Customeruser on index=" + i + " is not expected", usernames.get(i), cu.getUserName());
            i++;
        }
    }
}
