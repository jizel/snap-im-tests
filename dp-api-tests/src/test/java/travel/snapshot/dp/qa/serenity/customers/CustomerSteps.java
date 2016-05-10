package travel.snapshot.dp.qa.serenity.customers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.AddressUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyViewDto;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipViewDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.DateUtils;
import travel.snapshot.dp.qa.helpers.NullStringObjectValueConverter;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class CustomerSteps extends BasicSteps {

    private static final String SESSION_CUSTOMERS = "customers";
    private static final String SESSION_CREATED_CUSTOMER = "created_customer";
    private static final String SESSION_CUSTOMER_ID = "customer_id";
    private static final String SESSION_CREATED_CUSTOMER_PROPERTY = "created_customer_property";
    private static final String BASE_PATH_CUSTOMERS = "/identity/customers";

    public CustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH_CUSTOMERS);
    }

    @Step
    public void followingCustomersExist(List<CustomerDto> customers) {
        customers.forEach(t -> {
            t.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Customer cannot be created " + createResponse.getBody().asString());
            }

        });
        Serenity.setSessionVariable(SESSION_CUSTOMERS).to(customers);
    }

    @Step
    public void fileIsUsedForCreation(String filename) {
        CustomerDto customer = getCustomerFromFile(this.getClass().getResourceAsStream(filename));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }


    @Step
    public void followingCustomerIsCreated(CustomerDto customer) {
        customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ"));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomerIsCreatedWithAddress(CustomerDto customer, AddressDto address) {
        customer.setAddress(address);
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        CustomerDto existingCustomer = getCustomerByCodeInternal(customer.getCode());
        if (existingCustomer != null) {
            deleteEntity(existingCustomer.getCustomerId());
        }
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void compareCustomerOnHeaderWithStored(String headerName) {
        CustomerDto originalCustomer = Serenity.sessionVariableCalled(SESSION_CREATED_CUSTOMER);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName).replaceFirst(BASE_PATH_CUSTOMERS, "");
        given().spec(spec).get(customerLocation).then()
                .body("salesforce_id", is(originalCustomer.getSalesforceId()))
                .body("name", is(originalCustomer.getCompanyName()))
                .body("customer_code", is(originalCustomer.getCode()))
                .body("email", is(originalCustomer.getEmail()))
                .body("vat_id", is(originalCustomer.getVatId()));
    }

    @Step
    public void compareCustomerPropertyOnHeaderWithStored(String headerName) {
        CustomerPropertyViewDto originalCustomerProperty = getSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName).replaceFirst(BASE_PATH_CUSTOMERS, "");
        given().spec(spec).get(customerLocation).then()
                .body("property_id", is(originalCustomerProperty.getPropertyId()))
                //.body("type", is(originalCustomerProperty.getType()))
                .body("valid_from", is(DateUtils.isoDatefromDate(originalCustomerProperty.getValidFrom())))
                .body("valid_to", is(DateUtils.isoDatefromDate(originalCustomerProperty.getValidTo())));

    }

    private CustomerDto getCustomerFromFile(InputStream inputStream) {
        return from(inputStream).getObject("", CustomerDto.class);
    }

    private Response activateCustomer(String id) {
        return given().spec(spec)
                .when().post("/{id}/active", id);
    }

    private Response inactivateCustomer(String id) {
        return given().spec(spec)
                .when().post("/{id}/inactive", id);
    }

    public void allCustomersAreActive() {
        Response response = getSessionResponse();
        List<Integer> isActiveList = response.body().jsonPath().getList("is_active");
        for (int c : isActiveList) {
            assertEquals(1, c);
        }
    }

    private CustomerPropertyViewDto getCustomerPropertyForCustomerWithType(String customerId, String propertyId, String type) {
        //TODO add type to query
        setAccessTokenParamFromSession();
        String filter = String.format("property_id==%s;relationship_type==%s", propertyId, type);
        CustomerPropertyViewDto[] customerProperties = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(CustomerPropertyViewDto[].class);
        return Arrays.asList(customerProperties).stream().findFirst().orElse(null);
    }

    private Response addPropertyToCustomerWithTypeFromTo(String propertyId, String customerId, String type, String validFrom, String validTo) {
        Map<String, Object> customerProperty = new HashMap<>();
        if (propertyId != null) {
            customerProperty.put("property_id", propertyId);
        }
        if (type != null) {
            customerProperty.put("relationship_type", type);
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
        customerUser.put("is_primary", Integer.valueOf(isPrimary));


        return given().spec(spec)
                .body(customerUser)
                .when().post("/{customerId}/users", customerId);
    }

    public CustomerDto getCustomerByCodeInternal(String code) {
        CustomerDto[] customers = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_code==" + code, null, null).as(CustomerDto[].class);
        return Arrays.asList(customers).stream().findFirst().orElse(null);
    }

    public CustomerDto getCustomerById(String id) {
        CustomerDto[] customers = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + id, null, null).as(CustomerDto[].class);
        return Arrays.asList(customers).stream().findFirst().orElse(null);
    }

    @Step
    public void customerWithIdIsGot(String customerId) {
        Response response = getEntity(customerId, null);
        setSessionResponse(response);
    }

    @Step
    public void customerWithCodeIsGot(String code) {
        CustomerDto customerFromList = getCustomerByCodeInternal(code);

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
        CustomerDto c = getCustomerByCodeInternal(code);
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
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void listOfCustomersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void updateCustomerWithCode(String code, CustomerUpdateDto updatedCustomer) throws Throwable {
        CustomerDto original = getCustomerByCodeInternal(code);
        if (original == null) {
            fail("Customer with code " + code + " not found");
        }

        Response tempResponse = getEntity(original.getCustomerId(), null);

        ObjectMapper mapper = new ObjectMapper();
        String customerData = mapper.writeValueAsString(updatedCustomer);

        String s = NullStringObjectValueConverter.transform(customerData).toString();
        if (s.equalsIgnoreCase("{}")) {
            fail("Empty update, check parameters!");
        }

        Response response = updateEntity(original.getCustomerId(), s, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void updateCustomerAddress(String customerId, AddressUpdateDto updatedAddress) throws Throwable {
        Response temp = getEntity(customerId);
        if (temp.getStatusCode() != HttpStatus.SC_OK) {
            fail("Customer " + customerId + " not found!");
        }

        JSONObject addressJson = new JSONObject();
        JSONObject regexGenerated = RegexValueConverter.transform(retrieveDataNew(updatedAddress));
        addressJson.putOpt("address", regexGenerated);

        Response response = updateEntity(customerId, addressJson.toString(), temp.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void activateCustomerWithCode(String code) {
        CustomerDto customer = getCustomerByCodeInternal(code);
        Response response = activateCustomer(customer.getCustomerId());
        setSessionResponse(response);
    }

    @Step
    public void isActiveSetTo(boolean activeFlag, String code) {
        CustomerDto customer = getCustomerByCodeInternal(code);
        if (activeFlag) {
            assertNotNull("Customer should be returned", customer);
            assertEquals("Customer should have code=" + code, code, customer.getCode());
        } else {
            assertNull("Customer should NOT be returned, only active customer are returned", customer);
        }
    }

    @Step
    public void inactivateCustomerWithCode(String code) {
        CustomerDto customer = getCustomerByCodeInternal(code);
        Response response = inactivateCustomer(customer.getCustomerId());
        setSessionResponse(response);
    }

    @Step
    public void customerWithCodeIsGotWithEtag(String code) {
        //TODO implement actual customer search
        CustomerDto customerFromList = getCustomerByCodeInternal(code);

        Response tempResponse = getEntity(customerFromList.getCustomerId(), null);

        Response resp = getEntity(customerFromList.getCustomerId(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void customerWithCodeIsGotWithEtagAfterUpdate(String code) {
        CustomerDto customerFromList = getCustomerByCodeInternal(code);

        Response tempResponse = getEntity(customerFromList.getCustomerId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("vat_id", "CZ99999999");

        Response updateResponse = updateEntity(customerFromList.getCustomerId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Customer cannot be updated: " + updateResponse.asString());
        }

        Response resp = getEntity(customerFromList.getCustomerId(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    public void updateCustomerWithCodeIfUpdatedBefore(String code, CustomerDto updatedCustomer) throws Throwable {
        CustomerDto original = getCustomerByCodeInternal(code);

        Map<String, Object> customerData = retrieveData(CustomerDto.class, updatedCustomer);

        Response response = updateEntity(original.getCustomerId(), customerData, "fake-etag");
        setSessionResponse(response);
    }

    public void customerWithIdHasData(String id, CustomerDto data) throws Throwable {
        JSONObject customerFromDB = retrieveDataNew(getCustomerById(id));
        JSONObject updatedData = retrieveDataNew(data);

        Iterator<?> customerFromDBKeys = customerFromDB.keys();
        Iterator<?> updatedDataKeys = updatedData.keys();

        while (updatedDataKeys.hasNext()) {
            String key = (String) updatedDataKeys.next();

            Object updatedValue = updatedData.get(key);
            Object databaseValue = customerFromDB.get(key);

            assertEquals(updatedValue, databaseValue);
        }
    }

    @Step
    public void codesAreInResponseInOrder(List<String> codes) {
        Response response = getSessionResponse();
        CustomerDto[] customers = response.as(CustomerDto[].class);
        int i = 0;
        for (CustomerDto c : customers) {
            assertEquals("Customer on index=" + i + " is not expected", codes.get(i), c.getCode());
            i++;
        }
    }

    @Step
    public void propertyIsAddedToCustomerWithTypeFromTo(String propertyId, String customerCode, String type, String dateFrom, String dateTo) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        Response response = addPropertyToCustomerWithTypeFromTo(propertyId, c.getCustomerId(), type, dateFrom, dateTo);

        if (response.statusCode() == HttpStatus.SC_CREATED) {
            setSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY, response.as(CustomerPropertyViewDto.class));
        }
        setSessionResponse(response);
    }

    @Step
    public void relationExistsBetweenPropertyAndCustomerWithTypeFromTo(PropertyDto p, String customerCode, String type, String validFrom, String validTo) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        //TODO FIX temporary turned off checking of already created relationship
        /*setAccessTokenParamFromSession();

        CustomerProperty existingCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        if (existingCustomerProperty != null) {

            Response customerPropertyResponseWithEtag = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), null);
            String etag = customerPropertyResponseWithEtag.header(HEADER_ETAG);

            Map<String, Object> data = new HashMap<>();
            data.put("valid_from", validFrom);
            data.put("valid_to", validTo);

            Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, etag);
            if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("CustomerProperty cannot be modified");
            }
        } else {*/
        Response createResponse = addPropertyToCustomerWithTypeFromTo(p.getPropertyId(), c.getCustomerId(), type, validFrom, validTo);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerProperty cannot be created " + createResponse.getBody().asString());
        }
        //}
    }

    public void propertyIsUpdateForCustomerWithType(PropertyDto p, String customerCode, String type, String fieldName, String value) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        CustomerPropertyViewDto existingCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        Response customerPropertyResponseWithEtag = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), null);
        String etag = customerPropertyResponseWithEtag.header(HEADER_ETAG);

        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, etag);
        setSessionResponse(updateResponse);
    }

    public void propertyIsUpdateForCustomerWithTypeWithInvalidEtag(PropertyDto p, String customerCode, String type, String fieldName, String value) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        CustomerPropertyViewDto existingCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void relationExistsBetweenUserAndCustomerWithPrimary(UserDto user, String customerCode, String isPrimary) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        CustomerUserRelationshipDto existingCustomerUser = getUserForCustomer(c.getCustomerId(), user.getUserName());
        if (existingCustomerUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, user.getUserId());
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("CustomerUser cannot be deleted " + deleteResponse.getBody().asString());
            }
        }
        Response createResponse = addUserToCustomerWithIsPrimary(user.getUserId(), c.getCustomerId(), isPrimary);
        if (createResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("CustomerUser cannot be created " + createResponse.getBody().asString());
        }
    }

    private CustomerUserRelationshipDto getUserForCustomer(String customerId, String userName) {
        Response customerUserResponse = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + userName, null, null);
        return Arrays.asList(customerUserResponse.as(CustomerUserRelationshipDto[].class)).stream().findFirst().orElse(null);
    }

    @Step
    public void propertyIsgotForCustomerWithType(PropertyDto p, String customerCode, String type) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        setAccessTokenParamFromSession();
        CustomerPropertyViewDto tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);
        setSessionResponse(response);
    }

    public void propertyIsgotForCustomerWithTypeWithEtag(PropertyDto p, String customerCode, String type) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        CustomerPropertyViewDto tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);

        Response tempResponse = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);

        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    public void propertyIsgotForCustomerWithTypeWithEtagAfterUpdate(PropertyDto p, String customerCode, String type) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        CustomerPropertyViewDto tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);

        Response tempResponse = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("valid_to", "2200-01-01");

        Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);
        setSessionResponse(response);
    }

    @Step
    public void getCustomerPropertyWithId(String customerCode, String relationshipId) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, relationshipId, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertiesIsGotWith(String customerCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        setAccessTokenParamFromSession();
        Response response = getSecondLevelEntities(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertySetsIsGotWith(String customerCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        setAccessTokenParamFromSession();
        Response response = getSecondLevelEntities(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomersDontExist(List<String> customerCodes) {
        customerCodes.forEach(this::customerWithCodeIsDeleted);
    }

    @Step
    public void userIsAddedToCustomerWithIsPrimary(UserDto u, String customerCode, String isPrimary) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        Response response = addUserToCustomerWithIsPrimary(u.getUserId(), c.getCustomerId(), isPrimary);
        setSessionResponse(response);
    }

    @Step
    public void userIsDeletedFromCustomer(UserDto u, String customerCode) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);

        Response deleteResponse = deleteSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, u.getUserId());
        setSessionResponse(deleteResponse);
    }

    @Step
    public void userDoesntExistForCustomer(UserDto u, String customerCode) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        CustomerUserRelationshipDto userForCustomer = getUserForCustomer(c.getCustomerId(), u.getUserName());
        assertNull("User should not be present in customer", userForCustomer);
    }

    @Step
    public void getCommSubscriptionForCustomerId(String customerId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(customerId,
                "", LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfCustomerCommSubscriptionsIsGotWith(String customerId, String limit, String cursor, String filter,
                                                         String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, "", limit,
                cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public List<CustomerDto> getCustomersForCodes(List<String> customerCodes) {
        String filter = "customer_code=in=(" + StringUtils.join(customerCodes.iterator(), ',') + ")";
        CustomerDto[] customers = getEntities(LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null).as(CustomerDto[].class);
        return Arrays.asList(customers);
    }

    public void removeAllUsersFromCustomers(List<String> codes) {
        codes.forEach(c -> {
            CustomerDto customer = getCustomerByCodeInternal(c);
            if (customer != null) {
                Response customerUsersResponse = getSecondLevelEntities(customer.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null);
                CustomerUserRelationshipDto[] customerUsers = customerUsersResponse.as(CustomerUserRelationshipDto[].class);
                for (CustomerUserRelationshipDto cu : customerUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(customer.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, cu.getUserId());
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set cannot be deleted: " + deleteResponse.getBody().asString());
                    }
                }
            }
        });
    }

    public void listOfUsersIsGotWith(String customerCode, String limit, String cursor, String filter, String sort, String sortDesc) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        Response response = getSecondLevelEntities(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        CustomerUserRelationshipViewDto[] customerUsers = response.as(CustomerUserRelationshipViewDto[].class);
        int i = 0;
        for (CustomerUserRelationshipViewDto cu : customerUsers) {
            assertEquals("Customeruser on index=" + i + " is not expected", usernames.get(i), cu.getUserName());
            i++;
        }
    }

    public void listOfCustomerApiSubscriptionsIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_API_SUBSCRIPTION, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void fieldNameHasValueForPropertyForCustomerAndType(String fieldName, String value, String propertyId, String customerCode, String type) {
        CustomerDto c = getCustomerByCodeInternal(customerCode);
        CustomerPropertyViewDto cp = getCustomerPropertyForCustomerWithType(c.getCustomerId(), propertyId, type);

        switch (fieldName) {
            case "valid_from": {
                assertEquals(value, DateUtils.isoDatefromDate(cp.getValidFrom()));
                break;
            }
            case "valid_to": {
                assertEquals(value, DateUtils.isoDatefromDate(cp.getValidTo()));
                break;
            }
            default:
                fail("Bad field for customer property");
        }
    }
}
