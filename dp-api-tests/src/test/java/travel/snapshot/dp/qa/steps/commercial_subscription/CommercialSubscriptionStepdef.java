package travel.snapshot.dp.qa.steps.commercial_subscription;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.commercial_subscription.CommercialSubscriptionSteps;

import java.util.List;

public class CommercialSubscriptionStepdef {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CommercialSubscriptionSteps commSubscriptionSteps;


    @Given("^The following commercial subscriptions exist$")
    public void theFollowingCommercialSubscriptionsExist(List<CommercialSubscriptionDto> commercialSubscriptions) throws Throwable {
        commSubscriptionSteps.comSubscriptionsExists(commercialSubscriptions);
    }

    @When("^Commercial subscription is created$")
    public void Commercial_subscription_is_created(List<CommercialSubscriptionDto> comSubcsriptions) {
        commSubscriptionSteps.comSubscriptionIsCreated(comSubcsriptions.get(0));
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is deleted$")
    public void Commercial_subscription_with_id_is_deleted(String commSubcriptionId) {
        commSubscriptionSteps.deleteCommSubscriptionWithId(commSubcriptionId);
    }

    @Then("Commercial subscription with same id does not exist")
    public void Commercial_subscription_with_same_id_does_not_exist() {
        commSubscriptionSteps.commSubscriptionWithSameIdDoesNotExist();
    }

    @When("Nonexistent commercial subscription id is deleted")
    public void Nonexistent_commercial_subscription_id_is_deleted() {
        commSubscriptionSteps.deleteCommSubscriptionWithId("NonExistentId");
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is got$")
    public void Commercial_subscription_with_id_is_got(String commSubscriptionId) {
        commSubscriptionSteps.commSubscriptionWithIdIsGot(commSubscriptionId);
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is got with etag$")
    public void Commercial_subscription_with_id_is_got_with_etag(String commSubscriptionId) {
        commSubscriptionSteps.commSubscriptionWithIdIsGotWithEtag(commSubscriptionId);
    }

    @When("Commercial subscription with id \"([^\"]*)\" is got for etag, updated and got with previous etag")
    public void Commercial_subscription_with_id_is_got_for_etag_updated_and_got_with_previous_etag(
            String commSubscriptionId) {
        commSubscriptionSteps.commSubscriptionWithIdIsGotWithExpiredEtag(commSubscriptionId);
    }

    @When("Nonexistent commercial subscription id is got")
    public void Nonexistent_commercial_subscription_id_is_got() {
        commSubscriptionSteps.commSubscriptionWithIdIsGot("NonExistentId");
    }

    @When("List of commercial subscriptions is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"")
    public void List_of_commercial_subscriptions_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) {
        commSubscriptionSteps.listOfCommSubscriptionsIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @Then("There are (\\d+) commercial subscriptions returned")
    public void There_are_commercial_subscriptions_returned(int count) throws Throwable {
        commSubscriptionSteps.numberOfEntitiesInResponse(CommercialSubscriptionDto.class, count);
    }

    @Then("^There are commercial subscriptions with following referenceIDs returned in order: \"([^\"]*)\"$")
    public void thereAreCommercialSubscriptionsWithFollowingIDsReturnedInOrder(List<String> commSubscriptionIds) throws Throwable {
        commSubscriptionSteps.idsInResponseInOrder(commSubscriptionIds);
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is activated$")
    public void commercialSubscriptionWithIdIsActivated(String commSubscriptionId) throws Exception {
        commSubscriptionSteps.setCommSubscriptionIsActiveField(commSubscriptionId, true);
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is deactivated$")
    public void commercialSubscriptionWithIdIsDeactivated(String commSubscriptionId) throws Throwable {
        commSubscriptionSteps.setCommSubscriptionIsActiveField(commSubscriptionId, false);
    }

    @Then("^Commercial subscription with id \"([^\"]*)\" is activate$")
    public void commercialSubscriptionWithIdIsActivate(String commSubscriptionId) throws Throwable {
        commSubscriptionSteps.checkIsActive(commSubscriptionId, 1);
    }

    @Then("^Commercial subscription with id \"([^\"]*)\" is not activate$")
    public void commercialSubscriptionWithIdIsNotActivate(String commSubscriptionId) throws Throwable {
        commSubscriptionSteps.checkIsActive(commSubscriptionId, 0);
    }
}
