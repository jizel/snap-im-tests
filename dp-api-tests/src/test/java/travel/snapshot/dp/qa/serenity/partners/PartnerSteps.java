package travel.snapshot.dp.qa.serenity.partners;

import static com.jayway.restassured.RestAssured.given;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartnerSteps extends BasicSteps {

    private static final String PARTNERS_PATH = "/identity/partners";
    private static final String SESSION_PARTNER_ID = "partner_id";
    private static final String USER_ID_KEY = "user_id";

    public PartnerSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
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

    public void followingPartnerIsCreated(PartnerDto partner) {
        Response createResponse = createEntity(partner);
        setSessionResponse(createResponse);
    }

    @Step
    public void deletePartnerWithName(String partnerName) {
        PartnerDto partner = getPartnerByName(partnerName);
        if (partner == null) {
            return;
        }

        String partnerId = partner.getPartnerId();
        deleteEntityWithEtag(partnerId);
        Serenity.setSessionVariable(SESSION_PARTNER_ID).to(partnerId);
    }

    @Step
    public void partnerWithSameIdInSessionDoesNotExists() {
        String partnerId = Serenity.sessionVariableCalled(SESSION_PARTNER_ID);

        Response response = getEntity(partnerId, null);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void activatePartnerWithId(String partnerId) {
        Response response = activatePartner(partnerId);
        setSessionResponse(response);
    }

    @Step
    public void partnerIsSetToActive(boolean isActive, String partnerId) {
        PartnerDto partner = getPartnerById(partnerId);
        if (isActive) {
            assertNotNull("Partner should be returned", partner);
            assertEquals("Status should be set to inactive", "active", partner.getIsActive());
        } else {
            assertNotNull("Partner should be returned", partner);
            assertEquals("Status should be set to inactive", "inactive", partner.getIsActive());
        }
    }

    @Step
    public void inactivatePartnerWithId(String partnerId) {
        Response response = inactivatePartner(partnerId);
        setSessionResponse(response);
    }

    @Step
    public void updatePartnerWithId(String partnerId, PartnerDto updatedPartner) throws Exception {

        Response tempResponse = getEntity(partnerId, null);

        JSONObject partnerData = retrieveData(updatedPartner);

        Response response = updateEntity(partnerId, partnerData.toString(), tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void partnerWithIdHasData(String partnerId, PartnerDto partnerData) throws Exception {
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
    public void partnerWithIdIsGot(String partnerId) {
        Response response = getEntity(partnerId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void partnerWithIdIsGotWithEtag(String partnerId) {
        Response tempResponse = getEntity(partnerId, null);
        Response resp = getEntity(partnerId, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(resp);
    }

    @Step
    public void partnerWithIdIsGotWithExpiredEtag(String partnerId) {
        Response tempResponse = getEntity(partnerId, null);

        Map<String, Object> mapForUpdate = new HashMap<>();
        mapForUpdate.put("name", "Partner test company 1");
        mapForUpdate.put("notes", "Updated Notes");
        mapForUpdate.put("website", "http://www.snapshot.travel");

        Response updateResponse = updateEntity(partnerId, mapForUpdate, tempResponse.getHeader(HEADER_ETAG));
        if (updateResponse.getStatusCode() != HttpStatus.SC_NO_CONTENT) {
            fail("Partner cannot be updated: " + updateResponse.asString());
        }
    }

    @Step
    public void listOfPartnersIsGotWith(String limit, String cursor, String filter, String sort, String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
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
    public void getApplicationsForPartnerId(String partnerId) {
        Response partnerApplications = getSecondLevelEntities(partnerId, "", LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(partnerApplications);
    }

    @Step
    public void listOfPartnerApplicationsIsGotWith(String partnerId, String limit, String cursor, String filter,
                                                   String sort, String sortDesc) {
        Response partnerApplications = getSecondLevelEntities(partnerId, "", limit,
                cursor, filter, sort, sortDesc, null);
        setSessionResponse(partnerApplications);
    }

    @Step
    public void getUsersForPartnerId(String partnerId) {
        Response partnerUsers = getSecondLevelEntities(partnerId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null, null);
        setSessionResponse(partnerUsers);
    }

    @Step
    public void createPartnerUserRelationship(String partnerId, String userId){
        Response response = createSecondLevelRelationship(partnerId, SECOND_LEVEL_OBJECT_USERS, singletonMap(USER_ID_KEY, userId));
        setSessionResponse(response);
    }

    public PartnerDto getPartnerByName(String name) {
        String filter = String.format("name =='%s'", name);
        PartnerDto[] partners = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null, null).as(PartnerDto[].class);
        return Arrays.asList(partners).stream().findFirst().orElse(null);
    }

    public PartnerDto getPartnerById(String partnerId) {
        PartnerDto[] partners = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST, "partner_id==" + partnerId, null, null, null)
                .as(PartnerDto[].class);
        return Arrays.asList(partners).stream().findFirst().orElse(null);
    }

    private Response activatePartner(String partnerId) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).when().post("/{id}/activate", partnerId);
    }

    private Response inactivatePartner(String partnerId) {
        return given().spec(spec).header(HEADER_XAUTH_USER_ID, DEFAULT_SNAPSHOT_USER_ID).when().post("/{id}/inactivate", partnerId);
    }
}
