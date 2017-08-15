package travel.snapshot.dp.qa.cucumber.steps.authorization;

import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_CLIENT_SECRET;
import static travel.snapshot.dp.qa.cucumber.serenity.BasicSteps.DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID;

import cucumber.api.java.en.When;
import net.thucydides.core.annotations.Steps;
import org.slf4j.LoggerFactory;
import travel.snapshot.dp.qa.cucumber.serenity.authorization.AuthorizationSteps;

public class AuthorizationStepdefs {

    org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Steps
    private AuthorizationSteps authorizationSteps;

    @When("^Getting token for user \"([^\"]*)\" with password \"([^\"]*)\"$")
    public void getting_token_for_user_with_username_and_password(String username, String password) {
        authorizationSteps.getToken(username, password, DEFAULT_SNAPSHOT_APPLICATION_VERSION_ID.toString(), DEFAULT_CLIENT_SECRET.toString());
    }

    @When("^Getting configuration data for \"([^\"]*)\" with token \"([^\"]*)\"$")
    public void getting_configuration_data_for_with_token(String url, String access_token) {
        authorizationSteps.getConfigurationData(url, access_token);
    }

    @When("^Getting configuration data for \"([^\"]*)\" with a new token for user \"([^\"]*)\" with password \"([^\"]*)\"$")
    public void getting_configuration_data_for_with_a_new_token_for_user_with_password(String url, String username, String password) {
        authorizationSteps.getConfigurationDataWithNewToken(url, username, password);
    }

    @When("^Getting identity data for \"([^\"]*)\" with token \"([^\"]*)\"$")
    public void getting_identity_data_for_with_token(String url, String access_token) {
        authorizationSteps.getIdentityData(url, access_token);
    }

    @When("^Getting identity data for \"([^\"]*)\" with a new token for user \"([^\"]*)\" with password \"([^\"]*)\"$")
    public void getting_identity_data_for_with_a_new_token_for_user_with_password(String url, String username, String password) {
        authorizationSteps.getIdentityDataWithNewToken(url, username, password);
    }
}
