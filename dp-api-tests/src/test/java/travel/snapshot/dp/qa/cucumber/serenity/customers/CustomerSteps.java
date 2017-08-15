package travel.snapshot.dp.qa.cucumber.serenity.customers;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.COMMERCIAL_SUBSCRIPTIONS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.CUSTOMERS_PATH;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.AddressDto;
import travel.snapshot.dp.api.identity.model.AddressUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerCreateDto;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipPartialUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerPropertyRelationshipType;
import travel.snapshot.dp.api.identity.model.CustomerType;
import travel.snapshot.dp.api.identity.model.CustomerUpdateDto;
import travel.snapshot.dp.api.identity.model.CustomerUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.UserCustomerRelationshipUpdateDto;
import travel.snapshot.dp.api.type.SalesforceId;
import travel.snapshot.dp.qa.cucumber.helpers.AddressUtils;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.helpers.RegexValueConverter;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sedlacek on 9/23/2015.
 */
public class CustomerSteps extends BasicSteps {

    private static final String SESSION_CUSTOMERS = "customers";
    private static final String SESSION_CREATED_CUSTOMER = "created_customer";
    private static final String SESSION_CUSTOMER_ID = "customer_id";
    private static final String SESSION_CREATED_CUSTOMER_PROPERTY = "created_customer_property";
    private static final String DEFAULT_VALID_FROM = "2015-12-31";
    private static final String DEFAULT_VALID_TO = "2050-12-31";
    public static final String DEFAULT_RELATION_TYPE = "chain";

