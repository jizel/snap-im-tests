package travel.snapshot.dp.qa.serenity.commercial_subscription;

import static java.util.Arrays.stream;
import static org.junit.Assert.*;

import com.jayway.restassured.response.Response;
import net.serenitybdd.core.Serenity;
import net.thucydides.core.annotations.Step;
import org.apache.http.HttpStatus;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionUpdateDto;
import travel.snapshot.dp.qa.helpers.PropertiesHelper;
import travel.snapshot.dp.qa.serenity.BasicSteps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            CommercialSubscriptionDto existingSubscription = getSubscriptionById(s.getCommercialSubscriptionId());
            if (existingSubscription != null) {
                deleteEntityWithEtag(existingSubscription.getCommercialSubscriptionId());
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

    private void updateCommSubscriptionWithId(String commSubscriptionId, CommercialSubscriptionUpdateDto updatedCommSubscription) {
        Response tempResponse = getEntity(commSubscriptionId);
        Map<String, Object> mapToUpdate = new HashMap<>();

        mapToUpdate.put("is_active", updatedCommSubscription.getIsActive());

        Response response = updateEntity(commSubscriptionId, mapToUpdate, tempResponse.getHeader(HEADER_ETAG));
        setSessionResponse(response);
    }

    @Step
    public void setCommSubscriptionIsActiveField(String commSubscriptionId, Boolean isActive) {
        CommercialSubscriptionDto comSub = new CommercialSubscriptionDto();
        comSub.setIsActive(isActive);

        updateCommSubscriptionWithId(commSubscriptionId, comSub);
    }

    @Step
    public void commSubscriptionWithIdIsGot(String commSubscriptionId) {
        Response response = getEntity(commSubscriptionId);
        Serenity.setSessionVariable(SESSION_RESPONSE).to(response);
    }

    @Step
    public void listOfCommSubscriptionsIsGotWith(String limit, String cursor, String filter, String sort,
                                                 String sortDesc) {
        Response response = getEntities(null, limit, cursor, filter, sort, sortDesc, null);
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
                    s.getCommercialSubscriptionId());
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
