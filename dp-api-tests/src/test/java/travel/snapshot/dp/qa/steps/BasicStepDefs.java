package travel.snapshot.dp.qa.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import gherkin.formatter.model.DataTableRow;
import net.thucydides.core.annotations.Steps;
import org.apache.commons.lang3.StringUtils;
import travel.snapshot.dp.api.identity.model.UserDto;
import travel.snapshot.dp.qa.serenity.BasicSteps;
import travel.snapshot.dp.qa.serenity.users.UsersSteps;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.seleniumhq.jetty9.util.StringUtil.isNotBlank;

public class BasicStepDefs {

    public static final String NONEXISTENT_ID = "00000000-0000-4000-a000-000000000000";

    @Steps
    private BasicSteps basicSteps;
    @Steps
    private UsersSteps usersSteps;

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

    @When("^POST request is sent to \"([^\"]*)\" on module \"([^\"]*)\" with$")
    public void post_request_is_sent_to_on_module_with(String url, String module, DataTable contents) throws Throwable {
        HashMap<String, String> contentsMap = new HashMap<String, String>();
        for (DataTableRow row: contents.getGherkinRows()) {
            contentsMap.put(row.getCells().get(0), row.getCells().get(1));
        }
        String body = new ObjectMapper().writeValueAsString(contentsMap);
        basicSteps.sendPostWithBody(url, module, body);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\" value \"([^\"]*)\"$")
    public void Body_contains_entity_with_attribute_value(String atributeName, String value) throws Throwable {
        basicSteps.bodyContainsEntityWith(atributeName, value);
    }

    @Then("^Body contains entity with attribute \"([^\"]*)\" and integer value (\\d+)$")
    public void Body_contains_entity_with_attribute_value(String atributeName, Integer value) throws Throwable {
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

    @And("^The \"([^\"]*)\" attribute in response contains only CAPITAL latin characters or numbers$")
    public void theAttributeInResponseContainsOnlyCAPITALLatinCharactersOrNumbers(String attributeName) throws Throwable {
        String attributeValue = basicSteps.getAttributeValue(attributeName);

        assertThat("Attribute " + attributeName + " is blank", isNotBlank(attributeValue), is(true));
        assertThat("Attribute " + attributeName + " contains white spaces: " + attributeValue,
                StringUtils.containsWhitespace(attributeValue), is(false));
        assertThat("Attribute " + attributeName + " is not sequence of capital latin letters and digits. It is: "+ attributeValue,
                attributeValue.matches("[A-Z0-9]+"), is(true));
    }

    @When("^GET request is sent to \"([^\"]*)\" on module \"([^\"]*)\" by user \"([^\"]*)\"$")
    public void getRequestIsSentToOnModule(String url, String module, String username) throws Throwable {
        String userId = usersSteps.resolveUserId(username);
        basicSteps.sendGetRequestToUrlByUser(userId, url, module);
    }

    @When("^GET request is sent to \"([^\"]*)\" on module \"([^\"]*)\" without X-Auth-UserId header$")
    public void getRequestIsSentToOnModuleWithoutXAuthUserIdHeader(String url, String module) throws Throwable {
        basicSteps.sendGetRequestToUrlWithoutUserHeader(url, module);
    }

    @When("^GET request is sent to \"([^\"]*)\" on module \"([^\"]*)\" with empty X-Auth-UserId header$")
    public void getRequestIsSentToOnModuleWithEmptyXAuthUserIdHeader(String url, String module) throws Throwable {
        basicSteps.sendGetRequestToUrlByUser("", url, module);
    }

    @When("^DELETE request is sent to \"([^\"]*)\" on module \"([^\"]*)\"$")
    public void deleteRequestIsSentToOnModule(String url, String module) throws Throwable {
        basicSteps.sendDeleteToUrl(url, module);
    }
}