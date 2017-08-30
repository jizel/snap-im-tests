package travel.snapshot.dp.qa.cucumber.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Run all nonPms tests - Review, Web Performance and all Social Media tests.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/nonPms/", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunNonPmsTests {
}
