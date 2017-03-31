package travel.snapshot.dp.qa.tests.identity;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Created by zelezny on 3/30/2017.
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/identity/access_checks", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunAccessCheckTests {

}