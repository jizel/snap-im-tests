package travel.snapshot.dp.qa.tests.identity;

import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/identity/applications", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunIdentityApplicationsTests {

}

