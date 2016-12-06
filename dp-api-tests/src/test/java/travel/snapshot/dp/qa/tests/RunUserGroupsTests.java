package travel.snapshot.dp.qa.tests;

import cucumber.api.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

/**
 * Serenity runner class for running User Groups tests
 */
@RunWith(CucumberWithSerenity.class)
@CucumberOptions(features = "src/test/resources/features/identity/user_groups", glue = "travel.snapshot.dp.qa", tags = {"~@skipped"})
public class RunUserGroupsTests {

}
