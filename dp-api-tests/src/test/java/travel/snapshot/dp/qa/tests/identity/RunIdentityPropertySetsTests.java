package travel.snapshot.dp.qa.tests.identity;

import net.serenitybdd.cucumber.CucumberWithSerenity;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/identity/property_sets", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunIdentityPropertySetsTests {

}