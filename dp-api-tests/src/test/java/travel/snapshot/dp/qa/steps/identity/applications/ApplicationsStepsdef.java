package travel.snapshot.dp.qa.steps.identity.applications;



import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static travel.snapshot.dp.qa.serenity.BasicSteps.NON_EXISTENT_ID;

import com.jayway.restassured.response.Response;
import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.api.identity.model.ApplicationDto;
import travel.snapshot.dp.api.identity.model.ApplicationUpdateDto;
import travel.snapshot.dp.api.identity.model.ApplicationVersionDto;
import travel.snapshot.dp.api.identity.model.CommercialSubscriptionDto;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.serenity.applications.ApplicationVersionsSteps;
import travel.snapshot.dp.qa.serenity.applications.ApplicationsSteps;

import java.util.List;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

public class ApplicationsStepsdef {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private ApplicationsSteps applicationSteps;

    @Steps
    private UsersSteps usersSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionsSteps;

    @Steps
    private ApplicationVersionsSteps applicationVersionSteps;


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

    @When("^Nonexistent application( version)? id is deleted$")
    public void Nonexistent_application_id_is_deleted(String version) throws Throwable {
        if (version != null) {
            applicationVersionSteps.deleteApplicationVersion(NON_EXISTENT_ID);
        } else {
            applicationSteps.applicationWithIdIsDeleted(NON_EXISTENT_ID);
        }
    }

    @When("^Application(?: with id)? \"([^\"]*)\" is updated with data$")
    public void Application_With_Id_Is_Updated_With_Data(String applicationName, List<ApplicationUpdateDto> applications)
            throws Throwable {
        String applicationId = applicationSteps.resolveApplicationId(applicationName);
        String etag = applicationSteps.getEntityEtag(applicationId);
        applicationSteps.updateApplication(applicationId, applications.get(0), etag);
    }

    @Then("^Updated application(?: with id)? \"([^\"]*)\" has data$")
    public void Updated_Application_With_Id_Has_Data(String applicationName, List<ApplicationDto> applicationData)
            throws Throwable {
        String applicationId = applicationSteps.resolveApplicationId(applicationName);
        applicationSteps.applicationWithIdHasData(applicationId, applicationData.get(0));
    }

    @When("^Application with id \"([^\"]*)\" is updated with invalid etag$")
    public void Application_with_id_is_updated_with_data_if_updated_before(String applicationId,
                                                                           List<ApplicationUpdateDto> applications) throws Throwable {
        applicationSteps.updateApplication(applicationId, applications.get(0), "Invalid_etag");
    }

    @When("^Application with id \"([^\"]*)\" is (?:got|requested)$")
    public void Application_WithIdIsGot(String applicationId) {
        applicationSteps.applicationWithIdIsRequested(applicationId);
    }

    @When("^Application with id \"([^\"]*)\" is got with etag$")
    public void Application_with_id_is_got_with_etag(String applicationId) {
        applicationSteps.applicationWithIdIsGotWithEtag(applicationId);
    }

    @When("^Nonexistent application( version)? id is (?:requested|got)$")
    public void Nonexistent_application_id_is_got(String version) {
        if (version != null) {
            applicationVersionSteps.getApplicationVersion(NON_EXISTENT_ID);
        } else {
            applicationSteps.applicationWithIdIsRequested(NON_EXISTENT_ID);
        }
    }

