package travel.snapshot.dp.qa.steps;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.serenity.BasicSteps;

public class BasicStepDefs {

    public static final String NONEXISTENT_ID = "00000000-0000-4000-a000-000000000000";

    @Steps
    private BasicSteps basicSteps;

    @Then("^Response code is (\\d+)$")
    public void response_code_is(int responseCode) throws Throwable {
        basicSteps.responseCodeIs(responseCode);
    }

    @Then("^Response code is \"([^\"]*)\"$")
    public void response_code_is_string(String responseCode) throws Throwable {
        basicSteps.responseCodeIs(Integer.valueOf(responseCode));
    }

    @When("^\"([^\"]*)\" is called without token using \"([^\"]*)\"$")
    public void is_called_without_token_using(String service, String method) throws Throwable {
        basicSteps.isCalledWithoutTokenUsingMethod(service, method);
    }

    @Then("^Custom code is (\\d+)$")
    public void Custom_code_is(Integer customCode) throws Throwable {
        basicSteps.customCodeIs(customCode);
    }

    @Then("^Custom code is \"([^\"]*)\"$")
    public void custom_code_is(Integer customCode) throws Throwable {
        basicSteps.customCodeIs(customCode);
    }

    @Then("^Content type is \"([^\"]*)\"$")
    public void content_type_is(String contentType) throws Throwable {
        basicSteps.contentTypeIs(contentType);
    }

    @Then("^Body is empty$")
    public void body_is_empty() throws Throwable {
        basicSteps.bodyIsEmpty();
    }

    @Then("^Etag header is present$")
    public void Etag_header_is_present() throws Throwable {
        basicSteps.etagIsPresent();
    }

    @When("^File \"([^\"]*)\" is used for \"([^\"]*)\" to \"([^\"]*)\" on \"([^\"]*)\"$")
    public void File_is_used_for_to_on(String filename, String method, String url, String module) throws Throwable {
        basicSteps.useFileForSendDataTo(filename, method, url, module);
    }

    @When("^Empty POST request is sent to \"([^\"]*)\" on module \"([^\"]*)\"$")
    public void emptyPOSTRequestIsSentToOn(String url, String module) throws Throwable {
        basicSteps.sendBlankPost(url, module);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_entity_with_attribute_value(String atributeName, String value) throws Throwable {
        basicSteps.bodyContainsEntityWith(atributeName, value);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\"$")
    public void Body_contains_entity_with_attribute(String atributeName) throws Throwable {
        basicSteps.bodyContainsEntityWith(atributeName);
    }

    @Then("^Body doesn't contain entity with attribute \"([^\"]*)\"$")
    public void Body_doesnt_contains_entity_with_attribute(String atributeName) throws Throwable {
        basicSteps.bodyDoesntContainEntityWith(atributeName);
    }

    @Then("^Link header is '(.*)'$")
    public void Link_header_is(String linkHeader) throws Throwable {
        basicSteps.headerIs("Link", linkHeader);
    }

    @Then("^Total count is \"(.*)\"$")
    public void Total_count_is_total(String total) throws Throwable {
        basicSteps.headerIs("X-Total-Count", total);
    }

    @Then("^Response contains (\\d+) values of attribute named \"([^\"]*)\"$")
    public void responseContainsPropertiesWithAttribute(int count, String attributeName) throws Throwable {
        basicSteps.responseContainsNoOfAttributes(count, attributeName);
    }
}