package travel.snapshot.dp.qa.steps;

import cucumber.api.PendingException;
import cucumber.api.Transform;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.serenity.BasicSteps;

public class BasicStepDefs {
	
	@Steps
	private BasicSteps basicSteps;

    @Then("^Response code is \"(\\d+)\"$")
    public void response_code_is(int responseCode) throws Throwable {
        basicSteps.responseCodeIs(responseCode);
    }
    
    @When("^\"([^\"]*)\" is called without token using \"([^\"]*)\"$")
    public void is_called_without_token_using(String service, String method) throws Throwable {
        basicSteps.isCalledWithoutTokenUsingMethod(service, method);
    }
	
    @Then("^Custom code is \"(\\d+)\"$")
    public void Custom_code_is(Integer customCode) throws Throwable {
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

    @Then("^Body contains entity with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_customer_type_with_value(String atributeName, String value) throws Throwable {
        basicSteps.bodyContainsEntityWithAtribute(atributeName, value);
    }
}
