package travel.snapshot.dp.qa.steps.identity.applications;

import net.thucydides.core.annotations.Steps;

import org.slf4j.LoggerFactory;

import java.util.List;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.api.identity.model.RoleDto;
import travel.snapshot.dp.api.identity.model.VersionDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.applications.ApplicationsSteps;

public class ApplicationsStepsdef {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private ApplicationsSteps applicationSteps;

    @When("^Application is created$")
    public void Application_is_created(List<ApplicationDto> applications) {
        applicationSteps.followingApplicationIsCreated(applications.get(0));
    }

    @Given("^The following applications exist$")
    public void The_following_applications_exist(List<ApplicationDto> applications) throws Throwable {
        applicationSteps.followingApplicationsExist(applications);
    }

    @When("^Application with id \"([^\"]*)\" is deleted$")
    public void Application_With_Name_Is_Deleted(String applicationId) throws Throwable {
        applicationSteps.applicationWithIdIsDeleted(applicationId);
    }

    @Then("^Application with same id does not exist$")
    public void Application_With_Same_Id_Does_Not_Exist() throws Throwable {
        applicationSteps.applicationIdInSessionDoesntExist();
    }

    @When("^Nonexistent application id is deleted$")
    public void Nonexistent_application_id_is_deleted() throws Throwable {
        applicationSteps.deleteApplicationWithId("nonexistent_id");
    }

    @When("^Application with id \"([^\"]*)\" is updated with data$")
    public void Application_With_Id_Is_Updated_With_Data(String applicationId, List<ApplicationDto> applications)
            throws Throwable {
        applicationSteps.updateApplicationWithId(applicationId, applications.get(0));
    }

    @Then("^Updated application with id \"([^\"]*)\" has data$")
    public void Updated_Application_With_Id_Has_Data(String applicationId, List<ApplicationDto> applicationData)
            throws Throwable {
        applicationSteps.applicationWithIdHasData(applicationId, applicationData.get(0));
    }

    @When("^Application with id \"([^\"]*)\" is updated with data if updated before$")
    public void Application_with_id_is_updated_with_data_if_updated_before(String applicationId,
            List<ApplicationDto> applicationData) throws Throwable {
        applicationSteps.updateApplicationWithIdIfUpdatedBefore(applicationId, applicationData.get(0));
    }

    @When("^Application with id \"([^\"]*)\" is got$")
    public void Application_WithIdIsGot(String applicationId) {
        applicationSteps.applicationWithIdIsGot(applicationId);
    }

    @When("^Application with id \"([^\"]*)\" is got with etag$")
    public void Application_with_id_is_got_with_etag(String applicationId) {
        applicationSteps.applicationWithIdIsGotWithEtag(applicationId);
    }

    @When("^Nonexistent application id is got$")
    public void Nonexistent_application_id_is_got() {
        applicationSteps.applicationWithIdIsGot("nonexistentId");
    }

    @When("^List of applications is got with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_applications_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        applicationSteps.listOfApplicationsIsGotWith(limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) applications returned$")
    public void There_are_applications_returned(int count) throws Throwable {
        applicationSteps.numberOfEntitiesInResponse(ApplicationDto.class, count);
    }

    @When("^Application with id \"([^\"]*)\" is got for etag, updated and got with previous etag$")
    public void Application_with_id_is_got_for_not_current_etag(String applicationId) {
        applicationSteps.applicationWithIdIsGotWithEtagAfterUpdate(applicationId);
    }

    @Then("^There are applications with following names returned in order: (.*)")
    public void There_are_applications_with_following_names_returned_in_order(List<String> applications)
            throws Throwable {
        applicationSteps.namesInResponseInOrder(applications);
    }

    @When("^Applications roles for application id \"([^\"]*)\" is got$")
    public void Applications_roles_for_application_id_is_got(String applicationId) {
        applicationSteps.getApplicationsRolesForApplicationId(applicationId);
    }

    @When("^List of applications roles is got for application with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_applications_roles_is_got_for_application_id_and_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            String applicationId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        applicationSteps.listOfApplicationsRolesIsGotWith(applicationId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) applications roles returned$")
    public void There_are_applications_roles_returned(int count) throws Throwable {
        applicationSteps.numberOfEntitiesInResponse(RoleDto.class, count);
    }

    @Then("^There are applications roles with following names returned in order: (.*)")
    public void There_are_applications_roles_with_following_names_returned_in_order(List<String> roleNames)
            throws Throwable {
        applicationSteps.roleNamesInResponseInOrder(roleNames);
    }

    @When("^Application versions are created for application with id \"([^\"]*)\"$")
    public void Application_versions_are_created_for_application_with_id(String applicationId,
            List<VersionDto> applicationVersions) {
        applicationSteps.followingApplicationVersionsAreCreated(applicationId, applicationVersions.get(0));
    }

