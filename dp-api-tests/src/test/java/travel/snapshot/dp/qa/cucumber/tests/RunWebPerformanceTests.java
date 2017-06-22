package travel.snapshot.dp.qa.cucumber.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Serenity runner class for running Web Performance tests
 *
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/web_performance", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunWebPerformanceTests {

}
