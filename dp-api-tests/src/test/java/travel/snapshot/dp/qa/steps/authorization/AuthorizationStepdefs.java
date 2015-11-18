package travel.snapshot.dp.qa.steps.authorization;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.serenity.authorization.AuthorizationSteps;

import java.util.List;

public class AuthorizationStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private AuthorizationSteps authorizationSteps;
    
    @Given("^User \"([^\"]*)\" with password \"([^\"]*)\" exists$")
    public void user_with_password_exists(String username, String password) throws Throwable {
        // Write code here that turns the phrase above into concrete actions
    }

    @When("^Posting to \"([^\"]*)\" username \"([^\"]*)\" and password \"([^\"]*)\"$")
    public void Posting_to_url_username_and_password(String url, String username, String password) throws Throwable {
        authorizationSteps.postData(url, username, password);
    }

}
