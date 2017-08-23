package travel.snapshot.dp.qa.junit.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runners.Suite;

@Suite.SuiteClasses(Categories.Authorization.class)
public class NonDestructiveSmokeTestSuite {

    @BeforeClass
    public static void setUp() {
    }

    @AfterClass
    public static void tearDown() {
        // Do any setting after the whole suites is performed
    }
}
