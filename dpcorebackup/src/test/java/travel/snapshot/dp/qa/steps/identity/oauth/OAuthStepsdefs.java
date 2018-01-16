package travel.snapshot.dp.qa.cucumber.steps.identity.oauth;

import net.thucydides.core.annotations.Steps;

import cucumber.api.java.en.When;
import travel.snapshot.dp.qa.cucumber.serenity.oauth.OAuthSteps;

/**
 * Created by vlcek on 2/9/2016.
 */
public class OAuthStepsdefs {

    @Steps
    private OAuthSteps oAuthSteps;


    @When("Get token for user \"([^\"]*)\" with password \"([^\"]*)\"")
    public void getToken(String username, String password) throws Throwable {
        oAuthSteps.getToken(username, password);
    }

}
