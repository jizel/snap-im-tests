package travel.snapshot.dp.qa.cucumber.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Serenity runner class for running Smoke tests
 *
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features", glue = "travel.snapshot.dp.qa", tags = {"~@skipped", "@Smoke"})
public class RunSmokeTests {

}
