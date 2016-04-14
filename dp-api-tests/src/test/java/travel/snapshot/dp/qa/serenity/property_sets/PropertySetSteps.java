package travel.snapshot.dp.qa.serenity.property_sets;

import com.jayway.restassured.response.Response;

import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;

import org.apache.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertyViewDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserViewDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author martin.konkol(at)snapshot.travel Created by Martin Konkol on 9/23/2015.
 */
public class PropertySetSteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTY_SETS = "property_sets";
    private static final String SERENITY_SESSION__CREATED_PROPERTY_SET = "created_property_set";
    private static final String SERENITY_SESSION__PROPERTY_SET_ID = "property_set_id";

    private static final String BASE_PATH__PROPERTY_SETS = "/identity/property_sets";

    public PropertySetSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH__PROPERTY_SETS);
    }

    // --- steps ---

    @Step
    public void followingPropertySetsExistForCustomer(List<PropertySetDto> propertySets, CustomerDto customer) {
        propertySets.forEach(t -> {
            // remove duplicates
            PropertySetDto existingPropertySet = getPropertySetByNameForCustomer(t.getPropertySetName(), customer.getCustomerId());
            if (existingPropertySet != null) {
                deleteEntity(existingPropertySet.getPropertySetId());
            }

            t.setCustomerId(customer.getCustomerId());
            Response createResponse = createEntity(t);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property set cannot be created: " + createResponse.asString());
            }
        });
        Serenity.setSessionVariable(SERENITY_SESSION__PROPERTY_SETS).to(propertySets);
    }

    public PropertySetDto getPropertySetByNameForCustomer(String propertySetName, String customerId) {
        String filter = String.format("name==%s and customer_id==%s", propertySetName, customerId);
        PropertySetDto[] properties = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(PropertySetDto[].class);
        return Arrays.asList(properties).stream().findFirst().orElse(null);
    }

