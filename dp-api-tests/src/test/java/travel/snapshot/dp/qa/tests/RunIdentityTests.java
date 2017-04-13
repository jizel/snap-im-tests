package travel.snapshot.dp.qa.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Serenity runner class for running Identity tests (all test features annotated by @Identity tag)
 *
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features", glue = "travel.snapshot.dp.qa", tags = {"@Identity", "~@skipped"})
public class RunIdentityTests {

}
