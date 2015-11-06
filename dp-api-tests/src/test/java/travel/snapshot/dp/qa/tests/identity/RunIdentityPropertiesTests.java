package travel.snapshot.dp.qa.tests.identity;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/identity/properties", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunIdentityPropertiesTests {
    
}