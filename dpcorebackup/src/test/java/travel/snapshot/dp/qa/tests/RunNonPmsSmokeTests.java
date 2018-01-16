package travel.snapshot.dp.qa.cucumber.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Run smoke tests for nonPms integrations - Review, Web Performance and Social Media tests.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/nonPms/", glue = "travel.snapshot.dp.qa", tags = {"@Smoke", "~@skipped"})
public class RunNonPmsSmokeTests {
}
