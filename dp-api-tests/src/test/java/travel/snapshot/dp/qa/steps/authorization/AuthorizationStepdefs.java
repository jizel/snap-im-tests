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
    
    @When("^Getting configuration data for \"([^\"]*)\" with token \"([^\"]*)\"$")
    public void getting_configuration_data_for_with_token(String url, String access_token){
        authorizationSteps.getConfigurationData(url, access_token);
    }
    
    @When("^Getting identity data for \"([^\"]*)\" with token \"([^\"]*)\"$")
    public void getting_identity_data_for_with_token(String url, String access_token){
        authorizationSteps.getIdentityData(url, access_token);
    }
    
    @Given("^User \"([^\"]*)\" with password \"([^\"]*)\" exists$")
    public void user_with_password_exists(String username, String password){
        // Write code here that turns the phrase above into concrete actions
    }

    @When("^Getting token with username \"([^\"]*)\" and password \"([^\"]*)\"$")
    public void getting_token_with_username_and_password(String username, String password){
        authorizationSteps.GetToken(username, password);
    }

}