/*
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
                responseWithETag.getHeader(HEADER_ETAG));
        if (updateResponse.getStatusCode() != 204) {
            fail("Property cannot be updated: " + updateResponse.asString());
        }

        // get with old ETag
        Response resp = getProperty(propertyFromList.getPropertyId(), responseWithETag.getHeader(HEADER_ETAG));
        
        // store to session
        Serenity.setSessionVariable(SESSION_RESPONSE).to(resp);
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
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }
    
    */

    public void deleteAllPropertySetsForCustomer(List<CustomerDto> customers) {
        customers.forEach(c -> {
            String filter = "customer_id==" + c.getCustomerId();
            Response entities = getEntities(LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null);
            PropertySetDto[] propertySets = entities.as(PropertySetDto[].class);
            for (PropertySetDto ps : propertySets) {
                Response deleteResponse = deleteEntity(ps.getPropertySetId());
                if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                    fail("Property set cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    public void listOfPropertySetsIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void propertySetNamesAreInResponseInOrder(List<String> names) {
        Response response = getSessionResponse();
        PropertySetDto[] propertySets = response.as(PropertySetDto[].class);
        int i = 0;
        for (PropertySetDto ps : propertySets) {
            assertEquals("Property set on index=" + i + " is not expected", names.get(i), ps.getPropertySetName());
            i++;
        }
    }

    public void followingPropertySetIsCreatedForCustomer(CustomerDto c, PropertySetDto propertySet) {
        PropertySetDto existingPropertySet = getPropertySetByNameForCustomer(propertySet.getPropertySetName(), c.getCustomerId());
        if (existingPropertySet != null) {
            deleteEntity(existingPropertySet.getPropertySetId());
        }

        propertySet.setCustomerId(c.getCustomerId());
        Serenity.setSessionVariable(SERENITY_SESSION__CREATED_PROPERTY_SET).to(propertySet);
        Response createResponse = createEntity(propertySet);
        setSessionResponse(createResponse);
    }

    public void propertySetWithNameForCustomerIsDeleted(CustomerDto c, String propertySetName) {
        PropertySetDto existingPropertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());
        if (existingPropertySet == null) {
            return;
        }
        String propertySetId = existingPropertySet.getPropertySetId();
        Response response = deleteEntity(propertySetId);
        setSessionResponse(response);
        setSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID, propertySetId);
    }

    public void propertySetIdInSessionDoesntExist() {
        String propertySetId = getSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID);

        Response response = getEntity(propertySetId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    public void deletePropertySetWithId(String propertySetId) {
        Response response = deleteEntity(propertySetId);
        setSessionResponse(response);
    }

    public void removeAllUsersForPropertySetsForCustomer(List<String> propertySetNames, CustomerDto c) {
        propertySetNames.forEach(n -> {
            String filter = String.format("property_set_name==%s and customer_id==%s", n, c.getCustomerId());
            PropertySetDto[] propertySets = getEntities(LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null).as(PropertySetDto[].class);
            for (PropertySetDto ps : propertySets) {
                Response propertyUsersResponse = getSecondLevelEntities(ps.getPropertySetId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null);
                UserViewDto[] propertyUsers = propertyUsersResponse.as(UserViewDto[].class);
                for (UserViewDto pu : propertyUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(ps.getPropertySetId(), SECOND_LEVEL_OBJECT_USERS, pu.getUserId());
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set user cannot be deleted: status code: " + deleteResponse.statusCode() + ", body: [" + deleteResponse.asString() + "]");
                    }
                }
            }

        });
    }

    public void relationExistsBetweenUserAndPropertySetForCustomer(UserDto u, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());

        UserViewDto existingPropertySetUser = getUserForPropertySet(propertySet.getPropertySetId(), u.getUserName());
        if (existingPropertySetUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(c.getCustomerId(), SECOND_LEVEL_OBJECT_USERS, u.getUserId());
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertySetUser cannot be deleted");
            }
        }
        Response createResponse = addUserToPropertySet(u.getUserId(), propertySet.getPropertySetId());
        if (createResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("PropertySetUser cannot be created");
        }
    }

    private Response addUserToPropertySet(String userId, String propertySetId) {
        Map<String, Object> propertySetUser = new HashMap<>();
        propertySetUser.put("user_id", userId);
        return given().spec(spec)
                .body(propertySetUser)
                .when().post("/{propertySetId}/users", propertySetId);
    }

    private UserViewDto getUserForPropertySet(String propertySetId, String userName) {
        Response propertySetUserResponse = getSecondLevelEntities(propertySetId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "user_name==" + userName, null, null);
        return Arrays.asList(propertySetUserResponse.as(UserViewDto[].class)).stream().findFirst().orElse(null);
    }

    public void userIsAddedToPropertySetForCustomer(UserDto u, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());

        Response response = addUserToPropertySet(u.getUserId(), propertySet.getPropertySetId());
        setSessionResponse(response);
    }

    public void userIsRemovedFromPropertySetForCustomer(UserDto u, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());

        Response deleteResponse = deleteSecondLevelEntity(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_USERS, u.getUserId());
        setSessionResponse(deleteResponse);
    }

    public void userDoesntExistForPropertySetForCustomer(UserDto u, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());
        UserViewDto existingPropertySetUser = getUserForPropertySet(propertySet.getPropertySetId(), u.getUserName());
        assertNull("User should not be present in propertyset", existingPropertySetUser);
    }

    public void listOfPropertiesIsGotWith(String propertySetName, CustomerDto customer, String limit, String cursor, String filter, String sort, String sortDesc) {
        PropertySetDto p = getPropertySetByNameForCustomer(propertySetName, customer.getCustomerId());
        Response response = getSecondLevelEntities(p.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotWith(String propertySetName, CustomerDto customer, String limit, String cursor, String filter, String sort, String sortDesc) {
        PropertySetDto p = getPropertySetByNameForCustomer(propertySetName, customer.getCustomerId());
        Response response = getSecondLevelEntities(p.getPropertySetId(), SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        UserViewDto[] propertiesUsers = response.as(UserViewDto[].class);
        int i = 0;
        for (UserViewDto pu : propertiesUsers) {
            assertEquals("Propertysetuser on index=" + i + " is not expected", usernames.get(i), pu.getUserName());
            i++;
        }
    }

    public void propertyNamesAreInResponseInOrder(List<String> propertyNames) {
        Response response = getSessionResponse();
        PropertyViewDto[] propertyPropertySets = response.as(PropertyViewDto[].class);
        int i = 0;
        for (PropertyViewDto pu : propertyPropertySets) {
            assertEquals("Propertyuser on index=" + i + " is not expected", propertyNames.get(i), pu.getPropertyName());
            i++;
        }
    }

    public void removeAllPropertiesFromPropertySetsForCustomer(List<String> propertySetNames, CustomerDto customer) {
        propertySetNames.forEach(psn -> {
            String filter = String.format("property_set_name==%s and customer_id==%s", psn, customer.getCustomerId());
            PropertySetDto[] propertySets = getEntities(LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null).as(PropertySetDto[].class);
            for (PropertySetDto ps : propertySets) {
                Response propertyPropertySetResponse = getSecondLevelEntities(ps.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null);
                PropertyViewDto[] propertyPropertySets = propertyPropertySetResponse.as(PropertyViewDto[].class);
                for (PropertyViewDto pps : propertyPropertySets) {
                    Response deleteResponse = deleteSecondLevelEntity(ps.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, pps.getPropertyId());
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set property cannot be deleted: " + deleteResponse.asString());
                    }
                }
            }
        });
    }

    public void relationExistsBetweenPropertyAndPropertySetForCustomer(PropertyDto p, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());

        PropertyViewDto existingPropertySetUser = getPropertyForPropertySet(propertySet.getPropertySetId(), p.getPropertyId());
        if (existingPropertySetUser != null) {

            Response deleteResponse = deleteSecondLevelEntity(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, p.getPropertyId());
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertySetProperty cannot be deleted");
            }
        }
        Response createResponse = addPropertyToPropertySet(p.getPropertyId(), propertySet.getPropertySetId());
        if (createResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail(String.format("PropertySetProperty cannot be created. Status: %d, %s", createResponse.getStatusCode(), createResponse.asString()));
        }
    }

    private Response addPropertyToPropertySet(String propertyId, String propertySetId) {
        Map<String, Object> propertySetProperty = new HashMap<>();
        propertySetProperty.put("property_id", propertyId);
        return given().spec(spec)
                .body(propertySetProperty)
                .when().post("/{propertySetId}/properties", propertySetId);
    }

    private PropertyViewDto getPropertyForPropertySet(String propertySetId, String propertyId) {
        Response propertySetPropertiesResponse = getSecondLevelEntities(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "property_id==" + propertyId, null, null);
        return Arrays.asList(propertySetPropertiesResponse.as(PropertyViewDto[].class)).stream().findFirst().orElse(null);
    }

    public void propertiesDoesntExistForPropertySetForCustomer(PropertyDto p, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());
        PropertyViewDto existingPropertySetProperty = getPropertyForPropertySet(propertySet.getPropertySetId(), p.getPropertyId());
        assertNull("Property should not be present in propertyset", existingPropertySetProperty);
    }

    public void propertyIsAddedToPropertySetForCustomer(PropertyDto p, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());

        Response response = addPropertyToPropertySet(p.getPropertyId(), propertySet.getPropertySetId());
        setSessionResponse(response);
    }

    public void propertyIsRemovedFromPropertySetForCustomer(PropertyDto p, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());

        Response deleteResponse = deleteSecondLevelEntity(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, p.getPropertyId());
        setSessionResponse(deleteResponse);
    }

    @Step
    public void comparePropertySetOnHeaderWithStored(String headerName) {
        PropertySetDto originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY_SET);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String propertyLocation = response.header(headerName).replaceFirst(BASE_PATH__PROPERTY_SETS, "");
        given().spec(spec).get(propertyLocation).then()
                .body("property_set_type", is(originalProperty.getPropertySetType()))
                .body("property_set_description", is(originalProperty.getPropertySetDescription()))
                .body("property_set_name", is(originalProperty.getPropertySetName()))
                .body("customer_id", is(originalProperty.getCustomerId()));

    }

    public void propertysetWithIdIsGot(String propertySet) {
        Response response = getEntity(propertySet, null);
        setSessionResponse(response);
    }
}
