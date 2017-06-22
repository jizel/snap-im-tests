package travel.snapshot.dp.qa.cucumber.serenity.commercial_subscription;

import static java.util.Arrays.stream;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionUpdateDto;
import travel.snapshot.dp.qa.cucumber.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.cucumber.serenity.BasicSteps;

import java.util.List;

public class CommercialSubscriptionSteps extends BasicSteps {

    private static final String COMMERCIAL_SUBSCRIPTION_PATH = "/identity/commercial_subscriptions";
    private static final String SESSION_COMMERCIAL_SUBSCRIPTIONS = "commercial_subcsriptions";
    private static final String SESSION_COMMERCIAL_SUBSCRIPTION_ID = "commercial_subscription_id";

    public CommercialSubscriptionSteps() {
        super();
        spec.baseUri(PropertiesHelper.getProperty(IDENTITY_BASE_URI));
        spec.basePath(COMMERCIAL_SUBSCRIPTION_PATH);
    }

    @Step
    public void comSubscriptionsExists(List<CommercialSubscriptionDto> commSubcsriptions) {
        commSubcsriptions.forEach(s -> {
            CommercialSubscriptionDto existingSubscription = getSubscriptionById(s.getId());
            if (existingSubscription != null) {
                deleteEntityWithEtag(existingSubscription.getId());
            }

            Response createResponse = createEntity(s);
            if (createResponse.getStatusCode() != HttpStatus.SC_CREATED) {
                fail("Commercial subscription cannot be created " + createResponse.getBody().asString());
            }
        });
        Serenity.setSessionVariable(SESSION_COMMERCIAL_SUBSCRIPTIONS).to(commSubcsriptions);
    }

    @Step
    public void commercialSubscriptionIsCreated(CommercialSubscriptionDto commercialSubscription) {
        Response resp = createEntity(commercialSubscription);
        setSessionResponse(resp);
    }

    @Step
    public void deleteCommSubscriptionWithId(String subscriptionId) {
        deleteEntityWithEtag(subscriptionId);
        Serenity.setSessionVariable(SESSION_COMMERCIAL_SUBSCRIPTION_ID).to(subscriptionId);
    }

    @Step
    public void commSubscriptionWithSameIdDoesNotExist() {
        String commSubcriptionId = Serenity.sessionVariableCalled(SESSION_COMMERCIAL_SUBSCRIPTION_ID);
        Response response = getEntity(commSubcriptionId);
        response.then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Step
    public void updateCommSubscription(String commSubscriptionId, CommercialSubscriptionUpdateDto updatedCommSubscription) {
        try {
            JSONObject jsonUpdate = retrieveData(updatedCommSubscription);
            Response response = updateEntity(commSubscriptionId, jsonUpdate.toString(), getEntityEtag(commSubscriptionId));
            setSessionResponse(response);
        } catch(JsonProcessingException e){
            fail("Exception while retrieving JSON data from commercialSubscriptionUpdateDto object: " + e.getMessage());
        }
    }

    @Step
    public void commSubscriptionWithIdIsGot(String commSubscriptionId) {
        Response response = getEntity(commSubscriptionId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void listOfCommSubscriptionsIsGotWith(String userId, String appVersionId, String limit, String cursor, String filter, String sort,
                                                 String sortDesc) {
        Response response = getEntitiesByUserForApp(userId, appVersionId, null, limit, cursor, filter, sort, sortDesc, null);
        setSessionResponse(response);
    }

    @Step
    public void idsInResponseInOrder(List<String> commSubscriptionIds) {
        if (commSubscriptionIds.isEmpty()) {
            return;
        }

        Response response = getSessionResponse();
        CommercialSubscriptionDto[] commSubscriptions = response.as(CommercialSubscriptionDto[].class);
        int i = 0;
        for (CommercialSubscriptionDto s : commSubscriptions) {
            assertEquals("Commercial subscription on index=" + i + " is not expected", commSubscriptionIds.get(i),
                    s.getId());
            i++;
        }
    }

    public CommercialSubscriptionDto getSubscriptionById(String commSubscriptionId) {
        CommercialSubscriptionDto[] comSubscription = getEntities(null, LIMIT_TO_ONE, CURSOR_FROM_FIRST,
                "commercial_subscription_id==" + commSubscriptionId, null, null, null).as(CommercialSubscriptionDto[].class);
        return stream(comSubscription).findFirst().orElse(null);
    }

    public void checkIsActive(String commSubscriptionId, boolean isActive) {
        CommercialSubscriptionDto comSub = getSubscriptionById(commSubscriptionId);
        assertEquals(comSub.getIsActive(), isActive);
    }
}
