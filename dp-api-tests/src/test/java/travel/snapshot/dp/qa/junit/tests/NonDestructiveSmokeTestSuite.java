package travel.snapshot.dp.qa.junit.tests;

import com.googlecode.junittoolbox.ExcludeCategories;
import com.googlecode.junittoolbox.WildcardPatternSuite;
import com.googlecode.junittoolbox.SuiteClasses;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(WildcardPatternSuite.class)
@SuiteClasses("*/*SmokeTests.class")
@ExcludeCategories(Categories.SlowTests.class)
public class NonDestructiveSmokeTestSuite {

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
        // Do any setting after the whole suites is performed
    }
}
