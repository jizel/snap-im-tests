package travel.snapshot.dp.qa.serenity.property_sets;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

    public void followingPropertySetsExist(List<PropertySetDto> propertySets, String customerId, String userId, Boolean isActive) {
        propertySets.forEach( (PropertySetDto propertySet) -> {
            propertySet.setCustomerId(customerId);
            Response createResponse = createEntity(propertySet);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property set cannot be created: " + createResponse.asString());
            }
            String propertySetId = createResponse.as(PropertySetDto.class).getId();
            Response addUserResponse = addUserToPropertySet(userId, propertySetId, isActive);
            if (addUserResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Failed to add user to property set: " + addUserResponse.asString());
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
        Response response = getEntity(id);
        setSessionResponse(response);
        return response.as(PropertySetDto.class);
    }

    @Step
    public void getPropertySetByUserForApp(String userId, String applicationVersionId, String propertySetId) {
        Response response = getEntityByUserForApplication(userId, applicationVersionId, propertySetId);
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
            String filter = "customer_id==" + c.getId();
            Response entities = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null);
            PropertySetDto[] propertySets = entities.as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                deleteEntityWithEtag(propertySet.getId());
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
    public void listOfPropertySetsIsGotByUserForApp(String userId, String applicationVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, applicationVersionId, null, limit, cursor, filter, sort, sortDesc, null);
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
            assertEquals("Property set on index=" + i + " is not expected", names.get(i), propertySet.getName());
            i++;
        }
    }

    @Step
    public void deletePropertySet(String propertySetId){
        deleteEntityWithEtag(propertySetId);
        setSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID, propertySetId);
    }

    @Step
    public void deletePropertySetByUserForApp(String userId, String applicationVersionId, String propertySetId){
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, propertySetId);
    }

    public void propertySetIdInSessionDoesntExist() {
        String propertySetId = getSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID);

        Response response = getEntity(propertySetId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    public void removeAllUsersForPropertySetsForCustomer(List<String> propertySetNames, CustomerDto c) {
        propertySetNames.forEach(n -> {
            String filter = String.format("name==%s and customer_id==%s", n, c.getId());
            PropertySetDto[] propertySets = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                Response propertyUsersResponse = getSecondLevelEntities(propertySet.getId(), SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                PropertyUserRelationshipDto[] propertyUsers = propertyUsersResponse.as(PropertyUserRelationshipDto[].class);
                for (PropertyUserRelationshipDto pu : propertyUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(propertySet.getId(), SECOND_LEVEL_OBJECT_USERS, pu.getUserId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set user cannot be deleted: status code: " + deleteResponse.statusCode() + ", body: [" + deleteResponse.asString() + "]");
                    }
                }
            }

        });
    }

    public void relationExistsBetweenUserAndPropertySetForCustomer(String userId, String propertySetId, Boolean isActive) {
        PropertySetUserRelationshipDto existingPropertySetUser = getUserForPropertySet(userId, propertySetId);
        if (existingPropertySetUser != null) {
            Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, null);
            if (deleteResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
                fail("PropertySetUser cannot be deleted");
            }
        }
        Response createResponse = addUserToPropertySet(userId, propertySetId, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail("PropertySetUser cannot be created");
        }
    }

    @Step
    public Response addUserToPropertySet(String userId, String propertySetId, Boolean isActive) {
        return addUserToPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertySetId, isActive);
    }

    @Step
    public Response addUserToPropertySetByUserForApp(String performerId, String applicationVersionId, String userId, String propertySetId, Boolean isActive) {
        PropertySetUserRelationshipDto relation = new PropertySetUserRelationshipDto();
        relation.setUserId(userId);
        relation.setIsActive(isActive);
        return createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_USERS, relation);

    }

    @Step
    public PropertySetUserRelationshipDto getUserForPropertySet(String userId, String propertySetId) {
        return getUserForPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertySetId);
    }

    @Step
    public PropertySetUserRelationshipDto getUserForPropertySetByUserForApp(String performerId, String applicationVersionId, String userId, String propertySetId) {
        Response propertySetUserResponse = getSecondLevelEntityByUserForApp(performerId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId);
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
    public void removeUserFromPropertySetByUserForApp(String performerId, String applicationVersionId, String userId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void listOfPropertiesIsGotWith(String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfPropertiesForPropertySetIsGotByUserForApp(String userId, String applicationVersionId, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotWith(String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        listOfUsersForPropertySetIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void listOfUsersForPropertySetIsGotByUserForApp(String userId, String applicationVersionId, String propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_USERS, limit, cursor, filter, sort, sortDesc, null);
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
            String filter = String.format("name==%s and customer_id==%s", psn, customer.getId());
            PropertySetDto[] propertySets = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                Response propertyPropertySetResponse = getSecondLevelEntities(propertySet.getId(), SECOND_LEVEL_OBJECT_PROPERTIES, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                PropertySetPropertyRelationshipDto[] propertyPropertySets = propertyPropertySetResponse.as(PropertySetPropertyRelationshipDto[].class);
                for (PropertySetPropertyRelationshipDto pps : propertyPropertySets) {
                    Response deleteResponse = deleteSecondLevelEntity(propertySet.getId(), SECOND_LEVEL_OBJECT_PROPERTIES, pps.getPropertyId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set property cannot be deleted: " + deleteResponse.asString());
                    }
                }
            }
        });
    }

    @Step
    public void relationExistsBetweenPropertyAndPropertySet(String propertyId, String propertySetId, Boolean isActive) {
        PropertySetPropertyRelationshipDto existingPropertySetUser = getPropertyForPropertySet(propertySetId, propertyId);
        if (existingPropertySetUser != null) {
            Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
            assertThat("PropertySetProperty cannot be deleted", deleteResponse.getStatusCode(), not(HttpStatus.SC_NO_CONTENT));
        }
        Response createResponse = addPropertyToPropertySet(propertyId, propertySetId, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail(String.format("PropertySetProperty cannot be created. Status: %d, %s", createResponse.getStatusCode(), createResponse.asString()));
        }
    }

    public Response addPropertyToPropertySet(String propertyId, String propertySetId, Boolean isActive) {
        return addPropertyToPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, propertySetId, isActive);
    }

    @Step
    public Response addPropertyToPropertySetByUserForApp(String userId, String applicationVersionId, String propertyId, String propertySetId, Boolean isActive) {
        PropertySetPropertyRelationshipDto relation = new PropertySetPropertyRelationshipDto();
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);
        return createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, relation);
    }

    @Step
    public PropertySetPropertyRelationshipDto getPropertyForPropertySet(String propertySetId, String propertyId) {
        return getPropertyForPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, propertyId);
    }

    @Step
    public PropertySetPropertyRelationshipDto getPropertyForPropertySetByUserForApp(String userId, String applicationVersionId, String propertySetId, String propertyId) {
        Response propertySetPropertiesResponse = getSecondLevelEntityByUserForApp(userId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES,  propertyId);
        setSessionResponse(propertySetPropertiesResponse);
        if (propertySetPropertiesResponse.getStatusCode() == HttpStatus.SC_OK) {
            return propertySetPropertiesResponse.as(PropertySetPropertyRelationshipDto.class);
        }
        return null;
    }

    @Step
    public void removePropertyFromPropertySet(String propertyId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void removePropertyFromPropertySetByUserForApp(String userId, String applicationVersionId, String propertyId, String propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void comparePropertySetOnHeaderWithStored(String headerName) {
        PropertySetDto originalProperty = Serenity.sessionVariableCalled(SERENITY_SESSION__CREATED_PROPERTY_SET);
        Response response = Serenity.sessionVariableCalled(SESSION_RESPONSE);
        String propertyLocation = response.header(headerName).replaceFirst(BASE_PATH__PROPERTY_SETS, "");
        given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).header(HEADER_XAUTH_APPLICATION_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID).get(propertyLocation).then()
                .body("property_set_type", is(lowerCase(originalProperty.getType().toString())))
                .body("description", is(originalProperty.getDescription()))
                .body("name", is(originalProperty.getName()))
                .body("customer_id", is(originalProperty.getId()));

    }

    public void propertysetWithIdIsGot(String propertySet) {
        Response response = getEntity(propertySet);
        setSessionResponse(response);
    }

    public void updatePropertySet(String propertySetId, PropertySetUpdateDto propertySetUpdateDto) throws JsonProcessingException {
      updatePropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, propertySetUpdateDto);
    }

    public void updatePropertySetByUserForApp(String userId, String applicationVersionId, String propertySetId, PropertySetUpdateDto propertySetUpdate){
        String etag = getEntity(propertySetId).getHeader(HEADER_ETAG);
        try {
            JSONObject jsonUpdatePropSet = retrieveData(propertySetUpdate);
            Response resp = updateEntityByUserForApplication(userId, applicationVersionId, propertySetId, jsonUpdatePropSet.toString(), etag);
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
        updateUserPropertySetRelationByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertySetId, userPropertySetRelationshipUpdate);
    }

    @Step
    public void updateUserPropertySetRelationByUserForApp(String performerId, String applicationVersionId, String userId, String propertySetId, UserPropertySetRelationshipUpdateDto userPropertySetRelationshipUpdate) {
        try {
            JSONObject jsonUpdate = retrieveData(userPropertySetRelationshipUpdate);
            String etag = getSecondLevelEntityEtag(propertySetId, SECOND_LEVEL_OBJECT_USERS, userId);
            Response response = updateSecondLevelEntityByUserForApp(performerId, applicationVersionId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, jsonUpdate, etag);
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

    public String resolvePropertySetId(String propertySetName) {
        String propertySetId;
        if (isUUID(propertySetName)) {
            propertySetId = propertySetName;
        } else {
            PropertySetDto propertySet = getPropertySetByName(propertySetName);
            assertThat(String.format("Property with code \"%s\" does not exist", propertySetName), propertySet, is(notNullValue()));
            propertySetId = propertySet.getId();
        }
        return propertySetId;
    }

    @Step
    public void setPropertysetPropertyActivity(String propertySetId, String propertyId, boolean activity) throws JsonProcessingException {
        setPropertySetPropertyActivityByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, propertyId, activity);
    }

    @Step
    public void setPropertySetPropertyActivityByUser(String userId, String propertySetId, String propertyId, boolean activity) throws JsonProcessingException {
        String etag = getSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId).getHeader(HEADER_ETAG);
        PropertySetPropertyRelationshipUpdateDto relation = new PropertySetPropertyRelationshipUpdateDto();
        relation.setIsActive(activity);
        try {
            JSONObject obj = retrieveData(relation);
            Response resp = updateSecondLevelEntityByUser(userId, propertySetId, SECOND_LEVEL_OBJECT_PROPERTIES, propertyId, obj, etag);
            setSessionResponse(resp);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from PropertySetPropertyRelationshipUpdateDto object: " + e.toString());
        }
    }

    @Step
    public void setPropertysetUserActivity(String propertySetId, String userId, boolean activity) throws JsonProcessingException {
        setPropertySetUserActivityByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, userId, activity);
    }

    @Step
    public void setPropertySetUserActivityByUser(String requestorId, String propertySetId, String userId, boolean activity) throws JsonProcessingException {
        String etag = getSecondLevelEntity(propertySetId, SECOND_LEVEL_OBJECT_USERS, userId).getHeader(HEADER_ETAG);
        UserPropertySetRelationshipUpdateDto relation = new UserPropertySetRelationshipUpdateDto();
        relation.setIsActive(activity);
        try {
            JSONObject obj = retrieveData(relation);
            Response resp = updateSecondLevelEntityByUser(requestorId, propertySetId, SECOND_LEVEL_OBJECT_USERS, userId, obj, etag);
            setSessionResponse(resp);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from PropertySetUserRelationshipUpdateDto object: " + e.toString());
        }
    }
}
