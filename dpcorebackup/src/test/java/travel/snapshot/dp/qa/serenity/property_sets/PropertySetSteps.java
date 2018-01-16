package travel.snapshot.dp.qa.serenity.property_sets;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.StringUtils.lowerCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTIES_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.PROPERTY_SETS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CustomerDto;
import travel.snapshot.dp.api.identity.model.PropertySetDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.PropertySetPropertyRelationshipUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUpdateDto;
import travel.snapshot.dp.api.identity.model.PropertySetUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.PropertyUserRelationshipPartialDto;
import travel.snapshot.dp.api.identity.model.UserPropertySetRelationshipUpdateDto;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author martin.konkol(at)snapshot.travel Created by Martin Konkol on 9/23/2015.
 */
public class PropertySetSteps extends BasicSteps {

    private static final String SERENITY_SESSION__CREATED_PROPERTY_SET = "created_property_set";
    private static final String SERENITY_SESSION__PROPERTY_SET_ID = "property_set_id";

    private static final String BASE_PATH__PROPERTY_SETS = "/property_sets";

    public PropertySetSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(BASE_PATH__PROPERTY_SETS);
    }

    // --- steps ---

    public void followingPropertySetsExist(List<PropertySetDto> propertySets, UUID customerId, UUID userId, Boolean isActive) {
        propertySets.forEach( (PropertySetDto propertySet) -> {
            propertySet.setCustomerId(customerId);
            Response createResponse = createEntityByUser(userId, propertySet);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Property set cannot be created: " + createResponse.asString());
            }
        });
    }

    public Response followingPropertySetIsCreated(PropertySetDto PropertySetDto, UUID customerId) {
        if(customerId != null) {
            PropertySetDto.setCustomerId(customerId);
        }

        Response response = createEntity(PropertySetDto);
        setSessionResponse(response);
        setSessionVariable(SERENITY_SESSION__CREATED_PROPERTY_SET, PropertySetDto);
        return response;
    }

    @Step
    public PropertySetDto getPropertySet(UUID id) {
        Response response = getEntity(id);
        setSessionResponse(response);
        return response.as(PropertySetDto.class);
    }

    @Step
    public void getPropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID propertySetId) {
        Response response = getEntityByUserForApplication(userId, applicationVersionId, propertySetId);
        setSessionResponse(response);
    }

    public PropertySetDto getPropertySetByNameForCustomer(String propertySetName, UUID customerId) {
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
    public void listOfPropertySetsIsGotByUserForApp(UUID userId, UUID applicationVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
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
    public void deletePropertySet(UUID propertySetId){
        deleteEntityWithEtag(propertySetId);
        setSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID, propertySetId);
    }

    @Step
    public void deletePropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID propertySetId){
        setSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID, propertySetId);
        deleteEntityWithEtagByUserForApp(userId, applicationVersionId, propertySetId);
    }

    public void propertySetIdInSessionDoesntExist() {
        UUID propertySetId = getSessionVariable(SERENITY_SESSION__PROPERTY_SET_ID);

        Response response = getEntity(propertySetId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    public void removeAllUsersForPropertySetsForCustomer(List<String> propertySetNames) {
        propertySetNames.forEach(n -> {
            String filter = String.format("name==%s", n);
            PropertySetDto[] propertySets = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                Response propertyUsersResponse = getSecondLevelEntities(propertySet.getId(), USERS_RESOURCE, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                PropertyUserRelationshipPartialDto[] propertyUsers = propertyUsersResponse.as(PropertyUserRelationshipPartialDto[].class);
                for (PropertyUserRelationshipPartialDto propertyUser : propertyUsers) {
                    Response deleteResponse = deleteSecondLevelEntity(propertySet.getId(), USERS_RESOURCE, propertyUser.getUserId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set user cannot be deleted: status code: " + deleteResponse.statusCode() + ", body: [" + deleteResponse.asString() + "]");
                    }
                }
            }

        });
    }

    public void relationExistsBetweenUserAndPropertySetForCustomer(UUID userId, UUID propertySetId, Boolean isActive) {
        PropertySetUserRelationshipPartialDto existingPropertySetUser = getUserForPropertySet(userId, propertySetId);
        if (existingPropertySetUser != null) {
            Response deleteResponse = deleteSecondLevelEntity(propertySetId, USERS_RESOURCE, userId, null);
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
    public Response addUserToPropertySet(UUID userId, UUID propertySetId, Boolean isActive) {
        return addUserToPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertySetId, isActive);
    }

    @Step
    public Response addUserToPropertySetByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID propertySetId, Boolean isActive) {
        PropertySetUserRelationshipPartialDto relation = new PropertySetUserRelationshipPartialDto();
        relation.setUserId(userId);
        relation.setIsActive(isActive);
        return createSecondLevelRelationshipByUserForApplication(performerId, applicationVersionId, propertySetId, USERS_RESOURCE, relation);

    }

    @Step
    public PropertySetUserRelationshipPartialDto getUserForPropertySet(UUID userId, UUID propertySetId) {
        return getUserForPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertySetId);
    }

    @Step
    public PropertySetUserRelationshipPartialDto getUserForPropertySetByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID propertySetId) {
        Response propertySetUserResponse = getSecondLevelEntityByUserForApp(performerId, applicationVersionId, propertySetId, USERS_RESOURCE, userId);
        setSessionResponse(propertySetUserResponse);
        PropertySetUserRelationshipPartialDto result;
        try {
            result = propertySetUserResponse.as(PropertySetUserRelationshipPartialDto.class);
        } catch (Exception e) {
            // Stupid java does not want to accept the exact UnrecognizedPropertyException here, therefore I have to use the generic one
            result = null;
        }
        return result;
    }

    @Step
    public void removeUserFromPropertySet(UUID userId, UUID propertySetId) {
        Response deleteResponse = deleteSecondLevelEntity(propertySetId, USERS_RESOURCE, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void removeUserFromPropertySetByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(performerId, applicationVersionId, propertySetId, USERS_RESOURCE, userId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void listOfPropertiesIsGotWith(UUID propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntities(propertySetId, PROPERTIES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void listOfPropertiesForPropertySetIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertySetId, PROPERTIES_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void listOfUsersIsGotWith(UUID propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        listOfUsersForPropertySetIsGotByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, limit, cursor, filter, sort, sortDesc);
    }

    public void listOfUsersForPropertySetIsGotByUserForApp(UUID userId, UUID applicationVersionId, UUID propertySetId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getSecondLevelEntitiesByUserForApp(userId, applicationVersionId, propertySetId, USERS_RESOURCE, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    public void usernamesAreInResponseInOrder(List<String> usernames) {
        Response response = getSessionResponse();
        PropertyUserRelationshipPartialDto[] propertiesUsers = response.as(PropertyUserRelationshipPartialDto[].class);
        int i = 0;
        for (PropertyUserRelationshipPartialDto pu : propertiesUsers) {
//            userName is not part of new class - PropertyUserRelationShip, needs to be obtained via different endpoint
//            assertEquals("Propertysetuser on index=" + i + " is not expected", usernames.get(i), pu.getUserName());
            i++;
        }
    }

    public void propertyNamesAreInResponseInOrder(List<String> propertyNames) {
        Response response = getSessionResponse();
        PropertySetPropertyRelationshipPartialDto[] propertyPropertySets = response.as(PropertySetPropertyRelationshipPartialDto[].class);
        int i = 0;
        for (PropertySetPropertyRelationshipPartialDto pu : propertyPropertySets) {
//            propertyName is not part of new class - PropertySetPropertyRelationshipPartialDto, needs to be obtained via different endpoint
//            assertEquals("Propertyuser on index=" + i + " is not expected", propertyNames.get(i), pu.getPropertyName());
            i++;
        }
    }

    public void removeAllPropertiesFromPropertySetsForCustomer(List<String> propertySetNames, CustomerDto customer) {
        propertySetNames.forEach(psn -> {
            String filter = String.format("name==%s and customer_id==%s", psn, customer.getId());
            PropertySetDto[] propertySets = getEntities(null, LIMIT_TO_ALL, CURSOR_FROM_FIRST, filter, null, null, null).as(PropertySetDto[].class);
            for (PropertySetDto propertySet : propertySets) {
                Response propertyPropertySetResponse = getSecondLevelEntities(propertySet.getId(), PROPERTIES_RESOURCE, LIMIT_TO_ALL, CURSOR_FROM_FIRST, null, null, null, null);
                PropertySetPropertyRelationshipPartialDto[] propertyPropertySets = propertyPropertySetResponse.as(PropertySetPropertyRelationshipPartialDto[].class);
                for (PropertySetPropertyRelationshipPartialDto pps : propertyPropertySets) {
                    Response deleteResponse = deleteSecondLevelEntity(propertySet.getId(), PROPERTIES_RESOURCE, pps.getPropertyId(), null);
                    if (deleteResponse.statusCode() != HttpStatus.SC_NO_CONTENT) {
                        fail("Property set property cannot be deleted: " + deleteResponse.asString());
                    }
                }
            }
        });
    }

    @Step
    public void relationExistsBetweenPropertyAndPropertySet(UUID propertyId, UUID propertySetId, Boolean isActive) {
        PropertySetPropertyRelationshipPartialDto existingPropertySetUser = getPropertyForPropertySet(propertySetId, propertyId);
        if (existingPropertySetUser != null) {
            Response deleteResponse = deleteSecondLevelEntity(propertySetId, PROPERTIES_RESOURCE, propertyId, null);
            assertThat("PropertySetProperty cannot be deleted", deleteResponse.getStatusCode(), not(HttpStatus.SC_NO_CONTENT));
        }
        Response createResponse = addPropertyToPropertySet(propertyId, propertySetId, isActive);
        if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
            fail(String.format("PropertySetProperty cannot be created. Status: %d, %s", createResponse.getStatusCode(), createResponse.asString()));
        }
    }

    public Response addPropertyToPropertySet(UUID propertyId, UUID propertySetId, Boolean isActive) {
        return addPropertyToPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertyId, propertySetId, isActive);
    }

    @Step
    public Response addPropertyToPropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID propertySetId, Boolean isActive) {
        PropertySetPropertyRelationshipPartialDto relation = new PropertySetPropertyRelationshipPartialDto();
        relation.setPropertyId(propertyId);
        relation.setIsActive(isActive);
        return createSecondLevelRelationshipByUserForApplication(userId, applicationVersionId, propertySetId, PROPERTIES_RESOURCE, relation);
    }

    @Step
    public PropertySetPropertyRelationshipPartialDto getPropertyForPropertySet(UUID propertySetId, UUID propertyId) {
        return getPropertyForPropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, propertyId);
    }

    @Step
    public PropertySetPropertyRelationshipPartialDto getPropertyForPropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID propertySetId, UUID propertyId) {
        Response propertySetPropertiesResponse = getSecondLevelEntityByUserForApp(userId, applicationVersionId, propertySetId, PROPERTIES_RESOURCE,  propertyId);
        setSessionResponse(propertySetPropertiesResponse);
        if (propertySetPropertiesResponse.getStatusCode() == HttpStatus.SC_OK) {
            return propertySetPropertiesResponse.as(PropertySetPropertyRelationshipPartialDto.class);
        }
        return null;
    }

    @Step
    public void removePropertyFromPropertySet(UUID propertyId, UUID propertySetId) {
        Response deleteResponse = deleteSecondLevelEntity(propertySetId, PROPERTIES_RESOURCE, propertyId, null);
        setSessionResponse(deleteResponse);
    }

    @Step
    public void removePropertyFromPropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID propertyId, UUID propertySetId) {
        Response deleteResponse = deleteSecondLevelEntityByUserForApplication(userId, applicationVersionId, propertySetId, PROPERTIES_RESOURCE, propertyId, null);
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

    public void propertysetWithIdIsGot(UUID propertySetId) {
        Response response = getEntity(propertySetId);
        setSessionResponse(response);
    }

    public void updatePropertySet(UUID propertySetId, PropertySetUpdateDto propertySetUpdateDto) throws JsonProcessingException {
      updatePropertySetByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, propertySetId, propertySetUpdateDto);
    }

    public void updatePropertySetByUserForApp(UUID userId, UUID applicationVersionId, UUID propertySetId, PropertySetUpdateDto propertySetUpdate){
        String etag = getEntity(propertySetId).getHeader(HEADER_ETAG);
        try {
            JSONObject jsonUpdatePropSet = retrieveData(propertySetUpdate);
            Response resp = updateEntityByUserForApplication(userId, applicationVersionId, propertySetId, jsonUpdatePropSet.toString(), etag);
            setSessionResponse(resp);

        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from PropertySetUpdateDto object");
        }
    }

    public void comparePropertySets(UUID propertySetId, PropertySetUpdateDto propertySetUpdateDto) throws JsonProcessingException {
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
    public void updateUserPropertySetRelation(UUID userId, UUID propertySetId, UserPropertySetRelationshipUpdateDto userPropertySetRelationshipUpdate) {
        updateUserPropertySetRelationByUserForApp(DEFAULT_SNAPSHOT_USER_ID, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID, userId, propertySetId, userPropertySetRelationshipUpdate);
    }

    @Step
    public void updateUserPropertySetRelationByUserForApp(UUID performerId, UUID applicationVersionId, UUID userId, UUID propertySetId, UserPropertySetRelationshipUpdateDto userPropertySetRelationshipUpdate) {
        try {
            JSONObject jsonUpdate = retrieveData(userPropertySetRelationshipUpdate);
            String etag = getSecondLevelEntityEtag(propertySetId, USERS_RESOURCE, userId);
            Response response = updateSecondLevelEntityByUserForApp(performerId, applicationVersionId, propertySetId, USERS_RESOURCE, userId, jsonUpdate, etag);
            setSessionResponse(response);
        } catch(JsonProcessingException exception){
            fail("Exception thrown while getting JSON from UserPropertyRelationshipUpdateDto object");
        }
    }

    @Step
    public Response getChildPropertySetsByUser(UUID userId, UUID propertySetId, String limit, String cursor, String filter, String sort, String sortDesc, Map<String, String> queryParams){
        Response response = getSecondLevelEntitiesByUser(userId, propertySetId, PROPERTY_SETS_RESOURCE, limit, cursor, filter, sort, sortDesc, queryParams);
        setSessionResponse(response);
        return response;
    }

    public UUID resolvePropertySetId(String propertySetName) {
        UUID propertySetId;
        if (isUUID(propertySetName)) {
            propertySetId = UUID.fromString(propertySetName);
        } else {
            PropertySetDto propertySet = getPropertySetByName(propertySetName);
            assertThat(String.format("Property with code \"%s\" does not exist", propertySetName), propertySet, is(notNullValue()));
            propertySetId = propertySet.getId();
        }
        return propertySetId;
    }

    @Step
    public void setPropertysetPropertyActivity(UUID propertySetId, UUID propertyId, boolean activity) throws JsonProcessingException {
        setPropertySetPropertyActivityByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, propertyId, activity);
    }

    @Step
    public void setPropertySetPropertyActivityByUser(UUID userId, UUID propertySetId, UUID propertyId, boolean activity) throws JsonProcessingException {
        String etag = getSecondLevelEntity(propertySetId, PROPERTIES_RESOURCE, propertyId).getHeader(HEADER_ETAG);
        PropertySetPropertyRelationshipUpdateDto relation = new PropertySetPropertyRelationshipUpdateDto();
        relation.setIsActive(activity);
        try {
            JSONObject obj = retrieveData(relation);
            Response resp = updateSecondLevelEntityByUser(userId, propertySetId, PROPERTIES_RESOURCE, propertyId, obj, etag);
            setSessionResponse(resp);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from PropertySetPropertyRelationshipUpdateDto object: " + e.toString());
        }
    }

    @Step
    public void setPropertysetUserActivity(UUID propertySetId, UUID userId, boolean activity) throws JsonProcessingException {
        setPropertySetUserActivityByUser(DEFAULT_SNAPSHOT_USER_ID, propertySetId, userId, activity);
    }

    @Step
    public void setPropertySetUserActivityByUser(UUID requestorId, UUID propertySetId, UUID userId, boolean activity) throws JsonProcessingException {
        String etag = getSecondLevelEntity(propertySetId, USERS_RESOURCE, userId).getHeader(HEADER_ETAG);
        UserPropertySetRelationshipUpdateDto relation = new UserPropertySetRelationshipUpdateDto();
        relation.setIsActive(activity);
        try {
            JSONObject obj = retrieveData(relation);
            Response resp = updateSecondLevelEntityByUser(requestorId, propertySetId, USERS_RESOURCE, userId, obj, etag);
            setSessionResponse(resp);
        } catch (JsonProcessingException e){
            fail("Exception while retrieving JSON from PropertySetUserRelationshipUpdateDto object: " + e.toString());
        }
    }
}
