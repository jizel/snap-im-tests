package travel.snapshot.dp.qa.junit.tests;

import static travel.snapshot.dp.qa.junit.tests.Tags.AUTHORIZATION_TEST;
import static travel.snapshot.dp.qa.junit.tests.Tags.SLOW_TEST;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;


/**
 * Test suite that runs all JUnit tests. Using com.googlecode.junittoolbox.WildcardPatternSuite to be able to specify a
 * relative path to files. Plain JUnit enables only Categories where annotation needs to be added to every test or test
 * class (SmokeTests should be solved that way) or explicitly given SuiteClasses (as ConfigurationTestSuite).
 *
 * This class has to be located in a super package of all the classes it runs.
 */
@RunWith(JUnitPlatform.class)
@SelectPackages("travel.snapshot.dp.qa.junit.tests.identity")
@ExcludeTags({SLOW_TEST, AUTHORIZATION_TEST})
public class JUnitIdentityTestSuite {

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
        // Do any setting after the whole suites is performed
    }
}
