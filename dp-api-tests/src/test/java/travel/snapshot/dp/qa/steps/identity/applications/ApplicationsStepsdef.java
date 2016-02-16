package travel.snapshot.dp.qa.steps.identity.applications;

import java.util.List;

import org.slf4j.LoggerFactory;

import cucumber.api.Transform;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.helpers.NullEmptyStringConverter;
import travel.snapshot.dp.qa.model.Application;
import travel.snapshot.dp.qa.serenity.applications.ApplicationsSteps;

public class ApplicationsStepsdef {

  org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

  @Steps
  private ApplicationsSteps applicationSteps;

  @When("^Application is created$")
  public void Application_is_created(List<Application> applications) {
    applicationSteps.followingApplicationIsCreated(applications.get(0));
  }

  @Given("^The following applications exist$")
  public void The_following_applications_exist(List<Application> applications) throws Throwable {
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
  public void Application_With_Id_Is_Updated_With_Data(String applicationId,
      List<Application> applications) throws Throwable {
    applicationSteps.updateApplicationWithId(applicationId, applications.get(0));
  }

  @Then("^Updated application with id \"([^\"]*)\" has data$")
  public void Updated_Application_With_Id_Has_Data(String applicationId,
      List<Application> applicationData) throws Throwable {
    applicationSteps.applicationWithIdHasData(applicationId, applicationData.get(0));
  }

  @When("^Application with id \"([^\"]*)\" is updated with data if updated before$")
  public void Application_with_id_is_updated_with_data_if_updated_before(String applicationId,
      List<Application> applicationData) throws Throwable {
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
  public void List_of_applications_is_got_with_limit_and_cursor_and_filter_filter_and_sort_and_sort_desc(
      @Transform(NullEmptyStringConverter.class) String limit,
      @Transform(NullEmptyStringConverter.class) String cursor,
      @Transform(NullEmptyStringConverter.class) String filter,
      @Transform(NullEmptyStringConverter.class) String sort,
      @Transform(NullEmptyStringConverter.class) String sortDesc) throws Throwable {
    applicationSteps.listOfApplicationsIsGotWith(limit, cursor, filter, sort, sortDesc);
  }

  @Then("^There are (\\d+) applications returned$")
  public void There_are_customers_returned(int count) throws Throwable {
    applicationSteps.numberOfEntitiesInResponse(Application.class, count);
  }

  @When("^Application with id \"([^\"]*)\" is got for etag, updated and got with previous etag$")
  public void Application_with_id_is_got_for_not_current_etag(String applicationId) {
    applicationSteps.applicationWithIdIsGotWithEtagAfterUpdate(applicationId);
  }

}