    @Given("^The following application versions for application with id \"([^\"]*)\" exists$")
    public void The_following_application_versions_exists(String applicationId,
            List<VersionDto> applicationVersions) {
        applicationSteps.followingApplicationVersionsExists(applicationId, applicationVersions);
    }

    @Given("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is deleted$")
    public void application_version_for_application_with_id_is_deleted(String appVersionId, String applicationId) {
        applicationSteps.applicationVersionIsDeleted(appVersionId, applicationId);
    }

    @Then("^Application version with same id for application with id \"([^\"]*)\" does not exist$")
    public void Application_version_with_same_id_does_not_exist(String applicationId) {
        applicationSteps.applicationVersionIdInSessionDoesntExist(applicationId);
    }

    @When("^Nonexistent application version for application with id \"([^\"]*)\" is deleted$")
    public void Nonexistent_application_version_id_is_deleted(String applicationId) {
        applicationSteps.deleteAppVersionWithId("nonexistent_id", applicationId);
    }

    @When("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is updated with data$")
    public void Application_version_with_id_for_application_with_id_is_updated_with_data(String appVersionId,
            String applicationId, List<VersionDto> applicationVersion) throws Throwable {
        applicationSteps.updateApplicationVersionWithId(appVersionId, applicationId, applicationVersion.get(0));
    }

    @Then("^Updated application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" has data$")
    public void Updated_application_version_with_id_for_application_with_id_has_data(String appVersionId,
            String applicationId, List<VersionDto> applicationVersion) throws Throwable {
        applicationSteps.applicationVersionWithIdHasData(appVersionId, applicationId, applicationVersion.get(0));
    }

    @When("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is updated with data with invalid etag$")
    public void Application_version_with_id_for_application_with_id_is_updated_with_data_with_invalid_etag(
            String appVersionId, String applicationId, List<VersionDto> applicationVersion) throws Throwable {
        applicationSteps.updateApplicationVersionWithInvalidEtag(appVersionId, applicationId,
                applicationVersion.get(0));
    }

    @When("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is got$")
    public void Application_version_with_id_for_application_with_id_is_got(String appVersionId, String applicationId) {
        applicationSteps.applicationVersionWithIdIsGot(appVersionId, applicationId);
    }

    @When("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is got with etag$")
    public void Application_version_with_id_for_application_with_id_is_got_with_etag(String appVersionId,
            String applicationId) {
        applicationSteps.applicationVersionWithIdIsGotWithEtag(appVersionId, applicationId);
    }

    @When("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is got with etag, updated and got with previous etag$")
    public void Application_version_with_id_for_application_with_id_is_got_with_not_current_etag(String appVersionId,
            String applicationId) {
        applicationSteps.applicationVersionWithIdIsGotWithEtagAfterUpdate(appVersionId, applicationId);
    }

    @When("^Nonexistent application version id is got for application id \"([^\"]*)\"$")
    public void Nonexistent_application_version_id_is_got_for_application_id(String applicationId) {
        applicationSteps.applicationVersionWithIdIsGot("nonexistent", applicationId);
    }

    @When("^List of application versions is got for application id \"([^\"]*)\" with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_application_versions_is_got_for_application_id_with_limit_cursor_filter_sort_sortdesc(
            String applicationId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        applicationSteps.listOfApplicationVersionsIsGotWith(applicationId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) application versions returned$")
    public void There_are_application_versions_returned(int count) throws Throwable {
        applicationSteps.numberOfEntitiesInResponse(VersionDto.class, count);
    }

    @Then("^There are application version with following names returned in order: (.*)")
    public void There_are_application_versions_with_following_names_returned_in_order(List<String> versionNames)
            throws Throwable {
        applicationSteps.versionNamesInResponseInOrder(versionNames);
    }

    @When("Applications commercial subscriptions for application id \"([^\"]*)\" is got")
    public void Applications_commercial_subscriptions_for_application_id_is_got(String applicationId) {
        applicationSteps.getCommSubscriptionForApplicationId(applicationId);
    }

    @When("^List of application commercial subscriptions is got for application with id \"([^\"]*)\" and limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\"$")
    public void List_of_application_commercial_subscriptions_is_got_for_application_id_with_limit_cursor_filter_sort_sortdesc(
            String applicationId, @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
        applicationSteps.listOfApplicationCommSubscriptionsIsGotWith(applicationId, limit, cursor, filter, sort,
                sortDesc);
    }

    @Then("There are (\\d+) applications commercial subscriptions returned")
    public void There_are_applications_commercial_subscriptions_returned(int count) throws Throwable {
        applicationSteps.numberOfEntitiesInResponse(CommercialSubscriptionDto.class, count);
    }
}
