package travel.snapshot.dp.qa.serenity.customers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.*;
import travel.snapshot.dp.qa.helpers.AddressUtils;
import travel.snapshot.dp.qa.helpers.DateUtils;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.io.InputStream;
import java.util.*;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
    public void followingCustomersExistWithRandomAddress(List<CustomerCreateDto> customers) {
        customers.forEach(t -> {
            t.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Customer cannot be created " + createResponse.getBody().asString());
            }
            setSessionResponse(createResponse);
        });
        Serenity.setSessionVariable(SESSION_CUSTOMERS).to(customers);
    }

    @Step
    public void followingCustomersExist(List<CustomerCreateDto> customers) {
        customers.forEach(customer -> {
            Response createResponse = createEntity(customer);
            assertThat("Customer cannot be created " + createResponse.getBody().asString(), createResponse.getStatusCode(), is(HttpStatus.SC_CREATED));
            setSessionResponse(createResponse);
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
    public void followingCustomerIsCreatedWithRandomAddress(CustomerCreateDto customer) {
        customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomerIsCreated(CustomerCreateDto customer) {
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void followingCustomerIsCreatedWithAddress(CustomerCreateDto customer, AddressDto address) {
        customer.setAddress(address);
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        Response response = createEntity(customer);
        setSessionResponse(response);
    }

    @Step
    public void compareCustomerPropertyOnHeaderWithStored(String headerName) {
        CustomerPropertyRelationshipDto originalCustomerProperty = getSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY);
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

    private CustomerPropertyRelationshipDto getCustomerPropertyForCustomerWithType(String customerId, String propertyId, String type) {
        String filter = String.format("property_id==%s;relationship_type==%s", propertyId, type);
        CustomerPropertyRelationshipDto[] customerProperties = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(CustomerPropertyRelationshipDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    public CustomerPropertyRelationshipDto getCustomerPropertyRelationship(String customerId, String propertyId) {
        return getCustomerPropertyRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, customerId, propertyId);
    }

    public CustomerPropertyRelationshipDto getCustomerPropertyRelationshipByUser(String userId, String customerId, String propertyId) {
        String filter = String.format("property_id==%s", propertyId);
        CustomerPropertyRelationshipDto[] customerProperties = getSecondLevelEntitiesByUser(userId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, null, null, filter, null, null, null).as(CustomerPropertyRelationshipDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    private Response addPropertyToCustomerWithTypeFromTo(String propertyId, String customerId, String type, String validFrom, String validTo) {
        return addPropertyToCustomerWithTypeFromToByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, customerId, type, validFrom, validTo);
    }

    @Step
    public Response addPropertyToCustomerWithTypeFromToByUser(String userId, String propertyId, String customerId, String type, String validFrom, String validTo) {
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
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, userId)
                .body(customerProperty)
                .when().post("/{customerId}/properties", customerId);
    }

    @Step
    public Response addUserToCustomerWithIsPrimary(String userId, String customerId, Boolean isPrimary) {
        return addUserToCustomerWithIsPrimaryByUser(DEFAULT_SNAPSHOT_USER_ID, userId, customerId, isPrimary);
    }

    @Step
    public Response addUserToCustomerWithIsPrimaryByUser(String performerId, String userId, String customerId, Boolean isPrimary) {
        Map<String, Object> customerUser = new HashMap<>();
        customerUser.put("user_id", userId);
        customerUser.put("is_primary", isPrimary);

        return given().spec(spec).header(HEADER_XAUTH_USER_ID, performerId)
                .body(customerUser)
                .when().post("/{customerId}/users", customerId);
    }


    public CustomerDto getCustomerById(String id) {
        CustomerDto[] customers = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + id, null, null, null).as(CustomerDto[].class);
        return stream(customers).findFirst().orElse(null);
    }

    public CustomerDto getCustomerByIdByUser(String customerId, String userId) {
        Response response = getEntityByUser(userId, customerId, null);
        CustomerDto customer = response.as(CustomerDto.class);
        setSessionResponse(response);
        return customer;
    }

    @Step
    public void customerWithIdIsGot(String customerId) {
        Response response = getEntity(customerId, null);
        setSessionResponse(response);
    }

    @Step
    public void customerWithIdIsGotByUser(String userId, String customerId) {
        Response response = getEntityByUser(userId, customerId, null);
        setSessionResponse(response);
    }

    @Step
    public void deleteCustomer(String customerId) {
        deleteEntityWithEtag(customerId);
    }

    @Step
    public void deleteCustomerByUser(String userId, String customerId) {
        deleteEntityWithEtagByUser(userId, customerId);
    }

    @Step
    public Response listOfCustomersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfCustomersIsGotByUserWith(DEFAULT_SNAPSHOT_USER_ID, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfCustomersIsGotByUserWith(String userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUser(userId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void updateCustomer(String customerId, CustomerUpdateDto updatedCustomer) {
        updateCustomerByUser(customerId, DEFAULT_SNAPSHOT_USER_ID, updatedCustomer);
    }

    @Step
    public void updateCustomerByUser(String customerId, String userId, CustomerUpdateDto updatedCustomer) {
        try {
            String updatedCustomerString = retrieveData(updatedCustomer).toString();
            assertThat("Empty property update", updatedCustomerString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));

            Response response = updateEntityWithEtagByUser(userId, customerId, updatedCustomerString);
            setSessionResponse(response);
        } catch(JsonProcessingException jsonException){
            fail("Error while converting object to JSON: " + jsonException);
        }
    }

    @Step
    public void updateCustomerAddress(String customerId, AddressUpdateDto updatedAddress) throws Throwable {
        Response temp = getEntity(customerId);
        if (temp.getStatusCode() != HttpStatus.SC_OK) {
            fail("Customer " + customerId + " not found!");
        }

        JSONObject addressJson = new JSONObject();
        JSONObject regexGenerated = RegexValueConverter.transform(retrieveData(updatedAddress));
        addressJson.putOpt("address", regexGenerated);

        Response response = updateEntity(customerId, addressJson.toString(), temp.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }


    @Step
    public void isActiveSetTo(boolean activeFlag, String code) {
        //        Does not work - to be removed and replaced when testing DP-1319
        CustomerDto customer = getCustomerById(code);
        if (activeFlag) {
            assertNotNull("Customer should be returned", customer);
            assertEquals("Customer should have code=" + code, code, customer.getCode());
        } else {
//          Change isActive flag from 0 to true when running against DP version with DP-1319 merged
//           TODO: uncomment assertThat("Customer should have isActive flag set to 0", customer.getIsActive(), equalTo(0));
        }
    }

    @Step
    public void setCustomerIsActive(String customerId, Boolean isActive){
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setIsActive(isActive);
        updateCustomer(customerId, customerUpdate);
    }

    @Step
    public Boolean getCustomerIsActive(String customerId){
        return getCustomerById(customerId).getIsActive();
    }

    @Step
    public void customerWithIdIsGotWithEtag(String customerId) {
        Response tempResponse = getEntity(customerId, null);

        Response resp = getEntity(customerId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void customerWithIdIsGotWithEtagByUser(String customerId, String userId) {
        Response tempResponse = getEntityByUser(userId, customerId, null);

        Response resp = getEntity(customerId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void customerWithIdIsGotWithEtagAfterUpdate(String customerId, String userId) {
        Response tempResponse = getEntityByUser(userId, customerId, null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("vat_id", "CZ99999999");

        Response updateResponse = updateEntityByUser(userId, customerId, mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Customer cannot be updated: " + updateResponse.asString());
        }

        Response resp = getEntityByUser(userId, customerId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void invalidCustomerUpdate(String customerId, Map<String, Object> updateMap) {
        Response response = updateEntity(customerId, updateMap, getEntity(customerId).getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    public void customerWithIdHasData(String customerId, String userId, CustomerDto data) throws Throwable {
        JSONObject customerFromDB = retrieveData(getCustomerByIdByUser(customerId, userId));
        JSONObject updatedData = retrieveData(data);

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
    public void emailsAreInResponseInOrder(List<String> emails) {
        Response response = getSessionResponse();
        CustomerDto[] customers = response.as(CustomerDto[].class);
        int i = 0;
        for (CustomerDto c : customers) {
            contains("Customer on index=" + i + " is not expected", emails.get(i), c.getEmail());
            i++;
        }
    }

    @Step
    public void idsAreInResponseInOrder(List<String> ids) {
        Response response = getSessionResponse();
        PropertyCustomerRelationshipDto[] customers = response.as(PropertyCustomerRelationshipDto[].class);
        int i = 0;
        for (PropertyCustomerRelationshipDto customer : customers) {
            contains("Customer on index=" + i + " is not expected", ids.get(i), customer.getCustomerId());
            i++;
        }
    }

    @Step
    public void propertyIsAddedToCustomerWithTypeFromTo(String propertyId, String customerId, String type, String dateFrom, String dateTo) {
        Response response = addPropertyToCustomerWithTypeFromTo(propertyId, customerId, type, dateFrom, dateTo);

        if (response.statusCode() == HttpStatus.SC_CREATED) {
            setSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY, response.as(CustomerPropertyRelationshipDto.class));
        }
        setSessionResponse(response);
    }

    @Step
    public void relationExistsBetweenPropertyAndCustomerWithTypeFromTo(PropertyDto property, String customerId, String type, String validFrom, String validTo) {
        Response createResponse = addPropertyToCustomerWithTypeFromTo(property.getPropertyId(), customerId, type, validFrom, validTo);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerProperty cannot be created " + createResponse.getBody().asString());
        }
    }

    @Step
    public void propertyIsUpdateForCustomerWithType(PropertyDto property, String customerId, String type, String fieldName, String value) {
        CustomerPropertyRelationshipDto existingCustomerProperty = getCustomerPropertyForCustomerWithType(customerId, property.getPropertyId(), type);
        String etag = getSecondLevelEntityEtag(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId());
        Response updateResponse = updateSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), singletonMap(fieldName, value), etag);
        setSessionResponse(updateResponse);
    }

    @Step
    public void updateCustomerPropertyRelationshipByUser(String userId, String propertyId, String customerId, CustomerPropertyRelationshipUpdateDto relationshipUpdate) {
        String existingRelationshipId = getCustomerPropertyRelationship(customerId, propertyId).getRelationshipId();
        String etag = getSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingRelationshipId, null).header(HEADER_ETAG);

        try {
            JSONObject jsonRelationshipUpdate = retrieveData(relationshipUpdate);
            Response updateResponse = updateSecondLevelEntityByUser(userId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingRelationshipId, jsonRelationshipUpdate, etag);
            setSessionResponse(updateResponse);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }

    }

    public void propertyIsUpdateForCustomerWithTypeWithInvalidEtag(PropertyDto p, String customerId, String type, String fieldName, String value) {
        CustomerPropertyRelationshipDto existingCustomerProperty = getCustomerPropertyForCustomerWithType(customerId, p.getPropertyId(), type);
        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, existingCustomerProperty.getRelationshipId(), data, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void relationExistsBetweenUserAndCustomerWithPrimary(String userId, String customerId, Boolean isPrimary) {
        CustomerUserRelationshipDto existingCustomerUser = getUserForCustomer(customerId, userId);
        if (existingCustomerUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("CustomerUser cannot be deleted " + deleteResponse.getBody().asString());
            }
        }
        Response createResponse = addUserToCustomerWithIsPrimary(userId, customerId, isPrimary);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerUser cannot be created " + createResponse.getBody().asString());
        }
    }

    @Step
    public CustomerUserRelationshipDto getUserForCustomer(String customerId, String userId) {
        Response customerUserResponse = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_id==" + userId, null, null, null);
        return stream(customerUserResponse.as(CustomerUserRelationshipDto[].class)).findFirst().orElse(null);
    }

    @Step
    public void propertyIsgotForCustomerByUser(String userId, String propertyId, String customerId) {
        CustomerPropertyRelationshipDto customerPropertyRelationship = getCustomerPropertyRelationship(customerId, propertyId);
        assertThat(customerPropertyRelationship, is(notNullValue()));

        Response response = getSecondLevelEntityByUser(userId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, customerPropertyRelationship.getRelationshipId(), null);
        setSessionResponse(response);
    }

    @Step
    public void propertyIsgotForCustomerWithType(PropertyDto p, String customerId, String type) {
        CustomerDto c = getCustomerById(customerId);
        setAccessTokenParamFromSession();
        CustomerPropertyRelationshipDto tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);
        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);
        setSessionResponse(response);
    }

    public void propertyIsgotForCustomerWithTypeWithEtag(PropertyDto p, String customerId, String type) {
        CustomerDto c = getCustomerById(customerId);
        CustomerPropertyRelationshipDto tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);

        Response tempResponse = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);

        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    public void propertyIsgotForCustomerWithTypeWithEtagAfterUpdate(PropertyDto p, String customerId, String type) {
        CustomerDto c = getCustomerById(customerId);
        CustomerPropertyRelationshipDto tempCustomerProperty = getCustomerPropertyForCustomerWithType(c.getCustomerId(), p.getPropertyId(), type);

        Response tempResponse = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("valid_to", "2200-01-01");

        Response updateResponse = updateSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), mapForUpdate, tempResponse.getHeader(HEADER_ETAG));

        Response response = getSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_PROPERTIES, tempCustomerProperty.getRelationshipId(), null);
        setSessionResponse(response);
    }

    @Step
    public void getCustomerPropertyWithRelationshipId(String customerId, String relationshipId) {
        Response response = getSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, relationshipId, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertiesIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertiesIsGotByUser(String userId, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, customerId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertySetsIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertySetsIsGotByUser(String userId, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, customerId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void userIsAddedToCustomerWithIsPrimary(UserDto user, String customerId, Boolean isPrimary) {
        Response response = addUserToCustomerWithIsPrimary(user.getUserId(), customerId, isPrimary);
        setSessionResponse(response);
    }

    @Step
    public void userIsDeletedFromCustomer(String userId, String customerId) {
        Response deleteResponse = deleteSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void userIsDeletedFromCustomerByUser(String performerId, String userId, String customerId) {
        Response deleteResponse = deleteSecondLevelEntityByUser(performerId, customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void updateUserCustomerRelationship(String userId, String customerId, UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate) {
        updateUserCustomerRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, userId, customerId, userCustomerRelationshipUpdate);
    }

    @Step
    public void updateUserCustomerRelationshipByUser(String performerId, String userId, String customerId, UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate) {
        try {
            JSONObject jsonUpdate = retrieveData(userCustomerRelationshipUpdate);
            String etag = getSecondLevelEntityEtag(customerId, SECOND_LEVEL_OBJECT_USERS, userId);
            Response response = updateSecondLevelEntityByUser(performerId, customerId, SECOND_LEVEL_OBJECT_USERS, userId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserCustomerRelationshipUpdate object");
        }
    }

    @Step
    public void userDoesntExistForCustomer(String userId, String customerId) {
        CustomerUserRelationshipDto userForCustomer = getUserForCustomer(customerId, userId);
        assertNull("User should not be present in customer", userForCustomer);
    }

    @Step
    public void getCommSubscriptionForCustomerId(String customerId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(customerId,
                SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfCustomerCommSubscriptionsIsGotWith(String customerId, String limit, String cursor, String filter,
                                                         String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_COMMERCIAL_SUBSCRIPTIONS, limit,
                cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public List<CustomerDto> getCustomersForIds(List<String> customerIds) {
        String filter = "customer_id=in=(" + StringUtils.join(customerIds, ',') + ")";
        CustomerDto[] customers = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(CustomerDto[].class);
        return Arrays.asList(customers);
    }

    public void removeAllUsersFromCustomers(List<String> customerIds) {
        customerIds.forEach(customerId -> {
            CustomerDto customer = getCustomerById(customerId);
            if (customer != null) {
                Response customerUsersResponse = getSecondLevelEntities(customer.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                CustomerUserRelationshipDto[] customerUsers = customerUsersResponse.as(CustomerUserRelationshipDto[].class);
                for (CustomerUserRelationshipDto cu : customerUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(customer.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, cu.getUserId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set cannot be deleted: " + deleteResponse.getBody().asString());
                    }
                }
            }
        });
    }

    @Step
    public void listOfUsersIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfUsersIsGotByUser(String userId, String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, customerId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        CustomerUserRelationshipDto[] customerUsers = response.as(CustomerUserRelationshipDto[].class);
        int i = 0;
        for (CustomerUserRelationshipDto cu : customerUsers) {
//            userName is not part of new class - CustomerUserRelationshipDto, needs to be obtained via different endpoint
//          TODO: uncomment  assertEquals("Customeruser on index=" + i + " is not expected", usernames.get(i), cu.getUserName());
            i++;
        }
    }

    public void listOfCustomerApiSubscriptionsIsGotWith(String customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_API_SUBSCRIPTION, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void fieldNameHasValueForPropertyForCustomerAndType(String fieldName, String value, String propertyId, String customerId, String type) {
        CustomerDto c = getCustomerById(customerId);
        CustomerPropertyRelationshipDto cp = getCustomerPropertyForCustomerWithType(c.getCustomerId(), propertyId, type);

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

    public void relationBetweenUserAndCustomerIsDeleted(String userId, String customerId) {
        Response resp = deleteSecondLevelEntity(customerId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(resp);
    }

    public void customerWithIdDoesNotExist(String customerId) {
        Response resp = getEntity(customerId);
        if (resp.getStatusCode() == HttpStatus.SC_OK) {
            fail("Customer should not be present, but it is!");
        }
    }

    public void customerWithIdIsUpdatedWithOutdatedEtag(String customerId) throws JsonProcessingException {
        CustomerUpdateDto updateData = new CustomerUpdateDto();
        updateData.setNotes("UpdatedNotes");

        Response tempResp = getEntity(customerId);
        Response firstUpdate = updateEntity(customerId, retrieveData(updateData).toString(), tempResp.getHeader(HEADER_ETAG));
        Response secondUpdate = updateEntity(customerId, retrieveData(updateData).toString(), tempResp.getHeader(HEADER_ETAG));
        setSessionResponse(secondUpdate);
    }

    public void getCustomerUserRelationByUser(String requestorId, String customerId, String targetUserId) {
        Response response = getSecondLevelEntityByUser( requestorId, customerId, SECOND_LEVEL_OBJECT_USERS, targetUserId, null);
        setSessionResponse(response);
    }
    /*public void checkCustomerActivity(String customerId, boolean activity) {
        CustomerDto customer = getEntity(customerId).as(CustomerDto.class);
        assertEquals(customer.getIsActive(), activity);
    }*/
}
