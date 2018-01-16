package travel.snapshot.dp.qa.steps.commercial_subscription;

import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionUpdateDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.commercial_subscription.CommercialSubscriptionSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.List;
import java.util.UUID;

public class CommercialSubscriptionStepdef {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private CommercialSubscriptionSteps commSubscriptionSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionsSteps;

    @Steps
    private UsersSteps usersSteps;


    @Given("^The following commercial subscriptions exist$")
    public void theFollowingCommercialSubscriptionsExist(List<CommercialSubscriptionDto> commercialSubscriptions) throws Throwable {
        commSubscriptionSteps.comSubscriptionsExists(commercialSubscriptions);
    }

    @When("^Commercial subscription is created$")
    public void Commercial_subscription_is_created(List<CommercialSubscriptionDto> comSubcsriptions) {
        commSubscriptionSteps.commercialSubscriptionIsCreated(comSubcsriptions.get(0));
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is deleted$")
    public void Commercial_subscription_with_id_is_deleted(UUID commSubcriptionId) {
        commSubscriptionSteps.deleteCommSubscriptionWithId(commSubcriptionId);
    }

    @Then("Commercial subscription with same id does not exist")
    public void Commercial_subscription_with_same_id_does_not_exist() {
        commSubscriptionSteps.commSubscriptionWithSameIdDoesNotExist();
    }

    @When("Nonexistent commercial subscription id is deleted")
    public void Nonexistent_commercial_subscription_id_is_deleted() {
        commSubscriptionSteps.deleteCommSubscriptionWithId(NON_EXISTENT_ID);
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is (?:got|requested)$")
    public void Commercial_subscription_with_id_is_got(UUID commSubscriptionId) {
        commSubscriptionSteps.commSubscriptionWithIdIsGot(commSubscriptionId);
    }

    @When("Nonexistent commercial subscription id is (?:got|requested)")
    public void Nonexistent_commercial_subscription_id_is_got() {
        commSubscriptionSteps.commSubscriptionWithIdIsGot(NON_EXISTENT_ID);
    }

    @When("List of commercial subscriptions is (?:requested|got)(?: with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?")
    public void List_of_commercial_subscriptions_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            @Transform(NullEmptyStringConverter.class) String userName,
            @Transform(NullEmptyStringConverter.class) String appVersionName) {
        UUID appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        UUID userId = usersSteps.resolveUserId(userName);
        commSubscriptionSteps.listOfCommSubscriptionsIsGotWith(userId, appVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("There are (\\d+) commercial subscriptions returned")
    public void There_are_commercial_subscriptions_returned(int count) throws Throwable {
        commSubscriptionSteps.numberOfEntitiesInResponse(CommercialSubscriptionDto.class, count);
    }

    @Then("^There are commercial subscriptions with following referenceIDs returned in order: \"([^\"]*)\"$")
    public void thereAreCommercialSubscriptionsWithFollowingIDsReturnedInOrder(List<String> commSubscriptionIds) throws Throwable {
        commSubscriptionSteps.idsInResponseInOrder(commSubscriptionIds);
    }

    @When("^Commercial subscription with id \"([^\"]*)\" is (in|de)?activated$")
    public void commercialSubscriptionWithIdIsActivated(UUID commSubscriptionId, String negation) throws Exception {
        Boolean isActive = (negation == null);
        CommercialSubscriptionUpdateDto commercialSubscriptionUpdate = new CommercialSubscriptionDto();
        commercialSubscriptionUpdate.setIsActive(isActive);
        commSubscriptionSteps.updateCommSubscription(commSubscriptionId, commercialSubscriptionUpdate);
    }

    @Then("^Commercial subscription with id \"([^\"]*)\" is activate$")
    public void commercialSubscriptionWithIdIsActivate(UUID commSubscriptionId) throws Throwable {
        commSubscriptionSteps.checkIsActive(commSubscriptionId, true);
    }

    @Then("^Commercial subscription with id \"([^\"]*)\" is not activate$")
    public void commercialSubscriptionWithIdIsNotActivate(UUID commSubscriptionId) throws Throwable {
        commSubscriptionSteps.checkIsActive(commSubscriptionId, false);
    }
}
