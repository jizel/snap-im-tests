package travel.snapshot.dp.qa.steps.partners;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.PartnerDto;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.partners.PartnerSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.List;

public class PartnersStepdef {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private PartnerSteps partnerSteps;

    @Steps
    private UsersSteps userSteps;

    @Given("^Default partner is created$")
    public void defaultPartnerIsCreated() throws Throwable {
        PartnerDto defaultPartner = new PartnerDto();
        defaultPartner.setPartnerId(partnerSteps.DEFAULT_SNAPSHOT_PARTNER_ID);
        defaultPartner.setName("Somepartner");
        defaultPartner.setWebsite(partnerSteps.SNAPSHOT_WEBSITE);
        defaultPartner.setIsActive(true);
        defaultPartner.setEmail("somemail@snapshot.travel");
        defaultPartner.setVatId(partnerSteps.DEFAULT_SNAPSHOT_PARTNER_VAT_ID);
        partnerSteps.followingPartnerIsCreated(defaultPartner);
    }

    @Given("^The following partner exist$")
    public void theFollowingPartnerExist(List<PartnerDto> partners) throws Throwable {
        partnerSteps.partnerExists(partners);
    }

    @When("^The following partner is created$")
    public void theFollowingPartnerIsCreated(List<PartnerDto> partners) throws Throwable {
        partnerSteps.followingPartnerIsCreated(partners.get(0));
    }

    @When("^Partner with name \"([^\"]*)\" is deleted$")
    public void Partner_with_name_is_deleted(String partnerId) {
        partnerSteps.deletePartnerWithName(partnerId);
    }

    @Then("^Partner with same id does not exist$")
    public void Partner_with_same_id_does_not_exist() {
        partnerSteps.partnerWithSameIdInSessionDoesNotExists();
    }

    @When("^Nonexistent partner id is deleted$")
    public void Nonexistent_partner_id_is_deleted() {
        partnerSteps.deletePartnerWithName("nonexistentName");
    }

    @When("^Partner with id \"([^\"]*)\" is activated$")
    public void Partner_with_id_is_activated(String partnerId) {
        partnerSteps.activatePartnerWithId(partnerId);
    }

    @When("Partner with id \"([^\"]*)\" is active")
    public void Partner_with_id_is_active(String partnerId) {
        partnerSteps.partnerIsSetToActive(true, partnerId);
    }

    @When("Partner with id \"([^\"]*)\" is not active")
    public void Partner_with_id_is_not_active(String partnerId) {
        partnerSteps.partnerIsSetToActive(false, partnerId);
    }

    @When("^Partner with id \"([^\"]*)\" is inactivated$")
    public void Partner_with_id_is_inactivated(String partnerId) {
        partnerSteps.inactivatePartnerWithId(partnerId);
    }

    @When("^Partner with id \"([^\"]*)\" is updated with data$")
    public void Partner_with_id_is_updated_with_data(String partnerId, List<PartnerDto> updatedPartner) throws Exception {
        partnerSteps.updatePartnerWithId(partnerId, updatedPartner.get(0));
    }

    @Then("^Updated partner with id \"([^\"]*)\" has data$")
    public void Updated_partner_with_id_has_data(String partnerId, List<PartnerDto> partnerData) throws Exception {
        partnerSteps.partnerWithIdHasData(partnerId, partnerData.get(0));
    }

    @When("^Partner with id \"([^\"]*)\" is got$")
    public void Partner_with_id_is_got(String partnerId) {
        partnerSteps.partnerWithIdIsGot(partnerId);
    }

    @When("^Partner with id \"([^\"]*)\" is got with etag$")
    public void Partner_with_id_is_got_with_etag(String partnerId) {
        partnerSteps.partnerWithIdIsGotWithEtag(partnerId);
    }

    @When("^Partner with id \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void Partner_with_id_is_got_for_etag_updated_and_got_with_previous_etag(String partnerId) {
        partnerSteps.partnerWithIdIsGotAfterUpdate(partnerId);
    }

    @When("^Nonexistent partner id is got$")
    public void Nonexistent_partner_id_is_got() {
        partnerSteps.partnerWithIdIsGot("nonexistentId");
    }

    @When("^List of partners is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_partners_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) {
        partnerSteps.listOfPartnersIsGotWith(limit, cursor, filter, sort, sortDesc);
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

        partnerSteps.createPartnerUserRelationship(partner.getPartnerId(), user.getUserId());
    }
}
