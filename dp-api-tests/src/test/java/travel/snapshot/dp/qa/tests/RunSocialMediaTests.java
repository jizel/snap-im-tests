package travel.snapshot.dp.qa.tests;

import net.serenitybdd.cucumber.CucumberWithSerenity;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;


@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/social_media/facebook", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunSocialMediaTests {

}