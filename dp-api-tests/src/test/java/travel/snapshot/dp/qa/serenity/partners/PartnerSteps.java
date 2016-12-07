package travel.snapshot.dp.qa.serenity.partners;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.*;

public class PartnerSteps extends BasicSteps {

    private static final String PARTNERS_PATH = "/identity/partners";
    private static final String SESSION_PARTNER_ID = "partner_id";

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

        Map<String, Object> partnerData = retrieveData(PartnerDto.class, updatedPartner);

        Response response = updateEntity(partnerId, partnerData, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void partnerWithIdHasData(String partnerId, PartnerDto partnerData) throws Exception {
        Map<String, Object> originalData = retrieveData(PartnerDto.class, getPartnerById(partnerId));
        Map<String, Object> expectedData = retrieveData(PartnerDto.class, partnerData);

        expectedData.forEach((key, value) -> {
            if (value == null) {
                assertFalse("Partner JSON should not contains attributes with null values",
                        originalData.containsKey(key));
                return;
            }
            assertTrue("Partner has no data for attribute " + key, originalData.containsKey(key));
            assertEquals(value, originalData.get(key));
        });
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
        Response response = getEntities(limit, cursor, filter, sort, sortDesc);
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
                CURSOR_FROM_FIRST, null, null, null);
        setSessionResponse(partnerApplications);
    }

    @Step
    public void listOfPartnerApplicationsIsGotWith(String partnerId, String limit, String cursor, String filter,
                                                   String sort, String sortDesc) {
        Response partnerApplications = getSecondLevelEntities(partnerId, "", limit,
                cursor, filter, sort, sortDesc);
        setSessionResponse(partnerApplications);
    }

    @Step
    public void getUsersForPartnerId(String partnerId) {
        Response partnerUsers = getSecondLevelEntities(partnerId, SECOND_LEVEL_OBJECT_USERS, LIMIT_TO_ALL,
                CURSOR_FROM_FIRST, null, null, null);
        setSessionResponse(partnerUsers);
    }

    public PartnerDto getPartnerByName(String name) {
        String filter = String.format("name =='%s'", name);
        PartnerDto[] partners = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, filter, null, null).as(PartnerDto[].class);
        return Arrays.asList(partners).stream().findFirst().orElse(null);
    }

    public PartnerDto getPartnerById(String partnerId) {
        PartnerDto[] partners = getEntities(LIMIT_TO_ONE, CURSOR_FROM_FIRST, "partner_id==" + partnerId, null, null)
                .as(PartnerDto[].class);
        return Arrays.asList(partners).stream().findFirst().orElse(null);
    }

    private Response activatePartner(String partnerId) {
        return given().spec(spec).when().post("/{id}/activate", partnerId);
    }

    private Response inactivatePartner(String partnerId) {
        return given().spec(spec).when().post("/{id}/inactivate", partnerId);
    }
}
