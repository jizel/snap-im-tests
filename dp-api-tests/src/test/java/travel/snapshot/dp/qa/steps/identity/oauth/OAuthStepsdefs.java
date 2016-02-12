package travel.snapshot.dp.qa.steps.identity.oauth;

import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import travel.snapshot.dp.qa.serenity.oauth.OAuthSteps;

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
