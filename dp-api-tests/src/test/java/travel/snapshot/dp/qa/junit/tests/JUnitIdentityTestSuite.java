package travel.snapshot.dp.qa.junit.tests;

import com.googlecode.junittoolbox.ExcludeCategories;
import com.googlecode.junittoolbox.SuiteClasses;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import travel.snapshot.dp.qa.junit.tests.Categories;


/**
 * Test suite that runs all JUnit tests. Using com.googlecode.junittoolbox.WildcardPatternSuite to be able to specify a
 * relative path to files. Plain JUnit enables only Categories where annotation needs to be added to every test or test
 * class (SmokeTests should be solved that way) or explicitly given SuiteClasses (as ConfigurationTestSuite).
 *
 * This class has to be located in a super package of all the classes it runs.
 */
@RunWith(WildcardPatternSuite.class)
@SuiteClasses("**/identity/*.class")
@ExcludeCategories({Categories.SlowTests.class, Categories.Authorization.class})
public class JUnitIdentityTestSuite {

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
        // Do any setting after the whole suites is performed
    }
}
