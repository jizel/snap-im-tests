package travel.snapshot.dp.qa.tests.web_performance;

import net.serenitybdd.cucumber.CucumberWithSerenity;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/web_performance", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunWebPerformanceTests {

}