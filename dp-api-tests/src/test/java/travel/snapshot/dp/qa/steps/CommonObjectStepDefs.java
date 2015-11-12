package travel.snapshot.dp.qa.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.helpers.ObjectField;
import travel.snapshot.dp.qa.helpers.ResponseEntry;
import travel.snapshot.dp.qa.serenity.CommonObjectSteps;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author konkol
 */
public class CommonObjectStepDefs {

  @Steps
  private CommonObjectSteps steps;

  // --- given ---
  @Given("^the following \"([^\"]*)\" object definition$")
  public void the_following_object_definition(String objectName, List<ObjectField> fields) throws Throwable {
    steps.setObjectDefinition(objectName, fields);
  }

  @Given("^the location \"([^\"]*)\" for object \"([^\"]*)\"$")
  public void the_location_for_object(String location, String objectName) throws Throwable {
    steps.setObjectLocation(objectName, location);
  }

  @Given("^unique identifier \"([^\"]*)\" for object \"([^\"]*)\"$")
  public void unique_identifier_for_object(String id, String objectName) throws Throwable {
    steps.setObjectIDField(objectName, id);
  }

  // --- when ---
  @When("^create \"([^\"]*)\" object with correct field values$")
  public void create_object_with_correct_field_values(String objectName) throws IOException {
    steps.createCorrectObject(objectName);
  }

  @When("^update \"([^\"]*)\" object with correct field values$")
  public void update_object_with_correct_field_values(String objectName) throws IOException {
    steps.updateCorrectObject(objectName);
  }

  @When("^create \"([^\"]*)\" objects each with one invalid field value$")
  public void create_objects_each_with_one_invalid_field_value(String objectName) throws JsonProcessingException {
    steps.createInvalidObjects(objectName);
  }

  @When("^create (\\d+) \"([^\"]*)\" objects$")
  public void create_objects(int count, String objectName) throws IOException {
    steps.createObjectsForSession(count, objectName);
  }

  @When("^create \"([^\"]*)\" objects each with one missing field$")
  public void create_objects_each_with_one_missing_field(String objectName) throws JsonProcessingException {
    steps.createObjectsWithMissingFields(objectName);
  }

  @When("^create \"([^\"]*)\" objects each with one long field$")
  public void create_objects_each_with_one_long_field(String objectName) throws JsonProcessingException {
    steps.createObjectsWithLongFields(objectName);
  }

  @When("^update \"([^\"]*)\" objects each with one correct field value$")
  public void update_objects_each_with_one_correct_field_value(String objectName) throws IOException {
    steps.updateObjectsWithCorrectValues(objectName);
  }

  @When("^update \"([^\"]*)\" objects each with one invalid field value$")
  public void update_objects_each_with_one_invalid_field_value(String objectName) throws IOException {
    steps.updateObjectsWithInvalidValues(objectName);
  }

  // --- then ---
  @Then("^there are following responses$")
  public void there_are_following_responses(List<ResponseEntry> entries) throws JsonProcessingException {
    steps.responsesMatch(entries);
  }

  @Then("^location header is set and points to the same object$")
  public void location_header_is_set_and_points_to_the_same_object() throws Throwable {
    steps.verifyLocationObject();
  }

  @Then("^filtering by top-level fields returns matching \"([^\"]*)\" objects$")
  public void filtering_by_toplevel_fields_returns_matching_objects(String objectName) throws Throwable {
    steps.filterObjectsByFields(objectName);
  }
  
  @Then("^returned \"([^\"]*)\" object matches$")
  public void returned_object_matches(String objectName) throws Throwable {
    steps.originalAndReturnedObjectsMatch(objectName);
  }

}
