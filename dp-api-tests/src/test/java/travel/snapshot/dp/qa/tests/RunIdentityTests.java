package travel.snapshot.dp.qa.tests;

import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;
import cucumber.api.CucumberOptions;

/**
 * Serenity runner class for running Identity tests (all test features annotated by @Identity tag)
 *
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features", glue = "travel.snapshot.dp.qa", tags = {"@Identity"})
public class RunIdentityTests {

}
