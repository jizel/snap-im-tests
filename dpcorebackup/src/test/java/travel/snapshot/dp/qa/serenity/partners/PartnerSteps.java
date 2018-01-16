package travel.snapshot.dp.qa.serenity.partners;


import static java.util.Arrays.stream;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.APPLICATIONS_RESOURCE;
import static travel.snapshot.dp.api.identity.resources.IdentityDefaults.USERS_RESOURCE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.List;
import java.util.UUID;

public class PartnerSteps extends BasicSteps {

    private static final String PARTNERS_PATH = "/partners";
    private static final String SESSION_PARTNER_ID = "partner_id";
    private static final String USER_ID_KEY = "user_id";

    public PartnerSteps() {
        super();
        spec.baseUri(propertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(PARTNERS_PATH);
    }

    public void partnerExists(List<PartnerDto> partners) {
        partners.forEach(partner -> {
            Response createResponse = createEntity(partner);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Partner cannot be created " + createResponse.getBody().asString());
            }
        });
    }

    public Response followingPartnerIsCreated(PartnerDto partner) {
        Response createResponse = createEntity(partner);
        setSessionResponse(createResponse);
        return createResponse;
    }

    @Step
    public void deletePartner(UUID partnerId) {
        deleteEntityWithEtag(partnerId);
        Serenity.setSessionVariable(SESSION_PARTNER_ID).to(partnerId);
    }

    @Step
    public void partnerWithSameIdInSessionDoesNotExists() {
        UUID partnerId = Serenity.sessionVariableCalled(SESSION_PARTNER_ID);

        Response response = getEntity(partnerId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void setPartnerIsActive(UUID partnerId, boolean isActive){
        PartnerUpdateDto partnerUpdate = new PartnerUpdateDto();
        partnerUpdate.setIsActive(isActive);
        updatePartner(partnerId, partnerUpdate);
    }

    @Step
    public void updatePartner(UUID partnerId, PartnerUpdateDto updatedPartner) {
        String etag = getEntityEtag(partnerId);
        try {
            JSONObject partnerData = retrieveData(updatedPartner);
            Response response = updateEntity(partnerId, partnerData.toString(), etag);
            setSessionResponse(response);
        }catch(JsonProcessingException jsonException){
            fail("Exception while parsing to JSON: " + jsonException);
        }
    }

    @Step
    public void partnerWithIdHasData(UUID partnerId, PartnerDto partnerData) throws Exception {
        JSONObject originalData = retrieveData(getPartnerById(partnerId));
        JSONObject expectedData = retrieveData(partnerData);
        for (String key : expectedData.keySet()){
            Object value = expectedData.get(key);
            if (value == null) {
                assertFalse("Partner JSON should not contain attributes with null values",
                    originalData.has(key));
                return;
            }
        }
    }

    @Step
    public void partnerWithIdIsGot(UUID partnerId) {
        Response response = getEntity(partnerId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void listOfPartnersIsGotWith(UUID userId, UUID appVersionId, String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void namesInResponseInOrder(List<String> partnerNames) {
        Response response = getSessionResponse();
        PartnerDto[] partners = response.as(PartnerDto[].class);
        int i = 0;
        for (PartnerDto p : partners) {
            assertEquals("Partner on index=" + i + " is not expected", partnerNames.get(i), p.getName());
            i++;
        }
    }

    @Step
    public void getApplicationsForPartnerId(UUID partnerId) {
        Response partnerApplications = getSecondLevelEntities(partnerId, APPLICATIONS_RESOURCE, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(partnerApplications);
    }

    @Step
    public void listOfPartnerApplicationsIsGotWith(UUID partnerId, String limit, String cursor, String filter,
                                                   String sort, String sortDesc) {
        Response partnerApplications = getSecondLevelEntities(partnerId, APPLICATIONS_RESOURCE, limit,
                cursor, filter, sort, sortDesc, null);
        setSessionResponse(partnerApplications);
    }

    @Step
    public void getUsersForPartnerId(UUID partnerId) {
        Response partnerUsers = getSecondLevelEntities(partnerId, USERS_RESOURCE, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(partnerUsers);
    }

    @Step
    public void createPartnerUserRelationship(UUID partnerId, UUID userId){
        Response response = createSecondLevelRelationship(partnerId, USERS_RESOURCE, singletonMap(USER_ID_KEY, userId));
        setSessionResponse(response);
    }

    public PartnerDto getPartnerByName(String name) {
        String filter = String.format("name =='%s'", name);
        PartnerDto[] partners = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(PartnerDto[].class);
        return stream(partners).findFirst().orElse(null);
    }

    public PartnerDto getPartnerById(UUID partnerId) {
        PartnerDto[] partners = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "partner_id==" + partnerId, null, null, null)
                .as(PartnerDto[].class);
        return stream(partners).findFirst().orElse(null);
    }

    public UUID resolvePartnerId(String partnerName) {
        if (partnerName == null) return DEFAULT_SNAPSHOT_USER_ID;

        UUID partnerId;
        if (isUUID(partnerName)) {
            partnerId = UUID.fromString(partnerName);
        } else {
            PartnerDto partner = getPartnerByName(partnerName);
            assertThat(String.format("Partner with name \"%s\" does not exist", partnerName), partner, is(notNullValue()));
            partnerId = partner.getId();
        }
        return partnerId;
    }
}
