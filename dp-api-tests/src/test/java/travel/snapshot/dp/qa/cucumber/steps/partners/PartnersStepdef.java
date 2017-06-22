package travel.snapshot.dp.qa.cucumber.steps.partners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.NON_EXISTENT_ID;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.PartnerUpdateDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.cucumber.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.cucumber.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.cucumber.serenity.partners.PartnerSteps;
import travel.snapshot.dp.qa.cucumber.serenity.users.UsersSteps;

import java.util.List;

public class PartnersStepdef {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private PartnerSteps partnerSteps;

    @Steps
    private UsersSteps userSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionsSteps;

    @Given("^The following partner exist$")
    public void theFollowingPartnerExist(List<PartnerDto> partners) throws Throwable {
        partnerSteps.partnerExists(partners);
    }

    @When("^The following partner is created$")
    public void theFollowingPartnerIsCreated(List<PartnerDto> partners) throws Throwable {
        partnerSteps.followingPartnerIsCreated(partners.get(0));
    }

    @When("^Partner with name \"([^\"]*)\" is deleted$")
    public void Partner_with_name_is_deleted(String partnerName) {
        PartnerDto partner = partnerSteps.getPartnerByName(partnerName);
        assertThat(partner, is(notNullValue()));
        partnerSteps.deletePartner(partner.getId());
    }

    @Then("^Partner with same id does not exist$")
    public void Partner_with_same_id_does_not_exist() {
        partnerSteps.partnerWithSameIdInSessionDoesNotExists();
    }

    @When("^Nonexistent partner id is deleted$")
    public void Nonexistent_partner_id_is_deleted() {
        partnerSteps.deletePartner(NON_EXISTENT_ID);
    }

    @When("^Partner with id \"([^\"]*)\" is activated$")
    public void Partner_with_id_is_activated(String partnerId) {
        partnerSteps.setPartnerIsActive(partnerId, true);
    }

    @When("Partner with id \"([^\"]*)\" is active")
    public void Partner_with_id_is_active(String partnerId) {
        assertThat("Partner should be active but it is not", partnerSteps.getPartnerById(partnerId).getIsActive(), is(true));
    }

    @When("Partner with id \"([^\"]*)\" is not active")
    public void Partner_with_id_is_not_active(String partnerId) {
        assertThat("Partner should be inactive but it's still active", partnerSteps.getPartnerById(partnerId).getIsActive(), is(false));
    }

    @When("^Partner with id \"([^\"]*)\" is inactivated$")
    public void Partner_with_id_is_inactivated(String partnerId) {
        partnerSteps.setPartnerIsActive(partnerId, false);
    }

    @When("^Partner with id \"([^\"]*)\" is updated with data$")
    public void Partner_with_id_is_updated_with_data(String partnerId, List<PartnerUpdateDto> updatedPartner) throws Exception {
        partnerSteps.updatePartner(partnerId, updatedPartner.get(0));
    }

    @Then("^Updated partner with id \"([^\"]*)\" has data$")
    public void Updated_partner_with_id_has_data(String partnerId, List<PartnerDto> partnerData) throws Exception {
        partnerSteps.partnerWithIdHasData(partnerId, partnerData.get(0));
    }

    @When("^Partner with id \"([^\"]*)\" is got$")
    public void Partner_with_id_is_got(String partnerId) {
        partnerSteps.partnerWithIdIsGot(partnerId);
    }

    @When("^Nonexistent partner id is got$")
    public void Nonexistent_partner_id_is_got() {
        partnerSteps.partnerWithIdIsGot(NON_EXISTENT_ID);
    }

    @When("^List of partners is (?:got|requested)?(?: with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void List_of_partners_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            @Transform(NullEmptyStringConverter.class) String userName,
            @Transform(NullEmptyStringConverter.class) String appVersionName) {
        String appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        String userId = userSteps.resolveUserId(userName);
        partnerSteps.listOfPartnersIsGotWith(userId, appVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) partners returned$")
    public void There_are_partners_returned(int count) throws Throwable {
        partnerSteps.numberOfEntitiesInResponse(PartnerDto.class, count);
    }

    @Then("^There are partners with following names returned in order: (.*)$")
    public void There_are_partners_with_following_names_returned_in_order(List<String> partnerNames) {
        partnerSteps.namesInResponseInOrder(partnerNames);
    }

    @When("^Partners applications for partner with id \"([^\"]*)\" is got$")
    public void Partners_applications_for_partner_with_id_is_got(String partnerId) {
        partnerSteps.getApplicationsForPartnerId(partnerId);
    }

    @When("List of partner applications is got for partner with id \"([^\"]*)\" with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"")
    public void List_of_partner_applications_is_got_for_partner_id_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String partnerId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) {
        partnerSteps.listOfPartnerApplicationsIsGotWith(partnerId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("There are (\\d+) partner applications returned")
    public void There_are_partner_applications_returned(int count) throws Throwable {
        partnerSteps.numberOfEntitiesInResponse(ApplicationDto.class, count);
    }

    @When("^Partner users for partner with id \"([^\"]*)\" is got$")
    public void Partner_users_for_partner_with_id_is_got(String partnerId) {
        partnerSteps.getUsersForPartnerId(partnerId);
    }


    @When("^user \"([^\"]*)\" is added to partner \"([^\"]*)\"$")
    public void userIsAddedToPartner(String username, String partnerName) throws Throwable {
        UserDto user = userSteps.getUserByUsername(username);
        PartnerDto partner = partnerSteps.getPartnerByName(partnerName);
        assertThat(user, is(notNullValue()));
        assertThat(partner, is(notNullValue()));

        partnerSteps.createPartnerUserRelationship(partner.getId(), user.getId());
    }
}
