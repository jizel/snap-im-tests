package travel.snapshot.dp.qa.steps.identity.oauth;

import net.thucydides.core.annotations.Steps;

import cucumber.api.PendingException;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.serenity.oauth.OAuthSteps;

/**
 * Created by vlcek on 2/9/2016.
 */
public class OAuthStepsdefs {

    @Steps
    private OAuthSteps oAuthSteps;



    @When("Get token for user \"([^\"]*)\" with password \"([^\"]*)\"")
    public void GetToken(String username, String password) throws Throwable {
        oAuthSteps.getToken(username,password);
    }

    @Given("^Set token to session, username \"([^\"]*)\", password \"([^\"]*)\"$")
    public void userTokenInSession(String username, String password) throws Throwable {
        oAuthSteps.setTokenToSession(username, password);
    }
}