    @When("^List of applications is (?:got|requested)(?: with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void List_of_applications_is_got_with_limit_and_cursor_and_filter_and_sort_and_sort_desc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            @Transform(NullEmptyStringConverter.class) String userName,
            @Transform(NullEmptyStringConverter.class) String appVersionName) throws Throwable {
        String appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        String userId = usersSteps.resolveUserId(userName);
        applicationSteps.listOfApplicationsIsGotWith(userId, appVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) applications returned$")
    public void There_are_applications_returned(int count) throws Throwable {
        applicationSteps.numberOfEntitiesInResponse(ApplicationDto.class, count);
    }

    @Then("^There are applications with following names returned in order: (.*)")
    public void There_are_applications_with_following_names_returned_in_order(List<String> applications)
            throws Throwable {
        applicationSteps.namesInResponseInOrder(applications);
    }

    @When("^Application version is created(?: for application with id \"([^\"]*)\")?$")
    public void Application_versions_are_created_for_application_with_id(String applicationId,
                                                                         List<ApplicationVersionDto> applicationVersions) {
        ApplicationVersionDto appVersion = applicationVersions.get(0);
        if (appVersion.getApplicationId() == null) {
            appVersion.setApplicationId(applicationId);
        }
        Response response = applicationVersionSteps.createApplicationVersion(applicationVersions.get(0));
        applicationVersionSteps.setSessionResponse(response);
    }

    @Given("^The following application versions exists$")
    public void The_following_application_versions_exists(List<ApplicationVersionDto> applicationVersions) {
        applicationVersionSteps.followingApplicationVersionsExists(applicationVersions);
    }

    @Given("^Application version with id \"([^\"]*)\" is deleted$")
    public void application_version_for_application_with_id_is_deleted(String appVersionId) {
        applicationVersionSteps.deleteApplicationVersion(appVersionId);
    }

    @Then("^Application version with id \"([^\"]*)\" does not exist$")
    public void Application_version_with_same_id_does_not_exist(String applicationVersionId) {
        Response response = applicationVersionSteps.getApplicationVersion(applicationVersionId);
        assertThat(response.getStatusCode(), is(HttpStatus.SC_NOT_FOUND));
    }

    @When("^Application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" is updated with data$")
    public void Application_version_with_id_for_application_with_id_is_updated_with_data(String appVersionId,
                                                                                         String applicationId, List<ApplicationVersionDto> applicationVersion) throws Throwable {
        String etag = applicationVersionSteps.getEntityEtag(appVersionId);
        applicationVersionSteps.updateApplicationVersion(appVersionId, applicationVersion.get(0), etag);
    }

    @Then("^Updated application version with id \"([^\"]*)\" for application with id \"([^\"]*)\" has data$")
    public void Updated_application_version_with_id_for_application_with_id_has_data(String appVersionId,
                                                                                     String applicationId, List<ApplicationVersionDto> applicationVersion) throws Throwable {
        applicationVersionSteps.applicationVersionWithIdHasData(appVersionId, applicationVersion.get(0));
    }

    @When("^Application version with id \"([^\"]*)\" is updated with data with invalid etag$")
    public void Application_version_with_id_for_application_with_id_is_updated_with_data_with_invalid_etag(
            String appVersionId, List<ApplicationVersionDto> applicationVersion) throws Throwable {
        applicationVersionSteps.updateApplicationVersion(appVersionId, applicationVersion.get(0), "invalid_etag");
    }

    @When("^Application version with id \"([^\"]*)\" is got$")
    public void Application_version_with_id_for_application_with_id_is_got(String appVersionId) {
        applicationVersionSteps.getApplicationVersion(appVersionId);
    }

    @When("^List of application versions is (?:requested|got)(?: with limit \"([^\"]*)\" and cursor \"([^\"]*)\" and filter \"([^\"]*)\" and sort \"([^\"]*)\" and sort_desc \"([^\"]*)\")?(?: by user \"([^\"]*)\")?(?: for application version \"([^\"]*)\")?$")
    public void List_of_application_versions_is_got_for_application_id_with_limit_cursor_filter_sort_sortdesc(
            @Transform(NullEmptyStringConverter.class) String limit,
            @Transform(NullEmptyStringConverter.class) String cursor,
            @Transform(NullEmptyStringConverter.class) String filter,
            @Transform(NullEmptyStringConverter.class) String sort,
            @Transform(NullEmptyStringConverter.class) String sortDesc,
            @Transform(NullEmptyStringConverter.class) String userName,
            @Transform(NullEmptyStringConverter.class) String appVersionName) throws Throwable {
        String appVersionId = applicationVersionsSteps.resolveApplicationVersionId(appVersionName);
        String userId = usersSteps.resolveUserId(userName);
        applicationVersionSteps.listOfApplicationVersionsIsGotWith(userId, appVersionId, limit, cursor, filter, sort, sortDesc);
    }

    @Then("^There are (\\d+) application versions returned$")
    public void There_are_application_versions_returned(int count) throws Throwable {
        applicationVersionSteps.numberOfEntitiesInResponse(ApplicationVersionDto.class, count);
    }

    @Then("^There are application version with following names returned in order: (.*)")
    public void There_are_application_versions_with_following_names_returned_in_order(List<String> versionNames)
            throws Throwable {
        applicationVersionSteps.versionNamesInResponseInOrder(versionNames);
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

    @When("^Application version with id \"([^\"]*)\" is requested directly$")
    public void applicationVersionIsRequestedDirectly(String versionId) throws Throwable {
//        Using versionId here instead of name and resolveName method because getting version by name would require additional
//        parameter applicationId (endpoint /application_version/ without versionId is not implemented)
        applicationVersionSteps.getApplicationVersion(versionId);
    }
}
