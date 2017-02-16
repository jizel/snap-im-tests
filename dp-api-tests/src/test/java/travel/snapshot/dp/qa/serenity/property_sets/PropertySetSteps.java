package travel.snapshot.dp.qa.serenity.property_sets;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertyDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.*;

/**
 * @author martin.konkol(at)snapshot.travel Created by Martin Konkol on 9/23/2015.
 */
public class PropertySetSteps extends BasicSteps {

    private static final String SERENITY_SESSION__PROPERTY_SETS = "property_sets";
    private static final String SERENITY_SESSION__CREATED_PROPERTY_SET = "created_property_set";
    private static final String SERENITY_SESSION__PROPERTY_SET_ID = "property_set_id";
    private static final String PROPERTY_ID_KEY = "property_id";

    private static final String BASE_PATH__PROPERTY_SETS = "/identity/property_sets";

    public PropertySetSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH__PROPERTY_SETS);
    }

    // --- steps ---

    public void followingPropertySetsExist(List<PropertySetDto> propertySets, String customerId, String userId) {
        propertySets.forEach(propertySet -> {
            propertySet.setCustomerId(customerId);
            Response createResponse = createEntity(propertySet);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property set cannot be created: " + createResponse.asString());
            }
            Response addUserResponse = addUserToPropertySet(userId, propertySet.getPropertySetId());
            if (addUserResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property set cannot be created: " + addUserResponse.asString());
            }
        });
    }

    public void followingPropertySetIsCreated(PropertySetDto PropertySetDto, String customerId) {
        PropertySetDto.setCustomerId(customerId);

        Response resp = createEntity(PropertySetDto);
        setSessionResponse(resp);
        setSessionVariable(SERENITY_SESSION__CREATED_PROPERTY_SET, PropertySetDto);
    }

    @Step
    public PropertySetDto getPropertySet(String id) {
        Response response = getEntity(id, null);
        setSessionResponse(response);
        return response.as(PropertySetDto.class);
    }

    @Step
    public void getPropertySetByUser(String userId, String propertySetId) {
        Response response = getEntityByUser(userId, propertySetId);
        setSessionResponse(response);
    }

    public PropertySetDto getPropertySetByNameForCustomer(String propertySetName, String customerId) {
        String filter = String.format("name==%s and customer_id==%s", propertySetName, customerId);
        PropertySetDto[] properties = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
        return stream(properties).findFirst().orElse(null);
    }

    public PropertySetDto getPropertySetByName(String propertySetName) {
        String filter = String.format("name==%s", propertySetName);
        PropertySetDto[] properties = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
        return stream(properties).findFirst().orElse(null);
    }

    public void deleteAllPropertySetsForCustomer(List<CustomerDto> customers) {
        customers.forEach(c -> {
            String filter = "customer_id==" + c.getCustomerId();
            Response entities = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null);
            PropertySetDto[] propertySets = entities.as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                deleteEntityWithEtag(propertySet.getPropertySetId());
                Response deleteResponse = getSessionResponse();
                if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                    fail("Property set cannot be deleted: " + deleteResponse.asString());
                }
            }
        });
    }

    @Step
    public void listOfPropertySetsIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfPropertySetsIsGotByUser(String userId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUser(userId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void propertySetNamesAreInResponseInOrder(List<String> names) {
        if (names.isEmpty()) {
            return;
        }
        Response response = getSessionResponse();
        PropertySetDto[] propertySets = response.as(PropertySetDto[].class);
        int i = 0;
        for (PropertySetDto propertySet : propertySets) {
            assertEquals("Property set on index=" + i + " is not expected", names.get(i), propertySet.getPropertySetName());
            i++;
        }
    }

    @Step
    public void deletePropertySet(String propertySetId){
        deleteEntityWithEtag(propertySetId);
        setSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID, propertySetId);
    }

    @Step
    public void deletePropertySetByUser(String userId, String propertySetId){
        deleteEntityWithEtagByUser(userId, propertySetId);
    }

    public void propertySetIdInSessionDoesntExist() {
        String propertySetId = getSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID);

        Response response = getEntity(propertySetId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    public void deletePropertySetWithId(String propertySetId) {
        deleteEntityWithEtag(propertySetId);
    }

    public void removeAllUsersForPropertySetsForCustomer(List<String> propertySetNames, CustomerDto c) {
        propertySetNames.forEach(n -> {
            String filter = String.format("name==%s and customer_id==%s", n, c.getCustomerId());
            PropertySetDto[] propertySets = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                Response propertyUsersResponse = getSecondLevelEntities(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                PropertyUserRelationshipDto[] propertyUsers = propertyUsersResponse.as(PropertyUserRelationshipDto[].class);
                for (PropertyUserRelationshipDto pu : propertyUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_USERS, pu.getUserId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set user cannot be deleted: status code: " + deleteResponse.statusCode() + ", body: [" + deleteResponse.asString() + "]");
                    }
                }
            }

        });
    }

    public void relationExistsBetweenUserAndPropertySetForCustomer(String userId, String propertySetId) {
        PropertySetUserRelationshipDto existingPropertySetUser = getUserForPropertySet(userId, propertySetId);
        if (existingPropertySetUser != null) {
            Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, null);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertySetUser cannot be deleted");
            }
        }
        Response createResponse = addUserToPropertySet(userId, propertySetId);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("PropertySetUser cannot be created");
        }
    }

    @Step
    public Response addUserToPropertySet(String userId, String propertySetId) {
        return addUserToPropertySetByUser(DEFAULT_SNAPSHOT_USER_ID, userId, propertySetId);
    }

    public Response addUserToPropertySetByUser(String performerId, String userId, String propertySetId) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, performerId)
                .body(singletonMap("user_id", userId))
                .when().post("/{propertySetId}/users", propertySetId);
    }

    @Step
    public PropertySetUserRelationshipDto getUserForPropertySet(String userId, String propertySetId) {
        return getUserForPropertySetByUser(DEFAULT_SNAPSHOT_USER_ID, userId, propertySetId);
    }

    @Step
    public PropertySetUserRelationshipDto getUserForPropertySetByUser(String performerId, String userId, String propertySetId) {
        Response propertySetUserResponse = getSecondLevelEntityByUser(performerId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(propertySetUserResponse);
        PropertySetUserRelationshipDto result;
        try {
            result = propertySetUserResponse.as(PropertySetUserRelationshipDto.class);
        } catch (Exception e) {
            // Stupid java does not want to accept the exact UnrecognizedPropertyException here, therefore I have to use the generic one
            result = null;
        }
        return result;
    }

    @Step
    public void removeUserFromPropertySet(String userId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void removeUserFromPropertySetByUser(String performerId, String userId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUser(performerId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void listOfPropertiesIsGotWith(String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfPropertiesForPropertySetIsGotByUser(String userId, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotWith(String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        listOfUsersForPropertySetIsGotByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void listOfUsersForPropertySetIsGotByUser(String userId, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUser(userId, propertySetId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        PropertyUserRelationshipDto[] propertiesUsers = response.as(PropertyUserRelationshipDto[].class);
        int i = 0;
        for (PropertyUserRelationshipDto pu : propertiesUsers) {
//            userName is not part of new class - PropertyUserRelationShip, needs to be obtained via different endpoint
//            assertEquals("Propertysetuser on index=" + i + " is not expected", usernames.get(i), pu.getUserName());
            i++;
        }
    }

    public void propertyNamesAreInResponseInOrder(List<String> propertyNames) {
        Response response = getSessionResponse();
        PropertySetPropertyRelationshipDto[] propertyPropertySets = response.as(PropertySetPropertyRelationshipDto[].class);
        int i = 0;
        for (PropertySetPropertyRelationshipDto pu : propertyPropertySets) {
//            propertyName is not part of new class - PropertySetPropertyRelationshipDto, needs to be obtained via different endpoint
//            assertEquals("Propertyuser on index=" + i + " is not expected", propertyNames.get(i), pu.getPropertyName());
            i++;
        }
    }

    public void removeAllPropertiesFromPropertySetsForCustomer(List<String> propertySetNames, CustomerDto customer) {
        propertySetNames.forEach(psn -> {
            String filter = String.format("name==%s and customer_id==%s", psn, customer.getCustomerId());
            PropertySetDto[] propertySets = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                Response propertyPropertySetResponse = getSecondLevelEntities(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                PropertySetPropertyRelationshipDto[] propertyPropertySets = propertyPropertySetResponse.as(PropertySetPropertyRelationshipDto[].class);
                for (PropertySetPropertyRelationshipDto pps : propertyPropertySets) {
                    Response deleteResponse = deleteSecondLevelEntity(propertySet.getPropertySetId(), SECOND_LEVEL_OBJECT_PROPERTIES, pps.getPropertyId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set property cannot be deleted: " + deleteResponse.asString());
                    }
                }
            }
        });
    }

    @Step
    public void relationExistsBetweenPropertyAndPropertySet(String propertyId, String propertySetId) {
        PropertySetPropertyRelationshipDto existingPropertySetUser = getPropertyForPropertySet(propertySetId, propertyId);
        if (existingPropertySetUser != null) {
            Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
            assertThat("PropertySetProperty cannot be deleted", deleteResponse.getStatusCode(), not(HttpStatus.SC_NO_CONTENT));
        }
        Response createResponse = addPropertyToPropertySet(propertyId, propertySetId);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail(String.format("PropertySetProperty cannot be created. Status: %d, %s", createResponse.getStatusCode(), createResponse.asString()));
        }
    }

    public Response addPropertyToPropertySet(String propertyId, String propertySetId) {
        return addPropertyToPropertySetByUser(DEFAULT_SNAPSHOT_USER_ID, propertyId, propertySetId);
    }

    @Step
    public Response addPropertyToPropertySetByUser(String userId, String propertyId, String propertySetId) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, userId)
                .body(singletonMap(PROPERTY_ID_KEY, propertyId))
                .when().post("/{propertySetId}/properties", propertySetId);
    }

    @Step
    public PropertySetPropertyRelationshipDto getPropertyForPropertySet(String propertySetId, String propertyId) {
        return getPropertyForPropertySetByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, propertyId);
    }

    @Step
    public PropertySetPropertyRelationshipDto getPropertyForPropertySetByUser(String userId, String propertySetId, String propertyId) {
        Response propertySetPropertiesResponse = getSecondLevelEntityByUser(userId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES,  propertyId, null);
        setSessionResponse(propertySetPropertiesResponse);
        if (propertySetPropertiesResponse.getStatusCode() == HttpStatus.SC_OK) {
            return propertySetPropertiesResponse.as(PropertySetPropertyRelationshipDto.class);
        }
        return null;
    }

    public void propertiesDoesntExistForPropertySetForCustomer(PropertyDto p, String propertySetName, CustomerDto c) {
        PropertySetDto propertySet = getPropertySetByNameForCustomer(propertySetName, c.getCustomerId());
        PropertySetPropertyRelationshipDto existingPropertySetProperty = getPropertyForPropertySet(propertySet.getPropertySetId(), p.getPropertyId());
        assertNull("Property should not be present in propertyset", existingPropertySetProperty);
    }


    @Step
    public void removePropertyFromPropertySet(String propertyId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void removePropertyFromPropertySetByUser(String userId, String propertyId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUser(userId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void comparePropertySetOnHeaderWithStored(String headerName) {
        PropertySetDto originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY_SET);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String propertyLocation = response.header(headerName).replaceFirst(BASE_PATH__PROPERTY_SETS, "");
        given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).get(propertyLocation).then()
                .body("property_set_type", is(lowerCase(originalProperty.getPropertySetType().toString())))
                .body("description", is(originalProperty.getPropertySetDescription()))
                .body("name", is(originalProperty.getPropertySetName()))
                .body("customer_id", is(originalProperty.getCustomerId()));

    }

    public void propertysetWithIdIsGot(String propertySet) {
        Response response = getEntity(propertySet, null);
        setSessionResponse(response);
    }

    public void updatePropertySet(String propertySetId, PropertySetUpdateDto propertySetUpdateDto) throws JsonProcessingException {
      updatePropertySetByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, propertySetUpdateDto);
    }

    public void updatePropertySetByUser(String userId, String propertySetId, PropertySetUpdateDto propertySetUpdate){
        String etag = getEntity(propertySetId).getHeader(HEADER_ETAG);
        try {
            JSONObject jsonUpdatePropSet = retrieveData(propertySetUpdate);
            Response resp = updateEntityByUser(userId, propertySetId, jsonUpdatePropSet.toString(), etag);
            setSessionResponse(resp);

        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from PropertySetUpdateDto object");
        }
    }

    public void comparePropertySets(String propertySetId, PropertySetUpdateDto propertySetUpdateDto) throws JsonProcessingException {
        JSONObject propertySetFromDb = retrieveData(getPropertySet(propertySetId));
        JSONObject udpatedData = retrieveData(propertySetUpdateDto);

        Iterator<?> updatedDataKeys = udpatedData.keys();

        while (updatedDataKeys.hasNext()) {
            String key = (String) updatedDataKeys.next();

            Object updatedValue = udpatedData.get(key);
            Object databaseValue = propertySetFromDb.get(key);

            assertEquals(updatedValue, databaseValue);
        }
    }

    @Step
    public void updateUserPropertySetRelation(String userId, String propertySetId, UserPropertySetRelationshipUpdateDto userPropertySetRelationshipUpdate) {
        updateUserPropertySetRelationByUser(DEFAULT_SNAPSHOT_USER_ID, userId, propertySetId, userPropertySetRelationshipUpdate);
    }

    @Step
    public void updateUserPropertySetRelationByUser(String performerId, String userId, String propertySetId, UserPropertySetRelationshipUpdateDto userPropertySetRelationshipUpdate) {
        try {
            JSONObject jsonUpdate = retrieveData(userPropertySetRelationshipUpdate);
            String etag = getSecondLevelEntityEtag(propertySetId, SECOND_LEVEL_OBJECT_USERS, userId);
            Response response = updateSecondLevelEntityByUser(performerId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserPropertyRelationshipUpdateDto object");
        }
    }

    @Step
    public Response getChildPropertySetsByUser(String userId, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams){
        Response response = getSecondLevelEntitiesByUser(userId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTY_SETS, limit, cursor, filter, sort, sortDesc, queryParams);
        setSessionResponse(response);
        return response;
    }
}
