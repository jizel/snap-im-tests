package travel.snapshot.dp.qa.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Serenity runner class for running Authorization tests
 *
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/authorization", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunAuthorizationTests {

}