    public CustomerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(CUSTOMERS_PATH);
    }

    @Step
    public void followingCustomersExistWithRandomAddress(List<CustomerCreateDto> customers) {
        customers.forEach(customer -> {
            if (customer.getType() == null) {
                customer.setType(CustomerType.valueOf(DEFAULT_CUSTOMER_TYPE));
            }
            if(customer.getSalesforceId() == null){
                customer.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
            }
            customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
            try {
            JSONObject jsonCustomer = retrieveData(customer);
                Response createResponse = createEntity(jsonCustomer.toString());
                if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                    fail("Customer cannot be created " + createResponse.getBody().asString());
                }
                setSessionResponse(createResponse);
            }catch(JsonProcessingException e){
                fail("Exception while converting to JSON: " + e.toString());
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
    public void followingCustomerIsCreatedWithRandomAddress(CustomerCreateDto customer) {
        customer.setAddress(AddressUtils.createRandomAddress(10, 7, 3, "CZ", null));
        followingCustomerIsCreated(customer);
    }

    @Step
    public void followingCustomerIsCreated(CustomerCreateDto customer) {
        Serenity.setSessionVariable(SESSION_CREATED_CUSTOMER).to(customer);
        try {
            JSONObject jsonCustomer = retrieveData(customer);
            Response response = createEntity(jsonCustomer.toString());
            setSessionResponse(response);
        } catch (JsonProcessingException e){
            fail("Exception while creating JSONObject from CustomerCreateDto: " + e.toString());
        }
    }

    @Step
    public void followingCustomerIsCreatedWithAddress(CustomerCreateDto customer, AddressDto address) {
        customer.setAddress(address);
        followingCustomerIsCreated(customer);
    }

    @Step
    public void compareCustomerPropertyOnHeaderWithStored(String headerName) {
        CustomerPropertyRelationshipPartialDto originalCustomerProperty = getSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String customerLocation = response.header(headerName).replaceFirst(CUSTOMERS_PATH, "");
        given().spec(spec).get(customerLocation).then()
                .body("property_id", is(originalCustomerProperty.getId()))
                //.body("type", is(originalCustomerProperty.getType()))
                .body("valid_from", is(originalCustomerProperty.getValidFrom().format( DateTimeFormatter.ISO_LOCAL_DATE )))
                .body("valid_to", is(originalCustomerProperty.getValidTo().format( DateTimeFormatter.ISO_LOCAL_DATE )));

    }

    private CustomerDto getCustomerFromFile(InputStream inputStream) {
        return from(inputStream).getObject("", CustomerDto.class);
    }

    private CustomerPropertyRelationshipPartialDto getCustomerPropertyForCustomer(UUID customerId, UUID propertyId) {
        String filter = String.format("property_id==%s", propertyId);
        CustomerPropertyRelationshipPartialDto[] customerProperties = getSecondLevelEntities(customerId, PROPERTIES_RESOURCE, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(CustomerPropertyRelationshipPartialDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    public CustomerPropertyRelationshipPartialDto getCustomerPropertyRelationship(UUID customerId, UUID propertyId) {
        return getCustomerPropertyRelationshipByUser(DEFAULT_SNAPSHOT_USER_ID, customerId, propertyId);
    }

    public CustomerPropertyRelationshipPartialDto getCustomerPropertyRelationshipByUser(UUID userId, UUID customerId, UUID propertyId) {
        String filter = String.format("property_id==%s", propertyId);
        CustomerPropertyRelationshipPartialDto[] customerProperties = getSecondLevelEntitiesByUser(userId, customerId, PROPERTIES_RESOURCE, null, null, filter, null, null, null).as(CustomerPropertyRelationshipPartialDto[].class);
        return stream(customerProperties).findFirst().orElse(null);
    }

    @Step
    public Response addPropertyToCustomer(UUID propertyId, UUID customerId, String type, String validFrom, String validTo, Boolean isActive) {
        return addPropertyToCustomerByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, customerId, type, validFrom, validTo, isActive);
    }

    @Step
    public Response addPropertyToCustomerByUser(UUID userId, UUID propertyId, UUID customerId, String type, String validFrom, String validTo, Boolean isActive) {
        return addPropertyToCustomerByUserForApp(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, customerId, type, validFrom, validTo, isActive);
    }

    @Step
    public Response addPropertyToCustomerByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID customerId, String type, String validFrom, String validTo, Boolean isActive) {
        CustomerPropertyRelationshipPartialDto relation = new CustomerPropertyRelationshipPartialDto();
        if (userId == null) {
            userId = DEFAULT_SNAPSHOT_USER_ID;
        }
        if (propertyId == null) {
            propertyId = NON_EXISTENT_ID;
        }
        if (type == null) {
            type = DEFAULT_RELATION_TYPE;
        }
        if (validFrom == null) {
            validFrom = DEFAULT_VALID_FROM;
        }
        if (validTo == null) {
            validTo = DEFAULT_VALID_TO;
        }
        relation.setType(CustomerPropertyRelationshipType.valueOf(type.toUpperCase()));
        relation.setValidFrom(LocalDate.parse(validFrom));
        relation.setValidTo(LocalDate.parse(validTo));
        relation.setIsActive(isActive);
        relation.setPropertyId(propertyId);
        Response response =  createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, customerId, PROPERTIES_RESOURCE, relation);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addPropertyToCustomerByUserForAppInvalid(UUID userId, UUID applicationVersionId, UUID propertyId, UUID customerId, String type, String validFrom, String validTo, Boolean isActive) {
        Map<String, String> customerPropertyRelationMap = new HashMap<>();
        customerPropertyRelationMap.put(RELATIONSHIP_TYPE, type.toUpperCase());
        customerPropertyRelationMap.put(VALID_FROM, validFrom);
        customerPropertyRelationMap.put(VALID_TO, validTo);
        customerPropertyRelationMap.put(IS_ACTIVE, isActive.toString());
        customerPropertyRelationMap.put(PROPERTY_ID, propertyId.toString());
        Response response = createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, customerId, PROPERTIES_RESOURCE, customerPropertyRelationMap);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addUserToCustomer(UUID userId, UUID customerId, Boolean isPrimary, Boolean isActive) {
        return addUserToCustomerByUser(DEFAULT_SNAPSHOT_USER_ID, userId, customerId, isPrimary, isActive);
    }

    @Step
    public Response addUserToCustomerByUser(UUID performerId, UUID userId, UUID customerId, Boolean isPrimary, Boolean isActive) {
        Response response = addUserToCustomerByUserForApp(performerId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, customerId, isPrimary, isActive);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response addUserToCustomerByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID customerId, Boolean isPrimary, Boolean isActive) {
        CustomerUserRelationshipPartialDto relation = new CustomerUserRelationshipPartialDto();
        relation.setUserId(userId);
        relation.setIsPrimary(isPrimary);
        relation.setIsActive(isActive);
        Response response = createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, customerId, USERS_RESOURCE, relation);
        setSessionResponse(response);
        return response;
    }


    public CustomerDto getCustomerById(UUID id) {
        CustomerDto[] customers = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "customer_id==" + id, null, null, null).as(CustomerDto[].class);
        return stream(customers).findFirst().orElse(null);
    }

    public CustomerDto getCustomerByCompanyName(String name) {
        CustomerDto[] customers = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "name==" + name, null, null, null).as(CustomerDto[].class);
        return stream(customers).findFirst().orElse(null);
    }

    public CustomerDto getCustomerByIdByUser(UUID customerId, UUID userId) {
        Response response = getEntityByUser(userId, customerId);
        CustomerDto customer = response.as(CustomerDto.class);
        setSessionResponse(response);
        return customer;
    }

    @Step
    public void customerWithIdIsGot(UUID customerId) {
        customerWithIdIsGotByUser(DEFAULT_SNAPSHOT_USER_ID, customerId);
    }

    @Step
    public void customerWithIdIsGotByUser(UUID userId, UUID customerId) {
        customerWithIdIsGotByUserForApplication(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, customerId);
    }

    @Step
    public void customerWithIdIsGotByUserForApplication(UUID userId, UUID applicationId, UUID customerId) {
        Response response = getEntityByUserForApplication(userId, applicationId, customerId);
        setSessionResponse(response);
    }

    @Step
    public Response deleteCustomer(UUID customerId) {
        return deleteEntityWithEtag(customerId);
    }

    @Step
    public void deleteCustomerByUser(UUID userId, UUID customerId) {
        deleteEntityWithEtagByUser(userId, customerId);
    }

    @Step
    public void deleteCustomerByUserForApp(UUID userId, UUID applicationVersionId, UUID customerId) {
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, customerId);
    }

    @Step
    public Response listOfCustomersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfCustomersIsGotByUserWith(DEFAULT_SNAPSHOT_USER_ID, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfCustomersIsGotByUserWith(UUID userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        return listOfCustomersIsGotByUserForAppVersionWith(userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, limit, cursor, filter, sort, sortDesc);
    }

    @Step
    public Response listOfCustomersIsGotByUserForAppVersionWith(UUID userId, UUID applicationVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, applicationVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public Response updateCustomer(UUID customerId, CustomerUpdateDto updatedCustomer) {
        return updateCustomerByUser(customerId, DEFAULT_SNAPSHOT_USER_ID, updatedCustomer);
    }

    @Step
    public Response updateCustomerByUser(UUID customerId, UUID userId, CustomerUpdateDto updatedCustomer) {
        return updateCustomerByUserForApp(customerId, userId, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, updatedCustomer);
    }

    @Step
    public Response updateCustomerByUserForApp(UUID customerId, UUID userId, UUID applicationVersionId, CustomerUpdateDto updatedCustomer) {
        try {
            String updatedCustomerString = retrieveData(updatedCustomer).toString();
            assertThat("Empty property update", updatedCustomerString, not(equalToIgnoringCase(CURLY_BRACES_EMPTY)));

            String etag = getEntityEtag(customerId);
            Response response = updateEntityByUserForApplication(userId, applicationVersionId, customerId, updatedCustomerString, etag);
            setSessionResponse(response);
            return response;
        } catch(JsonProcessingException jsonException){
            fail("Error while converting object to JSON: " + jsonException);
        }
        return null;
    }

    @Step
    public void updateCustomerAddress(UUID customerId, AddressUpdateDto updatedAddress) throws Throwable {
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
    public void setCustomerIsActive(UUID customerId, Boolean isActive){
        CustomerUpdateDto customerUpdate = new CustomerUpdateDto();
        customerUpdate.setIsActive(isActive);
        updateCustomer(customerId, customerUpdate);
    }

    @Step
    public Boolean getCustomerIsActive(UUID customerId){
        return getCustomerById(customerId).getIsActive();
    }

    @Step
    public void customerWithIdIsGotWithEtag(UUID customerId) {
        Response resp = getEntity(customerId);
        setSessionResponse(resp);
    }

    @Step
    public void customerWithIdIsGotWithEtagByUser(UUID customerId, UUID userId) {
        Response resp = getEntity(customerId);
        setSessionResponse(resp);
    }


    @Step
    public void invalidCustomerUpdate(UUID customerId, Map<String, Object> updateMap) {
        String etag = getEntityEtag(customerId);
        Response response = updateEntity(customerId, updateMap, etag);
        setSessionResponse(response);
    }

    public void customerWithIdHasData(UUID customerId, UUID userId, CustomerDto data) throws Throwable {
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
        CustomerPropertyRelationshipPartialDto[] customers = response.as(CustomerPropertyRelationshipPartialDto[].class);
        int i = 0;
        for (CustomerPropertyRelationshipPartialDto customer : customers) {
            contains("Customer on index=" + i + " is not expected", ids.get(i), customer.getId());
            i++;
        }
    }

    @Step
    public void propertyIsAddedToCustomerFromTo(UUID propertyId, UUID customerId, String type, String dateFrom, String dateTo, Boolean isActive) {
        Response response = addPropertyToCustomer(propertyId, customerId, type, dateFrom, dateTo, isActive);

        if (response.statusCode() == HttpStatus.SC_CREATED) {
            setSessionVariable(SESSION_CREATED_CUSTOMER_PROPERTY, response.as(CustomerPropertyRelationshipPartialDto.class));
        }
        setSessionResponse(response);
    }

    @Step
    public void relationExistsBetweenPropertyAndCustomerWithTypeFromTo(UUID propertyId, UUID customerId, String type, String validFrom, String validTo, Boolean isActive) {
        Response createResponse = addPropertyToCustomer(propertyId, customerId, type, validFrom, validTo, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerProperty cannot be created " + createResponse.getBody().asString());
        }
    }

    @Step
    public void propertyIsUpdatedForCustomerWith(UUID propertyId, UUID customerId, String fieldName, String value) {
        CustomerPropertyRelationshipPartialDto existingCustomerProperty = getCustomerPropertyForCustomer(customerId, propertyId);
        String etag = getSecondLevelEntityEtag(customerId, PROPERTIES_RESOURCE, existingCustomerProperty.getId());
        Response updateResponse = updateSecondLevelEntity(customerId, PROPERTIES_RESOURCE, existingCustomerProperty.getId(), singletonMap(fieldName, value), etag);
        setSessionResponse(updateResponse);
    }

    @Step
    public void updateCustomerPropertyRelationship(UUID propertyId, UUID customerId, CustomerPropertyRelationshipPartialUpdateDto relationshipUpdate) {
        updateCustomerPropertyRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, customerId, relationshipUpdate);
    }

    @Step
    public void updateCustomerPropertyRelationshipByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID customerId, CustomerPropertyRelationshipPartialUpdateDto relationshipUpdate) {
        UUID existingRelationshipId = getCustomerPropertyRelationship(customerId, propertyId).getId();
        String etag = getSecondLevelEntity(customerId, PROPERTIES_RESOURCE, existingRelationshipId).header(HEADER_ETAG);

        try {
            JSONObject jsonRelationshipUpdate = retrieveData(relationshipUpdate);
            Response updateResponse = updateSecondLevelEntityByUserForApp(userId, applicationVersionId, customerId, PROPERTIES_RESOURCE, existingRelationshipId, jsonRelationshipUpdate, etag);
            setSessionResponse(updateResponse);
        } catch(JsonProcessingException e) {
            fail("Exception thrown when trying to map PropertySetPropertyRelationshipUpdateDto to JSONObject: " +  e);
        }

    }

    public void propertyIsUpdateForCustomerWithInvalidEtag(PropertyDto p, UUID customerId, String fieldName, String value) {
        CustomerPropertyRelationshipPartialDto existingCustomerProperty = getCustomerPropertyForCustomer(customerId, p.getId());
        Map<String, Object> data = new HashMap<>();
        data.put(fieldName, value);

        Response updateResponse = updateSecondLevelEntity(customerId, PROPERTIES_RESOURCE, existingCustomerProperty.getId(), data, "invalid");
        setSessionResponse(updateResponse);
    }

    @Step
    public void relationExistsBetweenUserAndCustomer(UUID userId, UUID customerId, Boolean isPrimary, Boolean isActive) {
        CustomerUserRelationshipPartialDto existingCustomerUser = getUserForCustomer(customerId, userId);
        if (existingCustomerUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(customerId, USERS_RESOURCE, userId, null);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("CustomerUser cannot be deleted " + deleteResponse.getBody().asString());
            }
        }
        Response createResponse = addUserToCustomer(userId, customerId, isPrimary, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("CustomerUser cannot be created " + createResponse.getBody().asString());
        }
    }

    @Step
    public CustomerUserRelationshipPartialDto getUserForCustomer(UUID customerId, UUID userId) {
        Response customerUserResponse = getSecondLevelEntities(customerId, USERS_RESOURCE, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_id==" + userId, null, null, null);
        return stream(customerUserResponse.as(CustomerUserRelationshipPartialDto[].class)).findFirst().orElse(null);
    }

    @Step
    public void getPropertyForCustomerByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID customerId) {
        CustomerPropertyRelationshipPartialDto customerPropertyRelationship = getCustomerPropertyRelationship(customerId, propertyId);
        assertThat(customerPropertyRelationship, is(notNullValue()));
        Response response = getSecondLevelEntityByUserForApp(userId, applicationVersionId, customerId, PROPERTIES_RESOURCE, customerPropertyRelationship.getId());
        setSessionResponse(response);
    }

    @Step
    public void getCustomerPropertyWithRelationshipId(UUID customerId, UUID relationshipId) {
        Response response = getSecondLevelEntity(customerId, PROPERTIES_RESOURCE, relationshipId);
        setSessionResponse(response);
    }

    @Step
    public Response listOfCustomerPropertiesIsGotWith(UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, PROPERTIES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
        return response;
    }

    @Step
    public void listOfCustomerPropertiesIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, PROPERTIES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }


    @Step
    public void listOfCustomerPropertySetsIsGotWith(UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, PROPERTY_SETS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfCustomerPropertySetsIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, PROPERTY_SETS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void userIsAddedToCustomer(UUID userId, UUID customerId, Boolean isPrimary, Boolean isActive) {
        Response response = addUserToCustomer(userId, customerId, isPrimary, isActive);
        setSessionResponse(response);
    }

    @Step
    public void userIsDeletedFromCustomer(UUID userId, UUID customerId) {
        Response deleteResponse = deleteSecondLevelEntity(customerId, USERS_RESOURCE, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void userIsDeletedFromCustomerByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID customerId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, customerId, USERS_RESOURCE, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void updateUserCustomerRelationship(UUID userId, UUID customerId, UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate) {
        updateUserCustomerRelationshipByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, customerId, userCustomerRelationshipUpdate);
    }

    @Step
    public void updateUserCustomerRelationshipByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID customerId, UserCustomerRelationshipUpdateDto userCustomerRelationshipUpdate) {
        try {
            JSONObject jsonUpdate = retrieveData(userCustomerRelationshipUpdate);
            String etag = getSecondLevelEntityEtag(customerId, USERS_RESOURCE, userId);
            Response response = updateSecondLevelEntityByUserForApp(performerId, applicationVersionId, customerId, USERS_RESOURCE, userId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserCustomerRelationshipUpdate object");
        }
    }

    @Step
    public void userDoesntExistForCustomer(UUID userId, UUID customerId) {
        CustomerUserRelationshipPartialDto userForCustomer = getUserForCustomer(customerId, userId);
        assertNull("User should not be present in customer", userForCustomer);
    }

    @Step
    public void getCommSubscriptionForCustomerId(UUID customerId) {
        Response appCommSubscriptionResponse = getSecondLevelEntities(customerId,
                COMMERCIAL_SUBSCRIPTIONS_RESOURCE, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(appCommSubscriptionResponse);
    }

    @Step
    public void listOfCustomerCommSubscriptionsIsGotWith(UUID customerId, String limit, String cursor, String filter,
                                                         String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, COMMERCIAL_SUBSCRIPTIONS_RESOURCE, limit,
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
            CustomerDto customer = getCustomerById(UUID.fromString(customerId));
            if (customer != null) {
                Response customerUsersResponse = getSecondLevelEntities(customer.getId(), USERS_RESOURCE, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                CustomerUserRelationshipPartialDto[] customerUsers = customerUsersResponse.as(CustomerUserRelationshipPartialDto[].class);
                for (CustomerUserRelationshipPartialDto cu : customerUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(customer.getId(), USERS_RESOURCE, cu.getUserId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set cannot be deleted: " + deleteResponse.getBody().asString());
                    }
                }
            }
        });
    }

    @Step
    public void listOfUsersIsGotWith(UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, USERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfUsersIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, customerId, USERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        CustomerUserRelationshipPartialDto[] customerUsers = response.as(CustomerUserRelationshipPartialDto[].class);
        int i = 0;
        for (CustomerUserRelationshipPartialDto cu : customerUsers) {
//            userName is not part of new class - CustomerUserRelationshipPartialDto, needs to be obtained via different endpoint
//          TODO: uncomment  assertEquals("Customeruser on index=" + i + " is not expected", usernames.get(i), cu.getUserName());
            i++;
        }
    }

    public void listOfCustomerApiSubscriptionsIsGotWith(UUID customerId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(customerId, SECOND_LEVEL_OBJECT_API_SUBSCRIPTION, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void fieldNameHasValueForPropertyForCustomer(String fieldName, String value, UUID propertyId, UUID customerId) {
        CustomerDto c = getCustomerById(customerId);
        CustomerPropertyRelationshipPartialDto cp = getCustomerPropertyForCustomer(c.getId(), propertyId);

        switch (fieldName) {
            case "valid_from": {
                assertEquals(value, cp.getValidFrom().format( DateTimeFormatter.ISO_LOCAL_DATE));
                break;
            }
            case "valid_to": {
                assertEquals(value, cp.getValidTo().format( DateTimeFormatter.ISO_LOCAL_DATE ));
                break;
            }
            default:
                fail("Bad field for customer property");
        }
    }

    public void relationBetweenUserAndCustomerIsDeleted(UUID userId, UUID customerId) {
        Response resp = deleteSecondLevelEntity(customerId, USERS_RESOURCE, userId, null);
        setSessionResponse(resp);
    }

    public void customerWithIdDoesNotExist(UUID customerId) {
        Response resp = getEntity(customerId);
        if (resp.getStatusCode() == HttpStatus.SC_OK) {
            fail("Customer should not be present, but it is!");
        }
    }

    public void customerWithIdIsUpdatedWithOutdatedEtag(UUID customerId) throws JsonProcessingException {
        CustomerUpdateDto updateData = new CustomerUpdateDto();
        updateData.setNotes("UpdatedNotes");
        updateData.setWebsite(SNAPSHOT_WEBSITE);
        updateData.setPhone(SNAPSHOT_PHONE);
        updateData.setSalesforceId(SalesforceId.of(DEFAULT_SNAPSHOT_SALESFORCE_ID));
        updateData.setVatId(DEFAULT_SNAPSHOT_PARTNER_VAT_ID);
        updateData.setEmail("updatedemail@gmail.com");
        String etag = getEntityEtag(customerId);
        updateEntity(customerId, retrieveData(updateData).toString(), etag);
        Response secondUpdate = updateEntity(customerId, retrieveData(updateData).toString(), etag);
        setSessionResponse(secondUpdate);
    }

    public void getCustomerUserRelationByUser(UUID requestorId, UUID customerId, UUID targetUserId) {
        Response response = getSecondLevelEntityByUser( requestorId, customerId, USERS_RESOURCE, targetUserId);
        setSessionResponse(response);
    }

    public void removeUserFromCustomerByUserForApp(UUID requestorId, UUID appVersionId, UUID customerId, UUID targetUserId) {
        setSessionResponse(deleteSecondLevelEntityByUserForApplication(requestorId, appVersionId, customerId, USERS_RESOURCE, targetUserId, null));
    }

    public UUID resolveCustomerId(String companyName) {
        if (companyName == null) return DEFAULT_SNAPSHOT_USER_ID;

        UUID customerId;
        if (isUUID(companyName)) {
            customerId = UUID.fromString(companyName);
        } else {
            CustomerDto customer = getCustomerByCompanyName(companyName);
            assertThat(String.format("Customer with company name \"%s\" does not exist", companyName), customer, is(notNullValue()));
            customerId = customer.getId();
        }
        return customerId;
    }

}
